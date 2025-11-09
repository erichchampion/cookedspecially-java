# S3 and CloudFront Module Outputs

output "restaurant_images_bucket_name" {
  description = "Restaurant images S3 bucket name"
  value       = aws_s3_bucket.restaurant_images.id
}

output "restaurant_images_bucket_arn" {
  description = "Restaurant images S3 bucket ARN"
  value       = aws_s3_bucket.restaurant_images.arn
}

output "reports_bucket_name" {
  description = "Reports S3 bucket name"
  value       = aws_s3_bucket.reports.id
}

output "reports_bucket_arn" {
  description = "Reports S3 bucket ARN"
  value       = aws_s3_bucket.reports.arn
}

output "cloudfront_distribution_id" {
  description = "CloudFront distribution ID"
  value       = aws_cloudfront_distribution.main.id
}

output "cloudfront_domain_name" {
  description = "CloudFront distribution domain name"
  value       = aws_cloudfront_distribution.main.domain_name
}

output "cloudfront_distribution_arn" {
  description = "CloudFront distribution ARN"
  value       = aws_cloudfront_distribution.main.arn
}
