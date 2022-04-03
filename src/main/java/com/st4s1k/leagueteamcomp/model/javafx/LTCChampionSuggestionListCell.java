package com.st4s1k.leagueteamcomp.model.javafx;

import com.st4s1k.leagueteamcomp.model.champion.select.ChampionSlotDTO;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class LTCChampionSuggestionListCell extends ListCell<ListView<ChampionSlotDTO>> {

    @Override
    public void updateItem(ListView<ChampionSlotDTO> item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            setGraphic(item);
        }
    }
}
