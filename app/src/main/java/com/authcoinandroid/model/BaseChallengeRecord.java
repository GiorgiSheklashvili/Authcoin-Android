package com.authcoinandroid.model;

import io.requery.*;

@Entity
public class BaseChallengeRecord {

    @Key
    byte[] id;

    byte[] vaeId;

    long timestamp;

    String type;

    byte[] challenge;

    @ManyToOne
    EntityIdentityRecord verifier;

    @ManyToOne
    EntityIdentityRecord target;

    @ForeignKey(delete = ReferentialAction.CASCADE, update = ReferentialAction.CASCADE)
    @OneToOne
    ChallengeResponseRecord response;

    byte[] hash;
    byte[] signature;

    public BaseChallengeRecord(byte[] id, byte[] vaeId, String type, byte[] challenge, EntityIdentityRecord verifier, EntityIdentityRecord target) {
        this.id = id;
        this.vaeId = vaeId;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.challenge = challenge;
        this.verifier = verifier;
        this.target = target;
        this.hash = new byte[32];
        this.signature = new byte[128];
    }

    public BaseChallengeRecord() {
        // Don't use this constructor. It is required by requery.
    }
}
