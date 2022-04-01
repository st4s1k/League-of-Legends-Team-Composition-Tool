package com.st4s1k.leagueteamcomp.controller;

import com.st4s1k.leagueteamcomp.exceptions.LTCException;
import com.st4s1k.leagueteamcomp.model.champion.AttributeRatingsDTO;
import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.ChampSelectDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SummonerDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.TeamDTO;
import com.st4s1k.leagueteamcomp.model.enums.SummonerRoleEnum;
import com.st4s1k.leagueteamcomp.model.interfaces.ChampionHolder;
import com.st4s1k.leagueteamcomp.model.interfaces.Clearable;
import com.st4s1k.leagueteamcomp.model.interfaces.SlotItem;
import com.st4s1k.leagueteamcomp.service.ChampionSuggestionService;
import com.st4s1k.leagueteamcomp.service.LeagueTeamCompService;
import com.st4s1k.leagueteamcomp.service.SummonerRoleListGeneratorService;
import com.st4s1k.leagueteamcomp.utils.Utils;
import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;
import com.stirante.lolclient.ClientWebSocket;
import generated.LolChampSelectChampSelectPlayerSelection;
import generated.LolChampSelectChampSelectSession;
import generated.LolSummonerSummoner;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.*;
import java.util.function.DoubleBinaryOperator;

import static com.st4s1k.leagueteamcomp.model.enums.SummonerRoleEnum.*;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.*;
import static javafx.scene.control.SelectionMode.MULTIPLE;
import static org.controlsfx.control.textfield.TextFields.bindAutoCompletion;

@Slf4j
public class LeagueTeamCompController implements Initializable {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * App controls                                        *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML
    private TabPane tabPane;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button closeButton;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Role Compositions                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Champion Suggestions                                *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static final PseudoClass CHAMPION_STAT_GOOD_PSEUDO_CLASS = PseudoClass.getPseudoClass("good");
    private static final PseudoClass CHAMPION_STAT_BAD_PSEUDO_CLASS = PseudoClass.getPseudoClass("bad");

    @FXML
    private Label teamCompResultLabel;

    @FXML
    private TextField allySearchField;
    @FXML
    private ListView<SlotDTO<SummonerDTO>> allyListView;
    @FXML
    private ListView<SlotDTO<ChampionDTO>> allyBanListView;
    @FXML
    private ListView<ListView<SlotDTO<ChampionDTO>>> suggestionsListView;
    @FXML
    private TextFlow allyTeamResultTextFlow;

    @FXML
    private TextField enemySearchField;
    @FXML
    private ListView<SlotDTO<SummonerDTO>> enemyListView;
    @FXML
    private ListView<SlotDTO<ChampionDTO>> enemyBanListView;
    @FXML
    private TextFlow enemyTeamResultTextFlow;

    @FXML
    private ToggleButton manualModeToggle;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Controller fields                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static double xOffset = 0;
    private static double yOffset = 0;

    private final LeagueTeamCompService service = LeagueTeamCompService.getInstance();
    private final ChampionSuggestionService suggestionService = ChampionSuggestionService.getInstance();
    private final SummonerRoleListGeneratorService roleListGeneratorService = SummonerRoleListGeneratorService.getInstance();
    private final ChampSelectDTO champSelect = new ChampSelectDTO();
    private final ClientApi api = new ClientApi();

    private ClientWebSocket socket;

    public void setStageAndSetupListeners(Stage stage) {
        tabPane.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        tabPane.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
    }

    public void setCloseButtonAction(Runnable closeAction) {
        closeButton.setOnAction(actionEvent -> closeAction.run());
    }

    public void setMinimizeButtonAction(Runnable minimizeAction) {
        minimizeButton.setOnAction(actionEvent -> minimizeAction.run());
    }

    public void stop() {
        api.stop();
    }

    @Override
    @SneakyThrows
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerLeagueClientListeners();
        initializeRoleCompositions();
        initializeChampionSuggestions();
    }

    @SneakyThrows
    private void registerLeagueClientListeners() {
        log.info("Registering League Client listeners");
        api.addClientConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                log.info("League Client connected");
                registerSocketListener();
            }

            @Override
            public void onClientDisconnected() {
                log.info("League Client disconnected");
                if (socket != null) {
                    socket.close();
                }
            }
        });
    }

    @SneakyThrows
    private void registerSocketListener() {
        log.info("Registering socket listener");
        if (!api.isAuthorized()) {
            log.warn("Not logged in!");
            return;
        }
        socket = api.openWebSocket();
        socket.setSocketListener(new ClientWebSocket.SocketListener() {
            @Override
            public void onEvent(ClientWebSocket.Event event) {
                if (event.getEventType().equals("Update") &&
                    event.getUri().equals("/lol-champ-select/v1/session") &&
                    event.getData() instanceof LolChampSelectChampSelectSession session) {
                    Platform.runLater(() -> LeagueTeamCompController.this.onChampSelectUpdate(session));
                }
            }

            @Override
            public void onClose(int code, String reason) {
                log.warn("Socket closed, code: {}, reason: {}", code, reason);
            }
        });
    }

    private void onChampSelectUpdate(LolChampSelectChampSelectSession session) {
        if (!manualModeToggle.isSelected()) {
            updateTeam(champSelect.getAllyTeam(), session.myTeam, session.bans.myTeamBans);
            updateTeam(champSelect.getEnemyTeam(), session.theirTeam, session.bans.theirTeamBans);
        }
    }

    private void updateTeam(
        TeamDTO team,
        List<LolChampSelectChampSelectPlayerSelection> playerSelectionList,
        List<Integer> bans
    ) {
        playerSelectionList.forEach(playerSelection -> service.findChampionDataById(playerSelection.championId)
            .ifPresent(champion -> populateSummonerSlot(playerSelection, team, champion)));
        updateBans(team, bans);
    }

    private void updateBans(TeamDTO team, List<Integer> bans) {
        List<Integer> bannedChampionIdsBefore = team.getBannedChampionIds();
        boolean shouldLog = Utils.notSame(bannedChampionIdsBefore, bans);
        if (shouldLog) {
            log.debug("team: {}", team.getTeamSide());
            log.debug("response bans: {}", bans);
        }

        bans.stream()
            .filter(championId -> team.getBannedChampions().stream()
                .map(ChampionDTO::getId)
                .noneMatch(championId::equals))
            .map(service::findChampionDataById)
            .flatMap(Optional::stream)
            .forEach(champion -> team.getBans().stream()
                .filter(SlotDTO::isChampionNotSelected)
                .findFirst()
                .ifPresent(slot -> slot.setChampion(champion)));

        if (shouldLog) {
            log.debug("teamBans: {}\n", team.getBannedChampionIds());
        }
    }

    private void populateSummonerSlot(
        LolChampSelectChampSelectPlayerSelection playerSelection,
        TeamDTO team,
        ChampionDTO champion
    ) {
        int slotId = playerSelection.cellId.intValue();
        int slotIndex = slotId % 5;
        team.getSlot(slotIndex).getItem().ifPresent(summoner -> {
            summoner.setSlotId(slotId);
            summoner.setSummonerName(getSummonerName(playerSelection.summonerId));
            summoner.setChampion(champion);
        });
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

    private void initializeRoleCompositions() {
        log.info("Initializing role compositions");
        textArea.setEditable(false);
        resetAllCheckboxes();
        generateButton.setOnAction(event -> onGenerateButtonClick());
        resetButton.setOnAction(event -> onResetButtonClick());
    }

    private void onGenerateButtonClick() {
        textArea.clear();
        Map<String, List<SummonerRoleEnum>> playersToRoles = getPlayersToRolesMap();

        List<Map<String, SummonerRoleEnum>> combinations = roleListGeneratorService.getCombinations(playersToRoles);

        int nameReservedSize = calculateSummonerNameReservedSize(playersToRoles.keySet());
        List<String> rows = combinations.stream().map(row -> formatPLayerToRoleRow(row, nameReservedSize)).toList();
        List<String> rowsWithSeparator = rows.stream().map(this::appendSeparator).toList();

        StringBuilder sb = new StringBuilder();
        rowsWithSeparator.forEach(sb::append);
        if (!rowsWithSeparator.isEmpty()) {
            String lastRow = rows.get(rowsWithSeparator.size() - 1);
            sb.append(getSeparator(lastRow));
        }
        textArea.setText(sb.toString());
    }

    private Map<String, List<SummonerRoleEnum>> getPlayersToRolesMap() {
        validateSummonerNames();
        return Map.ofEntries(
                Map.entry(tf1.getText(), getRoles(cbTop1, cbMid1, cbAdc1, cbSup1, cbJgl1)),
                Map.entry(tf2.getText(), getRoles(cbTop2, cbMid2, cbAdc2, cbSup2, cbJgl2)),
                Map.entry(tf3.getText(), getRoles(cbTop3, cbMid3, cbAdc3, cbSup3, cbJgl3)),
                Map.entry(tf4.getText(), getRoles(cbTop4, cbMid4, cbAdc4, cbSup4, cbJgl4)),
                Map.entry(tf5.getText(), getRoles(cbTop5, cbMid5, cbAdc5, cbSup5, cbJgl5))
            ).entrySet().stream()
            .filter(entry -> !entry.getKey().isBlank())
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void validateSummonerNames() {
        List<String> summonerNames = List.of(
            tf1.getText(),
            tf2.getText(),
            tf3.getText(),
            tf4.getText(),
            tf5.getText()
        );
        Set<String> summonerNameSet = Set.copyOf(summonerNames);
        if (summonerNames.size() != summonerNameSet.size()) {
            throw new LTCException("Summoner names must be unique!");
        }
    }

    private static List<SummonerRoleEnum> getRoles(
        CheckBox cbTop,
        CheckBox cbMid,
        CheckBox cbAdc,
        CheckBox cbSup,
        CheckBox cbJgl
    ) {
        List<SummonerRoleEnum> roles = new ArrayList<>();
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

    private int calculateSummonerNameReservedSize(Set<String> summonerNames) {
        return Integer.min(16, summonerNames.stream().mapToInt(String::length).max().orElse(16));
    }

    private String formatPLayerToRoleRow(
        Map<String, SummonerRoleEnum> playerToRoleRow,
        int nameReservedSize
    ) {
        return playerToRoleRow.entrySet().stream()
            .map(playerToRole -> formatPlayerToRoleEntry(playerToRole, nameReservedSize))
            .collect(joining("    "));
    }

    private String formatPlayerToRoleEntry(Map.Entry<String, SummonerRoleEnum> playerToRole, int nameReservedSize) {
        return String.format("%" + nameReservedSize + "s", playerToRole.getKey()) + ": " + playerToRole.getValue().toString();
    }

    private String appendSeparator(String playerToRoleRow) {
        return getSeparator(playerToRoleRow) + playerToRoleRow + "\n";
    }

    private String getSeparator(String playerToRoleRow) {
        return "-".repeat(playerToRoleRow.length()) + "\n";
    }

    private void onResetButtonClick() {
        textArea.clear();
        resetAllCheckboxes();
    }

    private void resetAllCheckboxes() {
        List.of(
            cbTop1, cbMid1, cbAdc1, cbSup1, cbJgl1,
            cbTop2, cbMid2, cbAdc2, cbSup2, cbJgl2,
            cbTop3, cbMid3, cbAdc3, cbSup3, cbJgl3,
            cbTop4, cbMid4, cbAdc4, cbSup4, cbJgl4,
            cbTop5, cbMid5, cbAdc5, cbSup5, cbJgl5
        ).forEach(cb -> cb.setSelected(DEFAULT_CHECKBOX_STATE));
    }

    public void initializeChampionSuggestions() {
        log.info("Initializing champion suggestions");
        Utils.disableInteraction(allyListView);
        Utils.disableInteraction(enemyListView);
        Utils.disableInteraction(allyBanListView);
        Utils.disableInteraction(enemyBanListView);
        Utils.disableInteraction(suggestionsListView);
        suggestionService.initializeChampionListView(champSelect.getAllyTeam().getSlots(), allyListView, 50);
        suggestionService.initializeChampionListView(champSelect.getEnemyTeam().getSlots(), enemyListView, 50);
        suggestionService.initializeChampionListView(champSelect.getAllyTeam().getBans(), allyBanListView, 40);
        suggestionService.initializeChampionListView(champSelect.getEnemyTeam().getBans(), enemyBanListView, 40);
        suggestionService.initializeSuggestionsListView(champSelect, suggestionsListView, 40);
        manualModeToggle.setOnAction(event -> {
            if (manualModeToggle.isSelected()) {
                Utils.enableInteraction(allyListView);
                Utils.enableInteraction(enemyListView);
                Utils.enableInteraction(allyBanListView);
                Utils.enableInteraction(enemyBanListView);
                Utils.enableInteraction(suggestionsListView);
            } else {
                Utils.disableInteraction(allyListView);
                Utils.disableInteraction(enemyListView);
                Utils.disableInteraction(allyBanListView);
                Utils.disableInteraction(enemyBanListView);
                Utils.disableInteraction(suggestionsListView);
                allyListView.getSelectionModel().clearSelection();
                enemyListView.getSelectionModel().clearSelection();
                allyBanListView.getSelectionModel().clearSelection();
                enemyBanListView.getSelectionModel().clearSelection();
                suggestionsListView.getItems().forEach(item -> item.getSelectionModel().clearSelection());
            }
        });
        initializeTemporaryTeamStats();
    }

    private void initializeTemporaryTeamStats() {
        allyListView.getSelectionModel().setSelectionMode(MULTIPLE);
        enemyListView.getSelectionModel().setSelectionMode(MULTIPLE);
        allyBanListView.getSelectionModel().setSelectionMode(MULTIPLE);
        enemyBanListView.getSelectionModel().setSelectionMode(MULTIPLE);
        suggestionsListView.getItems().forEach(item -> item.getSelectionModel().setSelectionMode(MULTIPLE));

        allySearchField.setOnAction(actionEvent -> onSearchAction(allyListView, allySearchField.getText()));
        enemySearchField.setOnAction(actionEvent -> onSearchAction(enemyListView, enemySearchField.getText()));

        bindAutoCompletion(enemySearchField, request -> searchFieldAutoCompletion(request.getUserText()));
        bindAutoCompletion(allySearchField, request -> searchFieldAutoCompletion(request.getUserText()));

        allyListView.setOnKeyPressed(event -> handleDeleteButton(event, allyListView));
        enemyListView.setOnKeyPressed(event -> handleDeleteButton(event, enemyListView));
        allyBanListView.setOnKeyPressed(event -> handleDeleteButton(event, allyBanListView));
        enemyBanListView.setOnKeyPressed(event -> handleDeleteButton(event, enemyBanListView));
        suggestionsListView.getItems().forEach(item -> item.setOnKeyPressed(event -> handleDeleteButton(event, item)));

        getChampionListChangeListener().onChanged(null);
        allyListView.getItems().addListener(getChampionListChangeListener());
        enemyListView.getItems().addListener(getChampionListChangeListener());
    }

    private void onSearchAction(
        ListView<SlotDTO<SummonerDTO>> championListView,
        String searchFieldText
    ) {
        if (isValidSearchFieldText(championListView.getItems(), searchFieldText)) {
            log.debug("Search field text \"{}\" is valid", searchFieldText);
            service.findChampionDataByName(searchFieldText)
                .ifPresentOrElse(
                    champion -> getSearchActionItems(championListView).stream()
                        .filter(ChampionHolder::isChampionNotSelected)
                        .findFirst()
                        .ifPresentOrElse(
                            slot -> {
                                log.debug("Setting champion {} in slot {}", champion.getName(), slot);
                                slot.setChampion(champion);
                                log.debug("Champion {} set in slot {}", champion.getName(), slot);
                            },
                            () -> log.debug("No empty slots found")
                        ),
                    () -> log.debug("Champion not found for name: {}", searchFieldText));
        } else {
            log.debug("Search field text is not valid: {}", searchFieldText);
        }
    }

    private boolean isValidSearchFieldText(List<SlotDTO<SummonerDTO>> slots, String searchFieldText) {
        List<String> championKeys = slots.stream()
            .map(ChampionHolder::getChampion)
            .flatMap(Optional::stream)
            .map(ChampionDTO::getKey)
            .toList();
        long filledSlots = slots.stream().filter(ChampionHolder::isChampionSelected).count();
        return filledSlots < 5
            && !championKeys.contains(searchFieldText)
            && service.existsChampionDataByName(searchFieldText)
            && !service.findChampionDataByName(searchFieldText)
            .map(champSelect.getChampionPool()::contains)
            .orElse(false);
    }

    private ObservableList<SlotDTO<SummonerDTO>> getSearchActionItems(ListView<SlotDTO<SummonerDTO>> championListView) {
        ObservableList<SlotDTO<SummonerDTO>> selectedItems = championListView.getSelectionModel().getSelectedItems();
        return selectedItems.isEmpty() || selectedItems.stream().noneMatch(ChampionHolder::isChampionNotSelected)
            ? championListView.getItems()
            : selectedItems;
    }

    private List<String> searchFieldAutoCompletion(String userText) {
        List<String> championPoolKeys = champSelect.getChampionPool().stream()
            .map(ChampionDTO::getKey)
            .toList();
        return service.getAllChampionKeys().stream()
            .filter(not(championPoolKeys::contains))
            .map(service::findChampionDataByKey)
            .flatMap(Optional::stream)
            .map(ChampionDTO::getName)
            .filter(championName -> championName.toLowerCase().startsWith(userText.toLowerCase()))
            .collect(toList());
    }

    public <T extends SlotItem> void handleDeleteButton(KeyEvent event, ListView<SlotDTO<T>> listView) {
        if (event.getCode() == KeyCode.DELETE) {
            ObservableList<SlotDTO<T>> selectedItems = listView.getSelectionModel().getSelectedItems();
            if (!selectedItems.isEmpty()) {
                selectedItems.forEach(Clearable::clear);
            }
        }
    }

    private ListChangeListener<SlotDTO<SummonerDTO>> getChampionListChangeListener() {
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

    private void setTeamAttributeRatings(TeamDTO team) {
        AttributeRatingsDTO enemyTeamStats = team.getAttributeRatings();
        List<String> enemyChampionKeys = team.getChampions().stream().map(ChampionDTO::getKey).toList();
        enemyTeamStats.setDamage(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getDamage, 3));
        enemyTeamStats.setAttack(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getAttack, 10));
        enemyTeamStats.setDefense(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getDefense, 10));
        enemyTeamStats.setMagic(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getMagic, 10));
        enemyTeamStats.setDifficulty(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getDifficulty, 3));
        enemyTeamStats.setControl(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getControl, 3));
        enemyTeamStats.setToughness(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getToughness, 3));
        enemyTeamStats.setMobility(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getMobility, 3));
        enemyTeamStats.setUtility(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getUtility, 3));
        enemyTeamStats.setAbilityReliance(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getAbilityReliance, 100));
    }

    private void calculateTeamStats(
        AttributeRatingsDTO stats,
        AttributeRatingsDTO opponentStats,
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

    private long getBadStatCount(TextFlow textFlow) {
        return textFlow.getChildren().stream()
            .map(Node::getPseudoClassStates)
            .filter(pseudoClasses -> pseudoClasses.contains(CHAMPION_STAT_BAD_PSEUDO_CLASS))
            .count();
    }
}