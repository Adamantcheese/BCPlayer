package constructs;

import objects.Player;
import objects.Track;

/**
 * Created by Jacob on 3/31/2015.
 */
public class PlayerContainer extends Thread {

    private Player player;
    private boolean hoursLong;

    public PlayerContainer (Track track) throws Exception {
        player = new Player(track.getTrackURL().openStream());
        hoursLong = (track.getDuration().split(":").length == 3);
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
        int hours = duration / 1000 / 60 / 60;
        int minutes = duration / 1000 / 60 - hours * 60;
        int seconds = duration / 1000 - minutes * 60 - hours * 3600;
        //If the track's duration contains hours, we add hours to the current time counter
        if (hoursLong) {
            dur += String.format("%02d:", hours);
        }
        dur += String.format("%02d:%02d", minutes, seconds);
        return dur;
    }
}
