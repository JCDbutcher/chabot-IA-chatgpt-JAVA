package com.chatIA.chatbotIA.screens;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.fragments.ImageFragment;

/**
 * La actividad principal de la pantalla de visualización de imágenes.
 */
public class ImageScreen extends AppCompatActivity {

    private final static int REQUEST_CODE = 1;
    private ImageButton ibBack;
    private ImageButton ibDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.image_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ibDownload = findViewById(R.id.ib_download);

        replaceFragment(new ImageFragment());
        onClickDownloadButton(ibDownload);

    }

    /**
     * Configura el evento de clic para el botón de descarga.
     *
     * @param button el botón de descarga
     */
    private void onClickDownloadButton(ImageButton button) {
        button.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(ImageScreen.this,
                            Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else if (ContextCompat.checkSelfPermission(ImageScreen.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                requestStoragePermission();
            }
        });
    }

    /**
     * Solicita permisos de almacenamiento.
     */
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(ImageScreen.this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ImageScreen.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                showMessage("Please provide the required permissions");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Guarda la imagen en el almacenamiento externo.
     */
    private void saveImage() {
        String imageUrl = getIntent().getStringExtra("imageUrl");
        if (imageUrl != null) {
            Uri imageUri = Uri.parse(imageUrl);
            String fileName = "Diana_" + System.currentTimeMillis() + ".jpg";
            DownloadManager.Request request = new DownloadManager.Request(imageUri)
                    .setTitle(fileName)
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);
        } else {
            showMessage("Could not get image URL");
        }
    }

    /**
     * Configura el evento de clic para el botón de retroceso.
     *
     * @param button el botón de retroceso
     */


    /**
     * Reemplaza el fragmento actual con un nuevo fragmento.
     *
     * @param fragment el nuevo fragmento a mostrar
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        String imageUrl = getIntent().getStringExtra("imageUrl");
        bundle.putString("imageUrl", imageUrl);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Muestra un mensaje.
     *
     * @param message el mensaje a mostrar
     */
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}