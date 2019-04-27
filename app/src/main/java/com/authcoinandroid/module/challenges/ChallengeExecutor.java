package com.authcoinandroid.module.challenges;

import com.authcoinandroid.model.EntityIdentityRecord;

public interface ChallengeExecutor {

    byte[] execute(byte[] challenge, EntityIdentityRecord eir);

}
