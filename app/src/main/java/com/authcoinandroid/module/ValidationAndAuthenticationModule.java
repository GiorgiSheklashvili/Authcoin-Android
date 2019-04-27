package com.authcoinandroid.module;

import android.util.Pair;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.module.messaging.MessageHandler;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.service.transport.AuthcoinTransport;
import com.authcoinandroid.service.wallet.WalletService;

// differences:
// 1. CreateSendChallengeToVerifier is skipped (a verifier challenge is sent as a response to 'sendChallengeToTarget')
// 2. No temporary CRs
class ValidationAndAuthenticationModule {

    private CreateSendChallengeToTargetModule createAndSendChallengeModule;
    private CreateSendResponsesModule createSendResponsesModule;
    private CreateSignaturesModule createSignatureModule;
    private ChallengeService challengeService;
    private PostCRsAndRRsToBlockchainModule postCrAndRrModule;

    public ValidationAndAuthenticationModule(
            MessageHandler messageHandler,
            AuthcoinTransport transporter,
            ChallengeService challengeService,
            WalletService walletService
    ) {
        this.challengeService = challengeService;
        this.createAndSendChallengeModule = new CreateSendChallengeToTargetModule(messageHandler, transporter);
        this.createSendResponsesModule = new CreateSendResponsesModule(transporter, messageHandler);
        this.createSignatureModule = new CreateSignaturesModule(messageHandler, transporter, challengeService, walletService);
        this.postCrAndRrModule = new PostCRsAndRRsToBlockchainModule(challengeService, walletService);
    }

    public Triplet<
            Pair<ChallengeRecord, ChallengeRecord>,
            Pair<ChallengeResponseRecord, ChallengeResponseRecord>,
            Pair<SignatureRecord, SignatureRecord>> process(
            // VAE_ID, verifier EIR, target EIR
            Triplet<byte[], EntityIdentityRecord, EntityIdentityRecord> vae) {

        // 1. create and send challenge to target
        // the first parameter is target's CR; the second parameter is verifier's CR
        Pair<ChallengeRecord, ChallengeRecord> challenges = createAndSendChallengeModule.process(vae);
        challengeService.registerChallenge(challenges.first).blockingGet();
        challengeService.registerChallenge(challenges.second).blockingGet();
        // 2. create challenge to verifier
        // isn't needed. see comments in CreateSendChallengeToTargetModule module

        // 3. CreateSendResponses
        // the first parameter is target's RR; the second parameter is verifier's RR
        Pair<ChallengeResponseRecord, ChallengeResponseRecord> responses = createSendResponsesModule.process(challenges);
        challengeService.registerChallengeResponse(responses.first.getChallenge().getId(), responses.first).blockingGet();
        challengeService.registerChallengeResponse(responses.second.getChallenge().getId(), responses.second).blockingGet();

        // 4. PostCRAndRRsToBlockchain
        postCrAndRrModule.post(challenges, responses);

        // 5. CreateAndPostSignatures
        Pair<SignatureRecord, SignatureRecord> signatures = createSignatureModule.process(responses.first);

//         challengeService.registerSignatureRecord(signatures.first.getChallengeResponse().getChallenge().getId(), signatures.first).blockingGet();
//         challengeService.registerSignatureRecord(signatures.second.getChallengeResponse().getChallenge().getId(), signatures.second).blockingGet();
        return new Triplet<>(challenges, responses, signatures);
    }

}
