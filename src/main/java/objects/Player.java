package objects;

import java.io.InputStream;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

public class Player {
    private Bitstream bitstream;
    private Decoder decoder;
    private AudioDevice audio;
    private boolean playing;
    private int lastPosition;
    private PlaybackListener listener;

    public Player(InputStream stream) throws JavaLayerException {
        this.playing = false;
        this.lastPosition = 0;

        this.bitstream = new Bitstream(stream);
        this.decoder = new Decoder();

        FactoryRegistry r = FactoryRegistry.systemRegistry();
        this.audio = r.createAudioDevice();
        this.audio.open(this.decoder);
    }

    public boolean play(int frames) throws JavaLayerException {
        boolean ret = true;
        this.playing = true;

        while(frames-- > 0 && ret) {
            if (this.playing) {
                ret = decodeFrame();
            } else {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {

                }
            }
        }

        AudioDevice out = this.audio;
        if(out != null) {
            out.flush();
            synchronized(this) {
                this.close();
            }

            if(this.listener != null) {
                this.listener.playbackFinished(this.createEvent(out, PlaybackEvent.FINISHED));
            }
        }

        this.playing = false;

        return ret;
    }

    public synchronized void close() {
        AudioDevice out = this.audio;
        if(out != null) {
            this.playing = false;
            this.audio = null;
            out.close();
            this.lastPosition = out.getPosition();

            try {
                this.bitstream.close();
            } catch (BitstreamException var3) {

            }
        }

    }

    public int getPosition() {
        int position = this.lastPosition;
        AudioDevice out = this.audio;
        if(out != null) {
            position = out.getPosition();
        }

        return position;
    }

    protected boolean decodeFrame() throws JavaLayerException {
        try {
            AudioDevice ex = this.audio;
            if(ex == null) {
                return false;
            } else {
                Header h = this.bitstream.readFrame();
                if(h == null) {
                    return false;
                } else {
                    SampleBuffer output = (SampleBuffer)this.decoder.decodeFrame(h, this.bitstream);
                    synchronized(this) {
                        ex = this.audio;
                        if(ex != null) {
                            ex.write(output.getBuffer(), 0, output.getBufferLength());
                        }
                    }

                    this.bitstream.closeFrame();
                    return true;
                }
            }
        } catch (RuntimeException var7) {
            throw new JavaLayerException("Exception decoding audio frame", var7);
        }
    }

    protected boolean skipFrame() throws JavaLayerException {
        Header h = this.bitstream.readFrame();
        if(h == null) {
            return false;
        } else {
            this.bitstream.closeFrame();
            return true;
        }
    }

    public boolean play(int start, int end) throws JavaLayerException {
        boolean ret = true;

        for(int offset = start; offset-- > 0 && ret; ret = this.skipFrame()) {

        }

        return this.play(end - start);
    }

    private PlaybackEvent createEvent(int id) {
        return this.createEvent(this.audio, id);
    }

    private PlaybackEvent createEvent(AudioDevice dev, int id) {
        return new PlaybackEvent(this, id, dev.getPosition());
    }

    public void setPlayBackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    public void pause() {
        if(this.playing) {
            this.listener.playbackPaused(this.createEvent(PlaybackEvent.PAUSED));
            this.playing = false;
        } else {
            this.listener.playbackUnpaused(this.createEvent(PlaybackEvent.UNPAUSED));
            this.playing = true;
        }
    }
}