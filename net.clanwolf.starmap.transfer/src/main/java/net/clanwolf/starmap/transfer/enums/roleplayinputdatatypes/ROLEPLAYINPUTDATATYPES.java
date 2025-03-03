/* ---------------------------------------------------------------- |
 *    ____ _____                                                    |
 *   / ___|___ /                   Communicate - Command - Control  |
 *  | |     |_ \                   MK V "Cerberus"                  |
 *  | |___ ___) |                                                   |
 *   \____|____/                                                    |
 *                                                                  |
 * ---------------------------------------------------------------- |
 * Info        : https://www.clanwolf.net                           |
 * GitHub      : https://github.com/ClanWolf                        |
 * ---------------------------------------------------------------- |
 * Licensed under the Apache License, Version 2.0 (the "License");  |
 * you may not use this file except in compliance with the License. |
 * You may obtain a copy of the License at                          |
 * http://www.apache.org/licenses/LICENSE-2.0                       |
 *                                                                  |
 * Unless required by applicable law or agreed to in writing,       |
 * software distributed under the License is distributed on an "AS  |
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either  |
 * express or implied. See the License for the specific language    |
 * governing permissions and limitations under the License.         |
 *                                                                  |
 * C3 includes libraries and source code by various authors.        |
 * Copyright (c) 2001-2022, ClanWolf.net                            |
 * ---------------------------------------------------------------- |
 */
package net.clanwolf.starmap.transfer.enums.roleplayinputdatatypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.clanwolf.starmap.transfer.enums.catalogObjects.CHAR_Bloodhouse;
import net.clanwolf.starmap.transfer.enums.DATATYPES;
import net.clanwolf.starmap.transfer.enums.catalogObjects.ICatalogObject;
import net.clanwolf.starmap.transfer.util.CatalogLoader;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

//	System.out.println(Internationalization.getString(CHARACTER.CHARNAME.labelkey));

public enum ROLEPLAYINPUTDATATYPES {
	// CHARACTER
	CHARNAME("charname", DATATYPES.String, ROLEPLAYOBJECTTYPES.CHARACTER, null, false),
	LASTNAME("lastname", DATATYPES.String, ROLEPLAYOBJECTTYPES.CHARACTER, null, false),
	BIRTHDATE("birthdate", DATATYPES.Date, ROLEPLAYOBJECTTYPES.CHARACTER, null, false),
	BLOODHOUSE_SINGLE("bloodhouse", DATATYPES.SelectionSingle, ROLEPLAYOBJECTTYPES.CHARACTER, "CHAR_Bloodhouse", false),
	BLOODHOUSE_MULTI("bloodhouse", DATATYPES.SelectionMulti, ROLEPLAYOBJECTTYPES.CHARACTER, "CHAR_Bloodhouse", false),

	// DROPSHIP
	SHIPNAME("shipname", DATATYPES.String, ROLEPLAYOBJECTTYPES.DROPSHIP, null, false),
	SIZE("size", DATATYPES.String, ROLEPLAYOBJECTTYPES.DROPSHIP, null, false);

	public final String labelkey;
	public final DATATYPES datatype;
	public final ROLEPLAYOBJECTTYPES types;
	public final String classname;
	public final boolean mandatory;

	ROLEPLAYINPUTDATATYPES(String labelkey, DATATYPES datatype, ROLEPLAYOBJECTTYPES types, String classname, boolean mandatory) {
		this.labelkey = labelkey;
		this.datatype = datatype;
		this.types = types;
		this.classname = classname;
		this.mandatory = mandatory;
	}

	public ICatalogObject[] getList() throws Exception {
		if (classname != null) {
			return CatalogLoader.getList(classname);
		}
		return null;
	}

	@Override
	public String toString() {
		return "app_rp_storyeditor_roleplayinputdatatypes_" + ROLEPLAYOBJECTTYPES.CHARACTER + "_" + labelkey;
	}

}
