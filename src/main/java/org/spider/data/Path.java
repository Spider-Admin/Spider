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

package org.spider.data;

import java.time.OffsetDateTime;

public class Path {

	private String path;
	private Boolean online;
	private OffsetDateTime added;
	private OffsetDateTime crawled;

	public Path(String path, Boolean online, OffsetDateTime added, OffsetDateTime crawled) {
		this.path = path;
		this.online = online;
		this.added = added;
		this.crawled = crawled;
	}

	public String getPath() {
		return path;
	}

	public Boolean isOnline() {
		return online;
	}

	public OffsetDateTime getAdded() {
		return added;
	}

	public OffsetDateTime getCrawled() {
		return crawled;
	}
}
