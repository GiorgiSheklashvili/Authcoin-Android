package com.authcoinandroid.service.challenge;

import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.util.Util;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

/**
 * Challenge repository based on Requery ORM library.
 */
public class ChallengeRepository {

    private final ReactiveEntityStore<Persistable> dataStore;

    public ChallengeRepository(ReactiveEntityStore<Persistable> dataStore) {
        Util.notNull(dataStore, "DataStore");
        this.dataStore = dataStore;
    }

    public Single<ChallengeRecord> save(ChallengeRecord cr) {
        byte[] id = cr.getId();
        ChallengeRecord result = dataStore.findByKey(ChallengeRecord.class, id).blockingGet();
        if (result == null) {
            return dataStore.insert(cr);
        }
        return dataStore.update(cr);
    }

    public Single<ChallengeResponseRecord> save(ChallengeResponseRecord cr) {
        byte[] id = cr.getId();
        ChallengeResponseRecord result = dataStore.findByKey(ChallengeResponseRecord.class, id).blockingGet();
        if (result == null) {
            return dataStore.insert(cr);
        }
        return dataStore.update(cr);
    }

    public Single<SignatureRecord> save(SignatureRecord sr) {
        byte[] id = sr.getId();
        SignatureRecord result = dataStore.findByKey(SignatureRecord.class, id).blockingGet();
        if (result == null) {
            return dataStore.insert(sr);
        }
        return dataStore.update(sr);
    }

    /**
     * Get all challenge record values
     */
    public ReactiveResult<ChallengeRecord> findAll() {
        return dataStore.select(ChallengeRecord.class).orderBy(ChallengeRecord.ID.lower()).get();
    }

    /**
     * Find challenge record by primary key
     */
    public Maybe<ChallengeRecord> find(byte[] id) {
        return dataStore.findByKey(ChallengeRecord.class, id);
    }

    /**
     * Find challenge record by VAE key
     */
    public ReactiveResult<ChallengeRecord> findByVaeId(byte[] vaeId) {
        return dataStore.select(ChallengeRecord.class).where(ChallengeRecord.VAE_ID.eq(vaeId)).get();
    }

    public ReactiveResult<ChallengeRecord> findByEirId(byte[] eirId) {
        return dataStore.select(ChallengeRecord.class).where(ChallengeRecord.VERIFIER_ID.eq(eirId)).get();
    }

}
