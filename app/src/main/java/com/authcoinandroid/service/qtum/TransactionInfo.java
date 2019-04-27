package com.authcoinandroid.service.qtum;

import java.math.BigDecimal;

public abstract class TransactionInfo {
    public abstract String getAddress();

    public abstract BigDecimal getValue();

    public abstract boolean isOwnAddress();
}