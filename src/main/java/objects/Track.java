package objects;

import java.net.URL;

/**
 * Created by Jacob on 3/25/2015.
 */
public class Track {

    private String trackName;
    private String artist;
    private String duration;
    private URL artURL;
    private URL trackURL;

    public Track(String t, String a, double d, URL au, URL tu) {
        trackName = t;
        artist = a;
        duration = setDuration(d);
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

    public String getDuration() {
        return duration;
    }

    public String setDuration(double d) {
        String dur = "";
        int hours = (int) (d/60/60);
        int minutes = (int) (d/60) - hours*60;
        int seconds = (int) (d) - minutes*60;
        if(hours > 0) {
            dur += String.format("%02d:", hours);
        }
        dur += String.format("%02d:%02d", minutes, seconds);
        return dur;
    }
}
