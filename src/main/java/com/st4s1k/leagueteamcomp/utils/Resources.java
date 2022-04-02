package com.st4s1k.leagueteamcomp.utils;

import com.st4s1k.leagueteamcomp.LeagueTeamCompApplication;
import javafx.scene.image.Image;
import lombok.experimental.UtilityClass;

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

    public final Image EMPTY_SLOT_IMAGE = new Image(LeagueTeamCompApplication.class.getResourceAsStream(EMPTY_SLOT_IMAGE_PATH));

}
