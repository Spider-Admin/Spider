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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SoneParser {

	private DocumentBuilder builder;
	private List<String> values;
	private List<String> texts;

	public SoneParser() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
	}

	public void reset() {
		values = null;
		texts = null;
	}

	public void parseStream(InputStream stream) throws SAXException, IOException {
		reset();
		Document doc = builder.parse(stream);
		doc.getDocumentElement().normalize();

		values = getContentFromTag(doc, "field-value");
		texts = getContentFromTag(doc, "text");
	}

	private List<String> getContentFromTag(Document document, String tag) {
		List<String> content = new ArrayList<>();
		NodeList elements = document.getElementsByTagName(tag);
		for (int i = 0; i < elements.getLength(); i = i + 1) {
			Node node = elements.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				content.add(element.getTextContent());
			}
		}
		return content;
	}

	public List<String> getValues() {
		return values;
	}

	public List<String> getTexts() {
		return texts;
	}
}
