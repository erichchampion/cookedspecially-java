# Admin Service

Admin and user management service with RBAC, employee management, OTP authentication, and session management.

## Overview

The Admin Service is responsible for managing system users, employees, roles, permissions, and authentication for the CookedSpecially platform.

## Features

### User Management
- ✅ Admin user CRUD operations
- ✅ User activation/deactivation
- ✅ Password management with BCrypt encryption
- ✅ Account locking after failed login attempts
- ✅ Multi-factor authentication (MFA) support
- ✅ Cognito integration support

### Employee Management
- ✅ Employee CRUD operations
- ✅ Restaurant/organization assignment
- ✅ Employment status tracking (ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED, TERMINATED)
- ✅ Employment type support (FULL_TIME, PART_TIME, CONTRACT, TEMPORARY, INTERN)
- ✅ Salary and emergency contact management

### Role-Based Access Control (RBAC)
- ✅ Role CRUD operations
- ✅ Permission management
- ✅ Role-permission associations
- ✅ User-role associations
- ✅ Resource-level permissions (resource.action format)
- ✅ Default roles: SUPER_ADMIN, ADMIN, MANAGER, EMPLOYEE

### Authentication
- ✅ Login/logout functionality
- ✅ OTP generation and validation
- ✅ Password reset workflow with OTP
- ✅ Session token management
- ✅ Failed login attempt tracking
- ✅ Account locking mechanism

### Session Management
- ✅ Session creation and validation
- ✅ Session expiry management
- ✅ Multiple concurrent session support
- ✅ Redis-based session storage
- ✅ IP address and user agent tracking
- ✅ Session invalidation

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Data JPA** (MySQL)
- **Spring Security** (OAuth2 Resource Server, BCrypt)
- **Spring Session** (Redis)
- **Flyway** (Database migrations)
- **AWS SDK** (Cognito, SNS, Secrets Manager)
- **Lombok**
- **Swagger/OpenAPI** (API documentation)

## Architecture

### Domain Entities
- `User` - System users (admins, employees)
- `Role` - User roles with permissions
- `Permission` - Granular permissions (resource.action)
- `Employee` - Employee profiles and details
- `OTP` - One-time passwords for authentication
- `UserSession` - User session tracking

### Key Services
- `UserService` - User lifecycle management
- `RoleService` - Role and permission management
- `EmployeeService` - Employee management
- `AuthenticationService` - Login, logout, password reset
- `OTPService` - OTP generation and validation
- `SessionManagementService` - Session lifecycle

## API Endpoints

### User Management (6 endpoints)
- `POST /users` - Create user
- `PUT /users/{id}` - Update user
- `GET /users/{id}` - Get user
- `GET /users` - List all users
- `DELETE /users/{id}` - Delete user

### Authentication (5 endpoints)
- `POST /auth/login` - User login
- `POST /auth/logout` - User logout
- `POST /auth/otp/generate` - Generate OTP
- `POST /auth/otp/validate` - Validate OTP
- `POST /auth/password/reset` - Reset password

### Employee Management (4 endpoints)
- `POST /employees` - Create employee
- `GET /employees/{id}` - Get employee
- `GET /employees?restaurantId={id}` - List employees by restaurant
- `DELETE /employees/{id}` - Delete employee

### Role Management (4 endpoints)
- `POST /roles` - Create role
- `GET /roles/{id}` - Get role
- `GET /roles` - List all roles
- `DELETE /roles/{id}` - Delete role

## Configuration

### Environment Variables

```bash
# Database
DB_URL=jdbc:mysql://localhost:3306/admin_service
DB_USERNAME=admin
DB_PASSWORD=password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# OAuth2
JWT_ISSUER_URI=https://cognito-idp.us-east-1.amazonaws.com/us-east-1_xxxxx
JWT_JWK_SET_URI=https://cognito-idp.us-east-1.amazonaws.com/us-east-1_xxxxx/.well-known/jwks.json

# AWS
AWS_REGION=us-east-1
COGNITO_USER_POOL_ID=us-east-1_xxxxx
COGNITO_CLIENT_ID=xxxxx
SNS_TOPIC_ARN=arn:aws:sns:us-east-1:123456789012:admin-notifications

# Authentication
AUTH_OTP_LENGTH=6
AUTH_OTP_EXPIRY_MINUTES=5
AUTH_OTP_MAX_ATTEMPTS=3
```

## Running Locally

### Prerequisites
- Java 21
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

### Steps

1. **Start MySQL and Redis**
   ```bash
   docker-compose up -d mysql redis
   ```

2. **Build the application**
   ```bash
   mvn clean package
   ```

3. **Run the application**
   ```bash
   java -jar target/admin-service-1.0.0-SNAPSHOT.jar
   ```

4. **Access Swagger UI**
   ```
   http://localhost:8087/api/v1/swagger-ui.html
   ```

## Docker Build

```bash
docker build -t admin-service:latest .
docker run -p 8087:8087 admin-service:latest
```

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`

- `V1__Initial_schema.sql` - Creates all tables
- `V2__Seed_data.sql` - Seeds default roles and permissions

### Default Roles

| Role | Description | Permissions |
|------|-------------|-------------|
| SUPER_ADMIN | Full system access | All permissions |
| ADMIN | Most administrative access | Most permissions |
| MANAGER | Limited administrative access | Selected permissions |
| EMPLOYEE | Basic access | Read-only permissions |

### Default Permissions

- `user.{create,read,update,delete}`
- `role.{create,read,update,delete}`
- `employee.{create,read,update,delete}`

## Security

### Password Security
- BCrypt encryption with salt
- Minimum 8 characters
- Requires uppercase, lowercase, digit, and special character
- Password age tracking (90-day expiry)

### Account Security
- Failed login attempt tracking
- Account locking after 5 failed attempts
- 1-hour lock duration
- MFA support via OTP

### Session Security
- Redis-based session storage
- Session expiry (30 minutes default)
- Remember me option (30 days)
- IP address and user agent tracking
- Max 3 concurrent sessions per user

### API Security
- OAuth2 JWT authentication
- Role-based access control
- Resource-level permissions
- CORS configuration

## Integration Points

### AWS Cognito
- User pool integration
- OAuth2 token validation
- User attribute synchronization

### Other Services
- **Customer Service**: Customer authentication (separate)
- **Restaurant Service**: Restaurant owner management
- **Notification Service**: OTP delivery via SMS/email

## Monitoring

### Metrics
- Login success/failure rates
- Active sessions count
- OTP generation/validation rates
- Account lock events

### Health Checks
- Database connectivity
- Redis connectivity
- Cognito availability

## Future Enhancements

- [ ] Password complexity policies
- [ ] Session analytics dashboard
- [ ] Audit log for all user actions
- [ ] Advanced MFA (TOTP, biometric)
- [ ] SSO integration
- [ ] Password history tracking
- [ ] User activity monitoring

## License

Copyright © 2025 CookedSpecially. All rights reserved.
