import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
        String[] albums = s.getAlbumList();
        for(int i = 0; i < albums.length; i++) {
            if(s.getRandomSongFromURL(albums[i]) == null) {
                System.err.println(albums[i]);
            }
        }
    }
}
