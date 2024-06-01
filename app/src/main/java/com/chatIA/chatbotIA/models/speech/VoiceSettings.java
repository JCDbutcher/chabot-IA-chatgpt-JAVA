package com.chatIA.chatbotIA.models.speech;

public class VoiceSettings {

    private int stability;
    private int similarityBoost;
    private int style;
    private boolean useSpeakerBoost;

    public int getStability() {
        return stability;
    }

    public void setStability(int stability) {
        this.stability = stability;
    }

    public int getSimilarityBoost() {
        return similarityBoost;
    }

    public void setSimilarityBoost(int similarityBoost) {
        this.similarityBoost = similarityBoost;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public boolean isUseSpeakerBoost() {
        return useSpeakerBoost;
    }

    public void setUseSpeakerBoost(boolean useSpeakerBoost) {
        this.useSpeakerBoost = useSpeakerBoost;
    }
}
