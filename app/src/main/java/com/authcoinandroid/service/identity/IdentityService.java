package com.authcoinandroid.service.identity;

import com.authcoinandroid.exception.GetEirException;
import com.authcoinandroid.exception.RegisterEirException;
import com.authcoinandroid.model.EntityIdentityRecord;
import com.authcoinandroid.module.KeyGenerationAndEstablishBindingModule;
import com.authcoinandroid.service.contract.AuthcoinContractService;
import com.authcoinandroid.service.qtum.mapper.RecordContractParamMapper;
import com.authcoinandroid.service.qtum.model.SendRawTransactionResponse;
import com.authcoinandroid.service.qtum.model.Transaction;
import com.authcoinandroid.util.ContractUtil;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.bitcoinj.crypto.DeterministicKey;
import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;

import static android.text.TextUtils.isEmpty;
import static com.authcoinandroid.model.AssetBlockChainStatus.*;
import static com.authcoinandroid.service.qtum.mapper.RecordContractParamMapper.resolveAddressFromAbiReturn;
import static com.authcoinandroid.util.ContractUtil.bytesToBytes32;
import static com.authcoinandroid.util.ContractUtil.getEirIdAsString;
import static com.authcoinandroid.util.crypto.CryptoUtil.getPublicKeyByAlias;

public class IdentityService {

    private final EirRepository repository;
    private final KeyGenerationAndEstablishBindingModule module;
    private AuthcoinContractService authcoinContractService;

    public IdentityService(EirRepository repository, KeyGenerationAndEstablishBindingModule module, AuthcoinContractService authcoinContractService) {
        this.repository = repository;
        this.module = module;
        this.authcoinContractService = authcoinContractService;
    }

    /**
     * Registers a new eir. Does the following:
     * 1. Generates a new KeyPair and saves it to Android KeyStore
     * 2. Creates EIR object and saves it to the local database
     * 3. Calls QTUM smart contract to store the EIR on blockchain
     *
     * @param key         - wallet key
     * @param identifiers - EIR identifiers
     * @param alias       - alias used by Android Keystore
     * @return an observable
     * @throws RegisterEirException will be thrown if registration fails
     */
    public Observable<SendRawTransactionResponse> registerEir(DeterministicKey key, String[] identifiers, String alias) throws RegisterEirException {
        try {
            EntityIdentityRecord eir = module.generateAndEstablishBinding(identifiers, alias).second;
            List<Type> params = RecordContractParamMapper.resolveEirContractParams(eir);
            return this.authcoinContractService.registerEir(key, params)
                    .switchMap(sendRawTransactionResponse -> {
                        eir.setTransactionId(sendRawTransactionResponse.getTxid());
                        repository.save(eir).blockingGet();
                        return Observable.just(sendRawTransactionResponse);
                    });
        } catch (GeneralSecurityException | IOException e) {
            throw new RegisterEirException("Failed to register EIR", e);
        }
    }

    public Observable<EntityIdentityRecord> getEirByAlias(String alias) throws GetEirException {
        try {
            PublicKey key = getPublicKeyByAlias(alias);
            return this.getEirById(getEirIdAsString(key));
        } catch (GeneralSecurityException | IOException e) {
            throw new GetEirException("Failed to get EIR", e);
        }
    }

    public Observable<EntityIdentityRecord> getEirById(String eirId) {
        return this.authcoinContractService.getEirAddress(getEirIdAsBytes32(eirId))
                .switchMap(contractResponse -> getEirByAddress(resolveAddressFromAbiReturn(contractResponse.getItems().get(0).getOutput())));
    }

    public Observable<EntityIdentityRecord> getEirByAddress(Address address) {
        return this.authcoinContractService.getEirByAddress(address)
                .switchMap(contractResponse -> mapAbiResponseToObservable(contractResponse.getItems().get(0).getOutput()));
    }

    public Completable updateEirStatusFromBc(EntityIdentityRecord eir) {
        return Completable.fromAction(() -> {
            if (eir.getStatus() == SUBMITTED && !isEmpty(eir.getTransactionId())) {
                Transaction transaction = this.authcoinContractService.getTransaction(eir.getTransactionId()).blockingSingle();
                // if transaction is mined
                if (transaction.getBlockTime() != null) {
                    getEirByAlias(eir.getKeyStoreAlias()).doOnNext(e -> {
                        // if we can get eir from bc means eir was successfully saved
                        eir.setStatus(MINED);
                        repository.save(eir).blockingGet();
                    }).onErrorResumeNext(e -> {
                        // if we can not get eir from bc means eir failed to save
                        eir.setStatus(MINING_FAILED);
                        repository.save(eir).blockingGet();
                    }).blockingSingle();
                }
            }
        });
    }

    /**
     * Returns all entity identity records.
     */
    public List<EntityIdentityRecord> getAll() {
        return repository.findAll();
    }

    private Observable<EntityIdentityRecord> mapAbiResponseToObservable(String abiResponse) {
        return Observable.defer(() -> {
            try {

                EntityIdentityRecord eir = RecordContractParamMapper.resolveEirFromAbiReturn(abiResponse);
                // TODO compare keys
                //eir.setContent(key);
                return Observable.just(eir);
            } catch (Exception e) {
                return Observable.error(e);
            }
        });
    }

    private Bytes32 getEirIdAsBytes32(PublicKey key) {
        return bytesToBytes32(Hex.decode(ContractUtil.getEirIdAsString(key)));
    }

    private Bytes32 getEirIdAsBytes32(String eirId) {
        return bytesToBytes32(Hex.decode(eirId));
    }
}