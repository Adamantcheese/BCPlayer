import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main extends Application {

    public final String BASE_DIR = System.getProperty("java.io.tmpdir") + "/BCPlayer/";

    @Override
    public void start (Stage primaryStage) throws Exception {
        //Startup our main randomizer
        Random r = new Random();

        //Make the temp directory if it doesn't exist
        File baseDir = new File(BASE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        //Load in the array of strings
        ArrayList<String> albums = new ArrayList<String>(750);
        File albumFile = new File(Class.forName("Main").getClassLoader().getResource("albums.csv").toURI());
        Scanner albumScanner = new Scanner(albumFile);
        while (albumScanner.hasNext()) {
            String line = albumScanner.nextLine();
            albums.add(line.substring(1, line.length() - 1));
        }
        albumScanner.close();
        String[] albumURLs = new String[albums.size()];
        albums.toArray(albumURLs);

        //Display the window with UI
        Parent root = FXMLLoader.load(Class.forName("Main").getClassLoader().getResource("ui.fxml"));
        primaryStage.setTitle("Bandcamp Player");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        //Start the main loop, playing random songs until closed
        //while (true) {
            //Get a random album URL
            String URL = albumURLs[r.nextInt(albumURLs.length)];
            Document doc = Jsoup.connect(URL).get();
            Element script = doc.select("script").get(8);
            Pattern p = Pattern.compile("\"mp3-128\":\".*?\"");
            Matcher match = p.matcher(script.html());

            //Get all the songs in that URL
            ArrayList<String> possibles = new ArrayList<String>(15);
            while (match.find()) {
                String found = match.group();
                possibles.add(found.substring(11, found.length() - 1));
            }
            String[] songs = new String[possibles.size()];
            possibles.toArray(songs);

            //Load in a the test file and download it
            File tempFile = new File(BASE_DIR + "temp.mp3");
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
            FileUtils.copyURLToFile(new URL(songs[r.nextInt(songs.length)]), tempFile);

            //Setup the player with the testing file and play it
            Media m = new Media(tempFile.toURI().toString());
            MediaPlayer test = new MediaPlayer(m);
            test.play();
        //}
    }

    public static void main (String[] args) {
        launch(args);
    }
}
