package de.li2b2.shrine.broker.admin;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.transform.TransformerException;

import org.aktin.broker.server.Aggregator;
import org.aktin.broker.server.Broker;
import org.aktin.broker.xml.RequestInfo;
import org.w3c.dom.Element;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.crc.ResultType;

public class BrokerQueryManager implements QueryManager {
	public static final String MEDIA_TYPE_I2B2_QUERY_DEFINITION = "text/vnd.i2b2.query-definition+xml";
	private static final String MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST = "text/vnd.i2b2.result-output-list";
//	private static final String MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST = "text/vnd.i2b2.result-output-list+xml";
//	private static final String MEDIA_TYPE_I2B2_RESULT_ENVELOPE = "text/vnd.i2b2.result-envelope+xml";
	// TODO directly use the BrokerBackend without the local HTTP layer
	Broker broker;
	Aggregator aggregator;
	
	public BrokerQueryManager(Broker broker, Aggregator aggregator) {
		// TODO empty constructor and set brokerEndpoint later
		this.broker = broker;
		this.aggregator = aggregator;
	}
	@Override
	public Query runQuery(String userId, String groupId, Element queryDefinition, String[] result_types) throws IOException {
		try {
			int req = broker.createRequest(MEDIA_TYPE_I2B2_QUERY_DEFINITION, new StringReader(DOMUtils.toString(queryDefinition)));
			String displayName = "Query "+req;
			// add metadata
			QueryMetadata meta = new QueryMetadata(displayName, userId, groupId, Instant.now());
			meta.resultTypes = result_types;
			// for debugging+logging use intermediate string
			StringWriter tmp = new StringWriter();
			JAXB.marshal(meta, tmp);
			broker.setRequestDefinition(req, QueryMetadata.MEDIA_TYPE, new StringReader(tmp.toString()));
			BrokerI2b2Query query = new BrokerI2b2Query(this, broker.getRequestInfo(req));
			query.setMetadata(meta);
			// post result output list for i2b2 nodes
			broker.setRequestDefinition(req, MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST, new StringReader(String.join("\n", result_types)));
			// publish query (XXX allow manual publishing through workplace folders later)
			broker.setRequestPublished(req, Instant.now());
			return query;
		} catch (SQLException | TransformerException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Query getQuery(int queryId) throws IOException {
		RequestInfo info;
		try {
			info = broker.getRequestInfo(queryId);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		if( info == null ){
			return null;
		}
		BrokerI2b2Query query = new BrokerI2b2Query(this, info);
		if( !info.hasMediaType(QueryMetadata.MEDIA_TYPE) ){
			// query was created without our tool. try to construct metadata
			throw new UnsupportedOperationException("Externally created queries not supported yet");
		}
		query.loadMetadata();
		return query;
	}

	@Override
	public Iterable<? extends Query> listQueries(String userId) throws IOException{
		List<RequestInfo> requests;
		try {
			requests = broker.listAllRequests();
		} catch (SQLException e) {
			throw new IOException(e);
		}
		List<BrokerI2b2Query> queries = new ArrayList<>(requests.size());
		for( RequestInfo info : requests ){
			// convert request
			queries.add(new BrokerI2b2Query(this, info));
		}
		
		return queries;
	}

	@Override
	public Iterable<? extends ResultType> getResultTypes() {
		return Arrays.asList(ResultType.PATIENT_COUNT_XML);
	}

	@Override
	public void deleteQuery(int queryId) throws IOException{
		// delete query globally
		try {
			broker.deleteRequest(queryId);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

}
