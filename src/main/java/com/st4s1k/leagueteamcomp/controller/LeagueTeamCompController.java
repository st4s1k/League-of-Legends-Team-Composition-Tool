package com.st4s1k.leagueteamcomp.controller;

import com.st4s1k.leagueteamcomp.model.SummonerRole;
import com.st4s1k.leagueteamcomp.model.champion.AttributeRatings;
import com.st4s1k.leagueteamcomp.model.champion.Champion;
import com.st4s1k.leagueteamcomp.repository.ChampionRepository;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.SneakyThrows;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.st4s1k.leagueteamcomp.model.SummonerRole.*;
import static com.st4s1k.leagueteamcomp.service.SummonerRoleListGeneratorService.getCombinations;
import static java.util.stream.Collectors.*;

public class LeagueTeamCompController implements Initializable {

    private ChampionRepository championRepository;

    /* Role Compositions */

    private static final boolean DEFAULT_CHECKBOX_STATE = true;

    @FXML
    private TextArea textArea;

    @FXML
    private TextField tf1;
    @FXML
    private TextField tf2;
    @FXML
    private TextField tf3;
    @FXML
    private TextField tf4;
    @FXML
    private TextField tf5;

    @FXML
    private CheckBox cbTop1;
    @FXML
    private CheckBox cbMid1;
    @FXML
    private CheckBox cbAdc1;
    @FXML
    private CheckBox cbSup1;
    @FXML
    private CheckBox cbJgl1;

    @FXML
    private CheckBox cbTop2;
    @FXML
    private CheckBox cbMid2;
    @FXML
    private CheckBox cbAdc2;
    @FXML
    private CheckBox cbSup2;
    @FXML
    private CheckBox cbJgl2;

    @FXML
    private CheckBox cbTop3;
    @FXML
    private CheckBox cbMid3;
    @FXML
    private CheckBox cbAdc3;
    @FXML
    private CheckBox cbSup3;
    @FXML
    private CheckBox cbJgl3;

    @FXML
    private CheckBox cbTop4;
    @FXML
    private CheckBox cbMid4;
    @FXML
    private CheckBox cbAdc4;
    @FXML
    private CheckBox cbSup4;
    @FXML
    private CheckBox cbJgl4;

    @FXML
    private CheckBox cbTop5;
    @FXML
    private CheckBox cbMid5;
    @FXML
    private CheckBox cbAdc5;
    @FXML
    private CheckBox cbSup5;
    @FXML
    private CheckBox cbJgl5;

    /* Champion Suggestions  */

    @FXML
    private TextField enemySearchField;
    @FXML
    private ListView<String> enemyList;
    @FXML
    private Label enemyTeamResultLabel;

    @FXML
    private TextField allySearchField;
    @FXML
    private ListView<String> allyList;
    @FXML
    private Label allyTeamResultLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        championRepository = ChampionRepository.getInstance();
        initializeRoleCompositions();
        initializeChampionSuggestions();
    }

    private void initializeRoleCompositions() {
        resetAllCheckboxes();
        textArea.setEditable(false);
    }

    public void initializeChampionSuggestions() {
        enemyList.setCellFactory(param -> populateListCells());
        allyList.setCellFactory(param -> populateListCells());

        var championKeys = championRepository.getAllChampionKeys();
        TextFields.bindAutoCompletion(enemySearchField, request ->
            searchFieldAutoCompletion(request.getUserText(), championKeys));
        TextFields.bindAutoCompletion(allySearchField, request ->
            searchFieldAutoCompletion(request.getUserText(), championKeys));

        allyList.setOnKeyPressed(event -> handleDeleteButton(event, allyList));
        enemyList.setOnKeyPressed(event -> handleDeleteButton(event, enemyList));

        allyList.getItems().addListener((ListChangeListener<String>) change -> allyTeamResultLabel.setText(
            getTeamStats(allyList)
        ));
        enemyList.getItems().addListener((ListChangeListener<String>) change -> enemyTeamResultLabel.setText(
            getTeamStats(enemyList)
        ));
    }

    private String getTeamStats(ListView<String> allyList) {
        return
            "Damage:             " + getChampionInfoValue(allyList, AttributeRatings::getDamage) + "\n" +
            "Attack:             " + getChampionInfoValue(allyList, AttributeRatings::getAttack) + "\n" +
            "Defense:            " + getChampionInfoValue(allyList, AttributeRatings::getDefense) + "\n" +
            "Magic:              " + getChampionInfoValue(allyList, AttributeRatings::getMagic) + "\n" +
            "Difficulty:         " + getChampionInfoValue(allyList, AttributeRatings::getDifficulty) + "\n" +
            "Control:            " + getChampionInfoValue(allyList, AttributeRatings::getControl) + "\n" +
            "Toughness:          " + getChampionInfoValue(allyList, AttributeRatings::getToughness) + "\n" +
            "Mobility:           " + getChampionInfoValue(allyList, AttributeRatings::getMobility) + "\n" +
            "Utility:            " + getChampionInfoValue(allyList, AttributeRatings::getUtility) + "\n" +
            "Ability Reliance:   " + getChampionInfoValue(allyList, AttributeRatings::getAbilityReliance);
    }

    private String getChampionInfoValue(ListView<String> allyList, ToDoubleFunction<AttributeRatings> getValue) {
        var allyListItems = allyList.getItems();
        var value = allyListItems.stream()
            .map(championRepository::findChampionDataByKey)
            .flatMap(Optional::stream)
            .map(Champion::getAttributeRatings)
            .mapToDouble(getValue)
            .reduce(0.0, Double::sum) / (double) allyListItems.size();
        return String.format("%1$,.2f", value);
    }

    private ListCell<String> populateListCells() {
        return new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            public void updateItem(String championKey, boolean empty) {
                super.updateItem(championKey, empty);
                var championDataOptional = championRepository.findChampionDataByKey(championKey);
                if (empty || championKey == null || championDataOptional.isEmpty()) {
                    setText(null);
                    setGraphic(null);
                } else {
                    var championData = championDataOptional.get();
                    setText(championData.getName());
                    imageView.setImage(championData.getImage());
                    imageView.setFitWidth(30);
                    imageView.setFitHeight(30);
                    setGraphic(imageView);
                }
            }
        };
    }

    public void handleDeleteButton(KeyEvent event, ListView<String> listView) {
        if (event.getCode() == KeyCode.DELETE) {
            var selectedIdx = listView.getSelectionModel().getSelectedIndex();
            if (selectedIdx != -1) {
                var newSelectedIdx = selectedIdx == listView.getItems().size() - 1
                    ? selectedIdx - 1
                    : selectedIdx;
                listView.getItems().remove(selectedIdx);
                listView.getSelectionModel().select(newSelectedIdx);
            }
        }
    }

    @FXML
    protected void onGenerateButtonClick() {
        textArea.clear();
        var summonerNames = List.of(tf1.getText(),
            tf2.getText(),
            tf3.getText(),
            tf4.getText(),
            tf5.getText()
        );
        var playersToRoles = Stream.of(
                Map.entry(tf1.getText(), getRoles(cbTop1, cbMid1, cbAdc1, cbSup1, cbJgl1)),
                Map.entry(tf2.getText(), getRoles(cbTop2, cbMid2, cbAdc2, cbSup2, cbJgl2)),
                Map.entry(tf3.getText(), getRoles(cbTop3, cbMid3, cbAdc3, cbSup3, cbJgl3)),
                Map.entry(tf4.getText(), getRoles(cbTop4, cbMid4, cbAdc4, cbSup4, cbJgl4)),
                Map.entry(tf5.getText(), getRoles(cbTop5, cbMid5, cbAdc5, cbSup5, cbJgl5))
            ).filter(entry -> !entry.getKey().isBlank())
            .filter(entry -> summonerNames.stream().filter(entry.getKey()::equals).count() == 1)
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        var combinations = getCombinations(playersToRoles);
        var nameReservedSize = Integer.min(16, IntStream.of(
            tf1.getLength(),
            tf2.getLength(),
            tf3.getLength(),
            tf4.getLength(),
            tf5.getLength()
        ).max().getAsInt());
        var rows = combinations.stream()
            .map(playerToRoleRow -> formatPLayerToRoleRow(playerToRoleRow, nameReservedSize))
            .collect(toList());
        var rowsWithSeparator = rows.stream().map(this::appendSeparator).collect(toList());
        rowsWithSeparator.forEach(textArea::appendText);
        if (!rowsWithSeparator.isEmpty()) {
            var lastRow = rows.get(rowsWithSeparator.size() - 1);
            textArea.appendText(getSeparator(lastRow));
        }
    }

    private String formatPLayerToRoleRow(List<Map.Entry<String, SummonerRole>> playerToRoleRow, int nameReservedSize) {
        return playerToRoleRow.stream()
            .map(playerToRole -> formatPlayerToRoleEntry(playerToRole, nameReservedSize))
            .collect(joining("    "));
    }

    private String formatPlayerToRoleEntry(Map.Entry<String, SummonerRole> playerToRole, int nameReservedSize) {
        return String.format("%" + nameReservedSize + "s", playerToRole.getKey()) + ": " + playerToRole.getValue().toString();
    }

    private String appendSeparator(String playerToRoleRow) {
        return getSeparator(playerToRoleRow) + playerToRoleRow + "\n";
    }

    private String getSeparator(String playerToRoleRow) {
        return "-".repeat(playerToRoleRow.length()) + "\n";
    }

    @FXML
    protected void onResetButtonClick() {
        textArea.clear();
        resetAllCheckboxes();
    }

    @FXML
    protected void onEnemySearchAction() {
        onSearchAction(enemyList.getItems(), enemySearchField.getText());
    }

    @FXML
    protected void onAllySearchAction() {
        onSearchAction(allyList.getItems(), allySearchField.getText());
    }

    private void onSearchAction(
        ObservableList<String> listItems,
        String searchFieldText
    ) {
        if (isValidSearchFieldText(listItems, searchFieldText)) {
            championRepository.findChampionDataByName(searchFieldText)
                .map(Champion::getKey)
                .ifPresent(listItems::add);
        }
    }

    private boolean isValidSearchFieldText(ObservableList<String> listItems, String searchFieldText) {
        return listItems.size() < 5
            && !listItems.contains(searchFieldText)
            && championRepository.existsChampionDataByName(searchFieldText)
            && !championRepository.findChampionDataByName(searchFieldText)
            .map(Champion::getKey)
            .map(getChampionPool()::contains)
            .orElse(false);
    }

    private List<String> getChampionPool() {
        return Stream.concat(
            allyList.getItems().stream(),
            enemyList.getItems().stream()
        ).collect(toList());
    }

    @SneakyThrows
    private List<String> searchFieldAutoCompletion(
        String userText, List<String> championKeys
    ) {
        return championKeys.stream()
            .map(championRepository::findChampionDataByKey)
            .flatMap(Optional::stream)
            .map(Champion::getName)
            .filter(championName -> championName.toLowerCase().startsWith(userText.toLowerCase()))
            .collect(toList());
    }

    private static List<SummonerRole> getRoles(
        CheckBox cbTop,
        CheckBox cbMid,
        CheckBox cbAdc,
        CheckBox cbSup,
        CheckBox cbJgl
    ) {
        var roles = new ArrayList<SummonerRole>();
        if (cbTop.isSelected()) {
            roles.add(TOP);
        }
        if (cbMid.isSelected()) {
            roles.add(MID);
        }
        if (cbAdc.isSelected()) {
            roles.add(ADC);
        }
        if (cbSup.isSelected()) {
            roles.add(SUP);
        }
        if (cbJgl.isSelected()) {
            roles.add(JGL);
        }
        return roles;
    }

    private void resetAllCheckboxes() {
        cbTop1.setSelected(DEFAULT_CHECKBOX_STATE);
        cbMid1.setSelected(DEFAULT_CHECKBOX_STATE);
        cbAdc1.setSelected(DEFAULT_CHECKBOX_STATE);
        cbSup1.setSelected(DEFAULT_CHECKBOX_STATE);
        cbJgl1.setSelected(DEFAULT_CHECKBOX_STATE);

        cbTop2.setSelected(DEFAULT_CHECKBOX_STATE);
        cbMid2.setSelected(DEFAULT_CHECKBOX_STATE);
        cbAdc2.setSelected(DEFAULT_CHECKBOX_STATE);
        cbSup2.setSelected(DEFAULT_CHECKBOX_STATE);
        cbJgl2.setSelected(DEFAULT_CHECKBOX_STATE);

        cbTop3.setSelected(DEFAULT_CHECKBOX_STATE);
        cbMid3.setSelected(DEFAULT_CHECKBOX_STATE);
        cbAdc3.setSelected(DEFAULT_CHECKBOX_STATE);
        cbSup3.setSelected(DEFAULT_CHECKBOX_STATE);
        cbJgl3.setSelected(DEFAULT_CHECKBOX_STATE);

        cbTop4.setSelected(DEFAULT_CHECKBOX_STATE);
        cbMid4.setSelected(DEFAULT_CHECKBOX_STATE);
        cbAdc4.setSelected(DEFAULT_CHECKBOX_STATE);
        cbSup4.setSelected(DEFAULT_CHECKBOX_STATE);
        cbJgl4.setSelected(DEFAULT_CHECKBOX_STATE);

        cbTop5.setSelected(DEFAULT_CHECKBOX_STATE);
        cbMid5.setSelected(DEFAULT_CHECKBOX_STATE);
        cbAdc5.setSelected(DEFAULT_CHECKBOX_STATE);
        cbSup5.setSelected(DEFAULT_CHECKBOX_STATE);
        cbJgl5.setSelected(DEFAULT_CHECKBOX_STATE);
    }
}