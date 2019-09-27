# DID VOTING SERVICE API
==============

## Summary

This repo provide simple HTTP Restful API for  developers to send user blessing on elastos did side chain.

## Build with maven

In project directory, use maven command:
```Shell
$uname mvn clean compile package
```
If there is build success, Then the package blessing.star.service.api-0.0.1.jar will be in target directory.

## Configure project properties

in file: application.properties.

```yaml
## service url
elaservice.didServicePrefix      = https://api-wallet-did-testnet.elastos.org
elaservice.didExplorerUrl     = http://sidebackend-testnet.bbjb2qwn2i.ap-northeast-1.elasticbeanstalk.com

## Block agent Access Key
accesskey.id  = org.elastos.bless.star
accesskey.secret = SmATCfWN1LqHH8b5bbRDbaz0IMhA5u

## DID
did.node =  http://localhost:21334
did.did = iV8D3SfqUZUomodfmarPHdnfCScnNMgipJ
did.privateKey = 9DCD16F1E6975E056E569AAED5D0D149FE95DDB603A3DD85D61F08D145C8B770
did.publicKey = 03513E5A68ED091227CC507EA16171EA53EF584AB30968DFAB40C44C2910D7EE95

```

## Run

Copy blessing.star.service.api-0.0.1.jar to your deploy directory.
then use jar command to run this spring boot application.

```shell
$uname java -jar did.voting.service.api-0.0.1.jar
```
## Web Service APIs

### Send Blessing on chain
```yaml
HTTP: POST
URL: /api/1/didvoting/save
HEADERS: 
    Content-Type: application/json
```

We send voting data on chain like this:
```url
http://localhost:8095/api/1/didvoting/save
```
Post body like
```json
{
	"address":"did-voting/<topic-id>/<topic-object>",
	"data":"{
              Topic : {
                  Creator:”did”,
                  Name:””,
                  Desc:””,
                  Max-Selections: “3”,
                  Starting-height :””,
                  End-height:””,
                  Options:[
                    {
                        OptionID:””,
                        Name:””,
                        Desc:””
                     },
                    {}
                  ]
              },
              Signature: creator-did.sign(topic object)
            }"
}
```

If Success, we will get response like:
```json
{
    "data": {
        "txid": "efd47380fa1dd8cc7823ae7cbc513bf7e69ed05aaef21c071a582607a6d4d246"
    },
    "status": 200
}
```


