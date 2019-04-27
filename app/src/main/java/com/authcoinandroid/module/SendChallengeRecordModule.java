package com.authcoinandroid.module;

import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.service.transport.AuthcoinTransport;

/**
 * Differences:
 * 1. this module does not exist on CPN module
 */
public class SendChallengeRecordModule {

    private AuthcoinTransport transporter;

    public SendChallengeRecordModule(AuthcoinTransport transporter) {
        this.transporter = transporter;
    }

    public ChallengeResponseRecord send(ChallengeResponseRecord verifierResponse) {
        return transporter.send(transporter.getServerInfo().getId(), verifierResponse);
    }
}
