package com.authcoinandroid.service.qtum.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ContractResponse {
    @Expose
    private List<Item> items;

    public ContractResponse() {
    }

    public ContractResponse(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

}