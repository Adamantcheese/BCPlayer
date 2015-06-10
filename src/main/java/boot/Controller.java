package boot;

import constructs.PlayerContainer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import objects.Track;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public Label info;
    public ImageView playPauseIcon;
    public ImageView albumArt;
    public ImageView repeatButton;
    public ImageView downloadButton;

    private Track curTrack;
    private static PlayerContainer playerContainer;
    private static InfoWatcher infoWatcher;

    private LinkedList<Track> history;
    private boolean repeat;

    @FXML
    private void playPause () {
        playerContainer.pauseToggle();
        if (playerContainer.isPlaying()) {
            playPauseIcon.setImage(Constants.getPauseButton());
        } else if (playerContainer.isPaused()) {
            playPauseIcon.setImage(Constants.getPlayButton());
        }
    }

    @FXML
    private void playNext () {
        infoWatcher.skip();
        do {
            setNextSong();
        } while (playerContainer == null);
        albumArt.setImage(new Image(curTrack.getArtURL().toString()));
        playerContainer.playSong();
        playPauseIcon.setImage(Constants.getPauseButton());
        infoWatcher = new InfoWatcher();

        if(!repeat) {
            downloadButton.setDisable(false);
            downloadButton.setVisible(true);
        }
    }

    @FXML
    private void playPrev() {
        /*infoWatcher.skip();
        do {
            setNextSong();
        } while (playerContainer == null);
        albumArt.setImage(new Image(curTrack.getArtURL().toString()));
        playerContainer.playSong();
        playPauseIcon.setImage(Constants.getPauseButton());
        infoWatcher = new InfoWatcher();*/

        if(!repeat) {
            downloadButton.setDisable(false);
            downloadButton.setVisible(true);
        }
    }

    @FXML
    private void openURL() {
        if(playerContainer.isPlaying()) {
            playerContainer.pauseToggle();
            playPauseIcon.setImage(Constants.getPlayButton());
        }
        Constants.getHostServices().showDocument(curTrack.getPageURL());
    }

    @FXML
    private void downloadCurSong() {
        Constants.getDownloadManager().download(curTrack);
        downloadButton.setDisable(true);
        downloadButton.setVisible(false);
    }

    @FXML
    private void toggleRepeat() {
        if(repeat) {
            repeat = false;
            repeatButton.setImage(Constants.getRepeatOffButton());
        } else {
            repeat = true;
            repeatButton.setImage(Constants.getRepeatOnButton());
        }
    }

    private void handleKeyInput (KeyEvent event) {
        switch(event.getCode()) {
            case LEFT:
                playPrev();
                break;
            case RIGHT:
                playNext();
                break;
            case SPACE:
                playPause();
                break;
            case UP:
                openURL();
                break;
            case DOWN:
                downloadCurSong();
                break;
            case CONTROL:
                toggleRepeat();
                break;
            default:
                break;
        }
    }

    public void initialize (URL location, ResourceBundle resources) {
        history = new LinkedList<Track>();
        for(int i = 0; i < 50;) {
            try {
                history.add(Constants.getTrackHelper().getRandomSong());
                i++;
            } catch (Exception e) {

            }
        }
        repeat = false;
        playerContainer = null;
        while (playerContainer == null) {
            setNextSong();
        }
        albumArt.setImage(new Image(curTrack.getArtURL().toString()));
        playerContainer.playSong();
        infoWatcher = new InfoWatcher();
    }

    private void setNextSong () {
        do {
            try {
                if(!repeat) {
                    curTrack = Constants.getTrackHelper().getRandomSong();
                }
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

    public static void killCurSong() {
        infoWatcher.skip();
    }

    private class InfoWatcher extends Thread {
        private boolean skip;

        public InfoWatcher () {
            skip = false;
            this.start();
        }

        public void run () {
            PlayerContainer p = playerContainer;
            while (!p.isFinished() && !skip) {
                if (!p.isPaused()) {
                    Platform.runLater(new Runnable() {
                        public void run () {
                            info.setText(curTrack.getArtist() + '\n' + curTrack.getTrackName() + '\n' + playerContainer.getCurrentTime() + '/' + curTrack.getDuration());
                        }
                    });
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            p.stopSong();
            if (!skip) {
                playNext();
            }
        }

        public void skip () {
            skip = true;
        }
    }
}