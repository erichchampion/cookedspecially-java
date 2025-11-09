# S3 and CloudFront Module - Storage and CDN

locals {
  common_tags = {
    Module      = "S3-CloudFront"
    Environment = var.environment
  }
}

# S3 Bucket - Restaurant Images
resource "aws_s3_bucket" "restaurant_images" {
  bucket = "${var.project_name}-${var.environment}-restaurant-images"

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.environment}-restaurant-images"
      Purpose = "Restaurant and menu images"
    }
  )
}

# S3 Bucket Public Access Block
resource "aws_s3_bucket_public_access_block" "restaurant_images" {
  bucket = aws_s3_bucket.restaurant_images.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# S3 Bucket Versioning
resource "aws_s3_bucket_versioning" "restaurant_images" {
  bucket = aws_s3_bucket.restaurant_images.id

  versioning_configuration {
    status = "Enabled"
  }
}

# S3 Bucket Encryption
resource "aws_s3_bucket_server_side_encryption_configuration" "restaurant_images" {
  bucket = aws_s3_bucket.restaurant_images.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# S3 Bucket Lifecycle Rules
resource "aws_s3_bucket_lifecycle_configuration" "restaurant_images" {
  bucket = aws_s3_bucket.restaurant_images.id

  rule {
    id     = "transition-to-ia"
    status = "Enabled"

    transition {
      days          = 90
      storage_class = "STANDARD_IA"
    }

    transition {
      days          = 180
      storage_class = "GLACIER"
    }
  }

  rule {
    id     = "cleanup-old-versions"
    status = "Enabled"

    noncurrent_version_expiration {
      noncurrent_days = 30
    }
  }
}

# S3 Bucket - Reports
resource "aws_s3_bucket" "reports" {
  bucket = "${var.project_name}-${var.environment}-reports"

  tags = merge(
    local.common_tags,
    {
      Name    = "${var.project_name}-${var.environment}-reports"
      Purpose = "Generated reports (PDF, Excel)"
    }
  )
}

resource "aws_s3_bucket_public_access_block" "reports" {
  bucket = aws_s3_bucket.reports.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "reports" {
  bucket = aws_s3_bucket.reports.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# S3 Bucket Lifecycle for Reports
resource "aws_s3_bucket_lifecycle_configuration" "reports" {
  bucket = aws_s3_bucket.reports.id

  rule {
    id     = "delete-old-reports"
    status = "Enabled"

    expiration {
      days = var.reports_retention_days
    }
  }
}

# CloudFront Origin Access Identity
resource "aws_cloudfront_origin_access_identity" "main" {
  comment = "OAI for ${var.project_name} ${var.environment}"
}

# S3 Bucket Policy for CloudFront
resource "aws_s3_bucket_policy" "restaurant_images" {
  bucket = aws_s3_bucket.restaurant_images.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "AllowCloudFrontAccess"
        Effect = "Allow"
        Principal = {
          AWS = aws_cloudfront_origin_access_identity.main.iam_arn
        }
        Action   = "s3:GetObject"
        Resource = "${aws_s3_bucket.restaurant_images.arn}/*"
      }
    ]
  })
}

# CloudFront Distribution
resource "aws_cloudfront_distribution" "main" {
  enabled             = true
  is_ipv6_enabled     = true
  comment             = "${var.project_name} ${var.environment} CDN"
  default_root_object = "index.html"
  price_class         = var.cloudfront_price_class

  # S3 Origin
  origin {
    domain_name = aws_s3_bucket.restaurant_images.bucket_regional_domain_name
    origin_id   = "S3-${aws_s3_bucket.restaurant_images.id}"

    s3_origin_config {
      origin_access_identity = aws_cloudfront_origin_access_identity.main.cloudfront_access_identity_path
    }
  }

  # Default cache behavior
  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD", "OPTIONS"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "S3-${aws_s3_bucket.restaurant_images.id}"

    forwarded_values {
      query_string = false

      cookies {
        forward = "none"
      }
    }

    viewer_protocol_policy = "redirect-to-https"
    min_ttl                = 0
    default_ttl            = 3600
    max_ttl                = 86400
    compress               = true
  }

  # Geo restriction
  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  # SSL/TLS certificate
  viewer_certificate {
    cloudfront_default_certificate = true
    minimum_protocol_version       = "TLSv1.2_2021"
  }

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.environment}-cdn"
    }
  )
}

# CORS Configuration for Restaurant Images Bucket
resource "aws_s3_bucket_cors_configuration" "restaurant_images" {
  bucket = aws_s3_bucket.restaurant_images.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET", "HEAD"]
    allowed_origins = ["*"]  # Restrict to your domain in production
    expose_headers  = ["ETag"]
    max_age_seconds = 3000
  }
}
