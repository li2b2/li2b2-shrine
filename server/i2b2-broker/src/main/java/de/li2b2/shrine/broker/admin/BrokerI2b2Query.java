package de.li2b2.shrine.broker.admin;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXB;

import org.aktin.broker.client.BrokerAdmin;
import org.aktin.broker.xml.RequestInfo;
import org.aktin.broker.xml.RequestStatus;
import org.aktin.broker.xml.RequestStatusInfo;
import org.aktin.broker.xml.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.QueryExecution;

public class BrokerI2b2Query implements Query {

	private QueryMetadata meta;
	BrokerAdmin broker;
	private RequestInfo info;
	
	public BrokerI2b2Query(BrokerAdmin broker, RequestInfo info) {
		this.broker = broker;
		this.info = info;
	}
	public void setMetadata(QueryMetadata meta){
		this.meta = meta;
	}
	private void lazyLoadMetadata(){
		if( this.meta == null ){
			try {
				loadMetadata();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
	public void loadMetadata() throws IOException{
//		meta = JAXB.unmarshal(broker.getRequestDefinition(getId(), QueryMetadata.MEDIA_TYPE), QueryMetadata.class);
		// load for debugging
		String xml;
		try( Reader r = broker.getRequestDefinition(getId(), QueryMetadata.MEDIA_TYPE) ){
			xml = Util.readContent(r);
		}
		meta = JAXB.unmarshal(new StringReader(xml), QueryMetadata.class);
	}

	@Override
	public String getId() {
		return info.getId();
	}

	@Override
	public String getDisplayName() {
		lazyLoadMetadata();
		return meta.displayName;
	}

	@Override
	public String getUser() {
		lazyLoadMetadata();
		return meta.user;
	}

	@Override
	public String getGroupId() {
		lazyLoadMetadata();
		return meta.group;
	}

	@Override
	public Element getDefinition() throws IOException{
		Reader r = broker.getRequestDefinition(info.getId(), BrokerQueryManager.MEDIA_TYPE_I2B2_QUERY_DEFINITION);
		// convert from reader to Element
		Document dom = Util.parseDocument(r);
		r.close();
		return dom.getDocumentElement();
	}

	@Override
	public Instant getCreateDate() {
		lazyLoadMetadata();
		return meta.createDate;
	}

	@Override
	public void setDisplayName(String name) throws IOException{
		lazyLoadMetadata();
		meta.displayName = name;
		// write to broker
		broker.putRequestDefinition(info.getId(), QueryMetadata.MEDIA_TYPE, out -> JAXB.marshal(meta, out));
	}

	@Override
	public List<? extends QueryExecution> getExecutions() throws IOException {
		List<RequestStatusInfo> list = broker.listRequestStatus(info.getId());
		List<QueryExecution> exec = new ArrayList<>(list.size());
		// TODO cache the node list size somewhere outside to reduce unneccessary load on the broker
		int knownNodes = broker.listNodes().size();
		exec.add(new CurrentFeedbackExecution(this, knownNodes, list));
		for( RequestStatusInfo status : list ){
			exec.add(new BrokerI2b2Execution(this, status));
		}
		return exec;
	}
	/**
	 * Calculate the total number of patients over all returned results / nodes
	 *
	 * @return total number of patients or {@code null} if no results were submitted
	 * @throws IOException error retrieving/reading results
	 */
	public Integer calculateTotalPatientCount() throws IOException{
		int total = 0;
		int includedNodes = 0;
		List<RequestStatusInfo> list = broker.listRequestStatus(info.getId());
		for( RequestStatusInfo status : list ){
			if( status.getStatus() != RequestStatus.completed ){
				// we don't include unfinished requests in the total count.
				// unfinished requests will not have any submitted data
				continue;
			}
			// retrieve reported count
			String count = broker.getResultString(info.getId(), status.node, PatientCountResult.MEDIA_TYPE);
			// add if present
			if( count != null ){
				includedNodes ++;
				total += Integer.parseInt(count);
			}
		}

		// don't return a number if no results were retrieved
		if( includedNodes == 0 ){
			return null;
		}else{
			return total;
		}
	}

}
