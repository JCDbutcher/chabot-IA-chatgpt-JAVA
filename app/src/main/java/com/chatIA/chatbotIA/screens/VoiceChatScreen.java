package com.chatIA.chatbotIA.screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.Timestamp;
import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.helpers.RequestManager;
import com.chatIA.chatbotIA.listener.IResponseBody;
import com.chatIA.chatbotIA.listener.ITextResponse;
import com.chatIA.chatbotIA.models.speech.SpeechRequest;
import com.chatIA.chatbotIA.models.speech.VoiceSettings;
import com.chatIA.chatbotIA.models.text.Choice;
import com.chatIA.chatbotIA.models.text.Message;
import com.chatIA.chatbotIA.models.text.TextRequest;
import com.chatIA.chatbotIA.models.text.TextResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Pantalla de chat de voz que utiliza reconocimiento de voz para escuchar y procesar entradas de usuario.
 */
public class VoiceChatScreen extends AppCompatActivity {

    private LottieAnimationView lavVoice;
    private LottieAnimationView lavVoiceLine;
    private SpeechRecognizer speechRecognizer;
    private StringBuilder conversationBuilder;
    private RequestManager requestManager;
    private ImageButton ib_pause;
    private ImageButton ib_play;
    private ImageButton ib_cancel;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.voice_chat_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ib_pause = findViewById(R.id.ib_pause);
        ib_cancel = findViewById(R.id.ib_cancel);
        ib_play = findViewById(R.id.ib_play);

        ib_play.setVisibility(View.GONE);

        conversationBuilder = new StringBuilder();

        requestManager = new RequestManager(this);

        requestAudioPermission();

        ib_play.setOnClickListener(v -> togglePause());
        ib_pause.setOnClickListener(v -> togglePause());
        ib_cancel.setOnClickListener(v -> cancelInteraction());

        lavVoice = findViewById(R.id.lav_voice);
        lavVoiceLine = findViewById(R.id.lav_voice_line);

        // Inicializa los permisos
        requestAudioPermission();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                lavVoice.playAnimation();
                lavVoiceLine.playAnimation();
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                lavVoice.cancelAnimation();
                lavVoiceLine.cancelAnimation();
            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                List<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults != null && !voiceResults.isEmpty()) {
                    String userSpeech = voiceResults.get(0);
                    conversationBuilder.append("User: ").append(userSpeech).append("\n");

                    // Agrega la respuesta de la IA a la lista
                    Responses.add(userSpeech);

                    // Devuelve el mensaje a ChatScreen
                    Intent resultIntent = new Intent();
                    resultIntent.putStringArrayListExtra("messages", new ArrayList<>(Responses));
                    resultIntent.putExtra("sender", "user");

                    sendMessageToAI(userSpeech);
                    setResult(RESULT_OK, resultIntent);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        speechRecognizer.startListening(intent);
    }

    /**
     * Solicita permisos de grabación de audio.
     */
    private void requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    10);
        }
    }

    private void togglePause() {
        if (isPaused) {
            ib_pause.setVisibility(View.VISIBLE);
            ib_play.setVisibility(View.GONE);
            isPaused = false;
            if (speechRecognizer != null) {
                speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
            }
        } else {
            ib_pause.setVisibility(View.GONE);
            ib_play.setVisibility(View.VISIBLE);
            isPaused = true;
            if (speechRecognizer != null) {
                speechRecognizer.stopListening();
            }
            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                mediaPlayer.release();
            }
        }
    }

    private void cancelInteraction() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start voice recognition when activity becomes visible
        if (speechRecognizer != null) {
            speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop speech recognition when the activity is no longer visible
        if (speechRecognizer != null) {
            speechRecognizer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private void sendMessageToAI(String userMessage) {
        Message userMessageObj = new Message(userMessage, "user", Timestamp.now());
        TextRequest textRequest = new TextRequest("gpt-3.5-turbo-0125", Collections.singletonList(userMessageObj));
        requestManager.sendMessage(textRequest, iTextResponse);
    }

    private List<String> Responses = new ArrayList<>();
    private final ITextResponse iTextResponse = new ITextResponse() {
        @Override
        public void didFetch(TextResponse text, String msg) {
            List<Choice> choices = text.getChoices();
            if (!choices.isEmpty()) {
                Message aiMessage = choices.get(0).getMessage();
                String aiResponse = aiMessage.getContent();
                conversationBuilder.append("AI: ").append(aiResponse).append("\n");

                generateVoice(aiResponse);

                //Agrega la respuesta de la IA a la lista
                Responses.add(aiResponse);

                // Devuelve el mensaje a ChatScreen
                Intent resultIntent = new Intent();
                resultIntent.putExtra("messages", new ArrayList<>(Responses));
                resultIntent.putExtra("sender", "AI");
                setResult(RESULT_OK, resultIntent);
            }
        }

        @Override
        public void didError(String msg) {
            showMessage("Error: " + msg);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListeningForUserInput();
            } else {
                showMessage("Audio recording permission denied");
            }
        }
    }

    /**
     * Muestra un mensaje en un Toast.
     *
     * @param msg el mensaje a mostrar
     */
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Inicia la escucha de la entrada del usuario.
     */
    private void startListeningForUserInput() {
        if (speechRecognizer != null) {
            speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
        }
    }

    private void generateVoice(String text) {
        if (text.isEmpty()) {
            return;
        }

        VoiceSettings voiceSettings = new VoiceSettings();
        voiceSettings.setStability(1);
        voiceSettings.setSimilarityBoost(1);

        SpeechRequest request = new SpeechRequest();
        request.setText(text);
        request.setVoiceSettings(voiceSettings);

        requestManager.generateSpeech(request, iResponseBody);
    }

    MediaPlayer mediaPlayer;
    private final IResponseBody iResponseBody = new IResponseBody() {
        @Override
        public void didFetch(ResponseBody responseBody, String msg) {
            File audioFile = writeResponseBodyToDisk(responseBody);
            if (audioFile != null) {
                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(audioFile.getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (speechRecognizer != null) {
                                speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
                            }
                            Toast.makeText(getApplicationContext(), "Holaa, se ha terminado yaaaa?", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showMessage("Error saving audio file");
            }
        }

        @Override
        public void didError(String msg) {
            showMessage("Error en la conexión a SevenLabs: " + msg);
        }
    };


    private File writeResponseBodyToDisk(ResponseBody body) {
        try {
            File audioFile = new File(getExternalFilesDir(null) + File.separator + "audioFile.mp3");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(audioFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }
                outputStream.flush();
                return audioFile;
            } catch (IOException e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }
}
