-- Seed Data for Admin Service

-- Insert default permissions
INSERT INTO permissions (name, resource, action, description) VALUES
('user.create', 'user', 'create', 'Create new users'),
('user.read', 'user', 'read', 'Read user information'),
('user.update', 'user', 'update', 'Update user information'),
('user.delete', 'user', 'delete', 'Delete users'),
('role.create', 'role', 'create', 'Create new roles'),
('role.read', 'role', 'read', 'Read role information'),
('role.update', 'role', 'update', 'Update role information'),
('role.delete', 'role', 'delete', 'Delete roles'),
('employee.create', 'employee', 'create', 'Create employees'),
('employee.read', 'employee', 'read', 'Read employee information'),
('employee.update', 'employee', 'update', 'Update employee information'),
('employee.delete', 'employee', 'delete', 'Delete employees');

-- Insert default roles
INSERT INTO roles (name, description) VALUES
('SUPER_ADMIN', 'Super administrator with full system access'),
('ADMIN', 'Administrator with most permissions'),
('MANAGER', 'Manager with limited administrative access'),
('EMPLOYEE', 'Regular employee with basic access');

-- Assign all permissions to SUPER_ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'SUPER_ADMIN';

-- Assign read permissions to EMPLOYEE role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'EMPLOYEE' AND p.action = 'read';
