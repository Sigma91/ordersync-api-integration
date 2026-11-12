# Run OrderSync locally

Prerequisites: JDK 25. The Maven Wrapper downloads Maven 3.9.9 automatically
on first use; a global `mvn` installation is not required.

## Windows (PowerShell)

Select your installed JDK 25 for this terminal (replace the example path):

```powershell
$env:JAVA_HOME = 'C:\path\to\jdk-25'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd --version
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Confirm that `--version` reports Java 25 before running tests or the application.
These commands affect only the current terminal. In IntelliJ IDEA, also select
JDK 25 as the Project SDK and Maven runner JRE.

### Optional project-local JDK

The helper script requires an extracted JDK; it does not install Java. To use it,
download the Windows x64 JDK 25
ZIP from [Eclipse Adoptium](https://adoptium.net/temurin/releases/?version=25&os=windows&arch=x64&package=jdk)
and extract it into `.tools/jdk25`. The resulting layout should be
`.tools/jdk25/<jdk-directory>/bin/java.exe`. This directory is ignored by Git.

Once extracted, select it instead of setting `JAVA_HOME` manually:

```powershell
. .\scripts\use-java25.ps1
.\mvnw.cmd --version
```

### Submit an order

In another PowerShell terminal, submit an order:

```powershell
$body = @{ orderId = 'ORD-101'; customerName = 'John'; amount = 150.00; currency = 'EUR' } | ConvertTo-Json
Invoke-RestMethod -Uri http://localhost:8080/api/orders -Method Post -ContentType 'application/json' -Body $body
```

## Linux/macOS

Select your installed JDK 25 via `JAVA_HOME` and `PATH`, then run:

```bash
export JAVA_HOME=/path/to/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
sh ./mvnw --version
sh ./mvnw test
sh ./mvnw spring-boot:run
```

In another terminal:

```bash
curl -i -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"ORD-101","customerName":"John","amount":150.00,"currency":"EUR"}'
```

Expected HTTP status: 202 Accepted; body:
```json
{"reference":"MOCK-ORD-101","status":"SUBMITTED_TO_MOCK"}
```

The request is mapped to a synthetic provider model and handled by an in-process mock provider. No network calls, persistence, real shipment, or delivery guarantee exists.
