import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;


public class Main extends Application {

    public static final String BASE_DIR = System.getProperty("java.io.tmpdir") + "/BCPlayer/";
    public static final int SONG_BUFFER_SIZE = 5;
    public static SongUtil songHelper;
    public static File[] songFiles;
    public static int lastIndex;

    @Override
    public void start (Stage primaryStage) throws Exception {
        //Display the window with UI
        Parent root = FXMLLoader.load(Class.forName("Main").getClassLoader().getResource("ui.fxml"));
        primaryStage.setTitle("Bandcamp Player");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        //Start playing the first song
        MediaPlayer player = new MediaPlayer(songHelper.getMediaFromFile(songFiles[lastIndex]));
        player.play();
    }

    public static void main (String[] args) throws Exception {
        //Make the temp directory if it doesn't exist
        File baseDir = new File(BASE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        //Init the song helper
        songHelper = new SongUtil();

        //Allocate space for the song files
        songFiles = new File[SONG_BUFFER_SIZE + 1];

        //Load up 5 random songs and make a blank one for the sixth
        //Only for initial startup
        for (int i = 0; i <= SONG_BUFFER_SIZE; i++){
            File tempFile = new File(BASE_DIR + "temp" + i + ".mp3");
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            } else {
                songFiles[i] = tempFile;
                continue;
            }
            if(i == SONG_BUFFER_SIZE) {
                continue;
            }
            String albumURL = songHelper.getRandomAlbum();
            String songURL = songHelper.getRandomSongFromURL(albumURL);
            FileUtils.copyURLToFile(new URL(songURL), tempFile);
        }

        //Load where we left off last
        File lastIndexFile = new File(BASE_DIR + "last.tmp");
        if(lastIndexFile.exists()) {
            Scanner last = new Scanner(lastIndexFile);
            lastIndex = last.nextInt();
            last.close();
        } else {
            lastIndex = 0;
            lastIndexFile.createNewFile();
        }

        //Launch the application
        launch(args);

        //Save where we are leaving off in the array
        PrintWriter printWriter = new PrintWriter(lastIndexFile);
        printWriter.write(Integer.toString(lastIndex));
        printWriter.close();
    }
}
