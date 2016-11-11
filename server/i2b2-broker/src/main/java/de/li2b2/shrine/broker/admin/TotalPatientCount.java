package de.li2b2.shrine.broker.admin;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.api.crc.ResultType;

/**
 * Output the total number of patients over all participating nodes
 *
 * @author R.W.Majeed
 *
 */
public class TotalPatientCount implements QueryResult{
	private static final Logger log = Logger.getLogger(CurrentFeedbackExecution.class.getName());
	private BrokerI2b2Query query;
	private Integer totalCount;
	private boolean lazyRetrieved;

	public TotalPatientCount(BrokerI2b2Query query){
		this.query = query;
	}
	@Override
	public ResultType getResultType() {
		return ResultType.PATIENT_COUNT_XML;
	}

	@Override
	public Integer getSetSize() {
		if( !lazyRetrieved ){
			try {
				this.totalCount = query.calculateTotalPatientCount();
			} catch (IOException e) {
				log.log(Level.WARNING, "Unable to calculate total patient count", e);
			}
			this.lazyRetrieved = true;
		}
		return totalCount;
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
	public QueryStatus getStatus() {
		return QueryStatus.COMPLETED;
	}

	@Override
	public Iterable<? extends Entry<String, ?>> getBreakdownData() {
		return Collections.singletonMap("patient_count", getSetSize()).entrySet();
	}

}
