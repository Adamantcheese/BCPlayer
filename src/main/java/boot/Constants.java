package boot;

import com.sun.javafx.application.HostServicesDelegate;
import constructs.DownloadManager;
import constructs.TrackContainer;
import javafx.scene.image.Image;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 3/22/2015.
 */
public class Constants {

    public static final String BASE_TEMP_DIR = System.getProperty("java.io.tmpdir") + "BCPlayer\\";
    private static final String TRACK_FILE_DIR = BASE_TEMP_DIR + "tracks.nll";

    public static final File BASE_DIR = new File(BASE_TEMP_DIR);
    public static final File TRACK_FILE = new File(TRACK_FILE_DIR);

    private static TrackContainer TRACK_HELPER = null;

    public static TrackContainer getTrackHelper () throws Exception {
        if (TRACK_HELPER == null) {
            TRACK_HELPER = new TrackContainer();
        }
        return TRACK_HELPER;
    }

    private static DownloadManager DOWNLOAD_MANAGER = null;

    public static DownloadManager getDownloadManager() {
        if(DOWNLOAD_MANAGER == null) {
            DOWNLOAD_MANAGER = new DownloadManager();
            DOWNLOAD_MANAGER.start();
        }
        return DOWNLOAD_MANAGER;
    }

    private static HostServicesDelegate HOST_SERVICES = null;

    public static HostServicesDelegate getHostServices() {
        if(HOST_SERVICES == null) {
            throw new IllegalAccessError();
        }
        return HOST_SERVICES;
    }

    public static void setHostServices(HostServicesDelegate hsd) {
        HOST_SERVICES = hsd;
    }

    private static URL DEFAULT_COVER = null;

    public static URL getDefaultCover () {
        if (DEFAULT_COVER == null) {
            DEFAULT_COVER = ClassLoader.getSystemClassLoader().getResource("default_cover.png");
        }
        return DEFAULT_COVER;
    }

    private static Image PLAY_BUTTON = null;

    public static Image getPlayButton () {
        if (PLAY_BUTTON == null) {
            PLAY_BUTTON = new Image(ClassLoader.getSystemClassLoader().getResource("play.png").toString());
        }
        return PLAY_BUTTON;
    }

    private static Image PAUSE_BUTTON = null;

    public static Image getPauseButton () {
        if (PAUSE_BUTTON == null) {
            PAUSE_BUTTON = new Image(ClassLoader.getSystemClassLoader().getResource("pause.png").toString());
        }
        return PAUSE_BUTTON;
    }

    private static Image REPEAT_ON_BUTTON = null;

    public static Image getRepeatOnButton() {
        if (REPEAT_ON_BUTTON == null) {
            REPEAT_ON_BUTTON = new Image(ClassLoader.getSystemClassLoader().getResource("repeat_on.png").toString());
        }
        return REPEAT_ON_BUTTON;
    }

    private static Image REPEAT_OFF_BUTTON = null;

    public static Image getRepeatOffButton() {
        if (REPEAT_OFF_BUTTON == null) {
            REPEAT_OFF_BUTTON = new Image(ClassLoader.getSystemClassLoader().getResource("repeat_off.png").toString());
        }
        return REPEAT_OFF_BUTTON;
    }

    private static List<Image> ICONS = null;

    public static List<Image> getIcons() {
        if(ICONS == null) {
            ICONS = new ArrayList<Image>();
            ICONS.add(new Image(ClassLoader.getSystemClassLoader().getResource("icons/16x16.png").toString()));
            ICONS.add(new Image(ClassLoader.getSystemClassLoader().getResource("icons/24x24.png").toString()));
            ICONS.add(new Image(ClassLoader.getSystemClassLoader().getResource("icons/32x32.png").toString()));
            ICONS.add(new Image(ClassLoader.getSystemClassLoader().getResource("icons/48x48.png").toString()));
            ICONS.add(new Image(ClassLoader.getSystemClassLoader().getResource("icons/64x64.png").toString()));
            ICONS.add(new Image(ClassLoader.getSystemClassLoader().getResource("icons/96x96.png").toString()));
            ICONS.add(new Image(ClassLoader.getSystemClassLoader().getResource("icons/128x128.png").toString()));
            ICONS.add(new Image(ClassLoader.getSystemClassLoader().getResource("icons/256x256.png").toString()));
        }
        return ICONS;
    }

    public static int HISTORY_LIMIT = 150;
}
