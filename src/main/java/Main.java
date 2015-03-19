import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.media.*;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.*;


public class Main extends Application {

    public final String BASE_DIR = System.getProperty("java.io.tmpdir") + "/BCPlayer/";
    public final Random RANDOMIZER = new Random();

    @Override
    public void start (Stage primaryStage) throws Exception {
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

        //Get a random album URL
        String URL = albumURLs[RANDOMIZER.nextInt(albumURLs.length)];

        //Get all the songs in that URL
        String[] songs = SongUtil.getSongsFromURL(URL);

        //Download the file to be played
        File tempFile = new File(BASE_DIR + "temp.mp3");
        if (!tempFile.exists()) {
            tempFile.createNewFile();
            //FileUtils.copyURLToFile(new URL(songs[RANDOMIZER.nextInt(songs.length)]), tempFile);
        }
        FileUtils.copyURLToFile(new URL(songs[RANDOMIZER.nextInt(songs.length)]), tempFile);

        //Setup the player with the testing file and play it
        Media m = SongUtil.getMediaFromFile(tempFile);
        MediaPlayer test = new MediaPlayer(m);
        test.play();
    }

    public static void main (String[] args) {
        launch(args);
    }
}
