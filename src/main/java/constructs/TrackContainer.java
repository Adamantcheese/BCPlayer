package constructs;

import objects.Track;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sun.security.krb5.internal.PAData;
import util.Constants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jacob on 3/18/2015.
 */
public class TrackContainer {

    private static TrackContainer INSTANCE;

    private ArrayList<String> TRACK_LIST;

    private final Pattern TRACK_NAME = Pattern.compile("\"title\":\".*?\",\"id\"");
    private final Pattern ARTIST_NAME = Pattern.compile("artist: \".*?\",");
    private final Pattern ART_URL = Pattern.compile("artFullsizeUrl: \".*?\",");
    private final Pattern MP3_URL = Pattern.compile("\"mp3-128\":\".*?\"");

    public static TrackContainer getInstance () throws Exception {
        if (INSTANCE == null) {
            INSTANCE = new TrackContainer();
        }
        return INSTANCE;
    }

    private TrackContainer () throws Exception {
        //Init the album list
        TRACK_LIST = new ArrayList<String>(15000);

        //Internalize the track file
        Scanner albumScanner = new Scanner(Constants.TRACK_FILE);
        while (albumScanner.hasNextLine()) {
            String URL = albumScanner.nextLine();
            TRACK_LIST.add(URL);
        }
        albumScanner.close();
    }

    public Track getSong (String URL) {
        //Get the track HTML document
        Document doc = null;
        do {
            try {
                doc = Jsoup.connect(URL).get();
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                return null;
            }
        } while (doc == null);

        //Get the script
        String script = doc.select("script").get(8).html();

        //Setup the matchers for grabbing data
        Matcher trackNameMatcher = TRACK_NAME.matcher(script);
        Matcher artistNameMatcher = ARTIST_NAME.matcher(script);
        Matcher artURLMatcher = ART_URL.matcher(script);
        Matcher mp3URLMatcher = MP3_URL.matcher(script);

        //Get the right strings from the matchers
        String trackName = trackNameMatcher.group();
        trackName = trackName.substring(8, trackName.length() - 6);

        String artistName = artistNameMatcher.group();
        artistName = artistName.substring(8, artistName.length() - 2);

        String artURL = artURLMatcher.group();
        artURL = artURL.substring(16, artURL.length() - 2);
        URL art = null;
        try {
            art = new URL(artURL);
        } catch (MalformedURLException e) {
            art = null;
        }

        String mp3URL = mp3URLMatcher.group();
        mp3URL = mp3URL.substring(11, mp3URL.length() - 1);
        URL mp3 = null;
        try {
            mp3 = new URL(mp3URL);
        } catch (MalformedURLException e) {
            mp3 = null;
        }

        System.out.println("pausing line");
        //Construct and return the Track object
        return new Track(trackName, artistName, art, mp3);
    }

    public URL getRandomSong () throws Exception {
        return new URL(TRACK_LIST.get(Constants.RANDOMIZER.nextInt(TRACK_LIST.size())));
    }
}
