package com.authcoinandroid.service.qtum;

public class InsufficientFeeException extends RuntimeException {

    public InsufficientFeeException(String message) {
        super(message);
    }
}
