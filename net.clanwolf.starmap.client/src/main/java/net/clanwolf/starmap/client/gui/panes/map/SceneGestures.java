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
package net.clanwolf.starmap.client.gui.panes.map;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Circle;
import net.clanwolf.starmap.client.action.ACTIONS;
import net.clanwolf.starmap.client.action.ActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

class SceneGestures {
	private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private double previousX;
	private double previousY;

	private final DragContext sceneDragContext = new DragContext();
	private final PannableCanvas canvas;

	private boolean scrollingEnabled = true;

	SceneGestures(PannableCanvas canvas) {
		this.canvas = canvas;
	}

	EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	EventHandler<ScrollEvent> getOnScrollEventHandler() {
		return onScrollEventHandler;
	}

	EventHandler<MouseEvent> getOnMouseMovedEventHandler() {
		return onMouseMovedEventHandler;
	}

	EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
		return onMouseClickedEventHandler;
	}

	EventHandler<MouseEvent> getOnMouseReleasedEventHandler() { return onMouseReleasedEventHandler; }

	private final EventHandler<MouseEvent> onMouseMovedEventHandler = event -> {
//		// These methods did not calculate correct data. If needed, they need to be re-implemented to give
//		// correct results!
//		double universeX = getUniverseX(event.getSceneX() - 190);
//		double universeY = getUniverseY(event.getSceneY() - 70);
//		logger.info("[" + universeX + ", " + universeY + "]");
//		ActionManager.getAction(ACTIONS.UPDATE_COORD_INFO).execute("[" + String.format("%.2f", universeX) + ", " + String.format("%.2f", universeY) + "]");
	};

	private final EventHandler<MouseEvent> onMouseClickedEventHandler = event -> {
		if (event.getTarget() instanceof Circle || event.getTarget() instanceof ImageView || event.getTarget() instanceof Button) {
			logger.info("No action.");
			// nothing
		} else {
			if (event.getButton() == MouseButton.PRIMARY) {
				ActionManager.getAction(ACTIONS.HIDE_SYSTEM_DETAIL).execute();
				ActionManager.getAction(ACTIONS.HIDE_JUMPSHIP_DETAIL).execute();
			}
		}
	};

	private final EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<>() {
		@Override
		public void handle(MouseEvent event) {

			scrollingEnabled = false;
			// logger.info("Button pressed");

			if (event.isPrimaryButtonDown()) {
				canvas.hideStarSystemMarker();
			}

			// right mouse button => panning
			if (!event.isSecondaryButtonDown()) {
				return;
			}

			previousX = event.getX();
			previousY = event.getY();

			sceneDragContext.mouseAnchorX = event.getSceneX();
			sceneDragContext.mouseAnchorY = event.getSceneY();

			sceneDragContext.translateAnchorX = canvas.getTranslateX();
			sceneDragContext.translateAnchorY = canvas.getTranslateY();
		}
	};

	private final EventHandler<MouseEvent> onMouseReleasedEventHandler = mouseEvent -> {
		scrollingEnabled = true;
		// logger.info("Button released");
	};

	public void moveMapByDiff(double x, double y, double diffX, double diffY) {
//		String directionH = "";
//		String directionV = "";
		double multix = 0;
		double multiy = 0;

		if (x < previousX) {
//			directionH = "right";
			multix = -1;
		} else if (x > previousX) {
//			directionH = "left";
			multix = 1;
		}
		if (y < previousY) {
//			directionV = "up";
			multiy = -1;
		} else if (y > previousY) {
//			directionV = "down";
			multiy = 1;
		}
		for (int[] layer : Config.BACKGROUND_STARS_LAYERS) {
			int level = layer[0];
			int factor = layer[2];
			canvas.moveBackgroundStarPane(level, factor * multix, factor * multiy);
		}

		previousX = x;
		previousY = y;

		canvas.setTranslateX(diffX);
		canvas.setTranslateY(diffY);
	}

	private final EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {
		// right mouse button => panning
		if (!event.isSecondaryButtonDown()) {
			return;
		}

		double x = event.getX();
		double y = event.getY();
		double diffX = sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX;
		double diffY = sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY;

		moveMapByDiff(x, y, diffX, diffY);
		event.consume();
	};

	/**
	 * Mouse wheel handler: zoom to pivot point
	 */
	private final EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<>() {
		@Override
		public void handle(ScrollEvent event) {
			if (scrollingEnabled) {
				double delta = 1.2;
				double scale = canvas.getScale(); // only use Y, same value for X
				double oldScale = scale;

				if (event.getDeltaY() < 0) {
					scale /= delta;
				} else {
					scale *= delta;
				}

				scale = clamp(scale);
				double f = (scale / oldScale) - 1;

				// maxX = right overhang, maxY = lower overhang
				double maxX = canvas.getBoundsInParent().getMaxX() - canvas.localToParent(canvas.getPrefWidth(), canvas.getPrefHeight()).getX();
				double maxY = canvas.getBoundsInParent().getMaxY() - canvas.localToParent(canvas.getPrefWidth(), canvas.getPrefHeight()).getY();

				// minX = left overhang, minY = upper overhang
				double minX = canvas.localToParent(0, 0).getX() - canvas.getBoundsInParent().getMinX();
				double minY = canvas.localToParent(0, 0).getY() - canvas.getBoundsInParent().getMinY();

				// adding the overhangs together, as we only consider the width of
				// canvas itself
				double subX = maxX + minX;
				double subY = maxY + minY;

				// subtracting the overall overhang from the width and only the left
				// and upper overhang from the upper left point
				double dx = (event.getSceneX() - 190 - ((canvas.getBoundsInParent().getWidth() - subX) / 2 + (canvas.getBoundsInParent().getMinX() + minX)));
				double dy = (event.getSceneY() - 70 - ((canvas.getBoundsInParent().getHeight() - subY) / 2 + (canvas.getBoundsInParent().getMinY() + minY)));

				canvas.setScale(scale);

				// note: pivot value must be untransformed, i. e. without scaling
				canvas.setPivot(f * dx, f * dy);

				event.consume();
			}
		}
	};

	private static double clamp(double value) {
		if (Double.compare(value, Config.MAP_MIN_SCALE) < 0) {
			return Config.MAP_MIN_SCALE;
		}
		if (Double.compare(value, Config.MAP_MAX_SCALE) > 0) {
			return Config.MAP_MAX_SCALE;
		}
		return value;
	}

//	@SuppressWarnings("unused")
//	private double getUniverseX(double screenX) {
//		// These methods did not calculate correct data. If needed, they need to be re-implemented to give
//		// correct results!
//		double universeX = screenX - (Config.MAP_WIDTH / 2);
//		universeX = universeX / Config.MAP_COORDINATES_MULTIPLICATOR;
//		return universeX;
//	}
//
//	@SuppressWarnings("unused")
//	private double getUniverseY(double screenY) {
//		// These methods did not calculate correct data. If needed, they need to be re-implemented to give
//		// correct results!
//		double universeY = screenY - (Config.MAP_HEIGHT / 2);
//		universeY = universeY / Config.MAP_COORDINATES_MULTIPLICATOR;
//		return universeY;
//	}
}
