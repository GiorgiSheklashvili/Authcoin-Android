package com.authcoinandroid.module;

import android.util.Pair;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.module.messaging.MessageHandler;
import com.authcoinandroid.service.transport.AuthcoinTransport;

/**
 * Differences:
 * 1. SendResponse is a separate module
 * 2. SendResponse module returns target's RR
 */
public class CreateSendResponsesModule {

    private CreateResponseModule createResponseModule;
    private SendChallengeRecordModule sendChallengeRecordModule;

    public CreateSendResponsesModule(AuthcoinTransport transporter, MessageHandler messageHandler) {
        this.createResponseModule = new CreateResponseModule(messageHandler);
        this.sendChallengeRecordModule = new SendChallengeRecordModule(transporter);
    }

    public Pair<ChallengeResponseRecord, ChallengeResponseRecord> process(
            // the first parameter is target's CR; the second parameter is verifier's CR
            Pair<ChallengeRecord, ChallengeRecord> challenges) {

        ChallengeRecord verifierChallenge = challenges.second;

        ChallengeResponseRecord verifierResponse = createResponseModule.process(verifierChallenge);
        ChallengeResponseRecord targetResponse = sendChallengeRecordModule.send(verifierResponse);

        return Pair.create(targetResponse, verifierResponse);
    }

}
