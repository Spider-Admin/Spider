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
import java.util.stream.Collectors;

public class ListUtility {

	public static final String SPLIT_INTERNAL = ",";
	public static final String SPLIT_INTERNAL_ALT_1 = ";";
	public static final String SPLIT_INTERNAL_ALT_2 = "\n";
	public static final String SPLIT_INTERNAL_ALT_3 = " ";
	public static final String SPLIT_FORMAT = ", ";

	private static String detectSplitChar(String list) {
		String splitChar = ListUtility.SPLIT_INTERNAL;
		if (!list.contains(ListUtility.SPLIT_INTERNAL)) {
			splitChar = ListUtility.SPLIT_INTERNAL_ALT_1;
			if (!list.contains(ListUtility.SPLIT_INTERNAL_ALT_1)) {
				splitChar = ListUtility.SPLIT_INTERNAL_ALT_2;
				if (!list.contains(ListUtility.SPLIT_INTERNAL_ALT_2)) {
					splitChar = ListUtility.SPLIT_INTERNAL_ALT_3;
				}
			}
		}
		return splitChar;
	}

	/**
	 * Splits a string into a List<String> by using the separator ",". To increase
	 * compatibility with meta.keywords other separators are allowed too.
	 */
	public static List<String> toList(String value) {
		if (value == null || value.isEmpty()) {
			return new ArrayList<>();
		}
		List<String> list = new ArrayList<>(List.of(value.split(detectSplitChar(value))));
		return list.stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
	}

	public static String toString(List<String> list) {
		return String.join(SPLIT_INTERNAL, list);
	}

	public static String formatList(List<String> list) {
		return String.join(SPLIT_FORMAT, list);
	}
}
