package com.cherrydev.airsend.app.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.cherrydev.airsend.app.database.models.Device;
import com.cherrydev.airsend.app.database.models.SentMessage;
import com.cherrydev.airsend.app.database.models.UserMessage;
import com.cherrydev.airsendcore.core.MessageType;
import com.cherrydev.airsendcore.core.SentStatus;
import com.cherrydev.airsendcore.core.Status;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface DbDao {

    // order by PK which is auto incremented, highest = newest on top
    @Query("SELECT * FROM Device ORDER BY IP")
    List<Device> getAll();

    // to use in ViewModel + view observe
    @Query("SELECT * FROM Device")
    LiveData<List<Device>> getAllLD();

    // one time observation
    @Query("SELECT * FROM Device")
    Single<List<Device>> getAllDevices();

    @Query("SELECT * FROM device where IP IN (:list)")
    Single<List<Device>> getAllDevices(ArrayList<String> list);

    @Query("SELECT * FROM Device WHERE IP = :ipAddress AND " + "port = :port")
    Device findDevice(String ipAddress, int port);

    @Query("SELECT * FROM Device WHERE IP = :ipAddress")
    Device findByIP(String ipAddress);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addDevice(Device device);

    @Insert
    void addDevices(List<Device> devices);

    @Update
    void updateDevice(Device deviceExisting);

    @Query("UPDATE Device SET clientRunning=:clientRunning WHERE ip=:ip")
    void updateDevice(String ip, Status clientRunning);

    @Query("UPDATE Device SET clientRunning=:clientRunning WHERE ip in (:list)")
    void updateDevices(List<String> list, Status clientRunning);

    @Delete
    void deleteDevice(Device user);

    @Query("DELETE FROM Device")
    void deleteAllDevices();

    @Query("DELETE FROM Device WHERE ip=:ip")
    void deleteDevice(String ip);

    @Query("DELETE FROM Device WHERE ip in (:list)")
    void deleteDevices(List<String> list);


    ///////////////////
    ///////////////////

    @Query("SELECT * FROM UserMessage WHERE type=:type ORDER BY id DESC")
    LiveData<List<UserMessage>> getMessages(MessageType type);

    @Insert
    void addMessage(UserMessage userMessage);

    @Delete
    void deleteMessage(UserMessage userMessage);

    @Query("DELETE FROM UserMessage WHERE id=:id")
    void deleteMessage(long id);

    @Query("DELETE FROM UserMessage")
    void deleteAllMessages();


    @Query("SELECT * FROM usermessage WHERE text LIKE  '%' || :text || '%'")
    List<UserMessage> findByText(String text);

    @Query("SELECT * FROM usermessage WHERE IP LIKE  '%' || :IP || '%'")
    List<UserMessage> findMessagesByIp(String IP);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addSentMessage(SentMessage message);

    @Query("UPDATE SentMessage SET sentStatus=:sentStatus WHERE id =:id")
    void updateSentMessage(long id, SentStatus sentStatus);

    @Update()
    void updateSentMessage(SentMessage sentMessage);

    @Query("SELECT * FROM SentMessage ORDER BY id DESC")
    LiveData<List<SentMessage>> getSentMessages();

    @Query(
            "SELECT * FROM SentMessage " +
                    "WHERE (1=1)" +

                    "AND CASE WHEN :dateFrom is not -1 " +
                    "THEN dateTime > :dateFrom " +
                    "ELSE 1 " +
                    "END " +

                    "AND CASE WHEN :dateUntil is not -1 " +
                    "THEN dateTime < :dateUntil " +
                    "ELSE 1 " +
                    "END " +

                    "AND type in (:types)" +
                    "ORDER BY id DESC"
    )
    LiveData<List<SentMessage>> getSentMessages(long dateFrom, long dateUntil, List<MessageType> types);

    @Query("SELECT DISTINCT date (dateTime/1000, 'unixepoch') FROM SentMessage ORDER BY dateTime ASC")
    LiveData<List<String>> getUniqueDates();


    @Query("DELETE FROM SentMessage")
    void deleteAllSentMessages();

    @Query("SELECT * FROM SentMessage WHERE id = :id")
    SentMessage findSentMessage(long id);
}
