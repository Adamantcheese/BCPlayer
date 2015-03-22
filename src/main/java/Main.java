import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class Main extends Application {

    public static final String BASE_DIR = System.getProperty("java.io.tmpdir") + "BCPlayer/";
    public static final int SONG_BUFFER_SIZE = 5;
    public static SongUtil songHelper;
    public static File[] songFiles;
    public static int lastMP3PlayedIndex;
    public static String listLocation;

    @Override
    public void start (Stage primaryStage) throws Exception {
        //Display the window with UI
        Parent root = null;
        root = FXMLLoader.load(Class.forName("Main").getClassLoader().getResource("ui.fxml"));
        primaryStage.setTitle("Bandcamp Player");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        //Load up 5 random songs and make a blank one for the sixth
        //Only for initial startup
        for (int i = 0; i <= SONG_BUFFER_SIZE; i++){
            //Setup the file
            File tempMP3File = new File(BASE_DIR + "temp" + i + ".mp3");
            if (!tempMP3File.exists()) {
                tempMP3File.createNewFile();
            } else {
                songFiles[i] = tempMP3File;
                continue;
            }
            if(i == SONG_BUFFER_SIZE) {
                continue;
            }

            //Get a song that is under the given filesize (10MB)
            int filesize = -1;
            String songURL = null;
            URL songURLObj = null;
            do {
                //Get the song, avoiding pages with no songs/404 errors
                do {
                    String albumURL = songHelper.getRandomAlbum();
                    songURL = songHelper.getRandomSongFromURL(albumURL);
                    if(songURL == null) {
                        System.err.println("Album URL:\n" + albumURL + "\ncontains no songs. Trying again.");
                    }
                } while (songURL == null);

                //Check the filesize, make sure it is smaller than 10MB
                songURLObj = new URL(songURL);
                HttpURLConnection filesizeCheck = null;
                try {
                    filesizeCheck = (HttpURLConnection) songURLObj.openConnection();
                    filesize = filesizeCheck.getContentLength();
                } catch (IOException e) {
                    filesize = -1;
                } finally {
                    filesizeCheck.disconnect();
                }

                //File is larger than 10MB, skip it
                if (filesize > 10000000) {
                    System.err.println("Filesize for given song is greater than 10MB, skipping.");
                } else if (filesize < 0) {
                    System.err.println("File doesn't exist, trying again.");
                }
            } while (filesize < 0 || filesize > 10000000 || songURLObj == null);

            //Download the song
            System.out.println("Downloading song from URL:\n" + songURL + "\nto file:\n" + tempMP3File.toString().replace('\\', '/'));
            FileUtils.copyURLToFile(songURLObj, tempMP3File);
            songFiles[i] = tempMP3File;
            System.out.println("-----------------------------------------------------------------------------");
        }
        System.out.println("Song buffer ready, launching!");

        //Start playing the first song
        File mp3File = songFiles[lastMP3PlayedIndex];
        Media song = songHelper.getMediaFromFile(mp3File);
        MediaPlayer player = new MediaPlayer(song);
        player.play();
    }

    public static void main (String[] args) throws Exception {
        //Allow the user to enter a location for the list to be found
        if(args.length == 1) {
            if(FilenameUtils.getExtension(args[0]) != "nll") {
                System.err.println("Album list file should have the extension nll.");
                return;
            }
            listLocation = args[0];
        } else {
            listLocation = null;
        }

        //Make the temp directory if it doesn't exist
        File baseDir = new File(BASE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
            System.out.println("Made temporary directory: " + BASE_DIR.replace('\\', '/'));
        }

        //Init the song helper
        songHelper = new SongUtil();

        //Allocate space for the song files
        songFiles = new File[SONG_BUFFER_SIZE + 1];

        //Load where we left off last
        File tempFile = new File(BASE_DIR + "last.tmp");
        if(tempFile.exists()) {
            Scanner last = new Scanner(tempFile);
            lastMP3PlayedIndex = last.nextInt();
            last.close();
        } else {
            lastMP3PlayedIndex = 0;
            tempFile.createNewFile();
            PrintWriter printWriter = new PrintWriter(tempFile);
            printWriter.write(Integer.toString(lastMP3PlayedIndex));
            printWriter.close();
            System.out.println("Made location store: " + BASE_DIR.replace('\\', '/') + "last.tmp");
        }

        //Launch the application
        launch(args);

        //Save where we are leaving off in the array
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(tempFile);
        } catch (FileNotFoundException e) {
            try {
                tempFile.createNewFile();
                System.out.println("Made location store: " + BASE_DIR.replace('\\', '/') + "last.tmp");
            } catch (IOException e1) {
                return;
            }
        }
        printWriter.write(Integer.toString(lastMP3PlayedIndex));
        printWriter.close();
    }
}
