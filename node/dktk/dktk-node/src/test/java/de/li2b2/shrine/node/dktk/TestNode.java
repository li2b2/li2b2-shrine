package de.li2b2.shrine.node.dktk;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.aktin.broker.client.auth.HttpApiKeyAuth;

public class TestNode {

	public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException{
		String broker_service = args[0];
		String apiKey = args[1];
		String centraxx_endpoint = args[2];

		CentraxxNode app = new CentraxxNode(URI.create(centraxx_endpoint),"target/pending.properties");
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
}
