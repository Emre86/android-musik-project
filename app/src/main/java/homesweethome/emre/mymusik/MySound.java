package homesweethome.emre.mymusik;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MySound extends AppCompatActivity {

    private final static String TAG = "MySound";
    private ArrayList<Sound> listSound;
    // private ArrayList<String> listTitre;
    private ListView listView;
    //private ArrayAdapter<String> arrayAdapter;
    //private Sdcard sdcard;
    private MySoundAdapter mySoundAdapter;
    private MediaPlayer mediaPlayer = null;
    private HashMap<Integer, Sound> hashMap;
    private HashMap<Integer, Sound> hashMapShuffle;
    private TextView soundTitleView = null;
    private TextView soundTimeView = null;
    private int lecture = 0;
    private boolean isShuffle = false;
    private boolean isFollow = true;
    private boolean isRepatOne = false;
    private ArrayList<Integer> arrayList;

    private SoundTimeDuration soundTimeDuration;
    private String soundTimeInitDuration;


    private Toolbar toolbarBottom;

    private Menu menu;
    private MenuItem menuItemMyPlay;


    private final int BEGIN = 0;
    private final int PLAY = 1;
    private final int PAUSE = 2;
    private final int END = 3;

    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sound);


        listSound = getListSound();
        mySoundAdapter = new MySoundAdapter(this, listSound);
        listView = (ListView) findViewById(R.id.listv);

        listView.setAdapter(mySoundAdapter);

        hashMap = getHashMap(listSound);
        mediaPlayer = new MediaPlayer();


        arrayList = new ArrayList<Integer>();

        for (int i = 0; i < hashMap.size(); i++) {
            arrayList.add(i);
        }
        Collections.shuffle(arrayList);

        soundTimeDuration = new SoundTimeDuration();


        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.myplay:
                        switch (state){
                            case BEGIN:
                                Toast.makeText(getApplicationContext(), "Play ", Toast.LENGTH_SHORT).show();
                                //item.setIcon(R.drawable.mypause);
                                Log.d(TAG,""+hashMap.get(BEGIN).getTitre());
                                playMySound(hashMap.get(BEGIN));
                                item.setIcon(R.drawable.mypause);
                                break;
                            case END:
                                Toast.makeText(getApplicationContext(), "Play ", Toast.LENGTH_SHORT).show();
                                playMySound(hashMap.get(BEGIN));
                                item.setIcon(R.drawable.mypause);
                                break;
                            case PLAY:
                                state = PAUSE;
                                item.setIcon(R.drawable.myplay);
                                pauseSound();
                                //soundTimeDuration.stop();
                                break;
                            case PAUSE:
                                state = PLAY;
                                item.setIcon(R.drawable.mypause);
                                //soundTimeDuration.restart();
                                startSound();
                                break;
                            default:
                                break;
                        }
                        return true;
                    case R.id.myback:
                        Toast.makeText(getApplicationContext(), "back ", Toast.LENGTH_SHORT).show();
                        switch (state){
                            case END:
                                lecture = lecture - 1;
                                break;
                            case PLAY:
                                if (lecture != 0) {
                                    lecture = lecture - 1;
                                    if (isFollow) {
                                        playMySound(hashMap.get(lecture));
                                    }
                                    if (isShuffle) {
                                        playMySound(hashMap.get(arrayList.get(lecture)));
                                    }
                                }
                                break;
                            case PAUSE:
                                if (lecture != 0) {
                                    lecture = lecture - 1;
                                }
                                item.setIcon(R.drawable.mypause);
                                startSound();
                                break;
                            default:
                                break;
                        }
                        return true;
                    case R.id.mynext:
                        Toast.makeText(getApplicationContext(), "next ", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return true;
            }
        });
        // Inflate a menu to be displayed in the toolbar
        toolbarBottom.inflateMenu(R.menu.menu_toolbar);


        menu = toolbarBottom.getMenu();

        menuItemMyPlay = menu.findItem(R.id.myplay);




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), hashMap.get(i).getTitre(), Toast.LENGTH_SHORT).show();
                lecture = i;
                playMySound(hashMap.get(i));
                menuItemMyPlay.setIcon(R.drawable.mypause);
            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                menuItemMyPlay.setIcon(R.drawable.myplay);
                if (isRepatOne) {
                    soundTimeDuration.stop();
                    playMySound(hashMap.get(lecture));
                    menuItemMyPlay.setIcon(R.drawable.mypause);
                }
                if (isFollow) {
                    soundTimeDuration.stop();
                    lecture = lecture + 1;
                    if (lecture < hashMap.size()) {
                        playMySound(hashMap.get(lecture));
                        menuItemMyPlay.setIcon(R.drawable.mypause);
                    }
                }
                if (isShuffle) {
                    soundTimeDuration.stop();
                    lecture = lecture + 1;
                    if (lecture < hashMap.size()) {
                        playMySound(hashMap.get(arrayList.get(lecture)));
                        menuItemMyPlay.setIcon(R.drawable.mypause);
                    }
                }
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(this,"Bye Bye",Toast.LENGTH_SHORT).show();
        soundTimeDuration.stop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addPlaylist:
                getDialogueBoxAddPlaylist();
                return true;
            case R.id.removePlaylist:
                return true;
            case R.id.followings:
                Toast.makeText(this, "Following", Toast.LENGTH_SHORT).show();
                isFollow = true;
                isRepatOne = false;
                isShuffle = false;
                return true;
            case R.id.shuffle:
                Toast.makeText(this, "Shuffle", Toast.LENGTH_SHORT).show();
                isFollow = false;
                isRepatOne = false;
                isShuffle = true;
                return true;
            case R.id.repeat_ones:
                Toast.makeText(this, "Repeat one", Toast.LENGTH_SHORT).show();
                isFollow = false;
                isRepatOne = true;
                isShuffle = false;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void getDialogueBoxAddPlaylist() {
        AlertDialog.Builder alertPlaylist = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        alertPlaylist.setTitle("Ajout d'une playlist");
        alertPlaylist.setMessage("Entrez le nom de la nouvelle playlist");
        alertPlaylist.setView(editText);
        alertPlaylist.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String ed = editText.getText().toString();
                Toast.makeText(getApplicationContext(), "PLAYLIST: " + ed, Toast.LENGTH_SHORT).show();
            }
        });
        alertPlaylist.show();
    }



    public ArrayList<Sound> getListSound() {
        ArrayList<Sound> sounds = new ArrayList<>();
        Sound soundError = new Sound("Error Application", "00:00", -1);
        Sound soundNotFound = new Sound("Aucun fichier son trouvé", "00:00", -1);
        Sound sound;
        int titleColumn;
        int idColumn;
        long idSound = 0;
        String titleSound = "";
        String dureeSound = "";

        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            // query failed, handle error.
            sounds.add(soundError);
        } else if (!cursor.moveToFirst()) {
            sounds.add(soundNotFound);
        } else {
            titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);

            do {
                idSound = cursor.getLong(idColumn);
                if (idSound == -1) {
                    continue;
                }
                titleSound = cursor.getString(titleColumn);
                dureeSound = getDureeSound(idSound);

                if (!dureeSound.equals("00:00")) {
                    sound = new Sound(titleSound, dureeSound, idSound);
                    sounds.add(sound);
                }

                Log.i(TAG, "THIS ID is: " + idSound);
                Log.i(TAG, "THIS TITLE is: " + titleSound);

            }
            while (cursor.moveToNext());

        }
        return sounds;
    }


    public String getDureeSound(long idSound) {
        String duree;
        int millis;
        Uri musicUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, idSound);
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), musicUri);
        } catch (IOException io) {
            Log.e(TAG, "Erreur MediaPlayer.setDataSource with MediaPlayer");
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException io) {
            Log.e(TAG, "Erreur MediaPlayer.prepare with MediaPlayer");
        }
        millis = mediaPlayer.getDuration();
        if (millis == -1) {
            duree = "00:00";
        } else {
            duree = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        }
        mediaPlayer.reset();
        mediaPlayer = null;
        return duree;
    }

    public HashMap<Integer, Sound> getHashMap(ArrayList<Sound> listSound) {
        int hashmapSize = listSound.size();
        HashMap<Integer, Sound> hashmap = new HashMap<Integer, Sound>();

        for (int i = 0; i < hashmapSize; i++) {
            hashmap.put(i, listSound.get(i));
        }
        return hashmap;
    }



    public void pauseSound(){
        mediaPlayer.pause();
    }
    public void startSound(){
        mediaPlayer.start();
    }



    public void playMySound(Sound sound) {
        Log.i(TAG, "je suis dnas le play: ");
        long idSound = sound.getIdSound();
        if (idSound != -1) {
            Log.i(TAG, "je usi if ififififi");
            Uri musicUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, idSound);
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(getApplicationContext(), musicUri);
            } catch (IOException io) {
                Log.e(TAG, "Erreur MediaPlayer.setDataSource with MediaPlayer");
            }
            try {
                mediaPlayer.prepare();
            } catch (IOException io) {
                Log.e(TAG, "Erreur MediaPlayer.prepare with MediaPlayer");
            }

            soundTimeDuration.stop();
            soundTimeDuration = new SoundTimeDuration();


            soundTitleView = (TextView) findViewById(R.id.soundtitleview);
            soundTitleView.setText(sound.getTitre());
            soundTimeView = (TextView) findViewById(R.id.soundtimeview);
            soundTimeInitDuration = sound.getDuree();
            soundTimeView.setText(soundTimeInitDuration);
            state = PLAY;
            mediaPlayer.start();


            new Thread(soundTimeDuration).start();

        }
    }



    private class SoundTimeDuration implements Runnable {

        private AtomicBoolean stop;

        public SoundTimeDuration() {
            this.stop = new AtomicBoolean(true);
        }

        public void stop() {
            this.stop.set(false);
        }


        @Override
        public void run() {
            while (stop.get()) {
                if (state == PLAY){
                    try {
                        runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int mill = mediaPlayer.getCurrentPosition();
                            String duree = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(mill),
                                    TimeUnit.MILLISECONDS.toSeconds(mill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mill)));
                            soundTimeView.setText(duree);
                        }
                    });
                    Thread.sleep(811);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
                else{
                    try {
                        Thread.sleep(811);
                    }
                    catch(InterruptedException iee){
                        iee.printStackTrace();
                    }
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    soundTimeView.setText(soundTimeInitDuration);
                }
            });

        }
    }

}




