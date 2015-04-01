package util;

import constructs.TrackContainer;
import javafx.scene.image.Image;

import java.io.File;
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
    public static File getInternalTrackFile() throws Exception {
        if(INTERNAL_TRACK_FILE == null) {
            INTERNAL_TRACK_FILE = new File(Constants.class.getResource("../tracks.nll").toURI());
        }
        return INTERNAL_TRACK_FILE;
    }

    public static final Random RANDOMIZER = new Random();

    private static TrackContainer TRACK_HELPER = null;
    public static TrackContainer getTrackHelper() throws Exception {
        if(TRACK_HELPER == null) {
            TRACK_HELPER = new TrackContainer();
        }
        return TRACK_HELPER;
    }

    private static Image DEFAULT_ALBUM_COVER = null;
    public static Image getDefaultAlbumCover() throws Exception {
        if(DEFAULT_ALBUM_COVER == null) {
            DEFAULT_ALBUM_COVER = new Image(Constants.class.getResource("../default_cover.png").toString());
        }
        return DEFAULT_ALBUM_COVER;
    }
}
