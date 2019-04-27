package com.authcoinandroid.service.keypair;

import java.security.KeyPair;

/**
 * Interface for managing identity private and public keys.
 */
public interface KeyPairService {

    /**
     * Creates a new key-pair with given alias.
     */
    KeyPair create(String alias);

    /**
     * Returns a KeyPair
     */
    KeyPair get(String alias);

    // TODO remove
}
