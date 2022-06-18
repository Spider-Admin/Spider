/*
  Copyright 2021 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Export {

	private static final String NL = "\n";

	private static Pattern tablePattern = Pattern.compile("^CREATE TABLE.*?`(.*?)`.*?$", Pattern.CASE_INSENSITIVE);

	public static String dumpDatabase(Connection connection) throws SQLException {
		StringBuilder result = new StringBuilder();

		result.append("PRAGMA foreign_keys=OFF;" + NL);
		result.append("BEGIN TRANSACTION;" + NL);

		for (String query : Storage.tables.values()) {
			result.append(NL);
			result.append(query + ";" + NL);

			Matcher tableMatcher = tablePattern.matcher(query);
			if (tableMatcher.matches()) {
				result.append(NL);
				dumpTable(connection, result, tableMatcher.group(1));
			}
		}

		for (String query : Storage.views.values()) {
			result.append(NL);
			result.append(query + ";" + NL);
		}

		result.append(NL);
		result.append("COMMIT;" + NL);

		return result.toString();
	}

	private static String replace(String value, String search, String replace) {
		if (value.contains(search)) {
			value = value.replace(search, replace);
			value = "replace(" + value + ", '" + replace + "', char(" + (int) search.charAt(0) + "))";
		}
		return value;
	}

	private static void dumpTable(Connection connection, StringBuilder result, String table) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table);
				ResultSet resultSet = statement.executeQuery();) {
			ResultSetMetaData metaData = resultSet.getMetaData();
			Integer columnCount = metaData.getColumnCount();

			while (resultSet.next()) {
				result.append("INSERT INTO " + table + " VALUES (");
				for (int i = 0; i < columnCount; i++) {
					if (i > 0) {
						result.append(", ");
					}
					Object value = resultSet.getObject(i + 1);
					if (value == null) {
						result.append("NULL");
					} else {
						String outputValue = value.toString();
						outputValue = outputValue.replace("'", "''");
						outputValue = "'" + outputValue + "'";
						outputValue = replace(outputValue, "\n", "\\n");
						outputValue = replace(outputValue, "\r", "\\r");
						result.append(outputValue);
					}
				}
				result.append(");" + NL);
			}
		}
	}
}
