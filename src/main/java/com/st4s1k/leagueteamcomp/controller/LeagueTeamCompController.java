package com.st4s1k.leagueteamcomp.controller;

import com.google.gson.reflect.TypeToken;
import com.merakianalytics.orianna.types.core.championmastery.ChampionMastery;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import com.st4s1k.leagueteamcomp.exceptions.LTCException;
import com.st4s1k.leagueteamcomp.model.SummonerData;
import com.st4s1k.leagueteamcomp.model.champion.AttributeRatingsDTO;
import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.*;
import com.st4s1k.leagueteamcomp.model.enums.SummonerRoleEnum;
import com.st4s1k.leagueteamcomp.model.interfaces.ChampionProvider;
import com.st4s1k.leagueteamcomp.model.interfaces.Clearable;
import com.st4s1k.leagueteamcomp.model.interfaces.SlotItem;
import com.st4s1k.leagueteamcomp.service.ChampionSuggestionService;
import com.st4s1k.leagueteamcomp.service.LeagueTeamCompService;
import com.st4s1k.leagueteamcomp.service.SummonerRoleListGeneratorService;
import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;
import com.stirante.lolclient.ClientWebSocket;
import dev.failsafe.Failsafe;
import generated.*;
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

import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.st4s1k.leagueteamcomp.model.enums.SummonerRoleEnum.*;
import static com.st4s1k.leagueteamcomp.utils.CompressionUtils.compressB64;
import static com.st4s1k.leagueteamcomp.utils.CompressionUtils.decompressB64;
import static com.st4s1k.leagueteamcomp.utils.Resources.*;
import static com.st4s1k.leagueteamcomp.utils.Utils.*;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
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
    private TextField summonerName1;
    @FXML
    private TextField summonerName2;
    @FXML
    private TextField summonerName3;
    @FXML
    private TextField summonerName4;
    @FXML
    private TextField summonerName5;

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
    private TextFlow teamStatsTextFlow;
    @FXML
    private Button allyAddButton;
    @FXML
    private Button enemyAddButton;
    @FXML
    private TextField allySearchField;
    @FXML
    private TextField enemySearchField;
    @FXML
    private ListView<SummonerSlotDTO> allyListView;
    @FXML
    private ListView<SummonerSlotDTO> enemyListView;
    @FXML
    private ListView<ChampionSlotDTO> allyBanListView;
    @FXML
    private ListView<ChampionSlotDTO> enemyBanListView;
    @FXML
    private ListView<ListView<ChampionSlotDTO>> suggestionsListView;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Summoner Data                                       *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    @FXML
    private ToggleButton manualModeToggle;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Controller fields                                   *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML
    private ListView<SummonerData> summonerDataListView;
    @FXML
    private TextField summonerAddField;
    @FXML
    private Button summonerAddButton;

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
        String className = getClass().getSimpleName();
        log.info("Stopping {}...", className);
        save();
        api.stop();
        log.info("{} stopped.", className);
    }

    public void save() {
        log.info("Saving application state...");
        saveAllCheckboxes();
        saveAllTextFields();
        saveSummonerData();
        saveChampionSuggestions();
        log.info("Application saved.");
    }

    private void saveAllCheckboxes() {
        saveFields(this, CheckBox.class, Node::getId, CheckBox::isSelected);
    }

    private void saveAllTextFields() {
        saveFields(this, TextField.class, Node::getId, TextField::getText);
    }

    private void saveSummonerData() {
        log.info("Saving summoner data...");
        List<SummonerData> summonerDataList = new ArrayList<>(summonerDataListView.getItems());
        String summonerDataListJson = GSON.toJson(summonerDataList);
        String summonerDataListCompressed = compressB64(summonerDataListJson);
        PREFERENCES.put("summonerDataList", summonerDataListCompressed);
        log.info("Summoner data saved.");
    }

    private void saveChampionSuggestions() {
        log.info("Saving champ select data...");
        String champSelectJson = GSON.toJson(champSelect);
        String champSelectCompressed = compressB64(champSelectJson);
        PREFERENCES.put("champSelect", champSelectCompressed);
        log.info("Champ select data saved.");
    }

    @Override
    @SneakyThrows
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeAllCheckboxes();
        initializeAllTextFields();
        registerLeagueClientListeners();
        initializeRoleCompositions();
        initializeChampionSuggestions();
        initializeSummonerData();
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
        Failsafe.with(RETRY_POLICY)
            .runAsync(() -> socket = api.openWebSocket())
            .thenRunAsync(() -> socket.setSocketListener(new ClientWebSocket.SocketListener() {
                @Override
                public void onEvent(ClientWebSocket.Event event) {
                    Platform.runLater(() -> {
                        if (event.getEventType().equals("Update") &&
                            event.getUri().equals("/lol-champ-select/v1/session") &&
                            event.getData() instanceof LolChampSelectChampSelectSession session) {
                            log.debug(
                                "\nEvent: {}\nURI: {}\ndata: {}\n",
                                event.getEventType(),
                                event.getUri(),
                                GSON_PRETTY.toJson(session)
                            );
                            onChampSelectUpdate(session);
                        } else if (event.getEventType().equals("Update") &&
                            event.getUri().equals("/lol-gameflow/v1/gameflow-phase") &&
                            event.getData() instanceof LolGameflowGameflowPhase phase) {
                            log.debug("\nEvent: {}\nURI: {}\nphase: {}\n", event.getEventType(), event.getUri(), phase);
                            if (phase == LolGameflowGameflowPhase.NONE) {
                                champSelect.clear();
                            }
                        }
                    });
                }

                @Override
                public void onClose(int code, String reason) {
                    log.warn("Socket closed, code: {}, reason: {}", code, reason);
                }
            }));
    }

    private void onChampSelectUpdate(LolChampSelectChampSelectSession session) {
        champSelect.clear();
        List<LolChampSelectChampSelectAction> actions = getActions(session);
        List<Integer> myTeamBans = getTeamBans(session.bans.myTeamBans, actions, action -> action.isAllyAction);
        List<Integer> theirTeamBans = getTeamBans(session.bans.theirTeamBans, actions, action -> !action.isAllyAction);
        updateTeam(champSelect.getAllyTeam(), session.myTeam, myTeamBans);
        updateTeam(champSelect.getEnemyTeam(), session.theirTeam, theirTeamBans);
    }

    private List<LolChampSelectChampSelectAction> getActions(LolChampSelectChampSelectSession session) {
        return Optional.ofNullable(session.actions).stream()
            .flatMap(Collection::stream)
            .map(GSON::toJson)
            .map(json -> GSON.fromJson(json, LolChampSelectChampSelectAction[].class))
            .flatMap(Arrays::stream)
            .toList();
    }

    private List<Integer> getTeamBans(
        List<Integer> teamBans,
        List<LolChampSelectChampSelectAction> actions,
        Predicate<LolChampSelectChampSelectAction> teamFilter
    ) {
        return Optional.ofNullable(teamBans)
            .filter(not(List::isEmpty))
            .orElseGet(() -> actions.stream()
                .filter(teamFilter.and(action -> "ban".equals(action.type)))
                .map(action -> action.championId)
                .filter(championId -> championId != 0)
                .toList());
    }

    private void updateTeam(
        TeamDTO team,
        List<LolChampSelectChampSelectPlayerSelection> playerSelectionList,
        List<Integer> bans
    ) {
        playerSelectionList.forEach(playerSelection -> populateSummonerSlot(playerSelection, team));
        updateBans(team, bans);
    }

    private void updateBans(TeamDTO team, List<Integer> bans) {
        List<Integer> bannedChampionIdsBefore = team.getBannedChampionIds();
        boolean shouldLog = notSame(bannedChampionIdsBefore, bans);
        if (shouldLog) {
            log.debug("team: {}", team.getTeamSide());
            log.debug("response bans: {}", bans);
        }

        bans.stream()
            .filter(championId -> team.getBannedChampions().stream()
                .map(ChampionDTO::getId)
                .noneMatch(championId::equals))
            .map(service::findChampionById)
            .flatMap(Optional::stream)
            .forEach(champion -> team.getBans().stream()
                .filter(ChampionProvider::isChampionNotSelected)
                .findFirst()
                .ifPresent(slot -> slot.setChampion(champion)));

        if (shouldLog) {
            log.debug("teamBans: {}\n", team.getBannedChampionIds());
        }
    }

    private void populateSummonerSlot(
        LolChampSelectChampSelectPlayerSelection playerSelection,
        TeamDTO team
    ) {
        int slotId = playerSelection.cellId.intValue();
        int slotIndex = slotId % 5;
        team.getSlot(slotIndex).getItem().ifPresent(summoner -> {
            Long summonerId = playerSelection.summonerId;
            summoner.setSummonerId(summonerId);
            summoner.setSummonerName(getSummonerName(summonerId));
            service.findChampionById(playerSelection.championId)
                .or(() -> service.findChampionById(playerSelection.championPickIntent))
                .ifPresent(summoner::setChampion);
            populateSummonerSuggestions(summoner);
        });
    }

    @SneakyThrows
    private void populateSummonerSuggestions(SummonerDTO summoner) {
        if (summoner.getSummonerId() != null && summoner.getSummonerId() > 0) {
            Summoner.named(summoner.getSummonerName()).get().getChampionMasteries().stream()
                .sorted(comparing(ChampionMastery::getPoints, reverseOrder()))
                .mapToInt(championMastery -> championMastery.getChampion().getId())
                .forEach(championId -> summoner.getChampionSuggestions().stream()
                    .filter(ChampionSlotDTO::isEmpty).findFirst().ifPresent(suggestionSlot ->
                        service.findChampionById(championId).ifPresent(suggestionSlot::setChampion)));
        }
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
                Map.entry(summonerName1.getText(), getRoles(cbTop1, cbMid1, cbAdc1, cbSup1, cbJgl1)),
                Map.entry(summonerName2.getText(), getRoles(cbTop2, cbMid2, cbAdc2, cbSup2, cbJgl2)),
                Map.entry(summonerName3.getText(), getRoles(cbTop3, cbMid3, cbAdc3, cbSup3, cbJgl3)),
                Map.entry(summonerName4.getText(), getRoles(cbTop4, cbMid4, cbAdc4, cbSup4, cbJgl4)),
                Map.entry(summonerName5.getText(), getRoles(cbTop5, cbMid5, cbAdc5, cbSup5, cbJgl5))
            ).entrySet().stream()
            .filter(entry -> !entry.getKey().isBlank())
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void validateSummonerNames() {
        List<String> summonerNames = List.of(
            summonerName1.getText(),
            summonerName2.getText(),
            summonerName3.getText(),
            summonerName4.getText(),
            summonerName5.getText()
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
        initializeFieldsWithDefaultState(
            this,
            CheckBox.class,
            CheckBox::setSelected,
            DEFAULT_CHECKBOX_STATE
        );
    }

    private void initializeAllCheckboxes() {
        initializeFields(
            this,
            CheckBox.class,
            CheckBox::getId,
            CheckBox::setSelected,
            Boolean::parseBoolean,
            DEFAULT_CHECKBOX_STATE
        );
    }

    private void initializeAllTextFields() {
        initializeFields(
            this,
            TextField.class,
            TextField::getId,
            TextInputControl::setText,
            Function.identity(),
            ""
        );
    }

    public void initializeChampionSuggestions() {
        log.info("Initializing champion suggestions");
        initializeChampSelect();
        disableInteraction(allyListView);
        disableInteraction(enemyListView);
        disableInteraction(allyBanListView);
        disableInteraction(enemyBanListView);
        disableInteraction(suggestionsListView);
        suggestionService.initializeChampionListView(champSelect.getAllyTeam().getSlots(), allyListView, 50);
        suggestionService.initializeChampionListView(champSelect.getEnemyTeam().getSlots(), enemyListView, 50);
        suggestionService.initializeChampionListView(champSelect.getAllyTeam().getBans(), allyBanListView, 40);
        suggestionService.initializeChampionListView(champSelect.getEnemyTeam().getBans(), enemyBanListView, 40);
        suggestionService.initializeSuggestionsListView(champSelect, suggestionsListView, 40);
        initializeTemporaryTeamStats();
    }

    private void initializeChampSelect() {
        String champSelectCompressed = PREFERENCES.get("champSelect", "");
        if (!champSelectCompressed.isBlank()) {
            String champSelectJson = decompressB64(champSelectCompressed);
            ChampSelectDTO champSelect = GSON.fromJson(champSelectJson, ChampSelectDTO.class);
            populateChampSelectTeams(champSelect.getAllyTeam(), this.champSelect.getAllyTeam());
            populateChampSelectTeams(champSelect.getEnemyTeam(), this.champSelect.getEnemyTeam());
        }
    }

    private void populateChampSelectTeams(TeamDTO team, TeamDTO thisTeam) {
        thisTeam.setAttributeRatings(team.getAttributeRatings());
        populateChampSelectSlots(team, thisTeam);
        populateChampSelectBans(team, thisTeam);
    }

    private void populateChampSelectSlots(TeamDTO enemyTeam, TeamDTO thisEnemyTeam) {
        enemyTeam.getSlots().forEach(slot -> thisEnemyTeam.getSlots().stream()
            .filter(ChampionProvider::isChampionNotSelected)
            .findFirst()
            .ifPresent(thisSlot -> slot.getItem().ifPresent(thisSlot::setItem)));
    }

    private void populateChampSelectBans(TeamDTO enemyTeam, TeamDTO thisEnemyTeam) {
        enemyTeam.getBans().forEach(ban -> thisEnemyTeam.getBans().stream()
            .filter(ChampionProvider::isChampionNotSelected)
            .findFirst()
            .ifPresent(thisBan -> ban.getItem().ifPresent(thisBan::setItem)));
    }

    private void initializeTemporaryTeamStats() {
        allyListView.getSelectionModel().setSelectionMode(MULTIPLE);
        enemyListView.getSelectionModel().setSelectionMode(MULTIPLE);
        allyBanListView.getSelectionModel().setSelectionMode(MULTIPLE);
        enemyBanListView.getSelectionModel().setSelectionMode(MULTIPLE);
        suggestionsListView.getItems().forEach(item -> item.getSelectionModel().setSelectionMode(MULTIPLE));

        allyAddButton.setOnAction(event -> onSearchAction(allyListView, allySearchField.getText()));
        enemyAddButton.setOnAction(event -> onSearchAction(enemyListView, enemySearchField.getText()));
        allySearchField.setOnAction(actionEvent -> onSearchAction(allyListView, allySearchField.getText()));
        enemySearchField.setOnAction(actionEvent -> onSearchAction(enemyListView, enemySearchField.getText()));
        manualModeToggle.setOnAction(event -> manualToggleAction());

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

    private void manualToggleAction() {
        if (manualModeToggle.isSelected()) {
            enableInteraction(allyListView);
            enableInteraction(enemyListView);
            enableInteraction(allyBanListView);
            enableInteraction(enemyBanListView);
            enableInteraction(suggestionsListView);
        } else {
            disableInteraction(allyListView);
            disableInteraction(enemyListView);
            disableInteraction(allyBanListView);
            disableInteraction(enemyBanListView);
            disableInteraction(suggestionsListView);
            allyListView.getSelectionModel().clearSelection();
            enemyListView.getSelectionModel().clearSelection();
            allyBanListView.getSelectionModel().clearSelection();
            enemyBanListView.getSelectionModel().clearSelection();
            suggestionsListView.getItems().forEach(item -> item.getSelectionModel().clearSelection());
        }
    }

    private void onSearchAction(
        ListView<SummonerSlotDTO> championListView,
        String searchFieldText
    ) {
        if (isValidSearchFieldText(championListView.getItems(), searchFieldText)) {
            log.debug("Search field text \"{}\" is valid", searchFieldText);
            service.findChampionByName(searchFieldText)
                .ifPresentOrElse(
                    champion -> getSearchActionItems(championListView).stream()
                        .filter(ChampionProvider::isChampionNotSelected)
                        .findFirst()
                        .ifPresentOrElse(
                            slot -> {
                                log.debug("Setting champion {} in slot {}", champion.getName(), slot);
                                slot.setChampion(champion);
                                log.debug("Champion {} set in slot {}", champion.getName(), slot);
                            },
                            () -> log.debug("No empty slots found")
                        ),
                    () -> log.debug("Champion not found for name: {}", searchFieldText)
                );
        } else {
            log.debug("Search field text is not valid: {}", searchFieldText);
        }
    }

    private boolean isValidSearchFieldText(List<SummonerSlotDTO> slots, String searchFieldText) {
        List<String> championKeys = slots.stream()
            .map(ChampionProvider::getChampion)
            .flatMap(Optional::stream)
            .map(ChampionDTO::getKey)
            .toList();
        long filledSlots = slots.stream().filter(ChampionProvider::isChampionSelected).count();
        return filledSlots < 5
            && !championKeys.contains(searchFieldText)
            && service.existsChampionByName(searchFieldText)
            && !service.findChampionByName(searchFieldText)
            .map(champSelect.getChampionPool()::contains)
            .orElse(false);
    }

    private ObservableList<SummonerSlotDTO> getSearchActionItems(ListView<SummonerSlotDTO> championListView) {
        ObservableList<SummonerSlotDTO> selectedItems = championListView.getSelectionModel().getSelectedItems();
        return selectedItems.isEmpty() || selectedItems.stream().noneMatch(ChampionProvider::isChampionNotSelected)
            ? championListView.getItems()
            : selectedItems;
    }

    private List<String> searchFieldAutoCompletion(String userText) {
        List<String> championPoolKeys = champSelect.getChampionPool().stream()
            .map(ChampionDTO::getKey)
            .toList();
        return service.getAllChampionKeys().stream()
            .filter(not(championPoolKeys::contains))
            .map(service::findChampionByKey)
            .flatMap(Optional::stream)
            .map(ChampionDTO::getName)
            .filter(championName -> championName.toLowerCase().startsWith(userText.toLowerCase()))
            .collect(toList());
    }

    public <T extends SlotItem, S extends SlotDTO<T>> void handleDeleteButton(KeyEvent event, ListView<S> listView) {
        if (event.getCode() == KeyCode.DELETE) {
            ObservableList<S> selectedItems = listView.getSelectionModel().getSelectedItems();
            if (!selectedItems.isEmpty()) {
                selectedItems.forEach(Clearable::clear);
            }
        }
    }

    private ListChangeListener<SummonerSlotDTO> getChampionListChangeListener() {
        return change -> {
            setTeamAttributeRatings(champSelect.getAllyTeam());
            setTeamAttributeRatings(champSelect.getEnemyTeam());
            formatTeamStats(
                champSelect.getAllyTeam().getAttributeRatings(),
                champSelect.getEnemyTeam().getAttributeRatings(),
                teamStatsTextFlow
            );
            long badStatCount = getBadStatCount(teamStatsTextFlow);
            long goodStatCount = getGoodStatCount(teamStatsTextFlow);
            if (badStatCount > goodStatCount) {
                teamCompResultLabel.setText("Enemy Team is better");
            } else if (badStatCount < goodStatCount) {
                teamCompResultLabel.setText("Ally Team is better");
            } else {
                teamCompResultLabel.setText("Teams are similar");
            }
        };
    }

    private void setTeamAttributeRatings(TeamDTO team) {
        List<String> enemyChampionKeys = team.getChampions().stream().map(ChampionDTO::getKey).toList();
        AttributeRatingsDTO enemyTeamStats = AttributeRatingsDTO.builder()
            .damage(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getDamage, 3))
            .attack(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getAttack, 10))
            .defense(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getDefense, 10))
            .magic(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getMagic, 10))
            .difficulty(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getDifficulty, 3))
            .control(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getControl, 3))
            .toughness(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getToughness, 3))
            .mobility(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getMobility, 3))
            .utility(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getUtility, 3))
            .abilityReliance(service.getChampionStatValue(enemyChampionKeys, AttributeRatingsDTO::getAbilityReliance, 100))
            .build();
        team.setAttributeRatings(enemyTeamStats);
    }

    private void formatTeamStats(
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

    private long getGoodStatCount(TextFlow textFlow) {
        return textFlow.getChildren().stream()
            .map(Node::getPseudoClassStates)
            .filter(pseudoClasses -> pseudoClasses.contains(CHAMPION_STAT_GOOD_PSEUDO_CLASS))
            .count();
    }

    private void initializeSummonerData() {
        log.info("Initializing summoner data");
        String summonerDataListCompressed = PREFERENCES.get("summonerDataList", "");
        if (!summonerDataListCompressed.isBlank()) {
            String summonerDataListJson = decompressB64(summonerDataListCompressed);
            List<SummonerData> summonerDataList = GSON.fromJson(summonerDataListJson, getSummonerDataListType());
            summonerDataListView.getItems().addAll(summonerDataList);
        }
        summonerDataListView.setCellFactory(param -> new ListCell<>() {
            @Override
            public void updateItem(SummonerData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getSummonerName());
                }
            }
        });
        summonerAddField.setOnAction(action -> onSummonerNameFieldAction());
        summonerAddButton.setOnAction(action -> onSummonerNameFieldAction());
    }

    private Type getSummonerDataListType() {
        return new TypeToken<List<SummonerData>>() {
        }.getType();
    }

    private void onSummonerNameFieldAction() {
        SummonerData summonerData = new SummonerData();
        summonerData.setSummonerName(summonerAddField.getText());
        summonerDataListView.getItems().add(summonerData);
    }

}