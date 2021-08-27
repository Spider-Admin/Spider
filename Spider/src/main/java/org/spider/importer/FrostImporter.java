/*
  Copyright 2021 Spider-Admin@Z+d9Knmjd3hQeeZU6BOWPpAAxxs

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

package org.spider.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spider.Settings;
import org.spider.Spider;

import com.mcobject.perst.Index;
import com.mcobject.perst.Storage;
import com.mcobject.perst.StorageError;
import com.mcobject.perst.StorageFactory;

import frost.storage.perst.PerstString;
import frost.storage.perst.messagearchive.ArchiveMessageStorageRoot;
import frost.storage.perst.messagearchive.PerstFrostArchiveBoardObject;
import frost.storage.perst.messagearchive.PerstFrostArchiveMessageObject;
import frost.storage.perst.messages.MessageContentStorageRoot;
import frost.storage.perst.messages.MessageStorageRoot;
import frost.storage.perst.messages.PerstFrostBoardObject;
import frost.storage.perst.messages.PerstFrostMessageObject;

public class FrostImporter extends Spider {

	private static final Logger log = LoggerFactory.getLogger(FrostImporter.class);

	private static final String STORE_PATH = "store";

	private static final String MESSAGE_FILE = "messages.dbs";
	private static final String MESSAGE_CONTENT_FILE = "messagesContents.dbs";
	private static final String MESSAGE_ARCHIVE_FILE = "messageArchive.dbs";

	public FrostImporter(Connection connection) throws SQLException {
		super(connection);
	}

	private void copyFileToTemp(String filename) throws IOException {
		String tempDir = System.getProperty("java.io.tmpdir");

		Path source = Paths.get(filename);
		Path destination = Paths.get(tempDir + source.getFileName());
		destination.toFile().deleteOnExit();

		log.info("Copy {} to temporary folder...", new File(filename).getName());
		Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
	}

	public void addFreesiteFromFrost() throws IOException, SQLException {
		log.info("Add freesites from Frost");

		String path = Settings.getInstance().getString(Settings.IMPORT_FROST_PATH);

		copyFileToTemp(path + STORE_PATH + File.separator + MESSAGE_FILE);
		copyFileToTemp(path + STORE_PATH + File.separator + MESSAGE_CONTENT_FILE);
		copyFileToTemp(path + STORE_PATH + File.separator + MESSAGE_ARCHIVE_FILE);

		String tempDir = System.getProperty("java.io.tmpdir");
		importMessages(tempDir + MESSAGE_FILE, tempDir + MESSAGE_CONTENT_FILE);
		importMessageArchive(tempDir + MESSAGE_ARCHIVE_FILE);
	}

	private void importMessages(String filenameMessages, String filenameMessageContents)
			throws IOException, SQLException {
		log.info("Load messages from {} and {}", filenameMessages, filenameMessageContents);
		Storage dbFrostArchive = StorageFactory.getInstance().createStorage();
		dbFrostArchive.open(filenameMessages, Storage.DEFAULT_PAGE_POOL_SIZE);
		MessageStorageRoot rootMessages = (MessageStorageRoot) dbFrostArchive.getRoot();
		if (rootMessages == null) {
			throw new IOException(String.format("\"%s\" contains no data!", filenameMessages));
		}

		Storage dbMessageContents = StorageFactory.getInstance().createStorage();
		dbMessageContents.open(filenameMessageContents, Storage.DEFAULT_PAGE_POOL_SIZE);

		MessageContentStorageRoot rootMessageContents = (MessageContentStorageRoot) dbMessageContents.getRoot();
		if (rootMessageContents == null) {
			throw new IOException(String.format("\"%s\" contains no data!", filenameMessageContents));
		}
		Index<PerstString> messageContents = rootMessageContents.getContentByMsgOid();
//		Index<PerstString> publicKeys = rootMessageContents.getPublickeyByMsgOid();
//		Index<PerstString> signatures = rootMessageContents.getSignatureByMsgOid();
//		Index<PerstAttachments> attachments = rootMessageContents.getAttachmentsByMsgOid();

		Index<PerstFrostBoardObject> boards = rootMessages.getBoardsByName();
		Iterator<PerstFrostBoardObject> boardIt = boards.iterator();
		while (boardIt.hasNext()) {
			PerstFrostBoardObject board = boardIt.next();
			log.info("Load messages from board {}...", board.getBoardName());

			Index<PerstFrostMessageObject> messages = board.getMessageIndex();
			Iterator<PerstFrostMessageObject> messageIt = messages.iterator();
			while (messageIt.hasNext()) {
				PerstFrostMessageObject message = messageIt.next();
				int oid = message.getOid();
				try {
					// Read message-content
					PerstString messageContent = messageContents.get(oid);
					addFreesiteFromString(messageContent.getValue());

					// Read public keys
//					PerstString publicKey = publicKeys.get(oid);
//					if (publicKey != null) {
//						log.info("Public Key: {}", publicKey.getValue());
//					}

					// Read signatures
//					PerstString signature = signatures.get(oid);
//					if (signature != null) {
//						log.info("Signature: {}", signature.getValue());
//					}

					// Read attached boards
//					PerstAttachments attachment = attachments.get(oid);
//					if (attachment.getBoardAttachments() != null) {
//						log.info("Board: {}", attachment.getBoardAttachments().get(0).getName());
//					}

					// Read attached files
//					if (attachment.getFileAttachments() != null) {
//						log.info("File: {}", attachment.getFileAttachments().get(0).getName());
//					}
				} catch (StorageError e) {
					if (e.getErrorCode() == StorageError.DELETED_OBJECT) {
						log.error("Deleted Message {} {} {}", oid, message.getSubject(),
								new Date(message.getDateAndTime()));
					} else {
						throw e;
					}
				}
			}
		}
		dbFrostArchive.close();
	}

	private void importMessageArchive(String filename) throws IOException, SQLException {
		log.info("Load messages from {}", filename);
		Storage dbArchive = StorageFactory.getInstance().createStorage();
		dbArchive.open(filename, Storage.DEFAULT_PAGE_POOL_SIZE);
		ArchiveMessageStorageRoot rootArchive = (ArchiveMessageStorageRoot) dbArchive.getRoot();
		if (rootArchive == null) {
			throw new IOException(String.format("\"%s\" contains no data!", filename));
		}

		Index<PerstFrostArchiveBoardObject> boards = rootArchive.getBoardsByName();
		Iterator<PerstFrostArchiveBoardObject> boardIt = boards.iterator();
		while (boardIt.hasNext()) {
			PerstFrostArchiveBoardObject board = boardIt.next();
			log.info("Load messages from board {}...", board.getBoardName());

			Index<PerstFrostArchiveMessageObject> messages = board.getMessageIndex();
			Iterator<PerstFrostArchiveMessageObject> messageIt = messages.iterator();
			while (messageIt.hasNext()) {
				// Read message
				PerstFrostArchiveMessageObject message = messageIt.next();
				addFreesiteFromString(message.getContent());

				// Read public key
				/*
				 * PublicKey is always null in archive
				 * 
				 * @see frost.storage.perst.messages.PerstFrostMessageObject
				 * PerstFrostMessageObject#toFrostMessageObject does not set content,
				 * attachments, publicKey.
				 * 
				 * @see frost.storage.perst.messagearchive.PerstFrostArchiveMessageObject
				 * PerstFrostArchiveMessageObject#PerstFrostArchiveMessageObject loads content
				 * and attachments dynamically. PublicKey is still missing.
				 */
//				log.info("PublicKey {}", message.getPublicKey());

				// Read attached boards
//				Link<PerstFrostArchiveBoardAttachment> boardsAtt = message.getBoardAttachments();
//				if (boardsAtt != null) {
//					Iterator<PerstFrostArchiveBoardAttachment> boardAttIt = boardsAtt.iterator();
//					while (boardAttIt.hasNext()) {
//						PerstFrostArchiveBoardAttachment boardAtt = boardAttIt.next();
//						log.info("Board: {}", boardAtt.getName());
//					}
//				}

				// Read attached files
//				Link<PerstFrostArchiveFileAttachment> files = message.getFileAttachments();
//				if (files != null) {
//					Iterator<PerstFrostArchiveFileAttachment> fileIt = files.iterator();
//					while (fileIt.hasNext()) {
//						PerstFrostArchiveFileAttachment file = fileIt.next();
//						log.info("File: {}", file.getName());
//					}
//				}
			}
		}
		dbArchive.close();
	}
}
