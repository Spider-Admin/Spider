/*
  Copyright 2025 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppHome {

	public static final String APP_HOME = "APP_HOME";

	public static void init() {
		try {
			Path jarPath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			Path appHome = null;
			if (Files.isRegularFile(jarPath)) {
				// jarPath = ROOT/lib/APPNAME.jar
				appHome = jarPath.getParent().getParent();
			} else {
				// jarPath = ROOT/build/classes/java/main OR
				// jarPath = ROOT/bin/main
				while (jarPath != null) {
					if (Files.exists(jarPath.resolve("build.gradle"))) {
						appHome = jarPath;
						break;
					}
					jarPath = jarPath.getParent();
				}
			}
			System.setProperty(APP_HOME, appHome.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace(); // Logger is unavailable here
		}
	}
}
