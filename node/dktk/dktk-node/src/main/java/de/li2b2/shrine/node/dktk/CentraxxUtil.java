package de.li2b2.shrine.node.dktk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CentraxxUtil {

	public static String extractInfoVersion(InputStream in) throws IOException{
		Pattern pattern = Pattern.compile("\"centraxxVersion\"[^\\w]+([\\w\\._-]+)");
		String version = null;
		try( BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)) ){
			while( true ){
				String line = r.readLine();
				if( line == null ){
					// stop at EOF
					break;
				}
				Matcher matcher = pattern.matcher(line);
				if( matcher.find() ){
					// stop if version found
					version = matcher.group(1);
					break;
				}
			}
		}
		return version;
	}
}
