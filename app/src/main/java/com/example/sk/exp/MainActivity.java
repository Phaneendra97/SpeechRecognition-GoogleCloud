package com.example.sk.exp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeRequest;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import org.apache.commons.codec.binary.Base64;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.io.ByteStreams.toByteArray;



public class MainActivity extends AppCompatActivity {
    private final String CLOUD_API_KEY = "AIzaSyCxTR4FIrH8WTef1xcKCYgx8mAsJ399bsk";
    String opt;//option for language
    Button button;// talk->record intent
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button browse = findViewById(R.id.browse_button);

       button =  findViewById(R.id.talk);

        button.setOnClickListener(view -> {

            // Start NewActivity.class
            Intent myIntent = new Intent(MainActivity.this,
                    AudRec.class);
            startActivity(myIntent);
        });

        MaterialSpinner spinner =  findViewById(R.id.spinner);
        spinner.setItems("en-IN", "kn-IN", "hi-IN", "te-IN", "ta-IN");
        spinner.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> opt= item);


        browse.setOnClickListener(v -> {
            Intent filePicker = new Intent(Intent.ACTION_GET_CONTENT);
            filePicker.setType("audio/*");// all audio formats
            MainActivity.this.startActivityForResult(filePicker, 1);

        });

   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onActivityResult(requestCode, resultCode, data);
        final Uri soundUri = data.getData();
     //   sampleRecognize(soundUri);
        if(resultCode == RESULT_OK) {
       //     final Uri soundUri = data.getData(); //audio data
            try (SpeechClient speechClient = SpeechClient.create()) {

            AsyncTask.execute(() -> {
                InputStream stream = null;
                try {
                    stream = getContentResolver() //stream of audio
                            .openInputStream(soundUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] audioData = new byte[0]; //byte format of audio
                try {
                    audioData = toByteArray(stream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String base64EncodedData =
                        Base64.encodeBase64String(audioData);

                MediaPlayer player = new MediaPlayer();
                try {
                    player.setDataSource(MainActivity.this, soundUri);

                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.start();

// Release the player


                RecognitionConfig.AudioEncoding encoding = RecognitionConfig.AudioEncoding.LINEAR16;
                RecognitionConfig config =
                        RecognitionConfig.newBuilder()
                                .setLanguageCode(opt)
                                .setSampleRateHertz(16000)
                                .setEncoding(encoding)
                                .build();
                ByteString content = ByteString.copyFrom(audioData);
                RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(content).build();
                RecognizeRequest request =
                        RecognizeRequest.newBuilder().setConfig(config).setAudio(audio).build();
                RecognizeResponse response = speechClient.recognize(request);
                // Create request
//                SyncRecognizeRequest request = new SyncRecognizeRequest();
//                request.setConfig(recognitionConfig);
//                request.setAudio(recognitionAudio);

// Generate response
//                SyncRecognizeResponse response = null;
//                try {
//                    response = speechService.speech()
//                            .syncrecognize(request)
//                            .execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


                for (SpeechRecognitionResult result : response.getResultsList()) {
                    // First alternative is the most probable result
                    SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                    String finalTranscript  = alternative.getTranscript();
                    runOnUiThread(() -> {
                        EditText speechToTextResult =
                                findViewById(R.id.speech_to_text_result);
                        speechToTextResult.setText(finalTranscript);
                    });
                    break;
                }


            });

            } catch (Exception exception) {
                System.err.println("Failed to create the client due to: " + exception);
            }
                }

}

    public  void sampleRecognize(Uri soundUri) {
        try (SpeechClient speechClient = SpeechClient.create()) {

            // The language of the supplied audio
            String languageCode = "en-US";

            // Sample rate in Hertz of the audio data sent
            int sampleRateHertz = 16000;

            // Encoding of audio data sent. This sample sets this explicitly.
            // This field is optional for FLAC and WAV audio formats.
            RecognitionConfig.AudioEncoding encoding = RecognitionConfig.AudioEncoding.LINEAR16;
            RecognitionConfig config =
                    RecognitionConfig.newBuilder()
                            .setLanguageCode(languageCode)
                            .setSampleRateHertz(sampleRateHertz)
                            .setEncoding(encoding)
                            .build();
            InputStream stream = null;
            try {
                stream = getContentResolver() //stream of audio
                        .openInputStream(soundUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] audioData = new byte[0]; //byte format of audio
            try {
                audioData = toByteArray(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteString content = ByteString.copyFrom(audioData);
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(content).build();
            RecognizeRequest request =
                    RecognizeRequest.newBuilder().setConfig(config).setAudio(audio).build();
            RecognizeResponse response = speechClient.recognize(request);
            for (SpeechRecognitionResult result : response.getResultsList()) {
                // First alternative is the most probable result
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcript: %s\n", alternative.getTranscript());
            }
        } catch (Exception exception) {
            System.err.println("Failed to create the client due to: " + exception);
        }
    }


        }