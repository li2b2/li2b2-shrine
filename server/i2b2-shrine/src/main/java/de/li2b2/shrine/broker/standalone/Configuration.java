package de.li2b2.shrine.broker.standalone;

import java.io.IOException;
import java.io.InputStream;
public interface Configuration {

	public InputStream readOntologyXML()throws IOException;
	public InputStream readAPIKeyProperties()throws IOException;
	public String getDatabasePath();
	public String getAggregatorDataPath();
	public String getBrokerDataPath();
	public String getProjectManagerPath();
	public int getPort();
}
