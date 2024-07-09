//My code

//16 bit code


//FUCK OFF
package com.example.AudioDetective;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.AudioFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.Manifest;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import android.media.AudioTrack;


//import


//references
//https://gist.github.com/littlemove/467900/7d4484e1fb0661d7a54906a6d0d95332d3b55069
//https://stackoverflow.com/questions/19727109/how-to-exit-a-while-loop-after-a-certain-time
//https://developer.android.com/reference/android/media/AudioRecord#read(java.nio.ByteBuffer,%20int,%20int)
//https://github.com/wsieroci/audio-recognizer/tree/master/src/model
//https://dolby.io/blog/recording-audio-on-android-with-examples/
//https://gist.github.com/Venryx/e1f772b4c05b2da08e118ccd5cc162ff
//https://stackoverflow.com/questions/18276781/listbyte-to-single-byte
//https://gist.github.com/yavor87/b44c5096d211ce63c595?permalink_comment_id=3454547
//https://www.youtube.com/watch?v=v4gmd61SmIk
//https://developer.android.com/reference/java/nio/ByteOrder#BIG_ENDIAN
//https://stackoverflow.com/questions/37330443/little-and-big-endian-in-java-android
//https://www.novixys.com/blog/java-nio-using-bytebuffer/
//https://www.khanacademy.org/science/physics/mechanical-waves-and-sound/sound-topic/v/sound-properties-amplitude-period-frequency-wavelength
//https://stackoverflow.com/questions/2660232/convert-2-bytes-to-a-number
//https://stackoverflow.com/questions/4768933/read-two-bytes-into-an-integer
//https://stackoverflow.com/questions/21119846/amplitude-from-audiorecord?rq=3
//https://betterexplained.com/articles/understanding-big-and-little-endian-byte-order/
//https://docs.oracle.com/javase/8/docs/api/javax/sound/sampled/AudioFormat.Encoding.html
//https://forums.oracle.com/ords/apexds/post/algorithm-to-convert-byte-array-to-sample-array-9024
//https://stackoverflow.com/questions/31750160/get-unsigned-integer-from-byte-array-in-java?noredirect=1&lq=1
//https://stackoverflow.com/questions/4424881/in-java-i-want-to-convert-a-short-to-a-double





//implementation starts
public class MainActivity2 extends AppCompatActivity {


    //Global variables
    AudioRecord audioRecord;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    private static final int microphonePermissionCode = 200;
    // ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    short[] shortArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void StartListen(View v) {

        List<short[]> resultList = new ArrayList<>();
        List<Short> allShorts = new ArrayList<>();



        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC
                , SAMPLE_RATE
                , AudioFormat.CHANNEL_OUT_MONO
                , AUDIO_FORMAT
                , BUFFER_SIZE
                , AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackRate(SAMPLE_RATE);

        outStream.reset();
        //clear all data stored in output stream so we can go again

        //permission check

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, microphonePermissionCode);
        } else {
            //app only works if permissions are settled
            //changing the value of the text field once it has been clicked

            TextView textViewToChange = findViewById(R.id.textView2);
            textViewToChange.setText(R.string.listening);



            // new thread for continuous recording
            new Thread(() -> {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                // prioritising audio recording

                short[] buffer = new short[BUFFER_SIZE/2]; // take away /2




                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC
                        , SAMPLE_RATE
                        , AudioFormat.CHANNEL_IN_MONO
                        , AudioFormat.ENCODING_PCM_16BIT
                        , BUFFER_SIZE);

                // Start recording
                audioRecord.startRecording();

                // Record audio for 10 seconds
                long startTime = System.currentTimeMillis();
                while ((System.currentTimeMillis() - startTime) < 10000) {

                    int ShortsRead = audioRecord.read(buffer, 0, buffer.length);
                    // outStream.write(buffer, 0, ShortsRead);

                    Log.i("Live Buffer", Arrays.toString(buffer));
                    Log.i("shorts read", String.valueOf(ShortsRead));

                    short[] bufferCopy = Arrays.copyOf(buffer, buffer.length);
                    resultList.add(bufferCopy);

                    //ensures the main thread is used to update main ui
                    runOnUiThread(() -> {
                        ImageButton listenButton = findViewById(R.id.imageButton6);
                        listenButton.setEnabled(false); // enable the button
                    });
                }

                // Stop recording once loop 10 seconds is up
                audioRecord.stop();
                audioRecord.release();
                textViewToChange.setText(R.string.listen);




                //playing audio back to test if it was received correctly

                audioTrack.play();
                audioTrack.write(outStream.toByteArray(), 0, outStream.size());


                for (short[] array : resultList) {
                    Log.i("Buffer List", Arrays.toString(array));
                    //prints arrays

                    for(int i =0; i < array.length; i++){

                        allShorts.add(array[i]);
                    }
                    //prints out the 16-bit pairs of samples for amplitude
                    //data looks fine
                }


                Log.i("shorts arraylist size", String.valueOf(allShorts.size()));


                shortArray = new short[allShorts.size()];

                for(int i =0; i < allShorts.size(); i++){

                    shortArray [i] = allShorts.get(i);
                }

                Log.i("shorts array size", String.valueOf(shortArray.length));

                //every things seems fine up to here


                for(int i =0; i < shortArray.length; i++){


                    Log.i("every sample in short array", String.valueOf(shortArray[i]));
                }


                //how to print

                //byte[] byteArray = outStream.toByteArray();

                // again ensures the main thread is used to update main ui
                runOnUiThread(() -> {
                    ImageButton listenButton = findViewById(R.id.imageButton6);
                    listenButton.setEnabled(true); // enable the button
                });

                //native byte storage is in little endian
                if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                    Log.i("endian", "Little endian");
                } else {
                    Log.i("endian", "big endian");
                }



                double[] transformed = new double[shortArray.length];

                for (int i=0;i<transformed.length;i++) {
                    transformed[i] = (double)shortArray[i];
                    Log.i("double array", String.valueOf(transformed[i]));
                }

                short[] inputData = shortArray;

                // Find the minimum and maximum values
                short minValue = Short.MAX_VALUE;
                short maxValue = Short.MIN_VALUE;
                for (short value : inputData) {
                    minValue = (short) Math.min(minValue, value);
                    maxValue = (short) Math.max(maxValue, value);
                }

                // Calculate the scaling factor
                double range = (double)(maxValue - minValue);
                double scaleFactor = 2.0 / range;

                // Normalize the data
                double[] normalizedData = new double[inputData.length];
                for (int i = 0; i < inputData.length; i++) {
                    short x = (short)(inputData[i] - minValue);
                    normalizedData[i] = x * scaleFactor;
                }

                // Now normalizedData contains the normalized values within the range [-1.0, 1.0]



              //  public static short twoBytesToShort(byte b1, byte b2) {
             //       return (short) ((b1 << 8) | (b2 & 0xFF));
             //   }





                //byte[] byteArray = outStream.toByteArray();



                // for (byte array : byteArray) {
                //     Log.i("byte array values", String.valueOf(array));
                //prints out the 16-bit pairs of samples for amplitude
                //data looks fine
                // }

                //outStream drop all cpu
                //audio track drop all cpu
                // try {
                //     outStream.close();
                // } catch (IOException e) {
                //      throw new RuntimeException(e);
                // }
                // audioTrack.release();



                //Log.i("short array size", String.valueOf(byteArray.length));
                //printing the correct number of samples



                //we have to do this as we have picked to use 16-bit audio format which is clearer
                //if this does not work out do it with 8-bit so we can work with the data straight away
                //ArrayList<Integer> amplitude = new ArrayList<>();
                // in  byte array we have signed bytes and 2 bytes = 1 sample so
                //size should get halfed



                // Printing the array
                //for (int value : amplitude) {
                // Log.i("amplitude arraylist", String.valueOf(value));

                ///] }

                // Log.i("amplitude array size", String.valueOf(amplitude.size()));
                //size is fine




                // applying fft to around 5 secs of playtime
                double[] inputDatadouble = new double[262144];

                for (int i = 0; i < 262144; i++) {
                    inputDatadouble[i] = normalizedData[i];

                }

                //converts to an array of double

                UseFFT fftInstance = new UseFFT();
                //making it crash

                try {
                    fftInstance.computeFFT(inputDatadouble);
                } catch (Exception e) {
                    Log.i("exception", Objects.requireNonNull(e.getMessage()));
                    e.printStackTrace();

                }







            }).start();
        }
    }




}