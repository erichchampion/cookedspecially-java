# Integration Hub Service

Third-party integration hub for Zomato, social media, and partner platforms.

## Overview

The Integration Hub Service is responsible for managing all external integrations including:
- **Zomato Integration**: Order intake, order status synchronization, menu sync
- **Social Media Connectors**: Facebook, Instagram, WhatsApp integration
- **Generic Webhook Handler**: Configurable webhook routing for various partners

## Features

### Zomato Integration
- ✅ Order webhook receiver (duplicate detection)
- ✅ Order confirmation/rejection to Zomato
- ✅ Order status synchronization
- ✅ Menu synchronization
- ✅ Restaurant status updates

### Social Media Integration
- ✅ Social connector CRUD operations
- ✅ OAuth integration support
- ✅ Connector status management
- ✅ Manual sync triggers

### Webhook Management
- ✅ Webhook signature validation
- ✅ Request/response logging
- ✅ Error handling with retry mechanism
- ✅ Duplicate order detection (Redis)

### Integration Monitoring
- ✅ Health check endpoints
- ✅ Webhook log querying
- ✅ Integration analytics
- ✅ Error tracking

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Data JPA** (MySQL)
- **Spring Security** (OAuth2 Resource Server)
- **Spring Data Redis** (Caching & Deduplication)
- **WebFlux** (Reactive HTTP client)
- **Flyway** (Database migrations)
- **AWS SDK** (S3, SQS, DynamoDB, Secrets Manager)
- **Swagger/OpenAPI** (API documentation)

## Architecture

### Domain Entities
- `SocialConnector` - Social media connector configuration
- `IntegrationConfig` - Partner integration configuration
- `WebhookLog` - Webhook request/response logs

### Key Services
- `ZomatoService` - Zomato order and menu integration
- `SocialConnectorService` - Social media connector management
- `WebhookSecurityService` - Webhook signature validation
- `WebhookLogService` - Webhook logging and tracking

## API Endpoints

### Zomato Integration
- `POST /integrations/zomato/webhook/order` - Receive order webhook (public)
- `POST /integrations/zomato/orders/{orderId}/confirm` - Confirm order
- `POST /integrations/zomato/orders/{orderId}/reject` - Reject order
- `POST /integrations/zomato/orders/{orderId}/status` - Update order status
- `POST /integrations/zomato/menu/sync` - Sync menu
- `POST /integrations/zomato/restaurant/status` - Update restaurant status

### Social Connectors
- `POST /integrations/social-connectors` - Create connector
- `PUT /integrations/social-connectors/{id}` - Update connector
- `GET /integrations/social-connectors/{id}` - Get connector
- `GET /integrations/social-connectors?restaurantId={id}` - List connectors
- `DELETE /integrations/social-connectors/{id}` - Delete connector
- `POST /integrations/social-connectors/{id}/sync` - Trigger sync

### Integration Health
- `GET /integrations/health` - Health status
- `GET /integrations/webhooks/logs` - Webhook logs
- `GET /integrations/webhooks/logs/date-range` - Logs by date range
- `GET /integrations/analytics` - Integration analytics

## Configuration

### Environment Variables

```bash
# Database
DB_URL=jdbc:mysql://localhost:3306/integration_hub
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
S3_BUCKET=cookedspecially-integration-logs
SQS_WEBHOOK_RETRY_QUEUE=integration-hub-webhook-retry
SQS_DLQ_QUEUE=integration-hub-dlq

# Zomato Integration
ZOMATO_BASE_URL=https://api.zomato.com/v1
ZOMATO_WEBHOOK_SECRET=your-webhook-secret

# Facebook Integration
FACEBOOK_APP_ID=your-app-id
FACEBOOK_APP_SECRET=your-app-secret

# Webhook Security
WEBHOOK_SIGNATURE_VALIDATION=true
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
   java -jar target/integration-hub-service-1.0.0-SNAPSHOT.jar
   ```

4. **Access Swagger UI**
   ```
   http://localhost:8086/api/v1/swagger-ui.html
   ```

## Docker Build

```bash
docker build -t integration-hub-service:latest .
docker run -p 8086:8086 integration-hub-service:latest
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

- `V1__Initial_schema.sql` - Creates tables for social_connectors, integration_configs, webhook_logs

## Security

### Webhook Security
- HMAC-SHA256 signature validation for all webhooks
- Configurable signature validation (can be disabled for testing)
- Request/response logging for audit trail

### API Security
- OAuth2 JWT authentication for all authenticated endpoints
- Public webhook endpoints (signature-validated)
- CORS configuration

## Integration Points

### Order Service
- Create orders from external sources (Zomato, etc.)
- Update order status

### Restaurant Service
- Get menu data for synchronization
- Get restaurant configuration

### Notification Service
- Alert on integration failures
- Send webhook processing errors

## Monitoring

### Metrics
- Webhook processing time
- Integration success/failure rates
- Order volume by source

### Health Checks
- Database connectivity
- Redis connectivity
- External API availability

## Error Handling

### Retry Mechanism
- Exponential backoff (5s, 10s, 20s...)
- Max 3 retry attempts
- Dead letter queue for permanent failures

### Duplicate Detection
- Redis-based deduplication
- 24-hour TTL for order IDs

## Future Enhancements

- [ ] Swiggy integration
- [ ] Uber Eats integration
- [ ] WhatsApp Business API
- [ ] Instagram Direct integration
- [ ] Advanced analytics dashboard
- [ ] Real-time webhook monitoring UI

## License

Copyright © 2025 CookedSpecially. All rights reserved.
