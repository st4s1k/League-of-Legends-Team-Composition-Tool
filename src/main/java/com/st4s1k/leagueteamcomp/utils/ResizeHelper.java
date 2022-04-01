package com.st4s1k.leagueteamcomp.utils;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import static javafx.scene.Cursor.*;
import static javafx.scene.input.MouseEvent.*;

/**
 * Util class to handle window resizing when a stage style set to StageStyle.UNDECORATED.
 * Includes dragging of the stage.
 * Original on 6/13/14.
 * Updated on 8/15/17.
 * Updated on 12/19/19.
 *
 * @author Alexander.Berg
 * @author Evgenii Kanivets
 * @author Zachary Perales
 */

@Slf4j
public class ResizeHelper {

    public static void addResizeListener(
        Stage stage,
        double border
    ) {
        ResizeListener resizeListener = new ResizeListener(stage, border);
        stage.getScene().addEventHandler(MOUSE_MOVED, resizeListener);
        stage.getScene().addEventHandler(MOUSE_PRESSED, resizeListener);
        stage.getScene().addEventHandler(MOUSE_DRAGGED, resizeListener);
        stage.getScene().addEventHandler(MOUSE_EXITED, resizeListener);
        stage.getScene().addEventHandler(MOUSE_EXITED_TARGET, resizeListener);
        ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
        for (Node child : children) {
            addListenerDeeply(child, resizeListener);
        }
    }

    private static void addListenerDeeply(Node node, EventHandler<MouseEvent> listener) {
        node.addEventHandler(MOUSE_MOVED, listener);
        node.addEventHandler(MOUSE_PRESSED, listener);
        node.addEventHandler(MOUSE_DRAGGED, listener);
        node.addEventHandler(MOUSE_EXITED, listener);
        node.addEventHandler(MOUSE_EXITED_TARGET, listener);
        if (node instanceof Parent parent) {
            ObservableList<Node> children = parent.getChildrenUnmodifiable();
            for (Node child : children) {
                addListenerDeeply(child, listener);
            }
        }
    }

    static class ResizeListener implements EventHandler<MouseEvent> {
        private final Stage stage;
        private final double border;
        private Cursor cursorEvent = DEFAULT;
        private double startX = 0;
        private double startY = 0;

        public ResizeListener(Stage stage, double border) {
            this.stage = stage;
            this.border = border;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
            Scene scene = stage.getScene();

            double mouseEventX = mouseEvent.getSceneX(),
                mouseEventY = mouseEvent.getSceneY(),
                sceneWidth = scene.getWidth(),
                sceneHeight = scene.getHeight();

            if (MOUSE_MOVED.equals(mouseEventType)) {
                if (mouseEventX < border && mouseEventY < border) {
                    cursorEvent = NW_RESIZE;
                } else if (mouseEventX < border && mouseEventY > sceneHeight - border) {
                    cursorEvent = SW_RESIZE;
                } else if (mouseEventX > sceneWidth - border && mouseEventY < border) {
                    cursorEvent = NE_RESIZE;
                } else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {
                    cursorEvent = SE_RESIZE;
                } else if (mouseEventX < border) {
                    cursorEvent = W_RESIZE;
                } else if (mouseEventX > sceneWidth - border) {
                    cursorEvent = E_RESIZE;
                } else if (mouseEventY < border) {
                    cursorEvent = N_RESIZE;
                } else if (mouseEventY > sceneHeight - border) {
                    cursorEvent = S_RESIZE;
                } else {
                    cursorEvent = DEFAULT;
                }
                scene.setCursor(cursorEvent);
            } else if (MOUSE_EXITED.equals(mouseEventType) || MOUSE_EXITED_TARGET.equals(mouseEventType)) {
                scene.setCursor(DEFAULT);
            } else if (MOUSE_PRESSED.equals(mouseEventType)) {
                startX = stage.getWidth() - mouseEventX;
                startY = stage.getHeight() - mouseEventY;
            } else if (MOUSE_DRAGGED.equals(mouseEventType)) {
                if (!DEFAULT.equals(cursorEvent)) {
                    if (!W_RESIZE.equals(cursorEvent) && !E_RESIZE.equals(cursorEvent)) {
                        double minHeight = Math.max(stage.getMinHeight(), border * 2);
                        if (NW_RESIZE.equals(cursorEvent) || N_RESIZE.equals(cursorEvent) || NE_RESIZE.equals(cursorEvent)) {
                            if (stage.getHeight() > minHeight || mouseEventY < 0) {
                                stage.setHeight(stage.getY() - mouseEvent.getScreenY() + stage.getHeight());
                                double screenY = mouseEvent.getScreenY();
                                stage.setY(screenY);
                            }
                        } else {
                            if (stage.getHeight() > minHeight || mouseEventY + startY - stage.getHeight() > 0) {
                                stage.setHeight(mouseEventY + startY);
                            }
                        }
                    }

                    if (!N_RESIZE.equals(cursorEvent) && !S_RESIZE.equals(cursorEvent)) {
                        double minWidth = Math.max(stage.getMinWidth(), border * 2);
                        if (NW_RESIZE.equals(cursorEvent) || W_RESIZE.equals(cursorEvent) || SW_RESIZE.equals(cursorEvent)) {
                            if (stage.getWidth() > minWidth || mouseEventX < 0) {
                                stage.setWidth(stage.getX() - mouseEvent.getScreenX() + stage.getWidth());
                                double screenX = mouseEvent.getScreenX();
                                stage.setX(screenX);
                            }
                        } else {
                            if (stage.getWidth() > minWidth || mouseEventX + startX - stage.getWidth() > 0) {
                                stage.setWidth(mouseEventX + startX);
                            }
                        }
                    }
                }
            }
        }
    }
}
