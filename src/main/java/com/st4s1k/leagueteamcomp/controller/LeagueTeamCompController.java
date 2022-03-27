package com.st4s1k.leagueteamcomp.controller;

import com.st4s1k.leagueteamcomp.model.SummonerRole;
import com.st4s1k.leagueteamcomp.model.champion.AttributeRatings;
import com.st4s1k.leagueteamcomp.model.champion.Champion;
import com.st4s1k.leagueteamcomp.service.LeagueTeamCompService;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.st4s1k.leagueteamcomp.model.SummonerRole.*;
import static com.st4s1k.leagueteamcomp.service.SummonerRoleListGeneratorService.getCombinations;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.*;
import static javafx.geometry.Pos.CENTER;
import static javafx.scene.control.SelectionMode.MULTIPLE;
import static org.controlsfx.control.textfield.TextFields.bindAutoCompletion;

public class LeagueTeamCompController implements Initializable {

    private static double xOffset = 0;
    private static double yOffset = 0;

    private LeagueTeamCompService service;

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

    @FXML
    private Button generateButton;
    @FXML
    private Button resetButton;

    /* Champion Suggestions  */

    private static final PseudoClass CHAMPION_STAT_GOOD_PSEUDO_CLASS = PseudoClass.getPseudoClass("good");
    private static final PseudoClass CHAMPION_STAT_BAD_PSEUDO_CLASS = PseudoClass.getPseudoClass("bad");

    @FXML
    private Label teamCompResultLabel;

    @FXML
    private TextField allySearchField;
    @FXML
    private ListView<String> allyList;
    @FXML
    private TextFlow allyTeamResultTextFlow;

    @FXML
    private TextField enemySearchField;
    @FXML
    private ListView<String> enemyList;
    @FXML
    private TextFlow enemyTeamResultTextFlow;

    @FXML
    private GridPane gridPane;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button closeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        service = LeagueTeamCompService.getInstance();
        initializeRoleCompositions();
        initializeChampionSuggestions();
    }

    protected void onGenerateButtonClick() {
        textArea.clear();
        var summonerNames = List.of(
            tf1.getText(),
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
            .toList();
        var rowsWithSeparator = rows.stream().map(this::appendSeparator).toList();
        rowsWithSeparator.forEach(textArea::appendText);
        if (!rowsWithSeparator.isEmpty()) {
            var lastRow = rows.get(rowsWithSeparator.size() - 1);
            textArea.appendText(getSeparator(lastRow));
        }
    }

    protected void onResetButtonClick() {
        textArea.clear();
        resetAllCheckboxes();
    }


    public void setStageAndSetupListeners(Stage stage) {
        tabPane.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        tabPane.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
        closeButton.setOnAction(actionEvent -> stage.close());
        minimizeButton.setOnAction(actionEvent -> stage.setIconified(true));
    }

    private void initializeRoleCompositions() {
        gridPane.setAlignment(CENTER);
        textArea.setEditable(false);
        resetAllCheckboxes();
        generateButton.setOnAction(actionEvent -> onGenerateButtonClick());
        resetButton.setOnAction(actionEvent -> onResetButtonClick());
    }

    public void initializeChampionSuggestions() {
        allyList.getSelectionModel().setSelectionMode(MULTIPLE);
        enemyList.getSelectionModel().setSelectionMode(MULTIPLE);

        allyList.setCellFactory(param -> populateListCells());
        enemyList.setCellFactory(param -> populateListCells());

        allySearchField.setOnAction(actionEvent -> onSearchAction(allyList.getItems(), allySearchField.getText()));
        enemySearchField.setOnAction(actionEvent -> onSearchAction(enemyList.getItems(), enemySearchField.getText()));
        bindAutoCompletion(enemySearchField, request -> searchFieldAutoCompletion(request.getUserText()));
        bindAutoCompletion(allySearchField, request -> searchFieldAutoCompletion(request.getUserText()));

        allyList.setOnKeyPressed(event -> handleDeleteButton(event, allyList));
        enemyList.setOnKeyPressed(event -> handleDeleteButton(event, enemyList));

        getChampionListChangeListener().onChanged(null);
        allyList.getItems().addListener(getChampionListChangeListener());
        enemyList.getItems().addListener(getChampionListChangeListener());
    }

    private ListChangeListener<String> getChampionListChangeListener() {
        return change -> {
            calculateTeamStats(allyList.getItems(), enemyList.getItems(), allyTeamResultTextFlow);
            calculateTeamStats(enemyList.getItems(), allyList.getItems(), enemyTeamResultTextFlow);
            long allyBadStatCount = getBadStatCount(allyTeamResultTextFlow);
            long enemyBadStatCount = getBadStatCount(enemyTeamResultTextFlow);
            if (allyBadStatCount > enemyBadStatCount) {
                teamCompResultLabel.setText("Enemy Team is better");
            } else if (allyBadStatCount < enemyBadStatCount) {
                teamCompResultLabel.setText("Ally Team is better");
            } else {
                teamCompResultLabel.setText("Teams are similar");
            }
        };
    }

    private long getBadStatCount(TextFlow textFlow) {
        return textFlow.getChildren().stream()
            .map(Node::getPseudoClassStates)
            .filter(pseudoClasses -> pseudoClasses.contains(CHAMPION_STAT_BAD_PSEUDO_CLASS))
            .count();
    }

    private void calculateTeamStats(
        List<? extends String> championList,
        List<? extends String> opponentChampionList,
        TextFlow textFlow
    ) {
        double damage = service.getChampionInfoValue(championList, AttributeRatings::getDamage, 3);
        double attack = service.getChampionInfoValue(championList, AttributeRatings::getAttack, 10);
        double defense = service.getChampionInfoValue(championList, AttributeRatings::getDefense, 10);
        double magic = service.getChampionInfoValue(championList, AttributeRatings::getMagic, 10);
        double difficulty = service.getChampionInfoValue(championList, AttributeRatings::getDifficulty, 3);
        double control = service.getChampionInfoValue(championList, AttributeRatings::getControl, 3);
        double toughness = service.getChampionInfoValue(championList, AttributeRatings::getToughness, 3);
        double mobility = service.getChampionInfoValue(championList, AttributeRatings::getMobility, 3);
        double utility = service.getChampionInfoValue(championList, AttributeRatings::getUtility, 3);
        double abilityReliance = service.getChampionInfoValue(championList, AttributeRatings::getAbilityReliance, 100);

        double opponentDamage = service.getChampionInfoValue(opponentChampionList, AttributeRatings::getDamage, 3);
        double opponentAttack = service.getChampionInfoValue(opponentChampionList, AttributeRatings::getAttack, 10);
        double opponentDefense = service.getChampionInfoValue(opponentChampionList, AttributeRatings::getDefense, 10);
        double opponentMagic = service.getChampionInfoValue(opponentChampionList, AttributeRatings::getMagic, 10);
        double opponentDifficulty = service.getChampionInfoValue(opponentChampionList, AttributeRatings::getDifficulty, 3);
        double opponentControl = service.getChampionInfoValue(opponentChampionList, AttributeRatings::getControl, 3);
        double opponentToughness = service.getChampionInfoValue(opponentChampionList, AttributeRatings::getToughness, 3);
        double opponentMobility = service.getChampionInfoValue(opponentChampionList, AttributeRatings::getMobility, 3);
        double opponentUtility = service.getChampionInfoValue(opponentChampionList, AttributeRatings::getUtility, 3);
        double opponentAbilityReliance = service.getChampionInfoValue(opponentChampionList, AttributeRatings::getAbilityReliance, 100);

        Text damageText = getStatText("Damage:", damage);
        Text attackText = getStatText("Attack:", attack);
        Text defenseText = getStatText("Defense:", defense);
        Text magicText = getStatText("Magic:", magic);
        Text difficultyText = getStatText("Difficulty:", difficulty);
        Text controlText = getStatText("Control:", control);
        Text toughnessText = getStatText("Toughness:", toughness);
        Text mobilityText = getStatText("Mobility:", mobility);
        Text utilityText = getStatText("Utility:", utility);
        Text abilityRelianceText = getStatText("Ability Reliance:", abilityReliance);

        colorStatText(damageText, damage, opponentDamage, Double::max);
        colorStatText(attackText, attack, opponentAttack, Double::max);
        colorStatText(defenseText, defense, opponentDefense, Double::max);
        colorStatText(magicText, magic, opponentMagic, Double::max);
        colorStatText(difficultyText, difficulty, opponentDifficulty, Double::min);
        colorStatText(controlText, control, opponentControl, Double::max);
        colorStatText(toughnessText, toughness, opponentToughness, Double::max);
        colorStatText(mobilityText, mobility, opponentMobility, Double::max);
        colorStatText(utilityText, utility, opponentUtility, Double::max);
        colorStatText(abilityRelianceText, abilityReliance, opponentAbilityReliance, Double::min);

        textFlow.getChildren().clear();
        textFlow.getChildren().addAll(damageText, new Text("\n"));
        textFlow.getChildren().addAll(attackText, new Text("\n"));
        textFlow.getChildren().addAll(defenseText, new Text("\n"));
        textFlow.getChildren().addAll(magicText, new Text("\n"));
        textFlow.getChildren().addAll(difficultyText, new Text("\n"));
        textFlow.getChildren().addAll(controlText, new Text("\n"));
        textFlow.getChildren().addAll(toughnessText, new Text("\n"));
        textFlow.getChildren().addAll(mobilityText, new Text("\n"));
        textFlow.getChildren().addAll(utilityText, new Text("\n"));
        textFlow.getChildren().add(abilityRelianceText);
    }

    private void colorStatText(
        Text statText,
        double value,
        double opponentValue,
        DoubleBinaryOperator operation
    ) {
        if (value == opponentValue) {
            statText.pseudoClassStateChanged(CHAMPION_STAT_BAD_PSEUDO_CLASS, false);
            statText.pseudoClassStateChanged(CHAMPION_STAT_GOOD_PSEUDO_CLASS, false);
        } else if (operation.applyAsDouble(value, opponentValue) == value) {
            statText.pseudoClassStateChanged(CHAMPION_STAT_BAD_PSEUDO_CLASS, false);
            statText.pseudoClassStateChanged(CHAMPION_STAT_GOOD_PSEUDO_CLASS, true);
        } else {
            statText.pseudoClassStateChanged(CHAMPION_STAT_BAD_PSEUDO_CLASS, true);
            statText.pseudoClassStateChanged(CHAMPION_STAT_GOOD_PSEUDO_CLASS, false);
        }
    }

    private Text getStatText(String label, double value) {
        return new Text(getStatString(label, value));
    }

    private String getStatString(String label, double value) {
        return formatStatLabel(label).concat(formatStatValue(value));
    }

    private String formatStatLabel(String label) {
        return String.format("%-18s", label);
    }

    private String formatStatValue(double value) {
        return String.format("%4s / 10", String.format("%1$,.1f", value));
    }

    private ListCell<String> populateListCells() {
        return new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            public void updateItem(String championKey, boolean empty) {
                super.updateItem(championKey, empty);
                var championDataOptional = service.findChampionDataByKey(championKey);
                if (empty || championKey == null || championDataOptional.isEmpty()) {
                    setPrefHeight(60);
                    setGraphic(null);
                } else {
                    var championData = championDataOptional.get();
                    imageView.setImage(championData.getImage());
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    setGraphic(imageView);
                }
            }
        };
    }

    public void handleDeleteButton(KeyEvent event, ListView<String> listView) {
        if (event.getCode() == KeyCode.DELETE) {
            var selectedItems = listView.getSelectionModel().getSelectedItems();
            if (!selectedItems.isEmpty()) {
                listView.getItems().removeAll(selectedItems);
                listView.getSelectionModel().clearSelection();
            }
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

    private void onSearchAction(
        ObservableList<String> listItems,
        String searchFieldText
    ) {
        if (isValidSearchFieldText(listItems, searchFieldText)) {
            service.findChampionDataByName(searchFieldText)
                .map(Champion::getKey)
                .ifPresent(listItems::add);
        }
    }

    private boolean isValidSearchFieldText(ObservableList<String> listItems, String searchFieldText) {
        return listItems.size() < 5
            && !listItems.contains(searchFieldText)
            && service.existsChampionDataByName(searchFieldText)
            && !service.findChampionDataByName(searchFieldText)
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

    private List<String> searchFieldAutoCompletion(String userText) {
        return service.getAllChampionKeys().stream()
            .filter(not(getChampionPool()::contains))
            .map(service::findChampionDataByKey)
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