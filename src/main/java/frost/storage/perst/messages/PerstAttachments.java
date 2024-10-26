/*
  PerstAttachments.java / Frost
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

import java.util.Iterator;

import com.mcobject.perst.IPersistentList;
import com.mcobject.perst.Persistent;
import com.mcobject.perst.Storage;

public class PerstAttachments extends Persistent {

	private static final long serialVersionUID = 1L;

	private IPersistentList<PerstBoardAttachment> boardAttachments;
	private IPersistentList<PerstFileAttachment> fileAttachments;

	public PerstAttachments(Storage storage, IPersistentList<PerstBoardAttachment> otherBoardAttachments,
			IPersistentList<PerstFileAttachment> otherFileAttachments) {
		if (otherBoardAttachments != null && otherBoardAttachments.size() > 0) {
			this.boardAttachments = storage.createScalableList();
			Iterator<PerstBoardAttachment> attachmentIt = otherBoardAttachments.iterator();
			while (attachmentIt.hasNext()) {
				PerstBoardAttachment currentAttachment = attachmentIt.next();
				PerstBoardAttachment newtAttachment = new PerstBoardAttachment(currentAttachment.getName(),
						currentAttachment.getPubKey(), currentAttachment.getPrivKey(),
						currentAttachment.getDescription());
				boardAttachments.add(newtAttachment);
			}
		}
		if (otherFileAttachments != null && otherFileAttachments.size() > 0) {
			this.fileAttachments = storage.createScalableList();
			Iterator<PerstFileAttachment> attachmentIt = otherFileAttachments.iterator();
			while (attachmentIt.hasNext()) {
				PerstFileAttachment currentAttachment = attachmentIt.next();
				PerstFileAttachment newtAttachment = new PerstFileAttachment(currentAttachment.getName(),
						currentAttachment.getSize(), currentAttachment.getCHKKey());
				fileAttachments.add(newtAttachment);
			}
		}
	}

	public IPersistentList<PerstBoardAttachment> getBoardAttachments() {
		return boardAttachments;
	}

	public IPersistentList<PerstFileAttachment> getFileAttachments() {
		return fileAttachments;
	}
}
