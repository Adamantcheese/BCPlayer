package tools;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import boot.Constants;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jacob on 3/22/2015.
 */
public class AlbumListUpdater {

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        if(args.length == 1) {
            update(args[0]);
        } else {
            System.out.println("This tool takes a single pathname to a nll file.");
        }
        long end = System.currentTimeMillis();
        long duration = end - start;
        int mins = (int) (duration/1000/60);
        int secs = (int) (duration/1000) - mins*60;
        System.out.println("Runtime: " + mins + "m " + secs + "s");
    }

    public static void update(String path) throws Exception {
        //Make sure the file is the right format
        if(!FilenameUtils.getExtension(path).equals("nll")) {
            System.err.println("File should have extension nll, with one URL per line.");
            return;
        }

        //Open up the file and scan it in
        Scanner albumScanner = new Scanner(new File(path));
        ArrayList<String> trackList = new ArrayList<String>(15000);

        //Expand URLs if needed
        while(albumScanner.hasNextLine()) {
            String URL = albumScanner.nextLine();
            if (URL.contains("track")) {
                trackList.add(URL);
            } else if(URL.contains("album")) {
                Document doc = null;
                do {
                    try {
                        doc = Jsoup.connect(URL).get();
                    } catch (SocketTimeoutException e) {

                    } catch (HttpStatusException e) {
                        System.out.println("URL returns a " + e.getStatusCode() +" error: " + URL);
                        break;
                    } catch (IOException e) {
                        System.err.println("Something bad happened! Stopping.");
                        return;
                    }
                } while (doc == null);
                if(doc == null) {
                    continue;
                }

                System.out.println("Expanding album: " + URL);
                Elements trackLinks = doc.getElementsByClass("title").select("div");

                for(Element track : trackLinks) {
                    if(track.getElementsByAttribute("href").first() == null) {
                        continue;
                    }
                    String trackAppend = track.getElementsByAttribute("href").first().attr("href");
                    String expandedURL = URL.replace(URL.substring(URL.lastIndexOf('/') - 6), "") + trackAppend;
                    trackList.add(expandedURL);
                    System.out.println("\tAdded track: " + expandedURL);
                }
            } else {
                //URL needs expansion, grab all the album and track links on the page
                Document doc = null;
                do {
                    try {
                        doc = Jsoup.connect(URL).get();
                    } catch (SocketTimeoutException e) {
                        continue;
                    } catch (HttpStatusException e) {
                        System.out.println("URL returns a " + e.getStatusCode() +" error: " + URL);
                        break;
                    } catch (IOException e) {
                        System.err.println("Something bad happened! Stopping.");
                        return;
                    }
                } while (doc == null);
                //Skip this URL if it returns a HTTP error
                if(doc == null) {
                    continue;
                }

                Elements listingGrid = doc.getElementsByClass("editable-grid");

                //If the URL doesn't need expansion and redirects to a default album, we skip it here
                if(listingGrid.size() == 0) {
                    System.out.println("Skipping defaults-to-album/track URL: " + URL);
                    trackList.add(URL);
                    continue;
                }

                System.out.println("Expanding listing page: " + URL);

                Element listing = listingGrid.first();

                Matcher listingMatcher = Pattern.compile("(<a href=\"/album/.*?\">|<a href=\"/track/.*?\">)+?").matcher(listing.html());
                while(listingMatcher.find()) {
                    String item = listingMatcher.group();
                    String expandedURL = URL + item.substring(10, item.length() - 2);
                    if(expandedURL.contains("album")) {
                        //The URL is an album, there is a list of track URL's on the page that need to be parsed
                        Document doc1 = null;
                        do {
                            try {
                                doc1 = Jsoup.connect(expandedURL).get();
                            } catch (SocketTimeoutException e) {
                                continue;
                            } catch (HttpStatusException e) {
                                System.out.println("URL returns a " + e.getStatusCode() +" error: " + expandedURL);
                                break;
                            } catch (IOException e) {
                                System.err.println("Something bad happened! Stopping.");
                                return;
                            }
                        } while (doc1 == null);
                        //Skip this URL if it returns a HTTP error
                        if(doc1 == null) {
                            continue;
                        }

                        System.out.println("\tExpanding album: " + expandedURL);
                        Elements trackLinks = doc1.getElementsByClass("title").select("div");

                        for(Element track : trackLinks) {
                            if(track.getElementsByAttribute("href").first() == null) {
                                continue;
                            }
                            String trackAppend = track.getElementsByAttribute("href").first().attr("href");
                            String expandedURL2 = expandedURL.replace(expandedURL.substring(expandedURL.lastIndexOf('/') - 6), "") + trackAppend;
                            trackList.add(expandedURL2);
                            System.out.println("\t\tAdded track: " + expandedURL2);
                        }
                    } else {
                        //The URL is a track, just add it straight up
                        trackList.add(expandedURL);
                        System.out.println("\tAdded track: " + expandedURL);
                    }
                }
            }
        }
        albumScanner.close();

        //Write the expanded version of the file back
        PrintWriter albumWriter = new PrintWriter(Constants.TRACK_FILE);
        for(int i = 0; i < trackList.size(); i++) {
            albumWriter.print(trackList.get(i));
            if(i != trackList.size() - 1) {
                albumWriter.write('\n');
            }
        }
        albumWriter.close();
    }
}
