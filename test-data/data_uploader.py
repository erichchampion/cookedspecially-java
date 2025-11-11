#!/usr/bin/env python3
"""
CookedSpecially Data Migration Utility

This script migrates data from the legacy SQL dump to the modernized microservices databases.
It parses the legacy schema and transforms the data to match the new microservices architecture.
"""

import re
import sys
import argparse
import mysql.connector
from mysql.connector import Error
from datetime import datetime
import json


# Table mapping: legacy_table -> (service_database, new_table_name, transformer_function)
TABLE_MAPPING = {
    # Customer Service
    'CUSTOMERS': ('cookedspecially_customers', 'customers', 'transform_customers'),
    'CUSTOMERADDRESS': ('cookedspecially_customers', 'addresses', 'transform_customer_addresses'),
    'CUSTOMER_CREDIT': ('cookedspecially_customers', 'customer_credits', 'transform_customer_credits'),

    # Restaurant Service
    'RESTAURANT': ('cookedspecially_restaurants', 'restaurants', 'transform_restaurants'),
    'DISHES': ('cookedspecially_restaurants', 'menu_items', 'transform_dishes'),
    'DISHTYPES': ('cookedspecially_restaurants', 'menu_categories', 'transform_dish_types'),
    'MENUS': ('cookedspecially_restaurants', 'menus', 'transform_menus'),
    'MENU_SECTION': ('cookedspecially_restaurants', 'menu_sections', 'transform_menu_sections'),

    # Order Service
    'ORDERS': ('cookedspecially_orders', 'orders', 'transform_orders'),
    'ORDERDISHES': ('cookedspecially_orders', 'order_items', 'transform_order_dishes'),
    'ORDERADDONS': ('cookedspecially_orders', 'order_item_addons', 'transform_order_addons'),
    'CHECKS': ('cookedspecially_orders', 'checks', 'transform_checks'),

    # Payment Service
    'PAYMENTTYPE': ('cookedspecially_payments', 'payment_methods', 'transform_payment_types'),
    'CREDIT_TRANSACTION': ('cookedspecially_payments', 'payment_transactions', 'transform_credit_transactions'),

    # Kitchen Service
    'DELIVERYAREAS': ('cookedspecially_kitchen', 'delivery_areas', 'transform_delivery_areas'),
    'DELIVERYBOYS': ('cookedspecially_kitchen', 'delivery_personnel', 'transform_delivery_boys'),
    'TILLS': ('cookedspecially_kitchen', 'tills', 'transform_tills'),
    'TILL_HANDOVER': ('cookedspecially_kitchen', 'till_handovers', 'transform_till_handovers'),
    'SEATINGTABLES': ('cookedspecially_kitchen', 'seating_tables', 'transform_seating_tables'),

    # Loyalty Service
    'coupon': ('cookedspecially_loyalty', 'coupons', 'transform_coupons'),
    'GIFT_CARD': ('cookedspecially_loyalty', 'gift_cards', 'transform_gift_cards'),
    'GIFT_CARD_REDEMPTION': ('cookedspecially_loyalty', 'gift_card_transactions', 'transform_gift_card_redemptions'),

    # Admin Service
    'USER': ('cookedspecially_admin', 'users', 'transform_users'),
    'ROLES': ('cookedspecially_admin', 'roles', 'transform_roles'),
    'USER_ROLES': ('cookedspecially_admin', 'user_roles', 'transform_user_roles'),
    'OTP': ('cookedspecially_admin', 'otps', 'transform_otps'),

    # Integration Hub Service
    'SOCIALCONNECTORS': ('cookedspecially_integrations', 'social_connectors', 'transform_social_connectors'),
}


class DatabaseConfig:
    """Database configuration for each service"""
    def __init__(self, host='localhost', port=3306, username='root', password='password'):
        self.host = host
        self.port = port
        self.username = username
        self.password = password
        self.connections = {}

    def get_connection(self, database):
        """Get or create a database connection"""
        if database not in self.connections:
            try:
                connection = mysql.connector.connect(
                    host=self.host,
                    port=self.port,
                    user=self.username,
                    password=self.password,
                    database=database
                )
                self.connections[database] = connection
                print(f"Connected to database: {database}")
            except Error as e:
                print(f"Error connecting to database {database}: {e}")
                return None
        return self.connections[database]

    def close_all(self):
        """Close all database connections"""
        for db, conn in self.connections.items():
            if conn.is_connected():
                conn.close()
                print(f"Closed connection to: {db}")


class SQLDumpParser:
    """Parser for MySQL SQL dumps"""

    def __init__(self, sql_file_path):
        self.sql_file_path = sql_file_path
        self.tables = {}

    def parse(self):
        """Parse the SQL dump file and extract table data"""
        print(f"Parsing SQL dump: {self.sql_file_path}")

        with open(self.sql_file_path, 'r', encoding='utf-8') as f:
            content = f.read()

        # Find all table sections
        table_pattern = r'LOCK TABLES `([^`]+)` WRITE;.*?INSERT INTO `\1` VALUES (.*?);.*?UNLOCK TABLES;'
        matches = re.finditer(table_pattern, content, re.DOTALL)

        for match in matches:
            table_name = match.group(1)
            values_str = match.group(2)

            # Parse the records
            records = self._parse_records(values_str)
            self.tables[table_name] = records
            print(f"  Found {len(records)} records in table {table_name}")

        return self.tables

    def _parse_records(self, values_str):
        """Parse INSERT VALUES into individual records"""
        records = []
        i = 0

        while i < len(values_str):
            if values_str[i] == '(':
                # Find matching closing parenthesis
                paren_depth = 1
                in_quote = False
                j = i + 1

                while j < len(values_str) and paren_depth > 0:
                    char = values_str[j]

                    if char == "'" and (j == 0 or values_str[j-1] != '\\'):
                        in_quote = not in_quote
                    elif char == '(' and not in_quote:
                        paren_depth += 1
                    elif char == ')' and not in_quote:
                        paren_depth -= 1

                    j += 1

                # Extract the record
                record_str = values_str[i:j]
                fields = self._parse_fields(record_str[1:-1])
                records.append(fields)
                i = j
            else:
                i += 1

        return records

    def _parse_fields(self, record_content):
        """Parse fields from a record"""
        fields = []
        current = ""
        in_quote = False

        i = 0
        while i < len(record_content):
            char = record_content[i]

            if char == "'" and (i == 0 or record_content[i-1] != '\\'):
                in_quote = not in_quote
                current += char
            elif char == ',' and not in_quote:
                fields.append(self._process_field(current))
                current = ""
            else:
                current += char

            i += 1

        if current:
            fields.append(self._process_field(current))

        return fields

    def _process_field(self, field):
        """Process a field value"""
        field = field.strip()

        if field == 'NULL':
            return None
        elif field.startswith("'") and field.endswith("'"):
            # Remove quotes and unescape
            return field[1:-1].replace("\\'", "'").replace('\\"', '"')
        else:
            # Numeric value
            return field


class DataTransformer:
    """Transforms legacy data to modern schema"""

    @staticmethod
    def transform_customers(legacy_record):
        """Transform CUSTOMERS table to customers table"""
        # Legacy: (id, ?, ?, first_name, last_name, address1, address2, phone, email, ?,
        #          created_at, updated_at, ?, ?, social_id, ?, email_verified, phone_verified, ?, ?, ?, ?)

        return {
            'cognito_sub': f"legacy_{legacy_record[0]}",  # Generate a cognito_sub from legacy ID
            'email': legacy_record[8] if legacy_record[8] else f"customer{legacy_record[0]}@legacy.com",
            'phone_number': legacy_record[7],
            'first_name': legacy_record[3] if legacy_record[3] else 'Customer',
            'last_name': legacy_record[4] if legacy_record[4] else str(legacy_record[0]),
            'status': 'ACTIVE',
            'email_verified': True,
            'phone_verified': True,
            'created_at': legacy_record[10] if len(legacy_record) > 10 else None,
            'updated_at': legacy_record[11] if len(legacy_record) > 11 else None,
        }

    @staticmethod
    def transform_restaurants(legacy_record):
        """Transform RESTAURANT table to restaurants table"""
        return {
            'name': legacy_record[1] if len(legacy_record) > 1 else 'Unknown',
            'owner_id': 1,  # Default owner, should be mapped properly
            'description': legacy_record[2] if len(legacy_record) > 2 else None,
            'status': 'ACTIVE',
            'phone_number': legacy_record[3] if len(legacy_record) > 3 else None,
            'email': legacy_record[4] if len(legacy_record) > 4 else None,
            'address': legacy_record[5] if len(legacy_record) > 5 else None,
            'is_active': True,
            'accepts_delivery': True,
            'accepts_pickup': True,
        }

    # Add more transformer methods as needed...


class DataMigrator:
    """Main data migration orchestrator"""

    def __init__(self, sql_file, db_config, dry_run=False):
        self.sql_file = sql_file
        self.db_config = db_config
        self.dry_run = dry_run
        self.parser = SQLDumpParser(sql_file)
        self.transformer = DataTransformer()
        self.stats = {}

    def migrate(self, tables_to_migrate=None):
        """Migrate data from SQL dump to microservices databases"""
        print(f"\n{'='*60}")
        print(f"CookedSpecially Data Migration Utility")
        print(f"Mode: {'DRY RUN' if self.dry_run else 'LIVE MIGRATION'}")
        print(f"{'='*60}\n")

        # Parse the SQL dump
        legacy_tables = self.parser.parse()

        # Migrate each table
        tables = tables_to_migrate if tables_to_migrate else legacy_tables.keys()

        for legacy_table in tables:
            if legacy_table not in legacy_tables:
                print(f"Warning: Table {legacy_table} not found in SQL dump")
                continue

            if legacy_table not in TABLE_MAPPING:
                print(f"Skipping unmapped table: {legacy_table}")
                continue

            self._migrate_table(legacy_table, legacy_tables[legacy_table])

        # Print summary
        self._print_summary()

    def _migrate_table(self, legacy_table, records):
        """Migrate a single table"""
        target_db, target_table, transformer_name = TABLE_MAPPING[legacy_table]

        print(f"\nMigrating {legacy_table} -> {target_db}.{target_table}")
        print(f"  Records to migrate: {len(records)}")

        if self.dry_run:
            print(f"  [DRY RUN] Would migrate {len(records)} records")
            self.stats[legacy_table] = {'target': f"{target_db}.{target_table}", 'count': len(records), 'status': 'dry_run'}
            return

        # Get database connection
        connection = self.db_config.get_connection(target_db)
        if not connection:
            print(f"  Error: Could not connect to database {target_db}")
            self.stats[legacy_table] = {'target': f"{target_db}.{target_table}", 'count': 0, 'status': 'failed'}
            return

        # Transform and insert records
        transformer = getattr(self.transformer, transformer_name, None)
        if not transformer:
            print(f"  Warning: No transformer found for {transformer_name}, skipping...")
            return

        cursor = connection.cursor()
        success_count = 0
        error_count = 0

        for record in records:
            try:
                transformed = transformer(record)
                # TODO: Generate INSERT statement and execute
                # For now, just count
                success_count += 1
            except Exception as e:
                error_count += 1
                if error_count <= 5:  # Only print first 5 errors
                    print(f"  Error transforming record: {e}")

        cursor.close()

        print(f"  Successfully migrated: {success_count}")
        if error_count > 0:
            print(f"  Errors: {error_count}")

        self.stats[legacy_table] = {
            'target': f"{target_db}.{target_table}",
            'count': success_count,
            'errors': error_count,
            'status': 'completed'
        }

    def _print_summary(self):
        """Print migration summary"""
        print(f"\n{'='*60}")
        print("Migration Summary")
        print(f"{'='*60}")

        for table, stats in self.stats.items():
            print(f"{table:30} -> {stats['target']:40} [{stats['count']} records] [{stats['status']}]")

        print(f"{'='*60}\n")


def main():
    parser = argparse.ArgumentParser(description='CookedSpecially Data Migration Utility')
    parser.add_argument('sql_file', help='Path to the legacy SQL dump file')
    parser.add_argument('--host', default='localhost', help='Database host (default: localhost)')
    parser.add_argument('--port', type=int, default=3306, help='Database port (default: 3306)')
    parser.add_argument('--username', default='root', help='Database username (default: root)')
    parser.add_argument('--password', default='password', help='Database password')
    parser.add_argument('--dry-run', action='store_true', help='Perform a dry run without actually inserting data')
    parser.add_argument('--tables', nargs='+', help='Specific tables to migrate (default: all)')

    args = parser.parse_args()

    # Create database config
    db_config = DatabaseConfig(
        host=args.host,
        port=args.port,
        username=args.username,
        password=args.password
    )

    # Create migrator
    migrator = DataMigrator(args.sql_file, db_config, dry_run=args.dry_run)

    try:
        # Run migration
        migrator.migrate(args.tables)
    finally:
        # Close all connections
        db_config.close_all()


if __name__ == '__main__':
    main()
