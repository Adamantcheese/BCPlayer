package constructs;

import javazoom.jl.decoder.JavaLayerException;
import objects.PlaybackEvent;
import objects.PlaybackListener;
import objects.Player;
import objects.Track;

import java.net.URL;

/**
 * Created by Jacob on 3/31/2015.
 */
public class PlayerContainer extends Thread {

    private Player player;

    public PlayerContainer(Track track) throws Exception {
        player = new Player(track.getTrackURL().openStream());
        setupListener();
    }

    public PlayerContainer(URL test) throws Exception {
        player = new Player(test.openStream());
        setupListener();
    }

    public void run(){
        try {
            player.play();
        } catch (JavaLayerException e) {
            return;
        }
    }

    public void playSong() {
        this.start();
    }

    public void togglePause() {
        player.pauseToggle();
    }

    public void stopSong() {
        player.stop();
    }

    public String getCurrentTime() {
        String dur = "";
        int duration = player.getPosition();
        int hours = (int) (duration/1000/60/60);
        int minutes = (int) (duration/1000/60) - hours*60;
        int seconds = (int) (duration/1000) - minutes*60;
        if(hours > 0) {
            dur += hours + ":";
        }
        dur += minutes + ":";
        dur += seconds;
        return dur;
    }

    public void setupListener() {
        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackFinished (PlaybackEvent evt) {
                System.out.println("Finished playing song.");
                playbackStopped(evt);
            }

            @Override
            public void playbackPaused (PlaybackEvent evt) {
                System.out.println("Paused song.");
            }

            @Override
            public void playbackUnpaused (PlaybackEvent evt) {
                System.out.println("Unpaused song.");
            }

            @Override
            public void playbackStopped (PlaybackEvent evt) {
                System.out.println("Stopped song.");
            }
        });
    }
}
