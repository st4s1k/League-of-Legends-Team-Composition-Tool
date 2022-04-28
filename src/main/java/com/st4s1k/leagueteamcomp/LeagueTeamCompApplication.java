package com.st4s1k.leagueteamcomp;

import com.google.gson.reflect.TypeToken;
import com.merakianalytics.orianna.Orianna;
import com.st4s1k.leagueteamcomp.controller.LTCExceptionController;
import com.st4s1k.leagueteamcomp.controller.LeagueTeamCompController;
import com.st4s1k.leagueteamcomp.exceptions.LTCException;
import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import com.st4s1k.leagueteamcomp.repository.ChampionRepository;
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

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.DoubleStream;

import static com.merakianalytics.orianna.types.common.Region.EUROPE_WEST;
import static com.st4s1k.leagueteamcomp.utils.ResizeHelper.addResizeListener;
import static com.st4s1k.leagueteamcomp.utils.Resources.*;
import static com.st4s1k.leagueteamcomp.utils.Utils.setFieldValue;
import static java.net.http.HttpResponse.BodyHandlers;
import static java.util.Objects.requireNonNull;

@Slf4j
public class LeagueTeamCompApplication extends Application {

    public static void main(String[] args) {
        printBanner();
        launch();
    }

    @Override
    @SneakyThrows
    public void start(Stage stage) {
        Orianna.loadConfiguration("orianna-config.json");
        Orianna.setDefaultRegion(EUROPE_WEST);
        Thread.setDefaultUncaughtExceptionHandler(this::showError);
        getChampionsFromUrl();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE_PATH), LTC_VIEW_PROPERTIES);
        Parent root = loader.load();
        LeagueTeamCompController controller = loader.getController();
        controller.setStageAndSetupListeners(stage);
        controller.setCloseButtonAction(() -> applicationStopAction(stage, controller));
        controller.setMinimizeButtonAction(() -> stage.setIconified(true));

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.getIcons().add(new Image(requireNonNull(getClass().getResourceAsStream(ICON_FILE_PATH))));
        stage.setTitle(WINDOW_TITLE);
        stage.setResizable(WINDOW_IS_RESIZABLE);
        stage.setOnCloseRequest(event -> applicationStopAction(stage, controller));
        stage.show();
    }

    private void applicationStopAction(Stage stage, LeagueTeamCompController controller) {
        String className = getClass().getSimpleName();
        log.info("Stopping {}...", className);
        controller.stop();
        stage.close();
        log.info("{} stopped.", className);
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

    private Map<String, ChampionDTO> getChampions(HttpResponse<String> response) {
        Map<String, ChampionDTO> champions = GSON.fromJson(response.body(), getChampionMapType());
        champions.values().forEach(champion ->
            setFieldValue(champion, "image", new Image(champion.getIconUrl(), true)));
        return champions;
    }

    private Type getChampionMapType() {
        return new TypeToken<Map<String, ChampionDTO>>() {
        }.getType();
    }

    private void showError(Thread t, Throwable e) {
        log.error("Exception thrown in thread " + Thread.currentThread().getName(), e);
        if (Platform.isFxApplicationThread()) {
            showErrorDialog(e);
        }
    }

    @SneakyThrows
    private void showErrorDialog(Throwable e) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(LeagueTeamCompApplication.class.getResource("ltc-exception.fxml"));
        Region root = loader.load();
        LTCExceptionController controller = loader.getController();
        controller.setStageAndSetupListeners(dialog);
        int sceneWidth = 400;
        int sceneHeight = 150;
        if (e instanceof LTCException ltcException) {
            controller.setErrorText(ltcException.getMessage());
        } else {
            controller.setErrorText("An exception occurred, please try again later...");
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
        addResizeListener(dialog, border);
        dialog.show();
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  _                               _____                     ____                      ");
        System.out.println(" | |    ___  __ _  __ _ _   _  __|_   _|__  __ _ _ __ ___  / ___|___  _ __ ___  _ __  ");
        System.out.println(" | |   / _ \\/ _` |/ _` | | | |/ _ \\| |/ _ \\/ _` | '_ ` _ \\| |   / _ \\| '_ ` _ \\| '_ \\ ");
        System.out.println(" | |__|  __/ (_| | (_| | |_| |  __/| |  __/ (_| | | | | | | |__| (_) | | | | | | |_) |");
        System.out.println(" |_____\\___|\\__,_|\\__, |\\__,_|\\___||_|\\___|\\__,_|_| |_| |_|\\____\\___/|_| |_| |_| .__/ ");
        System.out.println("==================|___/========================================================|_|====");
        System.out.println(":: League of Legends Team Composition Tool ::".concat(String.format("%41s", String.format("(v%s)", LTC_VERSION))));
        System.out.println();
    }

    @Override
    public void stop() {
        Platform.exit();
    }
}
