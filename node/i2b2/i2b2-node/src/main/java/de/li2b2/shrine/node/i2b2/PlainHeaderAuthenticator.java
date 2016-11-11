package de.li2b2.shrine.node.i2b2;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.aktin.broker.client.auth.ClientAuthenticator;

public class PlainHeaderAuthenticator implements ClientAuthenticator{
	public static final String X_SSL_CLIENT_ID = "X-SSL-Client-ID";
	public static final String X_SSL_CLIENT_DN = "X-SSL-Client-DN";
	public static final String X_SSL_CLIENT_VERIFY = "X-SSL-Client-Verify";

	private String clientId;
	private String clientDn;
	
	private PlainHeaderAuthenticator(String clientId, String clientDn) {
		this.clientDn = clientDn;
		this.clientId = clientId;
	}
	@Override
	public HttpURLConnection openAuthenticatedConnection(URL url) throws IOException {
		HttpURLConnection c = (HttpURLConnection)url.openConnection();
		c.setRequestProperty(X_SSL_CLIENT_VERIFY, "SUCCESS");
		c.setRequestProperty(X_SSL_CLIENT_ID, clientId);
		c.setRequestProperty(X_SSL_CLIENT_DN, clientDn);
		return c;
	}
	
}
