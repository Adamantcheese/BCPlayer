
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

/**
 * Created by Jacob on 3/18/2015.
 */
public class SongUtil {

    private static SongUtil INSTANCE;

    private static Random RANDOMIZER = new Random();
    private ArrayList<String> ALBUM_LIST;

    public static SongUtil getInstance() throws Exception {
        return getInstance(null);
    }

    public static SongUtil getInstance (String loc) throws Exception {
        if (INSTANCE == null) {
            INSTANCE = new SongUtil(loc);
        }
        return INSTANCE;
    }

    private SongUtil (String loc) throws Exception {
        //Init the album list
        ALBUM_LIST = new ArrayList<String>(750);

        //Load in the file from the specified location, or the internal file if none was given
        File albumFile = null;
        if (loc != null) {
            albumFile = new File(loc);
        } else {
            albumFile = new File(SongUtil.class.getResource("albums.nll").toURI());
        }

        //Internalize the given file and ignore it afterwards
        Scanner albumScanner = new Scanner(albumFile);
        while (albumScanner.hasNextLine()) {
            String URL = albumScanner.nextLine();
            if(URL.trim().length() == 0) {
                continue;
            }
            ALBUM_LIST.add(URL);
        }
        albumScanner.close();
    }

    public static String getRandomSongFromURL (String URL) {
        ArrayList<String> songs = getSongsFromURL(URL);
        if (songs == null || songs.size() == 0) {
            return null;
        }
        return songs.get(RANDOMIZER.nextInt(songs.size()));
    }

    public String getRandomAlbum () {
        return ALBUM_LIST.get(RANDOMIZER.nextInt(ALBUM_LIST.size()));
    }

    private static ArrayList<String> getSongsFromURL (String URL) {
        //Get the album HTML document
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

        //Get all the javascript elements on the page
        Elements scripts = doc.select("script");
        if (scripts.size() == 0) {
            return null;
        }

        //Setup the pattern for matching
        Pattern MP3_PATTERN = Pattern.compile("\"mp3-128\":\".*?\"");

        //Get the script that contains the mp3's
        Element mp3Script = null;
        for (Element script : scripts) {
            Matcher tempMatcher = MP3_PATTERN.matcher(script.html());
            if (tempMatcher.find()) {
                mp3Script = script;
                break;
            }
        }
        if (mp3Script == null) {
            return null;
        }

        //Get all the mp3 URLs from the script
        Matcher mp3Matcher = MP3_PATTERN.matcher(mp3Script.html());
        ArrayList<String> possibles = new ArrayList<String>(15);
        while (mp3Matcher.find()) {
            String found = mp3Matcher.group();
            possibles.add(found.substring(11, found.length() - 1));
        }

        //Return the mp3 URLs
        return possibles;
    }

    public URL getRandomSong () throws Exception {
        String songURL = null;
        do {
            String albumURL = getRandomAlbum();
            songURL = getRandomSongFromURL(albumURL);
            if (songURL == null) {
                System.err.println("Album URL:\n" + albumURL + "\ncontains no songs. Trying again.");
            }
        } while (songURL == null);
        return new URL(songURL);
    }

    public ArrayList<String> getAlbumList () {
        return ALBUM_LIST;
    }
}
