package boot;

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
    private void playNext () throws Exception {
        infoWatcher.skip();
        if(repeat) {
            playerContainer = new PlayerContainer(curTrack);
        } else {
            do {
                setNextSong();
            } while (playerContainer == null);
        }
        albumArt.setImage(new Image(curTrack.getArtURL().toString()));
        playerContainer.playSong();
        playPauseIcon.setImage(Constants.getPauseButton());
        infoWatcher = new InfoWatcher();
    }

    @FXML
    private void playPrev() {

    }

    @FXML
    private void toggleRepeat() {
        if(repeat) {
            repeat = false;
        } else {
            repeat = true;
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
    private void handleKeyInput(KeyEvent event) throws Exception {
        if(event.getCode() == KeyCode.LEFT) {
            //Left is go back one song
            playPrev();
        } else if (event.getCode() == KeyCode.RIGHT) {
            //Right is go to the next song
            playNext();
        } else if (event.getCode() == KeyCode.SPACE) {
            //Space is pause/unpause
            playPause();
        } else if (event.getCode() == KeyCode.UP) {
            //Up is open the page containing the track
            openURL();
        } else if (event.getCode() == KeyCode.DOWN) {
            //Down may be for downloading the track, dunno
            downloadCurrentTrack();
        } else if (event.getCode() == KeyCode.CONTROL) {
            //Control means toggle repeat
            toggleRepeat();
        }
    }

    @FXML
    private void downloadCurrentTrack() {
        Downloader downloader = new Downloader(curTrack);
        downloader.start();
    }

    public void initialize (URL location, ResourceBundle resources) {
        history = new ArrayList<Track>();
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
                curTrack = Constants.getTrackHelper().getRandomSong();
            } catch (Exception e) {
                curTrack = null;
            }
        } while (curTrack == null || curTrack.getTrackURL() == null);

        history.add(curTrack);

        if(history.size() > 50) {
            history.remove(0);
        }

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
                try {
                    playNext();
                } catch (Exception e) {
                    System.exit(-1);
                }
            }
        }

        public void skip () {
            skip = true;
        }
    }
}