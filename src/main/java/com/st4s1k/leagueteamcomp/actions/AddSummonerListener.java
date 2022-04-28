package com.st4s1k.leagueteamcomp.actions;

import com.st4s1k.leagueteamcomp.actions.base.LTCActionListener;
import com.st4s1k.leagueteamcomp.controller.LeagueTeamCompController;
import javafx.application.Platform;

import static com.st4s1k.leagueteamcomp.utils.Utils.disableInteraction;
import static com.st4s1k.leagueteamcomp.utils.Utils.enableInteraction;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AddSummonerListener extends LTCActionListener {

    private static final int TIMEOUT = 300;

    private final LeagueTeamCompController controller;

    private String fieldText;

    public AddSummonerListener(LeagueTeamCompController controller) {
        setAllowParallelExecution(false);
        this.controller = controller;
    }

    @Override
    public void before() {
        disableInteraction(
            controller.getSummonerAddField(),
            controller.getSummonerAddButton(),
            controller.getSummonerDataListView()
        );
        fieldText = controller.getSummonerAddField().getText();
    }

    @Override
    public long whileRunning() throws Throwable {
        Platform.runLater(() -> controller.getSummonerAddField().setText("loading ."));
        MILLISECONDS.sleep(TIMEOUT);
        Platform.runLater(() -> controller.getSummonerAddField().setText("loading .."));
        MILLISECONDS.sleep(TIMEOUT);
        Platform.runLater(() -> controller.getSummonerAddField().setText("loading ..."));
        return TIMEOUT;
    }

    @Override
    public void onDone() {
        controller.getSummonerAddField().setText(fieldText);
        enableInteraction(
            controller.getSummonerAddField(),
            controller.getSummonerAddButton(),
            controller.getSummonerDataListView()
        );
    }
}
