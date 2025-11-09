# Development Environment Variables

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "ecr_repository_url" {
  description = "ECR repository URL (format: ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/cookedspecially)"
  type        = string
}

variable "image_tag" {
  description = "Docker image tag to deploy"
  type        = string
  default     = "latest"
}

variable "kafka_bootstrap_servers" {
  description = "Kafka bootstrap servers (optional for dev)"
  type        = string
  default     = ""
}
