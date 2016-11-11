package de.li2b2.shrine.broker.admin;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXB;

import org.aktin.broker.client.BrokerAdmin;
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
	private BrokerAdmin broker;
	
	public BrokerQueryManager(URI brokerEndpoint) {
		// TODO empty constructor and set brokerEndpoint later
		broker = new BrokerAdmin(brokerEndpoint);
	}
	@Override
	public Query runQuery(String userId, String groupId, Element queryDefinition, String[] result_types) throws IOException {
		// TODO Auto-generated method stub
		URI uri = broker.createRequest(MEDIA_TYPE_I2B2_QUERY_DEFINITION, queryDefinition);
		String displayName = "Query "+broker.getQueryId(uri);
		// add metadata
		QueryMetadata meta = new QueryMetadata(displayName, userId, groupId, Instant.now());
		meta.resultTypes = result_types;
		// for debugging+logging use intermediate string
		StringWriter tmp = new StringWriter();
		JAXB.marshal(meta, tmp);
		broker.putRequestDefinition(uri, QueryMetadata.MEDIA_TYPE, tmp.toString());
		BrokerI2b2Query query = new BrokerI2b2Query(broker, broker.getRequestInfo(broker.getQueryId(uri)));
		query.setMetadata(meta);
		// post result output list for i2b2 nodes
		broker.putRequestDefinition(uri, MEDIA_TYPE_I2B2_RESULT_OUTPUT_LIST, String.join("\n", result_types));
		return query;
	}

	@Override
	public Query getQuery(String queryId) throws IOException {
		RequestInfo info = broker.getRequestInfo(queryId);
		if( info == null ){
			return null;
		}
		BrokerI2b2Query query = new BrokerI2b2Query(broker, info);
		if( !info.hasMediaType(QueryMetadata.MEDIA_TYPE) ){
			// query was created without our tool. try to construct metadata
			throw new UnsupportedOperationException("Externally created queries not supported yet");
		}
		query.loadMetadata();
		return query;
	}

	@Override
	public Iterable<? extends Query> listQueries(String userId) throws IOException{
		List<RequestInfo> requests = broker.listAllRequests();
		List<BrokerI2b2Query> queries = new ArrayList<>(requests.size());
		for( RequestInfo info : requests ){
			// convert request
			queries.add(new BrokerI2b2Query(broker, info));
		}
		
		return queries;
	}

	@Override
	public Iterable<? extends ResultType> getResultTypes() {
		return Arrays.asList(ResultType.PATIENT_COUNT_XML);
	}

	@Override
	public void deleteQuery(String queryId) throws IOException{
		// delete query globally
		broker.delete(broker.resolveBrokerURI("request/"+queryId));
	}

}
