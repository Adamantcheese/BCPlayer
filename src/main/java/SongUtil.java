import javafx.scene.media.Media;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
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
    private String[] ALBUM_LIST;

    public SongUtil() throws Exception {
        //Load in the array of albums
        ArrayList<String> albums = new ArrayList<String>(750);
        File albumFile = new File(Class.forName("SongUtil").getClassLoader().getResource(ALBUM_LIST_FILE).toURI());
        Scanner albumScanner = new Scanner(albumFile);
        while (albumScanner.hasNext()) {
            String URL = albumScanner.nextLine();
            URL = URL.substring(1, URL.length() - 1);
            albums.add(URL);
        }
        albumScanner.close();
        ALBUM_LIST = albums.toArray(new String[albums.size()]);

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
        return ALBUM_LIST[RANDOMIZER.nextInt(ALBUM_LIST.length)];
    }

    private String[] getSongsFromURL(String URL) {
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            System.err.println("Exception: URL returns a connection error (probably 404): " + URL);
            return new String[0];
        }
        Elements scripts = doc.select("script");
        Pattern mp3Pattern = Pattern.compile("\"mp3-128\":\".*?\"");
        Element mp3Script = null;
        for(Element script : scripts) {
            Matcher tempMatcher = mp3Pattern.matcher(script.html());
            if(tempMatcher.find()) {
                mp3Script = script;
                break;
            }
        }
        Matcher mp3Matcher = mp3Pattern.matcher(mp3Script.html());
        ArrayList<String> possibles = new ArrayList<String>(15);
        while (mp3Matcher.find()) {
            String found = mp3Matcher.group();
            possibles.add(found.substring(11, found.length() - 1));
        }
        String[] songs = new String[possibles.size()];
        possibles.toArray(songs);
        return songs;
    }

    public Media getMediaFromFile(File f) {
        return new Media(f.toURI().toString());
    }
}
