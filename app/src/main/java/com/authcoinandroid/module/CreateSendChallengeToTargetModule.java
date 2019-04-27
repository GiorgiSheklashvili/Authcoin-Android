package com.authcoinandroid.module;

import android.util.Pair;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.module.challenges.Challenges;
import com.authcoinandroid.module.messaging.ChallengeTypeMessageResponse;
import com.authcoinandroid.module.messaging.MessageHandler;
import com.authcoinandroid.module.messaging.RequestChallengeTypeMessage;
import com.authcoinandroid.service.transport.AuthcoinTransport;
import com.authcoinandroid.util.Util;

/**
 * “CreateSendChallengeToTarget” module
 * <p>
 * Differences:
 * 1. SendChallengeToTarget returns verifier's CR.
 */
class CreateSendChallengeToTargetModule {

    private CreateChallengeForTarget challengeCreator;
    private SendChallengeToTarget challengeSender;

    public CreateSendChallengeToTargetModule(MessageHandler messageHandler, AuthcoinTransport transporter) {
        this.challengeCreator = new CreateChallengeForTarget(messageHandler);
        this.challengeSender = new SendChallengeToTarget(transporter);
    }

    public Pair<ChallengeRecord, ChallengeRecord> process(Triplet<byte[], EntityIdentityRecord, EntityIdentityRecord> vae) {
        // 1. create challenge
        ChallengeRecord crForTarget = challengeCreator.create(vae);
        // 2. send challenge
        ChallengeRecord crForVerifier = challengeSender.send(crForTarget);
        // NB! In CPN modules only target challenge record is returned.
        return Pair.create(crForTarget, crForVerifier);
    }

    /**
     * “CreateSendChallengeToTarget” module
     * <p>
     * Differences:
     * 1. TODO
     */
    class CreateChallengeForTarget {

        private final MessageHandler messageHandler;

        public CreateChallengeForTarget(MessageHandler messageHandler) {
            this.messageHandler = messageHandler;
        }

        public ChallengeRecord create(Triplet<byte[], EntityIdentityRecord, EntityIdentityRecord> vae) {
            // Communication with UI.  Challenge Type
            RequestChallengeTypeMessage req = new RequestChallengeTypeMessage();
            ChallengeTypeMessageResponse resp =
                    (ChallengeTypeMessageResponse) messageHandler.sendAndWaitResponse(req, 1);

            //  1. create challenge
            Challenge challenge = Challenges.get(resp.getChallengeType());
            byte[] crId = Util.generateId();
            return new ChallengeRecord(crId, vae.getFirst(), challenge.getType(), challenge.getContent(), vae.getSecond(), vae.getThird());
        }
    }

    /**
     * “SendChallengeToTarget” module
     * <p>
     * Differences:
     * 1. Very simple implementation. Almost completely ignores CPN module. Sorry.
     */
    class SendChallengeToTarget {

        private final AuthcoinTransport transporter;

        public SendChallengeToTarget(AuthcoinTransport transporter) {
            this.transporter = transporter;
        }

        public ChallengeRecord send(ChallengeRecord record) {
            return transporter.send(transporter.getServerInfo().getId(), record);
        }

    }

}
