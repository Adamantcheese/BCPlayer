
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.media.*;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.*;
import java.util.*;


public class Main extends Application {

    public static SongUtil songHelper;
    public static File[] songFiles;
    public static int lastMP3PlayedIndex;

    @Override
    public void start (Stage primaryStage) throws Exception {
        //Display the window with UI
        Parent root = null;
        root = FXMLLoader.load(Main.class.getResource("ui.fxml"));
        primaryStage.setTitle("Bandcamp Player");
        primaryStage.setScene(new Scene(root, 300, 300));
        primaryStage.show();

        //Start playing the first song
        File mp3File = songFiles[lastMP3PlayedIndex];
        Media song = new Media(mp3File.toURI().toString());
        MediaPlayer player = new MediaPlayer(song);
        player.play();

        //Start downloading the next song
        int nextDL = lastMP3PlayedIndex - 1;
        if (nextDL < 0) {
            nextDL = Constants.SONG_BUFFER_SIZE;
        }
        mp3File = songFiles[nextDL];
        SongDownloader temp = new SongDownloader(songHelper.getRandomSong(), mp3File);

        //Move the last played index forward
        lastMP3PlayedIndex++;
    }

    public static void main (String[] args) throws Exception {
        //Allow the user to enter a location for the list to be found
        if (args.length == 1) {
            if (FilenameUtils.getExtension(args[0]) != "nll") {
                System.err.println("Album list file should have the extension nll.");
                return;
            }
            songHelper = SongUtil.getInstance(args[0]);
        } else {
            songHelper = SongUtil.getInstance(null);
        }

        //Make the temp directory if it doesn't exist
        File baseDir = new File(Constants.BASE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        //Allocate space for the song files
        songFiles = new File[Constants.SONG_BUFFER_SIZE + 1];

        //Load where we left off last
        File tempFile = new File(Constants.BASE_DIR + "last.tmp");
        if (tempFile.exists()) {
            Scanner last = new Scanner(tempFile);
            lastMP3PlayedIndex = last.nextInt();
            last.close();
        } else {
            lastMP3PlayedIndex = 0;
            tempFile.createNewFile();
            PrintWriter printWriter = new PrintWriter(tempFile);
            printWriter.write(Integer.toString(lastMP3PlayedIndex));
            printWriter.close();
        }

        //Load up n random songs and make a blank one for the sixth
        //Only for initial startup
        ArrayList<SongDownloader> downloaders = new ArrayList<SongDownloader>();
        for (int i = 0; i <= Constants.SONG_BUFFER_SIZE; i++) {
            //Setup the file
            File tempMP3File = new File(Constants.BASE_DIR + "temp" + i + ".mp3");
            if (!tempMP3File.exists()) {
                tempMP3File.createNewFile();
            } else {
                songFiles[i] = tempMP3File;
                continue;
            }

            //If it is the nth file, skip the download, it will be filled later
            if (i == Constants.SONG_BUFFER_SIZE) {
                songFiles[i] = tempMP3File;
                continue;
            }

            //Get a song that is under the given filesize (10MB)
            int filesize = -1;
            URL songURL = null;
            do {
                //Get the song, avoiding pages with no songs/404 errors
                songURL = songHelper.getRandomSong();

                //Check the filesize, make sure it is smaller than 10MB
                HttpURLConnection filesizeCheck = null;
                try {
                    filesizeCheck = (HttpURLConnection) songURL.openConnection();
                    filesize = filesizeCheck.getContentLength();
                } catch (IOException e) {
                    filesize = -1;
                } finally {
                    filesizeCheck.disconnect();
                }

                //File is larger than 10MB, skip it
                if (filesize > 10 * Constants.MAX_FILESIZE) {
                    System.err.println("Filesize for given song is greater than " + Constants.MAX_MB + "MB, skipping.");
                } else if (filesize < 0) {
                    System.err.println("File doesn't exist, trying again.");
                }
            } while (filesize < 0 || filesize > 10 * Constants.MAX_FILESIZE);

            //Download the song
            downloaders.add(new SongDownloader(songURL, tempMP3File));
            songFiles[i] = tempMP3File;
        }

        //Wait for all the downloads to finish
        for (SongDownloader songDownloader : downloaders) {
            songDownloader.join();
        }

        //Launch the application
        System.out.println("Song buffer ready, launching!");
        launch(args);

        //Save where we are leaving off in the array
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(tempFile);
        } catch (FileNotFoundException e) {
            try {
                tempFile.createNewFile();
            } catch (IOException e1) {
                return;
            }
        }
        printWriter.write(Integer.toString(lastMP3PlayedIndex));
        printWriter.close();
    }
}
