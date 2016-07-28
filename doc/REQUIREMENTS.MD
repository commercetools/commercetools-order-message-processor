- Fetches all orders for which order confirmation mail has not been sent yet
```not syncInfo(channel(id="emailSentChannelUuid")) and lastModifiedAt > now - configured amount of time (i.e.: 48h / 2d / 1w) and not syncInfo(channel(id="emailSendErrorChannelUuid"))```
Default is to fetch orders from last 5 days (5d). It should process configured amount of orders per page. Default page size is ```100```

- For each fetched order matching the query it should call configured ```emailSenderUrl``` (web shop front end or another publicly available service) which sends an email and responds with http status code ```200```. Example URL: ```https://mydomain.com/sendorderconfirmation/order-uuid```

- In case ```emailSenderUrl``` is not provided configuration should define fully qualified class path to one of send mail services available in  [commercetools-sunrise-java-email](https://github.com/commercetools/commercetools-sunrise-java-email) module, email sender configuration and custom object container and key where handlebars email template is provided

- In case customer email can not be sent due to unrecoverable error (like missing email address) the order will get ```syncInfo``` with channel key=```emailSendErrorChannelKey``` and short description of the error as ```externalId```

- In case required configuration is not provided application should terminate with run time exception and user friendly error message

- Application should provide docker container

- Application should be able to log to console output, file, or to both (depending on configuration). Log level can be set over configuration

- Application should provide integration tests

- Application should provide documentation (how to run, how to run tests, how to use/configure)

- On start up application should setup required / configured channels (if those are not created yet)

Configuration example (minimum):
```
{
  "commercetools": {
    "projectKey": "",
    "clientId": "",
    "clientSecret": "",
    "authUrl": "",
    "apiUrl": ""
  },
  "emailSenderUrl": "https://mydomain.com/sendorderconfirmation/order-uuid"
}
```

Configuration example:
```
 {
   "commercetools": {
    "projectKey": "",
    "clientId": "",
    "clientSecret": "",
    "authUrl": "",
    "apiUrl": ""
  },
  "itemsOfLast": "5d",
  "itemsPerPage": 100,
   "emailSender": {
     "classPath": "io.commercetools.sunrise.email.smtp.SmtpAuthEmailSender",
     "configuration": {
       "host": "",
       "port": "",
       "transportSecurity": "",
       "username": "",
       "password": ""
     },
     "template": {
       "customObjectContainer": "email",
       "customObjectKey": "orderConfirmationTemplate"
     }
   },
   "emailSentChannelKey": "orderConfirmationEmail",
   "emailSendErrorChannelKey": "orderConfirmationEmailError"
}
```