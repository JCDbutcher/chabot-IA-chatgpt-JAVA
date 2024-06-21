package com.chatIA.chatbotIA.screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.adapters.RecentChatAdapter;
import com.chatIA.chatbotIA.adapters.SavedChatAdapter;
import com.chatIA.chatbotIA.assistants.models.Conversation;
import com.chatIA.chatbotIA.listener.IChatClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AllChatsScreen extends AppCompatActivity {

    private String typeChats;
    private TextView tvTypeChats;
    private ImageButton ibBack;
    private RecyclerView rvExplore;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private RecentChatAdapter recentChatAdapter;
    private SavedChatAdapter savedChatAdapter;

    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_chats_screen);

        tvTypeChats = findViewById(R.id.tv_typeChat);
        ibBack = findViewById(R.id.ib_back);
        typeChats = getIntent().getStringExtra("Type");
        rvExplore = findViewById(R.id.rv_explore);
        ivProfile = findViewById(R.id.iv_profile);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        setLayoutTitle();
        onClickBackButton(ibBack);
        loadConversations();
        String profile = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
        if (profile != null) {
            Picasso.get().load(profile).into(ivProfile);
        }
    }

    private void setLayoutTitle() {
        if (typeChats.equals("Saved")) {
            tvTypeChats.setText(R.string.saved);
        } else {
            tvTypeChats.setText(R.string.recents);
        }
    }

    /**
     * Configura el evento de clic para el botón de retroceso.
     *
     * @param button el botón de retroceso
     */
    private void onClickBackButton(ImageButton button) {
        button.setOnClickListener(v -> finish());
    }

    private void loadConversations() {
        if (typeChats.equals("Saved")) {
            loadSavedConversation();
        } else {
            loadRecentConversation();
        }
    }

    private void loadRecentConversation() {
        db.collection("conversation")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Conversation> conversations = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Conversation conversation = snapshot.toObject(Conversation.class);
                            conversations.add(conversation);
                        }
                        rvExplore.setHasFixedSize(true);
                        rvExplore.setLayoutManager(new LinearLayoutManager(AllChatsScreen.this,
                                LinearLayoutManager.VERTICAL, false));
                        recentChatAdapter = new RecentChatAdapter(AllChatsScreen.this, conversations, chatClickListener);
                        rvExplore.setAdapter(recentChatAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ALL_CHATS_SCREEN", "conversation:onError", e);
                    }
                });
    }

    private void loadSavedConversation() {
        db.collection("users")
                .document(currentUser.getUid())
                .collection("favorites")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Conversation> conversations = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Conversation conversation = snapshot.toObject(Conversation.class);
                            conversations.add(conversation);
                        }
                        rvExplore.setHasFixedSize(true);
                        rvExplore.setLayoutManager(new LinearLayoutManager(AllChatsScreen.this,
                                LinearLayoutManager.VERTICAL, false));
                        savedChatAdapter = new SavedChatAdapter(AllChatsScreen.this, conversations, chatClickListener);
                        rvExplore.setAdapter(savedChatAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ALL_CHATS_SCREEN", "Error loading saved chats", e);
                    }
                });
    }

    private final IChatClickListener chatClickListener = new IChatClickListener() {
        @Override
        public void onRecentChatClicked(String id) {
            // Implement your logic to handle recent chat click
        }

        @Override
        public void onDeleteChatClicked(String id) {
            // Implement your logic to handle chat deletion
        }
    };
}
