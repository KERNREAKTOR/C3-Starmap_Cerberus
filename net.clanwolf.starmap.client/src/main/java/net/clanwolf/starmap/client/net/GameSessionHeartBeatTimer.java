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
 * Copyright (c) 2001-2021, ClanWolf.net                            |
 * ---------------------------------------------------------------- |
 */
package net.clanwolf.starmap.client.net;

import net.clanwolf.starmap.client.nexus.Nexus;
import net.clanwolf.starmap.logging.C3Logger;
import net.clanwolf.starmap.transfer.GameState;
import net.clanwolf.starmap.transfer.enums.GAMESTATEMODES;

import java.util.TimerTask;

/**
 * @author Undertaker
 *
 */
public class GameSessionHeartBeatTimer extends TimerTask {

	public GameSessionHeartBeatTimer() {
		//
	}

	@Override
	public void run() {
		C3Logger.info("Sending keepalive heartbeat.");
		GameState heartbeatState = new GameState();
		heartbeatState.setMode(GAMESTATEMODES.SESSION_KEEPALIVE);
		heartbeatState.addObject(null);
		Nexus.fireNetworkEvent(heartbeatState);
	}
}