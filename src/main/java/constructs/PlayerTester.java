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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        player.pauseToggle();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        player.pauseToggle();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!testFin) {
            player.stopSong();
        }
    }
}
