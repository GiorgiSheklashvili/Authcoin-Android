package com.authcoinandroid.model.contract;

public enum ContractType {
    AUTHCOIN("authcoin");

    private final String name;

    ContractType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}