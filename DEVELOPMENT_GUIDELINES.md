# Development Guidelines and Contribution Standards: `PICC-PP-User-Portal-Backend`

This document defines the architectural standards, development workflows, coding conventions, and security requirements for contributors to **`PICC-PP-User-Portal-Backend`**.

---

## Table of Contents

1. [Architecture & Design Principles](#1-architecture--design-principles)
2. [Development Environment Setup](#2-development-environment-setup)
3. [Package Structure & Code Navigation](#3-package-structure--code-navigation)
4. [Coding Standards & Best Practices](#4-coding-standards--best-practices)
   - [Dependency Injection & Constructor Wiring](#dependency-injection--constructor-wiring)
   - [Safe Property Injections & Dynamic Fallbacks](#safe-property-injections--dynamic-fallbacks)
   - [Redis Caching Best Practices](#redis-caching-best-practices)
   - [Resilient External API Integrations](#resilient-external-api-integrations)
   - [Exception Handling Conventions](#exception-handling-conventions)
   - [Logging & Sensitive Data Masking](#logging--sensitive-data-masking)
5. [Security, Code Quality & Compliance Tooling](#5-security-code-quality--compliance-tooling)
   - [SAST: SpotBugs & FindSecBugs](#sast-spotbugs--findsecbugs)
   - [SCA: OWASP Dependency-Check](#sca-owasp-dependency-check)
   - [SBOM: CycloneDX Aggregate Generation](#sbom-cyclonedx-aggregate-generation)
   - [Checkstyle: Google Java Style](#checkstyle-google-java-style)
6. [Git Workflow & Branching Strategy](#6-git-workflow--branching-strategy)
   - [Branch Naming Conventions](#branch-naming-conventions)
   - [Conventional Commits](#conventional-commits)
7. [Pull Request (PR) Checklist](#7-pull-request-pr-checklist)
8. [Release Lifecycle & Versioning](#8-release-lifecycle--versioning)

---

## 1. Architecture & Design Principles

`PICC-PP-User-Portal-Backend` serves as the control plane for tenant portal experiences. All modifications must comply with these core tenets:

1. **Zero-Trust Hardcoded Configurations**: Never commit private IP addresses, company internal domains, default production passwords, or static tokens. All configurable values must use `@Value("${property.name:default}")` with safe fallbacks.
2. **Layered Separation of Concerns**: REST Controllers must never invoke persistence repositories directly. All domain operations and transactional boundaries (`@Transactional`) belong in the Service Layer.
3. **Resilient Startup & Graceful Degradation**: External dependencies (Redmine, SigNoz, Kafka, Prometheus) must not crash the service startup if temporarily unavailable in development environments.
4. **Cache Invalidation Discipline**: Any mutative updates to user roles, feature associations, or element details must cleanly evict related Redis cache keys (`urls:{userId}:{envId}`).
5. **Least-Privilege Security**: Container images must run as a non-root user (`appuser` UID 1001), and internal Actuator endpoints must sanitize environment and configuration property values (`show-values=NEVER`).

---

## 2. Development Environment Setup

### Required Tools
- **JDK 21 LTS** (Eclipse Temurin 21 or OpenJDK 21).
- **Maven 3.9+** (or use the repository's `./mvnw` / `.\mvnw.cmd`).
- **PostgreSQL 16** & **Redis 7** (can be spun up via `docker-compose up -d postgres redis`).
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with:
  - Lombok Annotation Processor enabled.
  - Checkstyle plugin with Google Java Style.

---

## 3. Package Structure & Code Navigation

```
src/main/java/com/nnp/dashboard/
├── builder/        # Query builders (e.g. SignozQueryBuilder)
├── client/         # HTTP and telemetry client interfaces and implementations
├── config/         # Spring @Configuration classes (Database, Redis, OpenAPI, Redmine)
├── controller/     # REST API controllers exposing HTTP endpoints
├── dto/            # Data Transfer Objects for API requests and telemetry
├── exception/      # Custom exceptions and global ControllerAdvice handlers
├── features/       # Modular feature packages (e.g. predictioncomparison)
├── model/          # JPA entity domain models
├── repo/           # Spring Data JPA repositories (multi-datasource partitioned)
├── scheduler/      # Background telemetry and usage data collectors
├── service/        # Core business service logic and domain transactions
└── vo/             # View Objects and presentation models
```

---

## 4. Coding Standards & Best Practices

### Dependency Injection & Constructor Wiring
Favor constructor injection over `@Autowired` on private fields:
```java
@Service
public class ExampleService {
    private final ExampleRepository repository;

    public ExampleService(ExampleRepository repository) {
        this.repository = repository;
    }
}
```

### Safe Property Injections & Dynamic Fallbacks
Always declare safe defaults in `@Value` annotations:
```java
// Correct:
@Value("${kafka.topic.podusage:nnp-pod-usage}")
private String podUsageKafkaTopic;

// Avoid:
@Value("${kafka.topic.podusage}")
private String podUsageKafkaTopic;
```

### Redis Caching Best Practices
- Structure cache keys consistently using namespaced prefixes (e.g. `urls:{userId}:{envId}`).
- Always assign a sensible TTL (`redisTemplate.expire(key, 9000, TimeUnit.SECONDS)`).
- Handle Redis connection timeouts gracefully without breaking core application flows.

### Resilient External API Integrations
When configuring HTTP clients (WebClient / HttpClientWrapper), configure explicit connect and read timeouts (e.g., 20 seconds). Avoid unbounded blocking on third-party APIs.

### Exception Handling Conventions
Throw specific domain exceptions extending `DashboardConfigException`. Map HTTP status codes and user-friendly error messages through `DashboardConfigExceptionHandler`.

### Logging & Sensitive Data Masking
- Use Slf4j (`log.info`, `log.debug`, `log.warn`, `log.error`).
- Never log raw passwords, authorization headers, or sensitive user tokens.

---

## 5. Security, Code Quality & Compliance Tooling

### SAST: SpotBugs & FindSecBugs
Static security analysis checks for SQL injection, cryptographic misuse, and resource leaks:
```bash
./mvnw spotbugs:check
```
Fine-tune exclusions in `spotbugs-exclude.xml`.

### SCA: OWASP Dependency-Check
Scans dependencies against the National Vulnerability Database (NVD):
```bash
./mvnw dependency-check:check
```

### SBOM: CycloneDX Aggregate Generation
Generates a CNCF-compliant Software Bill of Materials in JSON format:
```bash
./mvnw cyclonedx:makeAggregateBom
```

---

## 6. Git Workflow & Branching Strategy

### Branch Naming Conventions
- `feature/<issue-number>-short-description`
- `fix/<issue-number>-bug-title`
- `refactor/<short-description>`

### Conventional Commits
Use standard commit messages:
```
feat(accounts): add tenant component billing calculation
fix(scheduler): handle Kafka timeout during pod usage chunking
docs(readme): update quick start docker compose guide
refactor(config): remove hardcoded IP and externalize config import
```

---

## 7. Pull Request (PR) Checklist

Before submitting a Pull Request, ensure:
- [ ] `./mvnw clean test` passes with zero failures.
- [ ] `./mvnw spotbugs:check` reports no High or Medium severity security findings.
- [ ] No hardcoded passwords, tokens, internal hostnames, or `.env` files are committed.
- [ ] Appropriate JavaDoc and unit test coverage are provided.
- [ ] Commit history is clean and rebased against the target branch.
