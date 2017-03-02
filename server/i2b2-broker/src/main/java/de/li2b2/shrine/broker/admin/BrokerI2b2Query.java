package de.li2b2.shrine.broker.admin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXB;

import org.aktin.broker.server.DateDataSource;
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
	BrokerQueryManager qm;
	private RequestInfo info;
	
	public BrokerI2b2Query(BrokerQueryManager qm, RequestInfo info) {
		this.qm = qm;
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
		try( Reader r = qm.broker.getRequestDefinition(getId(), QueryMetadata.MEDIA_TYPE) ){
			xml = Util.readContent(r);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		meta = JAXB.unmarshal(new StringReader(xml), QueryMetadata.class);
	}

	@Override
	public int getId() {
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
		Document dom;
		try( Reader r = qm.broker.getRequestDefinition(info.getId(), BrokerQueryManager.MEDIA_TYPE_I2B2_QUERY_DEFINITION) ){
			// convert from reader to Element
			dom = Util.parseDocument(r);			
		} catch (SQLException e) {
			throw new IOException(e);
		}
		return dom.getDocumentElement();
	}

	@Override
	public Instant getCreateDate() {
		lazyLoadMetadata();
		return meta.createDate;
	}

	@Override
	public void setDisplayName(String name) throws IOException{
		// TODO will result in HTTP 500, FIXME
		lazyLoadMetadata();
		meta.displayName = name;
		// write to broker
		StringWriter w = new StringWriter();
		JAXB.marshal(meta, w);
		try {
			qm.broker.setRequestDefinition(info.getId(), QueryMetadata.MEDIA_TYPE, new StringReader(w.toString()));
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<? extends QueryExecution> getExecutions() throws IOException {
		List<RequestStatusInfo> list;
		int knownNodes;
		try {
			list = qm.broker.listRequestNodeStatus(info.getId());
			// TODO cache the node list size somewhere outside to reduce unneccessary load on the broker
			knownNodes = qm.broker.getAllNodes().size();
		} catch (SQLException e) {
			throw new IOException(e);
		}
		List<QueryExecution> exec = new ArrayList<>(list.size());
		exec.add(new CurrentFeedbackExecution(this, knownNodes, list));
		for( RequestStatusInfo status : list ){
			exec.add(new BrokerI2b2Execution(this, status));
		}
		return exec;
	}

	protected Integer readPatientCountResult(int node) throws NumberFormatException, IOException{
		DateDataSource result;
		// TODO verify PatientCountResult.MEDIA_TYPE
		try {
			result = qm.aggregator.getResult(getId(), node);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		if( result == null ){
			// no result
			return null;
		}else{
			try( InputStreamReader r = new InputStreamReader(result.getInputStream()) ){
				return Integer.parseInt(Util.readContent(r));
			}
		}
		
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
		try{
			List<RequestStatusInfo> list = qm.broker.listRequestNodeStatus(getId());
			for( RequestStatusInfo status : list ){
				if( status.getStatus() != RequestStatus.completed ){
					// we don't include unfinished requests in the total count.
					// unfinished requests will not have any submitted data
					continue;
				}
				// retrieve reported count
				Integer count = readPatientCountResult(status.node);
				// add if present
				if( count != null ){
					includedNodes ++;
					total += count;
				}
			}
		}catch( SQLException e ){
			throw new IOException(e);
		}
		// don't return a number if no results were retrieved
		if( includedNodes == 0 ){
			return null;
		}else{
			return total;
		}
	}

}
