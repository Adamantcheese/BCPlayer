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
    private int frame;

    public PlayerContainer(Track track) throws Exception {
        player = new Player(track.getTrackURL().openStream());
        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackFinished (PlaybackEvent evt) {

            }

            @Override
            public void playbackPaused (PlaybackEvent evt) {

            }

            @Override
            public void playbackUnpaused (PlaybackEvent evt) {

            }
        });
        frame = 0;
    }

    public PlayerContainer(URL test) throws Exception {
        player = new Player(test.openStream());
        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackFinished (PlaybackEvent evt) {
                frame = evt.getFrame();
            }
        });
        frame = 0;
    }

    public void run(){
        try {
            player.play(frame, Integer.MAX_VALUE);
        } catch (JavaLayerException e) {
            return;
        }
    }

    public void playSong() {
        this.start();
    }

    public void pauseSong() {
        player.pause();
    }

    public void stopSong() {
        this.stop();
        frame = 0;
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
}
