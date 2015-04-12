package boot;

import constructs.PlayerContainer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import objects.Track;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public Label info;
    public ImageView playPauseIcon;
    public ImageView albumArt;

    private Track curTrack;
    private PlayerContainer playerContainer;
    private InfoWatcher infoWatcher;

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
        infoWatcher.skip();
        do {
            setNextSong();
        } while (playerContainer == null);
        albumArt.setImage(new Image(curTrack.getArtURL().toString()));
        playerContainer.playSong();
        infoWatcher = new InfoWatcher();
    }

    @Override
    public void initialize (URL location, ResourceBundle resources) {
        playerContainer = null;
        while (playerContainer == null) {
            setNextSong();
        }
        albumArt.setImage(new Image(curTrack.getArtURL().toString()));
        playerContainer.playSong();
        infoWatcher = new InfoWatcher();
    }

    private void setNextSong() {
        do {
            try {
                curTrack = Constants.getTrackHelper().getRandomSong();
            } catch (Exception e) {
                curTrack = null;
            }
        } while (curTrack == null || curTrack.getTrackURL() == null);
        try {
            playerContainer = new PlayerContainer(curTrack);
        } catch (Exception e) {
            playerContainer = null;
        }
    }

    private class InfoWatcher extends Thread {
        private boolean skip;

        public InfoWatcher() {
            skip = false;
            this.start();
        }

        public void run() {
            PlayerContainer p = playerContainer;
            while(!p.isFinished() && !skip) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run () {
                        info.setText(curTrack.getArtist() + '\n' + curTrack.getTrackName() + '\n' + playerContainer.getCurrentTime() + '/' + curTrack.getDuration());
                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            p.stopSong();
            if(!skip) {
                playNext();
            }
        }

        public void skip() {
            skip = true;
        }
    }
}