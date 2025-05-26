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

package org.spider;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spider.utility.ListUtility;

public class Settings {

	private static final Logger log = LoggerFactory.getLogger(Settings.class);

	private static final Settings instance = new Settings();

	private Properties properties;

	public static final String INDEX_NAME = "index.name";
	public static final String INDEX_KEY = "index.key";
	public static final String INDEX_SOURCE_KEY = "index.source.key";
	public static final String INDEX_DATABASE_KEY = "index.database.key";

	public static final String DATABASE_FILE = "database.file";
	public static final String DATABASE_FILE_TEST = "database.file.test";

	public static final String HYPHANET_HOST = "hyphanet.host";
	public static final String HYPHANET_PORT_FCP = "hyphanet.port.fcp";
	public static final String HYPHANET_PORT_FPROXY = "hyphanet.port.fproxy";

	public static final String IMPORT_FMS_PATH = "import.fms.path";
	public static final String IMPORT_FROST_PATH = "import.frost.path";
	public static final String IMPORT_FROST_IGNORE_PRIVATE_MESSAGES = "import.frost.ignore-private-messages";
	public static final String IMPORT_FROST_IGNORE_MESSAGE_ARCHIVE = "import.frost.ignore-message-archive";

	public static final String OUTPUT_PATH_RELEASE = "output.path.release";
	public static final String OUTPUT_PATH_TEST = "output.path.test";

	public static final String META_AUTHOR = "meta.author";
	public static final String META_DESCRIPTION = "meta.description";
	public static final String META_KEYWORDS = "meta.keywords";

	public static final String CONTACT_AUTHOR = "contact.author";
	public static final String CONTACT_FREEMAIL = "contact.freemail";
	public static final String CONTACT_FROST = "contact.frost";
	public static final String CONTACT_FMS_PUBLIC_KEY = "contact.fms.public.key";
	public static final String CONTACT_FMS_FREESITE = "contact.fms.freesite";
	public static final String CONTACT_SONE = "contact.sone";

	public static final String SEED_KEY = "seed.key";

	public static final String FAQ_ABOUT = "faq.about";
	public static final String FAQ_PUBLISH_KEY = "faq.publish.key";
	public static final String FAQ_JSITE_KEY = "faq.jsite.key";

	public static final String FAKE_KEY_MAX_DIFF = "fake.key.max.diff";

	public static final String ERROR_MAX_COUNT = "error.max.count";

	public static final String CATEGORIES_HIDDEN = "categories.hidden";

	public static final String IGNORE_EDITION_UPDATE = "ignore.edition.update";

	public static final String UPDATE_WAIT_TIME = "update.wait.time";

	public static final String WAIT_STEP = "wait.step";

	private static final String SETTINGS_FILENAME = "spider.properties";

	private static final String SETTINGS_DISTRIBUTION_PATH = "src/main/dist/";

	private static final String NO_CATEGORY = "NO_CATEGORY";

	protected Settings() {
		String settingsFilename = SETTINGS_FILENAME;
		if (!Files.exists(Path.of(settingsFilename))) {
			settingsFilename = Path.of(SETTINGS_DISTRIBUTION_PATH, SETTINGS_FILENAME).toString();
		}

		log.info("Loading settings from {}", settingsFilename);

		try (FileReader reader = new FileReader(settingsFilename, getCharset())) {
			properties = new Properties();
			properties.load(reader);
		} catch (IOException e) {
			log.error("IO-Error!", e);
		}
	}

	public static Settings getInstance() {
		return instance;
	}

	public String getString(String key) {
		if (!properties.containsKey(key)) {
			throw new NoSuchElementException(String.format("Property \"%s\" is missing!", key));
		}
		return properties.getProperty(key);
	}

	public Integer getInteger(String key) {
		return Integer.valueOf(getString(key));
	}

	public Boolean getBoolean(String key) {
		return Boolean.valueOf(getString(key));
	}

	public final Charset getCharset() {
		return StandardCharsets.UTF_8;
	}

	public ArrayList<String> getHiddenCategories() {
		ArrayList<String> result = ListUtility.getList(getString(Settings.CATEGORIES_HIDDEN));
		if (result.contains(NO_CATEGORY)) {
			result.remove(NO_CATEGORY);
			result.add("");
		}
		return result;
	}
}
