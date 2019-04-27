package com.authcoinandroid.util;

import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Hash;

import java.security.PublicKey;

import static org.web3j.utils.Numeric.cleanHexPrefix;

public final class ContractUtil {

    private ContractUtil() {
    }

    public static Bytes32 stringToBytes32(String string) {
        return bytesToBytes32(string.getBytes());
    }

    public static Bytes32 bytesToBytes32(byte[] byteValue) {
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }

    public static String stripLeadingZeroes(String s) {
        return s.replaceAll("^0*", "");
    }

    public static String getEirIdAsString(PublicKey key) {
        return cleanHexPrefix(Hash.sha3(Hex.toHexString(key.getEncoded())));
    }

    public static String getEirIdAsString(byte[] id) {
        return cleanHexPrefix(Hash.sha3(Hex.toHexString(id)));
    }
}