package com.cherrydev.airsend.app.utils;

import static android.content.Intent.ACTION_SEND;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.MainActivity;
import com.cherrydev.airsend.app.MyApplication;
import com.cherrydev.airsend.app.service.ServerService;
import com.cherrydev.clipboard.ClipboardUtils;
import com.cherrydev.common.MimeTypes;

import timber.log.Timber;

public class IntentActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);

        Intent intent = getIntent();

        if (intent.getAction().equals(ACTION_SEND)) {
            intent.setAction(IntentAction.ACTION_SEND.getAction());
        }
        String action = intent.getAction();
        String type = intent.getType();


        String resolveType = intent.resolveType(getContentResolver());


        IntentAction intentAction;
        if (action != null) intentAction = IntentAction.valueOf(action);
        else intentAction = IntentAction.ACTION_OPEN_APP;


        switch (intentAction) {
            case ACTION_COPY_TO_CLIPBOARD:
                copyToClipboard(intent);
                break;

            case ACTION_SEND:
                Timber.d("action send, type = " + type);
                if (type == null) return;
                handleShareText(intent);
                break;

            case ACTION_SHARE_CLIPBOARD:
                openApp(intentAction);
                break;

            case ACTION_STOP_SERVICE:
                ServerService.stopService();
                finish();
                break;

            default:
            case ACTION_OPEN_APP:
                openApp(IntentAction.ACTION_OPEN_APP);
                break;
        }
    }


    private void copyToClipboard(Intent intent) {
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        ClipboardUtils.copyToClipboard(getApplicationContext(), text);
        Toast.makeText(this, "MyApplication.getInstance().getString(R.string.copy_to_clipboard_success)", Toast.LENGTH_LONG).show();

        finish();
    }


    private void handleShareText(Intent intent) {
        Timber.d("sanity check handleShareText");

        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (!MimeTypes.Text.PLAIN.equals(intent.getType())) {
            finish();
            return;
        }
        if (sharedText == null) {
            finish();
            return;
        }

        Intent mainIntent = new Intent(MyApplication.getInstance(), MainActivity.class); //changed from clear_task to clear_top 01.05.2019
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.putExtra(Intent.EXTRA_TEXT, sharedText);
        mainIntent.setType(MimeTypes.Text.PLAIN);
        mainIntent.setAction(IntentAction.ACTION_SEND.getAction());

        //end IntentActivity and then start the Intent
        finish();

        startActivity(mainIntent);
    }


    private void openApp(@NonNull IntentAction intentAction) {
        Timber.d("sanity check open app");

        Intent mainIntent = new Intent(MyApplication.getInstance(), MainActivity.class); //changed from clear_task to clear_top 01.05.2019
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //mainIntent.setType(MimeTypes.Text.PLAIN);
        mainIntent.setAction(intentAction.getAction());

        //end IntentActivity and then start the Intent
        finish();

        startActivity(mainIntent);
    }
}
