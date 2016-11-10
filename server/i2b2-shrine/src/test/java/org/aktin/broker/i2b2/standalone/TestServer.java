package org.aktin.broker.i2b2.standalone;

import java.io.InputStream;
import java.net.InetSocketAddress;

public class TestServer implements Configuration{

	@Override
	public InputStream readOntologyXML() {
		return getClass().getResourceAsStream("/ontology.xml");
	}

	@Override
	public InputStream readAPIKeyProperties() {
		return getClass().getResourceAsStream("/api-keys.properties");
	}

	@Override
	public String getDatabasePath() {
		return "target/broker";
	}

	@Override
	public String getAggregatorDataPath() {
		return "target/aggregator-data";
	}

	@Override
	public int getPort() {
		return 8080;
	}

	/**
	 * Run the test server with with the official i2b2
	 * webclient.
	 * @param args command line arguments: port can be specified optionally
	 * @throws Exception any error
	 */
	public static void main(String[] args) throws Exception{
		// use port if specified
		int port;
		if( args.length == 0 ){
			port = 8080;
		}else if( args.length == 1 ){
			port = Integer.parseInt(args[0]);
		}else{
			System.err.println("Too many command line arguments!");
			System.err.println("Usage: "+HttpServer.class.getCanonicalName()+" [port]");
			System.exit(-1);
			return;
		}

		
		// load hsql driver
		Class.forName("org.hsqldb.jdbcDriver");
		
		// start server
		HttpServer server = new HttpServer(new TestServer());
		try{
			server.start(new InetSocketAddress(port));
			System.err.println("Broker service at: "+server.getBrokerServiceURI());
			server.join();
		}finally{
			server.destroy();
		}
	}

}
