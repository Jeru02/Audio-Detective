

package com.example.AudioDetective;

//imports
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;

import com.acrcloud.rec.*;
import com.acrcloud.rec.ACRCloudClient;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.ByteOrder;



   //implementation starts
public class MainActivity extends AppCompatActivity implements IACRCloudListener {

    private ACRCloudClient mClient;
    private ACRCloudConfig mConfig;



    //-----------------------------------------------
    //audio recording stuff

    AudioRecord audioRecord;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    private static final int microphonePermissionCode = 200;

    //byte[] byteArray;
    // byte[] buffer = new byte[BUFFER_SIZE];

    private String iresult;




    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //attaching xml layout

        this.mConfig = new ACRCloudConfig();
        this.mConfig.acrcloudListener = this;
        this.mConfig.context = this;

        this.mConfig.host = "identify-eu-west-1.acrcloud.com";
        this.mConfig.accessKey = "4638ff92f8510990f4d893041f7add44";
        this.mConfig.accessSecret = "L510l3bIIgWPdu0O7z1LCtRWSnuGgugtBjC14q7e";
        //  this.mConfig.protocol = ACRCloudConfig.NetworkProtocol.HTTP; // PROTOCOL_HTTPS
        this.mConfig.recMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;

        this.mConfig.recorderConfig.isVolumeCallback = true;

        //pre recording not needed
        this.mConfig.recorderConfig.reservedRecordBufferMS = 0;

        this.mConfig.recorderConfig.rate = 44100;

        this.mConfig.recorderConfig.channels = 1;



        this.mClient = new ACRCloudClient();

        this.mClient.initWithConfig(this.mConfig);




        //initiates the client for audio detection


    }


    public void StartListen(View v) throws IOException {




        File musicDirectory = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        // Gaining access to the device's music directory

        //File filePath = new File(musicDirectory, "test" + ".mp3");
        //String path = filePath.getPath();
        //File file = new File(path);

        File file = new File(musicDirectory, "test.mp3");
        //creating a file to write to




        ImageButton listenButton = findViewById(R.id.imageButton6);
        RotateAnimation rotate = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF,
                .5f, RotateAnimation.RELATIVE_TO_SELF, .5f);
        rotate.setDuration(14500);
        listenButton.startAnimation(rotate);







        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, microphonePermissionCode);
        } else {
            //app only works if permissions are settled
            //changing the value of the text field once it has been clicked

            TextView textViewToChange = findViewById(R.id.textView2);
            textViewToChange.setText(R.string.listening);




            //setting the audio recorder parameters

            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC
                    , SAMPLE_RATE
                    , AudioFormat.CHANNEL_IN_MONO
                    , AudioFormat.ENCODING_PCM_16BIT
                    , BUFFER_SIZE);

            // Start recording
            audioRecord.startRecording();

           // AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC
            //        , SAMPLE_RATE
            //        , AudioFormat.CHANNEL_OUT_MONO
             //       , AUDIO_FORMAT
             //       , BUFFER_SIZE
             //       , AudioTrack.MODE_STREAM);

            //audioTrack.setPlaybackRate(SAMPLE_RATE);
            //audio track to test that the audio has come in correctly

            //new thread for background processing

            new Thread(() -> {
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                // prioritising audio recording



                // Record audio for 10 seconds
                long startTime = System.currentTimeMillis();
                while ((System.currentTimeMillis() - startTime) < 10000) {





                    byte[] buffer = new byte[BUFFER_SIZE];

                    audioRecord.read(buffer, 0, buffer.length);






                    try {
                        BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(file, true));
                        buf.write(buffer, 0, buffer.length);
                        buf.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }



                    //ensures the main thread is used to update main ui
                    runOnUiThread(() -> {
                        ImageButton listenButton2 = findViewById(R.id.imageButton6);
                        listenButton2.setEnabled(false); // enable the button
                    });

                }



                File file2 = new File(file.getPath());
                int size = (int) file2.length();
                byte[] bytes = new byte[size];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file2));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }



                iresult = this.mClient.recognize(bytes, bytes.length,44100, 1 );


                ACRCloudResult CloudResult = new ACRCloudResult();
                CloudResult.setResult(iresult);
                onResult(CloudResult);




                audioRecord.stop();
                audioRecord.release();



                //native byte storage is in little endian
                if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                    Log.i("endian", "Little endian");
                } else {
                    Log.i("endian", "big endian");
                }



              //  audioTrack.play();
               // audioTrack.write(outStream.toByteArray()
               //         , 0, outStream.size());


            }).start();


        }



    }


    @Override
    public void onResult(ACRCloudResult results) {

        //https://github.com/acrcloud/ACRCloudUniversalSDK

        String result = results.getResult();
        Log.i("resultttt", result);
        String tres = "\n";

        try {
            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            int j2 = j1.getInt("code");
            if(j2 == 0){
                //if music is found ...

                JSONObject metadata = j.getJSONObject("metadata");
                //
                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");

                        JSONObject music = (JSONObject) musics.get(0);
                        String title = music.getString("title");
                        JSONArray artistt = music.getJSONArray("artists");
                        JSONObject art = (JSONObject) artistt.get(0);
                        String artist = art.getString("name");

                    Intent intent = new Intent(MainActivity.this, answerActivity3.class);
                    intent.putExtra("artist",artist);
                    intent.putExtra("title",title);
                    startActivity(intent);





                }else{

                    JSONArray customFiles = metadata.getJSONArray("custom_files");
                    JSONObject file = customFiles.getJSONObject(0);
                    String title = file.getString("title");
                    String artists = file.getString("artists");

                    Intent intent = new Intent(MainActivity.this, answerActivity3.class);
                    intent.putExtra("artist",artists);
                    intent.putExtra("title",title);
                    startActivity(intent);








                }

                tres = tres + "\n\n" ;



            }else{
                //send us to no result activity

                Intent intent = new Intent(MainActivity.this, no_answer4.class);
                                   startActivity(intent);



                tres = result;
            }
        } catch (JSONException e) {
            tres = result;
            e.printStackTrace();

        }

        this.iresult = " ";

    }



    @Override
    public void onVolumeChanged(double v) {

    }


}

