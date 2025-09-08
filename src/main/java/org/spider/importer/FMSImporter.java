/*
  Copyright 2021 - 2025 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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
import org.spider.data.Key;
import org.spider.storage.Database;

public class FMSImporter extends Spider {

	private static final Logger log = LoggerFactory.getLogger(FMSImporter.class);

	private static final String DATABASE_FILE = "fms.db3";

	private static final String READ_MESSAGES_SQL = "SELECT `Body` FROM `tblMessage` WHERE `Body` LIKE '%USK@%'";
	private static final String READ_SONE_SQL = "SELECT `PublicKey`, `SoneLastIndex` FROM `tblIdentity` WHERE `SoneLastIndex` IS NOT NULL";

	public FMSImporter(Connection connection) throws SQLException {
		super(connection);
	}

	public void addFreesiteFromFMS() throws SQLException {
		log.info("Add freesites from FMS");
		String filename = Path.of(Settings.getInstance().getString(Settings.IMPORT_FMS_PATH), DATABASE_FILE).toString();
		try (Connection fmsConnection = Database.getConnection(filename, true);
				PreparedStatement stmtMsg = fmsConnection.prepareStatement(READ_MESSAGES_SQL);
				ResultSet resultSetMsg = stmtMsg.executeQuery()) {
			while (resultSetMsg.next()) {
				addFreesiteFromString(resultSetMsg.getString("Body"));
				connection.commit();
			}
			if (!Settings.getInstance().getBoolean(Settings.IMPORT_FMS_IGNORE_SONE)) {
				log.info("Add Sone freesites from FMS");
				try (PreparedStatement stmtSone = fmsConnection.prepareStatement(READ_SONE_SQL);
						ResultSet resultSetSone = stmtSone.executeQuery()) {
					while (resultSetSone.next()) {
						String key = Key.changeSSK2USK(resultSetSone.getString("PublicKey"));
						Integer edition = Database.getInteger(resultSetSone, "SoneLastIndex");
						String freesite = String.format("%s%s/%d/", key, Key.SONE_PATH, edition);
						addFreesiteFromString(freesite);
						connection.commit();
					}
				}
			}
		}
	}
}
