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

	// @see freenet.client.FetchException
	// - isFatal
	// - FetchExceptionMode
	// @see
	// https://github.com/freenet/fred/blob/master/src/freenet/l10n/freenet.l10n.en.properties
	public static String getErrorMessage(Integer errorCode) {
		String result = "Error: " + errorCode;
		switch (errorCode) {
		case 4: // INVALID_METADATA
			// @see freenet.client.MetadataParseException
			result = "MetadataParseException";
			break;
		case 5: // ARCHIVE_FAILURE
			// @see freenet.client.ArchiveFailureException
			result = "ArchiveFailureException";
			break;
		case 9: // TOO_MUCH_RECURSION
			result = "Too much recursion";
			break;
		case 10: // NOT_IN_ARCHIVE
			result = "Not in archive";
			break;
		case 11: // TOO_MANY_PATH_COMPONENTS
			// TODO Redirect in browser, but error in FCPLib?
			result = "Too many path components";
			break;
		case 13: // DATA_NOT_FOUND
			result = null;
			break;
		case 14: // ROUTE_NOT_FOUND
			result = null;
			break;
		case 15: // REJECTED_OVERLOAD
			result = null;
			break;
		case 18: // TRANSFER_FAILED
			result = null;
			break;
		case 19: // SPLITFILE_ERROR
			result = null;
			break;
		case 20: // INVALID_URI
			// @see java.net.MalformedURLException
			result = "MalformedURLException";
			break;
		case 28: // ALL_DATA_NOT_FOUND
			result = null;
			break;
		case 30: // RECENTLY_FAILED
			result = null;
			break;
		case 31: // CONTENT_VALIDATION_FAILED
			// @see freenet.client.filter.UnsafeContentTypeException
			// @see freenet.client.filter.UnknownCharsetException
			result = "Corrupt or malicious web page";
			break;
		case 32: // CONTENT_VALIDATION_UNKNOWN_MIME
			// @see freenet.client.filter.UnknownContentTypeException
			result = "Unknown and potentially dangerous content type";
			break;
		}
		return result;
	}
}
