package com.cherrydev.airsend.app.messages;

import androidx.lifecycle.ViewModel;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.MyApplication;
import com.cherrydev.airsend.app.database.models.Device;

import java.util.ArrayList;
import java.util.List;

public class MessageViewModel extends ViewModel {
    private String recipientChoice;
    private List<Device> selectedDevices = new ArrayList<>();
    private List<String> recipientChoices = List.of(MyApplication.getInstance().getString(R.string.connected_devices), MyApplication.getInstance().getString(R.string.selected_devices));

    String messageInputText = "";


    public MessageViewModel() {
        recipientChoice = recipientChoices.get(0);
    }


    public String getRecipientChoice() {
        return recipientChoice;
    }

    public List<Device> getSelectedDevices() {
        return selectedDevices;
    }

    public List<String> getRecipientChoices() {
        return recipientChoices;
    }

    public void setRecipientChoice(String recipientChoice) {
        this.recipientChoice = recipientChoice;
    }

    public void setSelectedDevices(List<Device> selected) {
        this.selectedDevices.clear();
        this.selectedDevices.addAll(selected);

    }

    public void setRecipientChoices(List<String> recipientChoices) {
        this.recipientChoices = recipientChoices;
    }

    public void setMessageInputText(String s) {
        messageInputText = s;
    }

    public String getMessageInputText() {
        return messageInputText;
    }
}