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

package org.spider.utility;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtility {

	public static final String UTC = "UTC";

	public static Calendar getUTCCalendar() {
		return Calendar.getInstance(TimeZone.getTimeZone(UTC));
	}

	public static Date getDate(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second) {
		Calendar calendar = getUTCCalendar();

		calendar.set(year, month - 1, day, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
}
