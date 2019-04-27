package com.authcoinandroid.service.contract;

import com.authcoinandroid.service.qtum.model.ContractRequest;
import com.authcoinandroid.service.qtum.model.UnspentOutput;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.script.Script;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;

import java.util.List;

import static com.authcoinandroid.service.contract.AuthcoinContractParams.*;
import static com.authcoinandroid.service.qtum.TransactionUtil.createScript;
import static com.authcoinandroid.service.qtum.TransactionUtil.createTransaction;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.web3j.abi.FunctionEncoder.encode;
import static org.web3j.utils.Numeric.cleanHexPrefix;

class ContractMethodEncoder {

    static Script resolveAuthCoinScript(String methodName, List<Type> methodParameters) {
        return resolveScript(methodName, methodParameters, AUTHCOIN_CONTRACT_ADDRESS);
    }

    private static Script resolveScript(String methodName, List<Type> methodParameters, String contractAddress) {
        String encodedFunction = encodeFunction(new Function(methodName, methodParameters, emptyList()));
        return createScript(encodedFunction, FUNCTION_GAS_LIMIT, GAS_PRICE, contractAddress);
    }

    static String resolveTransaction(DeterministicKey key, Script script, List<UnspentOutput> unspentOutput) {
        return createTransaction(script, unspentOutput, singletonList(key), GAS_LIMIT, GAS_PRICE, FEE_PER_KB, FEE);
    }

    static ContractRequest resolveContractRequest(String methodName, List<Type> methodParameters) {
        return new ContractRequest(new String[]{encodeFunction(new Function(methodName, methodParameters, emptyList()))});
    }

    private static String encodeFunction(Function function) {
        return cleanHexPrefix(encode(function));
    }
}