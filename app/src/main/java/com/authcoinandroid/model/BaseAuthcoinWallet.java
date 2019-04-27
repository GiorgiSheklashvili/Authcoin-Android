package com.authcoinandroid.model;

import java.io.File;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;

@Entity
public class BaseAuthcoinWallet {

    @Key @Generated
    int id;

    String location;

    public BaseAuthcoinWallet(String location) {
        this.location = location;
    }

    public BaseAuthcoinWallet() {
    }

    public File getLocationFile() {
        return new File(location);
    }
}
