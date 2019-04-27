package com.authcoinandroid.module.challenges;

import com.authcoinandroid.model.EntityIdentityRecord;


public interface ChallengeVerifier {

    boolean verify(EntityIdentityRecord eir, byte[] challenge, byte[] fulFilledChallenge);

}
