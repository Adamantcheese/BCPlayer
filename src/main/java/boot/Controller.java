package boot;

import constructs.PlayerContainer;
import javafx.event.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import objects.Track;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public Button playPause;
    public Button next;
    public Text info;
    public ImageView playPauseIcon;
    public ImageView albumArt;

    private Track curTrack;
    private PlayerContainer playerContainer;

    private void pauseToggle() {
        playerContainer.pauseToggle();
        if(playerContainer.isPlaying()) {
            playPauseIcon.setImage(Constants.getPauseButton());
        } else if (playerContainer.isPaused()) {
            playPauseIcon.setImage(Constants.getPlayButton());
        }
    }

    private void playNext() {
        playerContainer.stopSong();
        playerContainer = null;
        while (playerContainer == null) {
            getNextSong();
        }
        albumArt.setImage(new Image(curTrack.getArtURL().toString()));
        playerContainer.playSong();
    }

    @Override
    public void initialize (URL location, ResourceBundle resources) {
        //Initialize the handlers
        playPause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle (ActionEvent event) {
                pauseToggle();
            }
        });

        next.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle (ActionEvent event) {
                playNext();
            }
        });

        //Play the first song
        playerContainer = null;
        while (playerContainer == null) {
            getNextSong();
        }
        albumArt.setImage(new Image(curTrack.getArtURL().toString()));
        playerContainer.playSong();
    }

    private void getNextSong() {
        curTrack = null;
        while(curTrack == null || curTrack.getTrackURL() == null) {
            try {
                curTrack = Constants.getTrackHelper().getRandomSong();
            } catch (Exception e) {
                curTrack = null;
            }
        }
        try {
            playerContainer = new PlayerContainer(curTrack);
        } catch (Exception e) {
            playerContainer = null;
        }
    }
}