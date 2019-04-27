package com.authcoinandroid.model;

/**
 * Status of the assets. Defines the assets (EIR, CR, RR, SR) statuses.
 */
public enum AssetBlockChainStatus {

    /**
     * Asset is submitted to the blockchain but it isn't mined.
     */
    SUBMITTED,

    /**
     * Asset is mined.
     */
    MINED,

    /**
     * Asset mining failed
     */
    MINING_FAILED

}
