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
package net.clanwolf.starmap.client.gui.panes.map;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import net.clanwolf.starmap.client.nexus.Nexus;
import net.clanwolf.starmap.client.process.universe.BOJumpship;
import net.clanwolf.starmap.client.process.universe.BOStarSystem;
import net.clanwolf.starmap.client.process.universe.BOUniverse;

import java.util.ArrayList;
import java.util.HashMap;

public class PannableCanvas extends Pane {
	private DoubleProperty myScale = new SimpleDoubleProperty(1.0);
	private Canvas grid_500 = null;
	private Canvas grid_250 = null;
	private Canvas grid_center = null;
	private Pane starPane = null;
	private Pane attacksPane = null;
	private HashMap<Integer, ArrayList<Circle>> starPanelsStarLists = new HashMap<>();

	private boolean starSystemLabelsVisible = true;

	private Circle starSystemMarkerCircle30ly;
	private Circle starSystemMarkerCircle60ly;

	private BOUniverse boUniverse;

	PannableCanvas() {
		boUniverse = Nexus.getBoUniverse();
		setPrefSize(Config.MAP_WIDTH, Config.MAP_HEIGHT);
		setStyle("-fx-background-color:transparent;-fx-border-width:5px;-fx-border-color:gray;");

		// add scale transform
		scaleXProperty().bind(myScale);
		scaleYProperty().bind(myScale);

		double radius2 = 30 * 2 * Config.MAP_COORDINATES_MULTIPLICATOR; // 60 Lightyears
		starSystemMarkerCircle60ly = new Circle(radius2);
		starSystemMarkerCircle60ly.setStroke(Config.MAP_RANGE_CIRCLE_COLOR.deriveColor(.5, .5, .5, 1.0));
		starSystemMarkerCircle60ly.setStrokeWidth(2);
		starSystemMarkerCircle60ly.getStrokeDashArray().setAll(50d, 20d, 50d, 20d);
		starSystemMarkerCircle60ly.setFill(null);
		starSystemMarkerCircle60ly.setVisible(false);

		double radius = 30 * Config.MAP_COORDINATES_MULTIPLICATOR; // 30 Lightyears
		starSystemMarkerCircle30ly = new Circle(radius);
		starSystemMarkerCircle30ly.setStroke(Config.MAP_RANGE_CIRCLE_COLOR.deriveColor(.5, .5, .5, 1.0));
		starSystemMarkerCircle30ly.setStrokeWidth(2);
		starSystemMarkerCircle30ly.getStrokeDashArray().setAll(10d, 10d, 10d, 10d);
		starSystemMarkerCircle30ly.setFill(Config.MAP_RANGE_CIRCLE_COLOR.deriveColor(.6, .6, .6, 0.1));
		starSystemMarkerCircle30ly.setVisible(false);

		this.getChildren().add(starSystemMarkerCircle60ly);
		this.getChildren().add(starSystemMarkerCircle30ly);
	}

	public void setAttacksPane(Pane attacksPane) {
		this.attacksPane = attacksPane;
	}

	public void hideStarSystemMarker() {
		starSystemMarkerCircle60ly.setVisible(false);
		starSystemMarkerCircle30ly.setVisible(false);
	}

	public void showStarSystemMarker(BOStarSystem system) {
		double x = system.getScreenX();
		double y = system.getScreenY();

		starSystemMarkerCircle60ly.setCenterX(x);
		starSystemMarkerCircle60ly.setCenterY(y);
		starSystemMarkerCircle60ly.setVisible(true);

		starSystemMarkerCircle30ly.setCenterX(x);
		starSystemMarkerCircle30ly.setCenterY(y);
		starSystemMarkerCircle30ly.setVisible(true);
	}

	/**
	 * Add a grid to the canvas, send it to back
	 */
	public void addStarPane() {
		double w = Config.MAP_WIDTH;
		double h = Config.MAP_HEIGHT;

		if (starPane == null) {
			starPane = new Pane();
			starPane.setMaxWidth(w);
			starPane.setMaxHeight(h);
			starPane.setMouseTransparent(true);
		}

		for (int[] layer : Config.BACKGROUND_STARS_LAYERS) {
			int level = layer[0];
			int number = layer[1];

			ArrayList<Circle> l = new ArrayList<>();

			for (int i = 0; i < number; i++) {
				double x = (((Math.random()) * w + 1));
				double y = w - (((Math.random()) * h + 1));
				int size = (int) ((Math.random()) * Config.BACKGROUND_STARS_MAX_SIZE + 1);

				Circle c = new Circle(x, y, size);
				c.setStrokeWidth(0);
				c.setFill(Color.WHITESMOKE.deriveColor(1, 1, 1, 0.4));
				l.add(c);
				starPane.getChildren().add(c);
			}
			starPanelsStarLists.put(level, l);
			if (!getChildren().contains(starPane)) {
				getChildren().add(starPane);
				starPane.toBack();
			}
		}
	}

	public void moveBackgroundStarPane(int level, double x, double y) {
		Platform.runLater(() -> {
			ArrayList<Circle> l = starPanelsStarLists.get(level);
			for (Circle c : l) {
				c.setCenterX(c.getCenterX() + x);
				c.setCenterY(c.getCenterY() + y);
			}
		});
	}

	public void addGrid_500() {
		double w = Config.MAP_WIDTH;
		double h = Config.MAP_HEIGHT;

		grid_500 = new Canvas(w, h);
		grid_500.setMouseTransparent(true);

		GraphicsContext gc = grid_500.getGraphicsContext2D();
		gc.setStroke(Color.ORANGE);
		gc.setLineWidth(1);

		// draw grid lines
		double offset = 500;
		for (double i = offset; i < w; i += offset) {
			gc.strokeLine(i, 0, i, h);
			gc.strokeLine(0, i, w, i);
		}
		getChildren().add(grid_500);
		grid_500.toBack();
	}

	private void setGrid_500_Visible() {
		if (grid_500 != null) {
			if (myScale.get() >= Config.zoomLevelToHideGrid1) {
				grid_500.setVisible(true);
			} else if (myScale.get() < Config.zoomLevelToHideGrid1) {
				grid_500.setVisible(false);
			}
		}
	}

	public void addGrid_250() {
		double w = Config.MAP_WIDTH;
		double h = Config.MAP_HEIGHT;

		grid_250 = new Canvas(w, h);
		grid_250.setMouseTransparent(true);

		GraphicsContext gc = grid_250.getGraphicsContext2D();
		gc.setStroke(Color.GRAY);
		gc.setLineWidth(1);

		// draw grid lines
		double offset = 500;
		for (double i = 250; i < w; i += offset) {
			gc.strokeLine(i, 0, i, h);
			gc.strokeLine(0, i, w, i);
		}
		getChildren().add(grid_250);
		grid_250.toBack();
	}

	private void setGrid_250_Visible() {
		if (grid_250 != null) {
			if (myScale.get() >= Config.zoomLevelToHideGrid2) {
				grid_250.setVisible(true);
				grid_center.setVisible(true);
			} else if (myScale.get() < Config.zoomLevelToHideGrid2) {
				grid_250.setVisible(false);
				grid_center.setVisible(false);
			}
		}
	}

	public void addGrid_Center() {
		double w = Config.MAP_WIDTH;
		double h = Config.MAP_HEIGHT;

		grid_center = new Canvas(w, h);
		grid_center.setMouseTransparent(true);

		GraphicsContext gc = grid_center.getGraphicsContext2D();
		gc.setStroke(Color.RED);
		gc.setLineWidth(2);

		gc.strokeLine(w / 2, 0, w / 2, h);
		gc.strokeLine(0, h / 2, h, h / 2);

		getChildren().add(grid_center);
		grid_center.toBack();
	}

	public double getScale() {
		return myScale.get();
	}

	public void setPivot(double x, double y) {
		setTranslateX(getTranslateX() - x);
		setTranslateY(getTranslateY() - y);
	}

	public void setScale(double scale) {
		myScale.set(scale);
		setStarSystemLabelsVisible();
		setStarSystemCirclesVisible();
		setGrid_500_Visible();
		setGrid_250_Visible();
		setAttacksVisible();
	}

	private void setStarSystemLabelsVisible() {
		if (boUniverse.starSystemBOs != null) {
			if (myScale.get() >= Config.zoomLevelToHideStarSystemLabels) {
				if (!starSystemLabelsVisible) {
					starSystemLabelsVisible = true;
					for (BOStarSystem ss : boUniverse.starSystemBOs.values()) {
						Label l = ss.getStarSystemLabel();
						l.setVisible(starSystemLabelsVisible);
					}
				}
			} else if (myScale.get() < Config.zoomLevelToHideStarSystemLabels) {
				if (starSystemLabelsVisible) {
					starSystemLabelsVisible = false;
					for (BOStarSystem ss : boUniverse.starSystemBOs.values()) {
						Label l = ss.getStarSystemLabel();
						l.setVisible(starSystemLabelsVisible);
					}
				}
			}
		}
	}

	private void setStarSystemCirclesVisible() {
		if (boUniverse.starSystemBOs != null) {
			if (myScale.get() >= Config.zoomLevelToHideStarSystemCircles) {
				for (BOStarSystem ss : boUniverse.starSystemBOs.values()) {
					Circle c = ss.getStarSystemCircle();
					c.setVisible(true);
				}
			} else if (myScale.get() < Config.zoomLevelToHideStarSystemCircles) {
				for (BOStarSystem ss : boUniverse.starSystemBOs.values()) {
					Circle c = ss.getStarSystemCircle();
					c.setVisible(false);
				}
			}
		}
	}

	private void setAttacksVisible() {
		if (boUniverse.starSystemBOs != null && attacksPane != null) {
			if (myScale.get() >= Config.zoomLevelToHideAttacks) {
				attacksPane.setVisible(true);
			} else if (myScale.get() < Config.zoomLevelToHideAttacks) {
				attacksPane.setVisible(false);
				for (BOJumpship ship : boUniverse.jumpshipBOs.values()) {
					if (ship.getJumpshipImage() != null) {
						ship.getJumpshipImage().setVisible(false);
//						ship.getPredictedRouteLine().setVisible(false);
					}
				}
			}
			if (myScale.get() >= Config.zoomLevelToHideJumpships) {
				for (BOJumpship ship : boUniverse.jumpshipBOs.values()) {
					if (ship.getJumpshipImage() != null) {
						ship.getJumpshipImage().setVisible(true);
//						ship.getPredictedRouteLine().setVisible(true);
					}
				}
			} else if (myScale.get() < Config.zoomLevelToHideJumpships) {
				for (BOJumpship ship : boUniverse.jumpshipBOs.values()) {
					if (ship.getJumpshipImage() != null) {
						ship.getJumpshipImage().setVisible(false);
//						ship.getPredictedRouteLine().setVisible(false);
					}
				}
			}
		}
	}

	public void setVisibility() {
		setStarSystemLabelsVisible();
		setStarSystemCirclesVisible();
		setGrid_500_Visible();
		setGrid_250_Visible();
		setAttacksVisible();
	}
}