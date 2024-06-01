package com.chatIA.chatbotIA.screens;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.assistants.models.Tool;
import com.chatIA.chatbotIA.assistants.models.request.AssistantRequest;
import com.chatIA.chatbotIA.assistants.models.response.AssistantResponse;
import com.chatIA.chatbotIA.fragments.ChatFragment;
import com.chatIA.chatbotIA.fragments.HomeFragment;
import com.chatIA.chatbotIA.fragments.ProfileFragment;
import com.chatIA.chatbotIA.helpers.RequestManager;
import com.chatIA.chatbotIA.listener.IAssistantResponse;

import java.util.ArrayList;

/**
 * Actividad principal que maneja la navegación entre los fragmentos Home, Chat y Profile.
 */
public class MainScreen extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String currentFragmentTag;
    private RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom - navigationBarHeight);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        requestManager = new RequestManager(this);

        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString("current_fragment_tag");
            replaceFragment(getSupportFragmentManager().findFragmentByTag(currentFragmentTag), currentFragmentTag);
        } else {
            replaceFragment(new HomeFragment(), "home");
        }

        onClickItemBottomNavigation(bottomNavigationView);
    }

    /**
     * Configura el evento de clic para los elementos de la barra de navegación inferior.
     *
     * @param navigationView la vista de navegación inferior
     */
    private void onClickItemBottomNavigation(BottomNavigationView navigationView) {
        navigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment(), "home");
            } else if (item.getItemId() == R.id.new_chat) {
                replaceFragment(new ChatFragment(), "chat");
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment(), "profile");
            }
            return true;
        });
    }

    /**
     * Reemplaza el fragmento actual por uno nuevo.
     *
     * @param fragment el nuevo fragmento a mostrar
     * @param tag      la etiqueta del fragmento
     */
    private void replaceFragment(Fragment fragment, String tag) {
        currentFragmentTag = tag;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, tag);
        fragmentTransaction.commit();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            switch (tag) {
                case "home":
                case "profile":
                    if (isDarkTheme()) {
                        onChangeStatusBarColor(R.color.sky_blue);
                    } else {
                        onChangeStatusBarColor(R.color.blue);
                    }
                    break;
                case "chat":
                    if (isDarkTheme()) {
                        onChangeStatusBarColor(R.color.black_variant_1);
                    } else {
                        onChangeStatusBarColor(R.color.white);
                    }
                    break;
            }
        }, 100);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("current_fragment_tag", currentFragmentTag);
    }

    /**
     * Muestra un mensaje en un Toast.
     *
     * @param message el mensaje a mostrar
     */
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
        if (currentFragment instanceof ChatFragment || currentFragment instanceof ProfileFragment) {
            replaceFragment(new HomeFragment(), "home");
            bottomNavigationView.setSelectedItemId(R.id.home);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Verifica si el tema oscuro está habilitado.
     *
     * @return true si el tema oscuro está habilitado, false en caso contrario
     */
    private boolean isDarkTheme() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Cambia el color de la barra de estado.
     *
     * @param color el color a aplicar
     */
    private void onChangeStatusBarColor(int color) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, color));
    }

    /**
     * Crea un nuevo asistente con la configuración especificada.
     *
     * @param requestManager el administrador de solicitudes
     */
    private void createAssistant(RequestManager requestManager) {
        Tool tool = new Tool();
        tool.type = "code_interpreter";
        AssistantRequest assistantRequest = new AssistantRequest();
        assistantRequest.name = "Finance Tutor";
        assistantRequest.instructions = "You are a personal finance tutor. When asked a question, " +
                "write and run Python code to answer the question.";
        assistantRequest.tools = new ArrayList<>();
        assistantRequest.tools.add(tool);
        assistantRequest.model = "gpt-3.5-turbo";
        assistantRequest.description = "Second Assistant";

        requestManager.createAssistant("assistants=v2", assistantRequest, iAssistantResponse);
    }

    private final IAssistantResponse iAssistantResponse = new IAssistantResponse() {
        @Override
        public void didFetch(AssistantResponse assistantResponse, String msg) {
            showMessage("Assistant created with ID: " + assistantResponse.id);
        }

        @Override
        public void didError(String msg) {
            showMessage("Error " + msg);
        }
    };
}
