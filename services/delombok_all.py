#!/usr/bin/env python3
"""
Comprehensive Lombok to Plain Java converter for all microservices.
Handles entities, DTOs with static methods, DTOs without static methods, and @Slf4j.
"""

import re
import os
from pathlib import Path
from typing import List, Dict, Tuple

def read_file(path: str) -> str:
    """Read file content"""
    with open(path, 'r', encoding='utf-8') as f:
        return f.read()

def write_file(path: str, content: str):
    """Write file content"""
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

def extract_class_name(content: str) -> str:
    """Extract the class name from Java content"""
    match = re.search(r'public\s+(class|record|enum)\s+(\w+)', content)
    return match.group(2) if match else None

def extract_package(content: str) -> str:
    """Extract package declaration"""
    match = re.search(r'package\s+([\w.]+);', content)
    return match.group(1) if match else ""

def has_annotation(content: str, annotation: str) -> bool:
    """Check if content has a specific annotation"""
    return f'@{annotation}' in content

def has_static_methods(content: str) -> bool:
    """Check if class has static factory methods"""
    return bool(re.search(r'public\s+static\s+\w+\s+(fromEntity|of|from)\s*\(', content))

def is_entity(content: str) -> bool:
    """Check if this is a JPA entity"""
    return has_annotation(content, 'Entity') or has_annotation(content, 'Table')

def is_dto_path(filepath: str) -> bool:
    """Check if file is in DTO package"""
    return '/dto/' in filepath

def is_domain_path(filepath: str) -> bool:
    """Check if file is in domain package"""
    return '/domain/' in filepath

def extract_fields(content: str) -> List[Dict]:
    """Extract all field declarations with their annotations"""
    fields = []

    # Find all field declarations (even multi-line with annotations)
    lines = content.split('\n')
    i = 0
    while i < len(lines):
        line = lines[i]

        # Skip if not a field line
        if not re.search(r'^\s+private\s+', line):
            i += 1
            continue

        # Collect annotations above this field
        annotations = []
        j = i - 1
        while j >= 0 and (lines[j].strip().startswith('@') or lines[j].strip() == ''):
            if lines[j].strip().startswith('@'):
                annotations.insert(0, lines[j].strip())
            j -= 1

        # Parse the field line
        field_match = re.search(r'^\s+private\s+(\S+(?:<[^>]+>)?)\s+(\w+)\s*(?:=\s*([^;]+))?\s*;', line)
        if field_match:
            field_type = field_match.group(1)
            field_name = field_match.group(2)
            default_value = field_match.group(3).strip() if field_match.group(3) else None

            fields.append({
                'name': field_name,
                'type': field_type,
                'annotations': annotations,
                'default': default_value
            })

        i += 1

    return fields

def capitalize(s: str) -> str:
    """Capitalize first letter"""
    return s[0].upper() + s[1:] if s else s

def generate_constructor(class_name: str, fields: List[Dict], include_defaults: bool = False) -> str:
    """Generate all-args constructor"""
    if not fields:
        return ""

    params = []
    assignments = []

    for field in fields:
        params.append(f"{field['type']} {field['name']}")
        if include_defaults and field['default']:
            assignments.append(f"        this.{field['name']} = {field['name']} != null ? {field['name']} : {field['default']};")
        else:
            assignments.append(f"        this.{field['name']} = {field['name']};")

    params_str = ', '.join(params)
    if len(params_str) > 80:
        # Multi-line parameters
        params_str = ',\n' + ',\n'.join([f"                 {p}" for p in params])
        constructor = f'''    public {class_name}({params_str}) {{
{chr(10).join(assignments)}
    }}'''
    else:
        constructor = f'''    public {class_name}({params_str}) {{
{chr(10).join(assignments)}
    }}'''

    return constructor

def generate_getters_setters(fields: List[Dict]) -> str:
    """Generate getter and setter methods"""
    methods = []

    for field in fields:
        field_name = field['name']
        field_type = field['type']

        # Getter
        getter_name = f"get{capitalize(field_name)}"
        if field_type in ['boolean', 'Boolean']:
            getter_name = f"is{capitalize(field_name)}"

        getter = f'''    public {field_type} {getter_name}() {{
        return {field_name};
    }}'''
        methods.append(getter)

        # Setter
        setter_name = f"set{capitalize(field_name)}"
        setter = f'''    public void {setter_name}({field_type} {field_name}) {{
        this.{field_name} = {field_name};
    }}'''
        methods.append(setter)

    return '\n\n'.join(methods)

def remove_lombok_imports(content: str) -> str:
    """Remove all Lombok imports"""
    content = re.sub(r'import\s+lombok\..*?;\n', '', content)
    return content

def remove_lombok_annotations(content: str) -> str:
    """Remove Lombok annotations from class declaration"""
    annotations = ['@Data', '@Getter', '@Setter', '@NoArgsConstructor', '@AllArgsConstructor',
                   '@Builder', '@RequiredArgsConstructor', '@ToString', '@EqualsAndHashCode']

    for ann in annotations:
        content = re.sub(rf'{ann}\s*\n', '', content)
        content = re.sub(rf'{ann}\s*\(.*?\)\s*\n', '', content)

    # Remove @Builder.Default from fields
    content = re.sub(r'@Builder\.Default\s*\n\s*', '', content)

    return content

def add_slf4j_logger(content: str, class_name: str) -> str:
    """Replace @Slf4j with manual logger declaration"""
    if not has_annotation(content, 'Slf4j'):
        return content

    # Remove @Slf4j
    content = re.sub(r'@Slf4j\s*\n', '', content)
    content = re.sub(r'import\s+lombok\.extern\.slf4j\.Slf4j;\n', '', content)

    # Add SLF4J imports after package
    if 'import org.slf4j.Logger;' not in content:
        package_match = re.search(r'(package\s+[\w.]+;)', content)
        if package_match:
            package_line = package_match.group(1)
            content = content.replace(
                package_line,
                f"{package_line}\n\nimport org.slf4j.Logger;\nimport org.slf4j.LoggerFactory;"
            )

    # Add logger field after class opening brace
    class_pattern = rf'(public\s+(?:class|interface)\s+{class_name}[^{{]*\{{)'
    logger_field = f'\n\n    private static final Logger log = LoggerFactory.getLogger({class_name}.class);'

    content = re.sub(class_pattern, rf'\1{logger_field}', content)

    return content

def convert_entity_to_plain_class(content: str, class_name: str) -> str:
    """Convert JPA entity from Lombok to plain Java"""
    # Extract fields before removing annotations
    fields = extract_fields(content)

    # Remove Lombok stuff
    content = remove_lombok_annotations(content)
    content = remove_lombok_imports(content)

    # Generate constructors and methods
    no_args_constructor = f'''    // Constructors
    public {class_name}() {{
    }}'''

    all_args_constructor = generate_constructor(class_name, fields, include_defaults=True)
    getters_setters = generate_getters_setters(fields)

    # Find the last field or the position before helper methods/closing brace
    # Insert constructors and getters/setters before any existing methods

    # Look for existing methods or closing brace
    helper_methods_match = re.search(r'(\n\s+//\s*Helper methods|\n\s+public\s+\w+\s+\w+\s*\(|\n\})', content)

    if helper_methods_match:
        insert_pos = helper_methods_match.start()
        new_code = f'\n\n{no_args_constructor}\n\n{all_args_constructor}\n\n    // Getters and Setters\n{getters_setters}\n'
        content = content[:insert_pos] + new_code + content[insert_pos:]
    else:
        # Fallback: insert before closing brace
        content = re.sub(r'\n\}$', f'\n\n{no_args_constructor}\n\n{all_args_constructor}\n\n    // Getters and Setters\n{getters_setters}\n}}', content)

    return content

def convert_dto_with_static_methods(content: str, class_name: str) -> str:
    """Convert DTO that has static methods - keep as class with explicit getters"""
    fields = extract_fields(content)

    # Remove Lombok stuff
    content = remove_lombok_annotations(content)
    content = remove_lombok_imports(content)

    # Generate only getters (DTOs are typically immutable-ish)
    no_args_constructor = f'''    // Constructors
    public {class_name}() {{
    }}'''

    all_args_constructor = generate_constructor(class_name, fields)
    getters = []

    for field in fields:
        field_name = field['name']
        field_type = field['type']
        getter_name = f"get{capitalize(field_name)}"
        if field_type in ['boolean', 'Boolean']:
            getter_name = f"is{capitalize(field_name)}"

        getter = f'''    public {field_type} {getter_name}() {{
        return {field_name};
    }}'''
        getters.append(getter)

    getters_str = '\n\n'.join(getters)

    # Find position before static methods
    static_method_match = re.search(r'(\n\s+public\s+static\s+)', content)

    if static_method_match:
        insert_pos = static_method_match.start()
        new_code = f'\n\n{no_args_constructor}\n\n{all_args_constructor}\n\n    // Getters\n{getters_str}\n'
        content = content[:insert_pos] + new_code + content[insert_pos:]
    else:
        content = re.sub(r'\n\}$', f'\n\n{no_args_constructor}\n\n{all_args_constructor}\n\n    // Getters\n{getters_str}\n}}', content)

    return content

def convert_simple_dto_to_record(content: str, class_name: str) -> str:
    """Convert simple DTO without static methods to Java Record"""
    fields = extract_fields(content)

    # Build record parameters
    params = []
    for field in fields:
        # Add validation annotations if present
        param_parts = []
        for ann in field['annotations']:
            if not ann.startswith('@JsonInclude') and not ann.startswith('@Json'):
                param_parts.append(f"    {ann}")

        if param_parts:
            param_parts.append(f"    {field['type']} {field['name']}")
            params.append('\n'.join(param_parts))
        else:
            params.append(f"    {field['type']} {field['name']}")

    params_str = ',\n\n'.join(params)

    # Remove Lombok imports
    content = remove_lombok_imports(content)

    # Find class definition and replace with record
    class_pattern = r'(public\s+)class\s+' + class_name + r'\s*\{[^}]*?(\n\s+private\s+.*?)+(\n\s*//.*)*\n\s*\}'

    # Extract javadoc and annotations before class
    pre_class_match = re.search(r'((?:/\*\*[\s\S]*?\*/\s*)?(?:@[\w.]+(?:\([^)]*\))?\s*)*)(public\s+class\s+' + class_name + r')', content)

    if pre_class_match:
        pre_class = pre_class_match.group(1)

        record_def = f'''{pre_class}public record {class_name}(
{params_str}
) {{}}'''

        # Replace entire class definition with record
        content = re.sub(
            r'((?:/\*\*[\s\S]*?\*/\s*)?(?:@[\w.]+(?:\([^)]*\))?\s*)*)public\s+class\s+' + class_name + r'\s*\{[\s\S]*?\n\}',
            record_def,
            content
        )

    return content

def process_java_file(filepath: str) -> Tuple[bool, str]:
    """Process a single Java file. Returns (success, message)"""
    content = read_file(filepath)

    # Skip if no Lombok
    if 'lombok' not in content:
        return (False, "No Lombok")

    original_content = content
    class_name = extract_class_name(content)

    if not class_name:
        return (False, "ERROR: No class found")

    # Handle @Slf4j first (applies to all)
    if has_annotation(content, 'Slf4j'):
        content = add_slf4j_logger(content, class_name)

    # Determine conversion strategy
    if is_entity(content):
        # Entity: convert to plain class with getters/setters
        content = convert_entity_to_plain_class(content, class_name)
        conversion_type = "Entity -> Plain Class"
    elif is_dto_path(filepath):
        if has_static_methods(content):
            # DTO with static methods: keep as class
            content = convert_dto_with_static_methods(content, class_name)
            conversion_type = "DTO -> Class (has static methods)"
        else:
            # Simple DTO: convert to record
            content = convert_simple_dto_to_record(content, class_name)
            conversion_type = "DTO -> Record"
    elif has_annotation(content, 'RequiredArgsConstructor'):
        # Service/Controller with constructor injection
        content = remove_lombok_annotations(content)
        content = remove_lombok_imports(content)

        # Find final fields and create constructor
        final_fields = []
        for line in content.split('\n'):
            match = re.search(r'private\s+final\s+(\S+(?:<[^>]+>)?)\s+(\w+);', line)
            if match:
                final_fields.append({'type': match.group(1), 'name': match.group(2)})

        if final_fields:
            constructor = generate_constructor(class_name, final_fields)
            # Insert constructor after fields
            last_field_pattern = r'(private\s+final\s+\S+\s+\w+;)(\n)'
            content = re.sub(last_field_pattern, rf'\1\2\n    // Constructor\n{constructor}\n', content, count=1)

        conversion_type = "Service/Controller"
    else:
        # Generic class
        content = remove_lombok_annotations(content)
        content = remove_lombok_imports(content)
        conversion_type = "Generic"

    # Only write if content changed
    if content != original_content:
        write_file(filepath, content)
        return (True, conversion_type)
    else:
        return (False, "No changes needed")

def main():
    """Main conversion function"""
    services_dir = Path(__file__).parent

    # Process each microservice
    services = ['order-service', 'payment-service', 'restaurant-service', 'notification-service']

    total_files = 0
    converted_files = 0
    errors = []

    print("=" * 80)
    print("LOMBOK TO PLAIN JAVA CONVERTER")
    print("=" * 80)

    for service in services:
        print(f"\n### Processing {service} ###\n")

        service_path = services_dir / service / 'src' / 'main' / 'java'
        if not service_path.exists():
            print(f"  SKIP: Path not found: {service_path}")
            continue

        java_files = list(service_path.rglob('*.java'))
        print(f"  Found {len(java_files)} Java files")

        service_converted = 0

        for java_file in sorted(java_files):
            total_files += 1
            rel_path = java_file.relative_to(service_path)

            success, message = process_java_file(str(java_file))

            if success:
                service_converted += 1
                converted_files += 1
                print(f"  ✓ {rel_path} ({message})")
            elif "ERROR" in message:
                errors.append(f"{service}/{rel_path}: {message}")
                print(f"  ✗ {rel_path} - {message}")
            # Skip printing "No Lombok" messages

        print(f"\n  Converted {service_converted}/{len(java_files)} files in {service}")

    print("\n" + "=" * 80)
    print(f"SUMMARY: Converted {converted_files}/{total_files} files")

    if errors:
        print(f"\nERRORS ({len(errors)}):")
        for error in errors:
            print(f"  - {error}")

    print("\n⚠️  IMPORTANT: Review all changes before committing!")
    print("=" * 80)

if __name__ == '__main__':
    main()
