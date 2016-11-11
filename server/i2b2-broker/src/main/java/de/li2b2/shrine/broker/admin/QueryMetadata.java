package de.li2b2.shrine.broker.admin;

import java.time.Instant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.aktin.broker.xml.util.InstantAdapter;

@XmlRootElement(name="query-metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryMetadata {
	public static final String MEDIA_TYPE = "text/vnd.broker.meta+xml";
	String displayName;
	String user;
	String group;
	@XmlJavaTypeAdapter(InstantAdapter.class)
	Instant createDate;
	String[] resultTypes;

	protected QueryMetadata(){
		// empty constructor for JAXB
	}
	public QueryMetadata(String displayName, String user, String group, Instant createDate){
		this.displayName = displayName;
		this.user = user;
		this.group = group;
		this.createDate = createDate;
	}
}
