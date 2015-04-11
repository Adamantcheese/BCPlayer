import boot.Constants;
import constructs.PlayerContainer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import objects.Track;
import org.apache.commons.io.FilenameUtils;
import tools.AlbumListUpdater;

import java.io.PrintWriter;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start (Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("ui.fxml"));
        primaryStage.setTitle("Bandcamp Player");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle (WindowEvent event) {
                System.exit(0);
            }
        });
        primaryStage.setResizable(false);

        primaryStage.setScene(new Scene(root, 300, 300));
        primaryStage.show();
    }

    public static void writeTrackFile() throws Exception {
        Scanner tempScanner = new Scanner(Constants.getInternalTrackFile());
        PrintWriter tempWriter = new PrintWriter(Constants.TRACK_FILE);
        while (tempScanner.hasNextLine()) {
            tempWriter.println(tempScanner.nextLine());
        }
        tempScanner.close();
        tempWriter.close();
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
                    writeTrackFile();
                } else {
                    AlbumListUpdater.update(args[0]);
                }
            } else {
                writeTrackFile();
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
