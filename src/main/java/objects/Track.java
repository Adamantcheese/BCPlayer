package objects;

import java.net.URL;

/**
 * Created by Jacob on 3/25/2015.
 */
public class Track {

    private String trackName;
    private String artist;
    private URL artURL;
    private URL trackURL;

    public Track(String t, String a, URL au, URL tu) {
        trackName = t;
        artist = a;
        artURL = au;
        trackURL = tu;
    }

    public String getTrackName () {
        return trackName;
    }

    public String getArtist () {
        return artist;
    }

    public URL getArtURL () {
        return artURL;
    }

    public URL getTrackURL () {
        return trackURL;
    }
}
