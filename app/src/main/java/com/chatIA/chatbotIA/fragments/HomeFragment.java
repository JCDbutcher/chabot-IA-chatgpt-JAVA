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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.adapters.ExplorerAdapter;
import com.chatIA.chatbotIA.adapters.RecentChatAdapter;
import com.chatIA.chatbotIA.assistants.models.Conversation;
import com.chatIA.chatbotIA.assistants.models.response.AssistantData;
import com.chatIA.chatbotIA.assistants.models.response.ListAssistantResponse;/*
import com.ren.dianav2.database.ImageDatabaseManager;*/
import com.chatIA.chatbotIA.helpers.RequestManager;
import com.chatIA.chatbotIA.listener.IAssistantClickListener;
import com.chatIA.chatbotIA.listener.IChatClickListener;
import com.chatIA.chatbotIA.listener.IListAssistantResponse;
import com.chatIA.chatbotIA.models.ChatItem;
import com.chatIA.chatbotIA.models.Item;
import com.chatIA.chatbotIA.screens.ChatScreen;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Un {@link Fragment} simple que muestra una lista de exploración y chats recientes.
 * Método de construccion {@link HomeFragment#newInstance} para crear una instancia de este fragmento.
 */
public class HomeFragment extends Fragment {

    // parámetros de inicialización del fragmento, p. ej., ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Renombrar y cambiar tipos de parámetros
    private String mParam1;
    private String mParam2;
    private RecyclerView rv_explore;
    private ExplorerAdapter exploreAdapter;
    private RecyclerView rv_recent_chat;
    private RecentChatAdapter recentChatAdapter;
    private List<Item> items;
    private List<ChatItem> chatItems;
    private List<AssistantData> dataList;
    private RequestManager requestManager;
    private TextView tvUsername;
    private ImageView ivProfile;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
/*
    private ImageDatabaseManager miManager;*/

    public HomeFragment() {
        // Constructor público requerido
    }

    /**
     * Metodo de construccion para crear una nueva instancia de
     * este fragmento utilizando los parámetros proporcionados.
     *
     * @param param1 Parámetro 1.
     * @param param2 Parámetro 2.
     * @return Una nueva instancia del fragmento HomeFragment.
     */
    // TODO: Renombrar y cambiar tipos y número de parámetros
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Infla el diseño para este fragmento
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rv_explore = view.findViewById(R.id.rv_explore);
        rv_recent_chat = view.findViewById(R.id.rv_recent_chat);
        tvUsername = view.findViewById(R.id.tv_username);
        ivProfile = view.findViewById(R.id.iv_profile);

        requestManager = new RequestManager(requireContext());

        rv_explore.setHasFixedSize(true);
        rv_explore.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        /*
        miManager = new ImageDatabaseManager(getContext());
        miManager.open();*/
        addDataToList();

        exploreAdapter = new ExplorerAdapter(getContext(), items, dataList, listener);
        rv_explore.setAdapter(exploreAdapter);

        loadConversation();

        if (currentUser != null) {
            String username = currentUser.getDisplayName();
            String[] arrayPocho = username.split(" ");
            if (arrayPocho[1] == null) {
                arrayPocho[1] = "";
            }
            String nombrePocho = arrayPocho[0] + " " + arrayPocho[1];
            tvUsername.setText(nombrePocho);
            /*
            if (miManager.getImageUri() != null) {
                loadSavedProfileImage();
            } else {
                Picasso.get().load(profile).into(ivProfile);
            }*/

            String profile = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
            if(profile!=null){
                Picasso.get().load(profile).into(ivProfile);
            }
        }

        return view;
    }


    /**
     * Agrega datos a las listas de elementos y chats.
     */
    private void addDataToList() {
        items = new ArrayList<>();
        dataList = new ArrayList<>();
        chatItems = new ArrayList<>();

        items.add(new Item(R.drawable.assistant, getString(R.string.diana_name),
                getString(R.string.diana_description)));
        items.add(new Item(R.drawable.round_image_24, getString(R.string.images_title),
                getString(R.string.images_description)));

        requestManager.getListAssistant("assistants=v2", iListAssistantResponse);

        /*chatItems.add(new ChatItem(R.drawable.round_chat_24, getString(R.string.java_code_explanation)));
        chatItems.add(new ChatItem(R.drawable.round_chat_24, getString(R.string.math_exercises_resolution)));
        chatItems.add(new ChatItem(R.drawable.round_chat_24, getString(R.string.use_your_brain_example)));*/
    }

    private final IListAssistantResponse iListAssistantResponse = new IListAssistantResponse() {
        @Override
        public void didFetch(ListAssistantResponse listAssistantResponse, String msg) {
            if (listAssistantResponse != null && listAssistantResponse.data != null) {
                dataList.addAll(listAssistantResponse.data);
                exploreAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void didError(String msg) {
            showMessage("Error with list assistant: " + msg);
        }
    };


    private final IAssistantClickListener listener = id -> {
        Intent intent = new Intent(HomeFragment.this.getContext(), ChatScreen.class);
        intent.putExtra("Origin", "NewChat");
        intent.putExtra("IdAssistant", id);
        startActivity(intent);
    };

    private final IChatClickListener chatClickListener = id -> {
        Intent intent = new Intent(HomeFragment.this.getContext(), ChatScreen.class);
        intent.putExtra("Origin", "ExistingChat");
        intent.putExtra("IdThread", id);
        startActivity(intent);
    };

    private void loadConversation() {
        db.collection("conversation")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Conversation> conversations = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Conversation conversation = snapshot.toObject(Conversation.class);
                        conversations.add(conversation);
                    }
                    rv_recent_chat.setHasFixedSize(true);
                    rv_recent_chat.setLayoutManager(new LinearLayoutManager(getContext(),
                            LinearLayoutManager.VERTICAL, false));
                    recentChatAdapter = new RecentChatAdapter(getContext(), conversations, chatClickListener);
                    rv_recent_chat.setAdapter(recentChatAdapter);
                })
                .addOnFailureListener(e -> Log.d("HOME FRAGMENT", "conversation:onError", e));
    }

    private void showMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
/*
    private void loadSavedProfileImage() {
        String savedUriString = miManager.getImageUri();
        if (savedUriString != null) {
            Uri savedUri = Uri.parse(savedUriString);
            ivProfile.setImageURI(savedUri);
        }
    }*/
}
