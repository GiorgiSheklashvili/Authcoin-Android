package com.authcoinandroid.service.transport;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;

import java.util.UUID;

/**
 * Interface for different challenge related transport mechanisms.
 */
public interface AuthcoinTransport {

    /**
     * Sends CR to target and expects it to return verifier's CR.
     *
     * @param record target CR.
     * @return verifier's CR
     */
    ChallengeRecord send(UUID registrationId, ChallengeRecord record);

    /**
     * Sends RR to target and expects it to return target's RR.
     *
     * @param verifierResponse verifier CR.
     * @return target's CR
     */
    ChallengeResponseRecord send(UUID registrationId, ChallengeResponseRecord verifierResponse);

    /**
     * Sends SR to target and expects it to return target's SR.
     *
     * @param sr verifier SR.
     * @return target's SR
     */
    SignatureRecord send(UUID registrationId, SignatureRecord sr);

    ServerInfo getServerInfo();
}
