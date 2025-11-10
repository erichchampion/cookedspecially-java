# ECS Fargate Module Variables

variable "project_name" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "aws_region" {
  description = "AWS region"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID"
  type        = string
}

variable "public_subnet_ids" {
  description = "Public subnet IDs for ALB"
  type        = list(string)
}

variable "private_app_subnet_ids" {
  description = "Private application subnet IDs for ECS tasks"
  type        = list(string)
}

variable "ecr_repository_url" {
  description = "ECR repository URL"
  type        = string
}

variable "image_tag" {
  description = "Docker image tag"
  type        = string
  default     = "latest"
}

variable "service_cpu" {
  description = "CPU units for ECS task"
  type        = string
  default     = "512"
}

variable "service_memory" {
  description = "Memory (MB) for ECS task"
  type        = string
  default     = "1024"
}

variable "desired_count" {
  description = "Desired number of ECS tasks"
  type        = number
  default     = 2
}

variable "min_capacity" {
  description = "Minimum number of tasks for auto-scaling"
  type        = number
  default     = 2
}

variable "max_capacity" {
  description = "Maximum number of tasks for auto-scaling"
  type        = number
  default     = 10
}

variable "log_retention_days" {
  description = "CloudWatch logs retention in days"
  type        = number
  default     = 7
}

variable "db_host" {
  description = "Database host"
  type        = string
}

variable "redis_host" {
  description = "Redis host"
  type        = string
}

variable "kafka_bootstrap_servers" {
  description = "Kafka bootstrap servers"
  type        = string
  default     = ""
}

variable "secrets_arn_prefix" {
  description = "ARN prefix for AWS Secrets Manager secrets"
  type        = string
}
