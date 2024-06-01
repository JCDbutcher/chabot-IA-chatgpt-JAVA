package com.chatIA.chatbotIA.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.listener.IImageClick;
import com.chatIA.chatbotIA.models.Message;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adaptador para mostrar una lista de mensajes en un RecyclerView.
 */
public class MessageAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Message> messages;
    private IImageClick iImageClick;

    /**
     * Constructor para MessageAdapter.
     *
     * @param context el contexto en el cual el adaptador está operando
     * @param messages la lista de mensajes a mostrar
     * @param iImageClick el listener para manejar eventos de clic en las imágenes
     */
    public MessageAdapter(Context context, List<Message> messages, IImageClick iImageClick) {
        this.context = context;
        this.messages = messages;
        this.iImageClick = iImageClick;
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
        Message message = messages.get(position);
        if (this.getItemViewType(position) == 0) {
            RightMessageViewHolder rightMessageViewHolder = (RightMessageViewHolder) holder;
            rightMessageViewHolder.tvShowMessage.setText(message.getMessage());
        } else {
            LeftMessageViewHolder leftMessageViewHolder = (LeftMessageViewHolder) holder;
            if (message.isImage()) {
                leftMessageViewHolder.llImageCard.setVisibility(View.VISIBLE);
                leftMessageViewHolder.tvShowMessage.setVisibility(View.GONE);
                Picasso.get().load(message.getMessage()).into(leftMessageViewHolder.ivImage);
                leftMessageViewHolder.itemView.setOnClickListener(v -> {
                    iImageClick.onImageClick(messages.get(holder.getAdapterPosition()).getMessage());
                });
            } else {
                leftMessageViewHolder.tvShowMessage.setVisibility(View.VISIBLE);
                leftMessageViewHolder.tvShowMessage.setText(message.getMessage());
            }
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
        Message message = messages.get(position);
        return message.isUser() ? 0 : 1;
    }
}

/**
 * ViewHolder para mostrar un mensaje a la derecha en el MessageAdapter.
 */
class RightMessageViewHolder extends RecyclerView.ViewHolder {

    public TextView tvShowMessage;
    public ImageView ivUser;

    /**
     * Constructor para RightMessageViewHolder.
     *
     * @param itemView la vista del elemento
     */
    public RightMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        tvShowMessage = itemView.findViewById(R.id.tv_show_message);
        ivUser = itemView.findViewById(R.id.iv_user);
    }
}

/**
 * ViewHolder para mostrar un mensaje a la izquierda en el MessageAdapter.
 */
class LeftMessageViewHolder extends RecyclerView.ViewHolder {
    public TextView tvShowMessage;
    public LinearLayout llImageCard;
    public ImageView ivImage;

    /**
     * Constructor para LeftMessageViewHolder.
     *
     * @param itemView la vista del elemento
     */
    public LeftMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        tvShowMessage = itemView.findViewById(R.id.tv_show_message);
        llImageCard = itemView.findViewById(R.id.ll_image_card);
        ivImage = itemView.findViewById(R.id.iv_image);
    }
}
