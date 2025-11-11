# Data Migration Quick Start Guide

This guide will help you quickly migrate data from the legacy SQL dump to your microservices.

## Prerequisites

1. **Python 3.8+** installed
2. **MySQL** databases created for each service
3. **MySQL connector for Python**:
   ```bash
   pip install mysql-connector-python
   ```

## Quick Start (3 Steps)

### Step 1: Create Databases

```bash
# Login to MySQL
mysql -u root -p

# Create all service databases
CREATE DATABASE cookedspecially_customers;
CREATE DATABASE cookedspecially_restaurants;
CREATE DATABASE cookedspecially_orders;
CREATE DATABASE cookedspecially_payments;
CREATE DATABASE cookedspecially_kitchen;
CREATE DATABASE cookedspecially_loyalty;
CREATE DATABASE cookedspecially_admin;
CREATE DATABASE cookedspecially_integrations;

# Exit MySQL
EXIT;
```

### Step 2: Initialize Schemas

Start each microservice to let JPA/Hibernate create the tables:

```bash
cd services

# Start each service (in separate terminals or use tmux)
cd customer-service && ./mvnw spring-boot:run &
cd order-service && ./mvnw spring-boot:run &
cd restaurant-service && ./mvnw spring-boot:run &
cd payment-service && ./mvnw spring-boot:run &
cd kitchen-service && ./mvnw spring-boot:run &
cd loyalty-service && ./mvnw spring-boot:run &
cd admin-service && ./mvnw spring-boot:run &

# Wait for services to start, then stop them (Ctrl+C)
```

### Step 3: Run Migration

```bash
# Dry run first (recommended)
./test-data/migrate_data.sh --dry-run

# Run actual migration
./test-data/migrate_data.sh --password your_mysql_password
```

## Alternative: Python Script Directly

```bash
# Dry run
python3 test-data/data_uploader.py test-data/data-cookedspecially.sql --dry-run

# Actual migration
python3 test-data/data_uploader.py test-data/data-cookedspecially.sql \
  --username root \
  --password your_password
```

## Verify Migration

Check that data was migrated:

```bash
mysql -u root -p -e "
  SELECT
    'customers' as service,
    COUNT(*) as count
  FROM cookedspecially_customers.customers
  UNION ALL
  SELECT
    'restaurants',
    COUNT(*)
  FROM cookedspecially_restaurants.restaurants
  UNION ALL
  SELECT
    'orders',
    COUNT(*)
  FROM cookedspecially_orders.orders;
"
```

## Migrate Specific Tables

### Customer Data Only

```bash
./test-data/migrate_data.sh \
  --password your_password \
  --tables "CUSTOMERS CUSTOMERADDRESS"
```

### Restaurant Data Only

```bash
./test-data/migrate_data.sh \
  --password your_password \
  --tables "RESTAURANT DISHES MENUS"
```

## Troubleshooting

### Issue: "mysql-connector-python not found"

```bash
pip install mysql-connector-python
# or
pip3 install mysql-connector-python
```

### Issue: "Database doesn't exist"

Make sure you created the databases (Step 1) and ran migrations (Step 2).

### Issue: "Connection refused"

Check that MySQL is running:
```bash
# macOS
brew services start mysql

# Linux
sudo systemctl start mysql
```

### Issue: "Access denied"

Check your MySQL credentials:
```bash
mysql -u root -p
# Enter your password and verify it works
```

## What Gets Migrated?

| Service | Tables | Records (Example) |
|---------|--------|-------------------|
| Customer | customers, addresses | 4,816 customers |
| Restaurant | restaurants, menu_items | Varies |
| Order | orders, order_items | Varies |
| Payment | payments, transactions | Varies |
| Kitchen | delivery_areas, tills | Varies |
| Loyalty | coupons, gift_cards | Varies |
| Admin | users, roles | Varies |

## Important Notes

⚠️ **Before Production Migration:**
1. Always backup your databases
2. Test on staging environment first
3. Plan for downtime window
4. Verify foreign key relationships
5. Test application functionality post-migration

📚 **For Detailed Documentation:**
- See [DATA_MIGRATION_README.md](./DATA_MIGRATION_README.md) for comprehensive guide
- See [migration_config.json](./migration_config.json) for configuration options

## Need Help?

Check the detailed documentation:
- `DATA_MIGRATION_README.md` - Full migration guide
- `migration_config.json` - Configuration reference
- `data_uploader.py` - Source code with transformers

## Next Steps

After migration:
1. Start all microservices
2. Test API endpoints
3. Verify data integrity
4. Check application logs
5. Run integration tests
