package homesweethome.emre.mymusik;

/**
 * Created by emre on 04/11/16.
 */

public class SoundInList {

    private Sound sound = null;
    private int positionPlay = -1;


    public SoundInList(Sound sound,int positionPlay,int errorCode){
        this.sound = sound;
        this.positionPlay = positionPlay;
    }

}
