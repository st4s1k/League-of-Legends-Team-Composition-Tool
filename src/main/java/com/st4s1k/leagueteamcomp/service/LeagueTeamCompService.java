package com.st4s1k.leagueteamcomp.service;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LeagueTeamCompService {

    private static double xOffset = 0;
    private static double yOffset = 0;

    public static void onMousePressed(Stage stage, MouseEvent event) {
        xOffset = stage.getX() - event.getScreenX();
        yOffset = stage.getY() - event.getScreenY();
    }

    public static void onMouseDragged(Stage stage, MouseEvent event) {
        stage.setX(event.getScreenX() + xOffset);
        stage.setY(event.getScreenY() + yOffset);
    }
}
