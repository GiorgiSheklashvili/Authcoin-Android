package com.authcoinandroid.module;

import com.authcoinandroid.model.EntityIdentityRecord;

/**
 * EC key formal validation module.
 */
public class EcKeyFormalValidationModule implements FormalValidationModule{

    public boolean verify(EntityIdentityRecord target, EntityIdentityRecord verifier) {
        return !target.isRevoked() && !verifier.isRevoked();
    }

}
