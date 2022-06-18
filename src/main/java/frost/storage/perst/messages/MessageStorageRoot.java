/*
  MessageStorageRoot.java / Frost
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

import com.mcobject.perst.Index;
import com.mcobject.perst.Persistent;
import com.mcobject.perst.Storage;

public class MessageStorageRoot extends Persistent {

	private Index<PerstFrostBoardObject> boardsByName;
	private Index<PerstFrostBoardObject> boardsById;

	private Index<PerstIdentitiesMessages> identitiesMessages;

	public MessageStorageRoot(final Storage storage) {
		boardsByName = storage.createIndex(String.class, true);
		boardsById = storage.createIndex(int.class, true);

		identitiesMessages = storage.createIndex(String.class, true);
	}

	public Index<PerstFrostBoardObject> getBoardsByName() {
		return boardsByName;
	}

	public Index<PerstFrostBoardObject> getBoardsById() {
		return boardsById;
	}

	public Index<PerstIdentitiesMessages> getIdentitiesMessages() {
		return identitiesMessages;
	}
}
