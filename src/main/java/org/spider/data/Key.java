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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Key {

	private static final Pattern uskPattern = Pattern.compile(
			"^\\/?(?:((?:CHK|SSK|USK|KSK)@(?:.*?\\/)?(?:.*?\\/)?)(?:(-?\\d+)(?:\\/|$))?)?(.*?)(?:(?:#|\\?).*?)?$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern newLinePattern = Pattern.compile("(\\r|\\n)");

	// USK@------key-------/edition/-------path--------
	// USK@keyOnly/sitePath/edition/folder/filename.ext

	private String key;
	private String keyOnly;
	private String sitePath;

	private Long edition;
	private Long editionHint;

	private String path;
	private String folder;
	private String filename;
	private String extension;

	public Key(String freesite) {
		freesite = newLinePattern.matcher(freesite).replaceAll("");
		Matcher uskMatcher = uskPattern.matcher(freesite);
		if (uskMatcher.matches()) {
			key = uskMatcher.group(1);
			String editionRaw = uskMatcher.group(2);
			if (editionRaw != null) {
				edition = Long.valueOf(editionRaw);
			}
			path = uskMatcher.group(3);
		}
		updateKeyParts();
	}

	public Key(String key, Long edition, Long editionHint, String path) {
		this.key = key;
		this.edition = edition;
		this.editionHint = editionHint;
		this.path = path;
		updateKeyParts();
	}

	public Key(String key, Long edition, Long editionHint) {
		this(key, edition, editionHint, "");
	}

	private final void updateKeyParts() {
		keyOnly = "";
		sitePath = "";
		folder = "";
		filename = "";
		extension = "";
		if (isUSK()) {
			String[] parts = key.split("/");
			keyOnly = parts[0];
			if (parts.length > 1) {
				sitePath = parts[1];
			}
		}

		if (path != null && !path.isEmpty()) {
			String fullFilename = path;
			Integer pathPos = path.lastIndexOf("/");
			if (pathPos != -1) {
				folder = path.substring(0, pathPos + 1);
				fullFilename = path.substring(pathPos + 1);
			}
			filename = fullFilename;
			Integer filenamePos = fullFilename.lastIndexOf(".");
			if (filenamePos != -1) {
				filename = fullFilename.substring(0, filenamePos);
				extension = fullFilename.substring(filenamePos + 1);
			}
		}
	}

	public String getKey() {
		return key;
	}

	public final Boolean isUSK() {
		return key != null && key.startsWith("USK@");
	}

	public Boolean isKSK() {
		return key != null && key.startsWith("KSK@");
	}

	public Boolean isCHK() {
		return key != null && key.startsWith("CHK@");
	}

	public Boolean isSSK() {
		return key != null && key.startsWith("SSK@");
	}

	public Long getEdition() {
		return edition;
	}

	public Long getEditionHint() {
		return editionHint;
	}

	public void setEdition(Long edition) {
		this.edition = edition;
	}

	/**
	 * Change the edition such that spider search for new editions
	 *
	 * @see https://github.com/freenet/wiki/wiki/Updatable-Subspace-Key
	 */
	public void setEditionHint(Long editionHint) {
		// TODO Freenet: Check for updates of edition 0?
		// -1 does not work
		this.editionHint = Math.abs(editionHint) * -1;
	}

	public void clearEditionHint() {
		editionHint = null;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Boolean isKey() {
		return key != null;
	}

	private static String formatURL(String key, Long edition, String path) {
		String result = key + edition + "/";
		if (path != null) {
			result = result + path;
		}
		return result;
	}

	public String toString() {
		Long useEdition = edition;
		if (useEdition == null) {
			useEdition = editionHint;
		}
		return formatURL(key, useEdition, null);
	}

	public String toStringExt() {
		Long useEdition = edition;
		if (editionHint != null) {
			useEdition = editionHint;
		}
		return formatURL(key, useEdition, path);
	}

	public String getKeyOnly() {
		return keyOnly;
	}

	public String getSitePath() {
		return sitePath;
	}

	public Long getEditionWithHint() {
		Long useEdition = edition;
		if (useEdition == null) {
			useEdition = editionHint;
		}
		return useEdition;
	}

	public String getFolder() {
		return folder;
	}

	public String getFilename() {
		return filename;
	}

	public String getExtension() {
		return extension;
	}
}
