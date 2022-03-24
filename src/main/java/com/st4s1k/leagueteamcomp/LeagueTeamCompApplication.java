package com.st4s1k.leagueteamcomp;

import com.google.gson.Gson;
import com.st4s1k.leagueteamcomp.model.champion.Champions;
import com.st4s1k.leagueteamcomp.repository.ChampionRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

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
        System.setProperty("javafx.platform", "Desktop");
        launch();
    }

    @Override
    @SneakyThrows
    public void start(Stage stage) {
        preloadChampions();
        TabPane root = FXMLLoader.load(
            requireNonNull(getClass().getResource(FXML_FILE_PATH)),
            ResourceBundle.getBundle(BUNDLE_PATH)
        );
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.getIcons().add(new Image(requireNonNull(getClass().getResourceAsStream(ICON_FILE_PATH))));
        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(WINDOW_IS_RESIZABLE);
        stage.setOnCloseRequest(event -> closeProgram());
        stage.show();
    }

    @SneakyThrows
    private void preloadChampions() {
        Champions champions = getChampionsFromUrl();
        ChampionRepository.init(champions);
    }

    @SneakyThrows
    private Champions getChampionsFromUrl() {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("http://cdn.merakianalytics.com/riot/lol/resources/latest/en-US/champions.json"))
            .build();
        String response = HttpClient.newHttpClient()
            .sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .join();
        String json = "{\"champions\":" + response + "}";
        Champions champions = new Gson().fromJson(json, Champions.class);
        champions.getChampions().values()
            .forEach(champion -> champion.setImage(new Image(champion.getIconUrl(), true)));
        return champions;
    }

    private void closeProgram() {
        // TODO: Implement state saving
    }
}