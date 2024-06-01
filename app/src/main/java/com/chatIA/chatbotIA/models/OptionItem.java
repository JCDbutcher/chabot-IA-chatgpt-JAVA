package com.chatIA.chatbotIA.models;

/**
 * Clase que representa un elemento de opción con un nombre y una imagen.
 */
public class OptionItem {

    private String name;
    private int image;

    /**
     * Constructor para crear un objeto OptionItem.
     *
     * @param name el nombre de la opción
     * @param image la imagen de la opción
     */
    public OptionItem(String name, int image) {
        this.name = name;
        this.image = image;
    }

    /**
     * Obtiene el nombre de la opción.
     *
     * @return el nombre de la opción
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre de la opción.
     *
     * @param name el nuevo nombre de la opción
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la imagen de la opción.
     *
     * @return la imagen de la opción
     */
    public int getImage() {
        return image;
    }

    /**
     * Establece la imagen de la opción.
     *
     * @param image la nueva imagen de la opción
     */
    public void setImage(int image) {
        this.image = image;
    }
}

