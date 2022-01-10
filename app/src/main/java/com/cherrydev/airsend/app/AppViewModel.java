package com.cherrydev.airsend.app;

import static com.cherrydev.airsend.app.MyApplication.databaseManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cherrydev.airsend.app.database.models.Device;
import com.cherrydev.airsend.app.database.models.UserMessage;
import com.cherrydev.airsend.app.utils.NavUtils;
import com.cherrydev.airsend.core.MessageType;

import java.util.List;

public class AppViewModel extends ViewModel {
    private MutableLiveData<String> textToSend = new MutableLiveData<>();
    private LiveData<List<UserMessage>> serverMessages;
    private MutableLiveData<Boolean> needToCopyClipboard = new MutableLiveData<>();
    private LiveData<List<Device>> devices;

    private NavUtils.Tag navigationTag = NavUtils.Tag.MESSAGES;


    public AppViewModel() {

    }


    public MutableLiveData<String> getTextToSend() {
        return textToSend;
    }

    public void setTextToSend(String textToSend) {
        this.textToSend.setValue(textToSend);
    }


    public void setNeedToCopyClipboard(boolean b) {
        needToCopyClipboard.setValue(b);
    }

    public MutableLiveData<Boolean> getNeedToCopyClipboard() {
        return needToCopyClipboard;
    }


    public LiveData<List<UserMessage>> getMessages() {
        if (serverMessages == null)
            serverMessages = databaseManager.getDb().getMessages(MessageType.MESSAGE);
        return serverMessages;
    }


    public LiveData<List<Device>> getDevices() {
        if (devices == null)
            devices = databaseManager.getDb().getAllLD();
        return devices;
    }


    public NavUtils.Tag getNavigationTag() {
        return navigationTag;
    }

    public void setNavigationTag(NavUtils.Tag navigationTag) {
        this.navigationTag = navigationTag;
    }
}