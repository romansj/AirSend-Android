package com.cherrydev.airsend.app.messages;

public interface OnClickListener<T extends IMessage> {
    void onClick(T message);

    void onLongClick(T message);
}
