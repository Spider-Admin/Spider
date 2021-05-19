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

package org.spider;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public static final String FREENET_HOST = "freenet.host";
	public static final String FREENET_PORT_FCP = "freenet.port.fcp";
	public static final String FREENET_PORT_FPROXY = "freenet.port.fproxy";

	public static final String IMPORT_FMS_DATABASE_FILE = "import.fms.database.file";
	public static final String IMPORT_FROST_PATH = "import.frost.path";

	public static final String OUTPUT_PATH_RELEASE = "output.path.release";
	public static final String OUTPUT_PATH_TEST = "output.path.test";

	public static final String CONTACT_AUTHOR = "contact.author";
	public static final String CONTACT_FREEMAIL = "contact.freemail";
	public static final String CONTACT_FROST = "contact.frost";
	public static final String CONTACT_FMS_PUBLIC_KEY = "contact.fms.public.key";
	public static final String CONTACT_FMS_FREESITE = "contact.fms.freesite";
	public static final String CONTACT_SONE = "contact.sone";

	public static final String SEED_KEY = "seed.key";

	public static final String FAQ_TPI_KEY = "faq.tpi.key";
	public static final String FAQ_PUBLISH_KEY = "faq.publish.key";
	public static final String FAQ_JSITE_KEY = "faq.jsite.key";

	public static final String FAKE_KEY_MAX_DIFF = "fake.key.max.diff";

	public static final String UPDATE_WAIT_TIME = "update.wait.time";

	private static final String SETTINGS_FILENAME = "spider.properties";

	protected Settings() {
		log.info("Loading settings from {}", SETTINGS_FILENAME);

		try (FileReader reader = new FileReader(SETTINGS_FILENAME, getCharset())) {
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

	public Charset getCharset() {
		return StandardCharsets.UTF_8;
	}
}
