package boot;

import constructs.TrackContainer;
import javafx.scene.image.Image;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jacob on 3/22/2015.
 */
public class Constants {

    public static final String BASE_TEMP_DIR = System.getProperty("java.io.tmpdir") + "BCPlayer\\";
    private static final String TRACK_FILE_DIR = BASE_TEMP_DIR + "tracks.nll";

    public static final File BASE_DIR = new File(BASE_TEMP_DIR);
    public static final File TRACK_FILE = new File(TRACK_FILE_DIR);
    private static File INTERNAL_TRACK_FILE = null;

    public static File getInternalTrackFile () throws Exception {
        if (INTERNAL_TRACK_FILE == null) {
            INTERNAL_TRACK_FILE = new File(Constants.class.getResource("../tracks.nll").toURI());
        }
        return INTERNAL_TRACK_FILE;
    }

    public static final Random RANDOMIZER = new Random();

    private static TrackContainer TRACK_HELPER = null;

    public static TrackContainer getTrackHelper () throws Exception {
        if (TRACK_HELPER == null) {
            TRACK_HELPER = new TrackContainer();
        }
        return TRACK_HELPER;
    }

    private static URL DEFAULT_COVER = null;

    public static URL getDefaultCover () {
        if (DEFAULT_COVER == null) {
            DEFAULT_COVER = Constants.class.getResource("../default_cover.png");
        }
        return DEFAULT_COVER;
    }

    private static Image PLAY_BUTTON = null;

    public static Image getPlayButton () {
        if (PLAY_BUTTON == null) {
            PLAY_BUTTON = new Image(Constants.class.getResource("../play.png").toString());
        }
        return PLAY_BUTTON;
    }

    private static Image PAUSE_BUTTON = null;

    public static Image getPauseButton () {
        if (PAUSE_BUTTON == null) {
            PAUSE_BUTTON = new Image(Constants.class.getResource("../pause.png").toString());
        }
        return PAUSE_BUTTON;
    }

    private static List<Image> ICONS = null;

    public static List<Image> getIcons() {
        if(ICONS == null) {
            ICONS = new ArrayList<Image>();
            ICONS.add(new Image(Constants.class.getResource("../16x16.png").toString()));
            ICONS.add(new Image(Constants.class.getResource("../24x24.png").toString()));
            ICONS.add(new Image(Constants.class.getResource("../32x32.png").toString()));
            ICONS.add(new Image(Constants.class.getResource("../48x48.png").toString()));
            ICONS.add(new Image(Constants.class.getResource("../64x64.png").toString()));
            ICONS.add(new Image(Constants.class.getResource("../96x96.png").toString()));
            ICONS.add(new Image(Constants.class.getResource("../128x128.png").toString()));
            ICONS.add(new Image(Constants.class.getResource("../256x256.png").toString()));
        }
        return ICONS;
    }
}
