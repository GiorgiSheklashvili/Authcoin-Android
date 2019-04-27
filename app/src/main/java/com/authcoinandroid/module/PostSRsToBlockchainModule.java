package com.authcoinandroid.module;

import android.util.Pair;

import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.service.wallet.WalletService;

class PostSRsToBlockchainModule {

    private ChallengeService challengeService;
    private WalletService walletService;

    public PostSRsToBlockchainModule(ChallengeService challengeService, WalletService walletService) {
        this.challengeService = challengeService;
        this.walletService = walletService;
    }

    public void post(Pair<SignatureRecord, SignatureRecord> signatures) {
        post(signatures.first);
        post(signatures.second);
    }

    private void post(SignatureRecord sr) {
        challengeService.saveSignatureRecordToBc(walletService.getReceiveKey(), sr).blockingFirst();
    }
}
