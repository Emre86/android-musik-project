package homesweethome.emre.mymusik;

import android.media.MediaPlayer;
import android.widget.SeekBar;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by emre on 16/11/16.
 */
public class MediaObserver implements Runnable {

    private final SeekBar progress;
    private MediaPlayer mediaPlayer;
    private AtomicBoolean stop;

    public MediaObserver(SeekBar progress, MediaPlayer mediaPlayer){
        this.mediaPlayer = mediaPlayer ;
        this.progress = progress;
        this.stop =  new AtomicBoolean(true);
    }

    public void stop(){
        this.stop.set(false);
    }

    @Override
    public void run() {
        while(stop.get()){
            progress.setProgress(mediaPlayer.getCurrentPosition());
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }
}
