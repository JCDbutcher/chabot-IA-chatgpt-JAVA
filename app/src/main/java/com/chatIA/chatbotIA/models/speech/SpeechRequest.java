package com.chatIA.chatbotIA.models.speech;

import java.util.List;

public class SpeechRequest {

    private String text;
    private String modelId;
    private VoiceSettings voiceSettings;
    private List<PronunciationDictionaryLocator> pronunciationDictionaryLocators;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public VoiceSettings getVoiceSettings() {
        return voiceSettings;
    }

    public void setVoiceSettings(VoiceSettings voiceSettings) {
        this.voiceSettings = voiceSettings;
    }

    public List<PronunciationDictionaryLocator> getPronunciationDictionaryLocators() {
        return pronunciationDictionaryLocators;
    }

    public void setPronunciationDictionaryLocators(List<PronunciationDictionaryLocator> pronunciationDictionaryLocators) {
        this.pronunciationDictionaryLocators = pronunciationDictionaryLocators;
    }
}
