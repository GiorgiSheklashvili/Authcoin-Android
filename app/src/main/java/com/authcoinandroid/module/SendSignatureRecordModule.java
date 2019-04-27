package com.authcoinandroid.module;

import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.service.transport.AuthcoinTransport;

/**
 * Differences:
 * 1. this module does not exist on CPN module
 */
public class SendSignatureRecordModule {

    private AuthcoinTransport transporter;

    public SendSignatureRecordModule(AuthcoinTransport transporter) {
        this.transporter = transporter;
    }

    public SignatureRecord send(SignatureRecord sr) {
        return transporter.send(transporter.getServerInfo().getId(), sr);
    }
}
