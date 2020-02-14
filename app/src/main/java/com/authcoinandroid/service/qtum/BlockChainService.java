package com.authcoinandroid.service.qtum;

import android.util.Log;

import com.authcoinandroid.service.qtum.model.*;
import com.authcoinandroid.util.AuthCoinNetParams;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class BlockChainService implements BlockChainApi {

    private static BlockChainService blockChainService;
    private BlockChainApi blockChainApi;

    public static BlockChainService getInstance() {
        if (blockChainService == null) {
            blockChainService = new BlockChainService();
        }
        return blockChainService;
    }

    private BlockChainService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AuthCoinNetParams.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        blockChainApi = retrofit.create(BlockChainApi.class);
    }

    @Override
    public Observable<ContractResponse> callContract(String contractAddress, ContractRequest contractRequest) {
        Log.d("BSerContractAddress", contractAddress);
        for (int i=0; i<contractRequest.getHashes().length; i++){
            Log.d("BSerContractRequest", contractRequest.getHashes()[i]);
        }
        return blockChainApi.callContract(contractAddress, contractRequest);
    }

    @Override
    public Observable<SendRawTransactionResponse> sendRawTransaction(SendRawTransactionRequest sendRawTransactionRequest) {
        Log.d("BSSndRwTrnsctnDt", sendRawTransactionRequest.getData());
        Log.d("BSSndRwTrnsctnhighFee", sendRawTransactionRequest.getAllowHighFee().toString());
        return blockChainApi.sendRawTransaction(sendRawTransactionRequest);
    }

    @Override
    public Observable<List<UnspentOutput>> getUnspentOutput(List<String> addresses) {//transaction addresses
        return blockChainApi.getUnspentOutput(addresses);
    }

    @Override
    public Observable<Transaction> getTransaction(String transaction) {
        Log.d("BSerTransaction", transaction);
        return blockChainApi.getTransaction(transaction);
    }
}