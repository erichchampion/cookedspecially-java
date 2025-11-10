# CookedSpecially AWS Infrastructure

This directory contains the complete AWS infrastructure as code (IaC) for the CookedSpecially platform, implementing the modernization strategy outlined in `AWS_MODERNIZATION_PROPOSAL.md`.

## 📁 Directory Structure

```
infrastructure/
├── terraform/
│   ├── modules/               # Reusable Terraform modules
│   │   ├── vpc/              # VPC, subnets, NAT, VPC endpoints
│   │   ├── ecs-fargate/      # ECS cluster, services, ALB, auto-scaling
│   │   ├── rds/              # RDS MySQL Multi-AZ with read replicas
│   │   ├── elasticache/      # Redis cluster for caching
│   │   ├── dynamodb/         # DynamoDB tables for real-time data
│   │   ├── s3-cloudfront/    # S3 buckets and CloudFront CDN
│   │   ├── cognito/          # User authentication and authorization
│   │   ├── api-gateway/      # API Gateway (optional)
│   │   ├── monitoring/       # CloudWatch dashboards and alarms
│   │   └── security/         # WAF, security groups, IAM
│   ├── environments/         # Environment-specific configurations
│   │   ├── dev/             # Development environment
│   │   ├── staging/         # Staging environment
│   │   └── prod/            # Production environment
│   └── main.tf              # Root Terraform configuration
├── docker/                   # Docker configurations
└── ecs/                      # ECS task definitions
```

## 🏗️ Infrastructure Components

### 1. Network Layer (VPC Module)

**Features:**
- Multi-AZ VPC with public and private subnets
- NAT Gateways for private subnet internet access
- VPC Endpoints for cost optimization (S3, DynamoDB, ECR, CloudWatch, Secrets Manager)
- Security groups and NACLs

**Subnets:**
- Public subnets: ALB, NAT Gateway
- Private app subnets: ECS tasks, ElastiCache
- Private DB subnets: RDS instances

### 2. Compute Layer (ECS Fargate Module)

**Features:**
- Serverless container orchestration
- Application Load Balancer with path-based routing
- Auto-scaling based on CPU/Memory
- Blue-green deployment support
- CloudWatch Logs integration

**Services:**
- order-service (port 8081) - `/api/v1/orders*`
- payment-service (port 8082) - `/api/v1/payments*`
- restaurant-service (port 8083) - `/api/v1/restaurants*`, `/api/v1/menu-items*`
- notification-service (port 8084) - `/api/v1/notifications*`

**Configuration:**
- CPU: 512 (0.5 vCPU) - configurable
- Memory: 1024 MB - configurable
- Min tasks: 2 (dev: 1)
- Max tasks: 10 (dev: 2)
- Health checks via `/actuator/health`

### 3. Data Layer

#### RDS MySQL (Multi-AZ)
- MySQL 8.0.35
- Multi-AZ for high availability
- Automated backups (7-day retention)
- Performance Insights enabled
- Read replicas (optional, prod)
- Encryption at rest and in transit

#### ElastiCache Redis
- Redis 7.0
- Cluster mode with automatic failover
- Multi-AZ replication
- Automated backups
- CloudWatch monitoring

#### DynamoDB Tables
- **order-tracking**: Real-time order status
- **payment-transactions**: Payment records
- **sessions**: Distributed session management
- Features: TTL, Point-in-time recovery, DynamoDB Streams

### 4. Storage and CDN

#### S3 Buckets
- **restaurant-images**: Restaurant and menu photos
- **reports**: Generated reports (PDF, Excel)
- Features: Versioning, lifecycle policies, encryption

#### CloudFront
- Global CDN for static assets
- HTTPS/TLS 1.2+
- GZIP compression
- Cache optimization

### 5. Authentication (Cognito)

**User Pools:**
- Email-based authentication
- Password policy enforcement
- MFA support (optional)
- OAuth 2.0 flows

**User Groups:**
- customers
- restaurant-owners
- admins

### 6. Monitoring and Observability

**CloudWatch:**
- Centralized logging for all services
- Custom metrics and dashboards
- Alarms for critical metrics
- Log retention (7 days dev, 30 days prod)

**Alarms:**
- RDS CPU/Memory/Storage
- ElastiCache CPU/Memory
- DynamoDB throttling
- ECS task health

## 🚀 Getting Started

### Prerequisites

1. **Install Tools:**
   ```bash
   # Terraform
   brew install terraform  # macOS
   # Or download from https://www.terraform.io/downloads

   # AWS CLI
   brew install awscli  # macOS
   # Or download from https://aws.amazon.com/cli/

   # Docker
   # Download from https://www.docker.com/products/docker-desktop
   ```

2. **Configure AWS Credentials:**
   ```bash
   aws configure
   # Enter your AWS Access Key ID
   # Enter your AWS Secret Access Key
   # Default region: us-east-1
   # Default output: json
   ```

3. **Create ECR Repositories:**
   ```bash
   # Create ECR repositories for each service
   aws ecr create-repository --repository-name order-service --region us-east-1
   aws ecr create-repository --repository-name payment-service --region us-east-1
   aws ecr create-repository --repository-name restaurant-service --region us-east-1
   aws ecr create-repository --repository-name notification-service --region us-east-1

   # Note the repository URIs (format: ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/SERVICE_NAME)
   ```

### Deploy to Development Environment

1. **Navigate to dev environment:**
   ```bash
   cd infrastructure/terraform/environments/dev
   ```

2. **Copy and configure variables:**
   ```bash
   cp terraform.tfvars.example terraform.tfvars

   # Edit terraform.tfvars with your values:
   # - aws_region
   # - ecr_repository_url (from step 3 above)
   # - image_tag (use "latest" for first deployment)
   ```

3. **Initialize Terraform:**
   ```bash
   terraform init
   ```

4. **Review the plan:**
   ```bash
   terraform plan
   ```

5. **Apply the configuration:**
   ```bash
   terraform apply

   # Review the resources to be created
   # Type 'yes' to confirm
   ```

6. **Save outputs:**
   ```bash
   terraform output > outputs.txt
   ```

### Build and Push Docker Images

1. **Login to ECR:**
   ```bash
   aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com
   ```

2. **Build and push each service:**
   ```bash
   # From project root
   cd services/order-service
   docker build -t YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/order-service:latest .
   docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/order-service:latest

   cd ../payment-service
   docker build -t YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/payment-service:latest .
   docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/payment-service:latest

   cd ../restaurant-service
   docker build -t YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/restaurant-service:latest .
   docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/restaurant-service:latest

   cd ../notification-service
   docker build -t YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/notification-service:latest .
   docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/notification-service:latest
   ```

3. **Trigger ECS deployment:**
   ```bash
   # Services will automatically pull new images
   # Or force new deployment:
   aws ecs update-service --cluster cookedspecially-dev-cluster --service cookedspecially-dev-order-service --force-new-deployment
   ```

## 🐳 Local Development with Docker Compose

**Start all services locally:**
```bash
# From project root
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

**Access services:**
- Order Service: http://localhost:8081
- Payment Service: http://localhost:8082
- Restaurant Service: http://localhost:8083
- Notification Service: http://localhost:8084
- MySQL: localhost:3306
- Redis: localhost:6379
- LocalStack (AWS): http://localhost:4566

**Swagger UI:**
- http://localhost:8081/swagger-ui.html
- http://localhost:8082/swagger-ui.html
- http://localhost:8083/swagger-ui.html
- http://localhost:8084/swagger-ui.html

## 🔐 Secrets Management

### Create Secrets in AWS Secrets Manager

```bash
# Database password
aws secretsmanager create-secret \
  --name cookedspecially/dev/db-password \
  --secret-string "your-secure-password" \
  --region us-east-1

# JWT secret
aws secretsmanager create-secret \
  --name cookedspecially/dev/jwt-secret \
  --secret-string "your-jwt-secret-key" \
  --region us-east-1

# Stripe API key
aws secretsmanager create-secret \
  --name cookedspecially/dev/stripe-api-key \
  --secret-string "sk_test_your_stripe_key" \
  --region us-east-1

# Firebase credentials (JSON)
aws secretsmanager create-secret \
  --name cookedspecially/dev/firebase-credentials \
  --secret-string file://firebase-credentials.json \
  --region us-east-1
```

## 📊 Monitoring and Troubleshooting

### View Logs

```bash
# CloudWatch Logs
aws logs tail /ecs/cookedspecially-dev/order-service --follow

# ECS service status
aws ecs describe-services \
  --cluster cookedspecially-dev-cluster \
  --services cookedspecially-dev-order-service

# Task details
aws ecs list-tasks --cluster cookedspecially-dev-cluster
aws ecs describe-tasks --cluster cookedspecially-dev-cluster --tasks TASK_ID
```

### Common Issues

**Services not starting:**
```bash
# Check ECS events
aws ecs describe-services --cluster cookedspecially-dev-cluster --services cookedspecially-dev-order-service | jq '.services[0].events'

# Check task logs
aws logs tail /ecs/cookedspecially-dev/order-service --since 10m
```

**Database connection issues:**
```bash
# Verify security groups allow traffic from ECS to RDS
# Check RDS endpoint in Terraform outputs
terraform output database_endpoint
```

## 💰 Cost Estimation

### Development Environment (Monthly)
- ECS Fargate (4 services, 1 task each): ~$50
- RDS MySQL (db.t3.small): ~$30
- ElastiCache (cache.t3.micro): ~$15
- ALB: ~$25
- Data transfer: ~$10
- DynamoDB (on-demand): ~$5
- S3 + CloudFront: ~$5
- **Total: ~$140/month**

### Production Environment (Monthly)
- ECS Fargate (4 services, 3 tasks each): ~$450
- RDS MySQL Multi-AZ (db.r6g.large): ~$280
- ElastiCache (2 nodes, cache.r6g.large): ~$260
- ALB: ~$25
- Data transfer: ~$100
- DynamoDB (on-demand): ~$40
- S3 + CloudFront: ~$85
- **Total: ~$1,240/month**

**Cost Optimization:**
- Use Reserved Instances/Savings Plans (30-40% savings)
- Implement auto-scaling to scale down during off-peak
- Use Spot instances for non-critical workloads

## 🔄 CI/CD Pipeline

GitHub Actions workflow automatically:
1. Builds and tests all services
2. Runs security scans
3. Builds Docker images
4. Pushes to ECR
5. Deploys to development (on `develop` branch)
6. Deploys to production (on `main` branch)

**Required GitHub Secrets:**
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `ECR_REGISTRY` (e.g., `123456789012.dkr.ecr.us-east-1.amazonaws.com`)

## 🏗️ Infrastructure Updates

### Modify Infrastructure

```bash
# Make changes to Terraform files
# Plan changes
terraform plan -out=tfplan

# Apply changes
terraform apply tfplan
```

### Update Service Configuration

```bash
# Update task definition
# ECS will automatically deploy new version
terraform apply
```

### Destroy Infrastructure

```bash
# ⚠️ WARNING: This will delete all resources
terraform destroy

# For specific resources
terraform destroy -target=module.rds
```

## 📚 Additional Resources

- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)
- [ECS Best Practices](https://docs.aws.amazon.com/AmazonECS/latest/bestpracticesguide/)
- [Terraform AWS Provider Docs](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [Spring Boot on AWS](https://aws.amazon.com/blogs/opensource/spring-boot-application-on-aws/)

## 🆘 Support

For issues or questions:
1. Check CloudWatch Logs
2. Review Terraform state: `terraform show`
3. Check AWS Console for resource status
4. Review `AWS_MODERNIZATION_PROPOSAL.md` for architecture details

## 📝 License

Copyright © 2025 CookedSpecially. All rights reserved.
