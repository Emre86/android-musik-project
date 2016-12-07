package homesweethome.emre.mymusik;

/**
 * Created by emre on 01/10/16.
 */
public class Sound {

    //private String format ;
    private String titre ;
    private String duree ;
    private long idSound ;

    Sound(String titre, String duree, long idSound){
        this.titre = titre;
        this.duree = duree;
        this.idSound = idSound;
    }

    /*public String getFormat(){
        return this.format;
    }*/

    public String getTitre(){
        return this.titre;
    }

    public String getDuree(){
        return this.duree;
    }

    public long getIdSound(){
        return this.idSound;
    }


}


