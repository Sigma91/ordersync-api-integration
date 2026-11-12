# Java 25 compatibility (OS-JAVA25-001)

OrderSync requires JDK 25 for compilation, tests, and runtime. The Spring Boot
parent maps `java.version=25` to the Maven compiler's `--release 25`, producing
class file version 69. No preview features or experimental JVM flags are used.

## Dependency decision

Change the Spring Boot parent from 3.3.13 to 3.5.6 and retain its dependency and
plugin management without individual version overrides. This keeps the change
to one parent version and one Java property, while supplying a compatible stack.

[Boot 3.3.13 supports Java through 23](https://docs.spring.io/spring-boot/3.3/system-requirements.html)
and [Boot 3.4 supports Java through 24](https://docs.spring.io/spring-boot/3.4/system-requirements.html).
The [Boot Java 25 support issue](https://github.com/spring-projects/spring-boot/issues/47245)
documents readiness in 3.5.5 and the documentation update in 3.5.6. Select 3.5.6
as the first patch documenting that support. A Byte Buddy override on Boot 3.3
would fix the immediate mock failure but leave Boot outside its supported Java
range. Boot 4 would require a broader migration.

The [Byte Buddy compatibility table](https://github.com/raphw/byte-buddy#java-version-compatibility)
documents Java 25 support in the 1.17 line. The Boot 3.5.6 BOM supplies 1.17.7 for
both Byte Buddy artifacts, supporting Mockito's instrumentation of Java 25 classes.

| Component | Original | Updated | Reason |
| --- | --- | --- | --- |
| Java compiler release | 17 | 25 | Required target and runtime |
| Spring Boot parent, starters, Maven plugin | 3.3.13 | 3.5.6 | Documented Java 25 support |
| Spring Framework | 6.1.21 | 6.2.11 | Boot-managed compatible framework |
| Mockito core and JUnit Jupiter integration | 5.11.0 | 5.17.0 | Boot-managed test stack |
| Byte Buddy and Byte Buddy agent | 1.14.19 | 1.17.7 | Java 25 class file support |
| Maven Compiler Plugin | 3.13.0 | 3.14.0 | Boot-managed compiler plugin |
| Maven Surefire (and managed Failsafe) | 3.2.5 | 3.5.4 | Boot-managed test plugins |
| JUnit Jupiter | 5.10.5 | 5.12.2 | Boot-managed test stack |
| Jackson BOM | 2.17.3 | 2.19.2 | Boot-managed JSON stack |
| Hibernate Validator | 8.0.2.Final | 8.0.3.Final | Boot-managed validation stack |
| Embedded Tomcat | 10.1.42 | 10.1.46 | Boot-managed servlet container |

Maven Resources Plugin 3.3.1 and Maven JAR Plugin 3.4.2 remain unchanged.
The Maven Wrapper remains on 3.9.9; local verification uses Maven 3.9.11.
Other transitive dependencies follow the Boot BOM; none are independently pinned.

## Verification

Run `mvn -B clean verify` (or the Maven Wrapper equivalent) using JDK 25.
GitHub Actions selects Temurin 25 and runs `mvn -B verify`.
Start the packaged application with `java -jar target/ordersync-0.1.0-SNAPSHOT.jar`
and submit the example in [RUN_LOCAL.md](RUN_LOCAL.md).

Application and test source files are unchanged. Existing tests cover acceptance,
validation, malformed JSON, provider mapping and failure propagation, idempotency
conflicts, retry after failure, and concurrent duplicate submissions.

Local verification on 2026-11-12 with Oracle JDK 25.0.2 and Maven 3.9.11:

- Original suite: 10 tests, 7 passed, 0 assertion failures, 3 errors, 0 skipped.
  The controller context failed when Byte Buddy rejected Java 25 class files.
- Updated `mvn -B -ntp clean verify`: BUILD SUCCESS; 10 passed, 0 failures,
  0 errors, 0 skipped (controller: 3; integration service: 3; idempotency: 4).
- Packaged JAR started successfully in 1.682 seconds on an ephemeral loopback
  port. All 13 live HTTP checks passed: normalized acceptance, duplicate receipt,
  conflict response, invalid values, malformed JSON, missing fields, maximum
  order ID and customer lengths, blank customer, currency lengths, and minimum
  amount rejection/acceptance. The verification process was stopped afterward.
- `javap` confirmed class file version 69. Effective POM confirmed compiler
  release 25. Application and test source SHA-256 hashes remained unchanged.

The Java 25 upgrade was merged through PR #6 as main commit
`56fdf560434a5b27268615dde332df57dd8fdcc5`. The
[successful Java CI run](https://github.com/Sigma91/ordersync-api-integration/actions/runs/43325671229)
targets that exact merge commit. These are historical upgrade verification
results; they do not claim that later commits have been tested.

Java 25 may print warnings about Maven's Guice use of `sun.misc.Unsafe` and
Mockito's dynamic agent attachment. These are not test failures. No warnings
are suppressed. The existing `@MockBean` usage is deprecated by Boot 3.5 but
remains supported; migration of test annotations is outside this upgrade.

This is a minimum compatibility upgrade, not a refresh to the latest Boot patch
or a dependency security audit. Newer Boot 3.5 patches can be assessed separately.
