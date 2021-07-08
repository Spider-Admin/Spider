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

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spider.network.Freenet;
import org.spider.storage.Database;
import org.spider.storage.Export;
import org.spider.storage.Freesite;
import org.spider.storage.Storage;

import net.pterodactylus.fcp.SubscribeUSK;
import net.pterodactylus.fcp.highlevel.FcpClient;
import net.pterodactylus.fcp.highlevel.FcpException;
import net.pterodactylus.fcp.highlevel.GetResult;

public class Spider implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(Spider.class);

	private static final Pattern addKeyPattern = Pattern.compile("(USK@[^@ ]*?\\/.*?\\/-?\\d+)",
			Pattern.CASE_INSENSITIVE);

	private static final String IMPORT_FMS = "SELECT `Body` FROM `tblMessage` WHERE `Body` LIKE '%USK@%'";

	private static final ArrayList<String> FROST_LOGS;

	private static final String ACTIVE_LINK = "activelink.png";

	private static final String INDEX_PATH = "";

	public static enum UpdateType {
		ALL, EDITION_ZERO, ONLINE, OFFLINE
	};

	static {
		FROST_LOGS = new ArrayList<>();
		FROST_LOGS.add("frost0.log");
		FROST_LOGS.add("frost1.log");
	}

	private Storage storage;
	private Settings settings;
	private Connection connection;

	public Spider(Connection connection) throws SQLException {
		this.storage = new Storage(connection);
		this.settings = Settings.getInstance();
		this.connection = connection;
	}

	public void init() throws SQLException {
		log.info("Init database ...");
		addFreesite(settings.getString(Settings.SEED_KEY), null);
	}

	private void addFreesite(String freesite, String sourceFreesite) throws SQLException {
		Key key = new Key(freesite);
		Integer freesiteID = storage.getFreesiteID(key);
		if (freesiteID == null) {

			// Detect and skip double encoded keys
			String decodedFreesite = decodeURL(freesite);
			Key decodedKey = new Key(decodedFreesite);
			Integer decodedFreesiteID = storage.getFreesiteID(decodedKey);
			if (decodedFreesiteID != null) {
				log.warn("Skip double encoded key {}", freesite);
				return;
			}

			if (key.getEdition() != null) {
				key.setEditionHint(key.getEdition());
				key.setEdition(null);
				key.setPath(INDEX_PATH);
				log.info("Add freesite {}{}/", key.getKey(), key.getEditionHint());
				storage.addFreesite(key, new Date());
				storage.addPath(key, new Date());
			} else {
				log.error("Ignore freesite {} without edition!", freesite);
				return;
			}
		} else {
			updateFreesiteEdition(freesite, true);
		}

		if (sourceFreesite != null) {
			Key sourceKey = new Key(sourceFreesite);
			if (!sourceKey.getKey().equals(key.getKey())) {
				ArrayList<Integer> network = storage.getOutNetwork(sourceKey);
				if (!network.contains(freesiteID)) {
					log.info("Add network to {}", key.getKey());
					storage.addNetwork(sourceKey, key);
				}
			}
		}
	}

	private void addFreesiteFromString(String content) throws SQLException {
		Matcher addKeyMatcher = addKeyPattern.matcher(content);
		while (addKeyMatcher.find()) {
			String freesite = decodeURL(addKeyMatcher.group(0));
			freesite = HTMLParser.capitalizeKeyType(freesite);
			if (freesite.length() < 50) {
				log.warn("Ignore invalid freesite {}", freesite);
			} else {
				log.info("Manually add freesite {}", freesite);
				try {
					addFreesite(freesite, null);
				} catch (NumberFormatException e) {
					log.error("Invalid edition of key {}!", freesite);
				}
			}
		}
	}

	public void addFreesite(String freesite) throws SQLException {
		addFreesiteFromString(freesite);
	}

	public void addFreesiteFromFile(String filename) throws IOException, SQLException {
		log.info("Add freesites from file {}", filename);
		String content = Files.readString(Paths.get(filename), settings.getCharset());
		addFreesiteFromString(content);
	}

	public void addFreesiteFromFMS() throws SQLException {
		log.info("Add freesites from FMS");
		String fmsFilename = settings.getString(Settings.IMPORT_FMS_DATABASE_FILE);
		try (Connection fmsConnection = Database.getConnection(fmsFilename, true);
				PreparedStatement stmt = fmsConnection.prepareStatement(IMPORT_FMS);
				ResultSet resultSet = stmt.executeQuery()) {
			while (resultSet.next()) {
				addFreesiteFromString(resultSet.getString("Body"));
			}
		}
	}

	public void addFreesiteFromFrost() throws SQLException, IOException {
		log.info("Add freesites from Frost");
		String frostPath = settings.getString(Settings.IMPORT_FROST_PATH);
		for (String filename : FROST_LOGS) {
			String fullFilename = frostPath + filename;
			if (new File(fullFilename).exists()) {
				addFreesiteFromFile(fullFilename);
			}
		}
	}

	private ArrayList<Integer> extractIDs(String rawIDs) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (String rawID : rawIDs.split(",")) {
			ids.add(Integer.parseInt(rawID));
		}
		return ids;
	}

	private void resetOfflineFreesites(ArrayList<Integer> onlyIDs) throws SQLException {
		ArrayList<Freesite> freesites = storage.getAllFreesite(false);
		for (Freesite freesite : freesites) {
			if (freesite.isOnline() != null && !freesite.isOnline() && !freesite.ignoreResetOffline()
					&& (onlyIDs == null || onlyIDs.contains(freesite.getID()))) {
				log.debug("Reset freesite {}", freesite.getKeyObj().getKey());
				storage.updatePath(freesite.getKeyObj(), null, null);
			}
		}
	}

	public void resetCertainOfflineFreesites(String rawIDs) throws SQLException {
		log.info("Reset status of certain offline freesites ...");
		resetOfflineFreesites(extractIDs(rawIDs));
	}

	public void resetAllOfflineFreesites() throws SQLException {
		log.info("Reset status of all offline freesites ...");
		resetOfflineFreesites(null);
	}

	public void updateFreesites(FcpClient freenet, UpdateType type) throws IOException, SQLException {
		String logMsg = "";
		switch (type) {
		case ALL:
			break;
		case EDITION_ZERO:
			logMsg = " (only edition 0)";
			break;
		case ONLINE:
			logMsg = " (only online)";
			break;
		case OFFLINE:
			logMsg = " (only offline)";
			break;
		}
		log.info("Updating freesites{} ...", logMsg);

		ArrayList<Freesite> freesites = storage.getAllFreesite(false);
		connection.rollback();
		Iterator<Freesite> iterator = freesites.iterator();
		while (iterator.hasNext()) {
			Freesite freesite = iterator.next();
			Boolean checkUpdate = false;
			switch (type) {
			case ALL:
				if (freesite.getKeyObj().getEdition() != null) {
					checkUpdate = true;
				}
				break;
			case EDITION_ZERO:
				if (freesite.getKeyObj().getEdition() != null && freesite.getKeyObj().getEdition() == 0) {
					checkUpdate = true;
				}
				break;
			case ONLINE:
				if (freesite.isOnline()) {
					checkUpdate = true;
				}
				break;
			case OFFLINE:
				if (!freesite.isOnline()) {
					checkUpdate = true;
				}
				break;
			}
			if (!checkUpdate) {
				iterator.remove();
			}
		}

		for (Freesite freesite : freesites) {
			subscribeUSK(freenet, freesite.getKeyObj().toString());
		}
		log.info("Subscribed {} freesites", freesites.size());
	}

	public void crawl(FcpClient freenet) throws SQLException, IOException, FcpException {
		log.info("Start crawling ...");

		String url;
		HTMLParser parser = new HTMLParser();
		while ((url = getNextURL()) != null) {
			connection.rollback();

			log.info("Crawl {}", url);

			Key key = new Key(url);
			GetResult site = null;
			try {
				site = freenet.getURI(url, true);
				if (site.getErrorCode() == Freenet.Error.TOO_MANY_PATH_COMPONENTS.getCode()) {
					String redirect = url.substring(0, url.length() - 1);
					log.warn("{}. Try {}", Freenet.Error.TOO_MANY_PATH_COMPONENTS.toString(), redirect);
					site = freenet.getURI(redirect, true);
				}
			} catch (FcpException e) {
				// Handling of broken keys like
				// USK@something<spaces>something/site/edition/
				// Fred reports a INVALID_URI(20) in this case.
				if (e.getMessage().equals("Protocol error (4, Error parsing freenet URI")) {
					log.error("Broken key detected.");
					updateFreesite(key.toString(), "", "", "", "", "", false, false, false, true,
							Freenet.Error.INVALID_URI.toString());
					updatePath(key.toString(), key.getPath(), false);
					connection.commit();
				}
				// TODO Freenet: After an FcpException, the connection is broken:
				// - jFCPlib thinks we are disconnected.
				// - Calling disconnect and connect results in an "Socket closed".
				throw e;
			}

			String realURL = site.getRealUri();
			if (realURL != null) {
				// Sometimes getRealUri returns a similar, but different key
				if (key.getKeyOnly().equals(new Key(realURL).getKeyOnly())) {
					realURL = decodeURL(realURL);
					key = new Key(realURL);

					Boolean isUpdated = updateFreesiteEdition(key.toString(), false);
					if (!isUpdated) {
						log.info("Skip key, since there was no update.");
						Freesite freesite = storage.getFreesite(key);
						storage.updatePath(key, storage.getPath(key).isOnline(), freesite.getCrawled());
						connection.commit();
						continue;
					}
				} else {
					// TODO Freenet: Keys are not unique
					// Change some letters of a key and Fred returns the original key
					log.info("Invalid redirect. Mark key as fake");
					updatePath(key.toString(), key.getPath(), false);
					updateFreesite(key.toString(), "", "", "", "", "", false, false, false, true,
							Freenet.Error.FAKE_KEY.toString());
					connection.commit();
					continue;
				}
			} else {
				// Update: editionHint = 0 -> edition = 0
				if (key.getEdition() == 0 && key.getPath().isEmpty()) {
					updateFreesiteEdition(key.toString(), false);
				}
			}

			parser.parseStream(site.getInputStream());
			Boolean isOnline = site.isSuccess();

			if (key.getPath().isEmpty()) {
				String author = parser.getAuthor();
				String title = parser.getTitle();
				String keywords = parser.getKeywords();
				String description = parser.getDescription();
				String language = parser.getLanguage();
				String redirect = parser.getRedirect();

				Boolean hasActiveLink = false;
				if (isOnline) {
					log.info("Check for ActiveLink");
					hasActiveLink = hasActiveLink(freenet, key);
				}

				if (!redirect.isEmpty()) {
					log.debug("Found redirect in HTML: {}", redirect);
					addPath(key.toString(), redirect, key.toString());
				}

				Freesite freesite = storage.getFreesite(key);

				Boolean ignoreResetOffline = false;
				String comment = null;
				if (!isOnline) {
					comment = Freenet.Error.getMessage(site.getErrorCode());

					if (isFakeKey(key.getKey())) {
						if (comment != null) {
							comment = comment + ". ";
						} else {
							comment = "";
						}
						comment = comment + Freenet.Error.FAKE_KEY.toString();
					}

					ignoreResetOffline = comment != null;
				} else if (freesite.crawlOnlyIndex()) {
					comment = "Only Index-Page has been crawled";
				}

				log.info("Author: {}", author);
				log.info("Title: {}", title);
				log.info("Keywords: {}", keywords);
				log.info("Description: {}", description);
				log.info("Language: {}", language);
				log.info("Has ActiveLink: {}", hasActiveLink);
				log.info("Is Online: {}", isOnline);
				log.info("Redirect: {}", redirect);
				log.info("Comment: {}", comment);

				updateFreesite(key.toString(), author, title, keywords, description, language, hasActiveLink, isOnline,
						false, ignoreResetOffline, comment);
			}

			ArrayList<String> paths = parser.getPaths();
			for (String path : paths) {
				path = decodeURL(path);

				if (HTMLParser.isIgnored(path)) {
					log.debug("Ignored link: {}", path);
					continue;
				}

				if (path.contains("../")) {
					log.warn("Relative link {} was not resolved!", path);
				}

				Key linkKey = new Key(path);

				if (linkKey.isKey()) {
					log.debug("Found freesite: {}", linkKey);
					path = HTMLParser.capitalizeKeyType(path);

					if (key.getKey().equals(linkKey.getKey())) {
						log.debug("Ignore update edition of {} from {} to {}", key.getKey(), key.getEdition(),
								linkKey.getEdition());
						linkKey.setEdition(key.getEdition());
					}

					addFreesite(linkKey.toString(), key.toString());

					if (!linkKey.getPath().isEmpty()) {
						log.debug("Found link from key: {}", linkKey.getPath());
						addPath(linkKey.toString(), linkKey.getPath(), key.toString());
					}
				} else {
					log.debug("Found link: {}", key.getPathWithoutFilename() + linkKey.getPath());
					addPath(key.toString(), key.getPathWithoutFilename() + linkKey.getPath(), key.toString());
				}
			}
			updatePath(key.toString(), key.getPath(), isOnline);
			connection.commit();
		}
	}

	private void updateFreesite(String freesite, String author, String title, String keywords, String description,
			String language, Boolean hasActiveLink, Boolean isOnline, Boolean isObsolete, Boolean ignoreResetOffline,
			String comment) throws SQLException {
		Key key = new Key(freesite);
		Boolean isFMS = (title != null && (title.contains("FMS Site") || title.contains("FMS Recent Messages")))
				|| (description != null && description.contains("FMS-generated"));
		Boolean isSone = key.getSitePath().equalsIgnoreCase("Sone") && title.contains(" - Sone");

		Freesite oldFreesite = storage.getFreesite(key);
		Boolean oldIsOnline = oldFreesite.isOnline();
		Boolean oldIsHighlight = oldFreesite.isHighlight();
		Boolean isHighLight = (oldIsHighlight != null && oldIsHighlight) || (oldIsOnline == null && isOnline != null)
				|| (oldIsOnline != null && isOnline != null && oldIsOnline != isOnline);

		storage.updateFreesite(key, author, title, keywords, description, language, isFMS, isSone, hasActiveLink,
				isOnline, isObsolete, ignoreResetOffline, isHighLight, new Date(), comment);
	}

	public Boolean updateFreesiteEdition(String freesite, Boolean searchNew) throws SQLException {
		Boolean isUpdated = false;
		Key key = new Key(freesite);

		if (key.getEdition() == null) {
			log.error("Missing edition: {}", freesite);
			return isUpdated;
		}

		Freesite oldFreesite = storage.getFreesite(key);

		if (oldFreesite == null) {
			log.error("Freesite not found: {}", freesite);
			return isUpdated;
		}

		Key oldKey = oldFreesite.getKeyObj();
		Date crawledDate = oldFreesite.getCrawled();

		// Fake EditionHint:
		// E.g. A freesite links to edition -10, but only edition 5 is available.
		// Edition = N, Fake-Hint = N + 1
		// -> oldKey.getEdition() == key.getEdition()

		if (oldKey.getEdition() == null || oldKey.getEdition() <= key.getEdition()) {
			if (searchNew) {
				if ((oldKey.getEditionHint() != null && Math.abs(oldKey.getEditionHint()) >= key.getEdition())
						|| (oldKey.getEdition() != null && oldKey.getEdition() >= key.getEdition())) {
					log.debug("Ignore edition-hint of {} to {}", oldKey.getKey(), key.getEdition());
					return isUpdated;
				}

				log.info("Set edition-hint of {} to {}", oldKey.getKey(), key.getEdition());
				oldKey.setEditionHint(key.getEdition());
				oldKey.setPath(INDEX_PATH);
				storage.updatePath(oldKey, storage.getPath(oldKey).isOnline(), null);
				storage.updateFreesiteEdition(oldKey, crawledDate);
			} else {
				Boolean isEditionUpdate = false;
				Boolean isRemoveEditionHint = false;
				if (oldKey.getEdition() == null) {
					isEditionUpdate = true;
					log.info("Set edition of {} to {}", oldKey.getKey(), key.getEdition());
				} else {
					if (key.getEdition() > oldKey.getEdition()) {
						isEditionUpdate = true;
						log.info("Update edition of {} from {} to {}", oldKey.getKey(), oldKey.getEdition(),
								key.getEdition());
					} else if (oldKey.getEditionHint() != null && oldKey.getEdition().equals(key.getEdition())) {
						log.info("Remove edition-hint of {}", oldKey.getKey());
						isRemoveEditionHint = true;
					}
				}

				if (isEditionUpdate) {
					storage.deleteAllNetwork(key);
					storage.deleteAllPath(key);
					isUpdated = true;
					crawledDate = null;
					key.setPath(INDEX_PATH);
					storage.addPath(key, new Date());
				}
				if (isEditionUpdate || isRemoveEditionHint) {
					oldKey.setEdition(key.getEdition());
					oldKey.clearEditionHint();
					storage.updateFreesiteEdition(oldKey, crawledDate);
				}
			}
		}
		return isUpdated;
	}

	private void addPath(String freesite, String path, String sourceFreesite) throws SQLException {
		Key key = new Key(freesite);
		key.setPath(path);

		addFreesite(freesite, sourceFreesite);
		Integer id = storage.getPathID(key);

		if (id == null) {
			Key oldKey = storage.getFreesiteKey(key);
			Freesite freesiteObj = storage.getFreesite(key);

			if (freesiteObj.crawlOnlyIndex() && !path.equals(INDEX_PATH)) {
				log.warn("Ignore path {}", path);
				return;
			}

			if (oldKey.getEdition() != null && oldKey.getEdition().equals(key.getEdition())) {
				log.info("Add path {}", key.getPath());
				storage.addPath(key, new Date());
			} else {
				log.debug("Path {} not added. Edition does not match: {} != {}", key.getPath(), oldKey.getEdition(),
						key.getEdition());
			}
		}
	}

	private void updatePath(String freesite, String path, Boolean online) throws SQLException {
		Key key = new Key(freesite);
		key.setPath(path);
		storage.updatePath(key, online, new Date());
	}

	public void subscribeUSK(FcpClient freenet, String key) throws IOException {
		log.debug("Subscribe {}", key);
		SubscribeUSK usk = new SubscribeUSK(key, key);
		usk.setActive(true);

		freenet.getConnection().sendMessage(usk);
	}

	private String getNextURL() throws SQLException {
		Key nextKey = storage.getNextURL();
		if (nextKey != null) {
			return nextKey.toStringExt();
		} else {
			return null;
		}
	}

	private Boolean hasActiveLink(FcpClient freenet, Key key) throws IOException, FcpException {
		GetResult activeLinkSite = freenet.getURI(key.toString() + ACTIVE_LINK, true);
		return activeLinkSite.isSuccess();
	}

	private Boolean isFakeKey(String freesite) throws SQLException {
		Integer minDiff = Integer.MAX_VALUE;
		Integer maxDiff = settings.getInteger(Settings.FAKE_KEY_MAX_DIFF);

		Key key = storage.getFreesiteKey(new Key(freesite));
		if (key.getEditionHint() == null) {
			return false;
		}

		LevenshteinDistance distance = new LevenshteinDistance();

		ArrayList<Freesite> freesites = storage.findFreesite("/" + key.getSitePath() + "/");
		for (Freesite orgFreesite : freesites) {
			Key orgKey = orgFreesite.getKeyObj();
			if (!key.getKeyOnly().equals(orgKey.getKeyOnly()) && orgKey.getEdition() != null
					&& orgKey.getSitePath().equals(key.getSitePath())) {
				Integer diffChars = distance.apply(key.getKeyOnly(), orgKey.getKeyOnly());
				if (diffChars < maxDiff) {
					log.debug("Original: {} = {}", orgKey, diffChars);
				}
				minDiff = Math.min(minDiff, diffChars);
			}
		}
		return minDiff < maxDiff;
	}

	public String decodeURL(String url) {
		return URLDecoder.decode(url, settings.getCharset());
	}

	private void resetHighlight(ArrayList<Integer> onlyIDs) throws SQLException {
		ArrayList<Freesite> freesites = storage.getAllFreesite(false);
		for (Freesite freesite : freesites) {
			if (onlyIDs == null || onlyIDs.contains(freesite.getID())) {
				log.debug("Reset highlight-flag of freesite {}", freesite.getKeyObj().getKey());
				storage.resetHighlight(freesite.getKeyObj());
			}
		}
	}

	public void resetAllHighlight() throws SQLException {
		log.info("Reset highlight-flag of all freesites");
		resetHighlight(null);
	}

	public void resetCertainHighlight(String rawIDs) throws SQLException {
		log.info("Reset highlight-flag of certain freesites");
		resetHighlight(extractIDs(rawIDs));
	}

	public static String getExportFilename() {
		return String.format("%s.sql", Settings.getInstance().getString(Settings.INDEX_NAME));
	}

	public void exportDatabase() throws SQLException, IOException {
		String filename = getExportFilename();
		log.info("Export database to {}", filename);
		Files.deleteIfExists(Paths.get(filename));
		Files.writeString(Paths.get(filename), Export.dumpDatabase(connection), settings.getCharset(),
				StandardOpenOption.CREATE_NEW);
	}

	@Override
	public void close() throws SQLException {
		storage.close();
	}
}
