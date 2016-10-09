package org.aktin.broker.i2b2.admin;

import java.time.Instant;
import java.util.Collections;
import java.util.Map.Entry;

import de.sekmi.li2b2.api.crc.QueryResult;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.api.crc.ResultType;

public class PatientCountResult implements QueryResult{
	private BrokerI2b2Execution exec;
	private int patient_count;

	public PatientCountResult(int patientCount, BrokerI2b2Execution execution) {
		this.patient_count = patientCount;
		this.exec = execution;
	}
	@Override
	public ResultType getResultType() {
		return ResultType.PATIENT_COUNT_XML;
	}

	@Override
	public Integer getSetSize() {
		return patient_count;
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
		return exec.getStatus();
	}

	@Override
	public Iterable<? extends Entry<String, ?>> getBreakdownData() {
		return Collections.singletonMap("patient_count", new Integer(patient_count)).entrySet();
	}

}
