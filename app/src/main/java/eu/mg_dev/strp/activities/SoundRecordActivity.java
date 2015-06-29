package eu.mg_dev.strp.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import eu.mg_dev.strp.R;

public class SoundRecordActivity extends ActionBarActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private long startTime;
    private MediaRecorder mRecorder = null;

    // private PlayButton mPlayButton = null;
    private MediaPlayer mPlayer = null;
    private boolean mStartRecording = true;

    final Handler handler = new Handler();
    Runnable update_runnable = new Runnable() {
        public void run() {
            updateRecordTime();
        }
    };

    public SoundRecordActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
            startTime = System.currentTimeMillis();
            updateRecordTime();
        } else {
            stopRecording();
            startTime = Long.parseLong(null);
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    /*
    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_record);

        final Button recordButton = (Button) findViewById(R.id.recordButton);

        View.OnClickListener recordClick = new View.OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    // STOP
                    recordButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop_black_48dp, 0, 0, 0);
                } else {
                    // START
                    recordButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mic_black_48dp, 0, 0, 0);
                }
                mStartRecording = !mStartRecording;
            }
        };
        recordButton.setOnClickListener(recordClick);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sound_record, menu);
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

    public void updateRecordTime() {
        long duration = (int) ((System.currentTimeMillis() - startTime) / 1000);
        handler.postDelayed(update_runnable, 1000);

        ((TextView) findViewById(R.id.textView7)).setText("" + duration);
    }
}
