package com.chatIA.chatbotIA.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.adapters.ProfileOptionAdapter;
import com.chatIA.chatbotIA.helpers.LanguageHelper;
import com.chatIA.chatbotIA.listener.IThemeHandler;
import com.chatIA.chatbotIA.models.OptionItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Un {@link Fragment} simple que muestra las opciones del perfil del usuario.
 * Metodo de construcción {@link ProfileFragment#newInstance} para crear una instancia de este fragmento.
 */
public class ProfileFragment extends Fragment implements IThemeHandler {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private ProfileOptionAdapter adapter;
    private List<OptionItem> optionItems;
    private ImageView ivProfile;
    private TextView tvUsername;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    //private ImageDatabaseManager miManager;

    public ProfileFragment() {
        // Constructor público requerido
    }

    /**
     * Metodo de construcción para crear una nueva instancia de
     * este fragmento utilizando los parámetros proporcionados.
     *
     * @param param1 Parámetro 1.
     * @param param2 Parámetro 2.
     * @return Una nueva instancia del fragmento ProfileFragment.
     */
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el diseño para este fragmento
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView = view.findViewById(R.id.rv_option);
        ivProfile = view.findViewById(R.id.iv_profile);
        tvUsername = view.findViewById(R.id.tv_user_name);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        addDataToList();

        adapter = new ProfileOptionAdapter(getContext(), optionItems, this);
        recyclerView.setAdapter(adapter);

        /* Inicializa el ImageDatabaseManager y abre la base de datos
        miManager = new ImageDatabaseManager(getContext());
        miManager.open();*/

        // Cargar la imagen guardada de la base de datos


        if (currentUser != null) {
            String username = currentUser.getDisplayName();
            String [] arrayPocho = username.split(" ");
            if(arrayPocho[1]==null){
                arrayPocho[1]="";
            }
            String nombrePocho=arrayPocho[0]+" "+arrayPocho[1];
            String profile = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
            tvUsername.setText(nombrePocho);
            if (profile != null) {
                Picasso.get().load(profile).into(ivProfile);
            }
        }
        //loadSavedProfileImage();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /* Cierra la base de datos cuando el fragmento se destruya
        if (miManager != null) {
            miManager.close();
        }*/
    }

    /**
     * Carga la imagen de perfil guardada desde la base de datos.
     */
    /*private void loadSavedProfileImage() {
        String savedUriString = miManager.getImageUri();
        if (savedUriString != null) {
            Uri savedUri = Uri.parse(savedUriString);
            ivProfile.setImageURI(savedUri);
        }
    }
*/
    /**
     * Agrega datos a la lista de opciones del perfil.
     */
    private void addDataToList() {
        optionItems = new ArrayList<>();
        optionItems.add(new OptionItem(getString(R.string.edit_photo), R.drawable.round_arrow_forward_ios_24));
        optionItems.add(new OptionItem(getString(R.string.font_size), R.drawable.round_arrow_forward_ios_24));
        optionItems.add(new OptionItem(getString(R.string.theme), R.drawable.round_arrow_forward_ios_24));
        optionItems.add(new OptionItem(getString(R.string.notification), R.drawable.round_arrow_forward_ios_24));
        optionItems.add(new OptionItem(getString(R.string.language_change), R.drawable.round_arrow_forward_ios_24)); // Aquí está la nueva opción
        optionItems.add(new OptionItem(getString(R.string.privacy), R.drawable.round_arrow_forward_ios_24));
        optionItems.add(new OptionItem(getString(R.string.add_account), R.drawable.round_arrow_forward_ios_24));
        optionItems.add(new OptionItem(getString(R.string.logout), R.drawable.round_arrow_forward_ios_24));
    }

    /**
     * Muestra un cuadro de diálogo para seleccionar el tema de la aplicación.
     */
    @Override
    public void chooseTheme() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.select_theme))
                .setItems(new CharSequence[]{
                        getString(R.string.system),
                        getString(R.string.light_mode),
                        getString(R.string.dark_mode)
                }, (dialog, which) -> {
                    switch (which) {
                        case 0: // System
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            break;
                        case 1: // Light Mode
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            break;
                        case 2: // Dark Mode
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            break;
                    }
                    getActivity().recreate();
                    dialog.dismiss();
                });
        builder.create().show();
    }

    /**
     * Muestra un cuadro de diálogo para seleccionar el idioma de la aplicación.
     */
    private void chooseLanguage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.select_language))
                .setItems(new CharSequence[]{
                        getString(R.string.language_english),
                        getString(R.string.language_spanish)
                }, (dialog, which) -> {
                    switch (which) {
                        case 0: // English
                            LanguageHelper.changeLanguage(getContext(), "en");
                            break;
                        case 1: // Spanish
                            LanguageHelper.changeLanguage(getContext(), "es");
                            break;
                    }
                    getActivity().recreate();
                    dialog.dismiss();
                });
        builder.create().show();
    }

    // Llama a chooseLanguage() cuando el usuario selecciona la opción de cambio de idioma.
    private void onLanguageOptionSelected() {
        chooseLanguage();
    }
}
