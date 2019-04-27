package com.authcoinandroid.model;

import io.requery.Entity;
import io.requery.Key;
import io.requery.OneToOne;

@Entity
public class BaseSignatureRecord {

    @Key
    byte[] id;

    byte[] vaeId;

    Integer blockNumber;

    Integer expirationBlock;

    boolean revoked;

    boolean successful;

    long timestamp;

    byte[] hash;

    byte[] signature;

    @OneToOne(mappedBy = "signatureRecord")
    ChallengeResponseRecord challengeResponse;

    public BaseSignatureRecord(byte[] id, byte[] vaeId, Integer blockNumber, Integer expirationBlock, boolean revoked, boolean successful, byte[] hash, byte[] signature, ChallengeResponseRecord challengeResponse) {
        this.id = id;
        this.vaeId = vaeId;
        this.blockNumber = blockNumber;
        this.expirationBlock = expirationBlock;
        this.revoked = revoked;
        this.successful = successful;
        this.timestamp = System.currentTimeMillis();
        this.hash = hash;
        this.signature = signature;
        this.challengeResponse = challengeResponse;
    }

    public BaseSignatureRecord(byte[] id, Integer blockNumber, Integer expirationBlock, boolean successful,
                               ChallengeResponseRecord challengeResponse) {
        // TODO calculate hash and signature
        this(id, challengeResponse.getVaeId(), blockNumber, expirationBlock, false, successful, new byte[32], new byte[128],
                challengeResponse);
    }


    public BaseSignatureRecord() {
        // Don't use this constructor. It is required by requery.
    }
}
