package com.authcoinandroid.module;


import com.authcoinandroid.module.messaging.MessageHandler;

public abstract class AbstractModule {

    protected MessageHandler messageHandler;

    public AbstractModule(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

}
