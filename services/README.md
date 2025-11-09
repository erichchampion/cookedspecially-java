# CookedSpecially Microservices

This directory contains the Maven POM files for all CookedSpecially microservices with **proper Lombok configuration** to fix the 400+ build errors.

## Services

1. **order-service** - Order management and fulfillment
2. **payment-service** - Payment processing (Stripe, PayPal, etc.)
3. **restaurant-service** - Restaurant and menu management
4. **notification-service** - Notifications (Email via AWS SES, SMS, Push)

## What Was Fixed

All services now have proper Lombok annotation processing configured in Maven:

### Key Changes in Each pom.xml

✅ **Lombok dependency** added with `scope=provided`
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

✅ **Maven compiler plugin** configured with annotation processor
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.30</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

✅ **Spring Boot parent POM** v3.1.5 for automatic dependency management

✅ **Additional dependencies** added per service:
- **notification-service**: AWS SDK v2 for SES and SNS
- **payment-service**: Stripe Java SDK
- All services: Kafka, JaCoCo, Swagger/OpenAPI

## Building Services

### Copy These POM Files to Your Local Project

If you have existing source code in these service directories on your local machine:

```bash
# From the repository root
cp services/order-service/pom.xml /Users/erich/git/github/erichchampion/cookedspecially-java/services/order-service/
cp services/payment-service/pom.xml /Users/erich/git/github/erichchampion/cookedspecially-java/services/payment-service/
cp services/restaurant-service/pom.xml /Users/erich/git/github/erichchampion/cookedspecially-java/services/restaurant-service/
cp services/notification-service/pom.xml /Users/erich/git/github/erichchampion/cookedspecially-java/services/notification-service/
```

### Build Individual Services

```bash
# Order Service
cd services/order-service
mvn clean compile
mvn clean package -DskipTests

# Payment Service
cd ../payment-service
mvn clean compile
mvn clean package -DskipTests

# Restaurant Service
cd ../restaurant-service
mvn clean compile
mvn clean package -DskipTests

# Notification Service
cd ../notification-service
mvn clean compile
mvn clean package -DskipTests
```

### Build All Services at Once

From the `services` directory:

```bash
cd services
mvn clean compile
mvn clean package -DskipTests
```

## Expected Results

### Before (with errors)
```
[ERROR] cannot find symbol: variable log
[ERROR] cannot find symbol: method builder()
[ERROR] cannot find symbol: method getId()
[ERROR] 100 errors
[INFO] BUILD FAILURE
```

### After (successful)
```
[INFO] Compiling 28 source files to target/classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.245 s
```

## Running Services

Each service can be run individually:

```bash
# Order Service on port 8081
cd services/order-service
mvn spring-boot:run

# Payment Service on port 8082
cd services/payment-service
mvn spring-boot:run

# Restaurant Service on port 8083
cd services/restaurant-service
mvn spring-boot:run

# Notification Service on port 8084
cd services/notification-service
mvn spring-boot:run
```

## Configuration Files Needed

Each service directory should have an `application.yml` or `application.properties` file in `src/main/resources/`:

### Example application.yml
```yaml
server:
  port: 8081

spring:
  application:
    name: order-service
  datasource:
    url: jdbc:mysql://localhost:3306/cookedspecially_orders
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: order-service-group
```

## Service-Specific Notes

### Notification Service

The notification service includes AWS SDK v2 dependencies. To use AWS SES:

1. Add AWS credentials to `application.yml`:
```yaml
aws:
  region: us-east-1
  ses:
    from-email: noreply@cookedspecially.com
```

2. Ensure AWS credentials are configured:
```bash
aws configure
# OR set environment variables
export AWS_ACCESS_KEY_ID=your-key
export AWS_SECRET_ACCESS_KEY=your-secret
```

### Payment Service

The payment service includes Stripe SDK. Configure in `application.yml`:

```yaml
stripe:
  api-key: ${STRIPE_SECRET_KEY}
  webhook-secret: ${STRIPE_WEBHOOK_SECRET}
```

## Troubleshooting

### Still Getting Lombok Errors?

1. **Clear Maven cache**:
   ```bash
   rm -rf ~/.m2/repository/org/projectlombok
   mvn clean
   ```

2. **Reimport project in IDE**:
   - IntelliJ: File → Invalidate Caches / Restart
   - Eclipse: Project → Clean
   - VS Code: Reload window

3. **Enable annotation processing in IDE**:
   - IntelliJ: Settings → Build → Compiler → Annotation Processors → Enable
   - Eclipse: Install Lombok via `java -jar lombok.jar`

4. **Verify Java version**:
   ```bash
   java --version  # Should be 17
   mvn --version   # Should be 3.6.0+
   ```

### Import Errors

If you see import errors for AWS or Stripe classes:

```bash
mvn dependency:resolve
mvn dependency:tree
```

## Testing

Run tests for a service:

```bash
cd services/order-service
mvn test
```

Run tests for all services:

```bash
cd services
mvn test
```

## Code Coverage

JaCoCo is configured for code coverage. After running tests:

```bash
mvn test
# Coverage report at: target/site/jacoco/index.html
```

## API Documentation

Swagger UI is available for each service at:
- Order Service: http://localhost:8081/swagger-ui.html
- Payment Service: http://localhost:8082/swagger-ui.html
- Restaurant Service: http://localhost:8083/swagger-ui.html
- Notification Service: http://localhost:8084/swagger-ui.html

## Docker

To build Docker images (Dockerfiles needed):

```bash
cd services/order-service
mvn clean package
docker build -t cookedspecially/order-service:1.0.0 .
```

## CI/CD Integration

These POM files are ready for CI/CD pipelines:

### GitHub Actions Example
```yaml
- name: Build Order Service
  run: |
    cd services/order-service
    mvn clean package -DskipTests
```

### Jenkins Example
```groovy
stage('Build Services') {
    steps {
        sh 'cd services && mvn clean package -DskipTests'
    }
}
```

## Additional Resources

- **BUILD_ERROR_ANALYSIS.md** - Detailed error analysis
- **LOMBOK_BUILD_FIX.md** - Complete fix documentation
- **services-pom-example.xml** - Reference POM template

## Support

If you encounter issues:

1. Check the documentation files in the project root
2. Ensure all dependencies are in `~/.m2/repository`
3. Verify Java 17 and Maven 3.6+ are installed
4. Check for port conflicts if services won't start

## Next Steps

1. ✅ Copy pom.xml files to your local service directories
2. ✅ Run `mvn clean compile` for each service
3. ✅ Fix any remaining service-specific issues (see LOMBOK_BUILD_FIX.md)
4. ✅ Add application.yml configuration files
5. ✅ Run services and test endpoints
6. ✅ Commit working code to repository
7. ✅ Set up CI/CD pipeline
8. ✅ Deploy to dev environment

---

**Version**: 1.0.0-SNAPSHOT
**Last Updated**: 2025-11-09
**Lombok Version**: 1.18.30
**Spring Boot Version**: 3.1.5
**Java Version**: 17
