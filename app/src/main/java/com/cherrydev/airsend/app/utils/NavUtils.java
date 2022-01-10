package com.cherrydev.airsend.app.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.connections.FragmentConnections;
import com.cherrydev.airsend.app.messages.FragmentMessages;
import com.cherrydev.airsend.app.settings.FragmentSettings;

public class NavUtils {
    private FragmentManager fragmentManager;
    private int contentID = R.id.container;
    private int in = R.animator.scale_in, out = R.animator.scale_out;

    private NavigateCallback navigateCallback;

    public NavUtils(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public enum Tag {
        MESSAGES, CONNECTIONS, SETTINGS
    }

    @NonNull
    private Fragment getFragment(Tag tag) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag.name());

        if (fragment == null) {
            //Timber.i("getFragment " + tag + " : NULL after lookup in fm");
            fragment = initFragment(tag);
        }

        return fragment;
    }


    private Fragment initFragment(Tag tag) {
        Fragment fragment;
        switch (tag) {

            default:
            case MESSAGES:
                fragment = FragmentMessages.newInstance();
                break;
            case CONNECTIONS:
                fragment = FragmentConnections.newInstance();
                break;
            case SETTINGS:
                fragment = FragmentSettings.newInstance();
                break;

        }
        return fragment;
    }

    public void navigate(Tag to) {
        Fragment fragment = getFragment(to);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction
                .setCustomAnimations(in, out, in, out)
                .replace(R.id.container, fragment, to.name())
                .commit();

        if (navigateCallback != null) navigateCallback.onNavigate(to);
    }

    public void setNavigateCallback(NavigateCallback navigateCallback) {
        this.navigateCallback = navigateCallback;
    }

    public interface NavigateCallback {
        void onNavigate(Tag to);
    }
}
