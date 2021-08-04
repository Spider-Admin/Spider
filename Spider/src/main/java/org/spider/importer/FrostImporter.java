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

package org.spider.importer;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spider.Settings;
import org.spider.Spider;

public class FrostImporter extends Spider {

	private static final Logger log = LoggerFactory.getLogger(FrostImporter.class);

	private static final ArrayList<String> LOGS;

	static {
		LOGS = new ArrayList<>();
		LOGS.add("frost0.log");
		LOGS.add("frost1.log");
	}

	public FrostImporter(Connection connection) throws SQLException {
		super(connection);
	}

	public void addFreesiteFromFrost() throws SQLException, IOException {
		log.info("Add freesites from Frost");
		String frostPath = Settings.getInstance().getString(Settings.IMPORT_FROST_PATH);
		for (String filename : LOGS) {
			String fullFilename = frostPath + filename;
			if (new File(fullFilename).exists()) {
				addFreesiteFromFile(fullFilename);
			}
		}
	}
}
