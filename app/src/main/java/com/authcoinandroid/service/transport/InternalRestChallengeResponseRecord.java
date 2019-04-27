package com.authcoinandroid.service.transport;


import com.authcoinandroid.model.ChallengeResponseRecord;

class InternalRestChallengeResponseRecord {

    private byte[] challengeId;

    private byte[] vaeId;

    private long timestamp;

    private byte[] response;

    public InternalRestChallengeResponseRecord(ChallengeResponseRecord rr) {
        this.challengeId = rr.getChallenge().getId();
        this.vaeId = rr.getVaeId();
        this.timestamp = rr.getTimestamp();
        this.response = rr.getResponse();
    }

    public byte[] getChallengeId() {
        return challengeId;
    }

    public byte[] getVaeId() {
        return vaeId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getResponse() {
        return response;
    }

}
