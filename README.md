# Keycloak Phone Provider

- Phone support like e-mail
- OTP by phone
- Register with phone
- Authentication by phone

sms
voice
phone one key login

With this provider you can **enforce authentication policies based on a verification token sent to users' mobile phones**.
Currently, there are implementations of Twilio and TotalVoice and YunTongXun SMS sender services. That said, is nice to note that more
services can be used with ease thankfully for the adopted modularity and in fact, nothing stop you from implementing a
sender of TTS calls or WhatsApp messages.

This is what you can do for now:

- Check ownership of a phone number (Forms and HTTP API)
- Use SMS as second factor in 2FA method (Browser flow)
- Reset Password by phone (Testing)
- Authentication by phone (HTTP API)
- Authentication everybody by phone, auto create user on Grant(HTTP API)
- Register with phone
- Register only phone (user name is phone number)
- Register add user attribute with redirect_uri params

Two user attributes are going to be used by this provider: _phoneNumberVerified_ (bool) and _phoneNumber_ (str). Many
users can have the same _phoneNumber_, but only one of them is getting _phoneNumberVerified_ = true at the end of a
verification process. This accommodates the use case of pre-paid numbers that get recycled if inactive for too much time.

## Client:

see my project [KeycloakClient](https://github.com/cooper-lyt/KeycloakClient) ,is android client, nothing stop you from implementing other java program.

## Compatibility

This was initially developed using 11.0.3 version of Keycloak as baseline, and I did not test another user storage beyond
the default like Kerberos or LDAP. I may try to help you but I cannot guarantee.

## Usage

docker image is [coopersoft/keycloak-phone:11.0.3](https://hub.docker.com/layers/coopersoft/keycloak-phone/11.0.3/images/sha256-cfb890c723a2b9970c59f0bf3e0310499bb6e27e33d685edbc77d992ae15c4c9?context=repo)
for examples [docker-compose.yml](https://raw.githubusercontent.com/cooper-lyt/keycloak-phone-provider/master/examples/docker-compose.yml)
run as `docker-compose up` , docker-compose is required! (image base on [keycloak-callback:11.0.3](https://github.com/cooper-lyt/keycloak-callback-provider) provide registration callback for http get , post , rocketmq , or else)

<hr />

Comment unwanted providers from Dockerfile, and from jboss-cli/
(In root of repo) -> creates target directory with Wildly folder structure
mvn clean
mvn package
mvn docker:build

Change provider in docker compose
Cd into examples/snapshot/keycloak
./run-local.sh

Keycloak is ready on http://localhost:8901
user: admin
pass: admin

<hr />

If you want to build the project, simply run `mvn clean package docker:build` after cloning the repository.
At the end of the goal.

- local keycloak installed: copt the `target` directory all jars correctly placed in a WildFly-like folder structure.
- docker image build: for examples [run-local.sh](https://github.com/cooper-lyt/keycloak-phone-provider/blob/master/examples/snapshot/run-local.sh) or [run-remote.sh](https://github.com/cooper-lyt/keycloak-phone-provider/blob/master/examples/snapshot/run-remote.sh).

**Installing:**

1. Merge that content with the root folder of Keycloak. You can of course delete the modules of services you won't use,
   like TotalVoice if you're going to use Twilio.
2. Open your `standalone.xml` (or equivalent) and (i) include the base module and at least one SMS service provider in
   the declaration of modules for keycloak-server subsystem. (ii) Add properties for overriding the defaults of selected
   service provider and expiration time of tokens. (iii) Execute the additional step specified on selected service provider
   module README.md.
3. Start Keycloak.

i. add modules defs

```xml
<subsystem xmlns="urn:jboss:domain:keycloak-server:1.1">
    <web-context>auth</web-context>
    <providers>
        <provider>classpath:${jboss.home.dir}/providers/*</provider>
        <provider>module:keycloak-phone-provider</provider>
        <provider>module:keycloak-sms-provider-dummy</provider>
    </providers>
...
```

ii. set provider and token expiration time

```xml
<spi name="phoneMessageService">
    <provider name="default" enabled="true">
        <properties>
            <property name="service" value="TotalVoice"/>
            <property name="tokenExpiresIn" value="60"/>
        </properties>
    </provider>
</spi>
```

## OTP by Phone

in Authentication page, copy the browser flow and add a subflow to the forms, then adding `OTP Over SMS` as a
new execution. Don't forget to bind this flow copy as the de facto browser flow.
Finally, register the required actions `Update Phone Number` and `Configure OTP over SMS` in the Required Actions tab.

<hr/>

## get Access token use endpoints

### Only use phone login (requires being registered) :

Under Authentication > Flows:

- Copy the 'Direct Grant' flow to 'Direct grant with phone' flow
- Click on 'Actions > Add execution' on the 'Provide Phone Number' line
- Click on 'Actions > Add execution' on the 'Provide Verification Code' line
- Delete or disable other
- Set both of 'Provide Phone Number' and 'Provide Verification Code' to 'REQUIRED'

Your can either bind a client to this flow:

- Under _Clients > $YOUR_CLIENT_
- _Authentication Flow Overrides_
- Set **Direct Grant** Flow to _Direct grant with phone_  
  OR  
  You can set it as global default by
- _'Authentication > Bindings'_
- Set **Direct Grant** Flow to _'Direct grant with phone'_

<hr/>

## Everybody phone number

### ( if not exists create user by phone number) get Access token use endpoints:

Under Authentication > Flows:

- Copy the 'Direct Grant' flow to 'Direct grant everybody with phone' flow
- Click on 'Actions > Add execution' on the 'Authentication Everybody By Phone' line
- Delete or disable other
- Set 'Authentication Everybody By Phone' to 'REQUIRED'

Under 'Clients > $YOUR_CLIENT > Authentication Flow Overrides' or 'Authentication > Bindings'
Set Direct Grant Flow to 'Direct grant everybody with phone'

<hr />

## Reset credential

Testing , coming soon!

## Phone one key longin

Testing , coming soon!

<hr />

## Phone registration support

Go to the `Realm Settings` left menu and click it. Then go to the `Login` tab. There is a `User Registration` switch on this tab. Turn it on, then click the Save button. After you enable this setting, a `Register` link should show up on the login page.

Under Authentication > Flows:

- Create flows from registration:
  Copy the 'Registration' flow to 'Registration fast by phone' flow.

- (Optional) Phone number used as username for new user:  
   Delete or disable 'Registration User Creation'.
  Click on 'Registration Fast By Phone Registration Form > Actions > Add execution' on the 'Registration Phone As Username Creation' line.
  Move this item to first.
- Add phone number to profile
  Click on 'Registration Fast By Phone Registration Form > Actions > Add execution' on the 'Phone Validation' line

- (Optional)Hidden all other field phone except :  
   Click on 'Registration Fast By Phone Registration Form > Actions > Add execution' on the 'Registration Least' line

- (Optional)Read query parameter add to user attribute:
  Click on 'Registration Fast By Phone Registration Form > Actions > Add execution' on the 'Query Parameter Reader' line
  Click on 'Registration Fast By Phone Registration Form > Actions > configure' add accept param name in to

- (Optional)Hidden password field:
  Delete or disable 'Password Validation'.

Set All add item as Required.

Under Authentication > Bindings
Set Registration Flow to 'Registration fast by phone'

Under Realm Settings > Themes
Set Login Theme as 'phone'

Final settings should look like this [this](https://user-images.githubusercontent.com/4219608/105630188-95fbd200-5e82-11eb-8510-6eac4ee0f2f5.png)

test:
http://{addr}/auth/realms/{realm name}/protocol/openid-connect/registrations?client_id={client id}&response_type=code&scope=openid%20email&redirect_uri={redirect_uri}

<hr />

## About the API endpoints:

You'll get 2 extra endpoints that are useful to do the verification from a custom application.

- GET /auth/realms/{realmName}/sms/verification-code?phoneNumber=+5534990001234 (To request a number verification. No auth required.)
- POST /auth/realms/{realmName}/sms/verification-code?phoneNumber=+5534990001234&code=123456 (To verify the process. User must be authenticated.)

You'll get 2 extra endpoints that are useful to do the access token from a custom application.

- GET /auth/realms/{realmName}/sms/authentication-code?phoneNumber=+5534990001234 (To request a number verification. No auth required.)
- POST /auth/realms/shuashua/protocol/openid-connect/token  
  Content-Type: application/x-www-form-urlencoded  
  Body:
  - grant_type=password
  - phone_number=$PHONE_NUMBER
  - code=$VERIFICATION_CODE
  - client_id=$CLIENT_ID
  - client_secret=CLIENT_SECRECT

And then use Verification Code authentication flow with the code to obtain an access code.

## Thanks

Some code written is based on existing ones in these two projects: [keycloak-sms-provider](https://github.com/mths0x5f/keycloak-sms-provider)
and [keycloak-phone-authenticator](https://github.com/FX-HAO/keycloak-phone-authenticator). Certainly I would have many problems
coding all those providers blindly. Thank you!

————REGISTER————

When a user registers Screen
Send code button
URL = http://localhost:8901/auth/realms/bitloops/sms/registration-code?phoneNumber=6978756666
Method=‘GET’
Cookie: KC_RESTART=eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJkMmI0ZGMxMC0xOGVlLTQwZDAtOGUyMi1iN2RjNWM2ZmY3MGEifQ.eyJjaWQiOiJzZWN1cml0eS1hZG1pbi1jb25zb2xlIiwicHR5Ijoib3BlbmlkLWNvbm5lY3QiLCJydXJpIjoiaHR0cDovL2xvY2FsaG9zdDo4OTAxL2F1dGgvYWRtaW4vYml0bG9vcHMvY29uc29sZS8jL2ZvcmJpZGRlbiIsImFjdCI6IkFVVEhFTlRJQ0FURSIsIm5vdGVzIjp7InNjb3BlIjoib3BlbmlkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4OTAxL2F1dGgvcmVhbG1zL2JpdGxvb3BzIiwicmVzcG9uc2VfdHlwZSI6ImNvZGUiLCJjb2RlX2NoYWxsZW5nZV9tZXRob2QiOiJTMjU2IiwicmVkaXJlY3RfdXJpIjoiaHR0cDovL2xvY2FsaG9zdDo4OTAxL2F1dGgvYWRtaW4vYml0bG9vcHMvY29uc29sZS8jL2ZvcmJpZGRlbiIsInN0YXRlIjoiMTZlZmQ3YTktM2Y4NS00YmUwLWI4OGEtMzVkNGZkYWYyY2NmIiwibm9uY2UiOiI5NDg3MjgzZi02NGFiLTQ1YzgtODM5YS03YTUyMGM5ZmQ0Y2EiLCJjb2RlX2NoYWxsZW5nZSI6IktSUkdvZjRtNFJmWnZNZjVON3JNeDRCUktYNzhZc0UwWDZRaDJXRTZPbTQiLCJyZXNwb25zZV9tb2RlIjoiZnJhZ21lbnQifX0.6rzrjEPwdcytl6C0vHTRGOZ9KA-4XnaBsNytTrt0Avw; AUTH_SESSION_ID=056ebeab-383e-46aa-be22-1878498eeed0.6bc6be4fb49f; AUTH_SESSION_ID_LEGACY=056ebeab-383e-46aa-be22-1878498eeed0.6bc6be4fb49f

Register When we have code

URL: http://localhost:8901/auth/realms/bitloops/login-actions/registration?session_code=WkbCOSu2uxfrPncqOizTwR16M5q7wCwTjiuCfY4LxAQ&execution=e166bebc-d640-4296-ab2c-dff33ab9eee8&client_id=security-admin-console&tab_id=G32tDE6WwOQ
Content-Type: application/x-www-form-urlencoded

-     	Cookie: AUTH_SESSION_ID=52276154-8fe8-42b4-8705-ba7d2f93a659.6bc6be4fb49f; AUTH_SESSION_ID_LEGACY=52276154-8fe8-42b4-8705-ba7d2f93a659.6bc6be4fb49f; KC_RESTART=eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJkMmI0ZGMxMC0xOGVlLTQwZDAtOGUyMi1iN2RjNWM2ZmY3MGEifQ.eyJjaWQiOiJzZWN1cml0eS1hZG1pbi1jb25zb2xlIiwicHR5Ijoib3BlbmlkLWNvbm5lY3QiLCJydXJpIjoiaHR0cDovL2xvY2FsaG9zdDo4OTAxL2F1dGgvYWRtaW4vYml0bG9vcHMvY29uc29sZS8jL2ZvcmJpZGRlbiIsImFjdCI6IkFVVEhFTlRJQ0FURSIsIm5vdGVzIjp7InNjb3BlIjoib3BlbmlkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4OTAxL2F1dGgvcmVhbG1zL2JpdGxvb3BzIiwicmVzcG9uc2VfdHlwZSI6ImNvZGUiLCJjb2RlX2NoYWxsZW5nZV9tZXRob2QiOiJTMjU2IiwicmVkaXJlY3RfdXJpIjoiaHR0cDovL2xvY2FsaG9zdDo4OTAxL2F1dGgvYWRtaW4vYml0bG9vcHMvY29uc29sZS8jL2ZvcmJpZGRlbiIsInN0YXRlIjoiYzg2ZDU1NjQtMDcwZS00OGIwLThmZTYtZGI1OTJkM2MzNmU4Iiwibm9uY2UiOiJjN2Y0ODJlMi00NTJhLTQzODctODUxZi1mNzQwNmQ2OTE0NjkiLCJjb2RlX2NoYWxsZW5nZSI6IlU0WFhHOVZCVC1Icmljd1ZNY0RuTXVESWZsTzEwSXotSm8xd3pIMzgtb3MiLCJyZXNwb25zZV9tb2RlIjoiZnJhZ21lbnQifX0.nAd7Ih0V6KdxathbW_KfpANk_BdQ2L96td1WaV_kiwg

Query String Parameters session_code: WkbCOSu2uxfrPncqOizTwR16M5q7wCwTjiuCfY4LxAQ

-     	execution: e166bebc-d640-4296-ab2c-dff33ab9eee8
-     	client_id: security-admin-console
-     	tab_id: G32tDE6WwOQ
  Form Data
-     	phoneNumber: 6978756666
-     	registerCode: 559195
