- Fetches all orders for which order confirmation mail has not been sent yet
```not syncInfo(channel(id="order-confirmation-mail-channel-uuid")) and lastModifiedAt > now - configured amount of time (i.e.: 48h / 2d / 1w) and not syncInfo(channel(id="order-confirmation-email-error-channel-uuid"))```
Default is to fetch orders from last 5 days (5d). It should process configured amount of orders per page. Default page size is ```100```

- For each fetched order matching the query it should call configured ```emailSenderUrl``` (web shop front end or another publicly available service) which sends an email and responds with http status code ```200```. Example URL: ```https://mydomain.com/sendorderconfirmation/order-uuid```

- In case ```emailSenderUrl``` is not provided configuration should define fully qualified class path to one of send mail services available in  [commercetools-sunrise-java-email](https://github.com/commercetools/commercetools-sunrise-java-email) module, email sender configuration and custom object container and key where handlebars email template is provided

- In case customer email can not be send due to unrecoverable error (like missing email address) the order will get ```syncInfo``` with channel key=```email-error-channel-key``` and short description of the error as ```externalId```

- In case required configuration is not provided application should terminate with runtime exception and user friendly error message

- Application should provide docker container

- Application should be able to log to console output, file, or to both (depending on configuration). Log level can be set over configuration

- Application should provide integration tests

- Application should provide documentation (how to run, how to run tests, how to use/configure)

- On startup application should setup required / configured channels (if those are not created yet)

Example configuration:
```
{
  "ctp": {
    "client_secret": "xxx",
    "client_id": "xxx",
    "project_key": "xxx"
  },
  "emailSenderUrl": "https://mydomain.com/sendorderconfirmation/order-uuid",
  "email-send-channel-key": "order-confirmation-email",
  "email-error-channel-key": "order-confirmation-email-error"
}
```

Example configuration 2:
 ```
 {
   "ctp": {
     "client_secret": "xxx",
     "client_id": "xxx",
     "project_key": "xxx"
   },
   "emailSenderClassPath": "io.commercetools.sunrise.email.smtp.SmtpAuthEmailSender",
   "emailSenderConfiguration": {
     "host": "xxx",
     "port": "xxx",
     "transportSecurity": "xxx",
     "username": "xxx",
     "password": "xxx"
   },
   "emailSenderTemplate": {
     "custome-object-container": "email",
     "custom-object-key": "order-confirmation-template"
   }
   "email-send-channel-key": "order-confirmation-email",
   "email-error-channel-key": "order-confirmation-email-error"
}
 ```