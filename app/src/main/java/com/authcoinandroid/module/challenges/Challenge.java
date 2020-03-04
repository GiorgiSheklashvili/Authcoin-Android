package com.authcoinandroid.module.challenges;

import android.content.Context;

public interface Challenge {

    String getType();

    byte[] getContent(Context context);

}
