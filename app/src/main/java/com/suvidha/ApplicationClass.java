package com.suvidha;

import android.app.Application;

import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: Add OneSignal initialization here
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        String playeriD = getAnyID();

    }

    private String getAnyID() {
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        //String: the OS Player Id or null if device has not registered with OS Servers
        return status.getSubscriptionStatus().getUserId();//String: the OS Player Id or null if device has not registered with OS Servers

    }


}
