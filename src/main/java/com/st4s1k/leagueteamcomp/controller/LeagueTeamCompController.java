package com.st4s1k.leagueteamcomp.controller;

import com.st4s1k.leagueteamcomp.model.SummonerRole;
import com.st4s1k.leagueteamcomp.model.champion.AttributeRatings;
import com.st4s1k.leagueteamcomp.model.champion.Champion;
import com.st4s1k.leagueteamcomp.model.champion.select.ChampSelectDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.TeamDTO;
import com.st4s1k.leagueteamcomp.service.LeagueTeamCompService;
import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;
import com.stirante.lolclient.ClientWebSocket;
import generated.LolChampSelectChampSelectPlayerSelection;
import generated.LolChampSelectChampSelectSession;
import generated.LolSummonerSummoner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import lombok.SneakyThrows;

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
    private ListView<SlotDTO> allyListView;
    @FXML
    private TextFlow allyTeamResultTextFlow;

    @FXML
    private TextField enemySearchField;
    @FXML
    private ListView<SlotDTO> enemyListView;
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

    private ChampSelectDTO champSelect;

    private ClientWebSocket socket;
    private ClientApi api;

    @Override
    @SneakyThrows
    public void initialize(URL url, ResourceBundle resourceBundle) {
        service = LeagueTeamCompService.getInstance();
        api = new ClientApi();
        initializeRoleCompositions();
        initializeChampionSuggestions();
    }

    public void stop() {
        api.stop();
        socket.close();
    }

    @SneakyThrows
    private void registerLCUListeners() {
        api.addClientConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                registerSocketListener(api);
            }

            @Override
            public void onClientDisconnected() {
                socket.close();
            }
        });
    }

    @SneakyThrows
    private void registerSocketListener(ClientApi api) {
        if (!api.isAuthorized()) {
            System.out.println("Not logged in!");
            return;
        }
        socket = api.openWebSocket();
        socket.setSocketListener(new ClientWebSocket.SocketListener() {
            @Override
            public void onEvent(ClientWebSocket.Event event) {
                Platform.runLater(() -> {
                    if (event.getEventType().equals("Update") &&
                        event.getUri().equals("/lol-champ-select/v1/session") &&
                        event.getData() instanceof LolChampSelectChampSelectSession session) {
                        updateTeam(session.myTeam, champSelect.getAllyTeam());
                        updateTeam(session.theirTeam, champSelect.getEnemyTeam());
                    }
                });
            }

            @Override
            public void onClose(int code, String reason) {
                System.out.println("Socket closed, reason: " + reason);
            }
        });
    }

    private void updateTeam(List<LolChampSelectChampSelectPlayerSelection> session, TeamDTO team) {
        session.forEach(playerSelection -> service.findChampionDataById(playerSelection.championId)
            .ifPresent(champion -> {
                int slotId = playerSelection.cellId.intValue();
                int slotIndex = slotId % 5;
                SlotDTO slot = team.getSlot(slotIndex);
                slot.setSlotId(slotId);
                String summonerName = getSummonerName(playerSelection.summonerId);
                slot.setSummonerName(summonerName);
                slot.setChampion(champion);
            }));
    }

    @SneakyThrows
    private String getSummonerName(Long summonerId) {
        return Optional.ofNullable(api.executeGet(
                "/lol-summoner/v1/summoners/" + summonerId,
                LolSummonerSummoner.class
            ).getResponseObject())
            .map(summoner -> summoner.displayName)
            .orElse("");
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
        registerLCUListeners();
        champSelect = new ChampSelectDTO();
        ObservableList<SlotDTO> allyItems = FXCollections.observableArrayList(SlotDTO.extractor());
        ObservableList<SlotDTO> enemyItems = FXCollections.observableArrayList(SlotDTO.extractor());
        allyItems.addAll(champSelect.getAllyTeam().getSlots());
        enemyItems.addAll(champSelect.getEnemyTeam().getSlots());
        allyListView.setItems(allyItems);
        enemyListView.setItems(enemyItems);
        allyListView.setCellFactory(param -> populateListCells());
        enemyListView.setCellFactory(param -> populateListCells());

        initializeTemporaryTeamStats();
    }

    private void initializeTemporaryTeamStats() {
        allyListView.getSelectionModel().setSelectionMode(MULTIPLE);
        enemyListView.getSelectionModel().setSelectionMode(MULTIPLE);

        allySearchField.setOnAction(actionEvent -> onSearchAction(allyListView, allySearchField.getText()));
        enemySearchField.setOnAction(actionEvent -> onSearchAction(enemyListView, enemySearchField.getText()));

        bindAutoCompletion(enemySearchField, request -> searchFieldAutoCompletion(request.getUserText()));
        bindAutoCompletion(allySearchField, request -> searchFieldAutoCompletion(request.getUserText()));

        allyListView.setOnKeyPressed(event -> handleDeleteButton(event, allyListView));
        enemyListView.setOnKeyPressed(event -> handleDeleteButton(event, enemyListView));

        getChampionListChangeListener().onChanged(null);
        allyListView.getItems().addListener(getChampionListChangeListener());
        enemyListView.getItems().addListener(getChampionListChangeListener());
    }

    private ListChangeListener<SlotDTO> getChampionListChangeListener() {
        return change -> {
            setTeamAttributeRatings(champSelect.getAllyTeam());
            setTeamAttributeRatings(champSelect.getEnemyTeam());
            calculateTeamStats(
                champSelect.getAllyTeam().getAttributeRatings(),
                champSelect.getEnemyTeam().getAttributeRatings(),
                allyTeamResultTextFlow
            );
            calculateTeamStats(
                champSelect.getEnemyTeam().getAttributeRatings(),
                champSelect.getAllyTeam().getAttributeRatings(),
                enemyTeamResultTextFlow
            );
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
        AttributeRatings stats,
        AttributeRatings opponentStats,
        TextFlow textFlow
    ) {
        Text damageText = getStatText("Damage:", stats.getDamage());
        Text attackText = getStatText("Attack:", stats.getAttack());
        Text defenseText = getStatText("Defense:", stats.getDefense());
        Text magicText = getStatText("Magic:", stats.getMagic());
        Text difficultyText = getStatText("Difficulty:", stats.getDifficulty());
        Text controlText = getStatText("Control:", stats.getControl());
        Text toughnessText = getStatText("Toughness:", stats.getToughness());
        Text mobilityText = getStatText("Mobility:", stats.getMobility());
        Text utilityText = getStatText("Utility:", stats.getUtility());
        Text abilityRelianceText = getStatText("Ability Reliance:", stats.getAbilityReliance());

        colorStatText(damageText, stats.getDamage(), opponentStats.getDamage(), Double::max);
        colorStatText(attackText, stats.getAttack(), opponentStats.getAttack(), Double::max);
        colorStatText(defenseText, stats.getDefense(), opponentStats.getDefense(), Double::max);
        colorStatText(magicText, stats.getMagic(), opponentStats.getMagic(), Double::max);
        colorStatText(difficultyText, stats.getDifficulty(), opponentStats.getDifficulty(), Double::min);
        colorStatText(controlText, stats.getControl(), opponentStats.getControl(), Double::max);
        colorStatText(toughnessText, stats.getToughness(), opponentStats.getToughness(), Double::max);
        colorStatText(mobilityText, stats.getMobility(), opponentStats.getMobility(), Double::max);
        colorStatText(utilityText, stats.getUtility(), opponentStats.getUtility(), Double::max);
        colorStatText(abilityRelianceText, stats.getAbilityReliance(), opponentStats.getAbilityReliance(), Double::min);

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

    private void setTeamAttributeRatings(TeamDTO champSelect) {
        AttributeRatings enemyTeamStats = champSelect.getAttributeRatings();
        List<String> enemyChampionKeys = champSelect.getSlots().stream()
            .map(SlotDTO::getChampion)
            .flatMap(Optional::stream)
            .map(Champion::getKey)
            .toList();
        enemyTeamStats.setDamage(service.getChampionInfoValue(enemyChampionKeys, AttributeRatings::getDamage, 3));
        enemyTeamStats.setAttack(service.getChampionInfoValue(enemyChampionKeys, AttributeRatings::getAttack, 10));
        enemyTeamStats.setDefense(service.getChampionInfoValue(enemyChampionKeys, AttributeRatings::getDefense, 10));
        enemyTeamStats.setMagic(service.getChampionInfoValue(enemyChampionKeys, AttributeRatings::getMagic, 10));
        enemyTeamStats.setDifficulty(service.getChampionInfoValue(enemyChampionKeys, AttributeRatings::getDifficulty, 3));
        enemyTeamStats.setControl(service.getChampionInfoValue(enemyChampionKeys, AttributeRatings::getControl, 3));
        enemyTeamStats.setToughness(service.getChampionInfoValue(enemyChampionKeys, AttributeRatings::getToughness, 3));
        enemyTeamStats.setMobility(service.getChampionInfoValue(enemyChampionKeys, AttributeRatings::getMobility, 3));
        enemyTeamStats.setUtility(service.getChampionInfoValue(enemyChampionKeys, AttributeRatings::getUtility, 3));
        enemyTeamStats.setAbilityReliance(service.getChampionInfoValue(enemyChampionKeys, AttributeRatings::getAbilityReliance, 100));
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

    private ListCell<SlotDTO> populateListCells() {
        return new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            public void updateItem(SlotDTO slot, boolean empty) {
                super.updateItem(slot, empty);
                if (empty || slot == null) {
                    setPrefHeight(60);
                    setGraphic(null);
                } else {
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    imageView.setImage(slot.getImage());
                    setGraphic(imageView);
                }
            }
        };
    }

    public void handleDeleteButton(KeyEvent event, ListView<SlotDTO> listView) {
        if (event.getCode() == KeyCode.DELETE) {
            var selectedItems = listView.getSelectionModel().getSelectedItems();
            if (!selectedItems.isEmpty()) {
                selectedItems.forEach(SlotDTO::clear);
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
        ListView<SlotDTO> championListView,
        String searchFieldText
    ) {

        if (isValidSearchFieldText(championListView.getItems(), searchFieldText)) {
            service.findChampionDataByName(searchFieldText)
                .ifPresent(champion -> getSearchActionItems(championListView).stream()
                    .filter(slot -> slot.getChampion().isEmpty())
                    .findFirst()
                    .ifPresent(slot -> slot.setChampion(champion)));
        }
    }

    private ObservableList<SlotDTO> getSearchActionItems(ListView<SlotDTO> championListView) {
        ObservableList<SlotDTO> selectedItems = championListView.getSelectionModel().getSelectedItems();
        return selectedItems.isEmpty() || selectedItems.stream()
            .map(SlotDTO::getChampion)
            .noneMatch(Optional::isEmpty)
            ? championListView.getItems()
            : selectedItems;
    }

    private boolean isValidSearchFieldText(List<SlotDTO> slots, String searchFieldText) {
        List<String> championKeys = slots.stream()
            .map(SlotDTO::getChampion)
            .flatMap(Optional::stream)
            .map(Champion::getKey)
            .toList();
        long filledSlots = slots.stream().filter(SlotDTO::isFilled).count();
        return filledSlots < 5
            && !championKeys.contains(searchFieldText)
            && service.existsChampionDataByName(searchFieldText)
            && !service.findChampionDataByName(searchFieldText)
            .map(champSelect.getChampionPool()::contains)
            .orElse(false);
    }

    private List<String> searchFieldAutoCompletion(String userText) {
        List<String> championPoolKeys = champSelect.getChampionPool().stream()
            .map(Champion::getKey)
            .toList();
        return service.getAllChampionKeys().stream()
            .filter(not(championPoolKeys::contains))
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