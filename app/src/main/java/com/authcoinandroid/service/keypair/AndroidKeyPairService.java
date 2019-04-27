package com.authcoinandroid.service.keypair;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.authcoinandroid.util.crypto.CryptoUtil;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Android based {@link KeyPairService}
 */
public class AndroidKeyPairService implements KeyPairService {

    private static final String PROVIDER_ANDROID_KEY_STORE = "AndroidKeyStore";

    @Override
    public KeyPair create(String alias) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, PROVIDER_ANDROID_KEY_STORE);
            kpg.initialize(
                    new KeyGenParameterSpec.Builder(
                            alias,
                            KeyProperties.PURPOSE_SIGN)
                            .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                            .setDigests(KeyProperties.DIGEST_SHA256)
                            .setUserAuthenticationRequired(false)
                            //.setUserAuthenticationValidityDurationSeconds(5 * 60)
                            .build());
            KeyPair keyPair = kpg.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PublicKey unrestrictedPublicKey =
                    KeyFactory.getInstance(publicKey.getAlgorithm()).generatePublic(
                            new X509EncodedKeySpec(publicKey.getEncoded()));

            return new KeyPair(unrestrictedPublicKey, keyPair.getPrivate());
        } catch (GeneralSecurityException e) {
            throw new KeyPairException("KeyPair generation failed", e);
        }
    }

    @Override
    public KeyPair get(String alias) {
        return CryptoUtil.getKeyPair(alias);
    }

}
