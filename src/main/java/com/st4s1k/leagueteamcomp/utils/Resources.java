package com.st4s1k.leagueteamcomp.utils;

import com.st4s1k.leagueteamcomp.LeagueTeamCompApplication;
import dev.failsafe.RetryPolicy;
import javafx.scene.image.Image;

import java.time.Duration;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

@SuppressWarnings("ConstantConditions")
public final class Resources {

    private Resources() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Resource path constants                             *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static final String APP_BUNDLE_PATH = "com.st4s1k.leagueteamcomp.ltc";
    private static final String FXML_BUNDLE_PATH = "com.st4s1k.leagueteamcomp.ltc-view";
    public static final ResourceBundle LTC_PROPERTIES = ResourceBundle.getBundle(APP_BUNDLE_PATH);
    public static final ResourceBundle LTC_VIEW_PROPERTIES = ResourceBundle.getBundle(FXML_BUNDLE_PATH);
    public static final Preferences PREFERENCES = Preferences.userNodeForPackage(LeagueTeamCompApplication.class);

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Window constants                                    *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static final String WINDOW_TITLE = LTC_PROPERTIES.getString("window-title");
    public static final int WINDOW_WIDTH = parseInt(LTC_PROPERTIES.getString("window-width"));
    public static final int WINDOW_HEIGHT = parseInt(LTC_PROPERTIES.getString("window-height"));
    public static final boolean WINDOW_IS_RESIZABLE = parseBoolean(LTC_PROPERTIES.getString("window-is-resizable"));

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Files                                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static final String ICON_FILE_PATH = LTC_PROPERTIES.getString("icon-file-path");
    public static final String FXML_FILE_PATH = LTC_PROPERTIES.getString("fxml-file-path");
    public static final String EMPTY_SLOT_IMAGE_PATH = LTC_PROPERTIES.getString("empty-slot-image");

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Urls                                                *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static final String CHAMPIONS_URL = LTC_PROPERTIES.getString("champions-url");

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Application constants                               *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static final Image EMPTY_SLOT_IMAGE = new Image(LeagueTeamCompApplication.class.getResourceAsStream(EMPTY_SLOT_IMAGE_PATH));

    public static final RetryPolicy<?> RETRY_POLICY = RetryPolicy.builder()
        .withDelay(Duration.ofSeconds(1))
        .withMaxRetries(Integer.MAX_VALUE)
        .build();
}
