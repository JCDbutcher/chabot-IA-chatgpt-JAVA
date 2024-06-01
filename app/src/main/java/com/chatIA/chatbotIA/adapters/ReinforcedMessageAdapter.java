package com.chatIA.chatbotIA.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.assistants.models.request.MessageRequest;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adaptador para mostrar una lista de mensajes reforzados en un RecyclerView.
 */
public class ReinforcedMessageAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<MessageRequest> messages;
    private FirebaseUser currentUser;

    /**
     * Constructor para ReinforcedMessageAdapter.
     *
     * @param context el contexto en el cual el adaptador está operando
     * @param messages la lista de mensajes a mostrar
     * @param currentUser el usuario actual autenticado
     */
    public ReinforcedMessageAdapter(Context context, List<MessageRequest> messages, FirebaseUser currentUser) {
        this.context = context;
        this.messages = messages;
        this.currentUser = currentUser;
    }

    /**
     * Llamado cuando RecyclerView necesita un nuevo ViewHolder del tipo dado para representar un elemento.
     *
     * @param parent el ViewGroup en el que la nueva Vista será añadida después de ser enlazada a
     *               una posición del adaptador
     * @param viewType el tipo de vista del nuevo View
     * @return un nuevo ViewHolder que contiene una Vista del tipo dado
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (this.getItemViewType(viewType) == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_right_chat, parent, false);
            return new RightMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_left_chat, parent, false);
            return new LeftMessageViewHolder(view);
        }
    }

    /**
     * Llamado por RecyclerView para mostrar los datos en la posición especificada.
     *
     * @param holder el ViewHolder que debe ser actualizado para representar los contenidos del
     *               elemento en la posición dada en el conjunto de datos
     * @param position la posición del elemento dentro del conjunto de datos del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageRequest messageRequest = messages.get(position);
        if (this.getItemViewType(position) == 0) {
            RightMessageViewHolder rightMessageViewHolder = (RightMessageViewHolder) holder;
            rightMessageViewHolder.tvShowMessage.setText(messageRequest.content);
            if (currentUser != null) {
                Picasso.get().load(currentUser.getPhotoUrl()).into(rightMessageViewHolder.ivUser);
            }
        } else {
            LeftMessageViewHolder leftMessageViewHolder = (LeftMessageViewHolder) holder;
            leftMessageViewHolder.tvShowMessage.setVisibility(View.VISIBLE);
            leftMessageViewHolder.tvShowMessage.setText(messageRequest.content);
        }
    }

    /**
     * Retorna el número total de elementos en el conjunto de datos que posee el adaptador.
     *
     * @return el número total de elementos en este adaptador
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Determina el tipo de vista del elemento en la posición especificada.
     *
     * @param position la posición del elemento en el conjunto de datos del adaptador
     * @return el tipo de vista del elemento en la posición especificada
     */
    @Override
    public int getItemViewType(int position) {
        MessageRequest messageRequest = messages.get(position);
        return messageRequest.role.equals("user") ? 0 : 1;
    }
}