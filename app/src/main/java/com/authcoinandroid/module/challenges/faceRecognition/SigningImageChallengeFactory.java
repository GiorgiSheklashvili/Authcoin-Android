package com.authcoinandroid.module.challenges.faceRecognition;

import android.content.Context;

import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.module.challenges.ChallengeFactory;
import com.authcoinandroid.module.challenges.signing.SigningChallenge;

public class SigningImageChallengeFactory implements ChallengeFactory {

    @Override
    public Challenge create() {
        return new FaceRecognitionChallenge();
    }
}
