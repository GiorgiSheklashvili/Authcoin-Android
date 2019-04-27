package com.authcoinandroid.module.messaging;

import android.util.Pair;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;

public class UserAuthenticatedMessage implements AuthcoinMessage {

    private final Pair<ChallengeRecord, ChallengeRecord> challenges;

    public UserAuthenticatedMessage(Pair<ChallengeRecord, ChallengeRecord> challenges, Pair<ChallengeResponseRecord, ChallengeResponseRecord> second) {
        this.challenges = challenges;
    }

    public ChallengeRecord getTargetChallengeRecord() {
        return challenges.first;
    }

    public ChallengeRecord getVerifierChallengeRecord() {
        return challenges.second;
    }
}

