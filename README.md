# SWIFT gpi API demo application in Java (SpringBoot and Picocli)

Make calls to SWIFT APIs is easy using SWIFT SDK. All you need to do is add the SWIFT SDKs as dependency when building
your Java application through Maven or Gradle.

We built this demo Java application with Maven to show you how we are using it to make calls to SWIFT gpi APIs in
the [API Sandbox](https://developer.swift.com/reference#gsg).

## Getting Started ##

### Prerequisites ###

* Java 17 and above
* Gradle 8.* and above

### Install SWIFT SDK ###

Download [SWIFT SDK](https://developer.swift.com/swift-sdk) from SWIFT Developer Portal, login is required for download.

Unpackage the zip file and copy the dependency in your local [./libs](libs) repository.

```bash
export SWIFT_SDK_VERSION=2.17.3-7
unzip swift-sdk-$SWIFT_SDK_VERSION.zip
cp swift-sdk-$SWIFT_SDK_VERSION/lib/*.jar ./libs
```

### Install SWIFT Security SDK ###

Download [SWIFT Security SDK](https://developer.swift.com/swift-sdk) from SWIFT Developer Portal, login is required for
download.

Unpackage the zip file and copy the dependency in your local [./libs](libs) repository.

```bash
export SWIFT_SDK_VERSION=2.17.3-9
unzip swift-security-sdk-$SWIFT_SDK_VERSION.zip
cp swift-security-sdk-$SWIFT_SDK_VERSION/lib/*.jar ./libs
```

### Configure runtime SDK properties ###

Update ```config/config-swift-connect.yaml``` with your application credentials, consumer-key & consumer-secret. Obtain
from SWIFT Developer Portal by [creating an app](https://developer.swift.com/reference#sandbox-getting-started).

To use forward proxies update ```config/config-swift-connect-fp.yaml``` with your application credentials,
consumer-key & consumer-secret and forward proxy information. Obtain from SWIFT Developer Portal
by [creating an app](https://developer.swift.com/reference#sandbox-getting-started).

### Build ###

```bash
./gradlew clean build
```

### Run ###

```bash
java -Dswift.connect.config=config/config-swift-connect.yaml \
      -jar build/libs/gpi-v5-demo-app-0.0.1.jar
```

To use forward proxies:

```bash
java -Dswift.connect.config=config/config-swift-connect-fp.yaml \
      -jar build/libs/gpi-v5-demo-app-0.0.1.jar
```

## Keystore

The keystore (conf/keystore.jks) has these several entried that may need renewing when the Sandbox certificate is
renewed:

- globalsign_root_ca_r3, 18 Oct 2022, trustedCertEntry, Sandbox server root ca
- globalsign_rsa_ov_ssl_ca_2018, 18 Oct 2022, trustedCertEntry, Sandbox server sub ca
- sandbox_pub, 18 Oct 2022, trustedCertEntry, Sandbox server cert. Potentially not needed given its roots are present (
  see above)
- selfsigned, 18 Jun 2020, PrivateKeyEntry, Demo client key

## Authors

alex.salinas@swift.com
vijay.mukundhan@swift.com
rachel.cousins@swift.com
