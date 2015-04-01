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
    private boolean stop;
    private int lastPosition;
    private PlaybackListener listener;

    public Player(InputStream stream) throws JavaLayerException {
        this.playing = false;
        this.stop = true;
        this.lastPosition = 0;

        this.bitstream = new Bitstream(stream);
        this.decoder = new Decoder();

        FactoryRegistry r = FactoryRegistry.systemRegistry();
        this.audio = r.createAudioDevice();
        this.audio.open(this.decoder);
    }

    public void play() throws JavaLayerException {
        this.play(Integer.MAX_VALUE);
    }

    public boolean play(int frames) throws JavaLayerException {
        boolean ret = true;
        this.playing = true;
        this.stop = false;

        while(frames-- > 0 && ret) {
            if (this.stop) {
                AudioDevice out = this.audio;
                if(out != null) {
                    out.flush();
                    synchronized(this) {
                        this.close();
                    }

                    if(this.listener != null) {
                        this.listener.playbackFinished(this.createEvent(out, PlaybackEvent.STOPPED));
                    }
                }
                return ret;
            } else if (this.playing) {
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
        this.stop = true;

        return ret;
    }

    public synchronized void close() {
        AudioDevice out = this.audio;
        if(out != null) {
            this.playing = false;
            this.stop = true;
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

    private PlaybackEvent createEvent(int id) {
        return this.createEvent(this.audio, id);
    }

    private PlaybackEvent createEvent(AudioDevice dev, int id) {
        return new PlaybackEvent(this, id, dev.getPosition());
    }

    public void setPlayBackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    public void pauseToggle() {
        if(this.playing) {
            this.listener.playbackPaused(this.createEvent(PlaybackEvent.PAUSED));
            this.playing = false;
        } else {
            this.listener.playbackUnpaused(this.createEvent(PlaybackEvent.UNPAUSED));
            this.playing = true;
        }
    }

    public void stop() {
        if(!this.playing) {
            this.playing = true;
        }
        this.stop = true;
    }
}