package de.li2b2.shrine.node.i2b2;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.xml.transform.TransformerException;

import org.aktin.broker.client.auth.HttpApiKeyAuth;
import org.aktin.broker.node.AbstractNode;
import org.aktin.broker.node.SimpleTransformer;
import org.aktin.broker.xml.RequestInfo;
import org.aktin.broker.xml.RequestStatus;
import org.aktin.broker.xml.util.Util;
import org.w3c.dom.Document;

import de.sekmi.li2b2.client.Li2b2Client;
import de.sekmi.li2b2.client.crc.MasterInstanceResult;
import de.sekmi.li2b2.client.crc.QueryResultInstance;
import de.sekmi.li2b2.client.pm.UserConfiguration;
import de.sekmi.li2b2.hive.ErrorResponseException;
import de.sekmi.li2b2.hive.HiveException;
import de.sekmi.li2b2.hive.crc.QueryResultType;
import de.sekmi.li2b2.hive.pm.UserProject;

public class I2b2Node extends AbstractNode{
	private static final String MEDIA_TYPE_I2B2_QUERY_DEFINITION = "text/vnd.i2b2.query-definition+xml";
	private static final String MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST = "text/vnd.i2b2.result-output-list";
//	private static final String MEDIA_TYPE_I2B2_RESULT_ENVELOPE = "text/vnd.i2b2.result-envelope+xml";

	Li2b2Client i2b2;
	private List<RequestInfo> requests;
	private SimpleTransformer transformer;

	public I2b2Node(){
		transformer = new SimpleTransformer();
	}

	/**
	 * Establish a temporary connection to the specified i2b2 server with the specified
	 * credentials and load the configuration and user session information.
	 *
	 * @param proxy proxy URL string (optional)
	 * @param pm_service Project management service URL
	 * @param user user name
	 * @param password user password
	 * @param domain server domain id (as advertised by the server)
	 * @param projectId project id (optional)
	 * @throws IOException IO error
	 * @throws ErrorResponseException authentication failed
	 * @throws HiveException other communications error
	 */
	public void connectI2b2(String proxy, String pm_service, String user, String password, String domain, String projectId) throws IOException, ErrorResponseException, HiveException{
		Li2b2Client client = new Li2b2Client();
		if( proxy != null ){
			System.out.println("Using proxy "+proxy);
			client.setProxy(new URL(proxy));
		}
		client.setAuthorisation(user, password, domain);
		client.setPM(new URL(pm_service));
		UserConfiguration uc = client.PM().requestUserConfiguration();
		// find project
		int projectIndex = -1;
		UserProject[] projects = uc.getProjects();
		Objects.requireNonNull(projects, "Server did not return any projects");
		if( projectId == null ){
			// use first project
			projectIndex = 0;
		}else{
			// find project by id string
			for( int i=0; i<projects.length; i++ ){
				if( projects[i].id.equals(projectId) ){
					// found a match!
					projectIndex = i;
					break;
				}
			}
		}
		if( projectIndex == -1 ){
			// specified project not found in server response
			throw new IOException("Project not found in server response: "+projectId);
		}
		client.setProjectId(projects[0].id);
		System.out.println("Project:"+projects[0].id);
		System.out.println("Roles:"+Arrays.toString(projects[0].role));

		// set services
		client.setServices(uc.getCells());
		this.i2b2 = client;
		
	}
//	private void issueWarning(String warning){
//		System.err.println("Warning: "+warning);
//	}
	private void printError(String message, Throwable e){
		e.printStackTrace();
		System.err.println("Error: "+message);		
	}
	private void postOnlyPatientCount(RequestInfo request, MasterInstanceResult mir) throws IOException{
		Integer count = null;
		for( QueryResultInstance qr : mir.query_result_instance ){
			if( qr.query_result_type.display_type.equals(QueryResultType.I2B2_DISPLAY_CATNUM) 
					&& qr.set_size != null){
				//  use set_size
				count = qr.set_size;
				break;
			}
		}		
		// submit results to aggregator
		System.out.println("Patient count for request #"+request.getId()+" is "+count);
		broker.putRequestResult(request.getId(), "text/vnd.aktin.patient-count", Objects.toString(count));
	}

	// TODO for second version, concatenate the results (each starting with <?xml)
	// TODO for third version, add multiple result documents
//	private void postConcatenatedResults(RequestInfo request, MasterInstanceResult mir) throws IOException{
//		StringBuilder sb = new StringBuilder();
//		for( QueryResultInstance qr : mir.query_result_instance ){
//			switch( qr.query_result_type.display_type ){
//			case QueryResultType.I2B2_DISPLAY_CATNUM:
//				String doc;
//				try {
//					doc = i2b2.CRC().getResultDocument(qr.result_instance_id);
//				} catch (HiveException e) {
//					issueWarning("Unable to retrieve result document: "+e.getMessage());
//					continue;
//				}
//				sb.append(doc);
//				sb.append('\n');
//				break;
//			default:
//				issueWarning("Ignoring unsupported result type '"+qr.query_result_type.display_type+"': "+qr.query_result_type.description);
//			}
//
//		}
//		// submit results to aggregator
//		broker.putRequestResult(request.getId(), "application/vnd.i2b2.concat-results+xml", sb.toString());
//	}
	/**
	 * Load requests from the broker and preprocess
	 * the list so only processable requests are remaining.
	 * @throws IOException IO error
	 */
	private void loadRequests() throws IOException{
		requests = broker.listMyRequests();
		// remove unprocessable requests from list
		Iterator<RequestInfo> iter = requests.iterator();
		while( iter.hasNext() ){
			RequestInfo request = iter.next();
			// check media type
			if( !request.hasMediaType(MEDIA_TYPE_I2B2_QUERY_DEFINITION) ){
				// need query definition
				System.err.println("Unable to process query "+request.getId()+" without "+MEDIA_TYPE_I2B2_QUERY_DEFINITION);
				iter.remove();
			}else if( !request.hasMediaType(MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST) ){
				// need output list
				System.err.println("Unable to process query "+request.getId()+" without "+MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST);
				iter.remove();
			}
		}
	}
	/**
	 * Whether or not we have requests to process.
	 * @return {@code true} if there are processable requests in the queue, {@code false} otherwise.
	 */
	private boolean hasRequests(){
		return !requests.isEmpty();
	}
	/**
	 * Process pending requests.
	 * @throws IOException IO error
	 */
	private void processRequests() throws IOException{
		// process requests synchronously. first come first serve
		for( RequestInfo request : requests ){
			// retrieve definition
			Document def = broker.getMyRequestDefinitionXml(request.getId(), MEDIA_TYPE_I2B2_QUERY_DEFINITION);
			// retrieve result output list
			String[] resultList = broker.getMyRequestDefinitionLines(request.getId(), MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST);
			broker.postRequestStatus(request.getId(), RequestStatus.retrieved);
			// run query definition
			try {
				broker.postRequestStatus(request.getId(), RequestStatus.processing);
				// transform query
				if( transformer.hasTransformer() ){
					System.out.println("Applying transformation..");
					def = transformer.transform(def);
				}
				System.out.println("Running query #"+request.getId());
				Util.writeDOM(def, System.out, "UTF-8");
				MasterInstanceResult mir;
				mir = i2b2.CRC().runQueryInstance(def.getDocumentElement(), resultList);
				// retrieve results for primary instance as listed
				// this first version only reports the patient count as extracted from set_size
				postOnlyPatientCount(request, mir);
				// 
				// report completed
				broker.postRequestStatus(request.getId(), RequestStatus.completed);
				System.out.println("Completed request #"+request.getId());
			} catch (HiveException e) {
				// report error message
				printError("Query execution failed for request #"+request.getId(), e);
				broker.postRequestFailed(request.getId(), "Query execution failed", e);
			} catch (TransformerException e) {
				printError("Query transformation failed for request #"+request.getId(), e);
				broker.postRequestFailed(request.getId(), "Query transformation failed", e);
			}
			// delete request
			broker.deleteMyRequest(request.getId());
		}
		
	}

	private static void printUsage(){
		System.out.println("Usage: 'de...I2b2Node' broker_endpoint_url broker_api_key"
				+ " i2b2_pm_service_url['|'i2b2_proxy_url]"
				+ " i2b2_user'@'domain['/'project] i2b2_password"
				+ " [query_xslt_file]");
		System.out.println();
		System.out.println(" *i2b2_proxy_url* is not a normal HTTP proxy,");
		System.out.println("  but an i2b2-specific servicee. If is specified");
		System.out.println("  all cell communications is done via POST to");
		System.out.println("  the i2b2_proxy_url.");
		System.out.println();
		System.out.println(" *project* can be specified via the user argument.");
		System.out.println("  E.g. 'demo@i2b2demo/Demo'. If no project is");
		System.out.println("  specified, the first project returned by the");
		System.out.println("  server is used.");
		System.out.println();
		System.out.println(" *query_xslt_file* can be provided to transform");
		System.out.println("  the retrieved query_definition XML document.");
		System.out.println("  E.g. for mapping query concept keys to local");
		System.out.println("  metadata ontology.");
	}

	public static void main(String[] args) throws Exception{
		if( args.length < 5 || args.length > 6 ){
			printUsage();
			System.exit(-1);
		}
		String broker_service = args[0];
		String broker_key = args[1];
		String i2b2_pm_service = args[2];
		String i2b2_user = args[3];
		String i2b2_pass = args[4];

		// setup i2b2 client
		String i2b2_domain = null;
		// domain name is required
		int at = i2b2_user.indexOf('@');
		if( at == -1 ){
			System.err.println("User domain must be specified in the fourth argument via '@'. E.g. demo@i2b2demo");
			System.exit(-1);
		}
		i2b2_domain = i2b2_user.substring(at+1);
		i2b2_user = i2b2_user.substring(0, at);
		// use project name, if provided
		String i2b2_project = null;
		at = i2b2_domain.indexOf('/');
		if( at != -1 ){
			// project specified
			i2b2_project = i2b2_domain.substring(at+1);
			i2b2_domain = i2b2_domain.substring(0, at);
		}

		// extract proxy if specified
		String i2b2_proxy = null;
		at = i2b2_pm_service.indexOf('|');
		if( at != -1 ){
			// proxy specified following the | character
			i2b2_proxy = i2b2_pm_service.substring(at+1);
			i2b2_pm_service = i2b2_pm_service.substring(0, at);
		}
		// setup broker client
		I2b2Node app = new I2b2Node();
		if( args.length == 6 ){
			app.transformer.loadTransformer(args[5]);
		}
		
		app.connectBroker(broker_service, HttpApiKeyAuth.newBearer(broker_key));
		app.loadRequests();
		// only connect to i2b2 if there are requests to process
		if( app.hasRequests() ){
			// do some work
			app.connectI2b2(i2b2_proxy, i2b2_pm_service, i2b2_user, i2b2_pass, i2b2_domain, i2b2_project);
			app.processRequests();
		}
	}

}
