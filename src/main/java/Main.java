import boot.Constants;
import boot.Controller;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FilenameUtils;
import tools.AlbumListUpdater;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start (final Stage primaryStage) throws Exception {
        Constants.setHostServices(HostServicesFactory.getInstance(this));

        final Parent root = FXMLLoader.load(Main.class.getResource("ui.fxml"));

        primaryStage.setTitle("Bandcamp Player");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle (WindowEvent event) {
                primaryStage.close();
                Controller.killCurSong();
                while(!Constants.getDownloadManager().allDone()) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }
                System.exit(0);
            }
        });
        primaryStage.setResizable(false);
        primaryStage.getIcons().addAll(Constants.getIcons());

        primaryStage.setScene(new Scene(root, 300, 300));
        primaryStage.show();
    }

    public static void main (String[] args) throws Exception {
        boolean forceUpdate = false;
        //If the user specified a file, make sure it has the right extension
        if(args.length == 1 && args[0].equals("--forcelist")) {
            forceUpdate = true;
        } else if (args.length == 1 && !FilenameUtils.getExtension(args[0]).equals("nll")) {
            System.err.println("File should have extension nll, with one URL per line.");
            return;
        }

        //Make the temp directory if it doesn't exist
        if (!Constants.BASE_DIR.exists()) {
            Constants.BASE_DIR.mkdirs();
        }

        //Copy the internal track file to the temp directory, or expand the one specified to the temp directory
        if (!Constants.TRACK_FILE.exists()) {
            writeInternalFile();
        } else if (forceUpdate) {
            Constants.TRACK_FILE.delete();
            writeInternalFile();
        }

        //Update the track file with the one specified if so
        if (args.length == 1 && !forceUpdate) {
            System.out.println("Warning: The operation may take a while to finish, do you want to continue? (Y/N)");
            Scanner keyboard = new Scanner(System.in);
            String ans = "";
            while (!(ans.equals("y") || ans.equals("n"))) {
                ans = keyboard.nextLine().toLowerCase();
            }
            if (ans.equals("y")) {
                Constants.TRACK_FILE.delete();
                AlbumListUpdater.update(args[0]);
            }
        }

        //Launch the application
        launch(args);
    }

    public static void writeInternalFile() {
        //Write the track file
        Scanner tempScanner = new Scanner(ClassLoader.getSystemClassLoader().getResourceAsStream("tracks.nll"));
        PrintWriter tempWriter = null;
        try {
            tempWriter = new PrintWriter(Constants.TRACK_FILE);
        } catch (FileNotFoundException e) {
            return;
        }
        while (tempScanner.hasNextLine()) {
            tempWriter.println(tempScanner.nextLine());
        }
        tempScanner.close();
        tempWriter.close();
    }
}
