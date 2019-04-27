package com.authcoinandroid.module;


import com.authcoinandroid.model.EntityIdentityRecord;

public class DummyFormalValidationModule implements FormalValidationModule {

    private boolean result;

    public DummyFormalValidationModule(boolean result) {
        this.result = result;
    }

    @Override
    public boolean verify(EntityIdentityRecord target, EntityIdentityRecord verifier) {
        return result;
    }
}
