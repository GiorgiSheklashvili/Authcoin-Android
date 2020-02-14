package com.authcoinandroid.util;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.QtumTestNetParams;

public final class AuthCoinNetParams {

    private AuthCoinNetParams() {
    }

    public static NetworkParameters getNetParams() {
        return QtumTestNetParams.get();
    }

    public static String getUrl() {
//        return "http://172.17.199.17:5931/";
//        return "https://walletapi.qtum.org/";
//        return "http://192.168.0.12:5931/api/";
        return "https://testnet-walletapi.qtum.org/";
    }
}
