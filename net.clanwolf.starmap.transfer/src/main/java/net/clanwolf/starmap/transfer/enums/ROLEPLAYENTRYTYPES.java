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
package net.clanwolf.starmap.transfer.enums;

/**
 * @author Undertaker
 */
public enum ROLEPLAYENTRYTYPES {
	C3_RP_STORY("Story"),

	C3_RP_CHAPTER("Chapter"),

	C3_RP_STEP_V1("Step (Normal Story step)"),

	C3_RP_STEP_V2("Step (Path selection) without Image"),

	C3_RP_STEP_V3("Step (Data input)"),

	C3_RP_STEP_V4("Step (Dice)"),

	C3_RP_STEP_V5("Step (Path selection) Image left"),

	C3_RP_STEP_V6("Step Keypad"),

	C3_RP_STEP_V7("Step Message"),

	C3_RP_STEP_V8("Step prepare battle"),

	C3_RP_STEP_V9("Step invasion drop");

	private final String label;

	ROLEPLAYENTRYTYPES(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

}
