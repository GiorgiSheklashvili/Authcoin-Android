package com.authcoinandroid.module.challenges;

import com.authcoinandroid.AbstractTest;
import com.authcoinandroid.model.EntityIdentityRecord;

import org.junit.Test;

import java.security.KeyPair;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class ChallengesTest extends AbstractTest {

    private static final String CHALLENGE_NAME_DUMMY = "dummy";
    private static final String CHALLENGE_NAME_SIGN_CONTENT = "Sign Content";

    @Test
    public void testGetAllTypes() throws Exception {
        Set<String> types = Challenges.getAllTypes();
        assertEquals(1, types.size());
    }

    @Test
    public void testAddChallengeFactory() throws Exception {
        Challenges.add(CHALLENGE_NAME_DUMMY, new ChallengeFactory() {
            @Override
            public Challenge create() {
                return new Challenge() {
                    @Override
                    public String getType() {
                        return "dummy";
                    }

                    @Override
                    public byte[] getContent() {
                        return new byte[0];
                    }
                };
            }
        }, null, null);
        assertEquals(2, Challenges.getAllTypes().size());
    }

    @Test
    public void testGetSigningChallenge() throws Exception {
        EntityIdentityRecord eir = createEir("test");

        Challenge challenge = Challenges.get(CHALLENGE_NAME_SIGN_CONTENT);
        assertNotNull(challenge);
        byte[] content = challenge.getContent();
        assertNotNull(content);
        assertEquals("Sign Content", challenge.getType());
        assertTrue(content.length > 0);

        ChallengeExecutor executor = Challenges.getExecutor(CHALLENGE_NAME_SIGN_CONTENT);
        assertNotNull(executor);

        byte[] result = executor.execute(content, eir);

        ChallengeVerifier challengeVerifier = Challenges.getVerifier(CHALLENGE_NAME_SIGN_CONTENT);
        assertNotNull(executor);

        assertTrue(challengeVerifier.verify(eir, content, result));
    }

}