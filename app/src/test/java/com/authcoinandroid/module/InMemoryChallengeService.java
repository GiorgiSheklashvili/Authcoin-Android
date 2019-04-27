package com.authcoinandroid.module;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.service.qtum.model.SendRawTransactionResponse;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.bitcoinj.crypto.DeterministicKey;

import java.util.*;

public class InMemoryChallengeService implements ChallengeService {

    private Map<byte[], List<ChallengeRecord>> vaeIdToChallenges = new HashMap<>();

    @Override
    public Single<ChallengeRecord> registerChallenge(ChallengeRecord challenge) {
        List<ChallengeRecord> challenges = vaeIdToChallenges.get(challenge.getVaeId());
        if (challenges == null) {
            challenges = new LinkedList<>();
        }
        challenges.add(challenge);
        vaeIdToChallenges.put(challenge.getVaeId(), challenges);
        return null;
    }

    @Override
    public Observable<SendRawTransactionResponse> saveChallengeToBc(DeterministicKey key, ChallengeRecord challenge) {
        return null;
    }

    @Override
    public Observable<List<ChallengeRecord>> getChallengeRecordsForEir(EntityIdentityRecord eir) {
        return null;
    }

    @Override
    public Single<ChallengeRecord> registerChallengeResponse(byte[] challengeId, ChallengeResponseRecord response) {
        ChallengeRecord cr = get(challengeId).blockingGet();
        cr.setResponse(response);
        List<ChallengeRecord> challenges = vaeIdToChallenges.get(response.getChallenge().getVaeId());
        for(int i = 0; i < challenges.size(); i++) {
            if(Arrays.equals(challenges.get(i).getId(), cr.getId())) {
                challenges.set(i, cr);
                vaeIdToChallenges.put(cr.getVaeId(), challenges);
            }
        }
        return Single.just(cr);
    }

    @Override
    public Observable<SendRawTransactionResponse> saveChallengeResponseToBc(DeterministicKey key, ChallengeResponseRecord response) {
        return null;
    }

    @Override
    public Single<ChallengeRecord> registerSignatureRecord(byte[] challengeId, SignatureRecord signature) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public Maybe<ChallengeRecord> get(byte[] id) {
        for (List<ChallengeRecord> challenges : vaeIdToChallenges.values()) {
            for (ChallengeRecord c : challenges) {
                if (Arrays.equals(c.getId(), id)) {
                    return Maybe.just(c);
                }
            }
        }
        return Maybe.empty();
    }

    public Observable<SendRawTransactionResponse> saveSignatureRecordToBc(DeterministicKey key, SignatureRecord signatureRecord) {
        return null;
    }

    @Override
    public List<ChallengeRecord> getByEirId(byte[] id) {
        return null;
    }
}
