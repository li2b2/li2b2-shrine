package org.aktin.broker.i2b2.standalone;

import java.io.IOException;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.aktin.broker.RequestTypeManager;
import org.aktin.broker.auth.AuthCache;
import org.aktin.broker.db.AggregatorBackend;
import org.aktin.broker.db.AggregatorImpl;
import org.aktin.broker.db.BrokerBackend;
import org.aktin.broker.db.BrokerImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.ont.Ontology;
import de.sekmi.li2b2.api.pm.ProjectManager;


public class MyBinder extends AbstractBinder{

	private DataSource ds;
	private QueryManager qm;
	private ProjectManager pm;
	private Ontology ont;
	
	public MyBinder(DataSource ds, QueryManager qm, ProjectManager pm, Ontology ont){
		this.qm = qm;
		this.ds = ds;
		this.pm = pm;
		this.ont = ont;
	}
	@Override
	protected void configure() {
		// singleton

		BrokerBackend backend = new BrokerImpl(ds);
		bind(backend).to(BrokerBackend.class);
		bind(new AuthCache(backend)).to(AuthCache.class);
		try {
			// TODO set aggregator data directory
			AggregatorImpl adb = new AggregatorImpl(ds, Paths.get("target/aggregator-data"));
			bind(adb).to(AggregatorBackend.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		bind(new RequestTypeManager()).to(RequestTypeManager.class);

		// bind li2b2 backend implementations
		bind(qm).to(QueryManager.class);
		bind(pm).to(ProjectManager.class);
		bind(ont).to(Ontology.class);
		// bind 
		//bind(PMService.class).to(AbstractCell.class);
		//bind(WorkplaceService.class).to(AbstractCell.class);
	}

}
