/*
  PerstFrostMessageObject.java / Frost
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

import com.mcobject.perst.Persistent;

/**
 * Holds all necessary data for a FrostMessageObject and allows to be stored in
 * a perst Storage.
 */
public class PerstFrostMessageObject extends Persistent {

	private static final long serialVersionUID = 1L;

	private String messageId;
	private String inReplyTo;

	private long dateAndTime;
	private int msgIndex;

	private String invalidReason; // if set, the message is invalid

	private String fromName;

	private String subject;
	private String recipientName;
	private int signatureStatus;

	private boolean isDeleted;
	private boolean isNew;
	private boolean isReplied;
	private boolean isJunk;
	private boolean isFlagged;
	private boolean isStarred;

	private boolean hasBoardAttachments;
	private boolean hasFileAttachments;

	private int idLinePos;
	private int idLineLen;

	public PerstFrostMessageObject(String messageId, String inReplyTo, long dateAndTime, int msgIndex,
			String invalidReason, String fromName, String subject, String recipientName, int signatureStatus,
			boolean isDeleted, boolean isNew, boolean isReplied, boolean isJunk, boolean isFlagged, boolean isStarred,
			boolean hasBoardAttachments, boolean hasFileAttachments, int idLinePos, int idLineLen) {
		this.messageId = messageId;
		this.inReplyTo = inReplyTo;
		this.dateAndTime = dateAndTime;
		this.msgIndex = msgIndex;
		this.invalidReason = invalidReason;
		this.fromName = fromName;
		this.subject = subject;
		this.recipientName = recipientName;
		this.signatureStatus = signatureStatus;
		this.isDeleted = isDeleted;
		this.isNew = isNew;
		this.isReplied = isReplied;
		this.isJunk = isJunk;
		this.isFlagged = isFlagged;
		this.isStarred = isStarred;
		this.hasBoardAttachments = hasBoardAttachments;
		this.hasFileAttachments = hasFileAttachments;
		this.idLinePos = idLinePos;
		this.idLineLen = idLineLen;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public long getDateAndTime() {
		return dateAndTime;
	}

	public int getMsgIndex() {
		return msgIndex;
	}

	public String getInvalidReason() {
		return invalidReason;
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

	public int getSignatureStatus() {
		return signatureStatus;
	}

	public boolean isMsgDeleted() {
		return isDeleted;
	}

	public boolean isNew() {
		return isNew;
	}

	public boolean isReplied() {
		return isReplied;
	}

	public boolean isJunk() {
		return isJunk;
	}

	public boolean isFlagged() {
		return isFlagged;
	}

	public boolean isStarred() {
		return isStarred;
	}

	public boolean isHasBoardAttachments() {
		return hasBoardAttachments;
	}

	public boolean isHasFileAttachments() {
		return hasFileAttachments;
	}

	public int getIdLinePos() {
		return idLinePos;
	}

	public int getIdLineLen() {
		return idLineLen;
	}

	@Override
	public boolean recursiveLoading() {
		return false;
	}
}
