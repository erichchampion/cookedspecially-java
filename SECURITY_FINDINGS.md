# Security Findings Report

**Generated:** 2025-11-09
**Scanner:** Trivy v0.67.2
**Scan Type:** Filesystem scan (HIGH & CRITICAL severity only)

## Executive Summary

Trivy detected **289 total vulnerabilities** and **11 hardcoded secrets** across the codebase:
- **Microservices** (customer, order, payment, restaurant, notification): ~29-31 vulnerabilities each (27 HIGH, 2 CRITICAL per service)
- **Legacy back-end**: 111 vulnerabilities (71 HIGH, 40 CRITICAL)
- **Front-end**: 2 vulnerabilities
- **Hardcoded secrets**: 11 instances (Stripe API keys, SSL private key) - **ALL IN LEGACY CODE**

## Critical Issues Requiring Immediate Attention

### 1. Microservices - Critical Vulnerabilities (Affects all 5 services)

#### CVE-2025-24813 - Tomcat RCE (CRITICAL)
- **Component:** `org.apache.tomcat.embed:tomcat-embed-core`
- **Current Version:** 10.1.15
- **Fixed Version:** 11.0.3, 10.1.35, 9.0.99
- **Impact:** Potential Remote Code Execution, information disclosure, and/or information corruption with partial PUT requests
- **Recommendation:** Update Spring Boot to latest patch version (3.3.11 or 3.4.5) which will bring in updated Tomcat

#### CVE-2024-38821 - Spring Security Authorization Bypass (CRITICAL)
- **Component:** `org.springframework.security:spring-security-web`
- **Current Version:** 6.1.5
- **Fixed Version:** 6.3.4, 6.2.7, 6.1.11, 6.0.13, 5.8.15, 5.7.13
- **Impact:** Authorization bypass of static resources in WebFlux applications
- **Recommendation:** Update Spring Security dependencies

### 2. High Severity Vulnerabilities (All Microservices)

#### Tomcat Vulnerabilities (7 total)
- **CVE-2024-50379 & CVE-2024-56337:** RCE due to TOCTOU issue in JSP compilation
- **CVE-2025-48988:** DoS in multipart upload
- **CVE-2025-48989:** HTTP/2 "MadeYouReset" DoS attack
- **CVE-2025-55752:** Directory traversal via rewrite with possible RCE
- **CVE-2023-46589:** HTTP request smuggling via malformed trailer headers
- **CVE-2024-34750:** Improper handling of exceptional conditions

#### Spring Framework Vulnerabilities
- **CVE-2024-22234:** Broken access control with `isFullyAuthenticated`
- **CVE-2024-22257:** Broken access control with `AuthenticatedVoter`
- **CVE-2025-22228:** BCryptPasswordEncoder doesn't enforce max password length
- **CVE-2025-22235:** EndpointRequest.to() creates wrong matcher
- **CVE-2025-41249:** Annotation detection vulnerability
- **CVE-2024-22243/22259/22262:** URL parsing with host validation issues
- **CVE-2024-38816/38819:** Path traversal in RouterFunctions

#### Other Critical Dependencies
- **CVE-2023-6378:** Logback serialization vulnerability (logback 1.4.11 → 1.4.12)
- **CVE-2025-55163:** Netty HTTP/2 DDoS vulnerability (4.1.100.Final → 4.1.124.Final)
- **CVE-2025-24970:** Netty SslHandler validation issue
- **CVE-2022-1471:** SnakeYAML constructor deserialization RCE (1.33 → 2.0)
- **CVE-2023-52428:** Nimbus-JOSE-JWT DoS (9.24.4 → 9.37.2)
- **CVE-2023-22102:** MySQL connector vulnerability (8.0.33 → 8.2.0)
- **CVE-2023-34455/43642:** Snappy-java DoS vulnerabilities

### 3. Hardcoded Secrets (Legacy Code Only - NOT in microservices)

⚠️ **All secrets are in legacy `back-end` and `front-end` directories - NOT in the current microservices**

#### Stripe API Keys (6 instances - CRITICAL)
- **Location 1:** `back-end/src/main/java/com/cookedspecially/controller/RestaurantController.java:560`
- **Location 2:** `back-end/src/main/java/com/cookedspecially/controller/RestaurantController.java:614`
- **Location 3-4:** `back-end/src/main/resources/stripeTest.properties`
- **Location 5-6:** `back-end/src/main/resources/stripe_32.properties`
- **Location 7-8:** `back-end/src/main/resources/stripe_37.properties`
- **Impact:** Exposed Stripe secret keys could allow unauthorized payment operations
- **Recommendation:**
  - Rotate all Stripe API keys immediately
  - Remove hardcoded keys and use environment variables
  - Consider removing legacy back-end if not in use

#### SSL Private Key (1 instance - HIGH)
- **Location:** `front-end/public/static/SSLTomcat/cs.key`
- **Impact:** Exposed RSA private key could compromise SSL/TLS encryption
- **Recommendation:**
  - Regenerate SSL certificate with new private key
  - Never commit private keys to version control
  - Use secure key management (AWS Secrets Manager, etc.)

### 4. Legacy Backend Issues (111 vulnerabilities)

The `back-end` directory contains extremely outdated dependencies:
- **Jackson-databind 2.7.5** (should be 2.17.x): 50+ critical deserialization vulnerabilities
- **Logback 1.1.7** (should be 1.4.12+): Critical serialization vulnerability
- **Spring 4.x dependencies**: Multiple critical vulnerabilities

**Recommendation:**
- If this backend is no longer in use, **remove the entire `back-end` directory**
- If still needed, perform major dependency updates (breaking changes likely)
- Migrate to current microservices architecture if possible

## Recommended Actions

### Immediate (Within 24 hours)
1. ✅ **Rotate all Stripe API keys** - Keys are exposed in repository
2. ✅ **Regenerate SSL certificates** - Private key is exposed
3. ❌ **Update Spring Boot** to 3.3.11 or 3.4.5 (addresses most Tomcat & Spring issues)

### Short-term (Within 1 week)
4. ❌ **Update all microservice dependencies:**
   - Spring Boot: 3.1.5 → 3.3.11 or 3.4.5
   - Logback: 1.4.11 → 1.4.12
   - Netty: 4.1.100.Final → 4.1.124.Final
   - SnakeYAML: 1.33 → 2.0
   - MySQL Connector: 8.0.33 → 8.2.0
   - Nimbus-JOSE-JWT: 9.24.4 → 9.37.2

5. ❌ **Remove hardcoded secrets** from all files

6. ❌ **Add .trivyignore** for accepted risks

### Long-term (Within 1 month)
7. ❌ **Remove legacy `back-end` directory** if not in use
8. ❌ **Implement secret scanning** in CI/CD (already have Trivy, just need to enforce)
9. ❌ **Add dependency update automation** (Dependabot, Renovate)
10. ❌ **Regular security scans** in CI/CD pipeline

## Dependency Update Commands

### For All Microservices

Update Spring Boot parent version in each `pom.xml`:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <!-- Current: 3.1.5 -->
    <version>3.3.11</version>
    <relativePath/>
</parent>
```

Update specific dependencies:

```xml
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2023.0.0</spring-cloud.version>
    <netty.version>4.1.124.Final</netty.version>
    <snakeyaml.version>2.0</snakeyaml.version>
    <mysql-connector.version>8.2.0</mysql-connector.version>
    <nimbus-jose-jwt.version>9.37.2</nimbus-jose-jwt.version>
</properties>
```

## Risk Assessment

| Risk Level | Count | Impact |
|------------|-------|--------|
| CRITICAL | 42 | Remote Code Execution, Authorization Bypass, Exposed Secrets |
| HIGH | 243 | DoS, Path Traversal, Information Disclosure |
| **TOTAL** | **289** | **Multiple attack vectors available** |

## Notes

- ✅ **Good news:** No secrets found in current microservices (`services/` directory)
- ⚠️ **Concern:** All services share similar vulnerabilities (same Spring Boot version)
- ⚠️ **Legacy code:** `back-end` directory is a significant security liability

## Next Steps

After reviewing this report:
1. Prioritize immediate actions (Stripe key rotation, SSL cert regeneration)
2. Plan Spring Boot upgrade (test thoroughly, breaking changes possible)
3. Decide on legacy `back-end` removal or migration
4. Implement automated security scanning in development workflow
