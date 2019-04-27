package com.authcoinandroid.util;

import android.content.Context;
import android.widget.Toast;

public class AndroidUtil {
    public static String SHARED_PREFERENCES = "authcoinPreferences";

    public static void displayNotification(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
