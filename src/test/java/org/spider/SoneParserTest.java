package org.spider;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class SoneParserTest {

	private static final String TEST_PATH = "src/test/resources/SoneParser/";

	@Test
	public void normal() throws ParserConfigurationException, SAXException, IOException {
		SoneParser parser = new SoneParser();
		parser.parseStream(new FileInputStream(TEST_PATH + "sone.xml"));

		List<String> values = parser.getValues();
		assertEquals(2, values.size());
		assertEquals("some value", values.get(0));
		assertEquals("more\nvalues", values.get(1));

		List<String> texts = parser.getTexts();
		assertEquals(3, texts.size());
		assertEquals("some text", texts.get(0));
		assertEquals("more\ntexts", texts.get(1));
		assertEquals("more text\nin a\nreply", texts.get(2));
	}
}