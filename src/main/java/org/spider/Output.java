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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spider.storage.Freesite;
import org.spider.storage.Storage;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.utility.StandardCompress;

public class Output implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(Output.class);

	private enum Page {
		ONLINE, ONLINE_IMG, FMS, FMS_IMG, SONE, OFFLINE, FAQ
	};

	private Settings settings;

	private Configuration templateConfig;
	private HashMap<String, Object> params;

	private Storage storage;

	private String outputPath;

	private static StandardCompress outputCompress;
	private static HashMap<Page, String> filenames;
	private static HashMap<Page, String> titles;
	private static HashMap<Page, String> templateNames;

	static {
		outputCompress = StandardCompress.INSTANCE;

		filenames = new HashMap<>();
		filenames.put(Page.ONLINE, "index.htm");
		filenames.put(Page.ONLINE_IMG, "index-img.htm");
		filenames.put(Page.FMS, "fms.htm");
		filenames.put(Page.FMS_IMG, "fms-img.htm");
		filenames.put(Page.SONE, "sone.htm");
		filenames.put(Page.OFFLINE, "offline.htm");
		filenames.put(Page.FAQ, "faq.htm");

		titles = new HashMap<>();
		titles.put(Page.ONLINE, "");
		titles.put(Page.ONLINE_IMG, "Online (Gallery)");
		titles.put(Page.FMS, "FMS");
		titles.put(Page.FMS_IMG, "FMS (Gallery)");
		titles.put(Page.SONE, "Sone");
		titles.put(Page.OFFLINE, "Offline");
		titles.put(Page.FAQ, "About / FAQ");

		templateNames = new HashMap<>();
		templateNames.put(Page.ONLINE, "index.ftlh");
		templateNames.put(Page.ONLINE_IMG, "gallery.ftlh");
		templateNames.put(Page.FMS, "index.ftlh");
		templateNames.put(Page.FMS_IMG, "gallery.ftlh");
		templateNames.put(Page.SONE, "index.ftlh");
		templateNames.put(Page.OFFLINE, "index.ftlh");
		templateNames.put(Page.FAQ, "faq.ftlh");
	}

	public Output(Connection connection) throws SQLException {
		this.storage = new Storage(connection);
		this.settings = Settings.getInstance();

		templateConfig = new Configuration(Configuration.VERSION_2_3_30);
		templateConfig.setDefaultEncoding(settings.getCharset().name());
		templateConfig.setLocale(Locale.US);
		templateConfig.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
		templateConfig.setDateFormat("yyyy-MM-dd");
		templateConfig.setNumberFormat("0");
		templateConfig.setTimeZone(TimeZone.getTimeZone("UTC"));
		templateConfig.setClassForTemplateLoading(Output.class, "/templates");
		templateConfig.addAutoImport("format", "/macros/format.ftlh");
		templateConfig.addAutoImport("include", "/macros/include.ftlh");

		Freesite selfFreesite = storage.getFreesite(new Key(settings.getString(Settings.INDEX_KEY)), false);
		selfFreesite.setEdition(selfFreesite.getEditionWithHint() + 1);
		Freesite selfSourceFreesite = storage.getFreesite(new Key(settings.getString(Settings.INDEX_SOURCE_KEY)));
		Freesite fmsFreesite = storage.getFreesite(new Key(settings.getString(Settings.CONTACT_FMS_FREESITE)));
		Freesite publishFreesite = storage.getFreesite(new Key(settings.getString(Settings.FAQ_PUBLISH_KEY)), false);
		Freesite jsiteFreesite = storage.getFreesite(new Key(settings.getString(Settings.FAQ_JSITE_KEY)), false);

		params = new HashMap<>();
		params.put("metaAuthor", settings.getString(Settings.META_AUTHOR));
		params.put("metaDescription", settings.getString(Settings.META_DESCRIPTION));
		params.put("metaKeywords", settings.getString(Settings.META_KEYWORDS));
		params.put("author", settings.getString(Settings.CONTACT_AUTHOR));
		params.put("freemail", settings.getString(Settings.CONTACT_FREEMAIL));
		params.put("frost", settings.getString(Settings.CONTACT_FROST));
		params.put("fmsPublicKey", settings.getString(Settings.CONTACT_FMS_PUBLIC_KEY));
		params.put("fmsFreesite", fmsFreesite);
		params.put("sone", settings.getString(Settings.CONTACT_SONE));
		params.put("selfName", settings.getString(Settings.INDEX_NAME));
		params.put("selfFreesite", selfFreesite);
		params.put("selfSourceFreesite", selfSourceFreesite);
		params.put("selfDatabaseKey", settings.getString(Settings.INDEX_DATABASE_KEY));
		params.put("faqAbout", settings.getString(Settings.FAQ_ABOUT));
		params.put("publishFreesite", publishFreesite);
		params.put("jsiteFreesite", jsiteFreesite);
		params.put("hiddenCategories", settings.getHiddenCategories());
	}

	public void setMode(Boolean isRelease) {
		if (isRelease) {
			templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			outputPath = settings.getString(Settings.OUTPUT_PATH_RELEASE);
		} else {
			templateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
			outputPath = settings.getString(Settings.OUTPUT_PATH_TEST);
		}
	}

	private void writeFreesiteIndex(ArrayList<Freesite> freesiteList, Page type)
			throws IOException, TemplateException, SQLException {
		log.info("Write index for {}", type);

		Iterator<Freesite> iterator = freesiteList.iterator();
		while (iterator.hasNext()) {
			Freesite freesite = iterator.next();
			Boolean shouldRemove = false;
			if (freesite.getCrawled() == null) {
				shouldRemove = true;
			} else {
				switch (type) {
				case ONLINE:
					if (freesite.isFMS() || freesite.isSone() || !freesite.isOnline()) {
						shouldRemove = true;
					}
					break;
				case ONLINE_IMG:
					if (freesite.isFMS() || freesite.isSone() || !freesite.isOnline() || !freesite.hasActiveLink()) {
						shouldRemove = true;
					}
					break;
				case FMS:
					if (!freesite.isFMS() || !freesite.isOnline()) {
						shouldRemove = true;
					}
					break;
				case FMS_IMG:
					if (!freesite.isFMS() || !freesite.isOnline() || !freesite.hasActiveLink()) {
						shouldRemove = true;
					}
					break;
				case SONE:
					if (!freesite.isSone() || !freesite.isOnline()) {
						shouldRemove = true;
					}
					break;
				case OFFLINE:
					if (freesite.isOnline()) {
						shouldRemove = true;
					}
					break;
				case FAQ:
					break;
				}
			}
			if (shouldRemove) {
				iterator.remove();
			}
		}

		params.put("freesiteList", freesiteList);

		// TODO Show details about In (and Out?). Which freesite links to a freesite?
		writePage(type);
	}

	private void writePage(Page type) throws IOException, TemplateException, SQLException {
		params.put("title", titles.get(type));
		params.put("outputType", type);

		HashMap<String, Object> options = new HashMap<>();
		try (Writer writer = outputCompress.getWriter(
				new OutputStreamWriter(new FileOutputStream(outputPath + filenames.get(type)), settings.getCharset()),
				options)) {
			Template template = templateConfig.getTemplate(templateNames.get(type));
			Environment env = template.createProcessingEnvironment(params, writer);
			env.setOutputEncoding(settings.getCharset().name());
			env.process();
		}
	}

	private void copyFile(String filename) throws IOException {
		Files.copy(Paths.get(filename), Paths.get(outputPath + filename), StandardCopyOption.REPLACE_EXISTING);
	}

	public void writeFreesiteIndex(Boolean isRelease) throws IOException, SQLException, TemplateException {
		log.info("Writing freesite-index");

		setMode(isRelease);

		Files.createDirectories(Paths.get(outputPath));

		log.info("Loading content");
		ArrayList<Freesite> freesiteList = storage.getAllFreesite(true);

		ArrayList<String> hiddenCategories = settings.getHiddenCategories();

		Integer countOnline = 0;
		Iterator<Freesite> iterator = freesiteList.iterator();
		while (iterator.hasNext()) {
			Boolean isHidden = false;
			Freesite freesite = iterator.next();
			for (String category : hiddenCategories) {
				if ((category.isEmpty() && freesite.getCategory().isEmpty())
						|| (!category.isEmpty() && freesite.getCategory().contains(category))) {
					isHidden = true;
				}
			}
			if (isHidden) {
				iterator.remove();
			} else if (freesite.isOnline() != null && freesite.isOnline()) {
				countOnline = countOnline + 1;
			}
		}

		String keyPrefix = "";
		if (!isRelease) {
			keyPrefix = "http://" + settings.getString(Settings.FREENET_HOST) + ":"
					+ settings.getInteger(Settings.FREENET_PORT_FPROXY);
		}

		params.put("countTotal", freesiteList.size());
		params.put("isRelease", isRelease);
		params.put("countOnline", countOnline);
		params.put("lastUpdate", new Date());
		params.put("keyPrefix", keyPrefix);

		writeFreesiteIndex(new ArrayList<Freesite>(freesiteList), Page.ONLINE);
		writeFreesiteIndex(new ArrayList<Freesite>(freesiteList), Page.ONLINE_IMG);
		writeFreesiteIndex(new ArrayList<Freesite>(freesiteList), Page.FMS);
		writeFreesiteIndex(new ArrayList<Freesite>(freesiteList), Page.FMS_IMG);
		writeFreesiteIndex(new ArrayList<Freesite>(freesiteList), Page.SONE);
		writeFreesiteIndex(new ArrayList<Freesite>(freesiteList), Page.OFFLINE);
		writePage(Page.FAQ);

		log.info("Copy static files");
		copyFile("style.css");
		copyFile("activelink.png");
	}

	@Override
	public void close() throws SQLException {
		storage.close();
	}
}
