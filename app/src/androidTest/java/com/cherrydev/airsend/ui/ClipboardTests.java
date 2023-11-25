package com.cherrydev.airsend.ui;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.ClipboardManager;
import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.MainActivity;
import com.cherrydev.clipboard.ClipboardUtils;
import com.google.common.truth.Truth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ClipboardTests {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);


    @Test
    public void copiedTextIsRetrievable() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        ClipboardUtils.copyToClipboard(context, "Text");

        inputFocus();
        var clipboardText = ClipboardUtils.getClipboardText(context);

        Truth.assertThat(clipboardText).isEqualTo("Text");
    }

    @Test
    public void copiedTextMatchesManualRetrieval() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ClipboardUtils.copyToClipboard(context, "Text");


        inputFocus();
        var clipboardService = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        var clipboardText = clipboardService.getPrimaryClip().getItemAt(0).getText().toString();

        Truth.assertThat(clipboardText).isEqualTo("Text");
    }

    // because clipboard content cannot be accessed without having editText focus
    private void inputFocus() {
        onView(withId(R.id.et_message)).perform(typeText("Steve"));
    }
}