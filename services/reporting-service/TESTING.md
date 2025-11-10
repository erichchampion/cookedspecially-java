# Testing Guide for Reporting Service

## Overview

This document provides comprehensive information about testing the Reporting Service, including test structure, running tests, and test coverage.

## Test Structure

The test suite is organized into three main categories:

### 1. Unit Tests (`src/test/java/.../service/`)

Unit tests focus on testing individual components in isolation with mocked dependencies.

**Coverage:**
- `SalesReportServiceTest`: Tests sales report generation logic
- `ReportGenerationServiceTest`: Tests report orchestration and file generation
- `CustomerReportServiceTest`: Tests customer report generation
- `ProductReportServiceTest`: Tests product report generation
- `OperationsReportServiceTest`: Tests operations report generation

**Technologies:**
- JUnit 5
- Mockito for mocking
- AssertJ for assertions

### 2. Integration Tests (`src/test/java/.../controller/`)

Integration tests verify the interaction between controllers and services through the web layer.

**Coverage:**
- `ReportControllerTest`: Tests main report generation endpoints
- `SalesReportControllerTest`: Tests sales report REST APIs
- `CustomerReportControllerTest`: Tests customer report endpoints
- `ProductReportControllerTest`: Tests product report endpoints
- `OperationsReportControllerTest`: Tests operations report endpoints

**Technologies:**
- Spring Boot Test
- MockMvc for testing REST endpoints
- Mock security context for authentication

### 3. Repository Tests

Repository tests verify database queries and data access logic.

**Coverage:**
- Tests for custom SQL queries in repository implementations
- Database schema validation
- Entity relationship tests

## Running Tests

### Run All Tests

```bash
# Using Maven
./mvnw test

# Using Maven wrapper with specific profile
./mvnw test -Ptest

# Run with coverage
./mvnw test jacoco:report
```

### Run Specific Test Class

```bash
# Run a single test class
./mvnw test -Dtest=SalesReportServiceTest

# Run multiple test classes
./mvnw test -Dtest=SalesReportServiceTest,ReportGenerationServiceTest
```

### Run Specific Test Method

```bash
# Run a specific test method
./mvnw test -Dtest=SalesReportServiceTest#generateDailyInvoiceReport_ShouldReturnInvoiceList
```

### Run Tests by Category

```bash
# Run only unit tests
./mvnw test -Dgroups=unit

# Run only integration tests
./mvnw test -Dgroups=integration
```

## Test Configuration

### Application Properties for Tests

The test configuration is located in `src/test/resources/application-test.properties`:

```properties
# H2 in-memory database for testing
spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL
spring.datasource.driver-class-name=org.h2.Driver

# Disable security for tests
spring.security.oauth2.resourceserver.jwt.issuer-uri=
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=

# Disable scheduled reports in tests
scheduled.reports.enabled=false

# Simple cache for tests (no Redis required)
spring.cache.type=simple
```

### Test Dependencies

Key test dependencies (already included in `pom.xml`):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

## Test Examples

### Unit Test Example

```java
@ExtendWith(MockitoExtension.class)
class SalesReportServiceTest {

    @Mock
    private SalesReportRepository salesReportRepository;

    @InjectMocks
    private SalesReportService salesReportService;

    @Test
    void generateDailyInvoiceReport_ShouldReturnInvoiceList() {
        // Given
        when(salesReportRepository.generateDailyInvoiceReport(...))
            .thenReturn(List.of(invoice));

        // When
        List<InvoiceReportDTO> result = salesReportService
            .generateDailyInvoiceReport(...);

        // Then
        assertThat(result).isNotEmpty();
        verify(salesReportRepository).generateDailyInvoiceReport(...);
    }
}
```

### Integration Test Example

```java
@WebMvcTest(SalesReportController.class)
@ActiveProfiles("test")
class SalesReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalesReportService salesReportService;

    @Test
    @WithMockUser(roles = "USER")
    void getDailyInvoiceReport_ShouldReturnInvoices() throws Exception {
        // Given
        when(salesReportService.generateDailyInvoiceReport(...))
            .thenReturn(List.of(invoice));

        // When/Then
        mockMvc.perform(get("/api/v1/reports/sales/daily-invoice")
                .param("fromDate", "2025-01-01")
                .param("toDate", "2025-01-31"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].invoiceNumber").value("INV-001"));
    }
}
```

## Test Coverage

### Current Coverage Goals

- **Line Coverage**: Target 80%+
- **Branch Coverage**: Target 75%+
- **Method Coverage**: Target 85%+

### Generate Coverage Report

```bash
# Generate coverage report
./mvnw test jacoco:report

# View report
open target/site/jacoco/index.html
```

### Coverage by Component

| Component | Target Coverage |
|-----------|----------------|
| Services | 85%+ |
| Controllers | 80%+ |
| Repositories | 70%+ |
| DTOs | N/A (no logic) |
| Configuration | 60%+ |

## Mocking Strategy

### AWS Services

AWS services (S3, SES) are mocked in tests using Mockito:

```java
@MockBean
private S3Client s3Client;

@MockBean
private SesClient sesClient;
```

### Database

- **Unit Tests**: Use Mockito to mock repositories
- **Integration Tests**: Use H2 in-memory database
- **Repository Tests**: Use H2 with test data

### Security

Security is disabled in tests using `TestConfig`:

```java
@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
```

## Best Practices

### 1. Test Naming Convention

```java
// Unit tests
methodName_Condition_ExpectedResult()

// Examples
generateDailyInvoiceReport_WithValidDates_ShouldReturnInvoices()
generateReport_WithException_ShouldReturnFailedStatus()
```

### 2. Test Structure (Given-When-Then)

```java
@Test
void testMethod() {
    // Given - Set up test data and mocks
    InvoiceReportDTO invoice = InvoiceReportDTO.builder()...build();
    when(repository.find(...)).thenReturn(invoice);

    // When - Execute the method under test
    var result = service.generateReport(...);

    // Then - Verify the results
    assertThat(result).isNotNull();
    verify(repository).find(...);
}
```

### 3. Use AssertJ for Assertions

```java
// Prefer AssertJ fluent assertions
assertThat(result).isNotNull();
assertThat(result).hasSize(5);
assertThat(result.get(0).getName()).isEqualTo("Test");

// Over JUnit assertions
assertNotNull(result);
assertEquals(5, result.size());
```

### 4. Test Edge Cases

Always test:
- Happy path (successful execution)
- Error conditions (exceptions, null values)
- Edge cases (empty lists, boundary values)
- Security (unauthorized access)

## Continuous Integration

### GitHub Actions Integration

Tests are automatically run on:
- Pull request creation
- Push to main/develop branches
- Manual workflow trigger

### Pre-commit Testing

Recommended to run tests before committing:

```bash
# Add to .git/hooks/pre-commit
#!/bin/sh
./mvnw test
```

## Troubleshooting

### Common Issues

**Issue 1: H2 Database Compatibility**
- Solution: Use `MODE=MySQL` in connection URL
- Example: `jdbc:h2:mem:testdb;MODE=MySQL`

**Issue 2: Security Tests Failing**
- Solution: Use `@WithMockUser` annotation
- Or disable security with `@AutoConfigureMockMvc(addFilters = false)`

**Issue 3: Slow Tests**
- Solution: Use `@MockBean` instead of full context
- Use `@WebMvcTest` instead of `@SpringBootTest`

**Issue 4: AWS SDK Issues**
- Solution: Mock AWS clients in TestConfig
- Ensure test profile uses mocks

## Test Data

### Test Data Setup

For integration tests requiring database state:

```java
@BeforeEach
void setUp() {
    // Insert test data
    entityManager.persist(testEntity);
    entityManager.flush();
}

@AfterEach
void tearDown() {
    // Clean up test data
    entityManager.clear();
}
```

### Test Data Files

Test data can be loaded from JSON files:

```java
// src/test/resources/test-data/invoices.json
@Test
void testWithJsonData() throws IOException {
    String json = Files.readString(
        Paths.get("src/test/resources/test-data/invoices.json")
    );
    List<InvoiceDTO> invoices = objectMapper.readValue(
        json, new TypeReference<List<InvoiceDTO>>() {}
    );
}
```

## Performance Testing

### Load Tests

For performance-critical endpoints:

```java
@Test
void generateReport_PerformanceTest() {
    long startTime = System.currentTimeMillis();

    reportService.generateReport(request);

    long duration = System.currentTimeMillis() - startTime;
    assertThat(duration).isLessThan(5000); // 5 seconds max
}
```

### Benchmarking

Use JMH for detailed benchmarks:

```bash
# Add JMH dependency and run benchmarks
./mvnw clean install
java -jar target/benchmarks.jar
```

## Documentation

- Keep tests well-documented with JavaDoc
- Update this guide when adding new test patterns
- Document any special test configuration

## Next Steps

1. Increase test coverage to 85%+
2. Add performance tests for report generation
3. Add contract tests for external service integration
4. Set up mutation testing with PIT

## Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [AssertJ Documentation](https://assertj.github.io/doc/)
