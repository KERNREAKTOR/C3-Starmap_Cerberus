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
package net.clanwolf.starmap.client.process.universe;

import net.clanwolf.starmap.client.gui.panes.map.tools.GraphManager;
import net.clanwolf.starmap.client.nexus.Nexus;
import net.clanwolf.starmap.transfer.dtos.*;
import org.kynosarges.tektosyne.geometry.GeoUtils;
import org.kynosarges.tektosyne.geometry.PointD;
import org.kynosarges.tektosyne.geometry.PolygonLocation;
import org.kynosarges.tektosyne.geometry.VoronoiResults;
import org.kynosarges.tektosyne.subdivision.Subdivision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class BOUniverse {

	private UniverseDTO universeDTO;

	public HashMap<Long, BOStarSystem> starSystemBOs = new HashMap<>();
	public HashMap<String, BOFaction> factionBOs = new HashMap<>();
	public HashMap<String, BOJumpship> jumpshipBOs = new HashMap<>();
	public HashMap<Long, BOAttack> attackBOs = new HashMap<>();
	public HashMap<Long, ArrayList<RoutePointDTO>> routesList = new HashMap<>();

	public TreeSet<BOJumpship> jumpshipListSorted = null;

	public Integer currentSeason;
	public Integer currentSeasonMetaPhase;
	public Integer currentRound;
	public String currentDate;

	public VoronoiResults voronoiResults = null;
	public Subdivision delaunaySubdivision = null;
	public GraphManager graphManager = null;

	public BOJumpship currentlyDraggedJumpship = null;

	public BOStarSystem getStarSystemByPoint(PointD p) {
		PointD[] foundRegion = null;
		for (PointD[] region : voronoiResults.voronoiRegions()) {
			PolygonLocation location = GeoUtils.pointInPolygon(p, region);
			if ("INSIDE".equals(location.toString())) {
				foundRegion = region;
			}
		}
		if (foundRegion != null) {
			for (BOStarSystem boSs : starSystemBOs.values()) {
				if (foundRegion.equals(boSs.getVoronoiRegion())) {
					return boSs;
				}
			}
		}
		return null;
	}

	public TreeSet<BOJumpship> getJumpshipListSorted() {
		if (jumpshipListSorted == null) {
			jumpshipListSorted = new TreeSet<>();
			jumpshipListSorted.addAll(jumpshipBOs.values());
		}
		return jumpshipListSorted;
	}

	public HashMap<Long, RolePlayStoryDTO> getAttackStories() {
		return universeDTO.attackStorys;
	}

	public RolePlayStoryDTO getAttackStoriesByID(Long storyID) {
		return universeDTO.attackStorys.get(storyID);
	}

	public BOUniverse(UniverseDTO universeDTO) {
		this.universeDTO = universeDTO;

		for (StarSystemDataDTO ss : universeDTO.starSystems.values()) {
			BOStarSystem boStarSystem = new BOStarSystem(ss);
			starSystemBOs.put(ss.getStarSystemID().getId(), boStarSystem);
		}

		for (AttackDTO att : universeDTO.attacks) {
			BOAttack boAttack = new BOAttack(att);
			attackBOs.put(boAttack.getAttackDTO().getId(), boAttack);
		}

		for (FactionDTO factionDTO : universeDTO.factions.values()) {
			BOFaction boFaction = new BOFaction(factionDTO);
			factionBOs.put(factionDTO.getShortName(), boFaction);
		}

		for (JumpshipDTO jumpshipDTO : universeDTO.jumpships.values()) {
			BOJumpship boJumpship = new BOJumpship(jumpshipDTO);
			jumpshipBOs.put(jumpshipDTO.getJumpshipName(), boJumpship);
		}

		for (RoutePointDTO p : universeDTO.routepoints) {
			if (routesList.get(p.getJumpshipId()) != null) {
				routesList.get(p.getJumpshipId()).add(p);
			} else {
				ArrayList<RoutePointDTO> l = new ArrayList<>();
				l.add(p);
				routesList.put(p.getJumpshipId(), l);
			}
		}

		currentSeason = universeDTO.currentSeason;
		currentSeasonMetaPhase = universeDTO.currentSeasonMetaPhase;
		currentRound = universeDTO.currentRound;
		currentDate = universeDTO.currentDate;
	}

	public synchronized void setUniverseDTO(UniverseDTO uniDTO) {
		// Insert refreshed universe
		this.universeDTO = uniDTO;
		this.currentSeason = this.universeDTO.currentSeason;
		this.currentSeasonMetaPhase = this.universeDTO.currentSeasonMetaPhase;
		this.currentRound = this.universeDTO.currentRound;
		this.currentDate = this.universeDTO.currentDate;

		for (StarSystemDataDTO starSystemDataDTO : universeDTO.starSystems.values()) {
			BOStarSystem ss = starSystemBOs.get(starSystemDataDTO.getStarSystemID().getId());
			ss.setStarSystemDataDTO(starSystemDataDTO);
		}

		attackBOs.clear();
		for (AttackDTO att : universeDTO.attacks) {
			BOAttack boAttack = new BOAttack(att);
			attackBOs.put(boAttack.getAttackDTO().getId(), boAttack);
		}

		for (JumpshipDTO jumpshipDTO : universeDTO.jumpships.values()) {
			BOJumpship boJumpship = jumpshipBOs.get(jumpshipDTO.getJumpshipName());
			boJumpship.setJumpshipDTO(jumpshipDTO);
		}
	}

	public ArrayList<BOFaction> getFactionList(){
		return new ArrayList<>(factionBOs.values());
	}

	public ArrayList<BOJumpship> getJumpshipList() { return new ArrayList<>(jumpshipBOs.values()); }

	public BOFaction getFactionByID(Long id){
		for(BOFaction faction : Nexus.getBoUniverse().getFactionList()){
			if(faction.getID().equals(id))
				return faction;
		}
		return null;
	}

	public BOJumpship getJumpshipByID(Long id){
		for(BOJumpship ship : Nexus.getBoUniverse().getJumpshipList()){
			if(ship.getJumpshipId().equals(id))
				return ship;
		}
		return null;
	}
}
