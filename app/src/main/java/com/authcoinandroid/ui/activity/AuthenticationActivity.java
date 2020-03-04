package com.authcoinandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.graphics.Rect;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.authcoinandroid.R;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.messaging.*;
import com.authcoinandroid.service.challenge.ChallengeServiceImpl;
import com.authcoinandroid.service.transport.HttpRestAuthcoinTransport;
import com.authcoinandroid.service.transport.ServerInfo;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.ui.fragment.authentication.*;

import java.util.UUID;

public class AuthenticationActivity extends AppCompatActivity {

    private Handler mainThreadHandler;
    private Handler vaThreadHandler;
    private String challengeType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        Boolean isUnlocked = getIntent().getBooleanExtra("isUnlocked", false);
        if (!isUnlocked) {
            Intent unlockIntent = new Intent(getApplicationContext(), UnlockWithPinActivity.class);
            unlockIntent.putExtra("isAuthenticationProcess", true);
            unlockIntent.setData(getIntent().getData());
            startActivity(unlockIntent);
            finish();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Uri uri = getIntent().getData();

        String demoServer = uri.getQueryParameter("serverUrl");
        String serverSessionId = uri.getQueryParameter("sessionId");
        String serverEir = uri.getQueryParameter("serverEir");
        String appName = uri.getQueryParameter("appName");

        ChallengeServiceImpl challengeService = ((AuthCoinApplication) getApplication()).getChallengeService();
        ServerInfo serverInfo = new ServerInfo(serverEir, UUID.fromString(serverSessionId), appName);
        HttpRestAuthcoinTransport transport =
                new HttpRestAuthcoinTransport(demoServer, serverInfo, challengeService);
        EirSelectorFragment eirSelectionFragment = new EirSelectorFragment();
        eirSelectionFragment.setNextButtonListener(v -> {
            EntityIdentityRecord selectedEir = eirSelectionFragment.getSelectedEir();

            new Thread(() -> {
                EntityIdentityRecord target = ((AuthCoinApplication) getApplication()).getIdentityService().getEirById(serverEir).blockingSingle();
                ((AuthCoinApplication) getApplication()).getEirRepository().save(target).blockingGet();
                vaThreadHandler.post(
                        new VAProcessRunnable(
                                mainThreadHandler,
                                selectedEir,
                                target,
                                transport,
                                challengeService,
                                ((AuthCoinApplication) getApplication()).getWalletService(),
                                getApplicationContext()
                        )
                );
            }).start();
        });

        transaction.replace(R.id.container, eirSelectionFragment).commit();

        HandlerThread handlerThread = new HandlerThread("VA");
        handlerThread.start();

        mainThreadHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        // challenge type
                        ChallengeTypeSelectorFragment selectionFragment = new ChallengeTypeSelectorFragment();
                        selectionFragment.setSelectChallengeButtonListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new ChallengeTypeMessageResponse(selectionFragment.getSelectedChallenge()));
                                        VAProcessRunnable.lock.notify();
                                        challengeType = selectionFragment.getSelectedChallenge();
                                    }
                                }
                        );
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, selectionFragment).commit();
                        break;

                    case 2:
                        // evaluate target's challenge sent to verifier
                        EvaluateChallengeFragment evaluateChallengeFragment = new EvaluateChallengeFragment();
                        evaluateChallengeFragment.setChallengeRecord(((EvaluateChallengeMessage) msg.obj).getChallenge());
                        evaluateChallengeFragment.setApproveButtonListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new EvaluateChallengeResponseMessage(evaluateChallengeFragment.isApproved()));
                                        VAProcessRunnable.lock.notify();
                                    }
                                }
                        );
                        startFragment(evaluateChallengeFragment);
                        break;
                    case 3:
                        SignatureFragment signatureFragment = new SignatureFragment();
                        signatureFragment.setChallengeResponse(((SignatureMessage) msg.obj).getChallengeResponse());
                        signatureFragment.setApproveSignatureListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new SignatureResponseMessage(0, true));
                                        VAProcessRunnable.lock.notify();
                                    }
                                }
                        );
                        Bundle bundle = new Bundle();
                        bundle.putString("challengeType", challengeType);
                        signatureFragment.setArguments(bundle);
                        startFragment(signatureFragment);
                        break;
                    case 4:
                        CameraImage cameraImage = new CameraImage();
                        cameraImage.setSendButtonListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new EvaluateChallengeResponseMessage(true)); //image approved
                                        VAProcessRunnable.lock.notify();
                                    }
                                }
                        );
                        startFragment(cameraImage);
                        break;
                    case 5:
                        signatureFragment = new SignatureFragment();
                        signatureFragment.setChallengeResponse(((SignatureMessage) msg.obj).getChallengeResponse());
                        signatureFragment.setApproveSignatureListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new SignatureResponseMessage(signatureFragment.getLifespan(), true));
                                        VAProcessRunnable.lock.notify();
                                    }
                                }
                        );
                        startFragment(signatureFragment);
                        break;
                    case 10:
                        AuthenticationSuccessfulFragment asf = new AuthenticationSuccessfulFragment();
                        asf.setResult(((UserAuthenticatedMessage) msg.obj));
                        startFragment(asf);
                        break;
                }
            }
        };

        vaThreadHandler = new Handler(handlerThread.getLooper());
    }

    private void startFragment(Fragment f) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, f).commit();
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
