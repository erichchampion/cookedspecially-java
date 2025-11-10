# ⚠️ DEPRECATED: Historical Document

**UPDATE (2025-11-10):** This document is now deprecated. The legacy `back-end` directory referenced in this guide has been removed. All microservices are now fully implemented in the `services/` directory with complete source code.

**For current development:** Refer to the microservices in `services/` directory and LEGACY_MIGRATION_GAPS.md for migration status.

---

# ⚠️ IMPORTANT: Source Code Location Issue (Historical)

## Problem

The Maven builds are failing with:
```
[INFO] No sources to compile
[ERROR] Unable to find main class
```

This means:
- ✅ The pom.xml files are in the correct locations
- ❌ The Java source code (`src/main/java`) is NOT in those directories

## Where Is Your Source Code?

Based on your original error messages, you have microservice source code somewhere on your local machine that was showing Lombok compilation errors. The source code is **NOT** in this repository yet.

## Solution: Merge POM Files with Your Source Code

You need to copy the fixed `pom.xml` files to your existing service directories that contain the source code.

### Step 1: Find Your Source Code

Your source code is likely in one of these locations:

```bash
# Option 1: In your local repository (different branch)
/Users/erich/git/github/erichchampion/cookedspecially-java/services/

# Option 2: In a separate project directory
/Users/erich/projects/cookedspecially-microservices/
/Users/erich/workspace/order-service/
# etc.

# Option 3: In the original monolith that was split
/Users/erich/git/github/erichchampion/cookedspecially-java/back-end/
```

**Find your source code:**

```bash
# Search for Java files in order-service
find /Users/erich -name "*OrderService.java" 2>/dev/null | grep -v target

# Search for Java files in payment-service
find /Users/erich -name "*PaymentService.java" 2>/dev/null | grep -v target

# Search for Java files in restaurant-service
find /Users/erich -name "*RestaurantService.java" 2>/dev/null | grep -v target

# Search for Java files in notification-service
find /Users/erich -name "*NotificationService.java" 2>/dev/null | grep -v target
```

### Step 2: Once You Find the Source Code

#### Option A: Copy POM Files to Your Existing Services

If your source code has this structure:
```
/path/to/your/services/
├── order-service/
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/cookedspecially/orderservice/
│   │               ├── OrderServiceApplication.java
│   │               ├── controller/
│   │               ├── service/
│   │               ├── domain/
│   │               └── dto/
│   └── pom.xml (OLD - with errors)
├── payment-service/
│   ├── src/...
│   └── pom.xml (OLD - with errors)
# etc.
```

**Copy the fixed POM files:**

```bash
# From your repository directory
cd /Users/erich/git/github/erichchampion/cookedspecially-java

# Copy to your actual service locations
# Replace /path/to/your/actual/services with your real path

cp services/order-service/pom.xml /path/to/your/actual/services/order-service/
cp services/payment-service/pom.xml /path/to/your/actual/services/payment-service/
cp services/restaurant-service/pom.xml /path/to/your/actual/services/restaurant-service/
cp services/notification-service/pom.xml /path/to/your/actual/services/notification-service/
```

Then build:

```bash
cd /path/to/your/actual/services/order-service
mvn clean compile
```

#### Option B: Copy Your Source Code to This Repository

If you want to consolidate everything in this repository:

```bash
# From your repository directory
cd /Users/erich/git/github/erichchampion/cookedspecially-java

# Copy source code from your actual services to here
# Replace /path/to/your/actual/services with your real path

cp -r /path/to/your/actual/services/order-service/src services/order-service/
cp -r /path/to/your/actual/services/payment-service/src services/payment-service/
cp -r /path/to/your/actual/services/restaurant-service/src services/restaurant-service/
cp -r /path/to/your/actual/services/notification-service/src services/notification-service/

# Now build in this repository
cd services/order-service
mvn clean compile
```

### Step 3: Verify Source Code Structure

After copying, your service should look like this:

```
services/order-service/
├── pom.xml (the new fixed one)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── cookedspecially/
│   │   │           └── orderservice/
│   │   │               ├── OrderServiceApplication.java
│   │   │               ├── controller/
│   │   │               ├── service/
│   │   │               ├── domain/
│   │   │               ├── dto/
│   │   │               ├── repository/
│   │   │               └── event/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/
└── target/ (created by Maven)
```

Verify:

```bash
# Check if source code exists
ls -la services/order-service/src/main/java/

# Should see directories like:
# com/cookedspecially/orderservice/
```

### Step 4: Build After Merging

```bash
cd services/order-service
mvn clean compile

# You should now see:
# [INFO] Compiling 28 source files to target/classes
# [INFO] BUILD SUCCESS
```

## Quick Diagnostic

Run this to see what you have:

```bash
cd /Users/erich/git/github/erichchampion/cookedspecially-java/services

echo "=== Order Service ==="
ls -la order-service/ || echo "Directory doesn't exist"
ls -la order-service/src/main/java/ 2>/dev/null || echo "No source code"

echo "=== Payment Service ==="
ls -la payment-service/ || echo "Directory doesn't exist"
ls -la payment-service/src/main/java/ 2>/dev/null || echo "No source code"

echo "=== Restaurant Service ==="
ls -la restaurant-service/ || echo "Directory doesn't exist"
ls -la restaurant-service/src/main/java/ 2>/dev/null || echo "No source code"

echo "=== Notification Service ==="
ls -la notification-service/ || echo "Directory doesn't exist"
ls -la notification-service/src/main/java/ 2>/dev/null || echo "No source code"
```

## Need Help Finding Your Code?

If you can't find your source code, check:

### 1. Git Branches

```bash
cd /Users/erich/git/github/erichchampion/cookedspecially-java
git branch -a

# You might have source code on another branch like:
# claude/aws-modernization-todos-011CUt3aXapA3bjKQbYyLMoy
```

### 2. Git Stash

```bash
git stash list
# If you see stashed changes, you might have:
git stash show -p
```

### 3. Check Recent Commits

```bash
# See if source code was committed before
git log --oneline --all | head -20

# Check a specific commit
git show <commit-hash>:services/order-service/
```

### 4. IDE Projects

Check if your IDE has the projects open:
- IntelliJ IDEA: Recent Projects
- VS Code: File → Open Recent
- Eclipse: Workspace location

## Common Scenarios

### Scenario 1: Source Code on Different Branch

```bash
# List all branches
git branch -a

# Check if source exists on another branch
git show claude/aws-modernization-todos-011CUt3aXapA3bjKQbYyLMoy:services/order-service/

# If yes, merge or cherry-pick the source code
```

### Scenario 2: Source Code in Monolith

If your source is still in the `back-end/` directory:

```bash
# Check for microservice code in the monolith
ls -la back-end/src/main/java/com/cookedspecially/

# If you're extracting from monolith, you need to:
# 1. Identify which classes belong to each service
# 2. Copy them to the appropriate service directory
# 3. Update package names if needed
```

### Scenario 3: Source Code in Separate Repository

```bash
# If you have a separate repo with the microservices
cd /path/to/other/repo

# Copy to this repository
cp -r services/* /Users/erich/git/github/erichchampion/cookedspecially-java/services/
```

## What to Do Next

1. **Find your source code** using the commands above
2. **Merge with the POM files** (Option A or B)
3. **Build**: `mvn clean compile`
4. **Commit** the complete service (source + pom.xml)
5. **Push** to the repository

## Still Stuck?

If you still can't find your source code, please provide:

```bash
# Run this and share the output:
cd /Users/erich/git/github/erichchampion
find . -name "OrderService.java" -o -name "PaymentService.java" 2>/dev/null | head -20

# Also show:
cd /Users/erich/git/github/erichchampion/cookedspecially-java
git log --oneline --all --graph | head -30
git status
ls -la services/
```

This will help me locate where your source code actually is.

---

**TL;DR**: The pom.xml files are correct and in the right place, but there's no Java source code (`src/main/java`) in those directories. You need to find where your actual microservice source code is and either copy these pom.xml files there, or copy your source code here.
