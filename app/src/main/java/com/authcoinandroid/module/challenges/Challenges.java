package com.authcoinandroid.module.challenges;

import android.content.Context;

import com.authcoinandroid.module.challenges.faceRecognition.SigningImageChallengeExecutor;
import com.authcoinandroid.module.challenges.faceRecognition.SigningImageChallengeFactory;
import com.authcoinandroid.module.challenges.faceRecognition.SigningImageChallengeVerifier;
import com.authcoinandroid.module.challenges.signing.SigningChallengeExecutor;
import com.authcoinandroid.module.challenges.signing.SigningChallengeFactory;
import com.authcoinandroid.module.challenges.signing.SigningChallengeVerifier;
import com.authcoinandroid.module.Triplet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Singleton that holds {@link ChallengeFactory} instances.
 * NB! This class isn't thread safe.
 */
public final class Challenges {

    private static Map<String, Triplet<ChallengeFactory, ChallengeExecutor, ChallengeVerifier>> factories = new HashMap<>();

    static {
        factories.put(
                ChallengeType.SIGN_CONTENT.getValue(),
                new Triplet<>(
                        new SigningChallengeFactory(),
                        new SigningChallengeExecutor(),
                        new SigningChallengeVerifier()
                )

        );
        factories.put(
                ChallengeType.MFA_SIGN_CONTENT_AND_FACIAL_RECOGNITION.getValue(),
                new Triplet<>(
                        new SigningImageChallengeFactory(),
                        new SigningImageChallengeExecutor(),
                        new SigningImageChallengeVerifier()
                )

        );
        // TODO add more challenges :)

    }

    private Challenges() {
        // Singleton
    }
    public static Set<String> getAllTypes() {
        return factories.keySet();
    }

    public static void add(String type, ChallengeFactory factory, ChallengeExecutor executor, ChallengeVerifier verifier) {
        if (factories.containsKey(type)) {
            throw new IllegalArgumentException("Challenge Factory already registered");
        }
        factories.put(type, new Triplet<>(factory, executor, verifier));
    }

    public static Challenge get(String type) {
        ChallengeFactory factory = factories.get(type).getFirst();
        if (factory == null) {
            throw new IllegalArgumentException("Unknown challenge type");
        }
        return factory.create();
    }

    public static ChallengeExecutor getExecutor(String type) {
        ChallengeExecutor executor = factories.get(type).getSecond();
        if (executor == null) {
            throw new IllegalArgumentException("Unknown challenge type");
        }
        return executor;
    }

    public static ChallengeVerifier getVerifier(String type) {
        ChallengeVerifier verifier = factories.get(type).getThird();
        if (verifier == null) {
            throw new IllegalArgumentException("Unknown challenge type");
        }
        return verifier;
    }

}
