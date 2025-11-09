#!/bin/bash

# Build All CookedSpecially Microservices
# This script builds all services in the correct order

set -e  # Exit on error

echo "========================================"
echo "Building CookedSpecially Microservices"
echo "========================================"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Services to build
SERVICES=(
    "order-service"
    "payment-service"
    "restaurant-service"
    "notification-service"
)

# Track build status
SUCCESSFUL=0
FAILED=0

# Build each service
for SERVICE in "${SERVICES[@]}"; do
    echo -e "${YELLOW}======================================${NC}"
    echo -e "${YELLOW}Building $SERVICE...${NC}"
    echo -e "${YELLOW}======================================${NC}"

    cd "$SERVICE" || exit 1

    # Clean and compile
    if mvn clean compile -q; then
        echo -e "${GREEN}✅ $SERVICE compiled successfully${NC}"
        ((SUCCESSFUL++))
    else
        echo -e "${RED}❌ $SERVICE compilation failed${NC}"
        ((FAILED++))
        cd ..
        continue
    fi

    # Run package (skip tests for speed)
    if mvn package -DskipTests -q; then
        echo -e "${GREEN}✅ $SERVICE packaged successfully${NC}"
        echo -e "   JAR: target/$SERVICE-*.jar"
    else
        echo -e "${RED}❌ $SERVICE packaging failed${NC}"
        ((FAILED++))
    fi

    cd ..
    echo ""
done

# Summary
echo ""
echo "========================================"
echo "Build Summary"
echo "========================================"
echo -e "Successful: ${GREEN}$SUCCESSFUL${NC}"
echo -e "Failed: ${RED}$FAILED${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All services built successfully!${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Configure application.yml for each service"
    echo "2. Start MySQL database"
    echo "3. Start Kafka"
    echo "4. Run services: mvn spring-boot:run"
    exit 0
else
    echo -e "${RED}⚠️  Some services failed to build${NC}"
    echo ""
    echo "Troubleshooting:"
    echo "1. Check error messages above"
    echo "2. Ensure Java 17 is installed: java --version"
    echo "3. Ensure Maven 3.6+ is installed: mvn --version"
    echo "4. Clear Maven cache: rm -rf ~/.m2/repository/org/projectlombok"
    echo "5. See BUILD_ERROR_ANALYSIS.md for detailed help"
    exit 1
fi
