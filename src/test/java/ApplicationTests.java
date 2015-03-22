import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Created by Jacob on 3/21/2015.
 */
public class ApplicationTests extends TestCase {
    public ApplicationTests (String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ApplicationTests.class);
    }

    /* ------ INSERT TESTS BELOW HERE ----- */

    public void testInvalidAlbums() throws Exception {
        SongUtil s = new SongUtil();
        ArrayList<String> albums = s.getAlbumList();
        for(String URL : albums) {
            if(s.getRandomSongFromURL(URL) == null) {
                System.err.println(URL);
            }
        }
    }
}
