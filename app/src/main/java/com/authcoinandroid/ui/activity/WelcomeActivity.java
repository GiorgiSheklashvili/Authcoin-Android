package com.authcoinandroid.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.authcoinandroid.R;
import com.authcoinandroid.task.WalletCreationTask;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.util.AndroidUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {
    private final static String LOG_TAG = "WelcomeActivity";

    @BindView(R.id.et_wallet_password)
    EditText walletPassword;

    @BindView(R.id.et_wallet_password_confirm)
    EditText walletPasswordConfirm;

    @BindView(R.id.pb_wallet_creation)
    ProgressBar progressBar;

    @BindView(R.id.btn_create_wallet)
    Button createWalletButton;

    @OnClick({R.id.btn_create_wallet})
    void onCreateWalletClick(View view) {
        if (validatePasswords()) {
            disableElements();
            String password = walletPassword.getText().toString();
            new WalletCreationTask(getApplicationContext(), password, result -> {
                if (result.getError() != null) {
                    enableElements();
                    AndroidUtil.displayNotification(getApplicationContext(), "Failed to create wallet");
                } else {
                    Log.d(LOG_TAG, "Created wallet, mnemonic code: " + result.getResult().getKeyChainSeed().getMnemonicCode());
                    AndroidUtil.displayNotification(getApplicationContext(), "Wallet created");

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            },
                    ((AuthCoinApplication) getApplication()).getWalletService()).execute();
        }
    }

    private boolean validatePasswords() {
        String password = walletPassword.getText().toString();
        String passwordConfirm = walletPasswordConfirm.getText().toString();

        if (!password.equals(passwordConfirm)) {
            Log.d(LOG_TAG, "Passwords don't match");
            walletPasswordConfirm.setError("Passwords don't match");
            return false;
        }

        return true;
    }

    private void disableElements() {
        progressBar.setVisibility(View.VISIBLE);
        createWalletButton.setEnabled(false);
        createWalletButton.setVisibility(View.INVISIBLE);
        walletPassword.setEnabled(false);
        walletPasswordConfirm.setEnabled(false);
    }

    private void enableElements() {
        progressBar.setVisibility(View.INVISIBLE);
        createWalletButton.setVisibility(View.VISIBLE);
        createWalletButton.setEnabled(true);
        walletPassword.setEnabled(true);
        walletPasswordConfirm.setEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Hide activity content when focus is lost
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        ButterKnife.bind(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Finish to provoke UnlockWithPinActivity
        finish();
    }
}
