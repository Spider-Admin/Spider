/*
  PerstFrostMessageArchiveObject.java / Frost
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

import com.mcobject.perst.Link;
import com.mcobject.perst.Persistent;
import com.mcobject.perst.Storage;

public class PerstFrostArchiveMessageObject extends Persistent {

	private String messageId;
	private String inReplyTo;

	private long dateAndTime;
	private int msgIndex;

	private String fromName;

	private String subject;
	private String recipientName;
	private int signatureStatus;

	private boolean isReplied;
	private boolean isFlagged;
	private boolean isStarred;

	private int idLinePos;
	private int idLineLen;

	private String content;
	private String publicKey;

	private Link<PerstFrostArchiveBoardAttachment> boardAttachments;
	private Link<PerstFrostArchiveFileAttachment> fileAttachments;

	public PerstFrostArchiveMessageObject(Storage storage, String messageId, String inReplyTo, long dateAndTime,
			int msgIndex, String fromName, String subject, String recipientName, int signatureStatus, boolean isReplied,
			boolean isFlagged, boolean isStarred, int idLinePos, int idLineLen, String content, String publicKey) {
		this.messageId = messageId;
		this.inReplyTo = inReplyTo;
		this.dateAndTime = dateAndTime;
		this.msgIndex = msgIndex;
		this.fromName = fromName;
		this.subject = subject;
		this.recipientName = recipientName;
		this.signatureStatus = signatureStatus;
		this.isReplied = isReplied;
		this.isFlagged = isFlagged;
		this.isStarred = isStarred;
		this.idLinePos = idLinePos;
		this.idLineLen = idLineLen;
		this.content = content;
		this.publicKey = publicKey;
		this.boardAttachments = storage.createLink();
		this.fileAttachments = storage.createLink();
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

	public boolean isReplied() {
		return isReplied;
	}

	public boolean isFlagged() {
		return isFlagged;
	}

	public boolean isStarred() {
		return isStarred;
	}

	public int getIdLinePos() {
		return idLinePos;
	}

	public int getIdLineLen() {
		return idLineLen;
	}

	public String getContent() {
		return content;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public Link<PerstFrostArchiveBoardAttachment> getBoardAttachments() {
		return boardAttachments;
	}

	public Link<PerstFrostArchiveFileAttachment> getFileAttachments() {
		return fileAttachments;
	}

	@Override
	public boolean recursiveLoading() {
		return false;
	}
}
