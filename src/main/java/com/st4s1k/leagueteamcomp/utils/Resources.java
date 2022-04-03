package com.st4s1k.leagueteamcomp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.st4s1k.leagueteamcomp.LeagueTeamCompApplication;
import com.st4s1k.leagueteamcomp.json.*;
import com.st4s1k.leagueteamcomp.model.champion.select.*;
import dev.failsafe.RetryPolicy;
import javafx.scene.image.Image;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

@UtilityClass
@SuppressWarnings("ConstantConditions")
public class Resources {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Resource path constants                             *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private final String APP_BUNDLE_PATH = "com.st4s1k.leagueteamcomp.ltc";
    private final String FXML_BUNDLE_PATH = "com.st4s1k.leagueteamcomp.ltc-view";
    public final ResourceBundle LTC_PROPERTIES = ResourceBundle.getBundle(APP_BUNDLE_PATH);
    public final ResourceBundle LTC_VIEW_PROPERTIES = ResourceBundle.getBundle(FXML_BUNDLE_PATH);
    public final Preferences PREFERENCES = Preferences.userNodeForPackage(LeagueTeamCompApplication.class);

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Window constants                                    *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public final String WINDOW_TITLE = LTC_PROPERTIES.getString("window-title");
    public final int WINDOW_WIDTH = parseInt(LTC_PROPERTIES.getString("window-width"));
    public final int WINDOW_HEIGHT = parseInt(LTC_PROPERTIES.getString("window-height"));
    public final boolean WINDOW_IS_RESIZABLE = parseBoolean(LTC_PROPERTIES.getString("window-is-resizable"));

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Files                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public final String ICON_FILE_PATH = LTC_PROPERTIES.getString("icon-file-path");
    public final String FXML_FILE_PATH = LTC_PROPERTIES.getString("fxml-file-path");
    public final String EMPTY_SLOT_IMAGE_PATH = LTC_PROPERTIES.getString("empty-slot-image");

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Urls                                                *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public final String CHAMPIONS_URL = LTC_PROPERTIES.getString("champions-url");

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Application constants                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public final String RIOT_API_KEY = LTC_PROPERTIES.getString("riot-api-key");
    public final Image EMPTY_SLOT_IMAGE = new Image(LeagueTeamCompApplication.class.getResourceAsStream(EMPTY_SLOT_IMAGE_PATH));

    private final GsonBuilder GSON_BUILDER = new GsonBuilder()
        .registerTypeAdapter(ChampSelectDTO.class, new ChampSelectDTOJsonSerializer())
        .registerTypeAdapter(ChampSelectDTO.class, new ChampSelectDTOJsonDeserializer())
        .registerTypeAdapter(TeamDTO.class, new TeamDTOJsonSerializer())
        .registerTypeAdapter(TeamDTO.class, new TeamDTOJsonDeserializer())
        .registerTypeAdapter(ChampionSlotDTO.class, new ChampionSlotDTOJsonSerializer())
        .registerTypeAdapter(ChampionSlotDTO.class, new ChampionSlotDTOJsonDeserializer())
        .registerTypeAdapter(SummonerSlotDTO.class, new SummonerSlotDTOJsonSerializer())
        .registerTypeAdapter(SummonerSlotDTO.class, new SummonerSlotDTOJsonDeserializer())
        .registerTypeAdapter(SummonerDTO.class, new SummonerDTOJsonSerializer())
        .registerTypeAdapter(SummonerDTO.class, new SummonerDTOJsonDeserializer());
    public final Gson GSON = GSON_BUILDER.create();
    public final Gson GSON_PRETTY = GSON_BUILDER.setPrettyPrinting().create();
    public static final RetryPolicy<?> RETRY_POLICY = RetryPolicy.builder()
        .withDelay(Duration.ofSeconds(1))
        .withMaxRetries(Integer.MAX_VALUE)
        .build();

}
