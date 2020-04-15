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
