# CookedSpecially Data Migration Utility

This utility migrates data from the legacy monolithic SQL dump to the modernized microservices architecture.

## Overview

The legacy CookedSpecially application used a single MySQL database with uppercase table names. The modernized architecture uses multiple microservices, each with its own database:

- **customer-service**: `cookedspecially_customers`
- **restaurant-service**: `cookedspecially_restaurants`
- **order-service**: `cookedspecially_orders`
- **payment-service**: `cookedspecially_payments`
- **kitchen-service**: `cookedspecially_kitchen`
- **loyalty-service**: `cookedspecially_loyalty`
- **admin-service**: `cookedspecially_admin`
- **integration-hub-service**: `cookedspecially_integrations`

## Prerequisites

1. Python 3.8 or higher
2. MySQL databases created for each service
3. MySQL connector for Python:
   ```bash
   pip install mysql-connector-python
   ```

## Setup

### 1. Create Service Databases

First, create the databases for each microservice:

```sql
CREATE DATABASE cookedspecially_customers;
CREATE DATABASE cookedspecially_restaurants;
CREATE DATABASE cookedspecially_orders;
CREATE DATABASE cookedspecially_payments;
CREATE DATABASE cookedspecially_kitchen;
CREATE DATABASE cookedspecially_loyalty;
CREATE DATABASE cookedspecially_admin;
CREATE DATABASE cookedspecially_integrations;
```

### 2. Run Database Migrations

Ensure each microservice has run its Flyway/Liquibase migrations to create the schema:

```bash
# From services directory
cd services/customer-service
./mvnw spring-boot:run

# Repeat for each service to initialize schemas
```

## Usage

### Basic Usage

Perform a dry run first to see what would be migrated:

```bash
python3 test-data/data_uploader.py test-data/data-cookedspecially.sql --dry-run
```

### Migrate All Data

```bash
python3 test-data/data_uploader.py test-data/data-cookedspecially.sql \
  --host localhost \
  --port 3306 \
  --username root \
  --password your_password
```

### Migrate Specific Tables

Migrate only customer-related tables:

```bash
python3 test-data/data_uploader.py test-data/data-cookedspecially.sql \
  --tables CUSTOMERS CUSTOMERADDRESS CUSTOMER_CREDIT \
  --username root \
  --password your_password
```

### With Custom Configuration

```bash
python3 test-data/data_uploader.py test-data/data-cookedspecially.sql \
  --host database.example.com \
  --port 3306 \
  --username migration_user \
  --password secure_password
```

## Table Mapping

### Customer Service (cookedspecially_customers)

| Legacy Table | New Table | Description |
|--------------|-----------|-------------|
| CUSTOMERS | customers | Customer profiles |
| CUSTOMERADDRESS | addresses | Customer delivery addresses |
| CUSTOMER_CREDIT | customer_credits | Customer credit balances |

### Restaurant Service (cookedspecially_restaurants)

| Legacy Table | New Table | Description |
|--------------|-----------|-------------|
| RESTAURANT | restaurants | Restaurant profiles |
| DISHES | menu_items | Menu items/dishes |
| DISHTYPES | menu_categories | Dish categories |
| MENUS | menus | Restaurant menus |
| MENU_SECTION | menu_sections | Menu sections |
| ADDONDISHES | addon_items | Add-on dishes |

### Order Service (cookedspecially_orders)

| Legacy Table | New Table | Description |
|--------------|-----------|-------------|
| ORDERS | orders | Customer orders |
| ORDERDISHES | order_items | Line items in orders |
| ORDERADDONS | order_item_addons | Add-ons for order items |
| CHECKS | checks | Payment checks/bills |
| CHECK_ORDER | check_orders | Check-order associations |

### Payment Service (cookedspecially_payments)

| Legacy Table | New Table | Description |
|--------------|-----------|-------------|
| PAYMENTTYPE | payment_methods | Payment method types |
| CREDIT_TRANSACTION | payment_transactions | Payment transactions |
| CREDIT_BILLS | credit_bills | Credit bills |
| CREDIT_BILL_PAYMENTS | bill_payments | Bill payment records |

### Kitchen Service (cookedspecially_kitchen)

| Legacy Table | New Table | Description |
|--------------|-----------|-------------|
| DELIVERYAREAS | delivery_areas | Delivery coverage areas |
| DELIVERYBOYS | delivery_personnel | Delivery staff |
| TILLS | tills | POS tills/registers |
| TILL_HANDOVER | till_handovers | Till handover records |
| SEATINGTABLES | seating_tables | Restaurant seating tables |

### Loyalty Service (cookedspecially_loyalty)

| Legacy Table | New Table | Description |
|--------------|-----------|-------------|
| coupon | coupons | Discount coupons |
| GIFT_CARD | gift_cards | Gift cards |
| GIFT_CARD_REDEMPTION | gift_card_transactions | Gift card usage |

### Admin Service (cookedspecially_admin)

| Legacy Table | New Table | Description |
|--------------|-----------|-------------|
| USER | users | System users |
| ROLES | roles | User roles |
| USER_ROLES | user_roles | User-role assignments |
| OTP | otps | One-time passwords |

### Integration Hub Service (cookedspecially_integrations)

| Legacy Table | New Table | Description |
|--------------|-----------|-------------|
| SOCIALCONNECTORS | social_connectors | Social media integrations |

## Data Transformations

The migration script applies several transformations:

### Customer Data
- Generates `cognito_sub` from legacy customer IDs (format: `legacy_{id}`)
- Sets default email if missing: `customer{id}@legacy.com`
- Sets all customers to `ACTIVE` status
- Marks email and phone as verified

### Restaurant Data
- Sets default `owner_id` (should be customized)
- Sets restaurants to `ACTIVE` status
- Enables delivery and pickup by default

### Other Transformations
- Date format conversions
- NULL handling
- Default value assignments
- ID mapping for foreign keys

## Important Notes

### Data Integrity
- **Foreign Key Dependencies**: Migrate tables in the correct order to maintain referential integrity:
  1. Users and Roles (admin-service)
  2. Customers (customer-service)
  3. Restaurants (restaurant-service)
  4. Menu Items (restaurant-service)
  5. Orders (order-service)
  6. Payments (payment-service)
  7. Loyalty data (loyalty-service)

### ID Preservation
- The script does NOT preserve original IDs by default
- Foreign key relationships need to be remapped
- Consider maintaining an ID mapping table if needed

### AWS Cognito Integration
- Customer records require valid `cognito_sub` values
- Legacy customers get generated subs: `legacy_{original_id}`
- Production migration should create actual Cognito users

## Troubleshooting

### Connection Errors
```
Error connecting to database: Access denied for user
```
**Solution**: Check database credentials and user permissions

### Table Not Found
```
Table 'cookedspecially_customers.customers' doesn't exist
```
**Solution**: Run database migrations for that service first

### Foreign Key Violations
```
Cannot add or update a child row: a foreign key constraint fails
```
**Solution**: Migrate parent tables before child tables

## Advanced Configuration

Edit `migration_config.json` to customize:

```json
{
  "migration_options": {
    "batch_size": 1000,          // Records per batch insert
    "skip_on_error": true,       // Continue on errors
    "log_errors": true,          // Log errors to file
    "preserve_ids": false,       // Preserve original IDs
    "generate_cognito_subs": true // Auto-generate Cognito subs
  }
}
```

## Testing

Always perform a dry run first:

```bash
# 1. Dry run to see what would happen
python3 test-data/data_uploader.py test-data/data-cookedspecially.sql --dry-run

# 2. Migrate to test database
python3 test-data/data_uploader.py test-data/data-cookedspecially.sql \
  --host localhost \
  --username test_user \
  --password test_pass

# 3. Verify data in each service database
mysql -u root -p -e "SELECT COUNT(*) FROM cookedspecially_customers.customers;"
mysql -u root -p -e "SELECT COUNT(*) FROM cookedspecially_restaurants.restaurants;"
# ... etc
```

## Production Migration Checklist

- [ ] Backup all databases before migration
- [ ] Run dry run on production dump
- [ ] Test migration on staging environment
- [ ] Document any custom transformations needed
- [ ] Plan for Cognito user creation
- [ ] Schedule maintenance window
- [ ] Prepare rollback plan
- [ ] Verify foreign key relationships
- [ ] Test application functionality post-migration
- [ ] Monitor service logs during migration

## Support

For issues or questions:
1. Check the logs in `migration_errors.log`
2. Review the table mapping in `migration_config.json`
3. Verify database connectivity and permissions
4. Ensure all service schemas are up to date

## Future Enhancements

- [ ] Parallel migration for better performance
- [ ] Resume capability for interrupted migrations
- [ ] ID mapping table generation
- [ ] Cognito user creation integration
- [ ] Data validation and cleanup
- [ ] Migration rollback capability
