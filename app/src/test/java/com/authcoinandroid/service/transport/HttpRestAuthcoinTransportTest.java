package com.authcoinandroid.service.transport;

import com.authcoinandroid.AbstractTest;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.module.InMemoryChallengeService;
import com.authcoinandroid.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

@Ignore
public class HttpRestAuthcoinTransportTest extends AbstractTest {


    private HttpRestAuthcoinTransport t;
    private InMemoryChallengeService challengeService;

    @Before
    public void setUp() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        challengeService = new InMemoryChallengeService();
        t = new HttpRestAuthcoinTransport("http://localhost:8080/", new ServerInfo(), challengeService);
    }

    @Test
    public void sendChallengeRecord() throws Exception {
        ServerInfo serverInfo = t.start();

        EntityIdentityRecord verifier = createEir("Verifier");
        EntityIdentityRecord target = createEir("Target");
        ChallengeRecord record = new ChallengeRecord(
                Util.generateId(),
                Util.generateId(),
                "",
                new byte[32],
                verifier,
                target
        );
        ChallengeRecord result = t.send(serverInfo.getId(), record);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getId());
        Assert.assertArrayEquals(record.getVaeId(), result.getVaeId());
        Assert.assertArrayEquals(target.getId(), result.getVerifier().getId());
        Assert.assertArrayEquals(verifier.getId(), result.getTarget().getId());
    }

    @Test
    public void sendResponseRecord() throws Exception {
        ServerInfo serverInfo = t.start();

        EntityIdentityRecord verifier = createEir("Verifier");
        EntityIdentityRecord target = createEir("Target");
        ChallengeRecord challengeRecord = new ChallengeRecord(
                Util.generateId(),
                Util.generateId(),
                "",
                new byte[32],
                verifier,
                target
        );
        ChallengeRecord challengeResult = t.send(serverInfo.getId(), challengeRecord);
        challengeService.registerChallenge(challengeRecord);
        challengeService.registerChallenge(challengeResult);
        ChallengeResponseRecord responseRecord = new ChallengeResponseRecord(
                Util.generateId(),
                "response".getBytes(),
                challengeResult
        );
        ChallengeResponseRecord responseResult = t.send(serverInfo.getId(), responseRecord);
        Assert.assertNotNull(responseResult);
        Assert.assertNotNull(responseResult.getId());
        Assert.assertNotNull(responseResult.getChallenge());
        Assert.assertArrayEquals(responseRecord.getVaeId(), responseResult.getVaeId());
        Assert.assertArrayEquals(challengeRecord.getId(), responseResult.getChallenge().getId());
    }


    @Test
    public void sendSignatureRecord() throws Exception {
        ServerInfo serverInfo = t.start();

        EntityIdentityRecord verifier = createEir("Verifier");
        EntityIdentityRecord target = createEir("Target");

        ChallengeRecord challengeRecord = new ChallengeRecord(
                Util.generateId(),
                Util.generateId(),
                "",
                new byte[32],
                verifier,
                target
        );
        ChallengeRecord challengeResult = t.send(serverInfo.getId(), challengeRecord);
        challengeService.registerChallenge(challengeRecord);
        challengeService.registerChallenge(challengeResult);

        ChallengeResponseRecord responseRecord = new ChallengeResponseRecord(
                Util.generateId(),
                "response".getBytes(),
                challengeResult
        );
        ChallengeResponseRecord responseResult = t.send(serverInfo.getId(), responseRecord);
        challengeService.registerChallengeResponse(responseRecord.getChallenge().getId(), responseRecord);
        challengeService.registerChallengeResponse(responseResult.getChallenge().getId(), responseResult);

        SignatureRecord signatureRecord = new SignatureRecord(
                Util.generateId(),
                0,
                123,
                true,
                responseResult
        );
        SignatureRecord signatureResult = t.send(serverInfo.getId(), signatureRecord);
        Assert.assertNotNull(signatureResult);
        Assert.assertNotNull(signatureResult.getId());
        Assert.assertNotNull(signatureResult.getChallengeResponse());
        Assert.assertNotNull(signatureResult.getChallengeResponse().getChallenge());
        Assert.assertArrayEquals(signatureRecord.getVaeId(), signatureResult.getVaeId());
    }
}