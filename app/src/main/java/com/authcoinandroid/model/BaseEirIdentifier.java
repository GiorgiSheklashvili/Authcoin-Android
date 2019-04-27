package com.authcoinandroid.model;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToOne;

/**
 * Input requery class for EIR identifier.
 */
@Entity
public class BaseEirIdentifier {

    @Key
    @Generated
    long id;

    String value;

    @ManyToOne
    BaseEntityIdentityRecord eir;

    public BaseEirIdentifier(String value) {
        this.value = value;
    }

    public BaseEirIdentifier() {
        // Don't use this constructor. It is required by requery.
    }

    public long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
