package com.chatIA.chatbotIA.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.adapters.RecentChatAdapter;
import com.chatIA.chatbotIA.adapters.SavedChatAdapter;
import com.chatIA.chatbotIA.assistants.models.Conversation;
//import com.ren.dianav2.database.ImageDatabaseManager;
import com.chatIA.chatbotIA.listener.IChatClickListener;
import com.chatIA.chatbotIA.models.ChatItem;
import com.chatIA.chatbotIA.screens.ChatScreen;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Un {@link Fragment} simple que muestra una lista de chats recientes y guardados.
 * Utiliza el método de construccion {@link ChatFragment#newInstance} para crear una instancia de este fragmento.
 */
public class ChatFragment extends Fragment {

    // parámetros de inicialización del fragmento, p. ej., ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Renombrar y cambiar tipos de parámetros
    private String mParam1;
    private String mParam2;
    //private ImageDatabaseManager miManager;
    private RecyclerView recyclerViewChat;
    private RecentChatAdapter recentChatAdapter;
    private RecyclerView recyclerViewSaved;
    private SavedChatAdapter savedChatAdapter;
    private List<ChatItem> chatItems;
    private List<ChatItem> savedChatItems;
    private Button btnOption1;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private ImageView ivProfile;

    public ChatFragment() {
        // Constructor público requerido
    }

    /**
     * Utiliza este método de construccion para crear una nueva instancia de
     * este fragmento utilizando los parámetros proporcionados.
     *
     * @param param1 Parámetro 1.
     * @param param2 Parámetro 2.
     * @return Una nueva instancia del fragmento ChatFragment.
     */
    // TODO: Renombrar y cambiar tipos y número de parámetros
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // carga el diseño para este fragmento
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerViewChat = view.findViewById(R.id.rv_recent);
        recyclerViewSaved = view.findViewById(R.id.rv_saved);
        btnOption1 = view.findViewById(R.id.btn_option1);
        ivProfile = view.findViewById(R.id.iv_profile);

        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        /*recyclerViewSaved.setHasFixedSize(true);
        recyclerViewSaved.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));*/

        loadRecentConversation();
        loadSavedConversation();
        setButtonListeners(view);

        String profile = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
        if(profile!=null){
            Picasso.get().load(profile).into(ivProfile);
        }
        //addDataToList();

        //recentChatAdapter = new RecentChatAdapter(getContext(), chatItems);
        //recyclerViewChat.setAdapter(recentChatAdapter);

        /*
        miManager = new ImageDatabaseManager(this.getContext());
        miManager.open();
        //savedChatItems = new ArrayList<>();
        /*savedChatAdapter = new SavedChatAdapter(getContext(), savedChatItems);
        recyclerViewSaved.setAdapter(savedChatAdapter);

        if (miManager.getImageUri() != null) {
            loadSavedConversationProfileImage();
        } else {
            if (currentUser != null) {
                String profile = currentUser.getPhotoUrl().toString();
                Picasso.get().load(profile).into(ivProfile);
            }
        }*/
        return view;
    }

    /**
     * Carga la imagen de perfil guardada desde la base de datos.
     *//*
    private void loadSavedConversationProfileImage() {
        String savedUriString = miManager.getImageUri();
        if (savedUriString != null) {
            Uri savedUri = Uri.parse(savedUriString);
            ivProfile.setImageURI(savedUri);
        }
    }*/

    /**
     * Agrega datos a las listas de chats recientes y guardados.
     */
    private void addDataToList() {
        chatItems = new ArrayList<>();
        savedChatItems = new ArrayList<>();

        chatItems.add(new ChatItem(R.drawable.round_chat_24, getString(R.string.java_code_explanation)));
        chatItems.add(new ChatItem(R.drawable.round_chat_24, getString(R.string.math_exercises_resolution)));
        chatItems.add(new ChatItem(R.drawable.round_chat_24, getString(R.string.use_your_brain_example)));

        savedChatItems.add(new ChatItem(R.drawable.save_icon, getString(R.string.java_code_explanation)));
        savedChatItems.add(new ChatItem(R.drawable.save_icon, getString(R.string.math_exercises_resolution)));
        savedChatItems.add(new ChatItem(R.drawable.save_icon, getString(R.string.use_your_brain_example)));
    }

    /**
     * Configura los listeners de los botones.
     *
     * @param view la vista del fragmento
     */
    private void setButtonListeners(View view) {
        Button button1 = view.findViewById(R.id.btn_option1);
        Button button2 = view.findViewById(R.id.btn_option2);

        button1.setOnClickListener(v -> onClickButton(button1));
        button2.setOnClickListener(v -> onClickButton(button2));
    }

    /**
     * Maneja los clics de los botones.
     *
     * @param button el botón que fue clicado
     */
    private void onClickButton(Button button) {
        if (button.getId() == R.id.btn_option1) {
            Intent intent = new Intent(getContext(), ChatScreen.class);
            intent.putExtra("Origin", "NewChat");
            startActivity(intent);
        } else if (button.getId() == R.id.btn_option2) {
            Toast.makeText(getContext(), getString(R.string.search_chat), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRecentConversation() {
        db.collection("conversation")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Conversation> conversations = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Conversation conversation = snapshot.toObject(Conversation.class);
                        conversations.add(conversation);
                    }
                    recyclerViewChat.setHasFixedSize(true);
                    recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext(),
                            LinearLayoutManager.VERTICAL, false));
                    recentChatAdapter = new RecentChatAdapter(getContext(), conversations, chatClickListener);
                    recyclerViewChat.setAdapter(recentChatAdapter);
                })
                .addOnFailureListener(e -> Log.d("HOME FRAGMENT", "conversation:onError", e));
    }

    private void loadSavedConversation() {
        db.collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Conversation> conversations = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Conversation conversation = snapshot.toObject(Conversation.class);
                        conversations.add(conversation);
                    }
                    recyclerViewSaved.setHasFixedSize(true);
                    recyclerViewSaved.setLayoutManager(new LinearLayoutManager(requireContext(),
                            LinearLayoutManager.VERTICAL, false));
                    savedChatAdapter = new SavedChatAdapter(requireContext(), conversations, chatClickListener);
                    recyclerViewSaved.setAdapter(savedChatAdapter);
                })
                .addOnFailureListener(e -> Log.e("ChatFragment", "Error loading saved chats", e));
    }

    private final IChatClickListener chatClickListener = id -> {
        Intent intent = new Intent(this.getContext(), ChatScreen.class);
        intent.putExtra("Origin", "ExistingChat");
        intent.putExtra("IdThread", id);
        startActivity(intent);
    };
}
