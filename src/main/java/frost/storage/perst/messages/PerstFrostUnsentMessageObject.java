/*
  PerstFrostUnsentMessageObject.java / Frost
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

import com.mcobject.perst.Link;
import com.mcobject.perst.Persistent;
import com.mcobject.perst.Storage;

public class PerstFrostUnsentMessageObject extends Persistent {

	private static final long serialVersionUID = 1L;

	private String messageId;
	private String inReplyTo;

	private String fromName;

	private String subject;
	private String recipientName;

	private int idLinePos;
	private int idLineLen;

	private transient Link<PerstFrostUnsentBoardAttachment> boardAttachments;
	private transient Link<PerstFrostUnsentFileAttachment> fileAttachments;

	private String content;

	private long timeAdded;

	private long sendAfterTime;

	public PerstFrostUnsentMessageObject() {
	}

	public PerstFrostUnsentMessageObject(Storage storage, String messageId, String inReplyTo, String fromName,
			String subject, String recipientName, int idLinePos, int idLineLen, String content, long timeAdded,
			long sendAfterTime) {
		this.messageId = messageId;
		this.inReplyTo = inReplyTo;
		this.fromName = fromName;
		this.subject = subject;
		this.recipientName = recipientName;
		this.idLinePos = idLinePos;
		this.idLineLen = idLineLen;
		this.boardAttachments = storage.createLink();
		this.fileAttachments = storage.createLink();
		this.content = content;
		this.timeAdded = timeAdded;
		this.sendAfterTime = sendAfterTime;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public String getFromName() {
		return fromName;
	}

	public String getSubject() {
		return subject;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public int getIdLinePos() {
		return idLinePos;
	}

	public int getIdLineLen() {
		return idLineLen;
	}

	public Link<PerstFrostUnsentBoardAttachment> getBoardAttachments() {
		return boardAttachments;
	}

	public Link<PerstFrostUnsentFileAttachment> getFileAttachments() {
		return fileAttachments;
	}

	public String getContent() {
		return content;
	}

	public long getTimeAdded() {
		return timeAdded;
	}

	public long getSendAfterTime() {
		return sendAfterTime;
	}

	public class PerstFrostUnsentFileAttachment extends Persistent {

		private static final long serialVersionUID = 1L;

		private String name;
		private long size;
		private String chkKey;

		public PerstFrostUnsentFileAttachment() {
		}

		public PerstFrostUnsentFileAttachment(String name, long size, String chkKey) {
			this.name = name;
			this.size = size;
			this.chkKey = chkKey;
		}

		public String getName() {
			return name;
		}

		public long getSize() {
			return size;
		}

		public String getChkKey() {
			return chkKey;
		}
	}

	public class PerstFrostUnsentBoardAttachment extends Persistent {

		private static final long serialVersionUID = 1L;

		private String name;
		private String pubKey;
		private String privKey;
		private String description;

		public PerstFrostUnsentBoardAttachment() {
		}

		public PerstFrostUnsentBoardAttachment(String name, String pubKey, String privKey, String description) {
			this.name = name;
			this.pubKey = pubKey;
			this.privKey = privKey;
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public String getPubKey() {
			return pubKey;
		}

		public String getPrivKey() {
			return privKey;
		}

		public String getDescription() {
			return description;
		}
	}
}
