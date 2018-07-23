# commercetools-order-to-confirmation-email-processor

## Goal
Service which can be run as a cron job to ensure that for each commercetools [OrderCreated](https://docs.commercetools.com/http-api-message-types.html#OrderCreated) message an order confirmation e-mail is sent. It will attempt to call  configured web front end URL which in turn is able to send an e-mail for given order UUID. In order to avoid sending same e-mail multiple times order's [syncInfo](https://docs.commercetools.com/http-api-projects-orders.html#syncinfo) is utilized. More technical details can be also found [here](https://github.com/commercetools/commercetools-order-message-processor/blob/master/doc/REQUIREMENTS.MD).

## Configuration
The configuration is split between OS env variables and a custom object in the commercetools platform.

### OS env variables
The OS env variables stores the credentials for the commercetools platform and the name of the custom object.
The following OS env variables are required:

```
ctpProjectKey=""
ctpClientId=""
ctpClientSecret=""
ctpAuthUrl=""
ctpApiUrl=""
```

The following OS env varibales are optional and store the container and key for the custom object configuration:
```
customObjectConfigurationContainerName=""
customObjectConfigurationKey=""
```

The default values for the *container* is **commercetools-order-to-confirmation-email-processor** and for the *key* is **configuration**

### custom object
Stores values that define the behavior of this service.

At the moment the service cannot send Mails itself it just calls an URL that has all capabilities for sending the Mail.
Required entry in the custom object:
```
"emailSenderUrl": "https://mydomain.com/sendorderconfirmation/"
```

Optional entries (with default values) in the custom object:
```
"itemsOfLast": "5d"
"itemsPerPage": 100
"emailSentChannelKey": "orderConfirmationEmail"
"emailSendErrorChannelKey": "orderConfirmationEmailError"
```

The *itemsOfLast* must match the syntax:
```
"^[1-9][0-9]*[hdw]$"
```
It represents several (h)ours, (d)ays or (w)eeks.

## Run Tests
For the integration tests the enviroment variables must be provided

```
mvn clean test
```

## Creating/Starting the docker
```
mvn install -Dmaven.test.skip=true -Pdocker -Ddocker.image.tag=latest
```

```
docker run  commercetools/order-message-processor:latest 
```
