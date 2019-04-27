package com.authcoinandroid.service.transport;

import com.authcoinandroid.model.SignatureRecord;

public class InternalRestChallengeSignatureRecord {
    private byte[] challengeId;

    private byte[] vaeId;

    private Integer lifespan;

    private boolean revoked;

    private boolean successful;

    private long timestamp;


    public InternalRestChallengeSignatureRecord(SignatureRecord sr) {
        this.challengeId = sr.getChallengeResponse().getChallenge().getId();
        this.vaeId = sr.getVaeId();
        this.lifespan = sr.getExpirationBlock(); // TODO
        this.revoked = sr.isRevoked();
        this.successful = sr.isSuccessful();
        this.timestamp = sr.getTimestamp();
    }

    public byte[] getChallengeId() {
        return challengeId;
    }

    public byte[] getVaeId() {
        return vaeId;
    }

    public Integer getLifespan() {
        return lifespan;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

