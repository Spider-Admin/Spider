/*
  Copyright 2020 - 2025 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spider.Settings;
import org.spider.utility.DateUtility;
import org.spider.utility.ListUtility;
import org.sqlite.SQLiteConfig;

public class Database {

	private static final Logger log = LoggerFactory.getLogger(Database.class);

	private static Settings settings = Settings.getInstance();

	public static Connection getConnection() throws SQLException {
		return getConnection(false);
	}

	public static Connection getConnection(Boolean readOnly) throws SQLException {
		return getConnection(settings.getString(Settings.DATABASE_FILE), readOnly);
	}

	public static Connection getConnectionTest() throws SQLException {
		return getConnection(settings.getString(Settings.DATABASE_FILE_TEST), false);
	}

	public static Connection getConnection(String filename, Boolean readOnly) throws SQLException {
		SQLiteConfig config = new SQLiteConfig();
		config.setReadOnly(readOnly);
		log.debug("Open database {}", filename);
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + filename, config.toProperties());
		connection.setAutoCommit(false);
		return connection;
	}

	public static Integer getInteger(ResultSet resultSet, String columnName) throws SQLException {
		Integer result = resultSet.getInt(columnName);
		if (resultSet.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	public static void setInteger(PreparedStatement statement, Integer position, Integer value) throws SQLException {
		statement.setObject(position, value, Types.INTEGER);
	}

	public static Long getLong(ResultSet resultSet, String columnName) throws SQLException {
		Long result = resultSet.getLong(columnName);
		if (resultSet.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	public static void setLong(PreparedStatement statement, Integer position, Long value) throws SQLException {
		statement.setObject(position, value, Types.BIGINT);
	}

	public static Boolean getBoolean(ResultSet resultSet, String columnName) throws SQLException {
		Boolean result = resultSet.getBoolean(columnName);
		if (resultSet.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	public static void setBoolean(PreparedStatement statement, Integer position, Boolean value) throws SQLException {
		statement.setObject(position, value, Types.BOOLEAN);
	}

	public static List<String> getStringList(ResultSet resultSet, String columnName) throws SQLException {
		return ListUtility.toList(resultSet.getString(columnName));
	}

	public static void setStringList(PreparedStatement statement, Integer position, List<String> list)
			throws SQLException {
		statement.setString(position, ListUtility.toString(list));
	}

	public static OffsetDateTime getDate(ResultSet resultSet, String columnName) throws SQLException {
		Timestamp result = resultSet.getTimestamp(columnName);
		if (resultSet.wasNull()) {
			return null;
		} else {
			return OffsetDateTime.ofInstant(result.toInstant(), DateUtility.getTimeZone());
		}
	}

	public static void setDate(PreparedStatement statement, Integer position, OffsetDateTime value)
			throws SQLException {
		Timestamp timestamp = null;
		if (value != null) {
			timestamp = Timestamp.from(value.toInstant());
		}
		statement.setTimestamp(position, timestamp);
	}

	public static void execute(Connection connection, String query) throws SQLException {
		try (PreparedStatement stmt = connection.prepareStatement(query);) {
			stmt.executeUpdate();
		}
	}

	public static PreparedStatement prepareStatement(Connection connection, PreparedStatement statement, String query)
			throws SQLException {
		if (statement != null) {
			return statement;
		} else {
			return connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
		}
	}

	public static void closeStatement(PreparedStatement statement) throws SQLException {
		if (statement != null) {
			statement.close();
		}
	}
}
