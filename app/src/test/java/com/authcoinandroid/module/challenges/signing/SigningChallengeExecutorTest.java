package com.authcoinandroid.module.challenges.signing;

import com.authcoinandroid.AbstractTest;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.challenges.Challenge;
import com.authcoinandroid.module.challenges.Challenges;

import org.junit.Ignore;
import org.junit.Test;

import java.security.KeyPair;
import java.security.Signature;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SigningChallengeExecutorTest extends AbstractTest {

    private SigningChallengeExecutor executor = new SigningChallengeExecutor();

    @Test
    public void testExecute() throws Exception {
        EntityIdentityRecord eir = createTargetEir();
        Challenge challenge = Challenges.get("Sign Content");
        byte[] content = challenge.getContent(null);
        byte[] result = executor.execute(content, eir);
        assertNotNull(result);

        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initVerify(eir.getKeyPair().getPublic());
        signature.update(content);
        assertTrue(signature.verify(result));
    }

    protected EntityIdentityRecord createTargetEir() {
        KeyPair keypair = keyPairService.create("target");
        return new EntityIdentityRecord("target", keypair);
    }


}
