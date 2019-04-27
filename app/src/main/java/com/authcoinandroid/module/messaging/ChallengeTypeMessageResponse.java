package com.authcoinandroid.module.messaging;

public class ChallengeTypeMessageResponse implements AuthcoinMessage {

    private String challengeType;

    public ChallengeTypeMessageResponse(String challengeType) {
        this.challengeType = challengeType;
    }

    public String getChallengeType() {
        return challengeType;
    }
}
