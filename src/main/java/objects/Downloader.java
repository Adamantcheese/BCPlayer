package objects;

import boot.Constants;
import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;

import java.io.File;
import java.util.Random;

/**
 * Created by Jacob on 5/22/2015.
 */
public class Downloader extends Thread {

    private Track downloadTrack;

    public Downloader(Track track) {
        downloadTrack = track;
    }

    public void run() {
        //Setup the space for the MP3 file
        String songFileName = System.getProperty("user.home") + "\\Downloads\\BCPlayer Downloads\\" + downloadTrack.getArtist() + " - " + downloadTrack.getTrackName() + ".mp3";
        File songFile = new File(songFileName);
        Random r = new Random();

        try {
            //Download the track
            FileUtils.copyURLToFile(downloadTrack.getTrackURL(), songFile);

            //Download the artwork
            File tempArt = new File(Constants.BASE_TEMP_DIR + "temp" + r.nextInt(Integer.MAX_VALUE) + ".jpg");
            FileUtils.copyURLToFile(downloadTrack.getArtURL(), tempArt);

            //Add the tag data from the track as well, including the cover art and stuff
            AudioFile songMP3 = AudioFileIO.read(songFile);
            Tag songTag = songMP3.getTag();

            songTag.setField(FieldKey.ARTIST, downloadTrack.getArtist());
            songTag.setField(FieldKey.TITLE, downloadTrack.getTrackName());
            songTag.setField(Artwork.createArtworkFromFile(tempArt));

            songMP3.commit();

            tempArt.delete();
        } catch (Exception e) {
            System.err.println("Error: Unable to download track: " + downloadTrack.getTrackURL());
            songFile.delete();
        }
    }
}
