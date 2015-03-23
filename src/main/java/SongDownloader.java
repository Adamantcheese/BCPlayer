
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;

/**
 * Created by Jacob on 3/22/2015.
 */
public class SongDownloader extends Thread {

    private URL internalURL;
    private File internalFile;
    private boolean lock = false;

    public void run () {
        if(lock) {
            return;
        }
        try {
            FileUtils.copyURLToFile(internalURL, internalFile);
        } catch (IOException e) {
            System.err.println("An error occurred downloading the song from URL:\n" + internalURL.toString());
            return;
        }
        lock = true;
    }

    public SongDownloader (URL songURL, File songFile) {
        internalURL = songURL;
        internalFile = songFile;
        this.start();
    }
}
