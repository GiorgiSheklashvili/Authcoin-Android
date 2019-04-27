package com.authcoinandroid.module.messaging;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.module.EcKeyFormalValidationModule;
import com.authcoinandroid.module.Triplet;
import com.authcoinandroid.module.ValidationAndAuthenticationProcessingModule;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.service.transport.HttpRestAuthcoinTransport;
import com.authcoinandroid.service.wallet.WalletService;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Starts a V&A processing module in another thread.
 */
public class VAProcessRunnable implements Runnable {

    // NB! should not be public. DON'T USE IT IN PRODUCTION!
    public static Queue<AuthcoinMessage> queue = new LinkedList<>();
    // NB! should not be public. DON'T USE IT IN PRODUCTION!
    public static Object lock = new Object();
    private final EntityIdentityRecord verifier;
    private final EntityIdentityRecord target;
    private final WalletService walletService;

    private MessageHandler messageHandler;
    private HttpRestAuthcoinTransport transporter;
    private ChallengeService challengeService;

    public VAProcessRunnable(
            Handler mainHandler,
            EntityIdentityRecord verifier,
            EntityIdentityRecord target,
            HttpRestAuthcoinTransport transporter,
            ChallengeService challengeService,
            WalletService walletService
    ) {
        this.messageHandler = new MessageHandler(mainHandler);
        this.verifier = verifier;
        this.target = target;
        this.transporter = transporter;
        this.challengeService = challengeService;
        this.walletService = walletService;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        Log.i("VAProcessRunnable", "start V&A process: " + Thread.currentThread().getName());
        ValidationAndAuthenticationProcessingModule t = new ValidationAndAuthenticationProcessingModule(
                messageHandler,
                new EcKeyFormalValidationModule(),
                transporter,
                challengeService,
                walletService
        );
        Triplet<Pair<ChallengeRecord, ChallengeRecord>, Pair<ChallengeResponseRecord, ChallengeResponseRecord>, Pair<SignatureRecord, SignatureRecord>> resp = t.process(target, verifier);
        messageHandler.send(new UserAuthenticatedMessage(resp.getFirst(), resp.getSecond()), 10);
        Log.i("VAProcessRunnable", "end V&A process. result: " + resp + " " + Thread.currentThread().getName());
    }

}
