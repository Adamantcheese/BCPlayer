package objects;

public class PlaybackEvent {

    public static int PAUSED = 1;
    public static int UNPAUSED = 2;
    public static int FINISHED = 3;
    public static int STOPPED = 4;

    private Player source;
    private int frame;
    private int id;

    public PlaybackEvent(Player source, int id, int frame) {
        this.id = id;
        this.source = source;
        this.frame = frame;
    }

    public int getId() {
        return this.id;
    }

    public int getFrame() {
        return this.frame;
    }

    public Player getSource() {
        return this.source;
    }
}
