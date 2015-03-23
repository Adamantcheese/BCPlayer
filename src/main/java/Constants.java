import javafx.scene.image.Image;

/**
 * Created by Jacob on 3/22/2015.
 */
public class Constants {
    private static final int MB = 1000000;
    public static final int MAX_MB = 10;
    public static final int MAX_FILESIZE = MAX_MB * MB;
    public static final String BASE_DIR = System.getProperty("java.io.tmpdir") + "BCPlayer\\";
    public static final int SONG_BUFFER_SIZE = 5;

}
