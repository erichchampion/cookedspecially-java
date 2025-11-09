#!/usr/bin/env python3
"""
Convert Lombok annotations to plain Java code.
Handles DTOs (to Records) and entities (to plain classes).
"""

import re
import os
from pathlib import Path

def is_dto(file_path):
    """Check if file is a DTO (in dto package)"""
    return '/dto/' in file_path

def is_entity(file_path):
    """Check if file is an entity (in domain package)"""
    return '/domain/' in file_path

def has_static_methods(content):
    """Check if class has static factory methods like fromEntity"""
    return 'public static' in content and ('fromEntity' in content or 'of(' in content or 'from(' in content)

def has_jpa_annotations(content):
    """Check if class has JPA entity annotations"""
    return '@Entity' in content or '@Table' in content

def extract_fields(content):
    """Extract field definitions from class"""
    # Match private fields
    pattern = r'(\s+)(@[A-Z][^\n]*\n)*(\s+)private\s+(\S+)\s+(\w+)(.*?);'
    matches = re.findall(pattern, content, re.MULTILINE)

    fields = []
    for match in matches:
        indent = match[0]
        annotations = match[1] if match[1] else ''
        field_type = match[3]
        field_name = match[4]
        default_value = match[5].strip()

        fields.append({
            'indent': indent,
            'annotations': annotations.strip(),
            'type': field_type,
            'name': field_name,
            'default': default_value
        })

    return fields

def convert_to_record(content, class_name):
    """Convert DTO to Java Record"""
    fields = extract_fields(content)

    # Remove Lombok imports
    content = re.sub(r'import lombok\..*?;?\n', '', content)

    # Remove Lombok annotations from class
    content = re.sub(r'@Data\s*\n', '', content)
    content = re.sub(r'@NoArgsConstructor\s*\n', '', content)
    content = re.sub(r'@AllArgsConstructor\s*\n', '', content)
    content = re.sub(r'@Builder\s*\n', '', content)
    content = re.sub(r'@Getter\s*\n', '', content)
    content = re.sub(r'@Setter\s*\n', '', content)

    # Build record parameters
    params = []
    for field in fields:
        param_str = ''
        if field['annotations']:
            param_str += '    ' + field['annotations'] + '\n    '
        param_str += f"{field['type']} {field['name']}"
        params.append(param_str)

    params_str = ',\n\n'.join(params)

    # Replace class definition with record
    class_pattern = r'public class ' + class_name + r' \{[^}]*?\n(\s+private.*?;[\s\S]*?)\n\}'

    record_def = f'''public record {class_name}(
{params_str}
) {{
}}'''

    # This is a simplified conversion - for production, we'd need more sophisticated parsing
    return content

def generate_getters_setters(fields):
    """Generate getter and setter methods"""
    methods = []

    for field in fields:
        # Getter
        getter_name = 'get' + field['name'][0].upper() + field['name'][1:]
        if field['type'] == 'boolean' or field['type'] == 'Boolean':
            getter_name = 'is' + field['name'][0].upper() + field['name'][1:]

        getter = f'''    public {field['type']} {getter_name}() {{
        return {field['name']};
    }}'''
        methods.append(getter)

        # Setter
        setter_name = 'set' + field['name'][0].upper() + field['name'][1:]
        setter = f'''    public void {setter_name}({field['type']} {field['name']}) {{
        this.{field['name']} = {field['name']};
    }}'''
        methods.append(setter)

    return '\n\n'.join(methods)

def convert_entity(content):
    """Convert entity to plain Java class with getters/setters"""
    fields = extract_fields(content)

    # Remove Lombok imports
    content = re.sub(r'import lombok\..*?;?\n', '', content)

    # Remove Lombok annotations
    content = re.sub(r'@Data\s*\n', '', content)
    content = re.sub(r'@NoArgsConstructor\s*\n', '', content)
    content = re.sub(r'@AllArgsConstructor\s*\n', '', content)
    content = re.sub(r'@Builder\s*\n', '', content)
    content = re.sub(r'@Getter\s*\n', '', content)
    content = re.sub(r'@Setter\s*\n', '', content)

    # Generate getters and setters
    getters_setters = generate_getters_setters(fields)

    # Insert before closing brace
    content = re.sub(r'\n\}$', f'\n{getters_setters}\n}}', content)

    return content

def convert_slf4j(content):
    """Replace @Slf4j with standard logger"""
    if '@Slf4j' in content:
        # Remove @Slf4j annotation
        content = re.sub(r'@Slf4j\s*\n', '', content)

        # Remove Lombok import
        content = re.sub(r'import lombok\.extern\.slf4j\.Slf4j;\n', '', content)

        # Add SLF4J imports if not present
        if 'import org.slf4j.Logger;' not in content:
            # Find package declaration and add imports after it
            content = re.sub(
                r'(package .*?;\n)',
                r'\1\nimport org.slf4j.Logger;\nimport org.slf4j.LoggerFactory;\n',
                content
            )

        # Extract class name
        class_match = re.search(r'public class (\w+)', content)
        if class_match:
            class_name = class_match.group(1)

            # Add logger field after class declaration
            content = re.sub(
                r'(public class ' + class_name + r'[^\{]*\{)',
                r'\1\n    private static final Logger log = LoggerFactory.getLogger(' + class_name + r'.class);\n',
                content
            )

    return content

def process_file(file_path):
    """Process a single Java file"""
    print(f"Processing: {file_path}")

    with open(file_path, 'r') as f:
        content = f.read()

    # Skip if no Lombok annotations
    if 'lombok' not in content:
        print(f"  Skipping (no Lombok)")
        return False

    # Extract class name
    class_match = re.search(r'public (?:class|record) (\w+)', content)
    if not class_match:
        print(f"  ERROR: Could not find class name")
        return False

    class_name = class_match.group(1)

    # Convert based on type
    if is_dto(file_path) and not has_static_methods(content):
        print(f"  Converting DTO to Record")
        # For now, mark for manual conversion due to complexity
        print(f"  TODO: Manual conversion needed for {file_path}")
        return False
    elif is_entity(file_path) or has_jpa_annotations(content):
        print(f"  Converting Entity to plain class")
        content = convert_entity(content)
    else:
        print(f"  Converting to plain class")
        content = convert_entity(content)

    # Always convert @Slf4j
    content = convert_slf4j(content)

    # Write back
    with open(file_path, 'w') as f:
        f.write(content)

    print(f"  ✓ Converted")
    return True

def main():
    services_dir = Path(__file__).parent

    # Find all Java files
    java_files = []
    for service in ['order-service', 'payment-service', 'restaurant-service', 'notification-service']:
        service_path = services_dir / service / 'src' / 'main' / 'java'
        if service_path.exists():
            java_files.extend(service_path.rglob('*.java'))

    print(f"Found {len(java_files)} Java files")
    print("=" * 80)

    converted = 0
    for java_file in java_files:
        if process_file(str(java_file)):
            converted += 1

    print("=" * 80)
    print(f"Converted {converted} files")
    print("\nNote: DTOs with complex logic need manual conversion to Records")
    print("Please review all changes before committing!")

if __name__ == '__main__':
    main()
