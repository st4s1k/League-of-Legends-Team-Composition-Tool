package com.st4s1k.leagueteamcomp;

import com.google.gson.Gson;
import com.st4s1k.leagueteamcomp.controller.LeagueTeamCompController;
import com.st4s1k.leagueteamcomp.model.champion.Champions;
import com.st4s1k.leagueteamcomp.repository.ChampionRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static java.net.http.HttpResponse.BodyHandlers;
import static java.util.Objects.requireNonNull;

@Slf4j
public class LeagueTeamCompApplication extends Application {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Resource path constants                             *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static final String ICON_FILE_PATH = "icon.png";
    public static final String FXML_FILE_PATH = "ltc-view.fxml";
    public static final String APP_BUNDLE_PATH = "com.st4s1k.leagueteamcomp.ltc";
    public static final String FXML_BUNDLE_PATH = "com.st4s1k.leagueteamcomp.ltc-view";

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Window constants                                    *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static final String WINDOW_TITLE = "League of Legends Team Composition Tool";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final boolean WINDOW_IS_RESIZABLE = false;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Resource bundles                                    *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private final ResourceBundle ltcProperties = ResourceBundle.getBundle(APP_BUNDLE_PATH);
    private final ResourceBundle ltcViewProperties = ResourceBundle.getBundle(FXML_BUNDLE_PATH);

    public static void main(String[] args) {
        launch();
    }

    @Override
    @SneakyThrows
    public void start(Stage stage) {
        ChampionRepository.init(getChampionsFromUrl());
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE_PATH), ltcViewProperties);
        Parent root = loader.load();
        LeagueTeamCompController controller = loader.getController();
        controller.setStageAndSetupListeners(stage);
        controller.setCloseButtonAction(() ->  closeProgram(stage, controller));
        controller.setMinimizeButtonAction(() -> stage.setIconified(true));
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.getIcons().add(new Image(requireNonNull(getClass().getResourceAsStream(ICON_FILE_PATH))));
        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(WINDOW_IS_RESIZABLE);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    @SneakyThrows
    private CompletableFuture<Champions> getChampionsFromUrl() {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(ltcProperties.getString("champions-url")))
            .build();
        return HttpClient.newHttpClient()
            .sendAsync(request, BodyHandlers.ofString())
            .thenApply(this::getChampions);
    }

    private Champions getChampions(HttpResponse<String> response) {
        String json = MessageFormat.format("'{'\"champions\":{0}'}'", response.body());
        Champions champions = new Gson().fromJson(json, Champions.class);
        champions.getChampions().values()
            .forEach(champion -> champion.setImage(new Image(champion.getIconUrl(), true)));
        return champions;
    }

    private void closeProgram(Stage stage, LeagueTeamCompController controller) {
        log.info("Closing application...");
        controller.stop();
        stage.close();
        log.info("Application closed.");
    }
}