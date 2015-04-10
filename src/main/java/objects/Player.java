package objects;

import javazoom.jl.decoder.*;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

import java.io.InputStream;

public class Player {
    
    private Bitstream bitstream;
    private Decoder decoder;
    private AudioDevice audio;

    private boolean closed;
    private boolean playing;
    private boolean stop;

    private PlaybackListener listener;

    public Player(InputStream stream) throws JavaLayerException {
        closed = false;
        playing = false;
        stop = false;

        bitstream = new Bitstream(stream);
        decoder = new Decoder();

        FactoryRegistry r = FactoryRegistry.systemRegistry();
        audio = r.createAudioDevice();
        audio.open(decoder);
    }

    private boolean play(int frames) throws JavaLayerException {
        boolean ret = true;
        playing = true;

        while(frames-- > 0 && ret) {
            if (stop) {
                playing = false;
                return ret;
            } else if(playing) {
                ret = decodeFrame();
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return ret;
                }
            }
        }

        AudioDevice out = audio;
        if(out != null) {
            out.flush();
            synchronized(this) {
                close();
            }

            if(listener != null) {
                listener.playbackFinished(createEvent(out, PlaybackEvent.FINISHED));
                listener.playbackStopped(createEvent(out, PlaybackEvent.STOPPED));
            }
        }

        return ret;
    }

    public synchronized void close() {
        AudioDevice out = audio;
        if(out != null) {
            closed = true;
            audio = null;
            out.close();

            try {
                bitstream.close();
            } catch (BitstreamException var3) {

            }
        }
    }

    public int getPosition() {
        int position = 0;
        if(audio != null) {
            position = audio.getPosition();
        }

        return position;
    }

    private boolean decodeFrame() throws JavaLayerException {
        try {
            AudioDevice ex = audio;
            if(ex == null) {
                return false;
            } else {
                Header h = bitstream.readFrame();
                if(h == null) {
                    return false;
                } else {
                    SampleBuffer output = (SampleBuffer)decoder.decodeFrame(h, bitstream);
                    synchronized(this) {
                        ex = audio;
                        if(ex != null) {
                            ex.write(output.getBuffer(), 0, output.getBufferLength());
                        }
                    }

                    bitstream.closeFrame();
                    return true;
                }
            }
        } catch (RuntimeException var7) {
            throw new JavaLayerException("Exception decoding audio frame", var7);
        }
    }

    private PlaybackEvent createEvent(int id) {
        return createEvent(audio, id);
    }

    private PlaybackEvent createEvent(AudioDevice dev, int id) {
        return new PlaybackEvent(this, id, dev.getPosition());
    }

    public void setPlayBackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    public void stopSong() {
        if(!playing) {
            playing = true;
        }
        stop = true;
        playing = false;
        listener.playbackStopped(createEvent(PlaybackEvent.STOPPED));
        close();
    }

    public void playSong() {
        try {
            play(Integer.MAX_VALUE);
        } catch (JavaLayerException e) {
            listener.playbackFinished(createEvent(PlaybackEvent.FINISHED));
            stopSong();
        }
    }

    public void pauseToggle() {
        if(playing) {
            playing = false;
            listener.playbackPaused(createEvent(PlaybackEvent.PAUSED));
        } else {
            playing = true;
            listener.playbackUnpaused(createEvent(PlaybackEvent.UNPAUSED));
        }
    }

    public boolean isPlaying() {
        return playing && !closed;
    }

    public boolean isPaused() {
        return !playing && !closed;
    }

    public boolean isFinished() {
        return closed;
    }
}