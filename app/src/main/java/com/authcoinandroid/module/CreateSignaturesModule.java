package com.authcoinandroid.module;

import android.util.Pair;

import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.module.messaging.MessageHandler;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.service.transport.AuthcoinTransport;
import com.authcoinandroid.service.wallet.WalletService;

public class CreateSignaturesModule {

    private CreateSignaturesFromRRModule createSignaturesModule;

    public CreateSignaturesModule(
            MessageHandler messageHandler,
            AuthcoinTransport transporter,
            ChallengeService challengeService,
            WalletService walletService
            ) {
        this.createSignaturesModule = new CreateSignaturesFromRRModule(messageHandler, transporter, challengeService, walletService);
    }

    public Pair<SignatureRecord, SignatureRecord> process(ChallengeResponseRecord response) {
        return createSignaturesModule.process(response);
    }
}
