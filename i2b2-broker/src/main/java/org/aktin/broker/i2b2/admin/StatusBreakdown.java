package org.aktin.broker.i2b2.admin;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aktin.broker.xml.RequestStatus;
import org.aktin.broker.xml.RequestStatusInfo;

import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.api.crc.ResultType;
import de.sekmi.li2b2.api.crc.ResultTypeCategorial;

public class StatusBreakdown implements QueryResult{
	private static final ResultType resultType = new ResultTypeCategorial("response_summary", "Response summary");
	private Map<String, Integer> breakdown;
	private int knownNodes;

	public StatusBreakdown(int knownNodes, List<RequestStatusInfo> statusList) {
		this.knownNodes = knownNodes;
		this.breakdown = new HashMap<>();
		fillBreakdown(knownNodes, statusList);
	}

	public boolean hasAnyCompleted(){
		Integer c = breakdown.get(RequestStatus.completed.toString());
		if( c == null || c.intValue() == 0 ){
			return false;
		}else{
			return true;
		}
	}
	private void fillBreakdown(int knownNodes, List<RequestStatusInfo> statusList){
		int unaware = knownNodes - statusList.size();
		// count 
		int counts[] = new int[RequestStatus.values().length];
		for( RequestStatusInfo info : statusList ){
			RequestStatus status = info.getStatus();
			if( status == null ){
				unaware ++;
			}else{
				counts[status.ordinal()] ++;
			}
		}
		breakdown.put("unaware", unaware);
		for( int i=0; i<counts.length; i++ ){
			breakdown.put(RequestStatus.values()[i].toString(), counts[i]);
		}
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

	@Override
	public QueryStatus getStatus() {
		return QueryStatus.COMPLETED;
	}
}
