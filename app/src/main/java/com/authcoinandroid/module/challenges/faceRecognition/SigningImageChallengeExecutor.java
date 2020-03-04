package com.authcoinandroid.module.challenges.faceRecognition;

import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.ChallengeExecutor;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;

public class SigningImageChallengeExecutor implements ChallengeExecutor {
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
