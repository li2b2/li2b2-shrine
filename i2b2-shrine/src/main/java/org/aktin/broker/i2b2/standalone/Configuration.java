package org.aktin.broker.i2b2.standalone;

import java.io.IOException;
import java.io.InputStream;

public interface Configuration {

	public InputStream readOntologyXML()throws IOException;
	public InputStream readAPIKeyProperties()throws IOException;
	public String getDatabasePath();
	public int getPort();
}
