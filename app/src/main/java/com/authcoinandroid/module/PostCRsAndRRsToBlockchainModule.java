package com.authcoinandroid.module;


import android.util.Log;
import android.util.Pair;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.service.qtum.model.SendRawTransactionResponse;
import com.authcoinandroid.service.wallet.WalletService;

/**
 * "PostCRsAndRRsToBlockchain" module
 * <p>
 * Differences:
 * 1. 48h timelimit isn't implemented (yet)
 */
public class PostCRsAndRRsToBlockchainModule {

    private ChallengeService challengeService;
    private WalletService walletService;

    public PostCRsAndRRsToBlockchainModule(ChallengeService challengeService, WalletService walletService) {
        this.challengeService = challengeService;
        this.walletService = walletService;
    }

    public void post(Pair<ChallengeRecord, ChallengeRecord> challenges, Pair<ChallengeResponseRecord, ChallengeResponseRecord> responses) {
        post(challenges.first);
        post(challenges.second);
        post(responses.first);
        post(responses.second);
    }

    private void post(ChallengeRecord rc) {
        SendRawTransactionResponse transaction = challengeService.saveChallengeToBc(walletService.getReceiveKey(), rc).blockingFirst();
        Log.i("PostCRsAndRRsToBC", "VAE ID:" + rc.getVaeId()+"; challenge Id:"+rc.getId() + " QTUM txId" + transaction.txid);
    }

    private void post(ChallengeResponseRecord rr) {
        SendRawTransactionResponse transaction = challengeService.saveChallengeResponseToBc(walletService.getReceiveKey(), rr).blockingFirst();
        Log.i("PostCRsAndRRsToBC", "VAE" + rr.getVaeId() + " QTUM txId" + transaction.txid);
    }
}
