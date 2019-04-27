package com.authcoinandroid.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.authcoinandroid.R;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.util.AndroidUtil;

import static com.authcoinandroid.util.AndroidUtil.SHARED_PREFERENCES;

public class UnlockWithPinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_with_pin);

        // Hide activity content when focus is lost
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        String storedPin = preferences.getString("pin", "");

        PinLockView mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        IndicatorDots mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                if (pin.equals(storedPin)) {
                    Boolean isWalletCreated = ((AuthCoinApplication) getApplication())
                            .getWalletService()
                            .isWalletCreated(getApplicationContext());

                    Intent intent = new Intent(getApplicationContext(), isWalletCreated
                            ? MainActivity.class
                            : WelcomeActivity.class);

                    if (getIntent().getBooleanExtra("isAuthenticationProcess", false)) {
                        intent.setClass(getApplicationContext(), AuthenticationActivity.class);
                        intent.putExtra("isUnlocked", true);
                        intent.setData(getIntent().getData());
                    }

                    startActivity(intent);
                    finish();
                } else {
                    mPinLockView.resetPinLockView();
                    AndroidUtil.displayNotification(getApplicationContext(), "Incorrect PIN!");
                }
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        });
    }
}
