package com.chatIA.chatbotIA.screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.util.Log;
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
import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.assistants.models.request.MessageRequest;
import com.chatIA.chatbotIA.assistants.models.request.RunRequest;
import com.chatIA.chatbotIA.assistants.models.response.ListMessageResponse;
import com.chatIA.chatbotIA.assistants.models.response.MessageResponse;
import com.chatIA.chatbotIA.assistants.models.response.RunResponse;
import com.chatIA.chatbotIA.assistants.models.response.RunStatusResponse;
import com.chatIA.chatbotIA.helpers.RequestManager;
import com.chatIA.chatbotIA.listener.IListMessageResponse;
import com.chatIA.chatbotIA.listener.IMessageResponse;
import com.chatIA.chatbotIA.listener.IResponseBody;
import com.chatIA.chatbotIA.listener.IRunResponse;
import com.chatIA.chatbotIA.listener.IRunStatusResponse;
import com.chatIA.chatbotIA.models.speech.SpeechRequest;
import com.chatIA.chatbotIA.models.speech.VoiceSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private String idAssistant;
    private List<MessageRequest> messages;
    private String idThread;

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

        idAssistant = getIntent().getStringExtra("IdAssistant");
        idThread = getIntent().getStringExtra("IdThread");
        messages = new ArrayList<>();


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
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
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
        MessageRequest messageRequest = new MessageRequest("user", userMessage);
        messages.add(messageRequest);

        requestManager.createMessage("assistants=v2", idThread, messageRequest, iMessageResponse);
    }

    private final IMessageResponse iMessageResponse = new IMessageResponse() {
        @Override
        public void didFetch(MessageResponse messageResponse, String msg) {
            showMessage("Message added to thread");
            RunRequest runRequest = new RunRequest(idAssistant);
            requestManager.createRun("assistants=v2", idThread, runRequest, iRunResponse);
        }

        @Override
        public void didError(String msg) {
            showMessage("Error creating message: " + msg);
        }
    };

    private final IRunResponse iRunResponse = new IRunResponse() {
        @Override
        public void didFetch(RunResponse runResponse, String msg) {
            requestManager.getRunStatus("assistants=v2", idThread, runResponse.id, iRunStateResponse);
        }

        @Override
        public void didError(String msg) {
            showMessage("Error with run: " + msg);
        }
    };

    private final IRunStatusResponse iRunStateResponse = new IRunStatusResponse() {
        @Override
        public void didFetch(RunStatusResponse runResponse, String msg) {
            if ("completed".equals(runResponse.status)) {
                requestManager.getListMessage("assistants=v2", idThread, iListMessageResponse);
            } else {
                new Handler().postDelayed(() -> requestManager.getRunStatus(
                        "assistants=v2", idThread, runResponse.id, this), 500);
                showMessage(runResponse.status);
                Log.d("CHAT SCREEN", "Run status: " + runResponse.last_error);
            }
        }

        @Override
        public void didError(String msg) {
            showMessage("Error with run status: " + msg);
        }
    };

    private final IListMessageResponse iListMessageResponse = new IListMessageResponse() {
        @Override
        public void didFetch(ListMessageResponse listMessageResponse, String msg) {
            List<MessageResponse> message = listMessageResponse.data.stream()
                    .filter(assistantMessage -> assistantMessage.role.equals("assistant"))
                    .collect(Collectors.toList());
            MessageRequest assistantMessageRequest = new MessageRequest("assistant", message.get(0).content.get(0).text.value);
            messages.add(assistantMessageRequest);

            String aiResponse = assistantMessageRequest.content;
            conversationBuilder.append("AI: ").append(aiResponse).append("\n");

            generateVoice(aiResponse);

            //Agrega la respuesta de la IA a la lista
            Responses.add(aiResponse);

            // Devuelve el mensaje a ChatScreen
            Intent resultIntent = new Intent();
            resultIntent.putExtra("messages", new ArrayList<>(Responses));
            resultIntent.putExtra("sender", "AI");
            setResult(RESULT_OK, resultIntent);


            Log.d("CHAT SCREEN", "Assistant message: " + message);
        }

        @Override
        public void didError(String msg) {
            showMessage("Error with list message: " + msg);
        }
    };

    private List<String> Responses = new ArrayList<>();
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
