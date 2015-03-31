package constructs;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import objects.Track;

import java.net.URL;

/**
 * Created by Jacob on 3/31/2015.
 */
public class PlayerContainer extends Thread {

    private Player player;

    public PlayerContainer(Track track) throws Exception {
        player = new Player(track.getTrackURL().openStream());
    }

    public PlayerContainer(URL test) throws Exception {
        player = new Player(test.openStream());
    }

    public void run(){
        try {
            player.play();
        } catch (JavaLayerException e) {
            return;
        }
    }
}
