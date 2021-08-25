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

package org.spider.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.spider.Key;
import org.spider.network.Freenet;

public class Freesite {

	private Integer id;
	private Key key;
	private String author;
	private String title;
	private String keywords;
	private String description;
	private String language;
	private Boolean fms;
	private Boolean sone;
	private Boolean activeLink;
	private Boolean online;
	private Boolean obsolete;
	private Boolean ignoreResetOffline;
	private Boolean crawlOnlyIndex;
	private Boolean highlight;
	private Date added;
	private Date crawled;
	private String comment;

	private ArrayList<Path> pathList;
	private ArrayList<Integer> inNetwork;
	private ArrayList<Integer> outNetwork;

	private Integer pathOnlineSize;
	private Integer pathOnOfflineSize;

	public Freesite(Integer id, Key key, String author, String title, String keywords, String description,
			String language, Boolean fms, Boolean sone, Boolean activeLink, Boolean online, Boolean obsolete,
			Boolean ignoreResetOffline, Boolean crawlOnlyIndex, Boolean highlight, Date added, Date crawled,
			String comment) {
		this.id = id;
		this.key = key;
		this.author = author;
		this.title = title;
		this.keywords = keywords;
		this.description = description;
		this.language = language;
		this.fms = fms;
		this.sone = sone;
		this.activeLink = activeLink;
		this.online = online;
		this.obsolete = obsolete;
		this.ignoreResetOffline = ignoreResetOffline;
		this.crawlOnlyIndex = crawlOnlyIndex;
		this.highlight = highlight;
		this.added = added;
		this.crawled = crawled;
		this.comment = comment;
		pathList = null;
		inNetwork = null;
		outNetwork = null;
		pathOnlineSize = 0;
		pathOnOfflineSize = 0;
	}

	public Integer getID() {
		return id;
	}

	public Key getKeyObj() {
		return key;
	}

	public String getAuthor() {
		return StringUtils.normalizeSpace(author);
	}

	public String getTitle() {
		return title;
	}

	public String getKeywordsRaw() {
		return keywords;
	}

	public String getKeywords() {
		if (keywords == null) {
			return null;
		}
		String splitChar = ",";
		if (!keywords.contains(",")) {
			splitChar = ";";
			if (!keywords.contains(";")) {
				splitChar = "\n";
				if (!keywords.contains("\n")) {
					splitChar = " ";
				}
			}
		}

		List<String> keywordList = Arrays.asList(keywords.split(splitChar));
		String splitter = "";
		StringBuilder result = new StringBuilder();
		for (String keyword : keywordList) {
			keyword = keyword.trim();
			if (!keyword.isEmpty()) {
				result.append(splitter);
				result.append(keyword);
				splitter = ", ";
			}
		}
		return result.toString();
	}

	public String getDescription() {
		return StringUtils.normalizeSpace(description);
	}

	public String getLanguage() {
		return language;
	}

	public Boolean isFMS() {
		return fms;
	}

	public Boolean isSone() {
		return sone;
	}

	public Boolean hasActiveLink() {
		return activeLink;
	}

	public Boolean isOnline() {
		return online;
	}

	public Boolean isObsolete() {
		return obsolete;
	}

	public Boolean ignoreResetOffline() {
		return ignoreResetOffline;
	}

	public Boolean crawlOnlyIndex() {
		return crawlOnlyIndex;
	}

	public Boolean isHighlight() {
		return highlight;
	}

	public Date getAdded() {
		return added;
	}

	public Date getCrawled() {
		return crawled;
	}

	public String getComment() {
		return comment;
	}

	public void setPathList(ArrayList<Path> pathList) {
		this.pathList = pathList;

		for (Path path : pathList) {
			if (path.isOnline() != null) {
				pathOnOfflineSize = pathOnOfflineSize + 1;
				if (path.isOnline()) {
					pathOnlineSize = pathOnlineSize + 1;
				}
			}
		}
	}

	public ArrayList<Path> getPathList() {
		return pathList;
	}

	public void setInNetwork(ArrayList<Integer> inNetwork) {
		this.inNetwork = inNetwork;
	}

	public ArrayList<Integer> getInNetwork() {
		return inNetwork;
	}

	public void setOutNetwork(ArrayList<Integer> outNetwork) {
		this.outNetwork = outNetwork;
	}

	public ArrayList<Integer> getOutNetwork() {
		return outNetwork;
	}

	public Integer getPathOnlineSize() {
		return pathOnlineSize;
	}

	public Double getPathOnlinePercent() {
		if (pathOnOfflineSize > 0) {
			return pathOnlineSize * 100. / pathOnOfflineSize;
		} else {
			return 0.;
		}
	}

	public Boolean isKeyClickable() {
		return comment == null || (!comment.contains(Freenet.Error.INVALID_URI.toString())
				&& !comment.contains(Freenet.Error.ARCHIVE_FAILURE.toString())
				&& !comment.contains(Freenet.Error.INVALID_METADATA.toString())
				&& !comment.contains(Freenet.Error.FAKE_KEY.toString()));
	}

	public Boolean isFakeKey() {
		return comment != null && comment.contains(Freenet.Error.FAKE_KEY.toString());
	}

	// Shortcut getters
	public String getKey() {
		return key.getKeyOnly();
	}

	public String getSitePath() {
		return key.getSitePath();
	}

	public Long getEditionWithHint() {
		return key.getEditionWithHint();
	}

	// Shortcut setters
	public void setEdition(Long edition) {
		key.setEdition(edition);
	}
}
