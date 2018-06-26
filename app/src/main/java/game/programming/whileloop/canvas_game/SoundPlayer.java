package game.programming.whileloop.canvas_game;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.pushpika.canvas_game.R;

/**
 * Created by tuanamith on 4/22/18.
 */

public class SoundPlayer extends ContextWrapper{

    MediaPlayer mediaPlayer;
    private Context context;

    public SoundPlayer(Context context) {
        super(context);
    }

    public void playBtn() {
        Log.v("soundplayer", "mediaplayer released");
        mediaPlayer = MediaPlayer.create(this, R.raw.glass);
        Log.v("soundplayer", "playing fav hits");
        mediaPlayer.start();
    }

}
