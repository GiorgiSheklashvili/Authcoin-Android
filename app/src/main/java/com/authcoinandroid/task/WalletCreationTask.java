package com.authcoinandroid.task;

import android.content.Context;
import android.os.AsyncTask;
import com.authcoinandroid.service.wallet.WalletService;
import com.authcoinandroid.task.response.WalletCreationResponse;
import com.authcoinandroid.task.result.AsyncTaskResult;
import com.authcoinandroid.task.result.WalletCreationResult;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.IOException;

public class WalletCreationTask extends AsyncTask<Object, Object, AsyncTaskResult<Wallet>> {

    private final WalletService walletService;
    private Context context;
    private String password;
    private WalletCreationResponse walletCreationResponse;

    public WalletCreationTask(Context context, String password, WalletCreationResponse walletCreationResponse, WalletService walletService) {
        this.context = context;
        this.password = password;
        this.walletCreationResponse = walletCreationResponse;
        this.walletService = walletService;
    }

    @Override
    protected AsyncTaskResult<Wallet> doInBackground(Object... objects) {
        try {
            Wallet wallet = walletService.createWallet(context, this.password);
            return new WalletCreationResult<>(wallet);
        } catch (IOException | UnreadableWalletException e) {
            return new WalletCreationResult<>(e);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<Wallet> o) {
        walletCreationResponse.processFinish(o);
    }
}