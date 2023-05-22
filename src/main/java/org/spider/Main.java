/*
  Copyright 2020 - 2023 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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
import java.nio.file.InvalidPathException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import freemarker.template.TemplateException;
import net.pterodactylus.fcp.highlevel.FcpException;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		// Redirect java.util.logging (JUL) to SL4J
		// -> Required for FcpClient
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		// Ctrl+C from Gradle does not always call the ShutdownHook
		// Gradle uses Process.destroy(), which is platform-dependent:
		// - Windows platforms only support a forcible kill signal.
		// - Linux platforms support a normal (non-forcible) kill signal.
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Settings settings = Settings.getInstance();
				log.info("{} finished.", settings.getString(Settings.INDEX_NAME));
			}
		});

		try (TaskManager taskManager = new TaskManager();) {
			String defaultTask = taskManager.getDefaultTask();
			String task = defaultTask;
			if (args.length >= 1) {
				task = args[0];
			}
			String extra = "";
			if (args.length >= 2) {
				extra = args[1];
			}
			taskManager.execute(task, extra, false);
		} catch (SQLException e) {
			log.error("Database-Error!", e);
		} catch (IOException | InvalidPathException e) {
			log.error("IO-Error!", e);
		} catch (FcpException e) {
			log.error("Freenet-Error!", e);
		} catch (InterruptedException e) {
			log.error("Thread-Error!", e);
		} catch (TemplateException e) {
			log.error("Template-Error!", e);
		} catch (IllegalArgumentException e) {
			log.error("Illegal argument!", e);
		}
	}
}