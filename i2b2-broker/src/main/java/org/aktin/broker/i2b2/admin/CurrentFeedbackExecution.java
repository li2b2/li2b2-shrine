package org.aktin.broker.i2b2.admin;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aktin.broker.xml.RequestStatus;
import org.aktin.broker.xml.RequestStatusInfo;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryExecution;
import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.api.crc.ResultType;
import de.sekmi.li2b2.api.crc.ResultTypeCategorial;

public class CurrentFeedbackExecution implements QueryExecution, QueryResult{

	private Map<String, Integer> breakdown;
	private int knownNodes;
	private BrokerI2b2Query query;
	private static final ResultType resultType = new ResultTypeCategorial("response_summary", "Response summary");

	public CurrentFeedbackExecution(BrokerI2b2Query query, int knownNodes, List<RequestStatusInfo> statusList) {
		this.query = query;
		this.breakdown = new HashMap<>();
		fillBreakdown(knownNodes, statusList);
	}

	private void fillBreakdown(int knownNodes, List<RequestStatusInfo> statusList){
		int unknowing = knownNodes - statusList.size();
		// count 
		int counts[] = new int[RequestStatus.values().length];
		for( RequestStatusInfo info : statusList ){
			RequestStatus status = info.getStatus();
			if( status == null ){
				unknowing ++;
			}else{
				counts[status.ordinal()] ++;
			}
		}
		breakdown.put("unknowing", unknowing);
		for( int i=0; i<counts.length; i++ ){
			breakdown.put(RequestStatus.values()[i].toString(), counts[i]);
		}
	}
	@Override
	public Query getQuery() {
		return query;
	}

	
	@Override
	public QueryStatus getStatus() {
		return QueryStatus.COMPLETED;
	}

	@Override
	public String getLabel() {
		return "Broker";
	}

	@Override
	public List<? extends QueryResult> getResults() throws IOException {
		return Collections.singletonList(this);
	}

	@Override
	public ResultType getResultType() {
		return resultType;
	}

	@Override
	public Integer getSetSize() {
		return knownNodes;
	}

	@Override
	public Instant getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instant getEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<? extends Entry<String, ?>> getBreakdownData() {
		return breakdown.entrySet();
	}

}
