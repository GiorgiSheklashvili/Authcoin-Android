package com.authcoinandroid.service.contract;

import android.util.Log;

import com.authcoinandroid.exception.GetEirException;
import com.authcoinandroid.service.qtum.BlockChainService;
import com.authcoinandroid.service.qtum.model.*;
import io.reactivex.Observable;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.QtumTestNetParams;
import org.bitcoinj.script.Script;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;

import java.util.Collections;
import java.util.List;

import static android.text.TextUtils.isEmpty;
import static com.authcoinandroid.service.contract.AuthcoinContractParams.AUTHCOIN_CONTRACT_ADDRESS;
import static com.authcoinandroid.service.contract.ContractMethodEncoder.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.web3j.utils.Numeric.cleanHexPrefix;

public class AuthcoinContractService {
    private static AuthcoinContractService authcoinContractService;
    private final BlockChainService blockChainService;

    private final static String GET_EIR_COUNT = "getEirCount";
    private final static String REGISTER_EIR = "registerEir";
    private final static String REGISTER_CR = "registerChallengeRecord";
    private final static String REGISTER_RR = "registerChallengeResponse";
    private final static String REGISTER_SR = "registerChallengeSignature";
    private final static String GET_EIR = "getEir";
    private final static String GET_EIR_DATA = "getData";
    private static final String GET_CHALLENGE_IDS = "getChallengeIds";
    private final static String GET_CR_DATA = "getChallengeRecordData";
    private final static String GET_VAE_ARRAY_BY_EIR = "getVaeArrayByEirId";

    public static AuthcoinContractService getInstance() {
        if (authcoinContractService == null) {
            authcoinContractService = new AuthcoinContractService();
        }
        return authcoinContractService;
    }

    public AuthcoinContractService() {
        this.blockChainService = BlockChainService.getInstance();
    }

    public Observable<SendRawTransactionResponse> registerEir(final DeterministicKey key, List<Type> methodParameters) {
        return sendRawTransaction(key, resolveAuthCoinScript(REGISTER_EIR, methodParameters));
    }

    public Observable<SendRawTransactionResponse> registerChallengeRecord(DeterministicKey key, List<Type> methodParameters) {
        return sendRawTransaction(key, resolveAuthCoinScript(REGISTER_CR, methodParameters));
    }

    public Observable<SendRawTransactionResponse> registerChallengeResponseRecord(DeterministicKey key, List<Type> methodParameters) {
        return sendRawTransaction(key, resolveAuthCoinScript(REGISTER_RR, methodParameters));
    }

    public Observable<SendRawTransactionResponse> registerSignatureRecord(DeterministicKey key, List<Type> methodParameters) {
        return sendRawTransaction(key, resolveAuthCoinScript(REGISTER_SR, methodParameters));
    }

    public Observable<ContractResponse> getEirCount() {
        return callAuthCoinContract(resolveContractRequest(GET_EIR_COUNT, emptyList()));
    }

    public Observable<ContractResponse> getEirAddress(Bytes32 eirId) {
        return callAuthCoinContract(resolveContractRequest(GET_EIR, singletonList(eirId)));
    }

    public Observable<ContractResponse> getEirByAddress(Address address) {
        String eirAddress = cleanHexPrefix(address.toString());
        if (isEmpty(eirAddress)) {
            return Observable.error(new GetEirException("No such EIR with provided eirId"));
        } else {
            return callContract(eirAddress, resolveContractRequest(GET_EIR_DATA, emptyList()));
        }
    }

    public Observable<List<UnspentOutput>> getUnspentOutputs(DeterministicKey key) {//private key?
        for (int i=0; i<singletonList(key.toAddress(QtumTestNetParams.get()).toBase58()).size(); i++){
            Log.d("ACSerUnspent", singletonList(key.toAddress(QtumTestNetParams.get()).toBase58()).get(i));
        }
        return blockChainService.getUnspentOutput(singletonList(key.toAddress(QtumTestNetParams.get()).toBase58()));

    }

    public Observable<Transaction> getTransaction(String transaction) {
        Log.d("getTransaction", transaction);
        return blockChainService.getTransaction(transaction);
    }

    public Observable<ContractResponse> getVaeArrayByEirId(Bytes32 eirId) {
        return callAuthCoinContract(resolveContractRequest(GET_VAE_ARRAY_BY_EIR, singletonList(eirId)));
    }

    public Observable<ContractResponse> getChallengeIds(Address address) {
        return callContract(cleanHexPrefix(address.toString()), resolveContractRequest(GET_CHALLENGE_IDS, emptyList()));
    }

    public Observable<ContractResponse> getChallengeRecord(Address address, Bytes32 challengeId) {
        return callContract(cleanHexPrefix(address.toString()), resolveContractRequest(GET_CR_DATA, singletonList(challengeId)));
    }

    private Observable<SendRawTransactionResponse> sendRawTransaction(DeterministicKey key, Script script) {
        Log.d("BSSndRwTrnsctnDt", "todo");
        return getUnspentOutputs(key).switchMap(
                unspentOutput -> blockChainService.sendRawTransaction(
                        new SendRawTransactionRequest(resolveTransaction(key, script, unspentOutput), 1)
                ));
    }

    private Observable<ContractResponse> callAuthCoinContract(ContractRequest contractRequest) {
        return callContract(AUTHCOIN_CONTRACT_ADDRESS, contractRequest);
    }

    private Observable<ContractResponse> callContract(String contractAddress, ContractRequest contractRequest) {
        Log.d("ACSerContractAddress", contractAddress);
        for (int i=0; i<contractRequest.getHashes().length; i++){
            Log.d("ACSerContractRequest", contractRequest.getHashes()[i]);
        }
        return blockChainService.callContract(contractAddress, contractRequest);
    }
}