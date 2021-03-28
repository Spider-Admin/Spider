/*
  Copyright 2020 - 2021 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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

import org.spider.Key;

public class Storage implements AutoCloseable {

	private static final String CREATE_DATABASE_VERSION_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `DatabaseVersion` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT, `Version` INTEGER)";
	private static final String SELECT_DATABASE_VERSION_SQL = "SELECT `Version` FROM `DatabaseVersion` WHERE `ID` = 1";
	private static final String SET_DATABASE_VERSION_SQL = "INSERT OR REPLACE INTO `DatabaseVersion` (`ID`, `Version`) VALUES (1, ?)";

	private static final String CREATE_FREESITE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `Freesite` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `Key` VARCHAR(1024) UNIQUE NOT NULL, `Edition` INTEGER, `EditionHint` INTEGER, `Author` VARCHAR(1024), `Title` VARCHAR(1024), `Keywords` VARCHAR(10240), `Description` VARCHAR(10240), `Language` VARCHAR(1024), `FMS` BOOLEAN, `ActiveLink` BOOLEAN, `Online` BOOLEAN, `Obsolete` BOOLEAN, `IgnoreResetOffline` BOOLEAN, `CrawlOnlyIndex` BOOLEAN, `Highlight` BOOLEAN, `Added` DATETIME, `Crawled` DATETIME, `Comment` VARCHAR(1024))";
	private static final String INSERT_FREESITE_SQL = "INSERT INTO `Freesite` (`Key`, `Edition`, `EditionHint`, `Added`, `IgnoreResetOffline`, `CrawlOnlyIndex`) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_FREESITE_SQL = "UPDATE `Freesite` SET `Author` = ?, `Title` = ?, `Keywords` = ?, `Description` = ?, `Language` = ?, `FMS` = ?, `ActiveLink` = ?, `Online` = ?, `Obsolete` = ?, `IgnoreResetOffline` = ?, `Highlight` = ?, `Crawled` = ?, `Comment` = ? WHERE `Key` = ?";
	private static final String UPDATE_FREESITE_EDITION_SQL = "UPDATE `Freesite` SET `Edition` = ?, `EditionHint` = ?, `Crawled` = ? WHERE `Key` = ?";
	private static final String GET_FREESITE_ID_SQL = "SELECT `ID` FROM `Freesite` WHERE `Key` = ?";
	private static final String GET_FREESITE_KEY_SQL = "SELECT `Key`, `Edition`, `EditionHint` FROM `Freesite` WHERE `Key` = ?";

	private static final String FREESITE_FIELD_LIST = "`ID`, `Key`, `Edition`, `EditionHint`, `Author`, `Title`, `Keywords`, `Description`, `Language`, `FMS`, `ActiveLink`, `Online`, `Obsolete`, `IgnoreResetOffline`, `CrawlOnlyIndex`, `Highlight`, `Added`, `Crawled`, `Comment`";
	private static final String GET_FREESITE_SQL = "SELECT " + FREESITE_FIELD_LIST + " FROM `Freesite` WHERE `Key` = ?";
	private static final String FIND_FREESITE_SQL = "SELECT " + FREESITE_FIELD_LIST
			+ " FROM `Freesite` WHERE `Key` LIKE ?";
	private static final String GET_ALL_FREESITE_SQL = "SELECT " + FREESITE_FIELD_LIST
			+ " FROM `Freesite` ORDER BY `Crawled` DESC ";

	private static final String RESET_HIGHLIGHT_SQL = "UPDATE `Freesite` SET `Highlight` = ? WHERE `Key` = ?";

	private static final String CREATE_PATH_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `Path` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `FreesiteID` INTEGER NOT NULL, `Path` VARCHAR(1024), `Online` BOOLEAN, `Added` DATETIME, `Crawled` DATETIME, UNIQUE(`FreesiteID`, `Path`))";
	private static final String INSERT_PATH_SQL = "INSERT INTO `Path` (`FreesiteID`, `Path`, `Added`) VALUES (?, ?, ?)";
	private static final String UPDATE_PATH_SQL = "UPDATE `Path` SET `Online` = ?, `Crawled` = ? WHERE `FreesiteID` = ? AND `Path` = ?";
	private static final String DELETE_ALL_PATH_SQL = "DELETE FROM `Path` WHERE `FreesiteID` = ?";
	private static final String GET_PATH_ID_SQL = "SELECT `ID` FROM `Path` WHERE `FreesiteID` = ? AND `Path` = ?";
	private static final String GET_PATH_SQL = "SELECT `Path`, `Online`, `Added`, `Crawled` FROM `Path` WHERE `FreesiteID` = ? AND `Path` = ?";
	private static final String GET_ALL_PATH_SQL = "SELECT `Path`, `Online`, `Added`, `Crawled` FROM `Path` WHERE `FreesiteID` = ? ORDER BY `Crawled` DESC";

	private static final String CREATE_NETWORK_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `Network` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `FreesiteID` INTEGER NOT NULL, `TargetFreesiteID` INTEGER NOT NULL, UNIQUE(`FreesiteID`, `TargetFreesiteID`))";
	private static final String INSERT_NETWORK_SQL = "INSERT INTO `Network` (`FreesiteID`, `TargetFreesiteID`) VALUES (?, ?)";
	private static final String DELETE_ALL_NETWORK_SQL = "DELETE FROM `Network` WHERE `FreesiteID` = ?";
	private static final String GET_IN_NETWORK_SQL = "SELECT `FreesiteID` FROM `Network` WHERE `TargetFreesiteID` = ? ORDER BY `FreesiteID` ASC";
	private static final String GET_OUT_NETWORK_SQL = "SELECT `TargetFreesiteID` FROM `Network` WHERE `FreesiteID` = ? ORDER BY `TargetFreesiteID` ASC";

	private static final String GET_NEXT_URL_SQL = "SELECT `Key`, `Edition`, `EditionHint`, `Path` FROM `Freesite` F INNER JOIN `Path` P ON F.`ID` = P.`FreesiteID` WHERE P.`Crawled` IS NULL ORDER BY F.`Crawled` DESC, F.`Added` ASC";
	private static final String GET_NEXT_URL_VIEW_SQL = "CREATE VIEW IF NOT EXISTS `NextURL` AS " + GET_NEXT_URL_SQL;

	private static final String GET_UNKNOWN_FMS_VIEW_SQL = "CREATE VIEW IF NOT EXISTS `UnknownFMS` AS SELECT `ID`, `Key`, `Edition`, `Title` FROM `Freesite` WHERE `Key` LIKE '%/fms/%' AND `FMS` = 0 AND `Online` = 1";

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
	private PreparedStatement getNextURL;

	private static final int currentDatabaseVersion = 1;

	public Storage(Connection connection) throws SQLException {
		Database.execute(connection, CREATE_DATABASE_VERSION_TABLE_SQL);
		Database.execute(connection, CREATE_FREESITE_TABLE_SQL);
		Database.execute(connection, CREATE_PATH_TABLE_SQL);
		Database.execute(connection, CREATE_NETWORK_TABLE_SQL);
		Database.execute(connection, GET_NEXT_URL_VIEW_SQL);
		Database.execute(connection, GET_UNKNOWN_FMS_VIEW_SQL);

		getDatabaseVersion = connection.prepareStatement(SELECT_DATABASE_VERSION_SQL);
		setDatabaseVersion = connection.prepareStatement(SET_DATABASE_VERSION_SQL);

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

		getNextURL = connection.prepareStatement(GET_NEXT_URL_SQL);

		switch (getDatabaseVersion()) {
		case -1:
		case 0: // missing version
			setDatabaseVersion(currentDatabaseVersion);
			break;
		case currentDatabaseVersion: // nothing to do
			break;
		default:
			throw new SQLException("Unknown Database-Version!");
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
		insertFreesite.executeUpdate();
	}

	public void updateFreesite(Key key, String author, String title, String keywords, String description,
			String language, Boolean FMS, Boolean activeLink, Boolean online, Boolean obsolete,
			Boolean ignoreResetOffline, Boolean highlight, Date crawled, String comment) throws SQLException {
		updateFreesite.setString(1, author);
		updateFreesite.setString(2, title);
		updateFreesite.setString(3, keywords);
		updateFreesite.setString(4, description);
		updateFreesite.setString(5, language);
		Database.setBoolean(updateFreesite, 6, FMS);
		Database.setBoolean(updateFreesite, 7, activeLink);
		Database.setBoolean(updateFreesite, 8, online);
		Database.setBoolean(updateFreesite, 9, obsolete);
		Database.setBoolean(updateFreesite, 10, ignoreResetOffline);
		Database.setBoolean(updateFreesite, 11, highlight);
		Database.setDate(updateFreesite, 12, crawled);
		updateFreesite.setString(13, comment);
		updateFreesite.setString(14, key.getKey());
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
		Boolean hasActiveLink = Database.getBoolean(resultSet, "ActiveLink");
		Boolean isOnline = Database.getBoolean(resultSet, "Online");
		Boolean isObsolete = Database.getBoolean(resultSet, "Obsolete");
		Boolean ignoreResetOffline = Database.getBoolean(resultSet, "IgnoreResetOffline");
		Boolean crawlOnlyIndex = Database.getBoolean(resultSet, "CrawlOnlyIndex");
		Boolean highlight = Database.getBoolean(resultSet, "Highlight");
		Date added = Database.getDate(resultSet, "Added");
		Date crawled = Database.getDate(resultSet, "Crawled");
		String comment = resultSet.getString("Comment");
		Key resultKey = new Key(rawKey, edition, editionHint);
		return new Freesite(id, resultKey, author, title, keywords, description, language, isFMS, hasActiveLink,
				isOnline, isObsolete, ignoreResetOffline, crawlOnlyIndex, highlight, added, crawled, comment);
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
					null, null, null, null);
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
		getNextURL.close();
	}
}
