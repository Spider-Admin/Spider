package org.spider.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FreenetTest {

	@Test
	public void getMessage() {
		assertEquals("Error: 0", Freenet.Error.getMessage(0));
		assertEquals("MetadataParseException", Freenet.Error.getMessage(4));
		assertEquals("ArchiveFailureException", Freenet.Error.getMessage(5));
		assertEquals("Too much recursion", Freenet.Error.getMessage(9));
		assertEquals("Not in archive", Freenet.Error.getMessage(10));
		assertEquals("Too many path components", Freenet.Error.getMessage(11));
		assertEquals(null, Freenet.Error.getMessage(13));
		assertEquals(null, Freenet.Error.getMessage(14));
		assertEquals(null, Freenet.Error.getMessage(15));
		assertEquals(null, Freenet.Error.getMessage(18));
		assertEquals(null, Freenet.Error.getMessage(19));
		assertEquals("MalformedURLException", Freenet.Error.getMessage(20));
		assertEquals(null, Freenet.Error.getMessage(28));
		assertEquals(null, Freenet.Error.getMessage(30));
		assertEquals("Corrupt or malicious web page", Freenet.Error.getMessage(31));
		assertEquals("Unknown and potentially dangerous content type", Freenet.Error.getMessage(32));
	}

	@Test
	public void getMessageSpecial() {
		assertEquals("Too many path components", Freenet.Error.TOO_MANY_PATH_COMPONENTS.toString());
		assertEquals("Fake-Key", Freenet.Error.FAKE_KEY.toString());
	}
}