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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.spider.Spider.UpdateType;
import org.spider.network.Freenet;
import org.spider.network.USKListener;
import org.spider.storage.Database;

import freemarker.template.TemplateException;
import net.pterodactylus.fcp.highlevel.FcpClient;
import net.pterodactylus.fcp.highlevel.FcpException;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	private static final String HELP_FORMAT = "%-38s %s";
	private static final String HELP_FORMAT_EXTRA = "%-22s %-15s %s";
	private static final Integer HELP_WIDTH = 80;

	public enum Task {
		INIT("init"), ADD_FREESITE("add-freesite"), ADD_FREESITE_FROM_FILE("add-freesite-from-file"),
		ADD_FREESITE_FROM_FMS("add-freesite-from-fms"), ADD_FREESITE_FROM_FROST("add-freesite-from-frost"),
		RESET_ALL_OFFLINE("reset-all-offline"), RESET_OFFLINE("reset-offline"), UPDATE("update"), UPDATE_0("update-0"),
		UPDATE_ONLINE("update-online"), UPDATE_OFFLINE("update-offline"), SPIDER("spider"), OUTPUT_TEST("output-test"),
		OUTPUT_RELEASE("output-release"), HELP("help"), RESET_ALL_HIGHLIGHT("reset-all-highlight"),
		RESET_HIGHLIGHT("reset-highlight"), EXPORT_DATABASE("export-database");

		private String name;

		private Task(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}

		public static Task getTask(String task) {
			for (Task value : values()) {
				if (value.name.equalsIgnoreCase(task)) {
					return value;
				}
			}
			throw new IllegalArgumentException();
		}
	}

	public static void main(String[] args) {
		// Redirect java.util.logging (JUL) to SL4J
		// -> Required for FcpClient
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		// TODO Ctrl+C from Gradle does NOT call the ShutdownHook
		// Gradle uses Process.destroy(), which is platform-dependent:
		// - Windows platforms support a forcible kill signal.
		// - Linux platforms support a normal (non-forcible) kill signal.
		// Details: {@link Process#supportsNormalTermination()}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				// TODO Close open database-connections (if any). Use PooledConnection?
				log.info("Spider finished.");
			}
		});

		// TODO What is the correct link to a freemail?
		// - Spider-Admin@msXvLpwmDqprlrYZ5ZRZyi7VUcWQ~Wisznv9JkQuSXY.freemail?
		// - spider-admin@tlc66lu4eyhku24wwym6lfczzixnkuofsd4wrlgopp6smrbojf3a.freemail?
		// Asked Bombe, the author of Sone, but no response.

		try {
			Task defaultTask = Task.SPIDER;
			Task task = defaultTask;
			if (args.length >= 1) {
				task = Task.getTask(args[0]);
			}
			String extra = "";
			if (args.length >= 2) {
				extra = args[1];
			}

			switch (task) {
			case INIT:
			case ADD_FREESITE:
			case ADD_FREESITE_FROM_FILE:
			case ADD_FREESITE_FROM_FMS:
			case ADD_FREESITE_FROM_FROST:
			case RESET_ALL_OFFLINE:
			case RESET_OFFLINE:
			case RESET_ALL_HIGHLIGHT:
			case RESET_HIGHLIGHT:
			case EXPORT_DATABASE:
				try (Connection connection = Database.getConnection(); Spider spider = new Spider(connection);) {
					if (task == Task.INIT) {
						spider.init();
					} else if (task == Task.ADD_FREESITE) {
						spider.addFreesite(extra);
					} else if (task == Task.ADD_FREESITE_FROM_FILE) {
						spider.addFreesiteFromFile(extra);
					} else if (task == Task.ADD_FREESITE_FROM_FMS) {
						spider.addFreesiteFromFMS();
					} else if (task == Task.ADD_FREESITE_FROM_FROST) {
						spider.addFreesiteFromFrost();
					} else if (task == Task.RESET_ALL_OFFLINE) {
						spider.resetAllOfflineFreesites();
					} else if (task == Task.RESET_OFFLINE) {
						spider.resetCertainOfflineFreesites(extra);
					} else if (task == Task.RESET_ALL_HIGHLIGHT) {
						spider.resetAllHighlight();
					} else if (task == Task.RESET_HIGHLIGHT) {
						spider.resetCertainHighlight(extra);
					} else if (task == Task.EXPORT_DATABASE) {
						spider.exportDatabase();
					}
					connection.commit();
				}
				break;
			case OUTPUT_RELEASE:
			case OUTPUT_TEST:
				try (Connection connection = Database.getConnection(true); Output output = new Output(connection);) {
					if (task == Task.OUTPUT_TEST) {
						output.writeFreesiteIndex(false);
					} else if (task == Task.OUTPUT_RELEASE) {
						output.writeFreesiteIndex(true);
					}
					connection.rollback();
				}
				break;
			case SPIDER:
				try (Connection connection = Database.getConnection();
						Spider spider = new Spider(connection);
						FcpClient freenet = Freenet.getConnection();) {
					spider.spider(freenet);
				}
				break;
			case UPDATE:
			case UPDATE_0:
			case UPDATE_ONLINE:
			case UPDATE_OFFLINE:
				Settings settings = Settings.getInstance();
				Integer updateWaitTime = settings.getInteger(Settings.UPDATE_WAIT_TIME) * 1000;
				if (!extra.isEmpty()) {
					updateWaitTime = Integer.parseInt(extra) * 1000;
				}

				try (Connection connection = Database.getConnection();
						Spider spider = new Spider(connection);
						USKListener listener = new USKListener(connection);
						FcpClient freenet = Freenet.getConnection();) {
					freenet.addFcpListener(listener);
					if (task == Task.UPDATE) {
						spider.updateFreesites(freenet, UpdateType.ALL);
					} else if (task == Task.UPDATE_0) {
						spider.updateFreesites(freenet, UpdateType.EDITION_ZERO);
					} else if (task == Task.UPDATE_ONLINE) {
						spider.updateFreesites(freenet, UpdateType.ONLINE);
					} else if (task == Task.UPDATE_OFFLINE) {
						spider.updateFreesites(freenet, UpdateType.OFFLINE);
					}
					Thread.sleep(updateWaitTime);
					freenet.removeFcpListener(listener);
				}
				break;
			case HELP:
				System.out.println("");
				System.out.println("Default-Task: " + defaultTask);
				System.out.println("");
				System.out.println(StringUtils.leftPad("", HELP_WIDTH, "-"));
				System.out.println(String.format(HELP_FORMAT_EXTRA, "Task", "Extra", "Description"));
				System.out.println(String.format(HELP_FORMAT_EXTRA, "Parameter 1", "Parameter 2", ""));
				System.out.println(StringUtils.leftPad("", HELP_WIDTH, "-"));
				System.out.println(String.format(HELP_FORMAT, Task.HELP, "Show this help."));
				System.out.println("");
				System.out.println(String.format(HELP_FORMAT, Task.INIT, "Init the database by adding the seed-key."));
				System.out.println("");
				System.out.println(
						String.format(HELP_FORMAT_EXTRA, Task.ADD_FREESITE, "<freesite>", "Add freesite <freesite>."));
				System.out.println(String.format(HELP_FORMAT_EXTRA, Task.ADD_FREESITE_FROM_FILE, "<filename>",
						"Read freesites from text-file <filename> and adds them."));
				System.out.println(String.format(HELP_FORMAT, Task.ADD_FREESITE_FROM_FMS,
						"Searches the database of FMS for freesites and adds them."));
				System.out.println(String.format(HELP_FORMAT, Task.ADD_FREESITE_FROM_FROST,
						"Searches the logfiles of Frost for freesites and adds them."));
				System.out.println("");
				System.out.println(String.format(HELP_FORMAT, Task.RESET_ALL_OFFLINE,
						"Resets the state of all offline freesites, such that they can be crawled again."));
				System.out.println(String.format(HELP_FORMAT_EXTRA, Task.RESET_OFFLINE, "<ID1>,<ID2>,...",
						"Resets the state of freesites with the given IDs. The IDs can be seen in the test-output."));
				System.out.println("");
				System.out.println(String.format(HELP_FORMAT, Task.UPDATE,
						"Check for new editions of freesites by subscribing to all freesite with a edition."));
				System.out.println(String.format(HELP_FORMAT, Task.UPDATE_0,
						"Check for new editions of freesites by subscribing to all freesite with edition 0."));
				System.out.println(String.format(HELP_FORMAT, Task.UPDATE_ONLINE,
						"Check for new editions of freesites by subscribing to all freesite which are online."));
				System.out.println(String.format(HELP_FORMAT, Task.UPDATE_OFFLINE,
						"Check for new editions of freesites by subscribing to all freesite which are offline."));
				System.out.println("");
				System.out.println(String.format(HELP_FORMAT, Task.SPIDER, "Crawls freesites."));
				System.out.println("");
				System.out.println(String.format(HELP_FORMAT, Task.OUTPUT_TEST,
						"Generated the test-output. It contains the IDs of all freesites and clickable absolute links."));
				System.out.println(String.format(HELP_FORMAT, Task.OUTPUT_RELEASE,
						"Generates the release-output. This version is meant to published in Freenet."));
				System.out.println("");
				System.out.println(String.format(HELP_FORMAT, Task.RESET_ALL_HIGHLIGHT,
						"Resets the highlight-flag of all freesites. Call this after releasing an edition."));
				System.out.println(String.format(HELP_FORMAT_EXTRA, Task.RESET_HIGHLIGHT, "<ID1>,<ID2>,...",
						"Resets the highlight-flag of freesites with the given IDs. The IDs can be seen in the test-output. Call this after releasing an edition."));
				System.out.println("");
				System.out.println(String.format(HELP_FORMAT, Task.EXPORT_DATABASE,
						String.format("Exports the database as sql-dump to %s.", Spider.getExportFilename())));
				System.out.println(StringUtils.leftPad("", HELP_WIDTH, "-"));
				System.out.println("");
				break;
			}
		} catch (SQLException e) {
			log.error("Database-Error!", e);
		} catch (IOException e) {
			log.error("IO-Error!", e);
		} catch (FcpException e) {
			log.error("Freenet-Error!", e);
		} catch (InterruptedException e) {
			log.error("Thread-Error!", e);
		} catch (TemplateException e) {
			log.error("Template-Error!", e);
		} catch (NumberFormatException e) {
			log.error("Invalid number \"{}\"!", args[1]);
		} catch (IllegalArgumentException e) {
			log.error("Unknown task \"{}\"!", args[0]);
		}
	}
}