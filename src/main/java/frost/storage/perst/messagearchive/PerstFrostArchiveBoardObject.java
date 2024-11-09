/*
  PerstFrostBoardArchiveObject.java / Frost
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
package frost.storage.perst.messagearchive;

import com.mcobject.perst.Index;
import com.mcobject.perst.Persistent;
import com.mcobject.perst.Storage;

public class PerstFrostArchiveBoardObject extends Persistent {

	private static final long serialVersionUID = 1L;

	private String boardName;

	private Index<PerstFrostArchiveMessageObject> messageIndex;
	private Index<PerstFrostArchiveMessageObject> messageIdIndex;

	public PerstFrostArchiveBoardObject() {
	}

	public PerstFrostArchiveBoardObject(Storage storage, String name) {
		boardName = name;

		// index of msgDateTime
		messageIndex = storage.createIndex(long.class, false);

		// index of unique message ids, messages without messageId are NOT in this index
		messageIdIndex = storage.createIndex(String.class, true);
	}

	public String getBoardName() {
		return boardName;
	}

	public Index<PerstFrostArchiveMessageObject> getMessageIndex() {
		return messageIndex;
	}

	public Index<PerstFrostArchiveMessageObject> getMessageIdIndex() {
		return messageIdIndex;
	}
}
