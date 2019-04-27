package com.authcoinandroid.task.response;

import com.authcoinandroid.task.result.AsyncTaskResult;
import org.bitcoinj.wallet.Wallet;

public interface WalletCreationResponse {
    void processFinish(AsyncTaskResult<Wallet> output);
}