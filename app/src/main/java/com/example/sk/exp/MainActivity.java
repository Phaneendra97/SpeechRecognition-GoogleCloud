package com.example.sk.exp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.speech.v1beta1.Speech;
import com.google.api.services.speech.v1beta1.SpeechRequestInitializer;
import com.google.api.services.speech.v1beta1.model.RecognitionAudio;
import com.google.api.services.speech.v1beta1.model.RecognitionConfig;
import com.google.api.services.speech.v1beta1.model.SpeechRecognitionResult;
import com.google.api.services.speech.v1beta1.model.SyncRecognizeRequest;
import com.google.api.services.speech.v1beta1.model.SyncRecognizeResponse;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.io.ByteStreams.toByteArray;



public class MainActivity extends AppCompatActivity {
    private final String CLOUD_API_KEY = "API KEY HERE";
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
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            final Uri soundUri = data.getData(); //audio data

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
                player.setOnCompletionListener(
                        MediaPlayer::release);
                Speech speechService = new Speech.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null
                ).setSpeechRequestInitializer(
                        new SpeechRequestInitializer(CLOUD_API_KEY))
                        .build();
                RecognitionConfig recognitionConfig = new RecognitionConfig();
                recognitionConfig.setLanguageCode(opt);
                RecognitionAudio recognitionAudio = new RecognitionAudio();
                recognitionAudio.setContent(base64EncodedData);

                // Create request
                SyncRecognizeRequest request = new SyncRecognizeRequest();
                request.setConfig(recognitionConfig);
                request.setAudio(recognitionAudio);

// Generate response
                SyncRecognizeResponse response = null;
                try {
                    response = speechService.speech()
                            .syncrecognize(request)
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

// Extract transcript
                SpeechRecognitionResult result = response.getResults().get(0);
                final String transcript = result.getAlternatives().get(0)
                        .getTranscript();

                runOnUiThread(() -> {
                    EditText speechToTextResult =
                            findViewById(R.id.speech_to_text_result);
                    speechToTextResult.setText(transcript);
                });
            });


                }

}




        }