package com.authcoinandroid.module.challenges;

public enum ChallengeType {
    SIGN_CONTENT("Sign Content");

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
