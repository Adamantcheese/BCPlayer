package objects;

import boot.Constants;
import com.mpatric.mp3agic.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Random;

/**
 * Created by Jacob on 5/22/2015.
 */
public class Downloader extends Thread {

    private Track downloadTrack;
    private boolean finished;

    public Downloader(Track track) {
        downloadTrack = track;
        finished = false;
    }

    public void run() {
        //Setup the space for the MP3 file
        String songFileDir = System.getProperty("user.home") + "\\Downloads\\BCPlayer Downloads\\";
        String songFileName = downloadTrack.getArtist().replace(':', '-') + " - " + downloadTrack.getTrackName().replace(':', '-') + ".mp3";
        File songTempFile = new File(Constants.BASE_TEMP_DIR + songFileName);
        File songFileDirectory = new File(songFileDir);
        if(!songFileDirectory.exists()) {
            songFileDirectory.mkdirs();
        }

        Random r = new Random();
        File artFile = new File(Constants.BASE_TEMP_DIR + "temp" + r.nextInt(Integer.MAX_VALUE) + ".jpg");

        try {
            //Download the track
            FileUtils.copyURLToFile(downloadTrack.getTrackURL(), songTempFile);

            //Download the artwork
            FileUtils.copyURLToFile(downloadTrack.getArtURL(), artFile);

            //Add the tag data from the track as well, including the cover art and stuff
            Mp3File track = new Mp3File(songTempFile);

            ID3v2 id3v2Tag;
            if (track.hasId3v2Tag()) {
                id3v2Tag =  track.getId3v2Tag();
            } else {
                id3v2Tag = new ID3v24Tag();
                track.setId3v2Tag(id3v2Tag);
            }

            RandomAccessFile art = new RandomAccessFile(artFile, "r");
            byte[] image = new byte[(int) art.length()];
            art.read(image);
            art.close();
            id3v2Tag.setAlbumImage(image, "image/jpeg");
            id3v2Tag.setArtist(downloadTrack.getArtist());
            id3v2Tag.setTitle(downloadTrack.getTrackName());

            File newFile = new File(songFileDir + songFileName);
            newFile.createNewFile();

            track.save(songFileDir + songFileName);
        } catch (Exception e) {
            System.out.println("Error: Unable to download track: " + downloadTrack.getPageURL());
        } finally {
            songTempFile.delete();
            artFile.delete();
            finished = true;
        }
    }

    public Track getDownloadTrack() {
        return downloadTrack;
    }

    public boolean getFinished() {
        return finished;
    }
}
