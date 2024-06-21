package com.chatIA.chatbotIA.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatIA.chatbotIA.R;
import com.chatIA.chatbotIA.assistants.models.response.AssistantData;
import com.chatIA.chatbotIA.listener.IAssistantClickListener;
import com.chatIA.chatbotIA.models.Item;
import com.chatIA.chatbotIA.screens.ChatScreen;
import com.chatIA.chatbotIA.screens.ImageChatScreen;

import java.util.List;

/**
 * Adaptador para mostrar una lista de elementos y datos de asistentes en un RecyclerView.
 */
public class ExplorerAdapter extends RecyclerView.Adapter<ExplorerViewHolder> {

    private Context context;
    private List<Item> items;
    private Intent intent;
    private List<AssistantData> dataList;
    private IAssistantClickListener clickListener;

    /**
     * Constructor para ExplorerAdapter.
     *
     * @param context el contexto en el cual el adaptador está operando
     * @param items la lista de elementos a mostrar
     * @param dataList la lista de datos de asistentes a mostrar
     * @param clickListener el listener para manejar eventos de clic en los asistentes
     */
    public ExplorerAdapter(Context context, List<Item> items, List<AssistantData> dataList,
                           IAssistantClickListener clickListener) {
        this.context = context;
        this.items = items;
        this.dataList = dataList;
        this.clickListener = clickListener;
    }

    /**
     * Llamado cuando RecyclerView necesita un nuevo ViewHolder del tipo dado para representar un elemento.
     *
     * @param parent el ViewGroup en el que la nueva Vista será añadida después de ser enlazada a
     *               una posición del adaptador
     * @param viewType el tipo de vista del nuevo View
     * @return un nuevo ExplorerViewHolder que contiene una Vista del tipo dado
     */
    @NonNull
    @Override
    public ExplorerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExplorerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_explore,
                parent, false));
    }

    /**
     * Llamado por RecyclerView para mostrar los datos en la posición especificada.
     *
     * @param holder el ViewHolder que debe ser actualizado para representar los contenidos del
     *               elemento en la posición dada en el conjunto de datos
     * @param position la posición del elemento dentro del conjunto de datos del adaptador
     */
    @Override
    public void onBindViewHolder(@NonNull ExplorerViewHolder holder, int position) {
        if (position < items.size()) {
            holder.getIvAssistant().setImageResource(items.get(position).getIvAssistant());
            holder.getTvTitle().setText(items.get(position).getTvTitle());
            holder.getTvDescription().setText(items.get(position).getTvDescription());
        } else {
            int dataIndex = position - items.size();
            if (dataIndex < dataList.size()) {
                holder.getIvAssistant().setImageResource(dataList.get(dataIndex).image);
                holder.getTvTitle().setText(dataList.get(dataIndex).name);
                holder.getTvDescription().setText(String.valueOf(dataList.get(dataIndex).description));
            }
        }

        holder.itemView.setOnClickListener(v -> {
            switch (position) {
                case 0:
                    intent = new Intent(context, ChatScreen.class);
                    context.startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(context, ImageChatScreen.class);
                    context.startActivity(intent);
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    if (position - items.size() < dataList.size()) {
                        clickListener.onAssistantClicked(dataList.get(position - items.size()).id);
                    } else {
                        Toast.makeText(context, "Los datos del asistente no están disponibles", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        });
    }

    /**
     * Retorna el número total de elementos en el conjunto de datos que posee el adaptador.
     *
     * @return el número total de elementos en este adaptador
     */
    @Override
    public int getItemCount() {
        return items.size() + (dataList != null ? dataList.size() : 0);
    }
}

/**
 * ViewHolder para mostrar un elemento en el ExplorerAdapter.
 */
class ExplorerViewHolder extends RecyclerView.ViewHolder {

    private ImageView ivAssistant;
    private TextView tvTitle;
    private TextView tvDescription;

    /**
     * Constructor para ExplorerViewHolder.
     *
     * @param itemView la vista del elemento
     */
    public ExplorerViewHolder(@NonNull View itemView) {
        super(itemView);
        this.ivAssistant = itemView.findViewById(R.id.iv_assistant);
        this.tvTitle = itemView.findViewById(R.id.tv_title);
        this.tvDescription = itemView.findViewById(R.id.tv_description);
    }

    /**
     * Obtiene el ImageView del asistente.
     *
     * @return el ImageView del asistente
     */
    public ImageView getIvAssistant() {
        return ivAssistant;
    }

    /**
     * Establece el ImageView del asistente.
     *
     * @param ivAssistant el nuevo ImageView del asistente
     */
    public void setIvAssistant(ImageView ivAssistant) {
        this.ivAssistant = ivAssistant;
    }

    /**
     * Obtiene el TextView del título.
     *
     * @return el TextView del título
     */
    public TextView getTvTitle() {
        return tvTitle;
    }

    /**
     * Establece el TextView del título.
     *
     * @param tvTitle el nuevo TextView del título
     */
    public void setTvTitle(TextView tvTitle) {
        this.tvTitle = tvTitle;
    }

    /**
     * Obtiene el TextView de la descripción.
     *
     * @return el TextView de la descripción
     */
    public TextView getTvDescription() {
        return tvDescription;
    }

    /**
     * Establece el TextView de la descripción.
     *
     * @param tvDescription el nuevo TextView de la descripción
     */
    public void setTvDescription(TextView tvDescription) {
        this.tvDescription = tvDescription;
    }
}
