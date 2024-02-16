package com.cherrydev.benchmark;

import androidx.benchmark.BenchmarkState;
import androidx.benchmark.junit4.BenchmarkRule;

import com.google.gson.Gson;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.github.romansj.core.server.ServerEvent;
import io.github.romansj.core.server.ServerOperation;


@RunWith(JUnit4.class)
public class SerializationBenchmarkTest {
    public static Gson GSON = new Gson();

    @Rule
    public BenchmarkRule benchmarkRule = new BenchmarkRule();

    @Test
    public void gsonToJson() {
        var eventSerializable = new ServerEventSerializable(ServerOperation.START, 80);

        BenchmarkState state = benchmarkRule.getState();
        while (state.keepRunning()) {
            String json = GSON.toJson(eventSerializable);
        }
    }

    @Test
    public void gsonFromJson() {
        var json = "{\"operation\":\"START\",\"port\":80}";

        BenchmarkState state = benchmarkRule.getState();
        while (state.keepRunning()) {
            ServerEvent deserialized = GSON.fromJson(json, ServerEvent.class);
        }
    }
}
