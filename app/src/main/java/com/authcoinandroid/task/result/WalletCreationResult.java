package com.authcoinandroid.task.result;

import org.bitcoinj.wallet.Wallet;

public class WalletCreationResult<Wallet> extends AsyncTaskResult<Wallet> {

    public WalletCreationResult(Wallet wallet) {
        super(wallet);
    }

    public WalletCreationResult(Exception error) {
        super(error);
    }
}