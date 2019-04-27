package com.authcoinandroid.module;

import android.util.Pair;

import com.authcoinandroid.AbstractTest;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.service.identity.EirRepository;
import com.authcoinandroid.ui.AuthCoinApplication;

import junit.framework.Assert;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.security.KeyPair;

public class KeyGenerationAndEstablishBindingModuleTest extends AbstractTest {

    @Test
    public void testGenerateAndEstablishBinding() throws Exception {
        EirRepository repository = ((AuthCoinApplication)RuntimeEnvironment.application).getEirRepository();
        JdkKeyPairService keyPairService = new JdkKeyPairService();
        KeyGenerationAndEstablishBindingModule module = new KeyGenerationAndEstablishBindingModule(repository, keyPairService);
        Pair<KeyPair, EntityIdentityRecord> pair = module.generateAndEstablishBinding(new String[]{"test"}, "Test");

        Assert.assertNotNull(pair);
        Assert.assertEquals(keyPairService.get("Test"), pair.first);
        Assert.assertNotNull(pair.second);
        Assert.assertNotNull(pair.second.getIdentifiers());
        Assert.assertEquals(1, pair.second.getIdentifiers().size());
        Assert.assertEquals("test", pair.second.getIdentifiers().get(0).getValue());
    }

}