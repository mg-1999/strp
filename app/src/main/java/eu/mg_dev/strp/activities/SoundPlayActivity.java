package eu.mg_dev.strp.activities;

import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import eu.mg_dev.strp.R;

public class SoundPlayActivity extends ActionBarActivity {
    private static String mFileName = null;
    private MediaPlayer mediaPlayer = null;
    private boolean ongoing = false;
    private boolean mStartPlaying = true;
    private static int duration;
    private static TextView durationTV;

    final Handler handler = new Handler();
    Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            updateTimePosition();
        }
    };

    public SoundPlayActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    public void onPlay(boolean start) {
        if (start) {
            startPlaying();
            ongoing = true;
        } else {
            ongoing = false;
            stopPlaying();
        }
    }

    public void startPlaying() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new Listener());
        mediaPlayer.prepareAsync();
    }

    public void stopPlaying() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void updateTimePosition() {
        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar2);
        TextView position = (TextView) findViewById(R.id.textView8);

        int current = mediaPlayer.getCurrentPosition();
        position.setText(getTimeString(current));

        int progress = ((duration - current)/duration)*100;
        bar.setProgress(progress);

        if (ongoing) {
            handler.postDelayed(updateTime, 1000);
        }
    }

    public static String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int minutes = (int) (millis % (1000*60*60)) / (1000*60);
        int seconds = (int) (( millis % (1000*60*60)) % (1000*60) ) / 1000;

        buf.append(String.format("%02d", minutes)).append(":").append(String.format("%02d", seconds));

        return buf.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_play);
        durationTV = (TextView) findViewById(R.id.textView9);

        final Button playButton = (Button) findViewById(R.id.playButton);

        View.OnClickListener recordClick = new View.OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    // STOP
                    playButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop_black_48dp, 0, 0, 0);
                    playButton.setText("Stop Recording");
                } else {
                    // START
                    playButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow_black_48dp, 0, 0, 0);
                }
                mStartPlaying = !mStartPlaying;
            }
        };
        playButton.setOnClickListener(recordClick);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sound_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setDuration(int in) {
        duration = in;
    }

    public static TextView getDurationTextView() {
        return durationTV;
    }

    private class Listener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            SoundPlayActivity.setDuration(mp.getDuration());
            SoundPlayActivity.getDurationTextView().setText(SoundPlayActivity.getTimeString(mp.getDuration()));
            updateTimePosition();
        }
    }
}
