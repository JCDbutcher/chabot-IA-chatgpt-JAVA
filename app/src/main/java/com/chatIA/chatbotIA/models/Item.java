package com.chatIA.chatbotIA.models;

/**
 * Clase que representa un elemento con un asistente, un título y una descripción.
 */
public class Item {

    private int ivAssistant;
    private String tvTitle;
    private String tvDescription;

    /**
     * Constructor para crear un objeto Item.
     *
     * @param ivAssistant el icono del asistente
     * @param tvTitle el título del elemento
     * @param tvDescription la descripción del elemento
     */
    public Item(int ivAssistant, String tvTitle, String tvDescription) {
        this.ivAssistant = ivAssistant;
        this.tvTitle = tvTitle;
        this.tvDescription = tvDescription;
    }

    /**
     * Obtiene el icono del asistente.
     *
     * @return el icono del asistente
     */
    public int getIvAssistant() {
        return ivAssistant;
    }

    /**
     * Establece el icono del asistente.
     *
     * @param ivAssistant el nuevo icono del asistente
     */
    public void setIvAssistant(int ivAssistant) {
        this.ivAssistant = ivAssistant;
    }

    /**
     * Obtiene el título del elemento.
     *
     * @return el título del elemento
     */
    public String getTvTitle() {
        return tvTitle;
    }

    /**
     * Establece el título del elemento.
     *
     * @param tvTitle el nuevo título del elemento
     */
    public void setTvTitle(String tvTitle) {
        this.tvTitle = tvTitle;
    }

    /**
     * Obtiene la descripción del elemento.
     *
     * @return la descripción del elemento
     */
    public String getTvDescription() {
        return tvDescription;
    }

    /**
     * Establece la descripción del elemento.
     *
     * @param tvDescription la nueva descripción del elemento
     */
    public void setTvDescription(String tvDescription) {
        this.tvDescription = tvDescription;
    }
}
