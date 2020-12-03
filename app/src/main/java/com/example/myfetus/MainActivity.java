package com.example.myfetus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.gauravk.audiovisualizer.visualizer.BlobVisualizer;
import java.io.IOException;
import java.util.Date;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity{
    private ImageButton recordbtn, stopbtn, playbtn, stopplay, pausebtn;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    BlastVisualizer mVisualizer;
    CircleLineVisualizer clVisualizer;
    BlobVisualizer blVisualizer;
    private static final String LOG_TAG = "AudioRecording";
    private static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private int buttonState = 0;
    private EditText editsenha1;
    private TextView test1;
    private ImageView editImagem;
    private int i=0;
    Date createdTime = new Date();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordbtn = (ImageButton)findViewById(R.id.record);
        stopbtn = (ImageButton)findViewById(R.id.stopRecord);
        playbtn = (ImageButton)findViewById(R.id.play);
        stopplay = (ImageButton)findViewById(R.id.stopplay);
        pausebtn = (ImageButton)findViewById(R.id.pause);
        clVisualizer = findViewById(R.id.circleLine);
        //CircleVisualizer circleVisualizer = findViewById(R.id.visualizer);
        //audioRecordView = findViewById(R.id.audioRecordView);
        stopbtn.setEnabled(false);
        playbtn.setEnabled(false);
        stopplay.setEnabled(false);

        mFileName = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "MyFetus" +createdTime + "Audio.m4a").replaceAll(" ", "_").replaceAll(":", "-");
        test1 = findViewById(R.id.texto2);
         editImagem = findViewById(R.id.imageView4);

        recordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (CheckPermissions()) {
                        stopbtn.setEnabled(true);
                        recordbtn.setEnabled(false);
                        playbtn.setEnabled(false);
                        stopplay.setEnabled(false);
                        pausebtn.setEnabled(false);
                        mRecorder = new MediaRecorder();
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mRecorder.setAudioEncodingBitRate(128000);
                        mRecorder.setAudioSamplingRate(44100);
                        mRecorder.setOutputFile(mFileName);
                        try {
                            mRecorder.prepare();

                        } catch (IOException e) {
                            Log.e(LOG_TAG, "prepare() failed");
                        }
                        mRecorder.start();
                        //Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
                        test1.setText("Gravando...");
                        test1.setTextColor(Color.parseColor("#0af263"));
                    } else {
                        RequestPermissions();
                    }
                    recordbtn.setVisibility(View.INVISIBLE);
                    stopbtn.setVisibility(View.VISIBLE);
                }


        });
        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopbtn.setEnabled(false);
                recordbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(true);
                pausebtn.setEnabled(true);
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                //Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
                test1.setText("Aperte para gravar");
                test1.setTextColor(Color.parseColor("#FADCEE"));
                stopbtn.setVisibility(View.INVISIBLE);
                recordbtn.setVisibility(View.VISIBLE);
            }
        });
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopbtn.setEnabled(false);
                recordbtn.setEnabled(true);
                playbtn.setEnabled(false);
                stopplay.setEnabled(true);
                pausebtn.setEnabled(true);
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(mFileName);
                    mPlayer.prepare();
                    mPlayer.start();
                    int audioSessionId = mPlayer.getAudioSessionId();
                    if (audioSessionId != -1)
                        clVisualizer.show();
                    clVisualizer.setAudioSessionId(audioSessionId);
                    //Toast.makeText(getApplicationContext(), "Recording Started Playing", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });
        pausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayer!=null){
                mPlayer.pause();
                stopbtn.setEnabled(true);
                recordbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(true);
                pausebtn.setEnabled(false);
                Toast.makeText(getApplicationContext(),"Audio Paused", Toast.LENGTH_SHORT).show();}
            }
        });
        stopplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.release();
                if (clVisualizer != null) {
                    clVisualizer.release();
                    clVisualizer.hide();
                }
                mPlayer = null;
                stopbtn.setEnabled(false);
                recordbtn.setEnabled(true);
                playbtn.setEnabled(true);
                stopplay.setEnabled(false);
                pausebtn.setEnabled(true);
                Toast.makeText(getApplicationContext(),"Audio Stopped", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length> 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
}