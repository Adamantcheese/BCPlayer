import javafx.scene.media.Media;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
            String line = albumScanner.nextLine();
            albums.add(line.substring(1, line.length() - 1));
        }
        albumScanner.close();
        ALBUM_LIST = albums.toArray(new String[albums.size()]);

        //Startup randomizer
        RANDOMIZER = new Random();
    }
    public String getRandomSongFromURL(String URL) throws Exception {
        String[] songs = getSongsFromURL(URL);
        return songs[RANDOMIZER.nextInt(songs.length)];
    }

    public String getRandomAlbum() {
        return ALBUM_LIST[RANDOMIZER.nextInt(ALBUM_LIST.length)];
    }

    private String[] getSongsFromURL(String URL) throws Exception {
        Document doc = Jsoup.connect(URL).get();
        Element script = doc.select("script").get(8);
        Pattern p = Pattern.compile("\"mp3-128\":\".*?\"");
        Matcher match = p.matcher(script.html());
        ArrayList<String> possibles = new ArrayList<String>(15);
        while (match.find()) {
            String found = match.group();
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
