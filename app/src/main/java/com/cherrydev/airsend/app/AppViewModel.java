package com.cherrydev.airsend.app;

import static com.cherrydev.airsend.app.MyApplication.databaseManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cherrydev.airsend.app.database.models.Device;
import com.cherrydev.airsend.app.database.models.SentMessage;
import com.cherrydev.airsend.app.database.models.UserMessage;
import com.cherrydev.airsend.app.utils.NavUtils;
import com.cherrydev.time.CommonTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

import io.github.romansj.core.MessageType;

public class AppViewModel extends ViewModel {
    private MutableLiveData<String> textToSend = new MutableLiveData<>();
    private LiveData<List<UserMessage>> serverMessages;
    private MutableLiveData<Boolean> needToCopyClipboard = new MutableLiveData<>();
    private LiveData<List<Device>> devices;

    private NavUtils.Tag navigationTag = NavUtils.Tag.MESSAGES;
    private LiveData<List<SentMessage>> sentMessages;

    private List<MessageType> types = List.of(MessageType.values());
    private long dateFrom = -1;
    private long dateUntil = -1;


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

    public LiveData<List<SentMessage>> getSentMessages() {
        // if (sentMessages == null) // todo 12.06.2022
        // sentMessages = databaseManager.getDb().getSentMessages(dateFrom, dateUntil, types);
        sentMessages = databaseManager.getDb().getSentMessages(dateFrom, dateUntil, types);
        return sentMessages;
    }

    public void setSentMessageFilter(@Nullable LocalDateTime dateFrom, @Nullable LocalDateTime dateUntil, @NonNull List<MessageType> types) {
        this.dateFrom = dateFrom == null ? -1 : CommonTimeUtils.Convert.toMillis(dateFrom);
        this.dateUntil = dateUntil == null ? -1 : CommonTimeUtils.Convert.toMillis(dateUntil);
        this.types = types.isEmpty() ? List.of(MessageType.values()) : types;
    }
}