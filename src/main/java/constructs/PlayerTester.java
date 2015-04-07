package constructs;

/**
 * Created by Jacob on 4/2/2015.
 */
public class PlayerTester extends Thread {

    private PlayerContainer player;
    private boolean testFin;

    public PlayerTester(PlayerContainer player, boolean fin) {
        this.player = player;
        testFin = fin;

    }

    public void run() {
        player.playSong();
        if (!testFin) {
            player.stopSong();
        }
    }

    public boolean isFinished() {
        return player.isFinished();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }
}
