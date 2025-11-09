#!/bin/bash

# Script to convert Lombok code to plain Java using Maven delombok

cd "$(dirname "$0")"

echo "Running delombok on all services..."

for service in order-service payment-service restaurant-service notification-service; do
    echo "Processing $service..."
    cd "$service"

    # Run delombok
    mvn lombok:delombok -Dlombok.outputDirectory=src/main/delomboked

    # If successful, replace original files
    if [ -d "src/main/delomboked" ]; then
        echo "Replacing original files with delomboked versions for $service..."
        rm -rf src/main/java
        mv src/main/delomboked src/main/java
    fi

    cd ..
done

echo "Delombok complete! Now removing Lombok dependencies..."

# Remove Lombok from pom.xml files
for pom in */pom.xml; do
    echo "Cleaning $pom..."
    # This will be done manually to ensure correctness
done

echo "Done! Please review the changes and remove Lombok dependencies from pom.xml files."
