/* ---------------------------------------------------------------- |
 *    ____ _____                                                    |
 *   / ___|___ /                   Communicate - Command - Control  |
 *  | |     |_ \                   MK V "Cerberus"                  |
 *  | |___ ___) |                                                   |
 *   \____|____/                                                    |
 *                                                                  |
 * ---------------------------------------------------------------- |
 * Info        : http://www.clanwolf.net                            |
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
 * Copyright (c) 2001-2019, ClanWolf.net                            |
 * ---------------------------------------------------------------- |
 */
package net.clanwolf.starmap.client.process.logout;

import net.clanwolf.starmap.client.nexus.Nexus;
import net.clanwolf.starmap.client.action.ACTIONS;
import net.clanwolf.starmap.client.action.ActionManager;
import net.clanwolf.starmap.client.logging.C3Logger;
import net.clanwolf.starmap.transfer.GameState;
import net.clanwolf.starmap.transfer.enums.GAMESTATEMODES;

/**
 * @author Christian
 */
public class Logout {

	public static void doLogout() {
		doLogout(false);
	}

	/**
	 * Do the logout itself.
	 */
	public static void doLogout(boolean kickedAfterDoubleLogin) {
		C3Logger.info("Logout");
		ActionManager.getAction(ACTIONS.LOGGING_OFF).execute();

		GameState state = new GameState();
		state.setMode(GAMESTATEMODES.USER_LOG_OUT);
		Nexus.fireNetworkEvent(state);
		Nexus.resetAfterLogout();

		if (kickedAfterDoubleLogin) {
			ActionManager.getAction(ACTIONS.LOGGED_OFF_AFTER_DOUBLE_LOGIN_COMPLETE).execute();
		} else {
			ActionManager.getAction(ACTIONS.LOGGED_OFF_COMPLETE).execute();
		}
	}
}