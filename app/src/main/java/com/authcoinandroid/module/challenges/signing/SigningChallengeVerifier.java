package com.authcoinandroid.module.challenges.signing;

import android.util.Log;

import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.ChallengeVerifier;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class SigningChallengeVerifier implements ChallengeVerifier {

    public boolean verify(EntityIdentityRecord eir, byte[] challenge, byte[] fulFilledChallenge) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider.PROVIDER_NAME);
            PublicKey publicKey = eir.getPublicKey();
            PublicKey newPubKey = KeyFactory.getInstance(publicKey.getAlgorithm(), BouncyCastleProvider.PROVIDER_NAME).generatePublic(
                    new X509EncodedKeySpec(publicKey.getEncoded()));
            signature.initVerify(newPubKey);
            signature.update(challenge);
            return signature.verify(fulFilledChallenge);
        } catch (GeneralSecurityException e) {
            Log.i("SignatureVerifier", "Signature verification failed", e);
            return false;
        }
    }

}
