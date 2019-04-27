package com.authcoinandroid.service.qtum.model;

public class ContractRequest {
    private String[] hashes;

    public ContractRequest(String[] hashes) {
        this.hashes = hashes;
    }

    public String[] getHashes() {
        return hashes;
    }
}