package com.chatIA.chatbotIA.models;

/**
 * Clase que representa un elemento de chat.
 */
public class ChatItem {

    private int ivIcon;
    private String title;

    /**
     * Constructor para crear un objeto ChatItem.
     *
     * @param ivIcon el icono del elemento de chat
     * @param title el título del elemento de chat
     */
    public ChatItem(int ivIcon, String title) {
        this.ivIcon = ivIcon;
        this.title = title;
    }

    /**
     * Obtiene el icono del elemento de chat.
     *
     * @return el icono del elemento de chat
     */
    public int getIvIcon() {
        return ivIcon;
    }

    /**
     * Establece el icono del elemento de chat.
     *
     * @param ivIcon el nuevo icono del elemento de chat
     */
    public void setIvIcon(int ivIcon) {
        this.ivIcon = ivIcon;
    }

    /**
     * Obtiene el título del elemento de chat.
     *
     * @return el título del elemento de chat
     */
    public String getTitle() {
        return title;
    }

    /**
     * Establece el título del elemento de chat.
     *
     * @param title el nuevo título del elemento de chat
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
