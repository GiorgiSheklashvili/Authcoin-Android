package com.authcoinandroid.module.challenges.signing;

import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.ChallengeExecutor;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;


public class SigningChallengeExecutor implements ChallengeExecutor {

    @Override
    public byte[] execute(byte[] challenge, EntityIdentityRecord eir) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            PrivateKey aPrivate = eir.getKeyPair().getPrivate();
            signature.initSign(aPrivate);
            signature.update(challenge);
            return signature.sign();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Signing failed", e);
        }
    }

}
