package com.authcoinandroid.service.qtum;

import com.authcoinandroid.service.qtum.model.SendRawTransactionRequest;
import com.authcoinandroid.service.qtum.model.SendRawTransactionResponse;
import com.authcoinandroid.service.qtum.model.UnspentOutput;
import io.reactivex.Observable;
import junit.framework.Assert;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.QtumTestNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


public class BlockChainServiceTest {

    // replaceValues parameters: bytes32 + int
    private String ABI_PARAMS = "00000000000000000000000000000000000000000000000000000000000101060000000000000000000000000000000000000000000000000000000000000008";

    /**
     * Contract:
     * <p>
     * pragma solidity ^0.4.0;
     * <p>
     * <p>
     * contract MyTest {
     * <p>
     * bytes32 private val1;
     * int private val2;
     * <p>
     * event Log(bytes32 v, int v2);
     * <p>
     * function MyTest(){
     * <p>
     * }
     * <p>
     * function replaceValues(bytes32 _val1, int _val2) public returns(bool) {
     * val1 = _val1;
     * val2 = _val2;
     * Log(_val1, _val2);
     * return true;
     * }
     * <p>
     * function getVal1() public constant returns(bytes32) {
     * return val1;
     * }
     * <p>
     * function getVal2() public constant returns(int) {
     * return val2;
     * }
     * <p>
     * }
     */
    @Test
    @Ignore
    public void testGetUnspentOutput() throws Exception {
        Wallet wallet = createWallet("mypassword", new File("mywallet"));
        final DeterministicKey key = wallet.freshReceiveKey();
        System.out.println("Key: " + key.toAddress(QtumTestNetParams.get()).toBase58());
        final BlockChainService bcs = BlockChainService.getInstance();
        Observable<List<UnspentOutput>> observable = bcs.getUnspentOutput(Arrays.asList(key.toAddress(QtumTestNetParams.get()).toBase58()));
        List<UnspentOutput> unspentOutputs = observable.blockingFirst();
        // replaceValues -> 700279dd
        Script script = TransactionUtil.createScript("700279dd", ABI_PARAMS, 250000, 40, "ae6dfc1cbaf19990fa6f6a975c1427d47b16edec");

        String tx = TransactionUtil.createTransaction(script, unspentOutputs, Arrays.asList(key), 25000, 40, BigDecimal.valueOf(0.004), BigDecimal.valueOf(0.1));
        System.out.println("Transaction: " + tx);
        Observable<SendRawTransactionResponse> out = bcs.sendRawTransaction(new SendRawTransactionRequest(tx, 1));

        SendRawTransactionResponse response = out.blockingFirst();
        Assert.assertNotNull(response.getTxid());
        System.out.println(response.getResult() + " :: " + response.getTxid());
    }

    private Wallet createWallet(String password, File file) throws Exception {
        if (file.exists()) {
            return Wallet.loadFromFile(file);
        }
        DeterministicSeed seed = new DeterministicSeed("test test test test test test test test", null, password, System.currentTimeMillis() / 1000);
        Wallet wallet = Wallet.fromSeed(QtumTestNetParams.get(), seed);
        wallet.saveToFile(file);
        return wallet;
    }

}