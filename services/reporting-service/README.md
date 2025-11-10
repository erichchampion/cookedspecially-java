# Reporting Service

## Overview

The Reporting Service provides comprehensive business analytics and reporting capabilities for the CookedSpecially platform. It generates sales, customer, product, and operations reports with support for Excel and PDF export formats.

## Features

### Report Categories

#### 1. Sales Reports
- **Daily Invoice Report**: Detailed invoice listing with tax breakdown
- **Daily Sales Summary**: Aggregated sales data by fulfillment center
- **Detailed Invoice Report**: Line-item level sales details
- **Sales Register Report**: Till-wise sales summary with reconciliation

#### 2. Customer Reports
- **Customer List Report**: Customer registration and activity data
- **Customer Summary**: Customer lifetime value and behavior metrics
- **Top Customers**: Revenue-based customer ranking

#### 3. Product Reports
- **Top Dishes Report**: Best-selling items by quantity and revenue
- **Category Performance**: Sales analysis by product category
- **Menu Item Analytics**: Item profitability and performance trends

#### 4. Operations Reports
- **Delivery Performance**: Delivery personnel metrics and KPIs
- **Till Summary**: Cash register operations and reconciliation

### Key Capabilities

- **Multiple Export Formats**: Excel (.xlsx) and PDF
- **Scheduled Reports**: Automated daily, weekly, and monthly reports
- **Email Delivery**: Automated report distribution via AWS SES
- **S3 Storage**: Secure cloud storage with presigned download URLs
- **Caching**: Redis-based caching for improved performance
- **Date Range Filtering**: Flexible date-based report filtering

## Technology Stack

- **Framework**: Spring Boot 3.3.5
- **Language**: Java 21
- **Database**: MySQL 8.0 (with read replica support)
- **Cache**: Redis
- **Report Generation**: Apache POI (Excel), iText (PDF)
- **Cloud Services**: AWS S3, SES
- **Database Migration**: Flyway
- **API Documentation**: Swagger/OpenAPI 3
- **Security**: OAuth 2.0 Resource Server

## Architecture

### Component Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     Reporting Service                        │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Controllers                                                 │
│  ├── ReportController (Main report generation)              │
│  ├── SalesReportController                                  │
│  ├── CustomerReportController                               │
│  ├── ProductReportController                                │
│  ├── OperationsReportController                             │
│  └── ScheduledReportController                              │
│                                                              │
│  Services                                                    │
│  ├── ReportGenerationService (Orchestration)                │
│  ├── SalesReportService                                     │
│  ├── CustomerReportService                                  │
│  ├── ProductReportService                                   │
│  ├── OperationsReportService                                │
│  ├── ExcelGenerationService (Apache POI)                    │
│  ├── PdfGenerationService (iText)                           │
│  ├── S3StorageService (AWS S3)                              │
│  ├── EmailService (AWS SES)                                 │
│  └── ScheduledReportService (Quartz)                        │
│                                                              │
│  Data Sources                                                │
│  ├── MySQL Read Replica (Report queries)                    │
│  ├── Redis (Result caching)                                 │
│  └── S3 (Report storage)                                    │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Integration Points

- **Order Service**: Order data, transactions
- **Payment Service**: Payment transactions, refunds
- **Customer Service**: Customer profiles and data
- **Restaurant Service**: Menu items, categories
- **Kitchen Service**: Delivery data, till transactions
- **Loyalty Service**: Coupon usage, gift cards

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.9+
- MySQL 8.0
- Redis 6.0+
- AWS Account (for S3 and SES)

### Environment Variables

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=reporting_db
DB_USERNAME=reporting_user
DB_PASSWORD=your_password

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# AWS Configuration
AWS_REGION=us-east-1
S3_REPORTS_BUCKET=cookedspecially-reports
SES_FROM_EMAIL=reports@cookedspecially.com

# Security Configuration
JWT_ISSUER_URI=https://cognito-idp.us-east-1.amazonaws.com/your-pool-id
JWT_JWK_SET_URI=https://cognito-idp.us-east-1.amazonaws.com/your-pool-id/.well-known/jwks.json

# Scheduled Reports
SCHEDULED_REPORTS_ENABLED=true
DAILY_REPORT_CRON=0 0 8 * * ?
WEEKLY_REPORT_CRON=0 0 9 * * MON
MONTHLY_REPORT_CRON=0 0 9 1 * ?
```

### Building the Service

```bash
# Build with Maven
cd services/reporting-service
./mvnw clean package

# Build Docker image
docker build -t reporting-service:latest .
```

### Running Locally

```bash
# Run with Maven
./mvnw spring-boot:run

# Run with Docker
docker run -p 8086:8086 \
  -e DB_HOST=host.docker.internal \
  -e REDIS_HOST=host.docker.internal \
  reporting-service:latest
```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run integration tests
./mvnw verify
```

## API Documentation

### Swagger UI

Once the service is running, access the interactive API documentation at:

```
http://localhost:8086/swagger-ui.html
```

### Key Endpoints

#### Report Generation

**Generate Report**
```http
POST /api/v1/reports/generate
Content-Type: application/json

{
  "reportType": "DAILY_INVOICE",
  "format": "EXCEL",
  "fromDate": "2025-01-01",
  "toDate": "2025-01-31",
  "restaurantId": 1,
  "fulfillmentCenterId": 1
}
```

**Get Report Status**
```http
GET /api/v1/reports/{reportId}
```

#### Sales Reports

```http
GET /api/v1/reports/sales/daily-invoice?fromDate=2025-01-01&toDate=2025-01-31&restaurantId=1
GET /api/v1/reports/sales/daily-summary?fromDate=2025-01-01&toDate=2025-01-31
GET /api/v1/reports/sales/detailed-invoice?fromDate=2025-01-01&toDate=2025-01-31
```

#### Customer Reports

```http
GET /api/v1/reports/customers/list?fromDate=2025-01-01&toDate=2025-01-31
GET /api/v1/reports/customers/summary?fromDate=2025-01-01&toDate=2025-01-31
GET /api/v1/reports/customers/top?fromDate=2025-01-01&toDate=2025-01-31&limit=10
```

#### Product Reports

```http
GET /api/v1/reports/products/top-dishes?fromDate=2025-01-01&toDate=2025-01-31&limit=10
GET /api/v1/reports/products/category-performance?fromDate=2025-01-01&toDate=2025-01-31
```

#### Operations Reports

```http
GET /api/v1/reports/operations/delivery-performance?fromDate=2025-01-01&toDate=2025-01-31
GET /api/v1/reports/operations/till-summary?tillId=1&fromDate=2025-01-01&toDate=2025-01-31
```

#### Scheduled Reports

```http
POST   /api/v1/reports/scheduled
GET    /api/v1/reports/scheduled
PUT    /api/v1/reports/scheduled/{id}
DELETE /api/v1/reports/scheduled/{id}
```

## Database Schema

### Tables

#### scheduled_reports
Stores scheduled report configurations.

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| report_name | VARCHAR(255) | Report name |
| report_type | VARCHAR(50) | Report type enum |
| cron_expression | VARCHAR(100) | Cron schedule |
| format | VARCHAR(20) | Export format |
| recipient_emails | TEXT | Comma-separated emails |
| restaurant_id | BIGINT | Filter by restaurant |
| fulfillment_center_id | BIGINT | Filter by FC |
| parameters | TEXT | Additional parameters |
| active | BOOLEAN | Is active |
| created_at | TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | Update timestamp |
| last_run_at | TIMESTAMP | Last execution time |
| next_run_at | TIMESTAMP | Next execution time |

#### report_execution_history
Tracks report generation history.

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| scheduled_report_id | BIGINT | Related scheduled report |
| report_type | VARCHAR(50) | Report type |
| format | VARCHAR(20) | Export format |
| status | VARCHAR(20) | Execution status |
| start_time | TIMESTAMP | Start time |
| end_time | TIMESTAMP | End time |
| execution_duration_ms | BIGINT | Duration in ms |
| file_path | VARCHAR(500) | Local file path |
| file_size_bytes | BIGINT | File size |
| s3_url | VARCHAR(1000) | S3 download URL |
| parameters | TEXT | Report parameters |
| error_message | TEXT | Error details |
| row_count | INT | Number of rows |
| generated_by | VARCHAR(255) | User who generated |

## Performance Optimization

### Caching Strategy

Reports are cached in Redis with a 30-minute TTL:

- Daily invoice reports
- Sales summaries
- Customer lists
- Top dishes
- Category performance

### Database Optimization

- **Read Replicas**: Report queries use read replicas to avoid impacting production
- **Indexes**: Optimized indexes on date columns and foreign keys
- **Query Optimization**: Efficient aggregation queries with proper joins

### Report Generation

- **Async Processing**: Large reports generated asynchronously
- **Streaming**: Excel generation uses streaming for large datasets
- **Compression**: Reports compressed before S3 upload

## Monitoring

### Health Checks

```http
GET /actuator/health
```

### Metrics

Prometheus metrics available at:
```http
GET /actuator/prometheus
```

Key metrics:
- `report_generation_duration_seconds`: Report generation time
- `report_generation_total`: Total reports generated
- `report_generation_failures_total`: Failed report generations
- `cache_hit_ratio`: Cache effectiveness

### Logging

Structured logging with the following levels:
- **INFO**: Normal operations, report generation started/completed
- **WARN**: Performance issues, cache misses
- **ERROR**: Report generation failures, AWS service errors

## Scheduled Reports

### Configuration

Scheduled reports use cron expressions for flexible scheduling:

```java
// Daily at 8 AM
"0 0 8 * * ?"

// Weekly on Monday at 9 AM
"0 0 9 * * MON"

// Monthly on the 1st at 9 AM
"0 0 9 1 * ?"
```

### Email Templates

Reports are delivered via email with:
- Report name and type
- Presigned S3 download URL (7-day expiration)
- Generation timestamp
- HTML-formatted email body

## Security

### Authentication

- OAuth 2.0 JWT tokens required for all endpoints
- JWT validation against AWS Cognito

### Authorization

- Role-based access control (RBAC)
- Admin role for scheduled report management
- Restaurant-specific data isolation

### Data Protection

- S3 presigned URLs with limited expiration
- Encrypted data transfer (HTTPS)
- Secrets managed via AWS Secrets Manager

## Error Handling

### Exception Types

- `ReportNotFoundException`: Report not found by ID
- `ReportGenerationException`: Report generation failed
- `InvalidDateRangeException`: Invalid date range provided

### Error Responses

```json
{
  "status": 400,
  "message": "Invalid date range: fromDate must be before toDate",
  "timestamp": "2025-11-10T10:30:00"
}
```

## Deployment

### Docker Deployment

```bash
# Build image
docker build -t reporting-service:1.0.0 .

# Tag for ECR
docker tag reporting-service:1.0.0 \
  123456789012.dkr.ecr.us-east-1.amazonaws.com/reporting-service:1.0.0

# Push to ECR
docker push 123456789012.dkr.ecr.us-east-1.amazonaws.com/reporting-service:1.0.0
```

### ECS Deployment

Service runs on AWS ECS Fargate with:
- 2 vCPU, 4GB RAM
- Auto-scaling based on CPU utilization
- Load balancer integration
- Health check monitoring

### Environment-Specific Configuration

Use Spring profiles for environment-specific settings:
- `dev`: Development environment
- `staging`: Staging environment
- `prod`: Production environment

```bash
# Run with specific profile
java -jar app.jar --spring.profiles.active=prod
```

## Troubleshooting

### Common Issues

**Issue**: Report generation timeout
- **Solution**: Reduce date range, increase timeout settings

**Issue**: S3 upload failures
- **Solution**: Check AWS credentials, S3 bucket permissions

**Issue**: Cache connection errors
- **Solution**: Verify Redis connectivity, check credentials

**Issue**: Email delivery failures
- **Solution**: Verify SES configuration, check email addresses

### Debug Mode

Enable debug logging:

```properties
logging.level.com.cookedspecially.reportingservice=DEBUG
```

## Contributing

### Code Style

- Follow Java coding conventions
- Use Lombok for boilerplate reduction
- Write comprehensive JavaDoc comments
- Include unit and integration tests

### Testing Guidelines

- Minimum 80% code coverage
- Unit tests for all service methods
- Integration tests for controllers
- Mock external dependencies

## License

Copyright (c) 2025 CookedSpecially. All rights reserved.

## Support

For questions or issues:
- Email: support@cookedspecially.com
- Slack: #reporting-service
- Documentation: https://docs.cookedspecially.com/reporting-service
