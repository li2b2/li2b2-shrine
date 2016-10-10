package org.aktin.broker.i2b2.standalone;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DefaultConfiguration implements Configuration{

	@Override
	public InputStream readOntologyXML() throws FileNotFoundException {
		return new FileInputStream("ontology.xml");
	}

	@Override
	public InputStream readAPIKeyProperties() throws FileNotFoundException {
		return new FileInputStream("api-keys.properties");
	}

	@Override
	public String getDatabasePath() {
		return "broker";
	}

	@Override
	public int getPort() {
		return 8080;
	}

}
