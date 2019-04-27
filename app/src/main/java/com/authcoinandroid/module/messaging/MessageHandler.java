package com.authcoinandroid.module.messaging;

import android.os.Handler;
import android.util.Log;

public final class MessageHandler {

    private final Handler mainHandler;

    public MessageHandler(Handler mainHandler) {
        this.mainHandler = mainHandler;
    }

    public AuthcoinMessage sendAndWaitResponse(AuthcoinMessage req, int type) {
        send(req, type);
        AuthcoinMessage response = null;
        try {
            Log.i("VAProcessRunnable", "Wait " + Thread.currentThread().getName());
            synchronized (VAProcessRunnable.lock) {
                while (VAProcessRunnable.queue.peek() == null) {
                    VAProcessRunnable.lock.wait();
                }
                response = VAProcessRunnable.queue.poll();
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("VA process was interrupted", e);
        }

        return response;
    }

    public void send(AuthcoinMessage req, int type) {
        android.os.Message m = android.os.Message.obtain(mainHandler);
        m.what = type;
        m.obj = req;
        m.sendToTarget();
    }
}
