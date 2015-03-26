import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import objects.Track;
import org.apache.commons.io.FilenameUtils;
import tools.AlbumListUpdater;
import util.Constants;
import objects.SongDownloader;
import constructs.TrackContainer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {

    public static TrackContainer songHelper;
    public static File[] songFiles;
    public static int lastMP3PlayedIndex;

    public static void main (String[] args) throws Exception {
        //If the user specified a file, make sure it has the right extension
        if (args.length == 1 && FilenameUtils.getExtension(args[0]) != "nll") {
            System.err.println("File should have extension nll, with one URL per line.");
            return;
        }

        //Copy the internal track file to the temp directory, or expand the one specified to the temp directory
        if (!Constants.TRACK_FILE.exists()){
            if (args.length == 1) {
                System.out.println("Warning: The operation may take upwards of 30 minutes to finish, do you want to continue? (Y/N)");
                Scanner keyboard = new Scanner(System.in);
                String ans = "";
                while(!ans.equals("y") || !ans.equals("n")) {
                    ans = keyboard.nextLine().toLowerCase();
                }
                if(ans.equals("n")) {
                    return;
                } else {
                    AlbumListUpdater.update(args[0]);
                }
            } else {
                Scanner tempScanner = new Scanner(Constants.getInternalTrackFile());
                PrintWriter tempWriter = new PrintWriter(Constants.TRACK_FILE);
                while (tempScanner.hasNextLine()) {
                    tempWriter.println(tempScanner.nextLine());
                }
                tempScanner.close();
                tempWriter.close();
            }
        }

        //Initialize the song helper
        songHelper = TrackContainer.getInstance();

        //Make the temp directory if it doesn't exist
        if (!Constants.BASE_DIR.exists()) {
            Constants.BASE_DIR.mkdirs();
        }

        //Allocate space for the song files
        songFiles = new File[Constants.SONG_BUFFER_SIZE + 1];

        //Load where we left off last
        if (Constants.TEMP_FILE.exists()) {
            Scanner last = new Scanner(Constants.TEMP_FILE);
            lastMP3PlayedIndex = last.nextInt();
            //If the user changed the buffer size at compile time, but had a previous save file, reset the play index
            if(lastMP3PlayedIndex >= Constants.SONG_BUFFER_SIZE || lastMP3PlayedIndex < 0) {
                lastMP3PlayedIndex = 0;
            }
            last.close();
        } else {
            lastMP3PlayedIndex = 0;
            Constants.TEMP_FILE.createNewFile();
            PrintWriter printWriter = new PrintWriter(Constants.TEMP_FILE);
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
                Track song = songHelper.getSong(songURL.toString());

                //Check the filesize, make sure it is smaller than 10MB
                HttpURLConnection filesizeCheck = null;
                try {
                    filesizeCheck = (HttpURLConnection) song.getTrackURL().openConnection();
                    filesize = filesizeCheck.getContentLength();
                } catch (IOException e) {
                    filesize = -1;
                } finally {
                    filesizeCheck.disconnect();
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
        PrintWriter printWriter = new PrintWriter(Constants.TEMP_FILE);
        printWriter.write(Integer.toString(lastMP3PlayedIndex));
        printWriter.close();
    }

    @Override
    public void start (Stage primaryStage) throws Exception {
        //Display the window with UI
        Parent root = FXMLLoader.load(Main.class.getResource("ui.fxml"));
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
}
