package org.aktin.broker.i2b2.standalone;


import java.security.KeyStore;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;


// TODO (old) use http://www.smartjava.org/content/embedded-jetty-client-certificates
// TODO or http://git.eclipse.org/c/jetty/org.eclipse.jetty.project.git/tree/examples/embedded/src/main/java/org/eclipse/jetty/embedded/ManyConnectors.java

public class HttpsServer {
	// the keystore (with one key) we'll use to make the connection with the
	// broker
	private final static String KEYSTORE_LOCATION = "src/main/resources/client_keystore.jks";
	private final static String KEYSTORE_PASS = "secret";
 
	// the truststore we use for our server. This keystore should contain all the keys
	// that are allowed to make a connection to the server
	private final static String TRUSTSTORE_LOCATION = "src/main/resources/truststore.jks";
	private final static String TRUSTSTORE_PASS = "secret";
 
	/**
	 * Simple starter for a jetty HTTPS server.
	 * 
	 * @param args
	 * @throws Exception 
	 */
//	public static void mainOld(String[] args) throws Exception {
//		// create a jetty server and setup the SSL context
//		Server server = new Server();
//		SslContextFactory sslContextFactory = new SslContextFactory(KEYSTORE_LOCATION);
//		sslContextFactory.setKeyStorePassword(KEYSTORE_PASS);
//		sslContextFactory.setTrustStore(TRUSTSTORE_LOCATION);
//		sslContextFactory.setTrustStorePassword(TRUSTSTORE_PASS);
//		sslContextFactory.setNeedClientAuth(true);
// 
//		// create a https connector
//		SslSocketConnector connector = new SslSocketConnector(sslContextFactory);
//		connector.setPort(8443);
// 
//		// register the connector
//		server.setConnectors(new Connector[] { connector });
//		
// 
//		ServletContextHandler scHandler = new ServletContextHandler(server,"/");
//		scHandler.addServlet(NameOfServlet.class, "/");
//		server.start();
//		server.join();
//	}
	public static Server mainNew(KeyStore trustStore, ResourceConfig rc){
		SslConnectionFactory scf;
		Server server = new Server();
		SslContextFactory sslContextFactory = new SslContextFactory(KEYSTORE_LOCATION);
		sslContextFactory.setKeyStorePassword(KEYSTORE_PASS);
		sslContextFactory.setTrustStore(trustStore);
		sslContextFactory.setTrustStorePassword(TRUSTSTORE_PASS);
		// required: need
		sslContextFactory.setNeedClientAuth(true);
		// optional: want
//		sslContextFactory.setWantClientAuth(true);
 
		// create a https connector
		ServerConnector connector = new ServerConnector(server, sslContextFactory);
		connector.setPort(8443);
 
		// register the connector
		server.setConnectors(new Connector[] { connector });

		// add JAXRS servlet
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		ServletHolder jersey = new ServletHolder(new ServletContainer(rc));
//		jersey.setInitOrder(0);
		context.addServlet(jersey, "/*");

		return server;
//		server.start();
	}

}
