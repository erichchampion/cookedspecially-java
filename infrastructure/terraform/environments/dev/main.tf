# Development Environment Configuration

terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # Uncomment for remote state
  # backend "s3" {
  #   bucket         = "cookedspecially-terraform-state-dev"
  #   key            = "dev/terraform.tfstate"
  #   region         = "us-east-1"
  #   encrypt        = true
  #   dynamodb_table = "terraform-state-lock-dev"
  # }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "CookedSpecially"
      Environment = "dev"
      ManagedBy   = "Terraform"
      Owner       = "DevTeam"
    }
  }
}

# Local variables
locals {
  project_name = "cookedspecially"
  environment  = "dev"
  aws_region   = var.aws_region

  availability_zones = [
    "${var.aws_region}a",
    "${var.aws_region}b"
  ]
}

# VPC Module
module "vpc" {
  source = "../../modules/vpc"

  project_name       = local.project_name
  environment        = local.environment
  aws_region         = local.aws_region
  vpc_cidr           = "10.0.0.0/16"
  availability_zones = local.availability_zones
  enable_nat_gateway = true
}

# RDS Module
module "rds" {
  source = "../../modules/rds"

  project_name           = local.project_name
  environment            = local.environment
  vpc_id                 = module.vpc.vpc_id
  private_db_subnet_ids  = module.vpc.private_db_subnet_ids
  app_security_group_ids = [module.ecs.ecs_security_group_id]

  # Dev-specific settings
  instance_class              = "db.t3.small"
  allocated_storage           = 20
  max_allocated_storage       = 100
  multi_az                    = false  # Single AZ for dev
  backup_retention_period     = 3
  performance_insights_enabled = false
  create_read_replica         = false

  depends_on = [module.vpc]
}

# ElastiCache Module
module "elasticache" {
  source = "../../modules/elasticache"

  project_name           = local.project_name
  environment            = local.environment
  vpc_id                 = module.vpc.vpc_id
  private_app_subnet_ids = module.vpc.private_app_subnet_ids
  app_security_group_ids = [module.ecs.ecs_security_group_id]

  # Dev-specific settings
  node_type                = "cache.t3.micro"
  num_cache_nodes          = 1  # No HA for dev
  snapshot_retention_limit = 1

  depends_on = [module.vpc]
}

# DynamoDB Module
module "dynamodb" {
  source = "../../modules/dynamodb"

  project_name = local.project_name
  environment  = local.environment

  # Dev-specific settings
  billing_mode                   = "PAY_PER_REQUEST"  # On-demand for dev
  enable_point_in_time_recovery  = false  # Disable for dev
}

# S3 and CloudFront Module
module "s3_cloudfront" {
  source = "../../modules/s3-cloudfront"

  project_name            = local.project_name
  environment             = local.environment
  reports_retention_days  = 30  # Shorter retention for dev
  cloudfront_price_class  = "PriceClass_100"
}

# Cognito Module
module "cognito" {
  source = "../../modules/cognito"

  project_name = local.project_name
  environment  = local.environment

  enable_mfa       = false  # Disable MFA for dev
  callback_urls    = ["http://localhost:3000/callback"]
  logout_urls      = ["http://localhost:3000/"]
  mobile_callback_urls = ["cookedspecially://callback"]
  mobile_logout_urls   = ["cookedspecially://logout"]
}

# ECS Fargate Module
module "ecs" {
  source = "../../modules/ecs-fargate"

  project_name           = local.project_name
  environment            = local.environment
  aws_region             = local.aws_region
  vpc_id                 = module.vpc.vpc_id
  public_subnet_ids      = module.vpc.public_subnet_ids
  private_app_subnet_ids = module.vpc.private_app_subnet_ids

  # ECR repository URL (update with your account ID)
  ecr_repository_url = var.ecr_repository_url
  image_tag          = var.image_tag

  # Dev-specific settings
  service_cpu    = "512"
  service_memory = "1024"
  desired_count  = 1  # Single instance for dev
  min_capacity   = 1
  max_capacity   = 2

  # Database and cache endpoints
  db_host                 = module.rds.db_address
  redis_host              = module.elasticache.redis_endpoint
  kafka_bootstrap_servers = var.kafka_bootstrap_servers

  # Secrets ARN prefix
  secrets_arn_prefix = "arn:aws:secretsmanager:${local.aws_region}:${data.aws_caller_identity.current.account_id}:secret:${local.project_name}/${local.environment}"

  log_retention_days = 7

  depends_on = [module.vpc, module.rds, module.elasticache]
}

# Data source for current AWS account
data "aws_caller_identity" "current" {}
