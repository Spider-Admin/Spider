package org.spider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.spider.data.Key;
import org.spider.junit.InitExtension;

@ExtendWith(InitExtension.class)
public class KeyTest {

	@Test
	public void invalidKeys() {
		Key key = new Key("file.ext");
		assertFalse(key.isKey());
		assertEquals("file.ext", key.getPath());

		key = new Key("file.ext#anchor");
		assertFalse(key.isKey());
		assertEquals("file.ext", key.getPath());

		key = new Key("file.ext?params");
		assertFalse(key.isKey());
		assertEquals("file.ext", key.getPath());

		key = new Key("file.ext" + "\r");
		assertFalse(key.isKey());
		assertEquals("file.ext", key.getPath());

		key = new Key("file.ext" + "\n");
		assertFalse(key.isKey());
		assertEquals("file.ext", key.getPath());

		key = new Key("file.ext" + "\r\n");
		assertFalse(key.isKey());
		assertEquals("file.ext", key.getPath());

		key = new Key("folder/file.ext");
		assertFalse(key.isKey());
		assertEquals("folder/file.ext", key.getPath());

		key = new Key("/folder/file.ext");
		assertFalse(key.isKey());
		assertEquals("folder/file.ext", key.getPath());

		key = new Key("http://something/");
		assertFalse(key.isKey());
		assertEquals("http://something/", key.getPath());
	}

	@Test
	public void validKeys() {
		Key key = new Key("/USK@something/site/1/");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("", key.getFilename());
		assertEquals("", key.getExtension());

		key = new Key("/USK@something/site/1/file.ext");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("file.ext", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());

		key = new Key("/USK@something/site/1/file.ext#anchor");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("file.ext", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());

		key = new Key("/USK@something/site/1/file.ext?params");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("file.ext", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());

		key = new Key("/USK@something/site/1/file.ext" + "\r");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("file.ext", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());

		key = new Key("/USK@something/site/1/file.ext" + "\n");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("file.ext", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());

		key = new Key("/USK@something/site/1/file.ext" + "\r\n");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("file.ext", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());

		key = new Key("/USK@something/site/1/folder1/");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("folder1/", key.getPath());
		assertEquals("folder1/", key.getFolder());
		assertEquals("", key.getFilename());
		assertEquals("", key.getExtension());

		key = new Key("/USK@something/site/1/folder1/folder2/");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("folder1/folder2/", key.getPath());
		assertEquals("folder1/folder2/", key.getFolder());
		assertEquals("", key.getFilename());
		assertEquals("", key.getExtension());

		key = new Key("/USK@something/site/1/folder/file");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("folder/file", key.getPath());
		assertEquals("folder/", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("", key.getExtension());

		key = new Key("/USK@something/site/1/folder/file.ext");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("folder/file.ext", key.getPath());
		assertEquals("folder/", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());

		key = new Key("/USK@something/site/1/folder1/folder2/file.ext");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("folder1/folder2/file.ext", key.getPath());
		assertEquals("folder1/folder2/", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());

		key = new Key("/CHK@something/file.ext");
		assertTrue(key.isKey());
		assertTrue(key.isCHK());
		assertEquals("CHK@something/", key.getKey());
		assertEquals("", key.getKeyOnly());
		assertEquals("", key.getSitePath());
		assertNull(key.getEdition());
		assertNull(key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("file.ext", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());

		key = new Key("/SSK@something/file.ext");
		assertTrue(key.isKey());
		assertTrue(key.isSSK());
		assertEquals("SSK@something/", key.getKey());
		assertEquals("", key.getKeyOnly());
		assertEquals("", key.getSitePath());
		assertNull(key.getEdition());
		assertNull(key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("file.ext", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());

		key = new Key("/KSK@something/file.ext");
		assertTrue(key.isKey());
		assertTrue(key.isKSK());
		assertEquals("KSK@something/", key.getKey());
		assertEquals("", key.getKeyOnly());
		assertEquals("", key.getSitePath());
		assertNull(key.getEdition());
		assertNull(key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("file.ext", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());
	}

	@Test
	public void validSpecialKeys() {
		// No edition
		Key key = new Key("/USK@something/site/");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertNull(key.getEdition());
		assertNull(key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("", key.getFilename());
		assertEquals("", key.getExtension());

		// No "/" after edition
		key = new Key("/USK@something/site/1");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("", key.getFilename());
		assertEquals("", key.getExtension());

		// No leading "/"
		key = new Key("USK@something/site/1/file.ext");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("file.ext", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("file", key.getFilename());
		assertEquals("ext", key.getExtension());

		// Negative edition
		key = new Key("/USK@something/site/-1/");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(-1, key.getEdition());
		assertEquals(-1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("", key.getFilename());
		assertEquals("", key.getExtension());

		// Path starts with number
		key = new Key("/USK@something/site/1/123.htm");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("123.htm", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("123", key.getFilename());
		assertEquals("htm", key.getExtension());

		key = new Key("/USK@something/site/1/123/123.htm");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(1, key.getEdition());
		assertEquals(1, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("123/123.htm", key.getPath());
		assertEquals("123/", key.getFolder());
		assertEquals("123", key.getFilename());
		assertEquals("htm", key.getExtension());

		key = new Key("123/123.htm");
		assertFalse(key.isKey());
		assertNull(key.getKey());
		assertEquals("", key.getKeyOnly());
		assertEquals("", key.getSitePath());
		assertEquals("123/123.htm", key.getPath());
		assertEquals("123/", key.getFolder());
		assertEquals("123", key.getFilename());
		assertEquals("htm", key.getExtension());

		// Very big edition
		key = new Key("/USK@something/site/20161222094840/");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals("USK@something", key.getKeyOnly());
		assertEquals("site", key.getSitePath());
		assertEquals(20161222094840L, key.getEdition());
		assertEquals(20161222094840L, key.getEditionWithHint());
		assertNull(key.getEditionHint());
		assertEquals("", key.getPath());
		assertEquals("", key.getFolder());
		assertEquals("", key.getFilename());
		assertEquals("", key.getExtension());
	}

	@Test
	public void editionUpdate() {
		Key key = new Key("/USK@something/site/5/");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals(5, key.getEdition());
		assertNull(key.getEditionHint());
		key.setEditionHint(5L);
		assertEquals(5, key.getEdition());
		assertEquals(-5, key.getEditionHint());

		key = new Key("/USK@something/site/-5/");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals(-5, key.getEdition());
		assertNull(key.getEditionHint());
		key.setEditionHint(6L);
		assertEquals(-5, key.getEdition());
		assertEquals(-6, key.getEditionHint());

		key = new Key("/USK@something/site/0/");
		assertTrue(key.isKey());
		assertTrue(key.isUSK());
		assertEquals("USK@something/site/", key.getKey());
		assertEquals(0, key.getEdition());
		assertNull(key.getEditionHint());
		key.setEditionHint(0L);
		assertEquals(0, key.getEdition());
		assertEquals(0, key.getEditionHint());
	}
}