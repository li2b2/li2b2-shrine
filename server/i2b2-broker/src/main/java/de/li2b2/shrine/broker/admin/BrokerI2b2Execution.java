package de.li2b2.shrine.broker.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aktin.broker.xml.Node;
import org.aktin.broker.xml.RequestStatusInfo;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryExecution;
import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;

public class BrokerI2b2Execution implements QueryExecution{

	private RequestStatusInfo status;
	private BrokerI2b2Query query;

	public BrokerI2b2Execution(BrokerI2b2Query query, RequestStatusInfo status) {
		this.query = query;
		this.status = status;
	}
	@Override
	public Query getQuery() {
		return query;
	}

	
	@Override
	public QueryStatus getStatus() {
		QueryStatus ret;
		if( status.getStatus() == null ){
			// no status means not yet retrieved
			return QueryStatus.WAITTOPROCESS;
		}
		switch( status.getStatus() ){
		case queued:
		case processing:
			ret = QueryStatus.PROCESSING;
			break;
		case completed:
			ret = QueryStatus.COMPLETED;
			break;
		case failed:
		case rejected:
			ret = QueryStatus.ERROR;
			break;
		case retrieved:
		default:
			ret = QueryStatus.WAITTOPROCESS;
			break;
		}
		return ret;
	}

	@Override
	public String getLabel() {
		// try to retrieve info about the node
		String label = null;
		Node node;
		try {
			node = query.qm.broker.getNode(status.node);
		} catch (SQLException e) {
			// TODO log warning
			node = null;
		}
		if( node != null && node.clientDN != null ){
			label = node.getCommonName();
		}
		if( label == null ){
			label = "Node "+status.node;
		}
		return label;
	}

	@Override
	public List<? extends QueryResult> getResults() throws IOException {

		NodeList results = query.readResultBundleEntries(status.node);
		if( results == null ) {
			// no result yet
//			QueryMetadata meta = query.getMetadata();
			// XXX try to respond with incomplete results (name) from meta
			return Collections.emptyList();
		}

		List<BreakdownResult> list = new ArrayList<>();
		for( int i=0; i<results.getLength(); i++ ) {
			list.add(BreakdownResult.parseElement((Element)results.item(i)));
		}
		return list;
	}

}
