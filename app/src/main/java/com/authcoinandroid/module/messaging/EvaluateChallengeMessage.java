package com.authcoinandroid.module.messaging;


import com.authcoinandroid.model.ChallengeRecord;

public class EvaluateChallengeMessage implements AuthcoinMessage {

    private ChallengeRecord challenge;

    public EvaluateChallengeMessage(ChallengeRecord challenge) {
        this.challenge = challenge;
    }

    public ChallengeRecord getChallenge() {
        return challenge;
    }
}
