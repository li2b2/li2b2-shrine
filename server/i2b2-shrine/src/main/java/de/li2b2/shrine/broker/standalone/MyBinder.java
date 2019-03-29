package de.li2b2.shrine.broker.standalone;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.aktin.broker.RequestTypeManager;
import org.aktin.broker.auth.AuthCache;
import org.aktin.broker.db.AggregatorBackend;
import org.aktin.broker.db.AggregatorImpl;
import org.aktin.broker.db.BrokerBackend;
import org.aktin.broker.db.BrokerImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.li2b2.shrine.broker.admin.BrokerQueryManager;
import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.ont.Ontology;
import de.sekmi.li2b2.api.pm.ProjectManager;
import de.sekmi.li2b2.services.impl.OntologyImpl;
import de.sekmi.li2b2.services.token.TokenManager;


public class MyBinder extends AbstractBinder{

	private DataSource ds;
	private QueryManager qm;
	private ProjectManager pm;
	private Ontology ont;
	private Configuration config;
	private BrokerBackend broker;
	private AggregatorBackend aggregator;
	
	public MyBinder(DataSource ds, ProjectManager pm, Configuration config){
		this.ds = ds;
		this.pm = pm;
		this.config = config;
	}
	@Override
	protected void configure() {
		// singleton

		try {
			broker = new BrokerImpl(ds, Paths.get(config.getBrokerDataPath()));
			// set aggregator data directory
			aggregator = new AggregatorImpl(ds, Paths.get(config.getAggregatorDataPath()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		bind(broker).to(BrokerBackend.class);
		bind(aggregator).to(AggregatorBackend.class);
		bind(new AuthCache(broker)).to(AuthCache.class);
		bind(new RequestTypeManager()).to(RequestTypeManager.class);
		qm = new BrokerQueryManager(broker, aggregator);

		// bind li2b2 backend implementations
		bind(qm).to(QueryManager.class);
		bind(pm).to(ProjectManager.class);
		
		// ontology
		try {
			ont = OntologyImpl.parse(config.readOntologyXML());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		bind(ont).to(Ontology.class);

		bind(new TokenManagerImpl()).to(TokenManager.class);
		// bind 
		//bind(PMService.class).to(AbstractCell.class);
		//bind(WorkplaceService.class).to(AbstractCell.class);
	}

}
