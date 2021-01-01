package org.spider.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spider.Key;
import org.spider.utility.DateUtility;

public class StorageTest {
	private Connection connection;

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
	public void databaseVersion() throws SQLException {
		try (Storage storage = new Storage(connection);) {
		}
	}

	@Test
	public void addFreesite() throws SQLException {
		try (Storage storage = new Storage(connection);) {
			Key key1 = new Key("USK@something1/site/1/");
			Key key2 = new Key("USK@something1/site/2/file.ext");

			storage.addFreesite(key1, DateUtility.getDate(2020, 4, 1, 12, 0, 0));

			Integer id1 = storage.getFreesiteID(key1);
			Integer id2 = storage.getFreesiteID(key2);

			assertNotNull(id1);
			assertNotNull(id2);
			assertEquals(id1, id2);

			Freesite freesite = storage.getFreesite(key1);
			Key key = storage.getFreesiteKey(key1);

			assertEquals("USK@something1/site/", key.getKey());
			assertEquals(1, key.getEdition());
			assertNull(key.getEditionHint());

			assertEquals(id1, freesite.getID());
			assertEquals("USK@something1/site/", freesite.getKeyObj().getKey());
			assertEquals(1, freesite.getKeyObj().getEdition());
			assertNull(freesite.getKeyObj().getEditionHint());
			assertNull(freesite.getAuthor());
			assertNull(freesite.getTitle());
			assertNull(freesite.getKeywordsRaw());
			assertNull(freesite.getKeywords());
			assertNull(freesite.getDescription());
			assertNull(freesite.getLanguage());
			assertNull(freesite.isFMS());
			assertNull(freesite.hasActiveLink());
			assertNull(freesite.isOnline());
			assertFalse(freesite.ignoreResetOffline());
			assertFalse(freesite.crawlOnlyIndex());
			assertEquals(DateUtility.getDate(2020, 4, 1, 12, 0, 0), freesite.getAdded());
			assertNull(freesite.getCrawled());
			assertNull(freesite.getComment());
			assertNull(freesite.getPathList());
			assertNull(freesite.getInNetwork());
			assertNull(freesite.getOutNetwork());
		}
	}

	@Test
	public void updateFreesite() throws SQLException {
		try (Storage storage = new Storage(connection);) {
			Key key1 = new Key("USK@something1/site/1/");

			storage.addFreesite(key1, DateUtility.getDate(2020, 4, 1, 0, 0, 0));

			storage.updateFreesite(key1, "author", "title", "keywords", "description", "language", true, false, true,
					false, false, DateUtility.getDate(2020, 4, 1, 12, 0, 0), null);

			Integer id = storage.getFreesiteID(key1);

			assertNotNull(id);

			Freesite freesite = storage.getFreesite(key1);
			Key key = storage.getFreesiteKey(key1);

			assertEquals("USK@something1/site/", key.getKey());
			assertEquals(1, key.getEdition());
			assertNull(key.getEditionHint());

			assertEquals(id, freesite.getID());
			assertEquals("USK@something1/site/", freesite.getKeyObj().getKey());
			assertEquals(1, freesite.getKeyObj().getEdition());
			assertNull(freesite.getKeyObj().getEditionHint());
			assertEquals("author", freesite.getAuthor());
			assertEquals("title", freesite.getTitle());
			assertEquals("keywords", freesite.getKeywordsRaw());
			assertEquals("keywords", freesite.getKeywords());
			assertEquals("description", freesite.getDescription());
			assertEquals("language", freesite.getLanguage());
			assertTrue(freesite.isFMS());
			assertFalse(freesite.hasActiveLink());
			assertTrue(freesite.isOnline());
			assertFalse(freesite.isObsolete());
			assertFalse(freesite.ignoreResetOffline());
			assertFalse(freesite.crawlOnlyIndex());
			assertEquals(DateUtility.getDate(2020, 4, 1, 0, 0, 0), freesite.getAdded());
			assertEquals(DateUtility.getDate(2020, 4, 1, 12, 0, 0), freesite.getCrawled());
			assertNull(freesite.getComment());
			assertNull(freesite.getPathList());
			assertNull(freesite.getInNetwork());
			assertNull(freesite.getOutNetwork());
		}
	}

	@Test
	public void updateFreesiteEdition() throws SQLException {
		try (Storage storage = new Storage(connection);) {
			Key key1 = new Key("USK@something1/site/1/");

			storage.addFreesite(key1, DateUtility.getDate(2020, 4, 1, 0, 0, 0));

			key1.setEdition(2L);
			storage.updateFreesiteEdition(key1, DateUtility.getDate(2020, 4, 1, 12, 0, 0));

			Integer id = storage.getFreesiteID(key1);

			assertNotNull(id);

			Freesite freesite = storage.getFreesite(key1);
			Key key = storage.getFreesiteKey(key1);

			assertEquals(id, freesite.getID());
			assertEquals("USK@something1/site/", key.getKey());
			assertEquals(2, key.getEdition());
			assertNull(key.getEditionHint());

			assertEquals("USK@something1/site/", freesite.getKeyObj().getKey());
			assertEquals(2, freesite.getKeyObj().getEdition());
			assertNull(freesite.getKeyObj().getEditionHint());
			assertNull(freesite.getAuthor());
			assertNull(freesite.getTitle());
			assertNull(freesite.getKeywordsRaw());
			assertNull(freesite.getKeywords());
			assertNull(freesite.getDescription());
			assertNull(freesite.getLanguage());
			assertNull(freesite.isFMS());
			assertNull(freesite.hasActiveLink());
			assertNull(freesite.isOnline());
			assertFalse(freesite.ignoreResetOffline());
			assertFalse(freesite.crawlOnlyIndex());
			assertEquals(DateUtility.getDate(2020, 4, 1, 0, 0, 0), freesite.getAdded());
			assertEquals(DateUtility.getDate(2020, 4, 1, 12, 0, 0), freesite.getCrawled());
			assertNull(freesite.getComment());
			assertNull(freesite.getPathList());
			assertNull(freesite.getInNetwork());
			assertNull(freesite.getOutNetwork());

			key1.setEditionHint(3L);
			storage.updateFreesiteEdition(key1, DateUtility.getDate(2020, 4, 1, 18, 0, 0));

			id = storage.getFreesiteID(key1);

			assertNotNull(id);

			freesite = storage.getFreesite(key);
			key = storage.getFreesiteKey(key1);

			assertEquals("USK@something1/site/", key.getKey());
			assertEquals(2, key.getEdition());
			assertEquals(-3, key.getEditionHint());

			assertEquals(id, freesite.getID());
			assertEquals("USK@something1/site/", freesite.getKeyObj().getKey());
			assertEquals(2, freesite.getKeyObj().getEdition());
			assertEquals(-3, freesite.getKeyObj().getEditionHint());
			assertNull(freesite.getAuthor());
			assertNull(freesite.getTitle());
			assertNull(freesite.getKeywordsRaw());
			assertNull(freesite.getKeywords());
			assertNull(freesite.getDescription());
			assertNull(freesite.getLanguage());
			assertNull(freesite.isFMS());
			assertNull(freesite.hasActiveLink());
			assertNull(freesite.isOnline());
			assertFalse(freesite.ignoreResetOffline());
			assertFalse(freesite.crawlOnlyIndex());
			assertEquals(DateUtility.getDate(2020, 4, 1, 0, 0, 0), freesite.getAdded());
			assertEquals(DateUtility.getDate(2020, 4, 1, 18, 0, 0), freesite.getCrawled());
			assertNull(freesite.getComment());
			assertNull(freesite.getPathList());
			assertNull(freesite.getInNetwork());
			assertNull(freesite.getOutNetwork());
		}
	}

	@Test
	public void addPath() throws SQLException {
		try (Storage storage = new Storage(connection);) {
			Key key = new Key("USK@something1/site/1/");

			storage.addFreesite(key, DateUtility.getDate(2020, 4, 1, 0, 0, 0));

			key.setPath("file1.ext");
			storage.addPath(key, DateUtility.getDate(2020, 4, 1, 12, 0, 0));

			key.setPath("file2.ext");
			storage.addPath(key, DateUtility.getDate(2020, 4, 2, 12, 0, 0));

			Integer id = storage.getFreesiteID(key);

			assertNotNull(id);

			key.setPath("file1.ext");
			Integer pathID1 = storage.getPathID(key);
			key.setPath("file2.ext");
			Integer pathID2 = storage.getPathID(key);

			assertNotNull(pathID1);
			assertNotNull(pathID2);
			assertNotEquals(pathID1, pathID2);

			key.setPath("file1.ext");
			Path path1 = storage.getPath(key);
			assertEquals("file1.ext", path1.getPath());
			assertEquals(DateUtility.getDate(2020, 4, 1, 12, 0, 0), path1.getAdded());
			assertNull(path1.getCrawled());

			key.setPath("file2.ext");
			Path path2 = storage.getPath(key);
			assertEquals("file2.ext", path2.getPath());
			assertEquals(DateUtility.getDate(2020, 4, 2, 12, 0, 0), path2.getAdded());
			assertNull(path1.getCrawled());
		}
	}

	@Test
	public void updatePath() throws SQLException {
		try (Storage storage = new Storage(connection);) {
			Key key = new Key("USK@something1/site/1/");

			storage.addFreesite(key, DateUtility.getDate(2020, 4, 1, 0, 0, 0));

			key.setPath("file1.ext");
			storage.addPath(key, DateUtility.getDate(2020, 4, 1, 12, 0, 0));
			storage.updatePath(key, true, DateUtility.getDate(2020, 5, 1, 12, 0, 0));

			key.setPath("file2.ext");
			storage.addPath(key, DateUtility.getDate(2020, 4, 2, 12, 0, 0));
			storage.updatePath(key, false, DateUtility.getDate(2020, 5, 2, 12, 0, 0));

			Integer id = storage.getFreesiteID(key);

			assertNotNull(id);

			key.setPath("file1.ext");
			Integer pathID1 = storage.getPathID(key);
			key.setPath("file2.ext");
			Integer pathID2 = storage.getPathID(key);

			assertNotNull(pathID1);
			assertNotNull(pathID2);
			assertNotEquals(pathID1, pathID2);

			key.setPath("file1.ext");
			Path path1 = storage.getPath(key);
			assertEquals("file1.ext", path1.getPath());
			assertTrue(path1.isOnline());
			assertEquals(DateUtility.getDate(2020, 4, 1, 12, 0, 0), path1.getAdded());
			assertEquals(DateUtility.getDate(2020, 5, 1, 12, 0, 0), path1.getCrawled());

			key.setPath("file2.ext");
			Path path2 = storage.getPath(key);
			assertEquals("file2.ext", path2.getPath());
			assertFalse(path2.isOnline());
			assertEquals(DateUtility.getDate(2020, 4, 2, 12, 0, 0), path2.getAdded());
			assertEquals(DateUtility.getDate(2020, 5, 2, 12, 0, 0), path2.getCrawled());
		}
	}

	@Test
	public void deleteAllPath() throws SQLException {
		try (Storage storage = new Storage(connection);) {
			Key key = new Key("USK@something1/site/1/");

			storage.addFreesite(key, DateUtility.getDate(2020, 4, 1, 0, 0, 0));

			key.setPath("file1.ext");
			storage.addPath(key, DateUtility.getDate(2020, 4, 1, 12, 0, 0));
			storage.updatePath(key, true, DateUtility.getDate(2020, 5, 1, 12, 0, 0));

			key.setPath("file2.ext");
			storage.addPath(key, DateUtility.getDate(2020, 4, 2, 12, 0, 0));
			storage.updatePath(key, false, DateUtility.getDate(2020, 5, 2, 12, 0, 0));

			Integer id = storage.getFreesiteID(key);

			assertNotNull(id);

			key.setPath("file1.ext");
			Integer pathID1 = storage.getPathID(key);
			key.setPath("file2.ext");
			Integer pathID2 = storage.getPathID(key);

			assertNotNull(pathID1);
			assertNotNull(pathID2);
			assertNotEquals(pathID1, pathID2);

			key.setPath("file1.ext");
			Path path1 = storage.getPath(key);
			assertEquals("file1.ext", path1.getPath());
			assertTrue(path1.isOnline());
			assertEquals(DateUtility.getDate(2020, 4, 1, 12, 0, 0), path1.getAdded());
			assertEquals(DateUtility.getDate(2020, 5, 1, 12, 0, 0), path1.getCrawled());

			key.setPath("file2.ext");
			Path path2 = storage.getPath(key);
			assertEquals("file2.ext", path2.getPath());
			assertFalse(path2.isOnline());
			assertEquals(DateUtility.getDate(2020, 4, 2, 12, 0, 0), path2.getAdded());
			assertEquals(DateUtility.getDate(2020, 5, 2, 12, 0, 0), path2.getCrawled());

			storage.deleteAllPath(key);

			key.setPath("file1.ext");
			assertNull(storage.getPath(key));

			key.setPath("file2.ext");
			assertNull(storage.getPath(key));
		}
	}

	@Test
	public void getAllPath() throws SQLException {
		try (Storage storage = new Storage(connection);) {
			Key key = new Key("USK@something1/site/1/");

			storage.addFreesite(key, DateUtility.getDate(2020, 4, 1, 0, 0, 0));

			key.setPath("file1.ext");
			storage.addPath(key, DateUtility.getDate(2020, 4, 1, 12, 0, 0));
			storage.updatePath(key, true, DateUtility.getDate(2020, 5, 1, 12, 0, 0));

			key.setPath("file2.ext");
			storage.addPath(key, DateUtility.getDate(2020, 4, 2, 12, 0, 0));
			storage.updatePath(key, false, DateUtility.getDate(2020, 5, 2, 12, 0, 0));

			Integer id = storage.getFreesiteID(key);

			assertNotNull(id);

			ArrayList<Path> pathList = storage.getAllPath(key);
			assertEquals(2, pathList.size());

			Path path = pathList.get(1);
			assertEquals("file1.ext", path.getPath());
			assertTrue(path.isOnline());
			assertEquals(DateUtility.getDate(2020, 4, 1, 12, 0, 0), path.getAdded());
			assertEquals(DateUtility.getDate(2020, 5, 1, 12, 0, 0), path.getCrawled());

			path = pathList.get(0);
			assertEquals("file2.ext", path.getPath());
			assertFalse(path.isOnline());
			assertEquals(DateUtility.getDate(2020, 4, 2, 12, 0, 0), path.getAdded());
			assertEquals(DateUtility.getDate(2020, 5, 2, 12, 0, 0), path.getCrawled());
		}
	}

	@Test
	public void getAllFreesite() throws SQLException {
		try (Storage storage = new Storage(connection);) {
			Key key1 = new Key("USK@something1/site/1/");
			storage.addFreesite(key1, DateUtility.getDate(2020, 4, 1, 0, 0, 0));
			storage.updateFreesite(key1, "author1", "title1", "k11,k12,k13 k14", "description1", "language1", true,
					true, true, true, true, DateUtility.getDate(2020, 4, 1, 12, 0, 0), null);

			Key key2 = new Key("USK@something2/site/2/");
			storage.addFreesite(key2, DateUtility.getDate(2020, 4, 2, 0, 0, 0));
			storage.updateFreesite(key2, "author2", "title2", "k21 k22", "description2", "language2", false, false,
					false, false, false, DateUtility.getDate(2020, 4, 2, 12, 0, 0), null);

			Key key3 = new Key("USK@something3/site/3/");
			storage.addFreesite(key3, DateUtility.getDate(2020, 4, 3, 0, 0, 0));
			storage.updateFreesite(key3, "author3", "title3", "k31, k32", "description3", "language3", null, null, null,
					null, null, DateUtility.getDate(2020, 4, 3, 12, 0, 0), null);

			// Add some extra stuff
			key2.setPath("index.htm");
			storage.addPath(key2, new Date());
			storage.updatePath(key2, true, new Date());
			key2.setPath("content.htm");
			storage.addPath(key2, new Date());
			storage.updatePath(key2, true, new Date());
			key2.setPath("offlline.htm");
			storage.addPath(key2, new Date());
			storage.updatePath(key2, false, new Date());
			key2.setPath("not-spidered.htm");
			storage.addPath(key2, new Date());
			storage.addNetwork(key2, key1);
			storage.addNetwork(key2, key3);
			storage.addNetwork(key3, key2);

			ArrayList<Freesite> freesiteList = storage.getAllFreesite(true);
			assertEquals(3, freesiteList.size());

			Freesite freesite = freesiteList.get(2);
			assertNotNull(freesite.getID());
			assertEquals("USK@something1/site/", freesite.getKeyObj().getKey());
			assertEquals(1, freesite.getKeyObj().getEdition());
			assertEquals("author1", freesite.getAuthor());
			assertEquals("title1", freesite.getTitle());
			assertEquals("k11,k12,k13 k14", freesite.getKeywordsRaw());
			assertEquals("k11, k12, k13 k14", freesite.getKeywords());
			assertEquals("description1", freesite.getDescription());
			assertEquals("language1", freesite.getLanguage());
			assertTrue(freesite.isFMS());
			assertTrue(freesite.hasActiveLink());
			assertTrue(freesite.isOnline());
			assertTrue(freesite.isObsolete());
			assertTrue(freesite.ignoreResetOffline());
			assertFalse(freesite.crawlOnlyIndex());
			assertEquals(DateUtility.getDate(2020, 4, 1, 0, 0, 0), freesite.getAdded());
			assertEquals(DateUtility.getDate(2020, 4, 1, 12, 0, 0), freesite.getCrawled());
			assertNull(freesite.getComment());
			assertEquals(storage.getAllPath(freesite.getKeyObj()).size(), freesite.getPathList().size());
			assertEquals(storage.getInNetwork(freesite.getKeyObj()).size(), freesite.getInNetwork().size());
			assertEquals(storage.getOutNetwork(freesite.getKeyObj()).size(), freesite.getOutNetwork().size());
			assertEquals(0, freesite.getPathList().size());
			assertEquals(0, freesite.getPathOnlineSize());
			assertEquals(0, freesite.getPathOnlinePercent(), 0.001);

			freesite = freesiteList.get(1);
			assertNotNull(freesite.getID());
			assertEquals("USK@something2/site/", freesite.getKeyObj().getKey());
			assertEquals(2, freesite.getKeyObj().getEdition());
			assertEquals("author2", freesite.getAuthor());
			assertEquals("title2", freesite.getTitle());
			assertEquals("k21 k22", freesite.getKeywordsRaw());
			assertEquals("k21, k22", freesite.getKeywords());
			assertEquals("description2", freesite.getDescription());
			assertEquals("language2", freesite.getLanguage());
			assertFalse(freesite.isFMS());
			assertFalse(freesite.hasActiveLink());
			assertFalse(freesite.isOnline());
			assertFalse(freesite.isObsolete());
			assertFalse(freesite.ignoreResetOffline());
			assertFalse(freesite.crawlOnlyIndex());
			assertEquals(DateUtility.getDate(2020, 4, 2, 0, 0, 0), freesite.getAdded());
			assertEquals(DateUtility.getDate(2020, 4, 2, 12, 0, 0), freesite.getCrawled());
			assertNull(freesite.getComment());
			assertEquals(storage.getAllPath(freesite.getKeyObj()).size(), freesite.getPathList().size());
			assertEquals(storage.getInNetwork(freesite.getKeyObj()).size(), freesite.getInNetwork().size());
			assertEquals(storage.getOutNetwork(freesite.getKeyObj()).size(), freesite.getOutNetwork().size());
			assertEquals(4, freesite.getPathList().size());
			assertEquals(2, freesite.getPathOnlineSize());
			assertEquals(66.666, freesite.getPathOnlinePercent(), 0.001);

			freesite = freesiteList.get(0);
			assertNotNull(freesite.getID());
			assertEquals("USK@something3/site/", freesite.getKeyObj().getKey());
			assertEquals(3, freesite.getKeyObj().getEdition());
			assertEquals("author3", freesite.getAuthor());
			assertEquals("title3", freesite.getTitle());
			assertEquals("k31, k32", freesite.getKeywordsRaw());
			assertEquals("k31, k32", freesite.getKeywords());
			assertEquals("description3", freesite.getDescription());
			assertEquals("language3", freesite.getLanguage());
			assertNull(freesite.isFMS());
			assertNull(freesite.hasActiveLink());
			assertNull(freesite.isOnline());
			assertNull(freesite.isObsolete());
			assertNull(freesite.ignoreResetOffline());
			assertFalse(freesite.crawlOnlyIndex());
			assertEquals(DateUtility.getDate(2020, 4, 3, 0, 0, 0), freesite.getAdded());
			assertEquals(DateUtility.getDate(2020, 4, 3, 12, 0, 0), freesite.getCrawled());
			assertNull(freesite.getComment());
			assertEquals(storage.getAllPath(freesite.getKeyObj()).size(), freesite.getPathList().size());
			assertEquals(storage.getInNetwork(freesite.getKeyObj()).size(), freesite.getInNetwork().size());
			assertEquals(storage.getOutNetwork(freesite.getKeyObj()).size(), freesite.getOutNetwork().size());
			assertEquals(0, freesite.getPathList().size());
			assertEquals(0, freesite.getPathOnlineSize());
			assertEquals(0, freesite.getPathOnlinePercent(), 0.001);

			// Same tests as above, but without PathList, InNetwork, OutNetwork
			///////////////////////////////////////////////////////////////////

			freesiteList = storage.getAllFreesite(false);
			assertEquals(3, freesiteList.size());

			freesite = freesiteList.get(2);
			assertNotNull(freesite.getID());
			assertEquals("USK@something1/site/", freesite.getKeyObj().getKey());
			assertEquals(1, freesite.getKeyObj().getEdition());
			assertEquals("author1", freesite.getAuthor());
			assertEquals("title1", freesite.getTitle());
			assertEquals("k11,k12,k13 k14", freesite.getKeywordsRaw());
			assertEquals("k11, k12, k13 k14", freesite.getKeywords());
			assertEquals("description1", freesite.getDescription());
			assertEquals("language1", freesite.getLanguage());
			assertTrue(freesite.isFMS());
			assertTrue(freesite.hasActiveLink());
			assertTrue(freesite.isOnline());
			assertTrue(freesite.isObsolete());
			assertTrue(freesite.ignoreResetOffline());
			assertFalse(freesite.crawlOnlyIndex());
			assertEquals(DateUtility.getDate(2020, 4, 1, 0, 0, 0), freesite.getAdded());
			assertEquals(DateUtility.getDate(2020, 4, 1, 12, 0, 0), freesite.getCrawled());
			assertNull(freesite.getComment());
			assertNull(freesite.getPathList());
			assertNull(freesite.getInNetwork());
			assertNull(freesite.getOutNetwork());
			assertEquals(0, freesite.getPathOnlineSize());
			assertEquals(0, freesite.getPathOnlinePercent(), 0.001);

			freesite = freesiteList.get(1);
			assertNotNull(freesite.getID());
			assertEquals("USK@something2/site/", freesite.getKeyObj().getKey());
			assertEquals(2, freesite.getKeyObj().getEdition());
			assertEquals("author2", freesite.getAuthor());
			assertEquals("title2", freesite.getTitle());
			assertEquals("k21 k22", freesite.getKeywordsRaw());
			assertEquals("k21, k22", freesite.getKeywords());
			assertEquals("description2", freesite.getDescription());
			assertEquals("language2", freesite.getLanguage());
			assertFalse(freesite.isFMS());
			assertFalse(freesite.hasActiveLink());
			assertFalse(freesite.isOnline());
			assertFalse(freesite.isObsolete());
			assertFalse(freesite.ignoreResetOffline());
			assertFalse(freesite.crawlOnlyIndex());
			assertEquals(DateUtility.getDate(2020, 4, 2, 0, 0, 0), freesite.getAdded());
			assertEquals(DateUtility.getDate(2020, 4, 2, 12, 0, 0), freesite.getCrawled());
			assertNull(freesite.getComment());
			assertNull(freesite.getPathList());
			assertNull(freesite.getInNetwork());
			assertNull(freesite.getOutNetwork());
			assertEquals(0, freesite.getPathOnlineSize());
			assertEquals(0, freesite.getPathOnlinePercent(), 0.001);

			freesite = freesiteList.get(0);
			assertNotNull(freesite.getID());
			assertEquals("USK@something3/site/", freesite.getKeyObj().getKey());
			assertEquals(3, freesite.getKeyObj().getEdition());
			assertEquals("author3", freesite.getAuthor());
			assertEquals("title3", freesite.getTitle());
			assertEquals("k31, k32", freesite.getKeywordsRaw());
			assertEquals("k31, k32", freesite.getKeywords());
			assertEquals("description3", freesite.getDescription());
			assertEquals("language3", freesite.getLanguage());
			assertNull(freesite.isFMS());
			assertNull(freesite.hasActiveLink());
			assertNull(freesite.isOnline());
			assertNull(freesite.isObsolete());
			assertNull(freesite.ignoreResetOffline());
			assertFalse(freesite.crawlOnlyIndex());
			assertEquals(DateUtility.getDate(2020, 4, 3, 0, 0, 0), freesite.getAdded());
			assertEquals(DateUtility.getDate(2020, 4, 3, 12, 0, 0), freesite.getCrawled());
			assertNull(freesite.getComment());
			assertNull(freesite.getPathList());
			assertNull(freesite.getInNetwork());
			assertNull(freesite.getOutNetwork());
			assertEquals(0, freesite.getPathOnlineSize());
			assertEquals(0, freesite.getPathOnlinePercent(), 0.001);
		}
	}

	@Test
	public void addNetwork() throws SQLException {
		try (Storage storage = new Storage(connection);) {
			Key key = new Key("USK@something1/site/1/");
			Key targetKey1 = new Key("USK@something2/site/2/");
			Key targetKey2 = new Key("USK@something3/site/3/");

			storage.addFreesite(key, new Date());
			storage.addFreesite(targetKey1, new Date());
			storage.addFreesite(targetKey2, new Date());

			ArrayList<Integer> inNetwork = storage.getInNetwork(key);
			ArrayList<Integer> outNetwork = storage.getOutNetwork(key);
			assertEquals(0, inNetwork.size());
			assertEquals(0, outNetwork.size());

			storage.addNetwork(key, targetKey1);
			storage.addNetwork(key, targetKey2);

			inNetwork = storage.getInNetwork(key);
			outNetwork = storage.getOutNetwork(key);
			assertEquals(0, inNetwork.size());
			assertEquals(2, outNetwork.size());
			assertEquals(storage.getFreesiteID(targetKey1), outNetwork.get(0));
			assertEquals(storage.getFreesiteID(targetKey2), outNetwork.get(1));

			storage.addNetwork(targetKey1, key);

			inNetwork = storage.getInNetwork(key);
			outNetwork = storage.getOutNetwork(key);
			assertEquals(1, inNetwork.size());
			assertEquals(2, outNetwork.size());
			assertEquals(storage.getFreesiteID(targetKey1), inNetwork.get(0));
			assertEquals(storage.getFreesiteID(targetKey1), outNetwork.get(0));
			assertEquals(storage.getFreesiteID(targetKey2), outNetwork.get(1));

			storage.deleteAllNetwork(key);

			inNetwork = storage.getInNetwork(key);
			outNetwork = storage.getOutNetwork(key);
			assertEquals(1, inNetwork.size());
			assertEquals(0, outNetwork.size());

			storage.deleteAllNetwork(targetKey1);

			inNetwork = storage.getInNetwork(key);
			outNetwork = storage.getOutNetwork(key);
			assertEquals(0, inNetwork.size());
			assertEquals(0, outNetwork.size());
		}
	}
}
