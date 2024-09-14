/*
  Copyright 2020 - 2024 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateUtility {

	public static ZoneOffset getTimeZone() {
		return ZoneOffset.UTC;
	}

	public static OffsetDateTime getNow() {
		return OffsetDateTime.now(getTimeZone());
	}

	public static OffsetDateTime getDate(Integer year, Integer month, Integer day, Integer hour, Integer minute,
			Integer second) {
		return OffsetDateTime.of(year, month, day, hour, minute, second, 0, getTimeZone());
	}
}
