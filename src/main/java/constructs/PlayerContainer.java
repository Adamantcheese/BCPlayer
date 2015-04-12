package constructs;

import objects.Player;
import objects.Track;

/**
 * Created by Jacob on 3/31/2015.
 */
public class PlayerContainer extends Thread {

    private Player player;

    public PlayerContainer (Track track) throws Exception {
        player = new Player(track.getTrackURL().openStream());
    }

    public void run () {
        player.playSong();
    }

    public void playSong () {
        this.start();
    }

    public void pauseToggle () {
        player.pauseToggle();
    }

    public void stopSong () {
        player.stopSong();
    }

    public boolean isPlaying () {
        return player.isPlaying();
    }

    public boolean isFinished () {
        return player.isFinished();
    }

    public boolean isPaused () {
        return player.isPaused();
    }

    public String getCurrentTime () {
        String dur = "";
        int duration = player.getPosition();
        int hours = (int) (duration / 1000 / 60 / 60);
        int minutes = (int) (duration / 1000 / 60) - hours * 60;
        int seconds = (int) (duration / 1000) - minutes * 60;
        if (hours > 0) {
            dur += String.format("%02d:", hours);
        }
        dur += String.format("%02d:%02d", minutes, seconds);
        return dur;
    }
}
