package constructs;

import boot.Constants;
import objects.Track;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    private ArrayList<String> TRACK_LIST;

    private final Pattern TRACK_NAME = Pattern.compile("\"title\":\".*?\",\"id\"");
    private final Pattern ARTIST_NAME = Pattern.compile("artist: \".*?\",");
    private final Pattern DURATION = Pattern.compile("\"duration\":.*?,");
    private final Pattern ART_URL = Pattern.compile("artFullsizeUrl: \".*?\",");
    private final Pattern MP3_URL = Pattern.compile("\"mp3-128\":\".*?\"");

    public TrackContainer () throws Exception {
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

    public Track getRandomSong () {
        //Grab a random track URL
        String URL = TRACK_LIST.get(Constants.RANDOMIZER.nextInt(TRACK_LIST.size()));

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
        String script = null;
        Elements scripts = doc.select("script");
        for (Element s : scripts) {
            String sHtml = s.html();
            if (sHtml.contains("\"mp3-128\"")) {
                script = sHtml;
            }
        }

        //If we couldn't find the script, there are no songs on this page
        if (script == null) {
            return null;
        }

        //Setup the matchers for grabbing data
        Matcher trackNameMatcher = TRACK_NAME.matcher(script);
        Matcher artistNameMatcher = ARTIST_NAME.matcher(script);
        Matcher durationMatcher = DURATION.matcher(script);
        Matcher artURLMatcher = ART_URL.matcher(script);
        Matcher mp3URLMatcher = MP3_URL.matcher(script);

        //Get the right strings from the matchers
        String trackName;
        if (trackNameMatcher.find()) {
            trackName = trackNameMatcher.group();
            trackName = trackName.substring(9, trackName.length() - 6);
        } else {
            trackName = "N/A";
        }

        String artistName;
        if (artistNameMatcher.find()) {
            artistName = artistNameMatcher.group();
            artistName = artistName.substring(9, artistName.length() - 2);
        } else {
            artistName = "N/A";
        }

        double dur;
        if (durationMatcher.find()) {
            String duration = durationMatcher.group();
            duration = duration.substring(11, duration.length() - 1);
            dur = Double.parseDouble(duration);
        } else {
            dur = 0.0;
        }

        URL art;
        if (artURLMatcher.find()) {
            String artURL = artURLMatcher.group();
            artURL = artURL.substring(17, artURL.length() - 2).replace("https", "http");
            try {
                art = new URL(artURL);
            } catch (MalformedURLException e) {
                art = Constants.getDefaultCover();
            }
        } else {
            art = Constants.getDefaultCover();
        }

        URL mp3;
        if (mp3URLMatcher.find()) {
            String mp3URL = mp3URLMatcher.group();
            mp3URL = mp3URL.substring(11, mp3URL.length() - 1).replace("https", "http");
            try {
                mp3 = new URL(mp3URL);
            } catch (MalformedURLException e) {
                mp3 = null;
            }
        } else {
            mp3 = null;
        }

        //Construct and return the Track object
        return new Track(trackName, artistName, dur, art, mp3);
    }
}
