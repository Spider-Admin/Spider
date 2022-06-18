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

package org.spider.utility;

import java.util.ArrayList;
import java.util.Arrays;

public class ListUtility {

	public static ArrayList<String> getList(String listRaw) {
		if (listRaw == null || listRaw.isBlank()) {
			return new ArrayList<String>();
		}
		String splitChar = ",";
		if (!listRaw.contains(",")) {
			splitChar = ";";
			if (!listRaw.contains(";")) {
				splitChar = "\n";
				if (!listRaw.contains("\n")) {
					splitChar = " ";
				}
			}
		}
		return new ArrayList<String>(Arrays.asList(listRaw.split(splitChar)));
	}

	public static String formatList(String listRaw) {
		ArrayList<String> list = getList(listRaw);
		String splitter = "";
		StringBuilder result = new StringBuilder();
		for (String element : list) {
			element = element.trim();
			if (!element.isEmpty()) {
				result.append(splitter);
				result.append(element);
				splitter = ", ";
			}
		}
		return result.toString();
	}
}
