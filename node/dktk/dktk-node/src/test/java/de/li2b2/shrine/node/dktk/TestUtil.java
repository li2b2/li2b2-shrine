package de.li2b2.shrine.node.dktk;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import static org.junit.Assert.*;
public class TestUtil {
	@Test
	public void extractInfoVersion() throws IOException{
		try( InputStream in = getClass().getResourceAsStream("/centraxx-info1.json") ){
			assertEquals("3.5.4.13", CentraxxUtil.extractInfoVersion(in));
		}
		try( InputStream in = getClass().getResourceAsStream("/centraxx-info2.json") ){
			assertEquals("3.5.4.13", CentraxxUtil.extractInfoVersion(in));
		}
		try( InputStream in = getClass().getResourceAsStream("/centraxx-info3.json") ){
			assertEquals("3.5.4.13", CentraxxUtil.extractInfoVersion(in));
		}
	}
}
