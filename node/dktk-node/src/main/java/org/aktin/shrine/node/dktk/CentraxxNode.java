package org.aktin.shrine.node.dktk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.aktin.broker.client.auth.HttpApiKeyAuth;
import org.aktin.broker.node.AbstractNode;
import org.aktin.broker.xml.RequestInfo;
import org.aktin.broker.xml.RequestStatus;
import org.aktin.broker.xml.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * Dummy node which immediately response to all query requests with a random patient count.
 * Centraxx URI should look like http://server/centraxx/rest/teiler/
 * @author R.W.Majeed
 *
 */
public class CentraxxNode extends AbstractNode{
	private static final String MEDIA_TYPE_I2B2_QUERY_DEFINITION = "text/vnd.i2b2.query-definition+xml";
	private URI centraxx_teiler;
	// maps pending broker ids to centrax query ids
	private Map<String,String> pendingCentraxxQueries;
	private String pendingFile;


	public CentraxxNode(URI centraxx_teiler, String pendingProperties) throws ParserConfigurationException{
		this.centraxx_teiler = centraxx_teiler;
		this.pendingFile = pendingProperties;
	}

	public static void main(String[] args) throws Exception{
		String broker_service = args[0];
		String apiKey = args[1];
		String centraxx_endpoint = args[2];

		CentraxxNode app = new CentraxxNode(URI.create(centraxx_endpoint),"pending.properties");
		// setup broker client
		if( args.length == 4 ){
			System.out.println("Using transformation: "+args[3]);
			app.loadTransformer(args[3]);
		}
		app.connectBroker(broker_service, HttpApiKeyAuth.newBearer(apiKey));
		app.loadPendingQueries();
		app.processRequests();
		app.retrieveResults();
		app.writePendingQueries();
	}

	/**
	 * Submit a query to the DKTK Centraxx and return the query URI
	 * which can be used to retrieve the results
	 *
	 * @param query query document
	 * @return URI for the query
	 * @throws IOException error
	 */
	private String createCentraxxQuery(Document query) throws IOException{
		URL url = centraxx_teiler.resolve("requests?statisticsOnly=true").toURL();
		HttpURLConnection c = (HttpURLConnection)url.openConnection();
		// prepare connection
		c.setRequestMethod("POST");
		c.setRequestProperty("Content-Type", "application/xml;charset=UTF-8");
		c.setRequestProperty("Accept", "application/xml");
		c.setRequestProperty("Delay", "60");
		c.setDoInput(true);
		c.setDoOutput(true);
		c.connect();
		// write query
		try( OutputStream out = c.getOutputStream() ){
			// write to output stream
			Util.printDOM(query, out, "UTF-8");
		} catch (TransformerException e) {
			throw new IOException("Error while generating XML",e);
		}
		// TODO need to check response code?
		
		// make sure that the response code is in the 2xx family
		c.getInputStream().close(); // will fail for other status codes

		String location = c.getHeaderField("Location");		
		return location;
	}

	/**
	 * Retrieve query status.
	 * @param query_location query location URI
	 * @return query status XML document if the query is finished, otherwise {@code null} if the query is not finished yet.
	 * @throws IOException 
	 */
	private Document retrieveCentraxxQueryStatus(String query_location) throws IOException{
		URL url = centraxx_teiler.resolve(query_location + "/stats").toURL();
		HttpURLConnection c = (HttpURLConnection)url.openConnection();
		// prepare connection
		c.setRequestProperty("Accept", "application/xml");
		c.connect();
		switch( c.getResponseCode() ){
		case 200: // query finished
			// determine charset
			String ct = c.getHeaderField("Content-Type");
			String charset;
			if( ct == null || !ct.contains("charset=") ){
				// use default charset
				charset = "ISO-8859-1";
			}else{
				int pos = ct.indexOf("charset=");
				charset = ct.substring(pos+8);
			}
			// retrieve XML content
			try( InputStream in = c.getInputStream();
					InputStreamReader r = new InputStreamReader(in, charset) ){
				Document dom = Util.parseDocument(r);
				return dom;
			}
		case 202: // query still executing
			return null;
		default:
			// other error
			throw new IOException("Unexpected response code: "+c.getResponseCode());
		}
	}
	public void loadPendingQueries() throws IOException{
		Properties props = new Properties();
		File file = new File(pendingFile);
		if( file.exists() ){
			try( InputStream in = new FileInputStream(pendingFile) ){
				props.load(in);
			}
			System.out.println("Loaded "+props.size()+" pending queries");
		}else{
			System.out.println("No pending queries loaded, file does not exist: "+pendingFile);
		}
		pendingCentraxxQueries = new HashMap<>();
		// copy to our map
		for( String key : props.stringPropertyNames() ){
			pendingCentraxxQueries.put(key, props.getProperty(key));			
		}
	}

	public void processRequests() throws IOException{
		List<RequestInfo> requests = broker.listMyRequests();
		// process requests synchronously. first come first serve
		for( RequestInfo request : requests ){
			if( pendingCentraxxQueries.containsKey(request.getId()) ){
				System.out.println("Request #"+request.getId()+" already pending");
				continue;
			}
			// check media type
			if( !request.hasMediaType(MEDIA_TYPE_I2B2_QUERY_DEFINITION) ){
				// need query definition
				System.err.println("Unable to process query "+request.getId()+" without "+MEDIA_TYPE_I2B2_QUERY_DEFINITION);
				continue;
			}
			// retrieve definition
			Document def = broker.getMyRequestDefinitionXml(request.getId(), MEDIA_TYPE_I2B2_QUERY_DEFINITION);
			try{
				// transform to DKTK centraxx query
				if( hasTransformer() ){
					System.out.println("Applying transformation to query #"+request.getId());
					def = transform(def);
				}
				System.out.println("POSTing query #"+request.getId()+":");
				Util.printDOM(def, System.out, "UTF-8");
				String queryLocation = createCentraxxQuery(def);
				pendingCentraxxQueries.put(request.getId(), queryLocation);
				// notify broker of processing query
				broker.postRequestStatus(request.getId(), RequestStatus.processing);
			}catch( TransformerException e ){
				// print error
				e.printStackTrace();
				System.err.println("Query transformation failed: "+e.getMessage());
				broker.postRequestStatus(request.getId(), RequestStatus.failed);
			}catch( IOException e ){
				// print error
				e.printStackTrace();
				System.err.println("Unable to post query to centraxx: "+e.getMessage());
				broker.postRequestStatus(request.getId(), RequestStatus.failed);
			}
		}
	}

	public void retrieveResults() throws IOException{
		Iterator<Entry<String, String>> pending = pendingCentraxxQueries.entrySet().iterator();

		while( pending.hasNext() ){
			Entry<String,String> entry = pending.next();
			String requestId = entry.getKey();
			int patientCount;
			try{
				Document doc = retrieveCentraxxQueryStatus(entry.getValue());
				if( doc == null ){
					// still processing
					// continue with next
					continue;
				}
	
				// query finished, summary available
				NodeList nl = doc.getDocumentElement().getElementsByTagName("totalSize");
				if( nl.getLength() == 1 ){
					// may throw numberformatexception
					patientCount = Integer.parseInt(nl.item(0).getTextContent());
				}else{
					// no patient count found in stats
					throw new NumberFormatException("No totalSize found in stats document");
				}
			}catch( IOException e ){
				// unable to retrieve result status
				broker.postRequestStatus(requestId, RequestStatus.failed);
				continue;
			}catch( NumberFormatException e ){
				// unable to find patient count
				broker.postRequestStatus(requestId, RequestStatus.failed);
				continue;
			}
			// post result
			broker.putRequestResult(requestId, "text/vnd.aktin.patient-count", Integer.toString(patientCount));
			// report completed
			broker.postRequestStatus(requestId, RequestStatus.completed);
			System.out.println("Completed request #"+requestId);
			// delete request
			broker.deleteMyRequest(requestId);
			// remove entry
			pending.remove();
		}
		

	}
	public void writePendingQueries() throws FileNotFoundException, IOException{
		Properties props = new Properties();
		props.putAll(pendingCentraxxQueries);
		try( OutputStream out = new FileOutputStream(pendingFile) ){
			props.store(out, "pending centraxx queries");
		}
	}

}
