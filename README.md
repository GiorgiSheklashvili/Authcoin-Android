# Implementation of Multi-Factor Authentication in Authcoin-Android
There are several steps of configure before running application:
1. Spring project API should be accessible from phone. Since phone cannot access the API if the project is running on computer, the spring project
should be deployed. We have it deployed on Heroku.
2. Smart contract address should be provided in project.
3. API URL for interacting with smart contract should be present in project.

In the end this documentation also presents guide to add new challenge types such as MFA.
## Deployment of Spring project
Link of the Spring project:https://github.com/GiorgiSheklashvili/Authcoin-demo-server
In our case it is deployed on heroku: https://authcoin.herokuapp.com

The URL of deployed project should be provided in `index.html` file:

`var uri = "data?serverUrl=http%3A%2F%2Fauthcoin.herokuapp.com&serverEir=" + data.serverEir + "&appName=" + data.appName + "&sessionId=" + data.id;`
``` $('#auth').click(function () {
                $.getJSON('/registration', function (data) {
                    console.log(data);
                    var uri = "data?serverUrl=http%3A%2F%2Fauthcoin.herokuapp.com&serverEir=" + data.serverEir + "&appName=" + data.appName + "&sessionId=" + data.id;
                    console.log(uri);
                    Cookies.set("authcoin_bank_id", data.id);
                    $('#auth').hide();
                    $('#cancel').show();

                    setInterval(function() {
                        poll(data.id);
                    }, 1500);
                    window.location.href = 'authcoin://' + uri;
                });
            }); 
```

When clicking "Login Using Authcoin" button, Authcoin app is called using `authcoin://`

This call is specific for Android platform, so that's why URL should be accessed from Android phone.
## API URL
Both Spring project and Android app are communicating with smart contract through HTTP calls, which have to provided.

[Demo Server](https://github.com/GiorgiSheklashvili/Authcoin-demo-server/blob/748696a889387c884401146dd8a4712e9dc1ed65/src/main/java/com/authcoin/server/demo/services/blockchain/BlockChainService.java#L19)
```
 public BlockChainService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://testnet-walletapi.qtum.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        blockChainApi = retrofit.create(BlockChainApi.class);
    }
```

[Android application](https://github.com/GiorgiSheklashvili/Authcoin-Android/blob/31822307da7927a887157721ccda0f4e3937015f/app/src/main/java/com/authcoinandroid/util/AuthCoinNetParams.java#L17)

```
 public static String getUrl() {
//        return "https://walletapi.qtum.org/";
        return "https://testnet-walletapi.qtum.org/";
    }
 ```
The following URL is for endpoints when the smart contract is deployed on testnet: https://testnet-walletapi.qtum.org/

And this URL is for mainnet: https://walletapi.qtum.org/
## Smart Contract Address
The provided URLs need smart contract address which they use as a destination for method calls:

[Demo Server](https://github.com/GiorgiSheklashvili/Authcoin-demo-server/blob/748696a889387c884401146dd8a4712e9dc1ed65/src/main/java/com/authcoin/server/demo/services/blockchain/contract/AuthcoinContractParams.java#L7)

[Android application](https://github.com/GiorgiSheklashvili/Authcoin-Android/blob/31822307da7927a887157721ccda0f4e3937015f/app/src/main/java/com/authcoinandroid/service/contract/AuthcoinContractParams.java#L6)
```
static final String AUTHCOIN_CONTRACT_ADDRESS = "eb95c662869311bde0cc6cff0a178ea99f7eff22";
```
In our case the contract which is deployed on testnet has the following address: `eb95c662869311bde0cc6cff0a178ea99f7eff22`
## How to add challenge types
### Flow of the Authentication
For adding Multi-factor authentication in the project, one has to add two additional cases in switch statement in 

[AuthenticationActivity.java](https://github.com/GiorgiSheklashvili/Authcoin-Android/blob/5389237939581932e36a88bd00c7bac84996619f/app/src/main/java/com/authcoinandroid/ui/activity/AuthenticationActivity.java#L86-L170)

```
switch (msg.what) {
                    case 1:
                        // challenge type
                        ChallengeTypeSelectorFragment selectionFragment = new ChallengeTypeSelectorFragment();
                        selectionFragment.setSelectChallengeButtonListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new ChallengeTypeMessageResponse(selectionFragment.getSelectedChallenge()));
                                        VAProcessRunnable.lock.notify();
                                        challengeType = selectionFragment.getSelectedChallenge();
                                    }
                                }
                        );
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, selectionFragment).commit();
                        break;

                    case 2:
                        // evaluate target's challenge sent to verifier
                        EvaluateChallengeFragment evaluateChallengeFragment = new EvaluateChallengeFragment();
                        evaluateChallengeFragment.setChallengeRecord(((EvaluateChallengeMessage) msg.obj).getChallenge());
                        evaluateChallengeFragment.setApproveButtonListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new EvaluateChallengeResponseMessage(evaluateChallengeFragment.isApproved()));
                                        VAProcessRunnable.lock.notify();
                                    }
                                }
                        );
                        startFragment(evaluateChallengeFragment);
                        break;
                    case 3:
                        SignatureFragment signatureFragment = new SignatureFragment();
                        signatureFragment.setChallengeResponse(((SignatureMessage) msg.obj).getChallengeResponse());
                        signatureFragment.setApproveSignatureListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new SignatureResponseMessage(0, true));
                                        VAProcessRunnable.lock.notify();
                                    }
                                }
                        );
                        Bundle bundle = new Bundle();
                        bundle.putString("challengeType", challengeType);
                        signatureFragment.setArguments(bundle);
                        startFragment(signatureFragment);
                        break;
                    case 4:
                        CameraImage cameraImage = new CameraImage();
                        cameraImage.setSendButtonListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new EvaluateChallengeResponseMessage(true)); //image approved
                                        VAProcessRunnable.lock.notify();
                                    }
                                }
                        );
                        startFragment(cameraImage);
                        break;
                    case 5:
                        signatureFragment = new SignatureFragment();
                        signatureFragment.setChallengeResponse(((SignatureMessage) msg.obj).getChallengeResponse());
                        signatureFragment.setApproveSignatureListener(
                                v -> {
                                    synchronized (VAProcessRunnable.lock) {
                                        VAProcessRunnable.queue.add(new SignatureResponseMessage(signatureFragment.getLifespan(), true));
                                        VAProcessRunnable.lock.notify();
                                    }
                                }
                        );
                        startFragment(signatureFragment);
                        break;
                    case 10:
                        AuthenticationSuccessfulFragment asf = new AuthenticationSuccessfulFragment();
                        asf.setResult(((UserAuthenticatedMessage) msg.obj));
                        startFragment(asf);
                        break;
                }
```
Case 3 and Case 4 is added for second step of Multi-factor Authentication.


In case 1 of the switch statement, the identity which we want to use for authentication is chosen, and the listener is set for select challenge button. These listeners which are also used in other switch cases provides synchronized access to Validation and Authentication Process runnable. We add [AuthcoinMessage.java](app/src/main/java/com/authcoinandroid/module/messaging/AuthcoinMessage.java) in the queue. This messages in this queue is executed step by step by [MessageHandler.java](app/src/main/java/com/authcoinandroid/module/messaging/MessageHandler.java) and response is sent to main thread back to AuthenticationActivity where switch case receives them with a different case number. 

In case of listener in case 1, the challenge type is chosen and sent to Validation and Authentication queue. Case 2 presents the evaluation screen for a received challenge. Listener of "fulfill" button adds evaluation result message to Validation and Authentication Runnable queue. Case 3 displays response from the target after you fulfilled his/her challenge and if you agree to sign the response, then the second factor of authentication is started(if you have chosen MFA in challenge type). Case 4 is similar to case 2, it presents the screen to take the picture and the listener for "send photo" button adds the message to Validation and Authentication runnable queue. Case 5 displays the response of the target and if it's valid or not. Also you can write for how many days you want to trust the target and then you sign the response. "Sign Response" button listener adds SignatureResponseMessage to queue, and finally case 10 displays if authentication was successful and displays Verifiers and Targets challenge records.
### Modules and Classes

In the [Challenges.java](app/src/main/java/com/authcoinandroid/module/challenges/Challenges.java) class there is Hashmap field called "factories", which contains Triplets as a value. 
```
static {
        factories.put(
                ChallengeType.SIGN_CONTENT.getValue(),
                new Triplet<>(
                        new SigningChallengeFactory(),
                        new SigningChallengeExecutor(),
                        new SigningChallengeVerifier()
                )

        );
        factories.put(
                ChallengeType.MFA_SIGN_CONTENT_AND_FACIAL_RECOGNITION.getValue(),
                new Triplet<>(
                        new SigningImageChallengeFactory(),
                        new SigningImageChallengeExecutor(),
                        new SigningImageChallengeVerifier()
                )

        );

    }
```
This triplet contains three class objects: Factory, which is responsible for creating of the Challenge object, executor, which signs the challenge with given entity identity record private key and verifier, which verifies that fullfiled challenge is signed by Entity identity record. When adding the new type of challenge to this project, it is neccessary to implement all of those three classes and also add them as a triplet in [Challenges.java](app/src/main/java/com/authcoinandroid/module/challenges/Challenges.java) class.

When adding new factor of Authentication, Naturally new challengetype enum should be added to [ChallengeType.java](app/src/main/java/com/authcoinandroid/module/challenges/ChallengeType.java) class. 
[FaceRecognitionChallenge.java](app/src/main/java/com/authcoinandroid/module/challenges/faceRecognition/FaceRecognitionChallenge.java) class represents the actual new factor, and with "sign content" challenge type, we create two-factor authentication. getContent method gets the image which was captured by [CameraImage.java](app/src/main/java/com/authcoinandroid/ui/fragment/authentication/CameraImage.java) fragment, and convert it to byte array, which is later sent to smart contract method `registerChallengeRecord` [Authcoin.sol](https://github.com/GiorgiSheklashvili/Authcoin-truffle/blob/9e6db9e7aeace309b9ad56da585c011e548ef726/contracts/AuthCoin.sol#L105)
```
function registerChallengeRecord(
        bytes32 _id,
        bytes32 _vaeId,
        bytes32 _challengeType,
        bytes _challenge,
        bytes32 _verifierEir,
        bytes32 _targetEir,
        bytes32 _hash,
        bytes _signature) public returns (bool)
    {

        // verifier exists
        EntityIdentityRecord verifier = getEir(_verifierEir);
        require(address(verifier) != address(0));
        require(verifier.isRevoked() == false);
        require(this == verifier.getCreator());

        // target exists
        EntityIdentityRecord target = getEir(_targetEir);
        require(address(target) != address(0));
        require(target.isRevoked() == false);
        require(this == target.getCreator());

        // ensure CR hash is correct
        require(keccak256(_id, _vaeId, _challengeType, _challenge, _verifierEir, _targetEir) == _hash);

        // ensure CR signature is correct
        require(verifier.verifySignature(BytesUtils.bytes32ToString(_hash), _signature));

        // check VAE
        ValidationAuthenticationEntry vae = vaeIdToVae[_vaeId];
        bool isInitialized = (address(vae)!=address(0));

        if (!isInitialized) {
            vae = new ValidationAuthenticationEntry(_vaeId, msg.sender);
            vaeIdToVae[_vaeId] = vae;
            vaeIdList.push(_vaeId);
            LogNewVae(vae, _vaeId);
        } else {
            require(this == vae.getCreator());
        }

        vae.addChallengeRecord(_id,
            _vaeId,
            _challengeType,
            _challenge,
            verifier,
            target,
            _hash,
            _signature,
            msg.sender
        );

        return true;
    }
```


Afterwards, the returned ChallengeResponseRecord from [CreateImageResponseModule.java](app/src/main/java/com/authcoinandroid/module/CreateImageResponseModule.java) is used in [CreateSendImageResponsesModule.java](app/src/main/java/com/authcoinandroid/module/CreateSendImageResponsesModule.java) to send response to target server 
[CreateSendImageResponsesModule.java](https://github.com/GiorgiSheklashvili/Authcoin-Android/blob/d691380b1ab1ad178210a14e5d4bfc174dfd7668/app/src/main/java/com/authcoinandroid/module/CreateSendImageResponsesModule.java#L27). 

After responding to server, [ValidationAndAuthenticationModule.java](app/src/main/java/com/authcoinandroid/module/ValidationAndAuthenticationModule.java) registers challenges in challengeRepository and also posts them on blockchain. These challenges are verifierChallenge sent from Spring project 
[ChallengeRecordForVerifier](
https://github.com/GiorgiSheklashvili/Authcoin-demo-server/blob/748696a889387c884401146dd8a4712e9dc1ed65/src/main/java/com/authcoin/server/demo/controllers/authentication/AuthenticationController.java#L85) and [ChallengeRecordForTarget](
https://github.com/GiorgiSheklashvili/Authcoin-Android/blob/d691380b1ab1ad178210a14e5d4bfc174dfd7668/app/src/main/java/com/authcoinandroid/module/CreateSendChallengeToTargetModule.java#L73).


Last step of MFA in ValidationAndAuthenticationModule class is to create and post signatures for captured image - [CreateSignaturesFromImageRRModule](
https://github.com/GiorgiSheklashvili/Authcoin-Android/blob/d691380b1ab1ad178210a14e5d4bfc174dfd7668/app/src/main/java/com/authcoinandroid/module/CreateSignaturesFromImageRRModule.java#L36). It sends the message with number 5 to switch case of AuthenticationActivity, which lets the user sign the response and write the number of days to trust the target. Then, VerifierSignature(Verifier - the user of Android application), is sent to target and targetSignature is returned as a result. Finally, targetSignature and verifierSignature as a pair gets posted on blockchain.


Finally, case 10 is called for the switch statement `messageHandler.send(new UserAuthenticatedMessage(resp.getFirst(), resp.getSecond()), 10)`, which displays authentication result and also displays Verifiers and Targets challenge records.
