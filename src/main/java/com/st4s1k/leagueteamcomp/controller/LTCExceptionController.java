package com.st4s1k.leagueteamcomp.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LTCExceptionController implements Initializable {

    private static double xOffset = 0;
    private static double yOffset = 0;

    @FXML
    private Button minimizeButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button okButton;
    @FXML
    private TextArea errorMessage;
    @FXML
    private HBox windowTitleBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage.setEditable(false);
    }

    public void setStageAndSetupListeners(Stage stage) {
        windowTitleBar.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        windowTitleBar.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
        windowTitleBar.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    stage.setMaximized(!stage.isMaximized());
                }
            }
        });
        okButton.setOnAction(event -> stage.close());
        closeButton.setOnAction(actionEvent -> stage.close());
        minimizeButton.setOnAction(actionEvent -> stage.setIconified(true));
    }

    public void setErrorText(String text) {
        errorMessage.setText(text);
    }
}
