package com.st4s1k.leagueteamcomp.service;

import com.st4s1k.leagueteamcomp.model.champion.select.ChampSelectDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.ChampionSlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SummonerDTO;
import com.st4s1k.leagueteamcomp.model.interfaces.ObservablesProvider;
import com.st4s1k.leagueteamcomp.model.interfaces.SlotItem;
import com.st4s1k.leagueteamcomp.model.javafx.LTCChampionListCell;
import com.st4s1k.leagueteamcomp.model.javafx.LTCChampionSuggestionListCell;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class ChampionSuggestionService {

    private static ChampionSuggestionService INSTANCE;

    public static ChampionSuggestionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChampionSuggestionService();
        }
        return INSTANCE;
    }

    @SuppressWarnings("SameParameterValue")
    public void initializeSuggestionsListView(
        ChampSelectDTO champSelect,
        ListView<ListView<ChampionSlotDTO>> suggestionsListView,
        double imageSize
    ) {
        List<ListView<ChampionSlotDTO>> championSuggestionsListViews = champSelect.getAllyTeam().getSlots().stream()
            .map(SlotDTO::getItem)
            .flatMap(Optional::stream)
            .map(SummonerDTO::getChampionSuggestions)
            .map(championSuggestions ->
                initializeSuggestionsSubListView(championSuggestions, imageSize))
            .toList();
        initializeSuggestionsListView(championSuggestionsListViews, suggestionsListView);
    }

    public ListView<ChampionSlotDTO> initializeSuggestionsSubListView(
        List<ChampionSlotDTO> championSuggestions,
        double imageSize
    ) {
        var suggestionsListView = new ListView<ChampionSlotDTO>();
        suggestionsListView.getStyleClass().addAll("champion-list-view", "suggestions-sub-list-view");
        initializeChampionListView(championSuggestions, suggestionsListView, imageSize);
        return suggestionsListView;
    }

    public <T extends SlotItem, S extends SlotDTO<T>> void initializeChampionListView(
        List<S> slots,
        ListView<S> listView,
        double imageSize
    ) {
        ObservableList<S> itemsList = FXCollections.observableArrayList(ObservablesProvider::getObservables);
        itemsList.addAll(slots);
        listView.setItems(itemsList);
        listView.setCellFactory(param -> new LTCChampionListCell<>(imageSize));
    }

    public void initializeSuggestionsListView(
        List<ListView<ChampionSlotDTO>> items,
        ListView<ListView<ChampionSlotDTO>> listView
    ) {
        ObservableList<ListView<ChampionSlotDTO>> itemsList = FXCollections.observableArrayList(param ->
            param.getItems().stream()
                .map(ObservablesProvider::getObservables)
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .toArray(Observable[]::new));
        itemsList.addAll(items);
        listView.setItems(itemsList);
        listView.setCellFactory(param -> new LTCChampionSuggestionListCell());
    }
}
