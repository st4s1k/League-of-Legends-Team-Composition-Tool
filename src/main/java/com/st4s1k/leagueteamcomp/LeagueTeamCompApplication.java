package com.st4s1k.leagueteamcomp;

import com.google.gson.Gson;
import com.st4s1k.leagueteamcomp.controller.LeagueTeamCompController;
import com.st4s1k.leagueteamcomp.model.champion.Champions;
import com.st4s1k.leagueteamcomp.repository.ChampionRepository;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static java.net.http.HttpResponse.BodyHandlers;
import static java.util.Objects.requireNonNull;

public class LeagueTeamCompApplication extends Application {

    public static final String ICON_FILE_PATH = "icon.png";
    public static final String FXML_FILE_PATH = "ltc-view.fxml";
    public static final String BUNDLE_PATH = "com.st4s1k.leagueteamcomp.ltc-view";

    private static final String WINDOW_TITLE = "League of Legends Team Composition Tool";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final boolean WINDOW_IS_RESIZABLE = false;

    public static void main(String[] args) {
        launch();
    }

    @Override
    @SneakyThrows
    public void start(Stage stage) {
        loadChampionData();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE_PATH), ResourceBundle.getBundle(BUNDLE_PATH));
        Parent root = loader.load();
        LeagueTeamCompController controller = loader.getController();
        controller.setStageAndSetupListeners(stage);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.getIcons().add(new Image(requireNonNull(getClass().getResourceAsStream(ICON_FILE_PATH))));
        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(WINDOW_IS_RESIZABLE);
        stage.setOnCloseRequest(event -> closeProgram(controller));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    private void loadChampionData() {
        CompletableFuture.runAsync(() -> ChampionRepository.init(getChampionsFromUrl()));
    }

    @SneakyThrows
    private CompletableFuture<Champions> getChampionsFromUrl() {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("http://cdn.merakianalytics.com/riot/lol/resources/latest/en-US/champions.json"))
            .build();
        return HttpClient.newHttpClient()
            .sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenApply(this::getChampions);
    }

    private Champions getChampions(String response) {
        String json = "{\"champions\":" + response + "}";
        Champions champions = new Gson().fromJson(json, Champions.class);
        champions.getChampions().values()
            .forEach(champion -> champion.setImage(new Image(champion.getIconUrl(), true)));
        return champions;
    }

    private void closeProgram(LeagueTeamCompController controller) {
        controller.stop();
        Platform.exit();
    }
}