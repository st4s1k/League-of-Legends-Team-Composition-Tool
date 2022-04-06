package com.st4s1k.leagueteamcomp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.merakianalytics.orianna.Orianna;
import com.st4s1k.leagueteamcomp.controller.LTCExceptionController;
import com.st4s1k.leagueteamcomp.controller.LeagueTeamCompController;
import com.st4s1k.leagueteamcomp.exceptions.LTCException;
import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import com.st4s1k.leagueteamcomp.repository.ChampionRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.DoubleStream;

import static com.merakianalytics.orianna.types.common.Region.EUROPE_WEST;
import static com.st4s1k.leagueteamcomp.LeagueTeamCompStarter.StageReadyEvent;
import static com.st4s1k.leagueteamcomp.utils.ResizeHelper.addResizeListener;
import static com.st4s1k.leagueteamcomp.utils.Resources.*;
import static com.st4s1k.leagueteamcomp.utils.Utils.setFieldValue;
import static java.net.http.HttpResponse.BodyHandlers;
import static java.util.Objects.requireNonNull;

@Slf4j
@Component
public class LeagueTeamCompStageInitializer implements ApplicationListener<StageReadyEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Gson gson;

    @Override
    @SneakyThrows
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        Orianna.loadConfiguration("orianna-config.json");
        Orianna.setDefaultRegion(EUROPE_WEST);
        Thread.setDefaultUncaughtExceptionHandler(this::showError);
        getChampionsFromUrl();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE_PATH), LTC_VIEW_PROPERTIES);
        loader.setControllerFactory(applicationContext::getBean);
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
        Map<String, ChampionDTO> champions = gson.fromJson(response.body(), getChampionMapType());
        champions.values().forEach(champion ->
            setFieldValue(champion, "image", new Image(champion.getIconUrl(), true)));
        return champions;
    }

    private Type getChampionMapType() {
        return new TypeToken<Map<String, ChampionDTO>>() {
        }.getType();
    }

    private void showError(Thread t, Throwable e) {
        if (Platform.isFxApplicationThread() && e instanceof LTCException ltcException) {
            showErrorDialog(ltcException);
        } else {
            log.error(e.getMessage(), e);
        }
    }

    @SneakyThrows
    private void showErrorDialog(LTCException ltcException) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(LeagueTeamCompApplication.class.getResource("ltc-exception.fxml"));
        Region root = loader.load();
        LTCExceptionController controller = loader.getController();
        controller.setStageAndSetupListeners(dialog);
        int sceneWidth = 400;
        int sceneHeight = 150;
        controller.setErrorText(ltcException.getMessage());
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
}
