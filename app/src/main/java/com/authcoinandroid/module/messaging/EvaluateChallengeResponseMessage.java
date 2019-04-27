package com.authcoinandroid.module.messaging;


public class EvaluateChallengeResponseMessage implements  AuthcoinMessage{
    private final boolean approved;

    public EvaluateChallengeResponseMessage(boolean approved) {
        this.approved = approved;
    }

    public boolean isApproved() {
        return approved;
    }
}
