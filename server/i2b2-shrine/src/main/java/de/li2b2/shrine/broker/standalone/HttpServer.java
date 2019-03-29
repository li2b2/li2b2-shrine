package de.li2b2.shrine.broker.standalone;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.aktin.broker.AggregatorEndpoint;
import org.aktin.broker.BrokerEndpoint;
import org.aktin.broker.db.LiquibaseWrapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import de.li2b2.shrine.broker.standalone.db.HSQLDataSource;
import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.api.pm.User;
import de.sekmi.li2b2.services.OntologyService;
import de.sekmi.li2b2.services.PMService;
import de.sekmi.li2b2.services.QueryToolService;
import de.sekmi.li2b2.services.Webadmin;
import de.sekmi.li2b2.services.Webclient;
import de.sekmi.li2b2.services.WorkplaceService;
import de.sekmi.li2b2.services.impl.ProjectManagerImpl;
import liquibase.exception.LiquibaseException;

public class HttpServer {
	private Configuration config;
	private ResourceConfig rc;
	private Server jetty;
	private DataSource ds;
	private ProjectManager pm;
	
	public HttpServer(Configuration config) throws SQLException, IOException{
		this.config = config;
		ds = new HSQLDataSource(config.getDatabasePath());
		// initialize database
		initialiseDatabase();
		rc = new ResourceConfig();
		// register broker services
		try( InputStream in = config.readAPIKeyProperties() ){
			rc.register(new PropertyFileAPIKeys(in));			
		}
		register(BrokerEndpoint.class);
		register(AggregatorEndpoint.class);
		// register i2b2 cells
		register(PMService.class);
		register(QueryToolService.class);
		register(WorkplaceService.class);
		register(OntologyService.class);
		// register webclient
		register(Webclient.class);
		register(Webadmin.class);

		loadLi2b2Backend();
	}

	private void initialiseDatabase() throws SQLException{
		try( LiquibaseWrapper w = new LiquibaseWrapper(ds.getConnection()) ){
			w.update();
		} catch (LiquibaseException e ) {
			throw new SQLException("Unable to initialise database", e);
		}
	}
	private void loadLi2b2Backend() throws IOException{
		pm = new ProjectManagerImpl();
		User user = pm.addUser("demo");//, "i2b2demo");
		user.setPassword("demouser".toCharArray());
		pm.addProject("Demo", "li2b2 Demo").addUserRoles(user, "USER","EDITOR","DATA_OBFSC","DATA_AGG");
		//pm.addProject("Demo2", "li2b2 Demo2").addUserRoles(user, "USER");		
		
	}
	public final void register(Class<?> componentClass){
		rc.register(componentClass);
	}
	
	protected void start_local(int port) throws Exception{
		start(new InetSocketAddress(InetAddress.getLoopbackAddress(), port));
	}
	public URI getBrokerServiceURI(){
		return jetty.getURI().resolve(BrokerEndpoint.SERVICE_URL);
	}
	public void start(InetSocketAddress addr) throws Exception{
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		jetty = new Server(addr);
		jetty.setHandler(context);

		// initialise query manager
		rc.register(new MyBinder(ds, pm, config));

		ServletHolder jersey = new ServletHolder(new ServletContainer(rc));
//		jersey.setInitOrder(0);
		context.addServlet(jersey, "/*");

		jetty.start();
	}
	public void join() throws InterruptedException{
		jetty.join();
	}
	public void destroy() throws Exception{
		if( jetty == null ) {
			// jetty not started, no need to destry
			return;
		}
		jetty.destroy();
	}
	public void stop() throws Exception{
		jetty.stop();
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
		InetAddress bindaddr;
		if( args.length == 0 ){
			port = 8080;
			bindaddr = InetAddress.getLoopbackAddress();
		}else if( args.length == 1 ){
			int colon = args[0].indexOf(':');
			if( colon == -1 ){
				bindaddr = InetAddress.getLoopbackAddress();
				port = Integer.parseInt(args[0]);
			}else{
				bindaddr = InetAddress.getByName(args[0].substring(0, colon));
				port = Integer.parseInt(args[0].substring(colon+1));
			}
		}else{
			System.err.println("Too many command line arguments!");
			System.err.println("Usage: "+HttpServer.class.getCanonicalName()+" [[hostname:]port]");
			System.exit(-1);
			return;
		}

		
		// load hsql driver
		Class.forName("org.hsqldb.jdbcDriver");
		
		// start server
		HttpServer server = new HttpServer(new DefaultConfiguration());
		try{
			server.start(new InetSocketAddress(bindaddr, port));
			System.err.println("Broker service at: "+server.getBrokerServiceURI());
			server.join();
		}finally{
			server.destroy();
		}
	}
}
