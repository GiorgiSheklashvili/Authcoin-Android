package com.authcoinandroid.service.qtum;

public class InvalidAddressException extends RuntimeException {

    public InvalidAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}
