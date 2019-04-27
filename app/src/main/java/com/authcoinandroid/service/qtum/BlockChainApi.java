package com.authcoinandroid.service.qtum;

import com.authcoinandroid.service.qtum.model.*;
import io.reactivex.Observable;
import retrofit2.http.*;

import java.util.List;

/**
 * A low level API to communicate with QTUM blockchain
 */
public interface BlockChainApi {

    /**
     * Calls the smart contract. Can not change the state of the contract.
     */
    @POST("/contracts/{addressContract}/call")
    Observable<ContractResponse> callContract(@Path("addressContract") String addressContract, @Body ContractRequest contractRequest);

    /**
     * Calls the smart contract. Can change the state of the contract.
     */
    @POST("/send-raw-transaction")//sendrawtransaction
    Observable<SendRawTransactionResponse> sendRawTransaction(@Body SendRawTransactionRequest sendRawTransactionRequest);

    /**
     * Used to get the unspent transaction outputs by the address
     */
    @GET("/outputs/unspent")//listunspent?
    Observable<List<UnspentOutput>> getUnspentOutput(@Query("addresses[]") List<String> addresses);

    @GET("/transactions/{tx_hash}")//gettransaction
    Observable<Transaction> getTransaction(@Path("tx_hash") String txHash);
}