package com.authcoinandroid.util;

import android.view.View;
import butterknife.ButterKnife;

public final class ButterKnifeUtil {

    public static final ButterKnife.Action<View> SET_INVISIBLE = (view, index) -> view.setVisibility(View.INVISIBLE);

    private ButterKnifeUtil() {
    }
}
