package com.chatIA.chatbotIA.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chatIA.chatbotIA.R;

/**
 * Pantalla de bienvenida que muestra un botón para ingresar a la aplicación.
 */
public class WelcomeScreen extends AppCompatActivity {

    private Button buttonEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.welcome_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonEnter = findViewById(R.id.button_enter);

        onClickButtonEnter(buttonEnter);
    }

    /**
     * Configura el evento de clic para el botón de entrada.
     *
     * @param button el botón de entrada
     */
    private void onClickButtonEnter(Button button) {
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            finish();
        });
    }

}
