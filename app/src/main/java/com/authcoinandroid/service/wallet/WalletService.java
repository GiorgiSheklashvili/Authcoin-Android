package com.authcoinandroid.service.wallet;

import android.content.Context;

import com.authcoinandroid.model.AuthcoinWallet;
import com.authcoinandroid.util.AuthCoinNetParams;

import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

import static org.bitcoinj.core.Utils.currentTimeSeconds;
import static org.bitcoinj.wallet.DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS;

public class WalletService {

    private final ReactiveEntityStore<Persistable> dataStore;
    private Wallet wallet;

    public WalletService(ReactiveEntityStore<Persistable> dataStore) {
        this.dataStore = dataStore;
    }

    public Wallet createWallet(final Context context, String password) throws UnreadableWalletException, IOException {
        File keyStorageDirectory = resolveKeyStorageDirectory(context);
        DeterministicSeed seed = new DeterministicSeed(new SecureRandom(), DEFAULT_SEED_ENTROPY_BITS, password, currentTimeSeconds());
        wallet = Wallet.fromSeed(AuthCoinNetParams.getNetParams(), seed);
        wallet.saveToFile(keyStorageDirectory);
        dataStore.insert(new AuthcoinWallet(keyStorageDirectory.toString())).blockingGet();
        return wallet;
    }

    public String getWalletAddress() {
        return loadWalletFromFile().freshReceiveAddress().toBase58();
    }

    public void deleteWallet() {
        AuthcoinWallet location = getAuthcoinWallet();
        location.getLocationFile().delete();
    }

    public DeterministicKey getReceiveKey() {
        return loadWalletFromFile().freshReceiveKey();
    }

    public boolean isWalletCreated(Context context) {
        try {
            Wallet.loadFromFile(resolveKeyStorageDirectory(context));
        } catch (UnreadableWalletException e) {
            return false;
        }
        return true;
    }

    private Wallet loadWalletFromFile() {
        AuthcoinWallet aw = getAuthcoinWallet();
        try {
            return Wallet.loadFromFile(aw.getLocationFile());
        } catch (UnreadableWalletException e) {
            throw new IllegalStateException("Unreadable wallet", e);
        }
    }

    private File resolveKeyStorageDirectory(Context context) {
        return new File(context.getFilesDir().getPath() + "/authcoin");
    }

    private AuthcoinWallet getAuthcoinWallet() {
        AuthcoinWallet authcoinWallet = dataStore.select(AuthcoinWallet.class).distinct().get().toList().get(0);
        if (authcoinWallet == null) {
            throw new IllegalStateException("Wallet doesn't exist");
        }
        return authcoinWallet;
    }
}