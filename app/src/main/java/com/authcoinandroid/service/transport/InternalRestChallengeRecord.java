package com.authcoinandroid.service.transport;

import com.authcoinandroid.model.ChallengeRecord;

class InternalRestChallengeRecord {

    private byte[] id;
    private byte[] vaeId;
    private long timestamp;
    private String type;
    private byte[] challenge;
    private byte[] verifier;
    private byte[] target;

    public InternalRestChallengeRecord(ChallengeRecord cr) {
        this.id = cr.getId();
        this.vaeId = cr.getVaeId();
        this.timestamp = cr.getTimestamp();
        this.type = cr.getType();
        this.challenge = cr.getChallenge();
        this.verifier = cr.getVerifier().getId();
        this.target = cr.getTarget().getId();
    }

    public byte[] getId() {
        return id;
    }

    public byte[] getVaeId() {
        return vaeId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public byte[] getChallenge() {
        return challenge;
    }

    public byte[] getVerifier() {
        return verifier;
    }

    public byte[] getTarget() {
        return target;
    }
}
