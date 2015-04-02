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
    private int lastPosition;

    private PlaybackListener listener;

    public Player(InputStream stream) throws JavaLayerException {
        closed = false;
        playing = false;
        lastPosition = 0;

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
            if(playing) {
                ret = decodeFrame();
            } else {
                return true;
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
                listener.playbackFinished(createEvent(out, PlaybackEvent.STOPPED));
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
            lastPosition = out.getPosition();

            try {
                bitstream.close();
            } catch (BitstreamException var3) {

            }
        }
    }

    /*protected boolean skipFrame() throws JavaLayerException {
        Header h = bitstream.readFrame();
        if(h == null) {
            return false;
        } else {
            bitstream.closeFrame();
            return true;
        }
    }

    public boolean play(int start, int end) throws JavaLayerException {
        boolean ret = true;

        for(int offset = start; offset-- > 0 && ret; ret = skipFrame()) {

        }

        return play(end - start);
    }*/

    public int getPosition() {
        int position = lastPosition;
        AudioDevice out = audio;
        if(out != null) {
            position = out.getPosition();
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

    private void stop() {
        listener.playbackFinished(createEvent(PlaybackEvent.STOPPED));
        close();
    }

    public void playSong() {
        try {
            play(Integer.MAX_VALUE);
        } catch (JavaLayerException e) {
            listener.playbackFinished(createEvent(PlaybackEvent.FINISHED));
        }
    }

    public void pauseToggle() {
        if(playing) {
            playing = false;
            listener.playbackFinished(createEvent(PlaybackEvent.PAUSED));
        } else {
            listener.playbackFinished(createEvent(PlaybackEvent.UNPAUSED));
            playSong();
        }
    }

    public void stopSong() {
        stop();
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isPaused() {
        return !playing;
    }

    public boolean isFinished() {
        return closed;
    }
}