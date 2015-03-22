import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Jacob on 3/22/2015.
 */
public class SongDownloader extends Thread {

    private URL internalURL;
    private File internalFile;
    private boolean downloading;

    public void run() {
        try {
            FileUtils.copyURLToFile(internalURL, internalFile);
        } catch (IOException e) {
            System.err.println("An error occurred downloading the song from URL:\n" + internalURL.toString());
            return;
        }
    }

    public SongDownloader(URL songURL, File songFile) {
        internalURL = songURL;
        internalFile = songFile;
        downloading = true;
        this.start();
        downloading = false;
    }
}
