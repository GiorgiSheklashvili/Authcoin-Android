package com.authcoinandroid;

import android.support.annotation.NonNull;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.JdkKeyPairService;
import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.module.challenges.Challenges;
import com.authcoinandroid.service.challenge.ChallengeRepository;
import com.authcoinandroid.service.identity.EirRepository;
import com.authcoinandroid.service.keypair.KeyPairService;
import com.authcoinandroid.ui.AuthCoinApplication;
import com.authcoinandroid.util.Util;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {26}, application = AuthCoinApplication.class)
public abstract class AbstractTest {

    protected KeyPairService keyPairService = new JdkKeyPairService();
    protected EntityIdentityRecord verifier;
    protected EntityIdentityRecord target;
    protected EirRepository eirRepository;
    protected ReactiveEntityStore<Persistable> dataStore;
    protected ChallengeRepository challengeRepository;

    @Before
    public void setUp() throws Exception {
        this.dataStore = ((AuthCoinApplication) RuntimeEnvironment.application).getDataStore();
        this.eirRepository = new EirRepository(dataStore);
        this.verifier = eirRepository.save(createEir("Verifier")).blockingGet();
        this.target = eirRepository.save(createTargetEir()).blockingGet();
        this.challengeRepository = new ChallengeRepository(dataStore);
    }

    @After
    public void tearDown() throws Exception {
        if (dataStore != null)
            this.dataStore.close();
    }

    protected EntityIdentityRecord createEir(String alias) {
        return new EntityIdentityRecord(alias, keyPairService.create(alias));
    }

    protected EntityIdentityRecord createTargetEir() {
        return new EntityIdentityRecord(keyPairService.create("target").getPublic());
    }

    @NonNull
    protected ChallengeRecord createChallengeRecord() {
        byte[] id = Util.generateId();
        byte[] vaeId = Util.generateId();
        Challenge c = Challenges.get("Sign Content");
        return new ChallengeRecord(id, vaeId, c.getType(), c.getContent(null), verifier, target);
    }
}
