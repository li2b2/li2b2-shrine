package org.aktin.broker.i2b2.standalone;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Singleton;

import org.aktin.broker.auth.AuthFilterAPIKeys;

@Singleton
public class PropertyFileAPIKeys extends AuthFilterAPIKeys {

	private Properties properties;

	public PropertyFileAPIKeys(InputStream in) throws IOException {
		properties = new Properties();
		properties.load(in);
	}

	@Override
	public String getClientDN(String apiKey) {
		return properties.getProperty(apiKey);
	}

}
