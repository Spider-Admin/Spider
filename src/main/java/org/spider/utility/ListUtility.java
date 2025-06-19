/*
  Copyright 2021 - 2025 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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
import java.util.List;

public class ListUtility {

	public static final String SPLIT_INTERNAL = ",";
	public static final String SPLIT_INTERNAL_ALT_1 = ";";
	public static final String SPLIT_INTERNAL_ALT_2 = "\n";
	public static final String SPLIT_INTERNAL_ALT_3 = " ";
	public static final String SPLIT_FORMAT = ", ";

	public static List<String> toList(String value, String splitter) {
		if (value == null || value.isEmpty()) {
			return new ArrayList<>();
		}
		return new ArrayList<>(List.of(value.split(splitter)));
	}

	public static List<String> toList(String value) {
		return toList(value, SPLIT_INTERNAL);
	}

	public static String toString(List<String> list) {
		return String.join(SPLIT_INTERNAL, list);
	}

	public static String formatList(List<String> list) {
		return String.join(SPLIT_FORMAT, list);
	}
}
