/*
  Copyright 2020 - 2022 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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
import java.util.Base64;
import java.util.Base64.Encoder;
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

		DATA_NOT_FOUND(13, ""),

		ROUTE_NOT_FOUND(14, ""),

		REJECTED_OVERLOAD(15, ""),

		TRANSFER_FAILED(18, ""),

		SPLITFILE_ERROR(19, ""),

		// @see java.net.MalformedURLException
		INVALID_URI(20, "MalformedURLException"),

		ALL_DATA_NOT_FOUND(28, ""),

		RECENTLY_FAILED(30, ""),

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

		try {
			FcpClient connection = new FcpClient(settings.getString(Settings.FREENET_HOST),
					settings.getInteger(Settings.FREENET_PORT_FCP));
			connection.connect(getClientName());
			return connection;
		} catch (NumberFormatException e) {
			throw new FcpException(String.format("Invalid port \"%s\"!", settings.getString(Settings.FREENET_PORT_FCP)),
					e);
		}
	}

	public static Integer getMinUSKKeyLength() {
		// @see freenet.keys.FreenetURI
		// FreenetURI#toString(boolean, boolean)

		// @see freenet.keys.FreenetURI
		// @see freenet.keys.NodeSSK
		// RoutingKey is the pkHash. The actual routing key is calculated in NodeSSK.
		// PUBKEY_HASH_SIZE = 32
		byte[] routingKey = new byte[32];

		// @see freenet.keys.FreenetURI
		// Crypto key should be 32 bytes.
		byte[] cryptoKey = new byte[32];

		// @see freenet.keys.FreenetURI
		// @see freenet.keys.ClientSSK
		// EXTRA_LENGTH = 5;
		byte[] extra = new byte[5];

		// Minimum is one letter (according to jSite)
		String siteName = "s";

		Encoder base64 = Base64.getUrlEncoder().withoutPadding();

		String minKey = "USK" + "@" + base64.encodeToString(routingKey) + "," + base64.encodeToString(cryptoKey) + ","
				+ base64.encodeToString(extra) + "/" + siteName;
		return minKey.length();
	}
}
