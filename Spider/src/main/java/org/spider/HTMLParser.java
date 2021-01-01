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

package org.spider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLParser {

	private static final Logger log = LoggerFactory.getLogger(HTMLParser.class);

	private static final Pattern commentPattern = Pattern.compile("<!--(.*?)-->");

	private Document doc;

	private String author;
	private String title;
	private String description;
	private String keywords;
	private String language;
	private String redirect;
	private ArrayList<String> paths;

	public HTMLParser() {
	}

	public void parseStream(InputStream stream) throws IOException {
		author = "";
		title = "";
		description = "";
		keywords = "";
		language = "";
		redirect = "";
		paths = new ArrayList<>();

		doc = Jsoup.parse(stream, null, "");

		title = filter(doc.title());

		Element html = doc.select("html").first();
		if (html != null) {
			language = html.attr("lang");
			if (html.hasAttr("xml:lang")) {
				language = html.attr("xml:lang");
			}
		}

		Elements metaTags = doc.getElementsByTag("meta");
		for (Element metaTag : metaTags) {
			String content = metaTag.attr("content");
			String name = metaTag.attr("name");
			String nameAlternative = metaTag.attr("http-equiv");
			if (name.isEmpty() && !nameAlternative.isEmpty()) {
				name = nameAlternative;
			}
			if ("author".equalsIgnoreCase(name)) {
				author = content;
			}
			if ("description".equalsIgnoreCase(name)) {
				description = content;
			}
			if ("keywords".equalsIgnoreCase(name)) {
				keywords = content;
			}
			if (language.isEmpty() && "Content-Language".equalsIgnoreCase(name)) {
				language = content;
			}
			if ("refresh".equalsIgnoreCase(name)) {
				String[] redirectParts = content.split("=");
				if (redirectParts.length > 1) {
					redirect = redirectParts[1].trim();
				}
			}
		}

		Elements links = doc.select("a[href]");
		for (Element link : links) {
			String currentLink = link.attr("href");
			log.debug("Found link: {}", currentLink);
			paths.add(currentLink);
		}
		Elements frames = doc.select("frame[src]");
		for (Element frame : frames) {
			String currentFrame = frame.attr("src");
			log.debug("Found frame: {}", currentFrame);
			paths.add(currentFrame);
		}
		Elements iframes = doc.select("iframe[src]");
		for (Element frame : iframes) {
			String currentFrame = frame.attr("src");
			log.debug("Found iframe: {}", currentFrame);
			paths.add(currentFrame);
		}
	}

	private String filter(String value) {
		// Freenet.HTMLFilter adds warnings in comments like "deleted unknown style".
		return commentPattern.matcher(value).replaceAll("");
	}

	public String getAuthor() {
		return author;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getKeywords() {
		return keywords;
	}

	public String getLanguage() {
		return language;
	}

	public String getRedirect() {
		return redirect;
	}

	public ArrayList<String> getPaths() {
		return paths;
	}

	public static Boolean isIgnored(String url) {
		Boolean result = false;
		Key key = new Key(url);

		// " (quote) -> probably invalid html
		if (url.startsWith("/?newbookmark") || url.startsWith("/external-link/") || url.startsWith("/Sone/")
				|| key.isCHK() || key.isKSK() || key.isSSK() || (!key.isKey() && key.getPath().isEmpty())
				|| (!key.getPath().isEmpty() && !key.getPath().contains(".htm")) || key.getPath().contains("\"")) {
			result = true;
		}
		return result;
	}
}
