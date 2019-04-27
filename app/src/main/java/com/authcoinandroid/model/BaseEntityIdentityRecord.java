package com.authcoinandroid.model;

import com.authcoinandroid.util.crypto.CryptoUtil;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.util.List;

import io.requery.CascadeAction;
import io.requery.Entity;
import io.requery.Key;
import io.requery.OneToMany;
import io.requery.PostLoad;
import io.requery.Transient;

import static org.web3j.utils.Numeric.cleanHexPrefix;

/**
 * This is a requery class. Based on this class EntityIdentityRecord is generated.
 */
@Entity
public class BaseEntityIdentityRecord {

    @Key
    byte[] id;

    @OneToMany(cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    List<EirIdentifier> identifiers;

    @OneToMany
    List<ChallengeRecord> challenges;

    boolean revoked;

    String contentType;
    byte[] content;

    byte[] hash;
    byte[] signature;

    // Android key-store alias. Used to look up private and public key.
    String keyStoreAlias;
    // EIR status on blockchain
    AssetBlockChainStatus status;
    // Id of the transaction where eir was created
    String transactionId;

    @Transient
    KeyPair keyPair;

    @Transient
    PublicKey publicKey;

    /*
    * Constructor for creating a new entity identity record for this device
    */
    public BaseEntityIdentityRecord(String keyStoreAlias, KeyPair keyPair) {
        this.revoked = false;
        this.contentType = "test"; // TODO
        this.keyStoreAlias = keyStoreAlias;
        this.status = AssetBlockChainStatus.SUBMITTED;
        this.keyPair = keyPair;
        this.publicKey = keyPair.getPublic();
        this.content = publicKey.getEncoded();
        this.id = calcId();
        calculateHashAndSignature();
    }

    /*
    * Constructor for creating a new entity identity record without key pair (EIR doesn't belong to
    * current device.
    */
    public BaseEntityIdentityRecord(PublicKey publicKey) {
        this.revoked = false;
        this.contentType = "test"; // TODO
        this.status = AssetBlockChainStatus.SUBMITTED;
        this.publicKey = publicKey;
        this.content = publicKey.getEncoded();
        this.id = calcId();
        calculateHashAndSignature();
    }

    public BaseEntityIdentityRecord() {
        // Don't use this constructor. It is required by requery.
    }

    @PostLoad
    public void init() {
        this.publicKey = CryptoUtil.toPublicKey(content);
        if (keyStoreAlias != null) {
            this.keyPair = CryptoUtil.getKeyPair(keyStoreAlias);
        }
        calculateHashAndSignature();
    }

    public void setIdentifiers(List<EirIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    private byte[] calcId() {
        return Hash.sha3(publicKey.getEncoded());
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public void setStatus(AssetBlockChainStatus status) {
        this.status = status;
    }

    public PublicKey getPublicKey() {
        return CryptoUtil.getKeyPair(keyStoreAlias).getPublic();
    }

    public byte[] getId() {
        return id;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public byte[] getHash() {
        return hash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public String getKeyStoreAlias() {
        return keyStoreAlias;
    }

    public AssetBlockChainStatus getStatus() {
        return status;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    void calculateHashAndSignature() {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(this.id);
            os.write(this.contentType.getBytes());
            os.write(this.content);

           // for (EirIdentifier identifier : identifiers) {
           //     os.write(identifier.getValue().getBytes());
           // }
            os.write(revoked ? 0 : 1);
            // TODO should be tested
            byte[] message = os.toByteArray();
            hash = Hash.sha3(message);
/*
            Signature signature = Signature.getInstance("SHA256withECDSA");
            PrivateKey aPrivate = keyPair.getPrivate();
            signature.initSign(aPrivate);
            signature.update(message);*/
            this.signature = new byte[56];
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public String toString() {
        return getKeyStoreAlias();
    }
}