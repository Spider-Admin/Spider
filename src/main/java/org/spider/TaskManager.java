/*
  Copyright 2023 - 2025 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spider.Spider.UpdateType;
import org.spider.data.Task;
import org.spider.importer.FMSImporter;
import org.spider.importer.FrostImporter;
import org.spider.network.Freenet;
import org.spider.network.USKListener;
import org.spider.storage.Database;
import org.spider.storage.Storage;

import freemarker.template.TemplateException;
import net.pterodactylus.fcp.highlevel.FcpClient;
import net.pterodactylus.fcp.highlevel.FcpException;

public class TaskManager implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(TaskManager.class);

	private static final String HELP_FORMAT = "%-40s  %s";
	private static final String HELP_FORMAT_EXTRA = "%-23s  %-15s  %s";
	private static final Integer HELP_WIDTH = 80;

	private static final String TASK_LIST_FORMAT = "%-6s  %-23s  %-8s  %-13s";
	private static final Integer TASK_LIST_WIDTH = 56;

	private Integer waitStep;
	private TaskType defaultTask = TaskType.CRAWL;

	private Connection connection;
	private Storage storage;

	private enum TaskType {
		INIT("init"), ADD_FREESITE("add-freesite"), ADD_FREESITE_FROM_FILE("add-freesite-from-file"),
		ADD_FREESITE_FROM_FMS("add-freesite-from-fms"), ADD_FREESITE_FROM_FROST("add-freesite-from-frost"),
		RESET_ALL_OFFLINE("reset-all-offline"), RESET_OFFLINE("reset-offline"), UPDATE("update"), UPDATE_0("update-0"),
		UPDATE_ONLINE("update-online"), UPDATE_OFFLINE("update-offline"), CRAWL("crawl"), OUTPUT_TEST("output-test"),
		OUTPUT_RELEASE("output-release"), HELP("help"), RESET_ALL_HIGHLIGHT("reset-all-highlight"),
		RESET_HIGHLIGHT("reset-highlight"), EXPORT_DATABASE("export-database"), RUN_TASK_LIST("run-task-list"),
		RESET_TASK_LIST("reset-task-list"), SHOW_TASK_LIST("show-task-list");

		private String name;

		private TaskType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}

		public static TaskType getTask(String task) {
			for (TaskType value : values()) {
				if (value.name.equalsIgnoreCase(task)) {
					return value;
				}
			}
			throw new IllegalArgumentException(String.format("Unknown task \"%s\"!", task));
		}
	}

	public TaskManager() throws SQLException {
		Settings settings = Settings.getInstance();
		connection = Database.getConnection();
		storage = new Storage(getConnection());
		waitStep = settings.getInteger(Settings.WAIT_STEP);
	}

	public String getDefaultTask() {
		return defaultTask.toString();
	}

	public final Connection getConnection() {
		return connection;
	}

	public void execute(String task, String extra, Boolean isRunAsTaskList)
			throws SQLException, IOException, TemplateException, FcpException, InterruptedException {
		TaskType taskType = TaskType.getTask(task);
		switch (taskType) {
		case INIT:
		case ADD_FREESITE:
		case ADD_FREESITE_FROM_FILE:
		case RESET_ALL_OFFLINE:
		case RESET_OFFLINE:
		case RESET_ALL_HIGHLIGHT:
		case RESET_HIGHLIGHT:
		case EXPORT_DATABASE:
			try (Connection connection = Database.getConnection(); Spider spider = new Spider(connection);) {
				if (taskType == TaskType.INIT) {
					spider.init();
				} else if (taskType == TaskType.ADD_FREESITE) {
					spider.addFreesite(extra);
				} else if (taskType == TaskType.ADD_FREESITE_FROM_FILE) {
					spider.addFreesiteFromFile(extra);
				} else if (taskType == TaskType.RESET_ALL_OFFLINE) {
					spider.resetAllOfflineFreesites();
				} else if (taskType == TaskType.RESET_OFFLINE) {
					spider.resetCertainOfflineFreesites(extra);
				} else if (taskType == TaskType.RESET_ALL_HIGHLIGHT) {
					spider.resetAllHighlight();
				} else if (taskType == TaskType.RESET_HIGHLIGHT) {
					spider.resetCertainHighlight(extra);
				} else if (taskType == TaskType.EXPORT_DATABASE) {
					spider.exportDatabase();
				}
				connection.commit();
			}
			break;
		case ADD_FREESITE_FROM_FMS:
			try (Connection connection = Database.getConnection();
					FMSImporter importer = new FMSImporter(connection);) {
				importer.addFreesiteFromFMS();
			}
			break;
		case ADD_FREESITE_FROM_FROST:
			try (Connection connection = Database.getConnection();
					FrostImporter importer = new FrostImporter(connection);) {
				importer.addFreesiteFromFrost();
			}
			break;
		case OUTPUT_RELEASE:
		case OUTPUT_TEST:
			try (Connection connection = Database.getConnection(true); Output output = new Output(connection);) {
				if (taskType == TaskType.OUTPUT_TEST) {
					output.writeFreesiteIndex(false);
				} else if (taskType == TaskType.OUTPUT_RELEASE) {
					output.writeFreesiteIndex(true);
				}
				connection.rollback();
			}
			break;
		case CRAWL:
			try (Connection connection = Database.getConnection();
					Spider spider = new Spider(connection);
					FcpClient freenet = Freenet.getConnection();) {
				spider.crawl(freenet);
			}
			break;
		case UPDATE:
		case UPDATE_0:
		case UPDATE_ONLINE:
		case UPDATE_OFFLINE:
			Settings settings = Settings.getInstance();
			Integer updateWaitTime = settings.getInteger(Settings.UPDATE_WAIT_TIME) * 1000;
			if (!extra.isEmpty()) {
				try {
					updateWaitTime = Integer.parseInt(extra) * 1000;
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(String.format("\"%s\" is not a valid wait-time!", extra), e);
				}
			}

			try (Connection connection = Database.getConnection();
					Spider spider = new Spider(connection);
					USKListener listener = new USKListener(connection);
					FcpClient freenet = Freenet.getConnection();) {
				freenet.addFcpListener(listener);
				if (taskType == TaskType.UPDATE) {
					spider.updateFreesites(freenet, UpdateType.ALL);
				} else if (taskType == TaskType.UPDATE_0) {
					spider.updateFreesites(freenet, UpdateType.EDITION_ZERO);
				} else if (taskType == TaskType.UPDATE_ONLINE) {
					spider.updateFreesites(freenet, UpdateType.ONLINE);
				} else if (taskType == TaskType.UPDATE_OFFLINE) {
					spider.updateFreesites(freenet, UpdateType.OFFLINE);
				}

				if (isRunAsTaskList) {
					while (storage.getWaitSeconds() > 0) {
						getConnection().rollback();
						Thread.sleep(waitStep * 1000);
						Integer remainingWaitSeconds = storage.getWaitSeconds() - waitStep;
						if (remainingWaitSeconds < 0) {
							remainingWaitSeconds = 0;
						}
						storage.setWaitSeconds(remainingWaitSeconds);
						getConnection().commit();
					}
				} else {
					Thread.sleep(updateWaitTime);
				}
				freenet.removeFcpListener(listener);
			}
			break;
		case RUN_TASK_LIST:
			Task currentTask = null;
			List<TaskType> updateTasks = new ArrayList<>(
					List.of(TaskType.UPDATE, TaskType.UPDATE_0, TaskType.UPDATE_ONLINE, TaskType.UPDATE_OFFLINE));
			while ((currentTask = storage.getCurrentTask()) != null) {

				String waitTime = "";
				if (updateTasks.contains(TaskType.getTask(currentTask.getName()))) {
					waitTime = storage.getWaitSeconds().toString();
				}

				getConnection().rollback();
				execute(currentTask.getName(), waitTime, true);

				storage.finishCurrentTask();
				getConnection().commit();
			}
			execute(TaskType.RESET_TASK_LIST.toString(), "", true);
			break;
		case RESET_TASK_LIST:
			log.info("Reset task-list");
			storage.resetTaskList();
			getConnection().commit();
			break;
		case SHOW_TASK_LIST:
			currentTask = storage.getCurrentTask();
			ArrayList<Task> taskList = storage.getTaskList();
			Integer remaining = storage.getWaitSeconds();
			getConnection().rollback();
			StringJoiner showPage = new StringJoiner(System.lineSeparator());
			showPage.add("");
			showPage.add("Task list:");
			showPage.add("");
			showPage.add(StringUtils.leftPad("", TASK_LIST_WIDTH, "-"));
			showPage.add(String.format(TASK_LIST_FORMAT, "Active", "Task", "Wait (s)", "Remaining (s)"));
			showPage.add(StringUtils.leftPad("", TASK_LIST_WIDTH, "-"));
			for (Task printTask : taskList) {
				String formattedActive = "";
				String formattedWait = "";
				String formattedRemaining = "";
				if (printTask.getWaitSeconds() != null) {
					formattedWait = printTask.getWaitSeconds().toString();
				}
				if (currentTask.getID().equals(printTask.getID())) {
					formattedActive = "x";
					if (remaining != null) {
						formattedRemaining = remaining.toString();
					}
				}
				showPage.add(String.format(TASK_LIST_FORMAT, formattedActive, printTask.getName(), formattedWait,
						formattedRemaining));
			}
			showPage.add(StringUtils.leftPad("", TASK_LIST_WIDTH, "-"));
			showPage.add("");
			System.out.println(showPage);
			break;
		case HELP:
			StringJoiner helpPage = new StringJoiner(System.lineSeparator());
			helpPage.add("");
			helpPage.add("Default-Task: " + defaultTask);
			helpPage.add("");
			helpPage.add(StringUtils.leftPad("", HELP_WIDTH, "-"));
			helpPage.add(String.format(HELP_FORMAT_EXTRA, "Task", "Extra", "Description"));
			helpPage.add(String.format(HELP_FORMAT_EXTRA, "Parameter 1", "Parameter 2", ""));
			helpPage.add(StringUtils.leftPad("", HELP_WIDTH, "-"));
			helpPage.add(String.format(HELP_FORMAT, TaskType.HELP, "Show this help."));
			helpPage.add("");
			helpPage.add(String.format(HELP_FORMAT, TaskType.RUN_TASK_LIST, "Executes the predefined list of tasks."));
			helpPage.add(String.format(HELP_FORMAT, TaskType.SHOW_TASK_LIST, "Shows the predefined list of tasks."));
			helpPage.add(String.format(HELP_FORMAT, TaskType.RESET_TASK_LIST,
					"Resets the task list such that it will start from the beginning on the next launch."));
			helpPage.add("");
			helpPage.add(String.format(HELP_FORMAT, TaskType.INIT, "Init the database by adding the seed-key."));
			helpPage.add("");
			helpPage.add(
					String.format(HELP_FORMAT_EXTRA, TaskType.ADD_FREESITE, "<freesite>", "Add freesite <freesite>."));
			helpPage.add(String.format(HELP_FORMAT_EXTRA, TaskType.ADD_FREESITE_FROM_FILE, "<filename>",
					"Read freesites from text-file <filename> and adds them."));
			helpPage.add(String.format(HELP_FORMAT, TaskType.ADD_FREESITE_FROM_FMS,
					"Searches the database of FMS for freesites and adds them."));
			helpPage.add(String.format(HELP_FORMAT, TaskType.ADD_FREESITE_FROM_FROST,
					"Searches the logfiles of Frost for freesites and adds them."));
			helpPage.add("");
			helpPage.add(String.format(HELP_FORMAT, TaskType.RESET_ALL_OFFLINE,
					"Resets the state of all offline freesites, such that they can be crawled again."));
			helpPage.add(String.format(HELP_FORMAT_EXTRA, TaskType.RESET_OFFLINE, "<ID1>,<ID2>,...",
					"Resets the state of freesites with the given IDs. The IDs can be seen in the test-output."));
			helpPage.add("");
			helpPage.add(String.format(HELP_FORMAT_EXTRA, TaskType.UPDATE, "[wait-time]",
					"Check for new editions of freesites by subscribing for wait-time seconds to all freesite with a edition."));
			helpPage.add(String.format(HELP_FORMAT_EXTRA, TaskType.UPDATE_0, "[wait-time]",
					"Check for new editions of freesites by subscribing for wait-time seconds to all freesite with edition 0."));
			helpPage.add(String.format(HELP_FORMAT_EXTRA, TaskType.UPDATE_ONLINE, "[wait-time]",
					"Check for new editions of freesites by subscribing for wait-time seconds to all freesite which are online."));
			helpPage.add(String.format(HELP_FORMAT_EXTRA, TaskType.UPDATE_OFFLINE, "[wait-time]",
					"Check for new editions of freesites by subscribing for wait-time seconds to all freesite which are offline."));
			helpPage.add("");
			helpPage.add(String.format(HELP_FORMAT, TaskType.CRAWL, "Crawls freesites."));
			helpPage.add("");
			helpPage.add(String.format(HELP_FORMAT, TaskType.OUTPUT_TEST,
					"Generated the test-output. It contains the IDs of all freesites and clickable absolute links."));
			helpPage.add(String.format(HELP_FORMAT, TaskType.OUTPUT_RELEASE,
					"Generates the release-output. This version is meant to published in Freenet."));
			helpPage.add("");
			helpPage.add(String.format(HELP_FORMAT, TaskType.RESET_ALL_HIGHLIGHT,
					"Resets the highlight-flag of all freesites. Call this after releasing an edition."));
			helpPage.add(String.format(HELP_FORMAT_EXTRA, TaskType.RESET_HIGHLIGHT, "<ID1>,<ID2>,...",
					"Resets the highlight-flag of freesites with the given IDs. The IDs can be seen in the test-output. Call this after releasing an edition."));
			helpPage.add("");
			helpPage.add(String.format(HELP_FORMAT, TaskType.EXPORT_DATABASE,
					String.format("Exports the database as sql-dump to %s.", Spider.getExportFilename())));
			helpPage.add(StringUtils.leftPad("", HELP_WIDTH, "-"));
			helpPage.add("");
			System.out.println(helpPage);
			break;
		}
	}

	@Override
	public void close() throws SQLException {
		storage.close();
		getConnection().close();
	}
}
