package org.aktin.shrine.node.i2b2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.aktin.broker.client.BrokerClient;
import org.aktin.broker.client.auth.ClientAuthenticator;
import org.aktin.broker.xml.RequestInfo;
import org.aktin.broker.xml.RequestStatus;
import org.aktin.broker.xml.SoftwareModule;
import org.w3c.dom.Document;


/**
 * Dummy node which immediately response to all query requests with a random patient count.
 * 
 * @author R.W.Majeed
 *
 */
public class RandomCountNode {
	private static final String MEDIA_TYPE_I2B2_QUERY_DEFINITION = "text/vnd.i2b2.query-definition+xml";
	private static final String MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST = "text/vnd.i2b2.result-output-list";
	long startup;
	BrokerClient broker;
	DocumentBuilder builder;

	public RandomCountNode() throws ParserConfigurationException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		builder = factory.newDocumentBuilder();
		this.startup = System.currentTimeMillis();
	}
	public static void main(String[] args) throws Exception{
		String broker_service = args[0];
		
		String client_id = args[1];
		String client_dn = args[2];

		// TODO broker keystore
		// setup broker client
		RandomCountNode app = new RandomCountNode();

		app.connectBroker(broker_service, new PlainHeaderAuthenticator(client_id, client_dn));
		app.processRequests();
	}
	public void connectBroker(String broker_endpoint, ClientAuthenticator auth) throws IOException{
		try {
			this.broker = new BrokerClient(new URI(broker_endpoint));
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
		broker.setClientAuthenticator(auth);
		// optional status exchange
		broker.getBrokerStatus();
		broker.postMyStatus(startup, new SoftwareModule("org.aktin.broker.i2b2.node", "1.0-SNAPSHOT"));
	}
	public void processRequests() throws IOException{
		List<RequestInfo> requests = broker.listMyRequests();
		// process requests synchronously. first come first serve
		for( RequestInfo request : requests ){
			// check media type
			if( !request.hasMediaType(MEDIA_TYPE_I2B2_QUERY_DEFINITION) ){
				// need query definition
				System.err.println("Unable to process query "+request.getId()+" without "+MEDIA_TYPE_I2B2_QUERY_DEFINITION);
				continue;
			}else if( !request.hasMediaType(MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST) ){
				// need output list
				System.err.println("Unable to process query "+request.getId()+" without "+MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST);
				continue;
			}
			// retrieve definition
			Document def = broker.getMyRequestDefinitionXml(request.getId(), MEDIA_TYPE_I2B2_QUERY_DEFINITION);
			// retrieve result output list
			String[] resultList = broker.getMyRequestDefinitionLines(request.getId(), MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST);
			// run query definition
			int patientCount;
			try {
				patientCount = new Random().nextInt(2000);
				if( patientCount < 0 ){
					// fail
				}
			} catch (RuntimeException e) {
				// TODO log error
				// TODO report error message
				broker.postRequestStatus(request.getId(), RequestStatus.failed);
				continue;
			}
			broker.putRequestResult(request.getId(), "text/vnd.aktin.patient-count", Objects.toString(patientCount));
			// 
			// report completed
			broker.postRequestStatus(request.getId(), RequestStatus.completed);
			
			System.out.println("Completed request #"+request.getId());
			// delete request
			broker.deleteMyRequest(request.getId());
		}
		
	}

}
