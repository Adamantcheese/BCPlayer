import javafx.scene.media.Media;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jacob on 3/18/2015.
 */
public class SongUtil {
    private Random RANDOMIZER;
    private final String ALBUM_LIST_FILE = "albums.csv";
    private final Pattern MP3_PATTERN = Pattern.compile("\"mp3-128\":\".*?\"");
    private ArrayList<String> ALBUM_LIST;

    public SongUtil() throws Exception {
        //Load in the array of bandcamp-esque links
        ALBUM_LIST = new ArrayList<String>(750);
        File albumFile = new File(Class.forName("SongUtil").getClassLoader().getResource(ALBUM_LIST_FILE).toURI());
        Scanner albumScanner = new Scanner(albumFile);
        while (albumScanner.hasNext()) {
            String URL = albumScanner.nextLine();
            ALBUM_LIST.add(URL);
        }
        albumScanner.close();

        //Startup randomizer
        RANDOMIZER = new Random();
    }
    public String getRandomSongFromURL(String URL) {
        String[] songs = getSongsFromURL(URL);
        if(songs.length == 0) {
            return null;
        }
        return songs[RANDOMIZER.nextInt(songs.length)];
    }

    public String getRandomAlbum() {
        return ALBUM_LIST.get(RANDOMIZER.nextInt(ALBUM_LIST.size()));
    }

    private String[] getSongsFromURL(String URL) {
        //Get the album HTML document
        Document doc = null;
        do {
            try {
                doc = Jsoup.connect(URL).get();
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                return new String[0];
            }
        } while (doc == null);

        //Get all the javascript elements on the page
        Elements scripts = doc.select("script");
        if(scripts.size() == 0) {
            return new String[0];
        }

        //Get the script that contains the mp3's
        Element mp3Script = null;
        for(Element script : scripts) {
            Matcher tempMatcher = MP3_PATTERN.matcher(script.html());
            if(tempMatcher.find()) {
                mp3Script = script;
                break;
            }
        }
        if(mp3Script == null) {
            return new String[0];
        }

        //Get all the mp3 URLs from the script
        Matcher mp3Matcher = MP3_PATTERN.matcher(mp3Script.html());
        ArrayList<String> possibles = new ArrayList<String>(15);
        while (mp3Matcher.find()) {
            String found = mp3Matcher.group();
            possibles.add(found.substring(11, found.length() - 1));
        }

        //Return a string array of mp3 URLs
        return possibles.toArray(new String[possibles.size()]);
    }

    public Media getMediaFromFile(File f) {
        return new Media(f.toURI().toString());
    }

    public ArrayList<String> getAlbumList () {
        return ALBUM_LIST;
    }
}
