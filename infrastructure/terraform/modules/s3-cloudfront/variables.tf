# S3 and CloudFront Module Variables

variable "project_name" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "reports_retention_days" {
  description = "Number of days to retain reports"
  type        = number
  default     = 90
}

variable "cloudfront_price_class" {
  description = "CloudFront price class"
  type        = string
  default     = "PriceClass_100"  # Use only North America and Europe
}
