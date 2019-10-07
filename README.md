# wallet-service-showcase

Simple REST API service for managing and transferring funds between accounts (wallets).

Initially implemented as a "home" task for job application.

## Features

- Java 11
- Support for highly-concurrent API usage
- Using arbitrary precision storage for wallet balance (BigDecimal)
- "Idempotent-ish" API
- Frameworkless approach: service runs on built-in Java HttpServer
- TDD applied (using common sense)
- In-memory storage

## API

N.B: using client-generated UUIDs for all IDs (wallet ID, transaction ID).

N.B #2: using strings for storing balance and other amounts to avoid hassle with losing precision
in clients (e.g. if double is used for representing JSON floating point numbers). 

### Wallet management

#### Wallet creation

```
POST /api/v1/wallet
```

request body:

```json
{
  "id": "f422322b-39a7-4b08-a610-5a9e584dad5f", // Wallet ID
  "initialBalance": "10" // balance as string
}
```

No response on successful invocation.

#### Wallet removal

```
DELETE /api/v1/wallet/<wallet_id>
```

No response on successful invocation.

#### Get wallet balance

```
GET /api/v1/wallet/<wallet_id>
```

Response body:

```json
{
  "id": "f422322b-39a7-4b08-a610-5a9e584dad5f", // Wallet ID
  "balance": "145" // balance as string
}
```

### Transactions (transferring funds)

```
POST /api/v1/transaction
```

request body:

```json
{
  "id": "d6c5d110-cbd0-4384-89dc-b4157708edfb", // transaction ID
  "from": "f422322b-39a7-4b08-a610-5a9e584dad5f", // "from" wallet ID
  "to": "3ee42d94-7db8-445d-bd32-4db1d4d142a0", // "to" wallet ID
  "amount": "9.85" // amount as string
}
```

No response on successful invocation.

### Miscellaneous

Health-check (currently only checks HTTP server responsiveness):

```
GET /api/v1/health-check
```

No response on successful invocation.

## TODO

- Transaction log
- Swagger
- Support for currency
- Proper persistence
- Cluster mode

## How to run

Application uses Gradle build system. You can start application on default port (8080) by executing in root folder:

```
./gradlew run
```

Or for Windows OS:

```
gradlew.bat run
```

You can specify port to listen on as a first command line argument, e.g.:

```
./gradlew run 8888
```

Another option is to build application JAR and run it in a regular way.