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

package org.spider.network;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.spider.Settings;

import net.pterodactylus.fcp.highlevel.FcpClient;
import net.pterodactylus.fcp.highlevel.FcpException;

public class Freenet {

	// @see freenet.client.FetchException
	// - isFatal
	// - FetchExceptionMode
	// @see
	// https://github.com/freenet/fred/blob/master/src/freenet/l10n/freenet.l10n.en.properties
	public enum Error {
		// @see freenet.client.MetadataParseException
		INVALID_METADATA(4, "MetadataParseException"),

		// @see freenet.client.ArchiveFailureException
		ARCHIVE_FAILURE(5, "ArchiveFailureException"),

		TOO_MUCH_RECURSION(9, "Too much recursion"),

		NOT_IN_ARCHIVE(10, "Not in archive"),

		// TODO Freenet: Redirect in Fred, but error in FCPLib?
		TOO_MANY_PATH_COMPONENTS(11, "Too many path components"),

		DATA_NOT_FOUND(13, null),

		ROUTE_NOT_FOUND(14, null),

		REJECTED_OVERLOAD(15, null),

		TRANSFER_FAILED(18, null),

		SPLITFILE_ERROR(19, null),

		// @see java.net.MalformedURLException
		INVALID_URI(20, "MalformedURLException"),

		ALL_DATA_NOT_FOUND(28, null),

		RECENTLY_FAILED(30, null),

		// @see freenet.client.filter.UnsafeContentTypeException
		// @see freenet.client.filter.UnknownCharsetException
		CONTENT_VALIDATION_FAILED(31, "Corrupt or malicious web page"),

		// @see freenet.client.filter.UnknownContentTypeException
		CONTENT_VALIDATION_UNKNOWN_MIME(32, "Unknown and potentially dangerous content type"),

		FAKE_KEY(null, "Fake-Key");

		private String name;
		private Integer code;

		private Error(Integer code, String name) {
			this.code = code;
			this.name = name;
		}

		public String toString() {
			return name;
		}

		public Integer getCode() {
			return code;
		}

		public static String getMessage(Integer code) {
			for (Error value : values()) {
				if (value.code != null && value.code.equals(code)) {
					return value.toString();
				}
			}
			return "Error: " + code;
		}
	}

	private static String getClientName() {
		Settings settings = Settings.getInstance();
		return String.format("%s-%d", settings.getString(Settings.INDEX_NAME), ThreadLocalRandom.current().nextInt());
	}

	public static FcpClient getConnection() throws IOException, FcpException {
		Settings settings = Settings.getInstance();

		FcpClient connection = new FcpClient(settings.getString(Settings.FREENET_HOST),
				settings.getInteger(Settings.FREENET_PORT_FCP));
		connection.connect(getClientName());
		return connection;
	}
}
