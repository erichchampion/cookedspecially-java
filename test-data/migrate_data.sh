#!/bin/bash
#
# CookedSpecially Data Migration Script
#
# This script provides a convenient wrapper around the Python data migration utility.
#

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default values
SQL_FILE="test-data/data-cookedspecially.sql"
DB_HOST="localhost"
DB_PORT="3306"
DB_USER="root"
DB_PASS=""
DRY_RUN=false

# Function to print colored output
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to show usage
usage() {
    cat << EOF
Usage: $0 [OPTIONS]

Options:
    -f, --file FILE         SQL dump file (default: test-data/data-cookedspecially.sql)
    -h, --host HOST         Database host (default: localhost)
    -P, --port PORT         Database port (default: 3306)
    -u, --user USER         Database username (default: root)
    -p, --password PASS     Database password
    -d, --dry-run           Perform a dry run without inserting data
    -t, --tables TABLES     Space-separated list of tables to migrate (default: all)
    --help                  Show this help message

Examples:
    # Dry run
    $0 --dry-run

    # Migrate all data
    $0 --password mypassword

    # Migrate specific tables
    $0 --password mypassword --tables "CUSTOMERS ORDERS"

    # Custom database connection
    $0 --host db.example.com --port 3306 --user admin --password secret

EOF
    exit 1
}

# Parse command line arguments
TABLES=""
while [[ $# -gt 0 ]]; do
    case $1 in
        -f|--file)
            SQL_FILE="$2"
            shift 2
            ;;
        -h|--host)
            DB_HOST="$2"
            shift 2
            ;;
        -P|--port)
            DB_PORT="$2"
            shift 2
            ;;
        -u|--user)
            DB_USER="$2"
            shift 2
            ;;
        -p|--password)
            DB_PASS="$2"
            shift 2
            ;;
        -d|--dry-run)
            DRY_RUN=true
            shift
            ;;
        -t|--tables)
            TABLES="$2"
            shift 2
            ;;
        --help)
            usage
            ;;
        *)
            print_error "Unknown option: $1"
            usage
            ;;
    esac
done

# Check if SQL file exists
if [ ! -f "$SQL_FILE" ]; then
    print_error "SQL file not found: $SQL_FILE"
    exit 1
fi

# Check if Python is available
if ! command -v python3 &> /dev/null; then
    print_error "Python 3 is required but not installed."
    exit 1
fi

# Check if mysql-connector-python is installed
if ! python3 -c "import mysql.connector" 2> /dev/null; then
    print_error "mysql-connector-python is not installed."
    print_info "Install it with: pip install mysql-connector-python"
    exit 1
fi

# Print configuration
print_info "CookedSpecially Data Migration"
echo "  SQL File: $SQL_FILE"
echo "  Database Host: $DB_HOST:$DB_PORT"
echo "  Database User: $DB_USER"
echo "  Dry Run: $DRY_RUN"
if [ -n "$TABLES" ]; then
    echo "  Tables: $TABLES"
else
    echo "  Tables: ALL"
fi
echo ""

# Confirm if not dry run
if [ "$DRY_RUN" = false ]; then
    print_warn "This will modify your database!"
    read -p "Are you sure you want to continue? (yes/no): " -r
    echo
    if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
        print_info "Migration cancelled."
        exit 0
    fi
fi

# Build Python command
PYTHON_CMD="python3 test-data/data_uploader.py \"$SQL_FILE\" --host \"$DB_HOST\" --port $DB_PORT --username \"$DB_USER\""

if [ -n "$DB_PASS" ]; then
    PYTHON_CMD="$PYTHON_CMD --password \"$DB_PASS\""
fi

if [ "$DRY_RUN" = true ]; then
    PYTHON_CMD="$PYTHON_CMD --dry-run"
fi

if [ -n "$TABLES" ]; then
    PYTHON_CMD="$PYTHON_CMD --tables $TABLES"
fi

# Execute migration
print_info "Starting migration..."
eval $PYTHON_CMD

if [ $? -eq 0 ]; then
    print_info "Migration completed successfully!"
else
    print_error "Migration failed. Check the logs for details."
    exit 1
fi
