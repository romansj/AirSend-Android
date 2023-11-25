package com.cherrydev.airsend.app.connections;

public class CameraResult {
    String text;
    ResultType resultType;

    public CameraResult(String text, ResultType resultType) {
        this.text = text;
        this.resultType = resultType;
    }

    public CameraResult(String text) {
        this.text = text;
        this.resultType = ResultType.TEXT_RECOGNIZED;
    }

    public String getText() {
        return text;
    }

    public ResultType getResultType() {
        return resultType;
    }
}
