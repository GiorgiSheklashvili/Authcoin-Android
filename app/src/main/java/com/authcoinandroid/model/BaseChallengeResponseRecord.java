package com.authcoinandroid.model;

import io.requery.*;

@Entity
public class BaseChallengeResponseRecord {

    @Key
    byte[] id;

    byte[] vaeId;

    @Column(nullable = true)
    Integer blockNumber;

    long timestamp;

    byte[] response;

    byte[] hash;

    byte[] signature;

    @OneToOne(mappedBy = "response")
    ChallengeRecord challenge;

    @ForeignKey(delete = ReferentialAction.CASCADE, update = ReferentialAction.CASCADE)
    @OneToOne
    SignatureRecord signatureRecord;

    public BaseChallengeResponseRecord(byte[] id, byte[] vaeId, int blockNumber, byte[] response, byte[] hash, byte[] signature, ChallengeRecord challenge) {
        this.id = id;
        this.vaeId = vaeId;
        this.blockNumber = blockNumber;
        this.timestamp = System.currentTimeMillis();
        this.response = response;
        this.hash = hash;
        this.signature = signature;
        this.challenge = challenge;
    }

    public BaseChallengeResponseRecord(byte[] id, byte[] response, ChallengeRecord challenge) {
        this.id = id;
        this.vaeId = challenge.getVaeId();
        this.timestamp = System.currentTimeMillis();
        this.response = response;
        // TODO to avoid NULL constraint violations in DB
        this.blockNumber = 1;
        //TODO calculate hash
        this.hash = new byte[32];
        //TODO calculate signature
        this.signature = new byte[128];
        this.challenge = challenge;
    }

    public BaseChallengeResponseRecord() {
        // Don't use this constructor. It is required by requery.
    }

    public SignatureRecord getSignatureRecord() {
        return signatureRecord;
    }

    public void setSignatureRecord(SignatureRecord signatureRecord) {
        this.signatureRecord = signatureRecord;
    }
}
