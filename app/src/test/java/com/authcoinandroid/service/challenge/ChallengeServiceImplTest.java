package com.authcoinandroid.service.challenge;

import com.authcoinandroid.AbstractTest;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.util.Util;
import io.reactivex.Single;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class ChallengeServiceImplTest extends AbstractTest {

    private ChallengeServiceImpl challengeService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.challengeService = new ChallengeServiceImpl(challengeRepository, null, null);
    }

    @Test
    public void registerChallengeResponse() throws Exception {
        Single<ChallengeRecord> result = challengeService.registerChallenge(createChallengeRecord());
        ChallengeRecord challenge = result.blockingGet();
        Single<ChallengeRecord> newResult = challengeService.registerChallengeResponse(
                challenge.getId(),
                createResponseRecord(challenge)
        );
        ChallengeRecord updatedChallenge = newResult.blockingGet();
        Assert.assertNotNull(updatedChallenge);
        Assert.assertNotNull(updatedChallenge.getResponse());
    }

    @Test
    public void registerSignature() throws Exception {
        Single<ChallengeRecord> result = challengeService.registerChallenge(
                createChallengeRecord()
        );
        ChallengeRecord challenge = result.blockingGet();
        Single<ChallengeRecord> result2 = challengeService.registerChallengeResponse(
                challenge.getId(),
                createResponseRecord(challenge)
        );
        ChallengeRecord challengeWithResponse = result2.blockingGet();


        Single<ChallengeRecord> result3 = challengeService.registerSignatureRecord(
                challenge.getId(),
                createSignatureRecord(challengeWithResponse.getResponse())
        );
        ChallengeRecord challengeWithSignature = result3.blockingGet();

        Assert.assertNotNull(challengeWithSignature);
        Assert.assertNotNull(challengeWithSignature.getResponse());
        Assert.assertNotNull(challengeWithSignature.getResponse().getSignatureRecord());
    }

    private ChallengeResponseRecord createResponseRecord(ChallengeRecord challenge) {
        return new ChallengeResponseRecord(Util.generateId(), challenge.getVaeId(), 1,
                new byte[128], new byte[32], new byte[128], challenge);
    }

    private SignatureRecord createSignatureRecord(ChallengeResponseRecord response) {
        return new SignatureRecord(Util.generateId(), response.getVaeId(), 1, 10, false, true,
                new byte[128], new byte[32], response);
    }

}