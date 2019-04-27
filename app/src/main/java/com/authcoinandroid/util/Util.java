package com.authcoinandroid.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public final class Util {

    private Util() {
    }

    public static void notNull(Object o, String message) {
        if (o == null) {
            throw new NullPointerException(message + " is null");
        }
    }

    public static byte[] generateId() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
