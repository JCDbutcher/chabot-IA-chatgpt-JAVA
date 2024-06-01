package com.chatIA.chatbotIA.screens;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.adapters.MessageAdapter;
import com.chatIA.chatbotIA.helpers.RequestManager;
import com.chatIA.chatbotIA.listener.IImageClick;
import com.chatIA.chatbotIA.listener.IImageResponse;
import com.chatIA.chatbotIA.models.Message;
import com.chatIA.chatbotIA.models.request.images.ImageRequest;
import com.chatIA.chatbotIA.models.response.images.ImageData;
import com.chatIA.chatbotIA.models.response.images.ImageResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * La actividad principal de la pantalla de chat de imágenes.
 */
public class ImageChatScreen extends AppCompatActivity {

    private TextView welcomeText;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ImageButton micButton;
    private ImageButton microphoneBtn;
    private ImageButton ibBack;
    private ImageButton ibMore;
    private List<Message> messages;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView rvImageChat;
    private MessageAdapter messageAdapter;
    private RequestManager requestManager;
    private Dialog mainDialog;
    private Dialog changeNameDialog;
    private TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_chat_screen);

        welcomeText = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
        micButton = findViewById(R.id.mic_btn);
        microphoneBtn = findViewById(R.id.microphone_icon);
        tvText = findViewById(R.id.tv_text);

        ibBack = findViewById(R.id.ib_back);
        ibMore = findViewById(R.id.ib_more);

        rvImageChat = findViewById(R.id.rv_image_chat);

        messages = new ArrayList<>();

        // Visibilidad del botón
        sendButton.setVisibility(View.GONE);

        // Configuración del RecyclerView
        messageAdapter = new MessageAdapter(this, messages, imageClick);
        rvImageChat.setAdapter(messageAdapter);
        rvImageChat.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvImageChat.setLayoutManager(linearLayoutManager);

        // Inicializar el request manager
        requestManager = new RequestManager(this);

        onSendButtonClick(sendButton);
        onBackButtonClick(ibBack);
        onChangeEditText(messageEditText);
        changeStatusBarColor();
        changeNavigationBarColor();
        onClickMoreButton(ibMore);
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
     * Configura el evento de clic para el botón de retroceso.
     *
     * @param button el botón de retroceso
     */
    private void onBackButtonClick(ImageButton button) {
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
     * Configura el evento de cambio de texto en el campo de edición de mensajes.
     *
     * @param editText el campo de edición de mensajes
     */
    private void onChangeEditText(EditText editText) {
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
            showMessage("It doesn't work yet :((");
        } else {
            showMessage("Where is your honor trash");
        }
    }

    /**
     * Muestra el diálogo para cambiar el nombre.
     */
    private void showChangeNameDialog() {
        String title = tvText.getText().toString().trim();

        changeNameDialog = new Dialog(this);
        changeNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changeNameDialog.setContentView(R.layout.bottom_sheet_change_name);

        EditText etName = changeNameDialog.findViewById(R.id.et_name);
        Button btnSave = changeNameDialog.findViewById(R.id.button_save);

        etName.setText(title);
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                tvText.setText(name);
                changeNameDialog.dismiss();
            } else {
                etName.setText(title);
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

    private final IImageResponse iImageResponse = new IImageResponse() {
        @Override
        public void didFetch(ImageResponse imageResponse, String msg) {
            if (imageResponse != null && imageResponse.data != null && !imageResponse.data.isEmpty()) {
                ImageData imageData = imageResponse.data.get(0);
                String imageUrl = imageData.url;
                Message messageObject = new Message(false, true, imageUrl);
                messages.add(messageObject);
                messageAdapter.notifyItemInserted(messages.size() - 1);
                rvImageChat.getRecycledViewPool().clear();
                rvImageChat.smoothScrollToPosition(messages.size() - 1);
            }
        }

        @Override
        public void didError(String msg) {
            showMessage("Error getting image" + msg);
        }
    };

    /**
     * Muestra un mensaje.
     *
     * @param msg el mensaje a mostrar
     */
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Envía un mensaje.
     */
    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        if (!message.isEmpty()) {
            Message messageObject = new Message(true, false, message);
            messages.add(messageObject);
            welcomeText.setVisibility(View.GONE);
            messageAdapter.notifyItemInserted(messages.size() - 1);
            rvImageChat.getRecycledViewPool().clear();
            rvImageChat.smoothScrollToPosition(messages.size() - 1);

            ImageRequest imageRequest = new ImageRequest();
            imageRequest.n = 1;
            imageRequest.prompt = message;
            imageRequest.size = "1024x1024";

            requestManager.generateImage(imageRequest, iImageResponse);

            messageEditText.setText("");
        }
    }

    private final IImageClick imageClick = index -> {
        Intent intent = new Intent(this, ImageScreen.class);
        intent.putExtra("imageUrl", index);
        startActivity(intent);
    };
}