package com.chatIA.chatbotIA.screens;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.adapters.ReinforcedMessageAdapter;
import com.chatIA.chatbotIA.assistants.models.Conversation;
import com.chatIA.chatbotIA.assistants.models.request.MessageRequest;
import com.chatIA.chatbotIA.assistants.models.request.RunRequest;
import com.chatIA.chatbotIA.assistants.models.request.ThreadRequest;
import com.chatIA.chatbotIA.assistants.models.response.ListMessageResponse;
import com.chatIA.chatbotIA.assistants.models.response.MessageResponse;
import com.chatIA.chatbotIA.assistants.models.response.RunResponse;
import com.chatIA.chatbotIA.assistants.models.response.RunStatusResponse;
import com.chatIA.chatbotIA.assistants.models.response.ThreadResponse;
import com.chatIA.chatbotIA.helpers.RequestManager;
import com.chatIA.chatbotIA.listener.IListMessageResponse;
import com.chatIA.chatbotIA.listener.IMessageResponse;
import com.chatIA.chatbotIA.listener.IRunResponse;
import com.chatIA.chatbotIA.listener.IRunStatusResponse;
import com.chatIA.chatbotIA.listener.IThreadResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * La actividad principal de la pantalla de chat.
 */
public class ChatScreen extends AppCompatActivity {

    private TextView welcomeText;
    private TextView tvName;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ImageButton micButton;
    private ImageButton microphoneBtn;
    private ImageButton ibBack;
    private ImageButton ibMore;
    private RequestManager requestManager;
    private String idAssistant;
    private List<MessageRequest> messages;
    private RecyclerView rvTextChat;
    private ReinforcedMessageAdapter messageAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String idThread;
    private Dialog mainDialog;
    private Dialog changeNameDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private Conversation conversation;
    private boolean isFavorite = false;
    private boolean isScripted = false;
    private String script = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);

        welcomeText = findViewById(R.id.welcome_text);
        tvName = findViewById(R.id.tv_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
        micButton = findViewById(R.id.mic_btn);
        microphoneBtn = findViewById(R.id.microphone_icon);

        ibBack = findViewById(R.id.ib_back);
        ibMore = findViewById(R.id.ib_more);

        rvTextChat = findViewById(R.id.rv_chat);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        messages = new ArrayList<>();

        requestManager = new RequestManager(this);

        String origin = getIntent().getStringExtra("Origin");
        System.out.println("ORIGEN: " + origin);
        if (origin.equals("NewChat")) {
            idAssistant = "asst_frUYck2uPf2O7yjl2aqpRQl1";
            isScripted = false;
            createThread();
            System.out.println("Debe mostrarse la pantalla para un nuevo chat");
        } else if (origin.equals("NewChatScripted")) {
            script = getIntent().getStringExtra("Script");
            idAssistant = "asst_frUYck2uPf2O7yjl2aqpRQl1";
            isScripted = true;
            createThread();
            System.out.println("Debe mostrarse la pantalla para un nuevo chat con un string ya mandado");
        } else if (origin.equals("NewChatExplore")) {
            idAssistant = getIntent().getStringExtra("IdAssistant");
            createThread();
            System.out.println("Debe mostrarse la pantalla para un nuevo chat con un asistente escogido");
        } else if (origin.equals("ExistingChat")) {
            idThread = getIntent().getStringExtra("IdThread");
            System.out.println("Debe mostrarse el chat con la conversación ya lista para seguir");
            Toast.makeText(this, "ID OBTENIDO: " + idThread, Toast.LENGTH_SHORT).show();
        } else if (origin.equals("Assistant")) {
            idAssistant = getIntent().getStringExtra("IdAssistant");
            Toast.makeText(this, "ID OBTENIDO: " + idAssistant, Toast.LENGTH_SHORT).show();
            System.out.println("Debe mostrarse el chat con el asistente elegido");
        } else {
            Toast.makeText(this, "ID OBTENIDO EN EL RESTO: " + idAssistant, Toast.LENGTH_SHORT).show();
        }

        checkIfThreadExists();
        String title = tvName.getText().toString();
        conversation = new Conversation(idThread, currentUser.getUid(), messages, idAssistant, title);

        microphoneBtn.setOnClickListener(v -> sendVoiceToText());
        // Visibilidad del botón
        sendButton.setVisibility(View.GONE);

        // Configuración del RecyclerView
        messageAdapter = new ReinforcedMessageAdapter(this, messages, currentUser);
        rvTextChat.setAdapter(messageAdapter);
        rvTextChat.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvTextChat.setLayoutManager(linearLayoutManager);

        if(origin.equals("NewChatScripted")){
            try {
                Thread.sleep(1000);
                sendMessage();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        onClickBackButton(ibBack);
        onEditTextChange(messageEditText);
        changeStatusBarColor();
        changeNavigationBarColor();
        onSendButtonClick(sendButton);
        onClickMoreButton(ibMore);
        onClickMicButton(micButton);
    }

    //---------------------------- INICIO ONCLICK ----------------------------------//

    /**
     * Configura el evento de clic para el botón de retroceso.
     *
     * @param button el botón de retroceso
     */
    private void onClickBackButton(ImageButton button) {
        button.setOnClickListener(v -> finish());
    }

    /**
     * Configura el evento de clic para el botón "más".
     *
     * @param button el botón "más"
     */
    private void onClickMoreButton(ImageButton button) {
        button.setOnClickListener(v -> showDialog());
    }

    /**
     * Configura el evento de clic para el botón del micrófono.
     *
     * @param button el botón del micrófono
     */
    private void onClickMicButton(ImageButton button) {
        button.setOnClickListener(v -> {
            System.out.println(conversation.toString());
            Intent intent = new Intent(this, VoiceChatScreen.class);
            intent.putExtra("IdThread", conversation.getId());
            intent.putExtra("IdAssistant", conversation.getIdAssistant());
            startActivityForResult(intent, 20);
        });
    }

    /**
     * Muestra el diálogo principal.
     */
    private void showDialog() {
        mainDialog = new Dialog(this);
        mainDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mainDialog.setContentView(R.layout.bottom_sheet);

        LinearLayout option1 = mainDialog.findViewById(R.id.ll_option_1);
        LinearLayout option2 = mainDialog.findViewById(R.id.ll_option_2);
        LinearLayout option3 = mainDialog.findViewById(R.id.ll_option_3);
        LinearLayout option4 = mainDialog.findViewById(R.id.ll_option_4);

        option1.setOnClickListener(v -> onClickOption(option1));
        option2.setOnClickListener(v -> onClickOption(option2));
        option3.setOnClickListener(v -> onClickOption(option3));
        option4.setOnClickListener(v -> onClickOption(option4));

        mainDialog.setCancelable(true);
        mainDialog.show();
        mainDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mainDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_animation;
        mainDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     * Maneja el evento de clic en una opción del diálogo.
     *
     * @param linearLayout el layout de la opción clicada
     */
    private void onClickOption(LinearLayout linearLayout) {
        int id = linearLayout.getId();
        if (id == R.id.ll_option_1) {
            showMessage("It doesn't work yet :(");
        } else if (id == R.id.ll_option_2) {
            if (mainDialog != null && mainDialog.isShowing()) {
                mainDialog.dismiss();
            }
            showChangeNameDialog();
        } else if (id == R.id.ll_option_3) {
            onClickSaveConversation();
        } else {
            showMessage("Where is your honor trash");
        }
    }

    /**
     * Muestra el diálogo para cambiar el nombre.
     */
    private void showChangeNameDialog() {
        String title = tvName.getText().toString().trim();

        changeNameDialog = new Dialog(this);
        changeNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changeNameDialog.setContentView(R.layout.bottom_sheet_change_name);

        EditText etName = changeNameDialog.findViewById(R.id.et_name);
        Button btnSave = changeNameDialog.findViewById(R.id.button_save);

        etName.setText(title);
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                tvName.setText(name);
                conversation.setTitle(name);
                changeNameDialog.dismiss();
                saveConversation();
            } else {
                conversation.setTitle(title);
                etName.setText(title);
                saveConversation();
            }
        });

        changeNameDialog.setCancelable(true);
        changeNameDialog.show();
        changeNameDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        changeNameDialog.getWindow().getAttributes().windowAnimations = R.style.bottom_sheet_animation;
        changeNameDialog.getWindow().setGravity(Gravity.BOTTOM);
        changeNameDialog.setOnDismissListener(dialog -> {
            if (mainDialog != null && !mainDialog.isShowing()) {
                mainDialog.show();
            }
        });
    }


    /**
     * Configura el evento de cambio de texto en el campo de edición de mensajes.
     *
     * @param editText el campo de edición de mensajes
     */
    private void onEditTextChange(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementar
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No se necesita implementar
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    micButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                    microphoneBtn.setVisibility(View.VISIBLE);
                } else {
                    sendButton.setVisibility(View.VISIBLE);
                    micButton.setVisibility(View.GONE);
                    microphoneBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Cambia el color de la barra de estado.
     */
    private void changeStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.sky_blue));
        } else {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        }
    }

    /**
     * Cambia el color de la barra de navegación.
     */
    private void changeNavigationBarColor() {
        if (isDarkModeEnabled()) {
            setNavigationBarColor(ContextCompat.getColor(this, R.color.black_variant_1));
        } else {
            setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    /**
     * Establece el color de la barra de navegación.
     *
     * @param color el color a establecer
     */
    private void setNavigationBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(color);
        }
    }

    /**
     * Comprueba si el modo oscuro está habilitado.
     *
     * @return true si el modo oscuro está habilitado, false de lo contrario
     */
    private boolean isDarkModeEnabled() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Configura el evento de clic para el botón de envío.
     *
     * @param sendButton el botón de envío
     */
    private void onSendButtonClick(ImageButton sendButton) {
        sendButton.setOnClickListener(v -> sendMessage());
    }

    /**
     * Envía un mensaje.
     */
    private void sendMessage() {
        String question = "";
        if(!isScripted){
            question = messageEditText.getText().toString().trim();
        } else {
            question = script;
        }
        if (!question.isEmpty()) {

            MessageRequest messageRequest = new MessageRequest("user", question);
            messages.add(messageRequest);

            /*MessageRequest typingMessage = new MessageRequest(Message.TYPE_TYPING, "typing...");
            messages.add(typingMessage);*/

            welcomeText.setVisibility(View.GONE);
            messageAdapter.notifyItemInserted(messages.size() - 1);
            rvTextChat.getRecycledViewPool().clear();
            rvTextChat.smoothScrollToPosition(messages.size() - 1);
            requestManager.createMessage("assistants=v2", idThread, messageRequest, iMessageResponse);

            messageEditText.setText("");
            isScripted = false;
        }
    }

    /**
     * Muestra un mensaje.
     *
     * @param msg el mensaje a mostrar
     */
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private final IThreadResponse iThreadResponse = new IThreadResponse() {
        @Override
        public void didFetch(ThreadResponse threadResponse, String msg) {
            idThread = threadResponse.id;
            conversation.setId(idThread);
            Log.d("CHAT SCREEN", "Response Thread id: " + idThread);
            showMessage("Thread created with ID: " + idThread);
        }

        @Override
        public void didError(String msg) {
            showMessage("Error: " + msg);
        }
    };

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
            messageAdapter.notifyItemInserted(messages.size() - 1);
            rvTextChat.smoothScrollToPosition(messages.size() - 1);
            String titulo = tvName.getText().toString();
            conversation.setId(idThread);
            conversation.setMessages(messages);
            conversation.setTitle(titulo);

            saveConversation();

            Log.d("CHAT SCREEN", "Assistant message: " + message);
        }

        @Override
        public void didError(String msg) {
            showMessage("Error with list message: " + msg);
        }
    };

    private void sendVoiceToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tap to stop recording");

        try {
            startActivityForResult(intent, 10);
        } catch (Exception e) {
            showMessage(e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10: {
                if (resultCode == RESULT_OK && null != data) {
                    List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    messageEditText.setText(result.get(0));
                }
                break;
            }
            case 20: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> messages2 = data.getStringArrayListExtra("messages");
                    String sender = data.getStringExtra("sender");
                    int i = 1;
                    if (messages2 != null && !messages2.isEmpty()) {
                        for (String message : messages2) {
                            if (i % 2 != 0) {
                                sender = "user";
                            } else {
                                sender = "AI";
                            }
                            MessageRequest messageRequest = new MessageRequest(sender, message);
                            addMessage(messageRequest);
                            i++;
                        }
                        saveConversation();
                    }
                }
                break;
            }
        }
    }

    private void addMessage(MessageRequest message) {
        messages.add(message);
        welcomeText.setVisibility(View.GONE);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        rvTextChat.smoothScrollToPosition(messages.size() - 1);
        messageAdapter.notifyDataSetChanged();
    }

    private void saveConversation() {
        db.collection("conversation")
                .document()
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        db.collection("conversation")
                                .document(conversation.getId())
                                .update("messages", conversation.getMessages(), "title", conversation.getTitle())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("CHAT SCREEN", "ConversationThread updated in Firestore");
                                })
                                .addOnFailureListener(e -> Log.e("CHAT SCREEN", "Error updating ConversationThread in Firestore", e));
                    } else {
                        db.collection("conversation")
                                .document(conversation.getId())
                                .set(conversation)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("CHAT SCREEN", "ConversationThread added to Firestore");
                                })
                                .addOnFailureListener(e -> Log.e("CHAT SCREEN", "Error adding ConversationThread to Firestore", e));
                    }
                    System.out.println("EL TITULO ES: " + conversation.getTitle());
                })
                .addOnFailureListener(e -> Log.e("CHAT SCREEN", "Error getting ConversationThread from Firestore", e));
    }

    private void checkIfThreadExists() {
        db.collection("conversation")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        boolean threadExists = false;
                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                            if (queryDocumentSnapshots.getDocuments().get(i).getId().equals(idThread)) {
                                threadExists = true;
                                i = queryDocumentSnapshots.getDocuments().size() + 1;
                                loadMessages(idThread);
                            }
                        }
                        if (!threadExists) {
                            Log.e("ChatScreeen", "Error, thread doesn't exists");
                        }
                    } else {
                        createThread();
                    }
                }).addOnFailureListener(e -> Log.e("ChatScreen", "Error checking thread existence", e));
    }

    private void loadMessages(String threadId) {
        db.collection("conversation")
                .document(threadId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        conversation = documentSnapshot.toObject(Conversation.class);
                        idThread = conversation.getId();
                        idAssistant = conversation.getIdAssistant();
                        if (conversation != null) {
                            List<MessageRequest> messages = conversation.getMessages();
                            updateRecyclerView(messages);
                        }
                        tvName.setText(conversation.getTitle().toString());
                        welcomeText.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    showMessage("Error al cargar los mensajes: " + e.getMessage());
                });
    }

    private void updateRecyclerView(List<MessageRequest> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        messageAdapter.notifyDataSetChanged();
        rvTextChat.smoothScrollToPosition(messages.size() - 1);
    }

    private void createThread() {
        ThreadRequest threadRequest = new ThreadRequest();
        requestManager.createThread("assistants=v2", threadRequest, iThreadResponse);
    }

    private void onClickSaveConversation() {
        if (currentUser != null) {
            DocumentReference documentReference = db.collection("users")
                    .document(currentUser.getUid())
                    .collection("favorites")
                    .document(idThread);

            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        documentReference.delete()
                                .addOnSuccessListener(unused ->
                                        showMessage("Conversation removed"))
                                .addOnFailureListener(e ->
                                        showMessage("Error removing conversation"));
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", conversation.getId());
                        data.put("title", conversation.getTitle());
                        data.put("messages", conversation.getMessages());
                        data.put("userId", currentUser.getUid());
                        data.put("timestamp", Timestamp.now());

                        documentReference.set(data)
                                .addOnSuccessListener(unused ->
                                        showMessage("Conversation saved"))
                                .addOnFailureListener(e ->
                                        showMessage("Error saving conversation"));
                    }
                } else {
                    showMessage("Error checking saved status: " + task.getException()
                            .getMessage());
                }
            });

        }
    }

    private void checkIfFavorite() {
        Log.d("CHAT SCREEN", "THREAD ID: " + idThread);
        DocumentReference favoriteRef = db.collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .document(idThread);

        favoriteRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                isFavorite = task.getResult().exists();
                updateFavoriteButtonAppearance();
            } else {
                Log.e("RecipeScreen", "Error checking favorite status: " +
                        task.getException().getMessage());
            }
        });
    }

    private void updateFavoriteButtonAppearance() {
        /*if (isFavorite) {
            favoriteBtn.setImageResource(R.drawable.favorite_select_icon);
        } else {
            favoriteBtn.setImageResource(R.drawable.favorite_unselect_icon);
        }*/
    }
}