package com.authcoinandroid.service.challenge;

import com.authcoinandroid.AbstractTest;
import com.authcoinandroid.model.ChallengeRecord;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;


public class ChallengeRepositoryTest extends AbstractTest {

    @Test
    public void testInsert() throws Exception {
        ChallengeRecord cr = createChallengeRecord();
        Single<ChallengeRecord> result = challengeRepository.save(cr);
        Assert.assertNotNull(result.blockingGet());

        ChallengeRecord dbResult = dataStore.findByKey(ChallengeRecord.class, cr.getId()).blockingGet();
        Assert.assertNotNull(dbResult);
        Assert.assertNotNull(dbResult.getVerifier());
        Assert.assertNotNull(dbResult.getTarget());
    }

    @Test
    public void testFindAll() throws Exception {
        challengeRepository.save(createChallengeRecord()).blockingGet();
        challengeRepository.save(createChallengeRecord()).blockingGet();

        List<ChallengeRecord> result = challengeRepository.findAll().toList();
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void testFindById() throws Exception {
        ChallengeRecord challenge = challengeRepository.save(createChallengeRecord()).blockingGet();
        ChallengeRecord cr = challengeRepository.find(challenge.getId()).blockingGet();

        Assert.assertNotNull(cr);
        Assert.assertTrue(Arrays.equals(challenge.getId(), cr.getId()));
    }

    @Test
    public void findByVaeId() throws Exception {
        ChallengeRecord challenge = challengeRepository.save(createChallengeRecord()).blockingGet();
        List<ChallengeRecord> records = challengeRepository.findByVaeId(challenge.getVaeId()).toList();

        Assert.assertNotNull(records);
        Assert.assertEquals(1, records.size());
        Assert.assertTrue(Arrays.equals(challenge.getId(), records.get(0).getId()));
    }

}