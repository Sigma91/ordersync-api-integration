# ordersync-api-integration
Java Spring Boot API integration and data transformation demo

Requires JDK 25. Maven 3.9.9 is downloaded automatically by the Maven Wrapper on first use (internet access required).

On Windows (PowerShell):

```powershell
. .\scripts\use-java25.ps1
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

The first command selects a project-local JDK 25 for the current terminal. See
[local setup and API examples](docs/RUN_LOCAL.md) to install it or use your own JDK 25.
Use `mvnw.cmd` even when `mvn` is not installed.

See [Java 25 compatibility notes](docs/JAVA25_UPGRADE.md) for dependency versions
and the reasons for the upgrade.

On Linux/macOS:

```sh
sh ./mvnw test
sh ./mvnw spring-boot:run
```
