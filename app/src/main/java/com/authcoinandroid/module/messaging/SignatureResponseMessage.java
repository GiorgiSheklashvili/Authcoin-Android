package com.authcoinandroid.module.messaging;


public class SignatureResponseMessage implements AuthcoinMessage {
    private int lifespan;
    private boolean satisfied;

    public SignatureResponseMessage(int lifespan, boolean satisfied) {
        this.lifespan = lifespan;
        this.satisfied = satisfied;
    }

    public int getLifespan() {
        return lifespan;
    }

    public boolean isSatisfied() {
        return satisfied;
    }
}
