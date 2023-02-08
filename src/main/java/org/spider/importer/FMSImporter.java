/*
  Copyright 2021 - 2023 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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

package org.spider.importer;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spider.Settings;
import org.spider.Spider;
import org.spider.storage.Database;

public class FMSImporter extends Spider {

	private static final Logger log = LoggerFactory.getLogger(FMSImporter.class);

	private static final String DATABASE_FILE = "fms.db3";

	private static final String IMPORT = "SELECT `Body` FROM `tblMessage` WHERE `Body` LIKE '%USK@%'";

	public FMSImporter(Connection connection) throws SQLException {
		super(connection);
	}

	public void addFreesiteFromFMS() throws SQLException {
		log.info("Add freesites from FMS");
		String filename = Path.of(Settings.getInstance().getString(Settings.IMPORT_FMS_PATH), DATABASE_FILE).toString();
		try (Connection connection = Database.getConnection(filename, true);
				PreparedStatement stmt = connection.prepareStatement(IMPORT);
				ResultSet resultSet = stmt.executeQuery()) {
			while (resultSet.next()) {
				addFreesiteFromString(resultSet.getString("Body"));
				connection.commit();
			}
		}
	}
}
