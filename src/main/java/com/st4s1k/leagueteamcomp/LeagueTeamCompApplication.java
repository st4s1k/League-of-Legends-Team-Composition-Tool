package com.st4s1k.leagueteamcomp;

import com.google.gson.Gson;
import com.st4s1k.leagueteamcomp.controller.LTCExceptionController;
import com.st4s1k.leagueteamcomp.controller.LeagueTeamCompController;
import com.st4s1k.leagueteamcomp.exceptions.LTCException;
import com.st4s1k.leagueteamcomp.model.champion.ChampionsDTO;
import com.st4s1k.leagueteamcomp.repository.ChampionRepository;
import com.st4s1k.leagueteamcomp.utils.ResizeHelper;
import com.st4s1k.leagueteamcomp.utils.Utils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.stream.DoubleStream;

import static com.st4s1k.leagueteamcomp.utils.Resources.*;
import static java.net.http.HttpResponse.BodyHandlers;
import static java.util.Objects.requireNonNull;

@Slf4j
public class LeagueTeamCompApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    @SneakyThrows
    public void start(Stage stage) {
        Thread.setDefaultUncaughtExceptionHandler(LeagueTeamCompApplication::showError);
        getChampionsFromUrl();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE_PATH), LTC_VIEW_PROPERTIES);
        Parent root = loader.load();
        LeagueTeamCompController controller = loader.getController();
        controller.setStageAndSetupListeners(stage);
        controller.setCloseButtonAction(() -> Utils.stop(LeagueTeamCompApplication.class).accept(() -> {
            controller.stop();
            stage.close();
        }));
        controller.setMinimizeButtonAction(() -> stage.setIconified(true));
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.getIcons().add(new Image(requireNonNull(getClass().getResourceAsStream(ICON_FILE_PATH))));
        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(WINDOW_IS_RESIZABLE);
        stage.show();
    }

    @SneakyThrows
    private void getChampionsFromUrl() {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(CHAMPIONS_URL))
            .build();
        HttpClient.newHttpClient()
            .sendAsync(request, BodyHandlers.ofString())
            .thenApply(this::getChampions)
            .thenAccept(ChampionRepository::init);
    }

    private ChampionsDTO getChampions(HttpResponse<String> response) {
        String json = MessageFormat.format("'{'\"champions\":{0}'}'", response.body());
        ChampionsDTO champions = new Gson().fromJson(json, ChampionsDTO.class);
        champions.getChampions().values().forEach(champion ->
            Utils.setFieldValue(champion, "image", new Image(champion.getIconUrl(), true)));
        return champions;
    }

    private static void showError(Thread t, Throwable e) {
        log.error("***Default exception handler***");
        if (Platform.isFxApplicationThread()) {
            showErrorDialog(e);
        } else {
            log.error("An unexpected error occurred in " + t);
        }
    }

    @SneakyThrows
    private static void showErrorDialog(Throwable e) {
        StringWriter errorMsg = new StringWriter();
        e.printStackTrace(new PrintWriter(errorMsg));
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(LeagueTeamCompApplication.class.getResource("ltc-exception.fxml"));
        Region root = loader.load();
        LTCExceptionController controller = loader.getController();
        controller.setStageAndSetupListeners(dialog);
        int sceneWidth = 600;
        int sceneHeight = 400;
        if (e instanceof LTCException ltcException) {
            sceneWidth = 300;
            sceneHeight = 150;
            controller.setErrorText(ltcException.getMessage());
        } else {
            controller.setErrorText(errorMsg.toString());
        }
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        scene.setFill(Color.TRANSPARENT);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setMinHeight(sceneHeight);
        dialog.setMinWidth(sceneWidth);
        Insets padding = root.getPadding();
        double border = DoubleStream.of(padding.getTop(), padding.getRight(), padding.getBottom(), padding.getLeft())
            .min().orElse(4);
        ResizeHelper.addResizeListener(dialog, border);
        dialog.show();
    }
}
