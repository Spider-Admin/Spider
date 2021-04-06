package org.spider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * If some tests fail in Eclipse, set the (default) encoding to 'UTF-8'
 */
public class HTMLParserTest {

	private static final String TEST_PATH = "src/test/resources/HTMLParser/";

	@Test
	public void empty() throws FileNotFoundException, IOException {
		HTMLParser parser = new HTMLParser();
		parser.parseStream(new FileInputStream(TEST_PATH + "empty.htm"));
		assertEquals("", parser.getAuthor());
		assertEquals("", parser.getTitle());
		assertEquals("", parser.getDescription());
		assertEquals("", parser.getKeywords());
		assertEquals("", parser.getLanguage());
		assertEquals("", parser.getRedirect());
		assertEquals(0, parser.getPaths().size());
	}

	@Test
	public void normal() throws FileNotFoundException, IOException {
		HTMLParser parser = new HTMLParser();
		parser.parseStream(new FileInputStream(TEST_PATH + "normal.htm"));
		assertEquals("author образец", parser.getAuthor());
		assertEquals("title образец", parser.getTitle());
		assertEquals("description образец", parser.getDescription());
		assertEquals("keyword1, keyword2 образец", parser.getKeywords());
		assertEquals("en образец", parser.getLanguage());
		assertEquals("redirect образец.htm", parser.getRedirect());

		ArrayList<String> paths = parser.getPaths();
		assertEquals(4, paths.size());
		assertEquals("index образец.htm", paths.get(0));
		assertEquals("test.htm", paths.get(1));
		assertEquals("iframe образец.htm", paths.get(2));
		assertEquals("iframe2.htm", paths.get(3));
	}

	@Test
	public void frame() throws FileNotFoundException, IOException {
		HTMLParser parser = new HTMLParser();
		parser.parseStream(new FileInputStream(TEST_PATH + "frame.htm"));
		assertEquals("author образец", parser.getAuthor());
		assertEquals("title образец", parser.getTitle());
		assertEquals("description образец", parser.getDescription());
		assertEquals("keyword1, keyword2 образец", parser.getKeywords());
		assertEquals("en образец", parser.getLanguage());
		assertEquals("redirect образец.htm", parser.getRedirect());

		ArrayList<String> paths = parser.getPaths();
		assertEquals(2, paths.size());
		assertEquals("frame образец.htm", paths.get(0));
		assertEquals("frame2.htm", paths.get(1));
	}

	@Test
	public void special() throws FileNotFoundException, IOException {
		HTMLParser parser = new HTMLParser();
		parser.parseStream(new FileInputStream(TEST_PATH + "special.htm"));
		assertEquals("author образец", parser.getAuthor());
		assertEquals("title образец", parser.getTitle());
		assertEquals("description образец", parser.getDescription());
		assertEquals("keyword1, keyword2 образец", parser.getKeywords());
		assertEquals("en2 образец", parser.getLanguage());
		assertEquals("redirect образец.htm", parser.getRedirect());

		ArrayList<String> paths = parser.getPaths();
		assertEquals(4, paths.size());
		assertEquals("index образец.htm", paths.get(0));
		assertEquals("test.htm", paths.get(1));
		assertEquals("iframe образец.htm", paths.get(2));
		assertEquals("iframe2.htm", paths.get(3));
	}

	@Test
	public void ignoredLinks() {
		assertTrue(HTMLParser.isIgnored("/?newbookmark=USK@something/site/1/&desc=Something&hasAnActivelink=true"));
		assertTrue(HTMLParser.isIgnored("/external-link/?_CHECKED_HTTP_=https://www.apache.org/licenses/LICENSE-2.0"));
		assertTrue(HTMLParser.isIgnored("/Sone/viewSone.html?sone=something"));
		assertTrue(HTMLParser.isIgnored("CHK@something/file.ext"));
		assertTrue(HTMLParser.isIgnored("SSK@something/site-13/"));
		assertTrue(HTMLParser.isIgnored("KSK@file.ext"));
		assertTrue(HTMLParser.isIgnored("#something"));
		assertTrue(HTMLParser.isIgnored("?something"));
		assertTrue(HTMLParser.isIgnored("file.ext"));
		assertTrue(HTMLParser.isIgnored("/USK@something1/site/123/file.ext"));

		assertFalse(HTMLParser.isIgnored("/USK@something1/site/1/"));
		assertFalse(HTMLParser.isIgnored("/USK@something1/site/1/file.htm"));
		assertFalse(HTMLParser.isIgnored("file.htm"));
	}

	@Test
	public void capitalizeKeyType() {
		assertEquals("/USK@something1/site/123/file.ext",
				HTMLParser.capitalizeKeyType("/USK@something1/site/123/file.ext"));
		assertEquals("/USK@something1/site/123/file.ext",
				HTMLParser.capitalizeKeyType("/usk@something1/site/123/file.ext"));

		assertEquals("USK@something1/site/123/file.ext",
				HTMLParser.capitalizeKeyType("USK@something1/site/123/file.ext"));
		assertEquals("USK@something1/site/123/file.ext",
				HTMLParser.capitalizeKeyType("usk@something1/site/123/file.ext"));
	}
}