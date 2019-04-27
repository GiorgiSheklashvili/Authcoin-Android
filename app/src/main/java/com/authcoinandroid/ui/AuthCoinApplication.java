package com.authcoinandroid.ui;

import android.support.multidex.MultiDexApplication;
import com.authcoinandroid.jobs.JobsScheduler;
import com.authcoinandroid.model.Models;
import com.authcoinandroid.module.KeyGenerationAndEstablishBindingModule;
import com.authcoinandroid.service.challenge.ChallengeRepository;
import com.authcoinandroid.service.challenge.ChallengeServiceImpl;
import com.authcoinandroid.service.contract.AuthcoinContractService;
import com.authcoinandroid.service.identity.EirRepository;
import com.authcoinandroid.service.identity.IdentityService;
import com.authcoinandroid.service.keypair.AndroidKeyPairService;
import com.authcoinandroid.service.wallet.WalletService;
import io.requery.Persistable;
import io.requery.android.BuildConfig;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import io.requery.sql.TableCreationMode;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

/**
 * Base class for maintaining global AuthCoin application state.
 */
public class AuthCoinApplication extends MultiDexApplication {

    /**
     * AuthCoin application data store. We should have one data store per application.
     */
    private ReactiveEntityStore<Persistable> dataStore;
    private EirRepository eirRepository;
    private ChallengeRepository challengeRepository;
    private ChallengeServiceImpl challengeService;
    private AuthcoinContractService authcoinContractService;
    private IdentityService identityService;
    private KeyGenerationAndEstablishBindingModule keyGenerationAndEstablishBindingModule;
    private WalletService walletService;

    @Override
    public void onCreate() {
        super.onCreate();
        Provider[] providers = Security.getProviders();
        Security.insertProviderAt(new BouncyCastleProvider(), providers.length);
        // override onUpgrade to handle migrating to a new version
        DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 10);
        if (BuildConfig.DEBUG) {
            // use this in development mode to drop and recreate the tables on every upgrade
            source.setTableCreationMode(TableCreationMode.DROP_CREATE);
        }

        Configuration configuration = source.getConfiguration();
        this.dataStore = ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));

        // create repositories
        this.eirRepository = new EirRepository(dataStore);
        this.challengeRepository = new ChallengeRepository(dataStore);

        // create module
        this.keyGenerationAndEstablishBindingModule = new KeyGenerationAndEstablishBindingModule(this.eirRepository, new AndroidKeyPairService());

        //create services
        this.authcoinContractService = new AuthcoinContractService();
        this.identityService = new IdentityService(this.eirRepository, keyGenerationAndEstablishBindingModule, authcoinContractService);
        this.challengeService = new ChallengeServiceImpl(challengeRepository, authcoinContractService, identityService);

        // create wallet service
        this.walletService = new WalletService(dataStore);

        // start periodic jobs
        new JobsScheduler(this.getBaseContext()).init();
    }

    /**
     * Returns {@link io.requery.sql.EntityDataStore} single instance for the application.
     */
    public ReactiveEntityStore<Persistable> getDataStore() {
        return dataStore;
    }

    public EirRepository getEirRepository() {
        return eirRepository;
    }

    public ChallengeServiceImpl getChallengeService() {
        return challengeService;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    public WalletService getWalletService() {
        return walletService;
    }
}
