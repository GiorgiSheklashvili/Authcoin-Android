package com.authcoinandroid.module;

import com.authcoinandroid.model.EntityIdentityRecord;

/**
 * Interface for "Formal Validation" module.
 *
 * Differences:
 * 1. VAE_ID isn't part of input
 * 2. output is true (validation was successful) or false (validation was unsuccessful)
 */
public interface FormalValidationModule {

    boolean verify(EntityIdentityRecord target, EntityIdentityRecord verifier);
}

