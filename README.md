# Authcoin-Android
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

[Android application](https://github.com/GiorgiSheklashvili/Authcoin-Android/blob/31822307da7927a887157721ccda0f4e3937015f/app/src/main/java/com/authcoinandroid/util/AuthCoinNetParams.java#L17)

The following URL is for endpoints when the smart contract is deployed on testnet: https://testnet-walletapi.qtum.org/

And this URL is for mainnet: https://walletapi.qtum.org/
## Smart Contract Address
The provided URLs need smart contract address which they use as a destination for method calls:

[Demo Server](https://github.com/GiorgiSheklashvili/Authcoin-demo-server/blob/748696a889387c884401146dd8a4712e9dc1ed65/src/main/java/com/authcoin/server/demo/services/blockchain/contract/AuthcoinContractParams.java#L7)

[Android application](https://github.com/GiorgiSheklashvili/Authcoin-Android/blob/31822307da7927a887157721ccda0f4e3937015f/app/src/main/java/com/authcoinandroid/service/contract/AuthcoinContractParams.java#L6)

In our case the contract which is deployed on testnet has the following address: eb95c662869311bde0cc6cff0a178ea99f7eff22
## How to add challenge types
For adding Multi-factor authentication in the project, one has to add additional two additional cases in switch statement in [AuthenticationActivity.java](app/src/main/java/com/authcoinandroid/ui/activity/AuthenticationActivity.java)

https://github.com/GiorgiSheklashvili/Authcoin-Android/blob/5389237939581932e36a88bd00c7bac84996619f/app/src/main/java/com/authcoinandroid/ui/activity/AuthenticationActivity.java#L86-L170

In case 1 of the switch statement, the identity which we want to use for authentication is chosen, and the listener is set for select challenge button. These listeners which are also used in other switch cases provides synchronized access to Validation and Authentication Process runnable. We add [AuthcoinMessage.java](app/src/main/java/com/authcoinandroid/module/messaging/AuthcoinMessage.java) in the queue. This messages in this queue is executed step by step by [MessageHandler.java](app/src/main/java/com/authcoinandroid/module/messaging/MessageHandler.java) and response is sent to main thread back to AuthenticationActivity where switch case receives them with a different case number. 

In case of listener in case 1, the challenge type is chosen and sent to Validation and Authentication queue. Case 2 presents the evaluation screen for a received challenge. Listener of fulfill button adds evaluation result message to Validation and Authentication Runnable queue. Case 3 displays response from the target after you fulfilled his/her challenge and if you agree to sign the response, then the second factor of authentication is started(if you have chosen MFA in challenge type). Case 4 is similar to case 2, it presents the screen to take the picture and the listener for "send photo" button adds the message to Validation and Authentication runnable queue. Case 5 displays the response of the target and if it's valid or not. Also you can write for how many days you want to trust the target and then you sign the response. "Sign Response" button listener adds SignatureResponseMessage to queue, and finally case 10 displays if authentication was successful and displays Verifiers and Targets challenge records.
