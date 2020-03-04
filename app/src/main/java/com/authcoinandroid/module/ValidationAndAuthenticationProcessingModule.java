package com.authcoinandroid.module;

import android.content.Context;
import android.util.Pair;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.module.messaging.MessageHandler;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.service.transport.AuthcoinTransport;
import com.authcoinandroid.service.wallet.WalletService;
import com.authcoinandroid.util.Util;

import java.util.Arrays;

import static com.authcoinandroid.util.Util.notNull;

/**
 * "V&A-Processing" module.
 * <p>
 * Differences:
 * 1. VAE_ID counter is replaced by java.util.UUID.randomUUID()
 * 2. If something fails an exception is thrown
 */
public class ValidationAndAuthenticationProcessingModule extends AbstractModule {

    private FormalValidationModule formalValidationModule;
    private ValidationAndAuthenticationModule vaModule;

    public ValidationAndAuthenticationProcessingModule(
            MessageHandler messageHandler,
            FormalValidationModule formalValidationModule,
            AuthcoinTransport transporter,
            ChallengeService challengeService,
            WalletService walletService) {
        super(messageHandler);
        notNull(formalValidationModule, "Formal validation module");
        notNull(transporter, "AuthcoinTransport");
        this.formalValidationModule = formalValidationModule;
        this.vaModule = new ValidationAndAuthenticationModule(messageHandler, transporter, challengeService, walletService);
    }

    public Triplet<
            Pair<ChallengeRecord, ChallengeRecord>,
            Pair<ChallengeResponseRecord, ChallengeResponseRecord>,
            Pair<SignatureRecord, SignatureRecord>> process(
            EntityIdentityRecord target,
            EntityIdentityRecord verifier, Context context) {

        // 1. prepare verifier and target for processing. generate VAE id.
        notNull(target, "Target EIR");
        notNull(verifier, "Verifier EIR");
        if (Arrays.equals(target.getId(), verifier.getId())) {
            throw new IllegalArgumentException("target EIR == verifier EIR");
        }
        byte[] vaeId = Util.generateId();
        Triplet<byte[], EntityIdentityRecord, EntityIdentityRecord> vae =
                new Triplet<>(vaeId, verifier, target);

        // 2. formal validation. CPN uses VAE as input.
        boolean success = formalValidationModule.verify(target, verifier);
        if (!success) {
            throw new IllegalStateException("formal validation failed");
        }

        // 3. VA module
        return vaModule.process(vae, context);
    }

}
