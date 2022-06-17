package com.cherrydev.airsend.app;


import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.cherrydev.airsend.app.database.AppDatabase;
import com.cherrydev.airsend.app.database.DatabaseManager;
import com.cherrydev.airsend.app.database.DbDao;
import com.cherrydev.airsend.app.database.models.Device;
import com.cherrydev.airsend.app.database.models.UserMessage;
import com.cherrydev.airsendcore.core.MessageType;
import com.cherrydev.time.CommonTimeUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDateTime;


@RunWith(AndroidJUnit4.class)
public class DatabaseTests {

    private DbDao dao;
    private AppDatabase db;
    private Context context;
    private DatabaseManager databaseManager;

    @Before
    public void createDb() {
        context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        dao = db.dao();

        databaseManager = new DatabaseManager(db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void deviceIsAdded() throws Exception {
        databaseManager.addDevice(new Device("Device", "192.168.1.100", 0)).run();

        var device = dao.findByIP("192.168.1.100");
        assertThat(device.getIP()).isEqualTo("192.168.1.100");
    }

    @Test
    public void deviceIsDeleted() throws Exception {
        databaseManager.addDevice(new Device("Device", "192.168.1.100", 0)).run();
        var devices = dao.getAll();
        var sizeAfterAdd = devices.size();

        dao.deleteAllDevices();
        var devices2 = dao.getAll();
        var sizeAfterDelete = devices2.size();


        assertThat(sizeAfterDelete).isNotEqualTo(sizeAfterAdd);
    }

    @Test
    public void deviceIsUpdated() throws Exception {
        databaseManager.addDevice(new Device("Update", "Update", 0)).run();

        var device = dao.findDevice("Update", 0);
        device.setPort(10);
        device.setName("Update New");
        dao.updateDevice(device);

        var deviceAfterUpdate = databaseManager.findDeviceByIP("Update");
        assertThat(
                deviceAfterUpdate.getPort() == 10 &&
                        deviceAfterUpdate.getName().equals("Update New")
        ).isTrue();
    }


    @Test
    public void messageIsAddedByText() throws Exception {
        UserMessage userMessage = createMessage("null");
        databaseManager.addMessage(userMessage).run();

        var messages = dao.findByText("null");
        assertThat(messages.get(0).getText()).isEqualTo("null");
    }

    @Test
    public void messageIsAddedByIp() throws Exception {
        UserMessage userMessage = createMessage("null", "ip");
        databaseManager.addMessage(userMessage).run();

        var messages = dao.findMessagesByIp("ip");
        assertThat(messages.get(0).getIP()).isEqualTo("ip");
    }

    @Test
    public void messageIsDeleted() throws Exception {
        UserMessage userMessage = createMessage("hello");
        databaseManager.addMessage(userMessage).run();

        var messages = dao.findByText("hello");
        var id = messages.get(0).getId();
        databaseManager.deleteMessage(id).run();

        var messagesAfterDelete = dao.findByText("hello");
        assertThat(messagesAfterDelete).isEmpty();
    }


    @NonNull
    private UserMessage createMessage(String textStr) {
        String ip = "null";
        int port = 0;
        String text = textStr;
        MessageType type = MessageType.MESSAGE;
        String dateTime = CommonTimeUtils.Format.toFormattedDateTimeString(LocalDateTime.now(), true);
        return new UserMessage(ip, port, text, type, dateTime);
    }

    @NonNull
    private UserMessage createMessage(String textStr, String ip) {
        int port = 0;
        String text = textStr;
        MessageType type = MessageType.MESSAGE;
        String dateTime = CommonTimeUtils.Format.toFormattedDateTimeString(LocalDateTime.now(), true);
        return new UserMessage(ip, port, text, type, dateTime);
    }


    @Test
    public void useAppContext() {
        // if we don't pass here, then test did not instantiate correctly
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.cherrydev.airsend", appContext.getPackageName());
    }


}
