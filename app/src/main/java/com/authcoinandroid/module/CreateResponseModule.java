package com.authcoinandroid.module;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.module.challenges.ChallengeExecutor;
import com.authcoinandroid.module.challenges.Challenges;
import com.authcoinandroid.module.messaging.EvaluateChallengeResponseMessage;
import com.authcoinandroid.module.messaging.EvaluateChallengeMessage;
import com.authcoinandroid.module.messaging.MessageHandler;
import com.authcoinandroid.util.Util;

/**
 * Differences:
 * 1. currently doesn't check if challenge record is processed or noy.
 */
public class CreateResponseModule {

    private final MessageHandler messageHandler;

    public CreateResponseModule(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public ChallengeResponseRecord process(ChallengeRecord verifierChallenge) {
        // 1. is challenge already processed
        //TODO  processed challenge? use challenge service? ignored for now.

        // 2. evaluate challenge
        EvaluateChallengeMessage req = new EvaluateChallengeMessage(verifierChallenge);
        // this is a blocking call
        EvaluateChallengeResponseMessage resp =
                (EvaluateChallengeResponseMessage) messageHandler.sendAndWaitResponse(req, 2);

        // 3 not approved by user
        if (!resp.isApproved()) {
            // TODO send cancel message to the server
            // TODO throw exception
        }

        // 4. fulfill challenge
        ChallengeExecutor executor = Challenges.getExecutor(verifierChallenge.getType());
        byte[] result = executor.execute(verifierChallenge.getChallenge(), verifierChallenge.getTarget());

        // 5. create RR
        return new ChallengeResponseRecord(Util.generateId(), result, verifierChallenge);
    }

}
