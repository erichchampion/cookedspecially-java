# Development Environment Outputs

output "alb_dns_name" {
  description = "Application Load Balancer DNS name"
  value       = module.ecs.alb_dns_name
}

output "database_endpoint" {
  description = "RDS database endpoint"
  value       = module.rds.db_endpoint
  sensitive   = true
}

output "redis_endpoint" {
  description = "ElastiCache Redis endpoint"
  value       = module.elasticache.redis_endpoint
}

output "cloudfront_domain" {
  description = "CloudFront distribution domain"
  value       = module.s3_cloudfront.cloudfront_domain_name
}

output "cognito_user_pool_id" {
  description = "Cognito User Pool ID"
  value       = module.cognito.user_pool_id
}

output "cognito_web_client_id" {
  description = "Cognito Web Client ID"
  value       = module.cognito.web_client_id
}

output "s3_restaurant_images_bucket" {
  description = "S3 bucket for restaurant images"
  value       = module.s3_cloudfront.restaurant_images_bucket_name
}

output "ecs_cluster_name" {
  description = "ECS Cluster name"
  value       = module.ecs.cluster_name
}
