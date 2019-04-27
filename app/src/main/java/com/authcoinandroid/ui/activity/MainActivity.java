package com.authcoinandroid.ui.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.authcoinandroid.R;
import com.authcoinandroid.ui.fragment.ChallengeFragment;
import com.authcoinandroid.ui.fragment.IdentityFragment;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide activity content when focus is lost
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (item -> {
                    Class selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.action_identity:
                            selectedFragment = IdentityFragment.class;
                            Log.d(LOG_TAG, "User opened identity fragment");
                            break;
                        case R.id.action_challenges:
                            selectedFragment = ChallengeFragment.class;
                            Log.d(LOG_TAG, "User opened challenges fragment");
                            break;
                    }

                    //Prevent spamming current open fragment
                    Class currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass();
                    if (!currentFragment.equals(selectedFragment)) {
                        applyFragment(selectedFragment, false, false);
                        return true;
                    } else {
                        return false;
                    }
                });

        applyFragment(IdentityFragment.class, false, false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Finish to provoke UnlockWithPinActivity
        finish();
    }

    public void applyFragment(@NonNull Class fragmentClass, boolean addToBackStack, boolean hideNavigation) {
        Fragment fragment;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);

            if (addToBackStack) {
                transaction.addToBackStack(null);
            }

            if (hideNavigation) {
                hideBottomNavigation();
            } else {
                findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyFullFragmentWithBundle(@NonNull Class fragmentClass, Bundle bundle) {
        Fragment fragment;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            hideBottomNavigation();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideBottomNavigation() {
        // Set visibility to GONE after INVISIBLE,
        // since setting straight to INVISIBLE will cause other components
        // like the FloatingActionButton to jump down before switching fragments.
        View bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setVisibility(View.INVISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(() -> bottomNav.setVisibility(View.GONE), 250);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Hide keyboard and lose focus on EditText when clicking anywhere outside
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
