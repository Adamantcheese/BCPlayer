package boot;

import constructs.DownloadManager;
import constructs.PlayerContainer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import objects.Downloader;
import objects.Track;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public Label info;
    public ImageView playPauseIcon;
    public ImageView albumArt;

    private Track curTrack;
    private PlayerContainer playerContainer;
    private InfoWatcher infoWatcher;

    private ArrayList<Track> history;
    private boolean repeat;

    private DownloadManager downloadManager;

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
    }

    private void playPrev() {

    }

    @FXML
    private void openURL() {
        if(playerContainer.isPlaying()) {
            playerContainer.pauseToggle();
            playPauseIcon.setImage(Constants.getPlayButton());
        }
        Constants.getHostServices().showDocument(curTrack.getPageURL());
    }

    private void downloadCurSong() {
        downloadManager.download(curTrack);
    }

    private void toggleRepeat() {
        if(repeat) {
            repeat = false;
        } else {
            repeat = true;
        }
    }

    @FXML
    private void handleKeyInput(KeyEvent event) {
        switch(event.getCode()) {
            case KP_LEFT:
            case LEFT:
                playPrev();
                break;
            case KP_RIGHT:
            case RIGHT:
                playNext();
                break;
            case SPACE:
                playPause();
                break;
            case KP_UP:
            case UP:
                openURL();
                break;
            case KP_DOWN:
            case DOWN:
                downloadCurSong();
                break;
            case CAPS:
            case CONTROL:
                toggleRepeat();
                break;
            default:
                break;
        }
    }

    public void initialize (URL location, ResourceBundle resources) {
        history = new ArrayList<Track>(50);
        repeat = false;
        downloadManager = new DownloadManager();
        downloadManager.start();
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