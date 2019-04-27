package com.authcoinandroid.service.identity;

import com.authcoinandroid.model.AssetBlockChainStatus;
import com.authcoinandroid.model.EntityIdentityRecord;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

import static java.util.Arrays.asList;

/**
 * EIR database operations
 */
public class EirRepository {

    private final ReactiveEntityStore<Persistable> dataStore;

    public EirRepository(ReactiveEntityStore<Persistable> dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Inserts or updates EIR based on EIR ID.
     */
    public Single<EntityIdentityRecord> save(EntityIdentityRecord eir) {
        byte[] id = eir.getId();
        EntityIdentityRecord result = dataStore.findByKey(EntityIdentityRecord.class, id).blockingGet();
        if (result == null) {
            return dataStore.insert(eir);
        }
        return dataStore.update(eir);
    }

    /**
     * Get all EIR values
     */
    public List<EntityIdentityRecord> findAll() {
        return dataStore.select(EntityIdentityRecord.class).where(EntityIdentityRecord.KEY_STORE_ALIAS.notNull()).orderBy(EntityIdentityRecord.ID.lower()).get().toList();
    }

    /**
     * Find EIR by primary key
     */
    public Observable<EntityIdentityRecord> find(byte[] id) {
        Maybe<EntityIdentityRecord> r = dataStore.findByKey(EntityIdentityRecord.class, id);
        return r.toObservable();
    }

    public List<EntityIdentityRecord> findByStatus(AssetBlockChainStatus... status) {
        return dataStore
                .select(EntityIdentityRecord.class)
                .where(EntityIdentityRecord.STATUS.in(asList(status)))
                .orderBy(EntityIdentityRecord.ID.lower())
                .get().toList();
    }

    public EntityIdentityRecord findByAlias(String alias) {
        return dataStore
                .select(EntityIdentityRecord.class)
                .where(EntityIdentityRecord.KEY_STORE_ALIAS.equal(alias)).get().first();
    }
}
