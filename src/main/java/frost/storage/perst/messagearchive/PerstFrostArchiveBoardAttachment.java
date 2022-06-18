/*
  PerstFrostArchiveBoardAttachment.java / Frost
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

import com.mcobject.perst.Persistent;

public class PerstFrostArchiveBoardAttachment extends Persistent {

	private String name;
	private String pubKey;
	private String privKey;
	private String description;

	public PerstFrostArchiveBoardAttachment(String name, String pubKey, String privKey, String description) {
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