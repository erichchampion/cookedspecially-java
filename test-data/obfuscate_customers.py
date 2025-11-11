#!/usr/bin/env python3
"""
Script to obfuscate customer phone numbers and emails in SQL data file.
"""
import re
import sys

def split_csv_fields(record_content):
    """
    Split a record's content by commas, respecting quoted strings.
    record_content should not include the surrounding parentheses.
    """
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
            fields.append(current)
            current = ""
        else:
            current += char

        i += 1

    if current:
        fields.append(current)

    return fields


def obfuscate_customer_data(input_file, output_file):
    """
    Read SQL file and replace phone numbers and emails with generic auto-incrementing values.
    """
    counter = 1

    print(f"Reading from {input_file}...")
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()

    print("Processing CUSTOMERS table...")

    # Find the CUSTOMERS section
    customers_start = content.find("LOCK TABLES `CUSTOMERS` WRITE;")
    customers_end = content.find("UNLOCK TABLES;", customers_start)

    if customers_start == -1 or customers_end == -1:
        print("Could not find CUSTOMERS table section")
        return

    print(f"Found CUSTOMERS section")

    # Extract the CUSTOMERS section
    before = content[:customers_start]
    customers_section = content[customers_start:customers_end + len("UNLOCK TABLES;")]
    after = content[customers_end + len("UNLOCK TABLES;"):]

    # Find the VALUES clause
    values_start = customers_section.find("VALUES ")
    if values_start == -1:
        print("Could not find VALUES clause")
        return

    # Extract the part before and after VALUES
    section_before_values = customers_section[:values_start + len("VALUES ")]
    section_after_values = customers_section[values_start + len("VALUES "):]

    # Now parse records from section_after_values
    # Records are in format: (field1,field2,...),(field1,field2,...),... until we hit ;
    records = []
    i = 0
    while i < len(section_after_values):
        if section_after_values[i] == '(':
            # Find the matching closing parenthesis
            paren_depth = 1
            in_quote = False
            j = i + 1

            while j < len(section_after_values) and paren_depth > 0:
                char = section_after_values[j]

                if char == "'" and (j == 0 or section_after_values[j-1] != '\\'):
                    in_quote = not in_quote
                elif char == '(' and not in_quote:
                    paren_depth += 1
                elif char == ')' and not in_quote:
                    paren_depth -= 1

                j += 1

            # Extract the record (including parentheses)
            record = section_after_values[i:j]
            records.append(record)
            i = j
        elif section_after_values[i] == ';':
            # End of INSERT statement
            break
        else:
            i += 1

    print(f"Found {len(records)} customer records")

    # Process each record
    modified_records = []
    for record in records:
        # Remove the outer parentheses
        record_content = record[1:-1]

        # Split by commas
        fields = split_csv_fields(record_content)

        # Replace first name (field 3), last name (field 4), phone (field 7), and email (field 8)
        if len(fields) >= 9:
            # Replace first name (field 3)
            fields[3] = "'Customer'"

            # Replace last name (field 4)
            fields[4] = f"'1408{counter:07d}'"

            # Replace phone number (field 7)
            new_phone = f"'+1408{counter:07d}'"
            fields[7] = new_phone

            # Replace email (field 8)
            email_field = fields[8].strip()
            if email_field and email_field != 'NULL':
                new_email = f"'customer1408{counter:07d}@cookedspecially.com'"
                fields[8] = new_email

            counter += 1

        # Reconstruct the record
        modified_record = '(' + ','.join(fields) + ')'
        modified_records.append(modified_record)

    # Reconstruct the VALUES clause
    modified_values = ','.join(modified_records) + ';'

    # Find what comes after the VALUES clause in the original (after the semicolon)
    semicolon_pos = section_after_values.find(';')
    if semicolon_pos != -1:
        after_insert = section_after_values[semicolon_pos + 1:]
    else:
        after_insert = ""

    # Reconstruct the customers section
    modified_customers_section = section_before_values + modified_values + after_insert

    # Reconstruct the full content
    modified_content = before + modified_customers_section + after

    print(f"Writing to {output_file}...")
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(modified_content)

    print(f"Done! Obfuscated {counter - 1} customer records.")

if __name__ == "__main__":
    input_file = "test-data/data-cookedspecially.sql"
    output_file = "test-data/data-cookedspecially.sql"

    obfuscate_customer_data(input_file, output_file)
