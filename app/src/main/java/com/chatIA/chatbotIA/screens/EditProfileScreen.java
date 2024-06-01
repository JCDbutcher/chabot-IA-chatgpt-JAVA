package com.chatIA.chatbotIA.screens;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.chatIA.chatbotIA.R;
//import com.ren.dianav2.database_SQLITE.ImageDatabaseManager;
import com.chatIA.chatbotIA.listener.ICameraImagePermissionHandler;
import com.chatIA.chatbotIA.listener.IGalleryPermissionHandler;
import com.squareup.picasso.Picasso;

/**
 * Pantalla de edición de perfil donde el usuario puede tomar o subir una foto de perfil.
 */
public class EditProfileScreen extends AppCompatActivity {

    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
    };
    private static final String TAG = "Permission";

    private Button takePhotoButton;
    private Button uploadPhotoButton;
    private ImageButton backButton;
    private boolean isStorageImagePermitted = false;
    private boolean isCameraPermitted = false;
    private Uri uri;
    private ImageView ivProfile;
    //private ImageDatabaseManager miManager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private StorageReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_screen);

        takePhotoButton = findViewById(R.id.button_take_photo);
        uploadPhotoButton = findViewById(R.id.button_upload_photo);
        ivProfile = findViewById(R.id.iv_profile);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        reference = FirebaseStorage.getInstance().getReference("user_images");

        /*
        miManager = new ImageDatabaseManager(this);
        miManager.open();

        // Load the saved URI if it exists
        String savedUriString = miManager.getImageUri();
        if (savedUriString != null) {
            uri = Uri.parse(savedUriString);
            ivProfile.setImageURI(uri);
        }


*/
        String profile = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
        if(profile!=null){
            Picasso.get().load(profile).into(ivProfile);
        }
        onClickTakePhoto(takePhotoButton);
        onClickUploadPhoto(uploadPhotoButton);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void onClickTakePhoto(Button button) {
        button.setOnClickListener(v ->
                cameraImagePermissionHandler.requestStorageImageAndCameraPermission());
    }

    private void onClickUploadPhoto(Button button) {
        button.setOnClickListener(v -> galleryPermissionHandler.requestGalleryPermission());
    }

    private final ICameraImagePermissionHandler cameraImagePermissionHandler = () -> {
        if (isCameraPermitted) {
            openCamera();
        } else {
            requestPermissionsCamera();
        }
    };

    private final IGalleryPermissionHandler galleryPermissionHandler = () -> {
        if (!isStorageImagePermitted) {
            requestStorageImagePermission();
        }
        if (isCameraPermitted) {
            openGallery();
        }
    };

    public void openCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "New Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Captured by User name");
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        launcherCamera.launch(intent);
    }

    public void requestStorageImagePermission() {
        if (ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[0]) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, REQUIRED_PERMISSIONS[0] + "Granted");
            isStorageImagePermitted = true;
            requestPermissionsCamera();
        } else {
            requestPermissionLauncherStorageImages.launch(REQUIRED_PERMISSIONS[0]);
        }
    }

    private void requestPermissionsCamera() {
        if (ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[1]) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, REQUIRED_PERMISSIONS[1] + "Granted");
            isCameraPermitted = true;
        } else {
            requestPermissionLauncherCamera.launch(REQUIRED_PERMISSIONS[1]);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncherStorageImages =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            Log.d(TAG, REQUIRED_PERMISSIONS[0] + "Granted");
                            isStorageImagePermitted = true;
                        } else {
                            Log.d(TAG, REQUIRED_PERMISSIONS[0] + "Denied");
                            isStorageImagePermitted = false;
                        }
                        requestPermissionsCamera();
                    });

    private final ActivityResultLauncher<String> requestPermissionLauncherCamera =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            Log.d(TAG, REQUIRED_PERMISSIONS[1] + "Granted");
                            isCameraPermitted = true;
                        } else {
                            Log.d(TAG, REQUIRED_PERMISSIONS[1] + "Denied");
                            isCameraPermitted = false;
                        }
                    });

    private final ActivityResultLauncher<Intent> launcherCamera =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    o -> {
                        if (o.getResultCode() == RESULT_OK) {
                            ivProfile.setImageURI(uri);
                            saveUserImage();
                            //saveUriToDatabase(uri);
                        }
                    });

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            uri = data.getData();
                            if (uri != null) {
                                ivProfile.setImageURI(uri);
                                saveUserImage();
                                //saveUriToDatabase(uri);
                            }
                        }
                    }
                }
            });

   /* private void saveUriToDatabase(Uri uri) {
        miManager.saveImageUri(uri.toString());
    }*/
    private void saveUserImage(){
        if (uri != null) {
            StorageReference storageReference = reference.child(mAuth.getCurrentUser().getUid() +
                    "." + getFileExtension(uri));

            storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    currentUser = mAuth.getCurrentUser();

                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri).build();

                    currentUser.updateProfile(request).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            finish();
                        } else {
                            showMessage("Error with the user image");
                        }
                    });
                });
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        return typeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
