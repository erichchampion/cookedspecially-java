# Security Policy

## Supported Versions

The following versions of CookedSpecially are currently supported with security updates:

| Version | Supported          | Notes |
| ------- | ------------------ | ----- |
| 1.0.x   | :white_check_mark: | Current release - All microservices |
| < 1.0   | :x:                | Legacy monolith - Deprecated |

## Security Standards

CookedSpecially follows industry-standard security practices:

- ✅ **No Hardcoded Secrets**: All sensitive configuration uses environment variables and AWS Secrets Manager
- ✅ **Latest Dependencies**: Spring Boot 3.3.11, Java 21, AWS SDK 2.28.0
- ✅ **Automated Scanning**: Trivy vulnerability scanning in CI/CD
- ✅ **Dependency Management**: Automated updates via Dependabot
- ✅ **Secure Defaults**: Security-first configuration patterns

## Reporting a Vulnerability

We take security vulnerabilities seriously. If you discover a security issue, please follow these guidelines:

### **DO NOT** Create Public Issues

Please **do not** file a public issue on GitHub for security vulnerabilities. This could put the community at risk.

### Reporting Process

1. **Email**: Send details to **security@cookedspecially.com** (or create a private security advisory on GitHub)
2. **Include**:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if available)
3. **Response Time**: We aim to acknowledge reports within 48 hours
4. **Resolution**: We will work with you to understand and address the issue

### What to Expect

- **Acknowledgment**: Within 48 hours of report
- **Initial Assessment**: Within 5 business days
- **Status Updates**: Regular updates on progress
- **Credit**: Security researchers will be credited (if desired) once the issue is resolved
- **Disclosure**: Coordinated disclosure after patch is available

## Security Measures

### Authentication & Authorization

- **JWT Tokens**: Secure token-based authentication
- **AWS Cognito**: Enterprise-grade identity management
- **RBAC**: Role-based access control in Admin Service
- **API Security**: OAuth2 resource server protection

### Data Protection

- **Encryption in Transit**: TLS 1.2+ for all API communication
- **Encryption at Rest**: AWS RDS encryption for databases
- **Secrets Management**: AWS Secrets Manager for sensitive data
- **No Plaintext Passwords**: BCrypt password hashing

### Infrastructure Security

- **VPC Isolation**: Private subnets for application and database tiers
- **Security Groups**: Restrictive firewall rules
- **NAT Gateways**: Controlled egress traffic
- **CloudWatch Monitoring**: Real-time security event monitoring

### Dependency Management

We actively monitor and update dependencies:

- **Automated Scanning**: Weekly Trivy scans for vulnerabilities
- **Dependabot**: Automatic pull requests for security updates
- **Manual Review**: Critical security updates applied immediately

## Security Audits

### Latest Audit Results

**Date**: 2025-11-10

**Status**: ✅ PASS

**Summary**:
- No hardcoded secrets detected
- No CRITICAL vulnerabilities in current codebase
- All services using latest Spring Boot 3.3.11
- 72% reduction in vulnerabilities since legacy migration
- 100% elimination of hardcoded secrets

See [SECURITY_ANALYSIS_POST_MIGRATION.md](SECURITY_ANALYSIS_POST_MIGRATION.md) for detailed analysis.

### Known Issues

We maintain transparency about known security issues:

**Current (Non-Critical)**:
- Some transitive dependencies have MEDIUM severity issues (managed by Spring Boot)
- See [SECURITY_FINDINGS.md](SECURITY_FINDINGS.md) for complete list

**Planned Remediation**:
- Spring Boot dependency updates (ongoing via Dependabot)
- Regular security patch releases

## Security Best Practices for Contributors

If you're contributing to CookedSpecially, please follow these security guidelines:

### Code Security

1. **Never commit secrets** - Use environment variables
2. **Validate all input** - Use Spring Validation annotations
3. **Sanitize output** - Prevent XSS attacks
4. **Use parameterized queries** - Prevent SQL injection
5. **Handle exceptions properly** - Don't leak sensitive information

### Authentication

```java
// ✅ Good: Use Spring Security
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminEndpoint() { }

// ❌ Bad: Manual role checking
if (user.getRole().equals("ADMIN")) { }
```

### Secrets Management

```yaml
# ✅ Good: Environment variables
stripe:
  api-key: ${STRIPE_API_KEY}

# ❌ Bad: Hardcoded
stripe:
  api-key: sk_live_abc123
```

### SQL Injection Prevention

```java
// ✅ Good: JPA/Hibernate parameterized
@Query("SELECT u FROM User u WHERE u.email = :email")
User findByEmail(@Param("email") String email);

// ❌ Bad: String concatenation
String query = "SELECT * FROM users WHERE email = '" + email + "'";
```

## Security Testing

### Before Submitting PRs

1. **Run local security scan**:
   ```bash
   # Install Trivy
   brew install trivy  # or equivalent

   # Scan for vulnerabilities
   trivy fs . --severity HIGH,CRITICAL

   # Scan for secrets
   trivy fs . --scanners secret
   ```

2. **Check dependencies**:
   ```bash
   mvn dependency:tree
   mvn dependency-check:check
   ```

3. **Review changes**:
   - No secrets in diff
   - No sensitive data in logs
   - Input validation present
   - Authentication/authorization enforced

### CI/CD Security Checks

All pull requests automatically run:
- Trivy vulnerability scanning
- Secret detection
- Dependency checks
- Code quality analysis

PRs with HIGH or CRITICAL security issues will not be merged.

## Incident Response

### If a Vulnerability is Exploited

1. **Immediate**: Notify security team
2. **Assessment**: Evaluate impact and scope
3. **Containment**: Deploy emergency patch
4. **Communication**: Notify affected users
5. **Resolution**: Deploy permanent fix
6. **Post-Mortem**: Document and improve processes

### Emergency Contacts

- **Security Team**: security@cookedspecially.com
- **On-Call**: Available 24/7 for CRITICAL issues
- **GitHub**: [@erichchampion](https://github.com/erichchampion)

## Security Resources

### Internal Documentation

- [Security Analysis](SECURITY_ANALYSIS_POST_MIGRATION.md) - Latest security audit
- [Security Findings](SECURITY_FINDINGS.md) - Known vulnerabilities and status
- [Migration Gaps](LEGACY_MIGRATION_GAPS.md) - Architecture improvements

### External Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [AWS Security Best Practices](https://aws.amazon.com/security/best-practices/)
- [CVE Database](https://cve.mitre.org/)

## Compliance

### Standards Adherence

- **OWASP Top 10**: Mitigations implemented for all top 10 risks
- **CIS Benchmarks**: Following CIS Docker and Kubernetes benchmarks
- **PCI DSS**: Payment processing follows PCI DSS guidelines (via Stripe)
- **GDPR**: Data protection and privacy controls implemented

### Data Privacy

- **User Data**: Encrypted at rest and in transit
- **PII Protection**: Minimal PII collection, secure storage
- **Data Retention**: Configurable retention policies
- **Right to Deletion**: API support for data deletion requests

## Security Changelog

### 2025-11-10
- ✅ Removed legacy back-end (111 vulnerabilities eliminated)
- ✅ Eliminated all 11 hardcoded secrets
- ✅ Updated all services to Spring Boot 3.3.11
- ✅ Standardized AWS SDK to 2.28.0
- ✅ Implemented automated security scanning
- ✅ Added Dependabot for dependency updates
- 📊 **72% vulnerability reduction achieved**

### 2025-11-09
- 🔍 Comprehensive security audit completed
- 📋 SECURITY_FINDINGS.md created
- 📋 SECURITY_ANALYSIS_POST_MIGRATION.md created

## Questions?

If you have questions about security practices or this policy:

1. Review existing documentation (links above)
2. Check GitHub Discussions
3. Email security@cookedspecially.com

---

**Last Updated**: 2025-11-10
**Version**: 1.0
**Next Review**: 2025-12-10
