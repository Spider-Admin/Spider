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

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spider.Spider;

import net.pterodactylus.fcp.AllData;
import net.pterodactylus.fcp.CloseConnectionDuplicateClientName;
import net.pterodactylus.fcp.ConfigData;
import net.pterodactylus.fcp.DataFound;
import net.pterodactylus.fcp.EndListPeerNotes;
import net.pterodactylus.fcp.EndListPeers;
import net.pterodactylus.fcp.EndListPersistentRequests;
import net.pterodactylus.fcp.FCPPluginReply;
import net.pterodactylus.fcp.FcpConnection;
import net.pterodactylus.fcp.FcpListener;
import net.pterodactylus.fcp.FcpMessage;
import net.pterodactylus.fcp.FinishedCompression;
import net.pterodactylus.fcp.GetFailed;
import net.pterodactylus.fcp.IdentifierCollision;
import net.pterodactylus.fcp.NodeData;
import net.pterodactylus.fcp.NodeHello;
import net.pterodactylus.fcp.Peer;
import net.pterodactylus.fcp.PeerNote;
import net.pterodactylus.fcp.PeerRemoved;
import net.pterodactylus.fcp.PersistentGet;
import net.pterodactylus.fcp.PersistentPut;
import net.pterodactylus.fcp.PersistentPutDir;
import net.pterodactylus.fcp.PersistentRequestModified;
import net.pterodactylus.fcp.PersistentRequestRemoved;
import net.pterodactylus.fcp.PluginInfo;
import net.pterodactylus.fcp.PluginRemoved;
import net.pterodactylus.fcp.ProtocolError;
import net.pterodactylus.fcp.PutFailed;
import net.pterodactylus.fcp.PutFetchable;
import net.pterodactylus.fcp.PutSuccessful;
import net.pterodactylus.fcp.ReceivedBookmarkFeed;
import net.pterodactylus.fcp.SSKKeypair;
import net.pterodactylus.fcp.SentFeed;
import net.pterodactylus.fcp.SimpleProgress;
import net.pterodactylus.fcp.StartedCompression;
import net.pterodactylus.fcp.SubscribedUSK;
import net.pterodactylus.fcp.SubscribedUSKUpdate;
import net.pterodactylus.fcp.TestDDAComplete;
import net.pterodactylus.fcp.TestDDAReply;
import net.pterodactylus.fcp.URIGenerated;
import net.pterodactylus.fcp.UnknownNodeIdentifier;
import net.pterodactylus.fcp.UnknownPeerNoteType;

public class USKListener implements FcpListener, AutoCloseable {

	private Connection connection;

	public USKListener(Connection connection) {
		this.connection = connection;
	}

	private static final Logger log = LoggerFactory.getLogger(USKListener.class);

	@Override
	public void receivedNodeHello(FcpConnection fcpConnection, NodeHello nodeHello) {
	}

	@Override
	public void receivedCloseConnectionDuplicateClientName(FcpConnection fcpConnection,
			CloseConnectionDuplicateClientName closeConnectionDuplicateClientName) {
	}

	@Override
	public void receivedSSKKeypair(FcpConnection fcpConnection, SSKKeypair sskKeypair) {
	}

	@Override
	public void receivedPeer(FcpConnection fcpConnection, Peer peer) {
	}

	@Override
	public void receivedEndListPeers(FcpConnection fcpConnection, EndListPeers endListPeers) {
	}

	@Override
	public void receivedPeerNote(FcpConnection fcpConnection, PeerNote peerNote) {
	}

	@Override
	public void receivedEndListPeerNotes(FcpConnection fcpConnection, EndListPeerNotes endListPeerNotes) {
	}

	@Override
	public void receivedPeerRemoved(FcpConnection fcpConnection, PeerRemoved peerRemoved) {
	}

	@Override
	public void receivedNodeData(FcpConnection fcpConnection, NodeData nodeData) {
	}

	@Override
	public void receivedTestDDAReply(FcpConnection fcpConnection, TestDDAReply testDDAReply) {
	}

	@Override
	public void receivedTestDDAComplete(FcpConnection fcpConnection, TestDDAComplete testDDAComplete) {
	}

	@Override
	public void receivedPersistentGet(FcpConnection fcpConnection, PersistentGet persistentGet) {
	}

	@Override
	public void receivedPersistentPut(FcpConnection fcpConnection, PersistentPut persistentPut) {
	}

	@Override
	public void receivedEndListPersistentRequests(FcpConnection fcpConnection,
			EndListPersistentRequests endListPersistentRequests) {
	}

	@Override
	public void receivedURIGenerated(FcpConnection fcpConnection, URIGenerated uriGenerated) {
	}

	@Override
	public void receivedDataFound(FcpConnection fcpConnection, DataFound dataFound) {
	}

	@Override
	public void receivedAllData(FcpConnection fcpConnection, AllData allData) {
	}

	@Override
	public void receivedSimpleProgress(FcpConnection fcpConnection, SimpleProgress simpleProgress) {
	}

	@Override
	public void receivedStartedCompression(FcpConnection fcpConnection, StartedCompression startedCompression) {
	}

	@Override
	public void receivedFinishedCompression(FcpConnection fcpConnection, FinishedCompression finishedCompression) {
	}

	@Override
	public void receivedUnknownPeerNoteType(FcpConnection fcpConnection, UnknownPeerNoteType unknownPeerNoteType) {
	}

	@Override
	public void receivedUnknownNodeIdentifier(FcpConnection fcpConnection,
			UnknownNodeIdentifier unknownNodeIdentifier) {
	}

	@Override
	public void receivedConfigData(FcpConnection fcpConnection, ConfigData configData) {
	}

	@Override
	public void receivedGetFailed(FcpConnection fcpConnection, GetFailed getFailed) {
	}

	@Override
	public void receivedPutFailed(FcpConnection fcpConnection, PutFailed putFailed) {
	}

	@Override
	public void receivedIdentifierCollision(FcpConnection fcpConnection, IdentifierCollision identifierCollision) {
	}

	@Override
	public void receivedPersistentPutDir(FcpConnection fcpConnection, PersistentPutDir persistentPutDir) {
	}

	@Override
	public void receivedPersistentRequestRemoved(FcpConnection fcpConnection,
			PersistentRequestRemoved persistentRequestRemoved) {
	}

	@Override
	public void receivedSubscribedUSK(FcpConnection fcpConnection, SubscribedUSK subscribedUSK) {
		log.debug("Subscribed for {}", subscribedUSK.getURI());
	}

	@Override
	public void receivedSubscribedUSKUpdate(FcpConnection fcpConnection, SubscribedUSKUpdate subscribedUSKUpdate) {
		log.debug("Check: Update edition of {} to {}", subscribedUSKUpdate.getURI(), subscribedUSKUpdate.getEdition());

		try (Spider spider = new Spider(connection);) {
			spider.updateFreesiteEdition(spider.decodeURL(subscribedUSKUpdate.getURI()), false);
			connection.commit();
		} catch (SQLException e) {
			log.error("Database-Error!", e);
		}
	}

	@Override
	public void receivedPluginInfo(FcpConnection fcpConnection, PluginInfo pluginInfo) {
	}

	@Override
	public void receivedPluginRemoved(FcpConnection fcpConnection, PluginRemoved pluginRemoved) {
	}

	@Override
	public void receivedFCPPluginReply(FcpConnection fcpConnection, FCPPluginReply fcpPluginReply) {
	}

	@Override
	public void receivedPersistentRequestModified(FcpConnection fcpConnection,
			PersistentRequestModified persistentRequestModified) {
	}

	@Override
	public void receivedPutSuccessful(FcpConnection fcpConnection, PutSuccessful putSuccessful) {
	}

	@Override
	public void receivedPutFetchable(FcpConnection fcpConnection, PutFetchable putFetchable) {
	}

	@Override
	public void receivedSentFeed(FcpConnection source, SentFeed sentFeed) {
	}

	@Override
	public void receivedBookmarkFeed(FcpConnection fcpConnection, ReceivedBookmarkFeed receivedBookmarkFeed) {
	}

	@Override
	public void receivedProtocolError(FcpConnection fcpConnection, ProtocolError protocolError) {
	}

	@Override
	public void receivedMessage(FcpConnection fcpConnection, FcpMessage fcpMessage) {
	}

	@Override
	public void connectionClosed(FcpConnection fcpConnection, Throwable throwable) {
	}

	@Override
	public void close() throws SQLException {
	}
}
