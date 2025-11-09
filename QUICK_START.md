# Quick Start Guide - Fixing Build Errors

## TL;DR - Fix in 5 Minutes

Your microservices are failing to build because Maven isn't processing Lombok annotations. Here's how to fix it:

### Option 1: Use the Pre-Configured POM Files (Recommended)

If you already have source code in your local service directories:

```bash
# Copy the fixed pom.xml files from this repo to your local services
cp services/order-service/pom.xml /Users/erich/git/github/erichchampion/cookedspecially-java/services/order-service/
cp services/payment-service/pom.xml /Users/erich/git/github/erichchampion/cookedspecially-java/services/payment-service/
cp services/restaurant-service/pom.xml /Users/erich/git/github/erichchampion/cookedspecially-java/services/restaurant-service/
cp services/notification-service/pom.xml /Users/erich/git/github/erichchampion/cookedspecially-java/services/notification-service/

# Then build
cd /Users/erich/git/github/erichchampion/cookedspecially-java/services
mvn clean compile
```

### Option 2: Manual Fix

Add this to each service's `pom.xml`:

1. **Add Lombok dependency** (in `<dependencies>` section):
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

2. **Configure maven-compiler-plugin** (in `<build><plugins>` section):
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
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

3. **Build**:
```bash
mvn clean compile
```

## What Files Are Available

| File | Purpose |
|------|---------|
| `services/order-service/pom.xml` | Fixed POM for order-service |
| `services/payment-service/pom.xml` | Fixed POM for payment-service (includes Stripe SDK) |
| `services/restaurant-service/pom.xml` | Fixed POM for restaurant-service |
| `services/notification-service/pom.xml` | Fixed POM for notification-service (includes AWS SDK) |
| `services/pom.xml` | Parent POM to build all services together |
| `services/README.md` | Detailed documentation |
| `services/build-all-services.sh` | Automated build script |
| `BUILD_ERROR_ANALYSIS.md` | Complete error analysis |
| `LOMBOK_BUILD_FIX.md` | Detailed fix instructions |
| `services-pom-example.xml` | Reference template |

## Testing the Fix

### Single Service
```bash
cd services/order-service
mvn clean compile
# Should see: BUILD SUCCESS
```

### All Services
```bash
cd services
./build-all-services.sh
# Should see: 🎉 All services built successfully!
```

## Common Issues

### Still Getting Errors?

**Issue**: `cannot find symbol: variable log`
**Solution**: Lombok not in annotationProcessorPaths
- Check maven-compiler-plugin configuration (see Option 2 above)

**Issue**: `cannot find symbol: method builder()`
**Solution**: Same as above + verify `@Builder` annotation on classes

**Issue**: Build hangs or is very slow
**Solution**: Add `-q` flag: `mvn clean compile -q`

**Issue**: AWS SDK errors in notification-service
**Solution**: The pom.xml includes AWS SDK v2. If using v1, update imports:
```java
// Old (v1)
import com.amazonaws.services.simpleemail.model.SendQuota;

// New (v2)
import software.amazon.awssdk.services.ses.model.SendQuota;
```

## Verification Checklist

After applying the fix:

- [ ] `mvn --version` shows 3.6.0 or higher
- [ ] `java --version` shows 17
- [ ] Each service has Lombok dependency with `scope=provided`
- [ ] Each service has annotationProcessorPaths in maven-compiler-plugin
- [ ] `mvn clean compile` succeeds for each service
- [ ] No "cannot find symbol" errors
- [ ] JAR files created in `target/` directories

## What Gets Fixed

This fix resolves **400+ compilation errors** including:

✅ Missing `log` variable (from `@Slf4j`)
✅ Missing `builder()` methods (from `@Builder`)
✅ Missing getters/setters (from `@Data`, `@Getter`, `@Setter`)
✅ Missing constructors (from `@AllArgsConstructor`, `@NoArgsConstructor`)

## IDE Configuration

### IntelliJ IDEA
1. Install Lombok plugin: Settings → Plugins → Search "Lombok" → Install
2. Enable annotation processing: Settings → Build → Compiler → Annotation Processors → ✓ Enable
3. Rebuild project: Build → Rebuild Project

### Eclipse
1. Download lombok.jar: https://projectlombok.org/download
2. Run: `java -jar lombok.jar`
3. Select Eclipse installation directory
4. Click Install/Update
5. Restart Eclipse

### VS Code
1. Install extension: "Lombok Annotations Support for VS Code"
2. Install extension: "Language Support for Java"
3. Reload window

## Full Build Sequence

```bash
# 1. Pull latest code
git pull origin claude/fix-build-errors-011CUwhhYhfmvLkHyECJpd8j

# 2. Copy POM files to your local services (if source code is local)
# See Option 1 above

# 3. Build all services
cd services
mvn clean install -DskipTests

# 4. Run a service
cd order-service
mvn spring-boot:run
```

## Need More Help?

See detailed documentation:
- **BUILD_ERROR_ANALYSIS.md** - What went wrong and why
- **LOMBOK_BUILD_FIX.md** - Step-by-step fix guide
- **services/README.md** - Complete services documentation

## Summary

**Problem**: Maven not processing Lombok → 400+ compilation errors
**Solution**: Add Lombok to annotationProcessorPaths
**Time**: 5 minutes to fix all services
**Files**: Pre-configured POM files ready to use in `services/` directory

---

**Need to copy files from repo to your local machine?**

```bash
# Clone or pull the repo
git clone https://github.com/erichchampion/cookedspecially-java.git temp-repo
cd temp-repo
git checkout claude/fix-build-errors-011CUwhhYhfmvLkHyECJpd8j

# Copy POM files
cp services/*/pom.xml /Users/erich/git/github/erichchampion/cookedspecially-java/services/

# Clean up
cd ..
rm -rf temp-repo
```

Good luck! 🚀
