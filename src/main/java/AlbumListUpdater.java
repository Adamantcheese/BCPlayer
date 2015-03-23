import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.regex.*;

/**
 * Created by Jacob on 3/22/2015.
 */
public class AlbumListUpdater {

    private static final String ALBUM_REGEX = "<a href=\"/album/.*?\">";
    private static final String TRACK_REGEX = "<a href=\"/track/.*?\">";
    private static final Pattern EXPANSION_PATTERN = Pattern.compile("(" + ALBUM_REGEX + "|" + TRACK_REGEX + ")+?");

    public static void main(String[] args) throws Exception {
        //Open up the internal album list file
        File albumListFile = new File(AlbumListUpdater.class.getResource("albums.nll").toURI());
        Scanner albumScanner = new Scanner(albumListFile);
        ArrayList<String> albumList = new ArrayList<String>(750);

        //Expand URLs if needed
        while(albumScanner.hasNextLine()) {
            //Check the URL and expand it if needed
            String URL = albumScanner.nextLine();
            if(URL.contains("soundcloud")) {
                //Skip soundcloud links
            } else if(URL.contains("album") || URL.contains("track")) {
                //Doesn't need expansion, we expect a list of mp3 files to already be on the page
                albumList.add(URL);
            } else {
                //URL needs expansion, grab all the album and track links on the page
                Document doc = null;
                do {
                    try {
                        doc = Jsoup.connect(URL).get();
                    } catch (SocketTimeoutException e) {
                        continue;
                    } catch (IOException e) {
                        System.err.println("Something bad happened! Stopping.");
                        return;
                    }
                } while (doc == null);

                Elements possibleListing = doc.getElementsByClass("editable-grid");

                //If the URL doesn't need expansion and redirects to a default album, we skip it here
                if(possibleListing.size() == 0) {
                    System.out.println("Skipping album/track URL: " + URL);
                    continue;
                }

                System.out.println("Expanding URL: " + URL);

                Element listing = possibleListing.first();

                Matcher listingMatcher = EXPANSION_PATTERN.matcher(listing.html());
                while(listingMatcher.find()) {
                    String item = listingMatcher.group();
                    String expandedURL = URL + item.substring(10, item.length() - 2);
                    albumList.add(expandedURL);
                    System.out.println("Added expanded URL: " + expandedURL);
                }
            }
        }
        albumScanner.close();

        System.out.println("---------------------------------------------------");

        //Remove entries without any songs in them
        for(int i = 0; i < albumList.size(); i++) {
            String URL = albumList.get(i);
            if(SongUtil.getRandomSongFromURL(URL) == null) {
                System.out.println("Removed URL without songs: " + URL);
                albumList.remove(URL);
            }
        }

        System.out.println("---------------------------------------------------");

        //Write the expanded version of the file back
        File newAlbumListFile = new File(Constants.BASE_DIR + "albums.nll");
        PrintWriter albumWriter = new PrintWriter(newAlbumListFile);
        for(String URL : albumList) {
            albumWriter.write(URL + "\n");
        }
        albumWriter.close();
    }
}
