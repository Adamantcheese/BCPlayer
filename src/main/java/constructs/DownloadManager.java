package constructs;

import objects.Downloader;
import objects.Track;

import java.util.ArrayList;

/**
 * Created by Jacob on 5/24/2015.
 */
public class DownloadManager extends Thread {
    private ArrayList<Downloader> activeDownloads;

    public DownloadManager() {
        activeDownloads = new ArrayList<Downloader>();
    }

    public void download(Track track) {
        for(Downloader d : activeDownloads) {
            if(d.getDownloadTrack() == track) {
                return;
            }
        }

        Downloader newDownload = new Downloader(track);
        activeDownloads.add(newDownload);
        newDownload.start();
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<Downloader> done = new ArrayList<Downloader>();

            for(Downloader d : activeDownloads) {
                if(d.getFinished()) {
                    done.add(d);
                }
            }

            for(Downloader d : done) {
                activeDownloads.remove(d);
            }
        }
    }
}
