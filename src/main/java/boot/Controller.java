package boot;

import constructs.PlayerContainer;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import objects.Track;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public Text info;
    public ImageView playPauseIcon;
    public ImageView albumArt;

    private Track curTrack;
    private PlayerContainer playerContainer;

    @FXML
    private void playPause() {
        playerContainer.pauseToggle();
        if(playerContainer.isPlaying()) {
            playPauseIcon.setImage(Constants.getPauseButton());
        } else if (playerContainer.isPaused()) {
            playPauseIcon.setImage(Constants.getPlayButton());
        }
    }

    @FXML
    private void playNext() {
        playerContainer.stopSong();
        playerContainer = null;
        while (playerContainer == null) {
            setNextSong();
        }
        albumArt.setImage(new Image(curTrack.getArtURL().toString()));
        playerContainer.playSong();
    }

    @Override
    public void initialize (URL location, ResourceBundle resources) {
        playerContainer = null;
        while (playerContainer == null) {
            setNextSong();
        }
        albumArt.setImage(new Image(curTrack.getArtURL().toString()));
        playerContainer.playSong();
    }

    private void setNextSong() {
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