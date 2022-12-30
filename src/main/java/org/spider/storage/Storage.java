/*
  Copyright 2020 - 2022 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package org.spider.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spider.Key;
import org.spider.Settings;

public class Storage implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(Storage.class);

	private static final String TMP_TABLE_EXT = "-tmp";

	private static final LinkedHashMap<String, String> TABLES_V1;
	private static final LinkedHashMap<String, String> VIEWS_V1;

	private static final LinkedHashMap<String, String> TABLES_V2;
	private static final LinkedHashMap<String, String> VIEWS_V2;

	private static final String SET_SONE_V2;

	private static final String ADD_CATEGORY_V3;
	private static final String SET_CATEGORY_V3;
	private static final String CREATE_INVALID_EDITION_V3;

	private static final LinkedHashMap<String, String> VIEWS_V4;

	public static final LinkedHashMap<String, String> TABLES;
	public static final LinkedHashMap<String, String> VIEWS;

	private static final LinkedHashMap<String, String> TABLE_COPY_V1_TO_V2;

	private static final String SELECT_DATABASE_VERSION_SQL = "SELECT `Version` FROM `DatabaseVersion` WHERE `ID` = 1";
	private static final String SET_DATABASE_VERSION_SQL = "INSERT OR REPLACE INTO `DatabaseVersion` (`ID`, `Version`) VALUES (1, ?)";

	private static final String INSERT_FREESITE_SQL = "INSERT INTO `Freesite` (`Key`, `Edition`, `EditionHint`, `Added`, `IgnoreResetOffline`, `CrawlOnlyIndex`, `Category`) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_FREESITE_SQL = "UPDATE `Freesite` SET `Author` = ?, `Title` = ?, `Keywords` = ?, `Description` = ?, `Language` = ?, `FMS` = ?, `Sone` = ?, `ActiveLink` = ?, `Online` = ?, `Obsolete` = ?, `IgnoreResetOffline` = ?, `Highlight` = ?, `Crawled` = ?, `Comment` = ?, `Category` = ? WHERE `Key` = ?";
	private static final String UPDATE_FREESITE_EDITION_SQL = "UPDATE `Freesite` SET `Edition` = ?, `EditionHint` = ?, `Crawled` = ? WHERE `Key` = ?";
	private static final String GET_FREESITE_ID_SQL = "SELECT `ID` FROM `Freesite` WHERE `Key` = ?";
	private static final String GET_FREESITE_KEY_SQL = "SELECT `Key`, `Edition`, `EditionHint` FROM `Freesite` WHERE `Key` = ?";

	private static final String FREESITE_FIELD_LIST = "`ID`, `Key`, `Edition`, `EditionHint`, `Author`, `Title`, `Keywords`, `Description`, `Language`, `FMS`, `Sone`, `ActiveLink`, `Online`, `Obsolete`, `IgnoreResetOffline`, `CrawlOnlyIndex`, `Highlight`, `Added`, `Crawled`, `Comment`, `Category`";
	private static final String GET_FREESITE_SQL = "SELECT " + FREESITE_FIELD_LIST + " FROM `Freesite` WHERE `Key` = ?";
	private static final String FIND_FREESITE_SQL = "SELECT " + FREESITE_FIELD_LIST
			+ " FROM `Freesite` WHERE `Key` LIKE ?";
	private static final String GET_ALL_FREESITE_SQL = "SELECT " + FREESITE_FIELD_LIST
			+ " FROM `Freesite` ORDER BY `Highlight` DESC, `Crawled` DESC ";

	private static final String RESET_HIGHLIGHT_SQL = "UPDATE `Freesite` SET `Highlight` = ? WHERE `Key` = ?";

	private static final String INSERT_PATH_SQL = "INSERT INTO `Path` (`FreesiteID`, `Path`, `Added`) VALUES (?, ?, ?)";
	private static final String UPDATE_PATH_SQL = "UPDATE `Path` SET `Online` = ?, `Crawled` = ? WHERE `FreesiteID` = ? AND `Path` = ?";
	private static final String DELETE_ALL_PATH_SQL = "DELETE FROM `Path` WHERE `FreesiteID` = ?";
	private static final String GET_PATH_ID_SQL = "SELECT `ID` FROM `Path` WHERE `FreesiteID` = ? AND `Path` = ?";
	private static final String GET_PATH_SQL = "SELECT `Path`, `Online`, `Added`, `Crawled` FROM `Path` WHERE `FreesiteID` = ? AND `Path` = ?";
	private static final String GET_ALL_PATH_SQL = "SELECT `Path`, `Online`, `Added`, `Crawled` FROM `Path` WHERE `FreesiteID` = ? ORDER BY `Crawled` DESC";

	private static final String INSERT_NETWORK_SQL = "INSERT INTO `Network` (`FreesiteID`, `TargetFreesiteID`) VALUES (?, ?)";
	private static final String DELETE_ALL_NETWORK_SQL = "DELETE FROM `Network` WHERE `FreesiteID` = ?";
	private static final String GET_IN_NETWORK_SQL = "SELECT `FreesiteID` FROM `Network` WHERE `TargetFreesiteID` = ? ORDER BY `FreesiteID` ASC";
	private static final String GET_OUT_NETWORK_SQL = "SELECT `TargetFreesiteID` FROM `Network` WHERE `FreesiteID` = ? ORDER BY `TargetFreesiteID` ASC";

	private static final String INSERT_INVALID_EDITION_SQL = "INSERT INTO `InvalidEdition` (`FreesiteID`, `Edition`) VALUES (?, ?)";
	private static final String DELETE_ALL_INVALID_EDITION_SQL = "DELETE FROM `InvalidEdition` WHERE `FreesiteID` = ?";
	private static final String GET_INVALID_EDITION_SQL = "SELECT `Edition` FROM `InvalidEdition` WHERE `FreesiteID` = ? AND `Edition` = ?";

	private static final String GET_NEXT_URL_SQL = "SELECT `Key`, `Edition`, `EditionHint`, `Path` FROM `Freesite` F INNER JOIN `Path` P ON F.`ID` = P.`FreesiteID` WHERE P.`Crawled` IS NULL ORDER BY F.`Crawled` DESC, F.`Added` ASC";

	static {
		Settings settings = Settings.getInstance();

		TABLES_V1 = new LinkedHashMap<>();
		TABLES_V1.put("DatabaseVersion", "");
		TABLES_V1.put("Freesite", "");
		TABLES_V1.put("Path", "");
		TABLES_V1.put("Network", "");

		VIEWS_V1 = new LinkedHashMap<>();
		VIEWS_V1.put("NextURL", "");
		VIEWS_V1.put("UnknownFMS", "");

		TABLES_V2 = new LinkedHashMap<>();
		TABLES_V2.put("DatabaseVersion",
				"CREATE TABLE IF NOT EXISTS `DatabaseVersion` (`ID` INTEGER CONSTRAINT `PK_DatabaseVersion` PRIMARY KEY AUTOINCREMENT, `Version` INTEGER)");
		TABLES_V2.put("Freesite",
				"CREATE TABLE IF NOT EXISTS `Freesite` (`ID` INTEGER CONSTRAINT `PK_Freesite` PRIMARY KEY AUTOINCREMENT NOT NULL, `Key` VARCHAR(1024) CONSTRAINT `UQ_Freesite_Key` UNIQUE NOT NULL, `Edition` INTEGER, `EditionHint` INTEGER, `Author` VARCHAR(1024), `Title` VARCHAR(1024), `Keywords` VARCHAR(10240), `Description` VARCHAR(10240), `Language` VARCHAR(1024), `FMS` BOOLEAN, `Sone` BOOLEAN, `ActiveLink` BOOLEAN, `Online` BOOLEAN, `Obsolete` BOOLEAN, `IgnoreResetOffline` BOOLEAN, `CrawlOnlyIndex` BOOLEAN, `Highlight` BOOLEAN, `Added` DATETIME, `Crawled` DATETIME, `Comment` VARCHAR(1024))");
		TABLES_V2.put("Path",
				"CREATE TABLE IF NOT EXISTS `Path` (`ID` INTEGER CONSTRAINT `PK_Path` PRIMARY KEY AUTOINCREMENT NOT NULL, `FreesiteID` INTEGER NOT NULL, `Path` VARCHAR(1024), `Online` BOOLEAN, `Added` DATETIME, `Crawled` DATETIME, CONSTRAINT `UQ_Path_FreesiteID_Path` UNIQUE(`FreesiteID`, `Path`))");
		TABLES_V2.put("Network",
				"CREATE TABLE IF NOT EXISTS `Network` (`ID` INTEGER CONSTRAINT `PK_Network` PRIMARY KEY AUTOINCREMENT NOT NULL, `FreesiteID` INTEGER NOT NULL, `TargetFreesiteID` INTEGER NOT NULL, CONSTRAINT `UQ_Network_FreesiteID_TargetFreesiteID` UNIQUE(`FreesiteID`, `TargetFreesiteID`), CONSTRAINT `UQ_Network_TargetFreesiteID_FreesiteID` UNIQUE(`TargetFreesiteID`, `FreesiteID`))");

		VIEWS_V2 = new LinkedHashMap<>();
		VIEWS_V2.put("NextURL",
				"CREATE VIEW IF NOT EXISTS `NextURL` AS SELECT `Key`, `Edition`, `EditionHint`, `Path` FROM `Freesite` F INNER JOIN `Path` P ON F.`ID` = P.`FreesiteID` WHERE P.`Crawled` IS NULL ORDER BY F.`Crawled` DESC, F.`Added` ASC");
		VIEWS_V2.put("UnknownFMS",
				"CREATE VIEW IF NOT EXISTS `UnknownFMS` AS SELECT `ID`, `Key`, `Edition`, `Title` FROM `Freesite` WHERE `Key` LIKE '%/fms/%' AND `FMS` = 0 AND `Online` = 1");

		TABLE_COPY_V1_TO_V2 = new LinkedHashMap<>();
		TABLE_COPY_V1_TO_V2.put("Freesite",
				"INSERT INTO `Freesite` (`ID`, `Key`, `Edition`, `EditionHint`, `Author`, `Title`, `Keywords`, `Description`, `Language`, `FMS`, `Sone`, `ActiveLink`, `Online`, `Obsolete`, `IgnoreResetOffline`, `CrawlOnlyIndex`, `Highlight`, `Added`, `Crawled`, `Comment`) SELECT `ID`, `Key`, `Edition`, `EditionHint`, `Author`, `Title`, `Keywords`, `Description`, `Language`, `FMS`, 0, `ActiveLink`, `Online`, `Obsolete`, `IgnoreResetOffline`, `CrawlOnlyIndex`, 0, `Added`, `Crawled`, `Comment` FROM `Freesite"
						+ TMP_TABLE_EXT + "`");
		TABLE_COPY_V1_TO_V2.put("Path",
				"INSERT INTO `Path` (`ID`, `FreesiteID`, `Path`, `Online`, `Added`, `Crawled`) SELECT `ID`, `FreesiteID`, `Path`, `Online`, `Added`, `Crawled` FROM `Path"
						+ TMP_TABLE_EXT + "`");
		TABLE_COPY_V1_TO_V2.put("Network",
				"INSERT INTO `Network` (`ID`, `FreesiteID`, `TargetFreesiteID`) SELECT `ID`, `FreesiteID`, `TargetFreesiteID` FROM `Network"
						+ TMP_TABLE_EXT + "`");

		SET_SONE_V2 = "UPDATE `Path` SET `Crawled` = NULL WHERE `Path` = '' AND `FreesiteID` IN (SELECT `ID` FROM `Freesite` WHERE `Key` LIKE '%/Sone/%')";

		ADD_CATEGORY_V3 = "ALTER TABLE `Freesite` ADD `Category` VARCHAR(1024)";
		SET_CATEGORY_V3 = "UPDATE `Freesite` SET `Category` = ''";
		CREATE_INVALID_EDITION_V3 = "CREATE TABLE IF NOT EXISTS `InvalidEdition` (`ID` INTEGER CONSTRAINT `PK_InvalidEdition` PRIMARY KEY AUTOINCREMENT NOT NULL, `FreesiteID` INTEGER NOT NULL, `Edition` INTEGER NOT NULL, CONSTRAINT `UQ_InvalidEdition_FreesiteID_Edition` UNIQUE(`FreesiteID`, `Edition`))";

		VIEWS_V4 = new LinkedHashMap<>();
		VIEWS_V4.put("Categories",
				"CREATE VIEW IF NOT EXISTS `Categories` AS SELECT `Category`, COUNT(`ID`) AS 'Count' FROM `Freesite` WHERE `Online` = 1 OR `Category` != '' GROUP BY `Category`");
		VIEWS_V4.put("MissingCategoryOnline", String.format(
				"CREATE VIEW IF NOT EXISTS `MissingCategoryOnline` AS SELECT `ID`, 'http://%s:%d/' || `Key` || `Edition` || '/' AS 'Link', `Title`, `Category` FROM `Freesite` WHERE `Category` = '' AND `Online` = 1 ORDER BY `Crawled` DESC",
				settings.getString(Settings.FREENET_HOST), settings.getInteger(Settings.FREENET_PORT_FPROXY)));
		VIEWS_V4.put("MissingCategoryOffline", String.format(
				"CREATE VIEW IF NOT EXISTS `MissingCategoryOffline` AS SELECT `ID`, 'http://%s:%d/' || `Key` || `Edition` || '/' AS 'Link', `Title`, `Category` FROM `Freesite` WHERE `Category` = '' AND `Online` = 0 ORDER BY `Crawled` DESC",
				settings.getString(Settings.FREENET_HOST), settings.getInteger(Settings.FREENET_PORT_FPROXY)));

		TABLES = new LinkedHashMap<>();
		TABLES.put("DatabaseVersion", TABLES_V2.get("DatabaseVersion"));
		TABLES.put("Freesite",
				"CREATE TABLE IF NOT EXISTS `Freesite` (`ID` INTEGER CONSTRAINT `PK_Freesite` PRIMARY KEY AUTOINCREMENT NOT NULL, `Key` VARCHAR(1024) CONSTRAINT `UQ_Freesite_Key` UNIQUE NOT NULL, `Edition` INTEGER, `EditionHint` INTEGER, `Author` VARCHAR(1024), `Title` VARCHAR(1024), `Keywords` VARCHAR(10240), `Description` VARCHAR(10240), `Language` VARCHAR(1024), `FMS` BOOLEAN, `Sone` BOOLEAN, `ActiveLink` BOOLEAN, `Online` BOOLEAN, `Obsolete` BOOLEAN, `IgnoreResetOffline` BOOLEAN, `CrawlOnlyIndex` BOOLEAN, `Highlight` BOOLEAN, `Added` DATETIME, `Crawled` DATETIME, `Comment` VARCHAR(1024), `Category` VARCHAR(1024))");
		TABLES.put("Path", TABLES_V2.get("Path"));
		TABLES.put("Network", TABLES_V2.get("Network"));
		TABLES.put("InvalidEdition", CREATE_INVALID_EDITION_V3);

		VIEWS = new LinkedHashMap<>();
		VIEWS.put("NextURL", "CREATE VIEW IF NOT EXISTS `NextURL` AS " + GET_NEXT_URL_SQL);
		VIEWS.put("UnknownFMS",
				"CREATE VIEW IF NOT EXISTS `UnknownFMS` AS SELECT `ID`, `Key`, `Edition`, `Title` FROM `Freesite` WHERE `Key` LIKE '%/fms/%' AND `FMS` = 0 AND `Online` = 1");
		VIEWS.put("Categories", VIEWS_V4.get("Categories"));
		VIEWS.put("MissingCategoryOnline", VIEWS_V4.get("MissingCategoryOnline"));
		VIEWS.put("MissingCategoryOffline", VIEWS_V4.get("MissingCategoryOffline"));
	}

	private PreparedStatement getDatabaseVersion;
	private PreparedStatement setDatabaseVersion;
	private PreparedStatement insertFreesite;
	private PreparedStatement updateFreesite;
	private PreparedStatement updateFreesiteEdition;
	private PreparedStatement getFreesiteID;
	private PreparedStatement getFreesiteKey;
	private PreparedStatement getFreesite;
	private PreparedStatement findFreesite;
	private PreparedStatement getAllFreesite;
	private PreparedStatement resetHighlight;
	private PreparedStatement insertPath;
	private PreparedStatement updatePath;
	private PreparedStatement deleteAllPath;
	private PreparedStatement getPathID;
	private PreparedStatement getPath;
	private PreparedStatement getAllPath;
	private PreparedStatement insertNetwork;
	private PreparedStatement deleteAllNetwork;
	private PreparedStatement getInNetwork;
	private PreparedStatement getOutNetwork;
	private PreparedStatement insertInvalidEdition;
	private PreparedStatement deleteAllInvalidEdition;
	private PreparedStatement getInvalidEdition;
	private PreparedStatement getNextURL;

	private Connection connection;

	// Version
	// Spider = Database
	// 1.0 = 1
	// 1.1 = 2
	// 1.2 = 3
	// 1.3 = 4
	private static final int currentDatabaseVersion = 4;

	public Storage(Connection connection) throws SQLException {

		this.connection = connection;

		Database.execute(connection, TABLES.get("DatabaseVersion"));

		getDatabaseVersion = connection.prepareStatement(SELECT_DATABASE_VERSION_SQL);
		setDatabaseVersion = connection.prepareStatement(SET_DATABASE_VERSION_SQL);

		switch (getDatabaseVersion()) {
		case -1:
		case 0: // missing version
			log.info("Create database for version {}", currentDatabaseVersion);
			for (String query : TABLES.values()) {
				Database.execute(connection, query);
			}
			for (String query : VIEWS.values()) {
				Database.execute(connection, query);
			}
			setDatabaseVersion(currentDatabaseVersion);
			connection.commit();
			break;
		case 1:
		case 2:
		case 3:
			while (getDatabaseVersion() < currentDatabaseVersion) {
				Integer nextVersion = getDatabaseVersion() + 1;
				updateDatebase(nextVersion);
				setDatabaseVersion(nextVersion);
				connection.commit();
			}
			break;
		case currentDatabaseVersion: // nothing to do
			break;
		default:
			throw new SQLException("Unknown Database-Version!");
		}

		insertFreesite = connection.prepareStatement(INSERT_FREESITE_SQL);
		updateFreesite = connection.prepareStatement(UPDATE_FREESITE_SQL);
		updateFreesiteEdition = connection.prepareStatement(UPDATE_FREESITE_EDITION_SQL);
		getFreesiteID = connection.prepareStatement(GET_FREESITE_ID_SQL);
		getFreesiteKey = connection.prepareStatement(GET_FREESITE_KEY_SQL);
		getFreesite = connection.prepareStatement(GET_FREESITE_SQL);
		findFreesite = connection.prepareStatement(FIND_FREESITE_SQL);
		getAllFreesite = connection.prepareStatement(GET_ALL_FREESITE_SQL);
		resetHighlight = connection.prepareStatement(RESET_HIGHLIGHT_SQL);

		insertPath = connection.prepareStatement(INSERT_PATH_SQL);
		updatePath = connection.prepareStatement(UPDATE_PATH_SQL);
		deleteAllPath = connection.prepareStatement(DELETE_ALL_PATH_SQL);
		getPathID = connection.prepareStatement(GET_PATH_ID_SQL);
		getPath = connection.prepareStatement(GET_PATH_SQL);
		getAllPath = connection.prepareStatement(GET_ALL_PATH_SQL);

		insertNetwork = connection.prepareStatement(INSERT_NETWORK_SQL);
		deleteAllNetwork = connection.prepareStatement(DELETE_ALL_NETWORK_SQL);
		getInNetwork = connection.prepareStatement(GET_IN_NETWORK_SQL);
		getOutNetwork = connection.prepareStatement(GET_OUT_NETWORK_SQL);

		insertInvalidEdition = connection.prepareStatement(INSERT_INVALID_EDITION_SQL);
		deleteAllInvalidEdition = connection.prepareStatement(DELETE_ALL_INVALID_EDITION_SQL);
		getInvalidEdition = connection.prepareStatement(GET_INVALID_EDITION_SQL);

		getNextURL = connection.prepareStatement(GET_NEXT_URL_SQL);
	}

	private void renameTable(String oldName, String newName) throws SQLException {
		Database.execute(connection, String.format("ALTER TABLE `%s` RENAME TO `%s`", oldName, newName));
	}

	private void removeTable(String name) throws SQLException {
		Database.execute(connection, String.format("DROP TABLE IF EXISTS `%s`", name));
	}

	private void removeView(String name) throws SQLException {
		Database.execute(connection, String.format("DROP VIEW IF EXISTS `%s`", name));
	}

	private void updateDatebase(Integer version) throws SQLException {
		log.info("Update database to version {}", version);

		if (connection.isReadOnly()) {
			throw new SQLException("Read-only database. Use the default-task to update the database!");
		}

		if (version == 2) {
			// Recreate the tables to apply the names of the constraints.
			// Delete and create the views to keep the database consistent.

			for (String view : VIEWS_V1.keySet()) {
				removeView(view);
			}

			for (String table : TABLES_V1.keySet()) {
				log.info("Update table {}", table);
				renameTable(table, table + TMP_TABLE_EXT);
				Database.execute(connection, TABLES_V2.get(table));
				if (!table.equals("DatabaseVersion")) {
					Database.execute(connection, TABLE_COPY_V1_TO_V2.get(table));
				}
				removeTable(table + TMP_TABLE_EXT);
			}

			log.info("Update Sone-Flag");
			Database.execute(connection, SET_SONE_V2);

			for (String view : VIEWS_V2.values()) {
				Database.execute(connection, view);
			}
		} else if (version == 3) {
			log.info("Add column category");
			Database.execute(connection, ADD_CATEGORY_V3);
			Database.execute(connection, SET_CATEGORY_V3);

			log.info("Add table InvalidEdition");
			Database.execute(connection, CREATE_INVALID_EDITION_V3);
		} else if (version == 4) {
			for (String view : VIEWS_V4.values()) {
				Database.execute(connection, view);
			}
		}
	}

	private Integer getDatabaseVersion() throws SQLException {
		Integer result = -1;
		try (ResultSet resultSet = getDatabaseVersion.executeQuery()) {
			if (resultSet.next()) {
				result = Database.getInteger(resultSet, "Version");
			}
		}
		return result;
	}

	private void setDatabaseVersion(Integer version) throws SQLException {
		Database.setInteger(setDatabaseVersion, 1, version);
		setDatabaseVersion.executeUpdate();
	}

	public void addFreesite(Key key, Date added) throws SQLException {
		insertFreesite.setString(1, key.getKey());
		Database.setLong(insertFreesite, 2, key.getEdition());
		Database.setLong(insertFreesite, 3, key.getEditionHint());
		Database.setDate(insertFreesite, 4, added);
		Database.setBoolean(insertFreesite, 5, false); // IgnoreResetOffline
		Database.setBoolean(insertFreesite, 6, false); // CrawlOnlyIndex
		insertFreesite.setString(7, ""); // Category
		insertFreesite.executeUpdate();
	}

	public void updateFreesite(Key key, String author, String title, String keywords, String description,
			String language, Boolean FMS, Boolean sone, Boolean activeLink, Boolean online, Boolean obsolete,
			Boolean ignoreResetOffline, Boolean highlight, Date crawled, String comment, String category)
			throws SQLException {
		updateFreesite.setString(1, author);
		updateFreesite.setString(2, title);
		updateFreesite.setString(3, keywords);
		updateFreesite.setString(4, description);
		updateFreesite.setString(5, language);
		Database.setBoolean(updateFreesite, 6, FMS);
		Database.setBoolean(updateFreesite, 7, sone);
		Database.setBoolean(updateFreesite, 8, activeLink);
		Database.setBoolean(updateFreesite, 9, online);
		Database.setBoolean(updateFreesite, 10, obsolete);
		Database.setBoolean(updateFreesite, 11, ignoreResetOffline);
		Database.setBoolean(updateFreesite, 12, highlight);
		Database.setDate(updateFreesite, 13, crawled);
		updateFreesite.setString(14, comment);
		updateFreesite.setString(15, category);
		updateFreesite.setString(16, key.getKey());
		updateFreesite.executeUpdate();
	}

	public void updateFreesiteEdition(Key key, Date crawled) throws SQLException {
		Database.setLong(updateFreesiteEdition, 1, key.getEdition());
		Database.setLong(updateFreesiteEdition, 2, key.getEditionHint());
		Database.setDate(updateFreesiteEdition, 3, crawled);
		updateFreesiteEdition.setString(4, key.getKey());
		updateFreesiteEdition.executeUpdate();
	}

	public Integer getFreesiteID(Key key) throws SQLException {
		Integer result = null;
		getFreesiteID.setString(1, key.getKey());
		try (ResultSet resultSet = getFreesiteID.executeQuery()) {
			if (resultSet.next()) {
				result = Database.getInteger(resultSet, "ID");
			}
		}
		return result;
	}

	public Key getFreesiteKey(Key key) throws SQLException {
		Key result = null;
		getFreesiteKey.setString(1, key.getKey());
		try (ResultSet resultSet = getFreesiteKey.executeQuery()) {
			if (resultSet.next()) {
				String rawKey = resultSet.getString("Key");
				Long edition = Database.getLong(resultSet, "Edition");
				Long editionHint = Database.getLong(resultSet, "EditionHint");
				result = new Key(rawKey, edition, editionHint);
			}
		}
		return result;
	}

	private Freesite getFreesite(ResultSet resultSet) throws SQLException {
		Integer id = Database.getInteger(resultSet, "ID");
		String rawKey = resultSet.getString("Key");
		Long edition = Database.getLong(resultSet, "Edition");
		Long editionHint = Database.getLong(resultSet, "EditionHint");
		String author = resultSet.getString("Author");
		String title = resultSet.getString("Title");
		String keywords = resultSet.getString("Keywords");
		String description = resultSet.getString("Description");
		String language = resultSet.getString("Language");
		Boolean isFMS = Database.getBoolean(resultSet, "FMS");
		Boolean isSone = Database.getBoolean(resultSet, "Sone");
		Boolean hasActiveLink = Database.getBoolean(resultSet, "ActiveLink");
		Boolean isOnline = Database.getBoolean(resultSet, "Online");
		Boolean isObsolete = Database.getBoolean(resultSet, "Obsolete");
		Boolean ignoreResetOffline = Database.getBoolean(resultSet, "IgnoreResetOffline");
		Boolean crawlOnlyIndex = Database.getBoolean(resultSet, "CrawlOnlyIndex");
		Boolean highlight = Database.getBoolean(resultSet, "Highlight");
		Date added = Database.getDate(resultSet, "Added");
		Date crawled = Database.getDate(resultSet, "Crawled");
		String comment = resultSet.getString("Comment");
		String category = resultSet.getString("Category");
		Key resultKey = new Key(rawKey, edition, editionHint);
		return new Freesite(id, resultKey, author, title, keywords, description, language, isFMS, isSone, hasActiveLink,
				isOnline, isObsolete, ignoreResetOffline, crawlOnlyIndex, highlight, added, crawled, comment, category);
	}

	public Freesite getFreesite(Key key, Boolean nullOnMissing) throws SQLException {
		Freesite result = null;
		getFreesite.setString(1, key.getKey());
		try (ResultSet resultSet = getFreesite.executeQuery()) {
			if (resultSet.next()) {
				result = getFreesite(resultSet);
			}
		}
		if (result == null && !nullOnMissing) {
			result = new Freesite(0, key, null, key.getSitePath(), null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null);
		}
		return result;
	}

	public Freesite getFreesite(Key key) throws SQLException {
		return getFreesite(key, true);
	}

	public ArrayList<Freesite> findFreesite(String searchKey) throws SQLException {
		ArrayList<Freesite> result = new ArrayList<>();
		findFreesite.setString(1, "%" + searchKey + "%");
		try (ResultSet resultSet = findFreesite.executeQuery()) {
			while (resultSet.next()) {
				Freesite freesite = getFreesite(resultSet);
				result.add(freesite);
			}
		}
		return result;
	}

	public ArrayList<Freesite> getAllFreesite(Boolean getFull) throws SQLException {
		ArrayList<Freesite> result = new ArrayList<>();
		try (ResultSet resultSet = getAllFreesite.executeQuery()) {
			while (resultSet.next()) {
				Freesite freesite = getFreesite(resultSet);
				Key key = freesite.getKeyObj();
				if (getFull) {
					freesite.setPathList(getAllPath(key));
					freesite.setInNetwork(getInNetwork(key));
					freesite.setOutNetwork(getOutNetwork(key));
				}
				result.add(freesite);
			}
		}
		return result;
	}

	public void addPath(Key key, Date added) throws SQLException {
		Integer id = getFreesiteID(key);
		Database.setInteger(insertPath, 1, id);
		insertPath.setString(2, key.getPath());
		Database.setDate(insertPath, 3, added);
		insertPath.executeUpdate();
	}

	public void updatePath(Key key, Boolean online, Date crawled) throws SQLException {
		Integer id = getFreesiteID(key);
		Database.setBoolean(updatePath, 1, online);
		Database.setDate(updatePath, 2, crawled);
		Database.setInteger(updatePath, 3, id);
		updatePath.setString(4, key.getPath());
		updatePath.executeUpdate();
	}

	public void deleteAllPath(Key key) throws SQLException {
		Integer id = getFreesiteID(key);
		Database.setInteger(deleteAllPath, 1, id);
		deleteAllPath.executeUpdate();
	}

	public Integer getPathID(Key key) throws SQLException {
		Integer result = null;
		Integer id = getFreesiteID(key);
		Database.setInteger(getPathID, 1, id);
		getPathID.setString(2, key.getPath());
		try (ResultSet resultSet = getPathID.executeQuery()) {
			if (resultSet.next()) {
				result = Database.getInteger(resultSet, "ID");
			}
		}
		return result;
	}

	private Path getPath(ResultSet resultSet) throws SQLException {
		String path = resultSet.getString("Path");
		Boolean online = Database.getBoolean(resultSet, "Online");
		Date added = Database.getDate(resultSet, "Added");
		Date crawled = Database.getDate(resultSet, "Crawled");
		return new Path(path, online, added, crawled);
	}

	public Path getPath(Key key) throws SQLException {
		Path result = null;
		Integer id = getFreesiteID(key);
		Database.setInteger(getPath, 1, id);
		getPath.setString(2, key.getPath());
		try (ResultSet resultSet = getPath.executeQuery()) {
			if (resultSet.next()) {
				result = getPath(resultSet);
			}
		}
		return result;
	}

	public ArrayList<Path> getAllPath(Key key) throws SQLException {
		ArrayList<Path> result = new ArrayList<>();
		Integer id = getFreesiteID(key);
		Database.setInteger(getAllPath, 1, id);
		try (ResultSet resultSet = getAllPath.executeQuery()) {
			while (resultSet.next()) {
				result.add(getPath(resultSet));
			}
		}
		return result;
	}

	public void addNetwork(Key key, Key targetKey) throws SQLException {
		Integer id = getFreesiteID(key);
		Integer targetID = getFreesiteID(targetKey);
		Database.setInteger(insertNetwork, 1, id);
		Database.setInteger(insertNetwork, 2, targetID);
		insertNetwork.executeUpdate();
	}

	public void deleteAllNetwork(Key key) throws SQLException {
		Integer id = getFreesiteID(key);
		Database.setInteger(deleteAllNetwork, 1, id);
		deleteAllNetwork.executeUpdate();
	}

	public ArrayList<Integer> getInNetwork(Key key) throws SQLException {
		ArrayList<Integer> result = new ArrayList<>();
		Integer id = getFreesiteID(key);
		Database.setInteger(getInNetwork, 1, id);
		try (ResultSet resultSet = getInNetwork.executeQuery()) {
			while (resultSet.next()) {
				Integer targetID = Database.getInteger(resultSet, "FreesiteID");
				result.add(targetID);
			}
		}
		return result;
	}

	public ArrayList<Integer> getOutNetwork(Key key) throws SQLException {
		ArrayList<Integer> result = new ArrayList<>();
		Integer id = getFreesiteID(key);
		Database.setInteger(getOutNetwork, 1, id);
		try (ResultSet resultSet = getOutNetwork.executeQuery()) {
			while (resultSet.next()) {
				Integer targetID = Database.getInteger(resultSet, "TargetFreesiteID");
				result.add(targetID);
			}
		}
		return result;
	}

	public void addInvalidEdition(Key key) throws SQLException {
		Integer id = getFreesiteID(key);
		Database.setInteger(insertInvalidEdition, 1, id);
		Database.setLong(insertInvalidEdition, 2, Math.abs(key.getEdition()));
		insertInvalidEdition.executeUpdate();
	}

	public void deleteAllInvalidEdition(Key key) throws SQLException {
		Integer id = getFreesiteID(key);
		Database.setInteger(deleteAllInvalidEdition, 1, id);
		deleteAllInvalidEdition.executeUpdate();
	}

	public Boolean isEditionInvalid(Key key) throws SQLException {
		Integer id = getFreesiteID(key);
		Database.setInteger(getInvalidEdition, 1, id);
		Database.setLong(getInvalidEdition, 2, Math.abs(key.getEdition()));
		try (ResultSet resultSet = getInvalidEdition.executeQuery()) {
			return resultSet.next();
		}
	}

	public Key getNextURL() throws SQLException {
		Key result = null;
		try (ResultSet resultSet = getNextURL.executeQuery()) {
			if (resultSet.next()) {
				String key = resultSet.getString("Key");
				Long edition = Database.getLong(resultSet, "Edition");
				Long editionHint = Database.getLong(resultSet, "EditionHint");
				String path = resultSet.getString("Path");

				result = new Key(key, edition, editionHint, path);
			}
		}
		return result;
	}

	public void resetHighlight(Key key) throws SQLException {
		Database.setBoolean(resetHighlight, 1, false);
		resetHighlight.setString(2, key.getKey());
		resetHighlight.executeUpdate();
	}

	@Override
	public void close() throws SQLException {
		getDatabaseVersion.close();
		setDatabaseVersion.close();
		insertFreesite.close();
		updateFreesite.close();
		updateFreesiteEdition.close();
		getFreesiteID.close();
		getFreesiteKey.close();
		getFreesite.close();
		findFreesite.close();
		getAllFreesite.close();
		resetHighlight.close();
		insertPath.close();
		updatePath.close();
		deleteAllPath.close();
		getPathID.close();
		getPath.close();
		getAllPath.close();
		insertNetwork.close();
		deleteAllNetwork.close();
		getInNetwork.close();
		getOutNetwork.close();
		insertInvalidEdition.close();
		deleteAllInvalidEdition.close();
		getInvalidEdition.close();
		getNextURL.close();
	}
}
