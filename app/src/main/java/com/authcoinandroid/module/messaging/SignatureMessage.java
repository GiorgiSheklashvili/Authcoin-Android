package com.authcoinandroid.module.messaging;


import com.authcoinandroid.model.ChallengeResponseRecord;

public class SignatureMessage implements AuthcoinMessage{
    private ChallengeResponseRecord challengeResponse;

    public SignatureMessage(ChallengeResponseRecord challengeResponse) {
        this.challengeResponse = challengeResponse;
    }

    public ChallengeResponseRecord getChallengeResponse() {
        return challengeResponse;
    }
}
