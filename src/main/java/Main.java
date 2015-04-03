import constructs.PlayerContainer;
import constructs.PlayerTester;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import objects.Track;
import org.apache.commons.io.FilenameUtils;
import tools.AlbumListUpdater;
import util.Constants;

import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start (Stage primaryStage) throws Exception {
        //Start playing the song
        //Track track = Constants.getTrackHelper().getRandomSong();
        //PlayerContainer player = new PlayerContainer(track.getTrackURL());
        //player.playSong();

        PlayerContainer player = new PlayerContainer(new URL("http://popplers5.bandcamp.com/download/track?enc=mp3-128&fsig=073d42f36a880ac9b4ec5590137dd476&id=4078077538&stream=1&ts=1427996072.0"));
        PlayerTester playerTester = new PlayerTester(player, true);
        playerTester.start();

        //Display the window with UI
        Parent root = FXMLLoader.load(Main.class.getResource("ui.fxml"));
        primaryStage.setTitle("Bandcamp Player");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle (WindowEvent event) {
                System.exit(0);
            }
        });
        primaryStage.setResizable(false);

        StackPane sp = new StackPane();
        ImageView imgView = new ImageView(Constants.getDefaultAlbumCover());
        Text text = new Text(50, 250, player.getCurrentTime() + "/00:00");
        sp.getChildren().add(imgView);
        sp.getChildren().add(text);

        primaryStage.setScene(new Scene(sp, 300, 300));
        primaryStage.show();
    }

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
                while(!(ans.equals("y") || ans.equals("n"))) {
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

        //Make the temp directory if it doesn't exist
        if (!Constants.BASE_DIR.exists()) {
            Constants.BASE_DIR.mkdirs();
        }

        //Launch the application
        launch(args);
    }
}
