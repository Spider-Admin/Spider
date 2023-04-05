package org.spider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spider.data.Freesite;
import org.spider.data.Key;
import org.spider.storage.Database;
import org.spider.storage.Storage;

public class SpiderTest {
	private Connection connection;

	private static final String DUMMY_KEY = "USK@0123456789012345678901234567890123456789123,0123456789012345678901234567890123456789123,1234567/";

	@BeforeEach
	public void beforeTest() throws SQLException {
		connection = Database.getConnectionTest();
	}

	@AfterEach
	public void afterTest() throws SQLException {
		connection.rollback();
		connection.close();
	}

	@Test
	public void init() throws SQLException, IOException {
		try (Spider spider = new Spider(connection); Storage storage = new Storage(connection);) {
			ArrayList<Freesite> freesites = storage.getAllFreesite(false);
			assertEquals(0, freesites.size());

			spider.init();
			freesites = storage.getAllFreesite(false);
			assertEquals(1, freesites.size());

			Freesite freesite = freesites.get(0);
			Key key = freesite.getKeyObj();

			assertEquals(
					"USK@Isel-izgllc8sr~1reXQJz1LNGLIY-voOnLWWOyagYQ,xWfr4py0YZqAQSI-BX7bolDe-kI3DW~i9xHCHd-Bu9k,AQACAAE/linkageddon/",
					key.getKey());
			assertNull(key.getEdition());
			assertEquals(-1128, key.getEditionHint());
			assertEquals("", key.getPath());

			assertNull(freesite.getAuthor());
			assertNull(freesite.getTitle());
			assertNull(freesite.getKeywordsRaw());
			assertNull(freesite.getDescription());
			assertNull(freesite.getLanguage());
			assertNull(freesite.isFMS());
			assertNull(freesite.isSone());
			assertNull(freesite.hasActiveLink());
			assertNull(freesite.isOnline());
			assertNotNull(freesite.getAdded());
			assertNull(freesite.getCrawled());
			assertNull(freesite.getComment());
		}
	}

	@Test
	public void addFreesite() throws SQLException, IOException {
		try (Spider spider = new Spider(connection); Storage storage = new Storage(connection);) {
			ArrayList<Freesite> freesites = storage.getAllFreesite(false);
			assertEquals(0, freesites.size());

			spider.addFreesite("/" + DUMMY_KEY + "site1/1/file.ext");
			spider.addFreesite("/" + DUMMY_KEY + "site2/1/");
			spider.addFreesite("/" + DUMMY_KEY + "site3/-1/");
			spider.addFreesite("CHK@somethingsomethingsomethingsomething/file.ext");
			spider.addFreesite("CHK@somethingsomethingsomethingsomething/file.ext");

			freesites = storage.getAllFreesite(false);
			assertEquals(3, freesites.size());

			Freesite freesite = freesites.get(0);
			Key key = freesite.getKeyObj();

			assertEquals(DUMMY_KEY + "site1/", key.getKey());
			assertNull(key.getEdition());
			assertEquals(-1, key.getEditionHint());
			assertEquals("", key.getPath());

			assertNull(freesite.getAuthor());
			assertNull(freesite.getTitle());
			assertNull(freesite.getKeywordsRaw());
			assertNull(freesite.getDescription());
			assertNull(freesite.getLanguage());
			assertNull(freesite.isFMS());
			assertNull(freesite.isSone());
			assertNull(freesite.hasActiveLink());
			assertNull(freesite.isOnline());
			assertNotNull(freesite.getAdded());
			assertNull(freesite.getCrawled());
			assertNull(freesite.getComment());
			assertEquals("", freesite.getCategory());
		}
	}
}
