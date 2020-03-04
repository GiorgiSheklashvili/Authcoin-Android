package com.authcoinandroid.module;

import android.util.Pair;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.module.messaging.MessageHandler;
import com.authcoinandroid.service.transport.AuthcoinTransport;

public class CreateSendImageResponsesModule {

    private CreateImageResponseModule createImageResponseModule;
    private SendChallengeRecordModule sendChallengeRecordModule;

    public CreateSendImageResponsesModule(AuthcoinTransport transporter, MessageHandler messageHandler) {
        this.createImageResponseModule = new CreateImageResponseModule(messageHandler);
        this.sendChallengeRecordModule = new SendChallengeRecordModule(transporter);
    }

    public Pair<ChallengeResponseRecord, ChallengeResponseRecord> process(
            // the first parameter is target's CR; the second parameter is verifier's CR
            Pair<ChallengeRecord, ChallengeRecord> challenges) {

        ChallengeRecord verifierChallenge = challenges.second;

        ChallengeResponseRecord verifierResponse = createImageResponseModule.process(verifierChallenge);
        ChallengeResponseRecord targetResponse = sendChallengeRecordModule.send(verifierResponse);

        return Pair.create(targetResponse, verifierResponse);
    }
}
