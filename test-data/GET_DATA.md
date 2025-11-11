# Test Data

## Large SQL Dump File

The legacy database dump `data-cookedspecially.sql` (206MB) is too large for GitHub.

### Getting the Data

**Option 1: Generate Sample Data**
Use the migration utilities with empty databases to see the schema structure.

**Option 2: Request from Repository Owner**
Contact the repository owner for access to the full SQL dump file.

**Option 3: Use Your Own Data**
If you have access to a CookedSpecially database, you can export it using:

```bash
mysqldump -u username -p database_name > test-data/data-cookedspecially.sql
```

### File Location

Place the SQL file at: `test-data/data-cookedspecially.sql`

This location is already in `.gitignore` to prevent accidental commits of large files.

### Data Structure

The SQL dump should contain these legacy tables:
- CUSTOMERS (with obfuscated data)
- RESTAURANT, DISHES, MENUS
- ORDERS, ORDERDISHES, CHECKS
- PAYMENTTYPE, CREDIT_TRANSACTION
- DELIVERYAREAS, DELIVERYBOYS, TILLS
- coupon, GIFT_CARD
- USER, ROLES, OTP
- SOCIALCONNECTORS

See `DATA_MIGRATION_README.md` for complete table mapping.
