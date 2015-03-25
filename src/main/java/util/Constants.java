package util;

import java.io.File;
import java.util.Random;

/**
 * Created by Jacob on 3/22/2015.
 */
public class Constants {
    private static final int MB = 1000000;
    private static final int MAX_MB = 10;
    public static final int MAX_FILESIZE = MAX_MB * MB;

    public static final String BASE_DIR = System.getProperty("java.io.tmpdir") + "BCPlayer\\";
    private static final String TRACK_FILE_DIR = BASE_DIR + "tracks.nll";
    private static final String TEMP_FILE_DIR = BASE_DIR + "last.tmp";

    public static final File TRACK_FILE = new File(TRACK_FILE_DIR);
    public static final File TEMP_FILE = new File(TEMP_FILE_DIR);
    private static File INTERNAL_TRACK_FILE = null;
    public static File getInternalTrackFile() throws Exception {
        if(INTERNAL_TRACK_FILE == null) {
            INTERNAL_TRACK_FILE = new File(Constants.class.getResource("../tracks.nll").toURI());
        }
        return INTERNAL_TRACK_FILE;
    }

    public static final int SONG_BUFFER_SIZE = 5;

    public static final Random RANDOMIZER = new Random();
}
