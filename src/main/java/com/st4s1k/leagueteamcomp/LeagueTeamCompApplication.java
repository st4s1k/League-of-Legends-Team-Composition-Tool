package com.st4s1k.leagueteamcomp;

import com.google.gson.Gson;
import com.st4s1k.leagueteamcomp.model.champion.Champions;
import com.st4s1k.leagueteamcomp.repository.ChampionRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.text.MessageFormat;

import static java.net.http.HttpResponse.BodyHandlers;
import static java.util.Objects.requireNonNull;

public class LeagueTeamCompApplication extends Application {

    private static final String WINDOW_TITLE = "League of Legends Team Composition Tool";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final boolean WINDOW_IS_RESIZABLE = false;

    private static final String FXML_FILE_PATH = "ltc-view.fxml";

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        var gson = new Gson();
        preloadChampions(gson);

        var fxmlLoader = new FXMLLoader(LeagueTeamCompApplication.class.getResource(FXML_FILE_PATH));
        var scene = new Scene(fxmlLoader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.getIcons().add(new Image(requireNonNull(getClass().getResourceAsStream("icon.png"))));
        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(WINDOW_IS_RESIZABLE);
        stage.show();
        stage.setOnCloseRequest(event -> closeProgram());
    }

    @SneakyThrows
    private void preloadChampions(Gson gson) {
        var champions = getChampionsFromUrl(gson);
        ChampionRepository.init(champions);
        champions.getChampions().values().forEach(champion -> {
            var iconPath = MessageFormat.format("champions/{0}.png", champion.getKey());
            champion.setImage(new Image(getFileFromResourceAsStream(iconPath)));
        });
    }

    @SneakyThrows
    private Champions getChampionsFromUrl(Gson gson) {
        var request = HttpRequest.newBuilder()
            .uri(new URI("http://cdn.merakianalytics.com/riot/lol/resources/latest/en-US/champions.json"))
            .GET().build();
        var response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        var json = "{\"champions\":" + response.body() + "}";
        return gson.fromJson(json, Champions.class);
    }

    private InputStream getFileFromResourceAsStream(String fileName) {
        var inputStream = getClass().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    private void closeProgram() {
        // TODO: Implement state saving
    }
}