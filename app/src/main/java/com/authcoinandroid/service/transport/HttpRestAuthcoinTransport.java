package com.authcoinandroid.service.transport;

import android.util.Base64;
import com.authcoinandroid.model.ChallengeRecord;
import com.authcoinandroid.model.ChallengeResponseRecord;
import com.authcoinandroid.model.SignatureRecord;
import com.authcoinandroid.service.challenge.ChallengeService;
import com.authcoinandroid.util.Util;
import com.google.gson.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

public class HttpRestAuthcoinTransport implements AuthcoinTransport {

    private final RestApi api;
    private final ChallengeService challengeService;
    private ServerInfo serverInfo;

    public HttpRestAuthcoinTransport(String baseUrl, ServerInfo serverInfo, ChallengeService challengeService) {
        this.challengeService = challengeService;
        this.serverInfo = serverInfo;
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class,
                new ByteArrayToBase64TypeAdapter()).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        api = retrofit.create(RestApi.class);
    }

    public ServerInfo start() {
        try {
            Call<ServerInfo> call = api.start();
            Response<ServerInfo> response = call.execute();
            serverInfo = response.body();
            return serverInfo;
        } catch (IOException e) {
            throw new TransportException(e);
        }
    }

    @Override
    public ChallengeRecord send(UUID registrationId, ChallengeRecord record) {
        try {
            Call<InternalRestChallengeRecord> responseCall = api.send(
                    registrationId,
                    new InternalRestChallengeRecord(record)
            );
            Response<InternalRestChallengeRecord> response = responseCall.execute();
            InternalRestChallengeRecord body = response.body();
            ChallengeRecord challengeRecord = new ChallengeRecord(
                    body.getId(),
                    body.getVaeId(),
                    body.getType(),
                    body.getChallenge(),
                    record.getTarget(), // TODO record?
                    record.getVerifier() // TODO record?
            );
            return challengeRecord;
        } catch (IOException e) {
            throw new TransportException(e);
        }
    }

    @Override
    public ChallengeResponseRecord send(UUID registrationId, ChallengeResponseRecord rr) {
        try {
            Call<InternalRestChallengeResponseRecord> responseCall = api.send(
                    registrationId,
                    new InternalRestChallengeResponseRecord(rr)
            );
            Response<InternalRestChallengeResponseRecord> response = responseCall.execute();
            InternalRestChallengeResponseRecord body = response.body();
            ChallengeResponseRecord challengeRecord = new ChallengeResponseRecord(
                    Util.generateId(),
                    body.getVaeId(),
                    0, //TODO
                    body.getResponse(),
                    new byte[32],
                    new byte[32],
                    challengeService.get(body.getChallengeId()).blockingGet() //TODO
            );
            return challengeRecord;
        } catch (IOException e) {
            throw new TransportException(e);
        }
    }

    @Override
    public SignatureRecord send(UUID registrationId, SignatureRecord sr) {
        try {
            Call<InternalRestChallengeSignatureRecord> responseCall = api.send(
                    registrationId,
                    new InternalRestChallengeSignatureRecord(sr)
            );
            Response<InternalRestChallengeSignatureRecord> response = responseCall.execute();
            InternalRestChallengeSignatureRecord body = response.body();
            SignatureRecord signatureRecord = new SignatureRecord(
                    Util.generateId(),
                    body.getVaeId(),
                    0, // TODO i suppose
                    42, // TODO body.getLifespan() is currently null
                    body.isRevoked(),
                    body.isSuccessful(),
                    new byte[32],
                    new byte[32],
                    challengeService.get(body.getChallengeId()).blockingGet().getResponse()
            );
            return signatureRecord;
        } catch (IOException e) {
            throw new TransportException(e);
        }
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    private interface RestApi {

        @GET("/registration")
        Call<ServerInfo> start();

        @POST("/registration/{registrationId}/challenge")
        Call<InternalRestChallengeRecord> send(
                @Path("registrationId") UUID addressContract,
                @Body InternalRestChallengeRecord record
        );

        @POST("/registration/{registrationId}/response")
        Call<InternalRestChallengeResponseRecord> send(
                @Path("registrationId") UUID addressContract,
                @Body InternalRestChallengeResponseRecord verifierResponse
        );

        @POST("/registration/{registrationId}/response")
        Call<InternalRestChallengeSignatureRecord> send(
                @Path("registrationId") UUID addressContract,
                @Body InternalRestChallengeSignatureRecord sr
        );

        @GET("/registration/{registrationId}/finalize/{txId}")
        Call<Void> finalize(
                @Path("registrationId") UUID registrationId,
                @Path("txId") String txId
        );

    }

    public static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.decode(json.getAsString(), Base64.NO_WRAP);
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
        }
    }
}
