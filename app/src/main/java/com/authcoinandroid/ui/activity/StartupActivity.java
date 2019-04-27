package com.authcoinandroid.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.authcoinandroid.R;

import static com.authcoinandroid.util.AndroidUtil.SHARED_PREFERENCES;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
            String pin = preferences.getString("pin", "");
            Intent intent = new Intent(getApplicationContext(),
                    pin.equals("")
                            ? CreatePinActivity.class
                            : UnlockWithPinActivity.class);
            startActivity(intent);
            finish();
        }, 0);
    }
}
