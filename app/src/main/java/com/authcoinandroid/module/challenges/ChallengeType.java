package com.authcoinandroid.module.challenges;

public enum ChallengeType {
    SIGN_CONTENT("Sign Content"),
    MFA_SIGN_CONTENT_AND_FACIAL_RECOGNITION("MFA with Facial recognition");

    private String value;

    ChallengeType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
