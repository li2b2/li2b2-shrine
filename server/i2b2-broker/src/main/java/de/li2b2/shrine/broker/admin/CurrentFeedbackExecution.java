package de.li2b2.shrine.broker.admin;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.aktin.broker.xml.RequestStatusInfo;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryExecution;
import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;

public class CurrentFeedbackExecution implements QueryExecution{
	private BrokerI2b2Query query;
	private StatusBreakdown status;
	private TotalPatientCount count;

	public CurrentFeedbackExecution(BrokerI2b2Query query, int knownNodes, List<RequestStatusInfo> statusList) {
		this.query = query;
		this.status = new StatusBreakdown(knownNodes, statusList);
		this.count = new TotalPatientCount(query);
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
//		if( status.hasAnyCompleted() ){
			// calculate total size
			//query
			return Arrays.asList(status, count);	
//		}else{
//			return Collections.singletonList(status);
//		}
	}


}
