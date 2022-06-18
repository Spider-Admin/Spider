/*
  PerstFrostBoardObject.java / Frost
  Copyright (C) 2007  Frost Project <jtcfrost.sourceforge.net>

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 2 of
  the License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package frost.storage.perst.messages;

import com.mcobject.perst.IPersistentList;
import com.mcobject.perst.Index;
import com.mcobject.perst.Persistent;
import com.mcobject.perst.Storage;

/**
 * Relation between a board name and a unique number for this board.
 */
public class PerstFrostBoardObject extends Persistent {

	private String boardName;
	private int boardId;

	private Index<PerstFrostMessageObject> messageIndex; // key is msgdatetime; only valid msgs
	private Index<PerstFrostMessageObject> messageIdIndex; // key is messageId, only valid msgs

	// key is msgdatetime; only valid msgs; only unread msgs; msgs are in
	// messageIndex too!
	private Index<PerstFrostMessageObject> unreadMessageIndex;

	// key is msgdatetime; only valid msgs; only flagged or starred msgs; msgs are
	// in messageIndex too!
	private Index<PerstFrostMessageObject> flaggedMessageIndex;
	private Index<PerstFrostMessageObject> starredMessageIndex;

	private Index<PerstFrostMessageObject> invalidMessagesIndex; // key is msgdatetime; only invalid msgs if stored

	private IPersistentList<PerstFrostMessageObject> sentMessagesList;
	private IPersistentList<PerstFrostUnsentMessageObject> unsentMessagesList;
	private IPersistentList<PerstFrostUnsentMessageObject> draftMessagesList;

	public PerstFrostBoardObject(Storage storage, String boardName, int boardId) {
		this.boardName = boardName;
		this.boardId = boardId;

		// index of msgDateTime
		messageIndex = storage.createIndex(long.class, false);
		invalidMessagesIndex = storage.createIndex(long.class, false);

		unreadMessageIndex = storage.createIndex(long.class, false);
		flaggedMessageIndex = storage.createIndex(long.class, false);
		starredMessageIndex = storage.createIndex(long.class, false);

		// index of unique message ids, messages without messageId are NOT in this index
		messageIdIndex = storage.createIndex(String.class, true);

		sentMessagesList = storage.createScalableList();
		unsentMessagesList = storage.createScalableList();
		draftMessagesList = storage.createScalableList();
	}

	public String getBoardName() {
		return boardName;
	}

	public int getBoardId() {
		return boardId;
	}

	/**
	 * compound index of boardId and msgDateTimeindexName
	 */
	public Index<PerstFrostMessageObject> getMessageIndex() {
		return messageIndex;
	}

	/**
	 * compound index of boardId and msgDateTimeindexName
	 */
	public Index<PerstFrostMessageObject> getUnreadMessageIndex() {
		return unreadMessageIndex;
	}

	public Index<PerstFrostMessageObject> getFlaggedMessageIndex() {
		return flaggedMessageIndex;
	}

	public Index<PerstFrostMessageObject> getStarredMessageIndex() {
		return starredMessageIndex;
	}

	/**
	 * compound index of boardId and msgDateTimeindexName
	 */
	public Index<PerstFrostMessageObject> getInvalidMessagesIndex() {
		return invalidMessagesIndex;
	}

	/**
	 * index of unique message ids, messages without messageId are NOT in this index
	 */
	public Index<PerstFrostMessageObject> getMessageIdIndex() {
		return messageIdIndex;
	}

	public IPersistentList<PerstFrostMessageObject> getSentMessagesList() {
		return sentMessagesList;
	}

	public IPersistentList<PerstFrostUnsentMessageObject> getUnsentMessagesList() {
		return unsentMessagesList;
	}

	public IPersistentList<PerstFrostUnsentMessageObject> getDraftMessagesList() {
		return draftMessagesList;
	}
}
