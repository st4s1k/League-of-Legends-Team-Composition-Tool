package com.st4s1k.leagueteamcomp.actions;

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.st4s1k.leagueteamcomp.actions.base.LTCAction;
import com.st4s1k.leagueteamcomp.controller.LeagueTeamCompController;
import com.st4s1k.leagueteamcomp.exceptions.LTCException;
import com.st4s1k.leagueteamcomp.model.SummonerData;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import static com.st4s1k.leagueteamcomp.utils.LeagueTeamCompTransformerUtils.convertToSummonerData;

@Slf4j
public class AddSummonerAction extends LTCAction {

    private final LeagueTeamCompController controller;

    public AddSummonerAction(LeagueTeamCompController controller) {
        setAllowParallelExecution(false);
        this.controller = controller;
    }

    @Override
    public void run() {
        String fieldText = controller.getSummonerAddField().getText();
        if (!fieldText.isBlank()) {
            Summoner summoner = Orianna.summonerNamed(fieldText).get();
            if (summoner.exists()) {
                Platform.runLater(() -> {
                    if (summonerIsNotAlreadyAdded(summoner)) {
                        SummonerData summonerData = convertToSummonerData(summoner);
                        controller.getSummonerDataListView().getItems().add(summonerData);
                    } else {
                        log.error("Summoner {} was already added", summoner.getName());
                        throw LTCException.of("Summoner {} was already added", summoner.getName());
                    }
                });
            } else {
                Platform.runLater(() -> {
                    log.error("Summoner {} not found in region {}", summoner.getName(), summoner.getRegion().getTag());
                    throw LTCException.of(
                        "Summoner {} not found in region {}",
                        summoner.getName(),
                        summoner.getRegion().getTag()
                    );
                });
            }
        }
    }

    private boolean summonerIsNotAlreadyAdded(Summoner summoner) {
        return controller.getSummonerDataListView().getItems().stream()
            .noneMatch(summonerData -> summonerData.getSummonerName().equals(summoner.getName()));
    }
}
