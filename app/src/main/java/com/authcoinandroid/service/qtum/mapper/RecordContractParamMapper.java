package com.authcoinandroid.service.qtum.mapper;

import com.authcoinandroid.model.*;
import com.authcoinandroid.service.identity.IdentityService;
import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Hash;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.authcoinandroid.util.ContractUtil.bytesToBytes32;
import static com.authcoinandroid.util.ContractUtil.stringToBytes32;
import static com.authcoinandroid.util.crypto.CryptoUtil.toPublicKey;
import static org.web3j.abi.Utils.convert;
import static org.web3j.utils.Numeric.cleanHexPrefix;

public class RecordContractParamMapper {

    public static List<Type> resolveEirContractParams(EntityIdentityRecord eir) {
        List<Type> params = new ArrayList<>();
        List<Bytes32> identifiers = new ArrayList<>();
        for (BaseEirIdentifier s : eir.getIdentifiers()) {
            identifiers.add(stringToBytes32(s.getValue()));
        }
        params.add(new DynamicBytes(eir.getKeyPair().getPublic().getEncoded()));
        params.add(stringToBytes32(eir.getContentType()));
        params.add(new DynamicArray<>(identifiers));
        params.add(bytesToBytes32(eir.getHash()));
        params.add(new DynamicBytes(eir.getSignature()));
        return params;
    }

    public static List<Type> resolveChallengeRecordContractParams(ChallengeRecord challenge) {
        List<Type> params = new ArrayList<>();
        params.add(bytesToBytes32(challenge.getId()));
        params.add(bytesToBytes32(challenge.getVaeId()));
        params.add(stringToBytes32(challenge.getType()));
        params.add(new DynamicBytes(challenge.getChallenge()));
        params.add(bytesToBytes32(challenge.getVerifier().getId()));
        params.add(bytesToBytes32(challenge.getTarget().getId()));
        params.add(bytesToBytes32(challenge.getHash()));
        params.add(new DynamicBytes(challenge.getSignature()));
        return params;
    }

    public static List<Type> resolveChallengeResponseRecordContractParams(ChallengeResponseRecord response) {
        List<Type> params = new ArrayList<>();
        params.add(bytesToBytes32(response.getVaeId()));
        params.add(bytesToBytes32(response.getChallenge().getId()));
        params.add(new DynamicBytes(response.getResponse()));
        params.add(bytesToBytes32(response.getHash()));
        params.add(new DynamicBytes(response.getSignature()));
        return params;
    }

    public static List<Type> resolveSignatureRecordContractParams(SignatureRecord signatureRecord) {
        List<Type> params = new ArrayList<>();
        params.add(bytesToBytes32(signatureRecord.getVaeId()));
        params.add(bytesToBytes32(signatureRecord.getChallengeResponse().getChallenge().getId()));
        params.add(new Uint256(signatureRecord.getExpirationBlock()));
        params.add(new Bool(signatureRecord.isSuccessful()));
        params.add(bytesToBytes32(signatureRecord.getHash()));
        params.add(new DynamicBytes(signatureRecord.getSignature()));
        return params;
    }

    public static EntityIdentityRecord resolveEirFromAbiReturn(String abiReturn) {
        // let god have mercy on my soul
        List<TypeReference<?>> outputParameters = Arrays.asList(
                new TypeReference<Bytes32>() { // id
                }, new TypeReference<Uint256>() { // blockNumber
                }, new TypeReference<DynamicBytes>() { // content
                }, new TypeReference<Bytes32>() { // contentType
                }, new TypeReference<Bool>() { // revoked
                }, new TypeReference<DynamicArray<Bytes32>>() { // identifiers
                }, new TypeReference<Bytes32>() { // hash
                }, new TypeReference<DynamicBytes>() { // signature
                });
        List<Type> output = FunctionReturnDecoder.decode(abiReturn, convert(outputParameters));
        // TODO check if blockchain EIR and EIR in db match
        EntityIdentityRecord eir = new EntityIdentityRecord();
        eir.setPublicKey(toPublicKey(removeNullBytes((byte[]) output.get(2).getValue())));
        eir.setContent(removeNullBytes((byte[]) output.get(2).getValue()));
        eir.setId(Hex.decode(cleanHexPrefix(Hash.sha3(Hex.toHexString(eir.getContent())))));
        eir.setContentType(new String(removeNullBytes((byte[]) output.get(3).getValue()), StandardCharsets.UTF_8));
        eir.setRevoked(((Bool) output.get(4)).getValue());
        eir.setIdentifiers(getIdentifiers((List<Bytes32>) output.get(5).getValue()));
        eir.setHash(removeNullBytes((byte[]) output.get(6).getValue()));
        eir.setSignature(removeNullBytes((byte[]) output.get(7).getValue()));
        return eir;
    }

    public static ChallengeRecord resolveCrFromAbiReturn(String abiReturn, IdentityService identityService) {
        // TODO parse all data about cr
        List<TypeReference<?>> outputParameters = Arrays.asList(
                new TypeReference<Bytes32>() { // id
                }, new TypeReference<Uint256>() { // block number
                }, new TypeReference<Bytes32>() { // type
                }, new TypeReference<DynamicBytes>() { // challenge
                }, new TypeReference<Address>() { // verifierEir
                }, new TypeReference<Address>() { // targetEir
                }, new TypeReference<Bytes32>() { // hash
                }, new TypeReference<Bytes32>() { // signature
                }
        );
        List<Type> output = FunctionReturnDecoder.decode(abiReturn, convert(outputParameters));
        ChallengeRecord challengeRecord = new ChallengeRecord();
        challengeRecord.setId(removeNullBytes((byte[]) output.get(0).getValue()));
        challengeRecord.setType(new String(removeNullBytes((byte[]) output.get(2).getValue())));
        challengeRecord.setChallenge(removeNullBytes((byte[]) output.get(3).getValue()));
        challengeRecord.setVerifier(identityService.getEirByAddress((Address) output.get(4)).blockingFirst());
        challengeRecord.setTarget(identityService.getEirByAddress((Address) output.get(5)).blockingFirst());
        challengeRecord.setHash(removeNullBytes((byte[]) output.get(6).getValue()));
        challengeRecord.setSignature(removeNullBytes((byte[]) output.get(7).getValue()));
        return challengeRecord;
    }

    public static List<Address> resolveAddressesFromAbiReturn(String abiReturn) {
        List<TypeReference<?>> outputParameters = Collections.singletonList(
                new TypeReference<DynamicArray<Address>>() {
                });
        List<Type> output = FunctionReturnDecoder.decode(abiReturn, convert(outputParameters));
        return (List<Address>) output.get(0).getValue();
    }

    public static Address resolveAddressFromAbiReturn(String abiReturn) {
        List<TypeReference<?>> outputParameters = Collections.singletonList(
                new TypeReference<Address>() {
                });
        List<Type> output = FunctionReturnDecoder.decode(abiReturn, convert(outputParameters));
        return (Address) output.get(0);
    }

    public static List<Bytes32> resolveBytes32FromAbiReturn(String abiReturn) {
        List<TypeReference<?>> outputParameters = Collections.singletonList(
                new TypeReference<DynamicArray<Bytes32>>() {
                });
        List<Type> output = FunctionReturnDecoder.decode(abiReturn, convert(outputParameters));
        return (List<Bytes32>) output.get(0).getValue();
    }

    private static List<EirIdentifier> getIdentifiers(List<Bytes32> ids) {
        List<EirIdentifier> identifiers = new ArrayList<>();
        for (Bytes32 identifier : ids) {
            identifiers.add(new EirIdentifier(new String(removeNullBytes(identifier.getValue()), StandardCharsets.UTF_8)));
        }
        return identifiers;
    }

    private static byte[] removeNullBytes(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }
        return Arrays.copyOf(bytes, i + 1);
    }
}