import javafx.scene.media.Media;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jacob on 3/18/2015.
 */
public class SongUtil {
    public static String[] getSongsFromURL(String URL) {
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            return null;
        }
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

    public static Media getMediaFromFile(File f) {
        return new Media(f.toURI().toString());
    }
}
