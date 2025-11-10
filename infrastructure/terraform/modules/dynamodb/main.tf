# DynamoDB Module - NoSQL Tables for Real-time Data

locals {
  common_tags = {
    Module      = "DynamoDB"
    Environment = var.environment
  }
}

# Order Tracking Table
resource "aws_dynamodb_table" "order_tracking" {
  name           = "${var.project_name}-${var.environment}-order-tracking"
  billing_mode   = var.billing_mode
  read_capacity  = var.billing_mode == "PROVISIONED" ? var.read_capacity : null
  write_capacity = var.billing_mode == "PROVISIONED" ? var.write_capacity : null
  hash_key       = "orderId"
  range_key      = "timestamp"

  attribute {
    name = "orderId"
    type = "S"
  }

  attribute {
    name = "timestamp"
    type = "N"
  }

  attribute {
    name = "customerId"
    type = "S"
  }

  attribute {
    name = "status"
    type = "S"
  }

  # Global Secondary Index for customer queries
  global_secondary_index {
    name            = "CustomerIndex"
    hash_key        = "customerId"
    range_key       = "timestamp"
    projection_type = "ALL"
    read_capacity   = var.billing_mode == "PROVISIONED" ? var.read_capacity : null
    write_capacity  = var.billing_mode == "PROVISIONED" ? var.write_capacity : null
  }

  # Global Secondary Index for status queries
  global_secondary_index {
    name            = "StatusIndex"
    hash_key        = "status"
    range_key       = "timestamp"
    projection_type = "ALL"
    read_capacity   = var.billing_mode == "PROVISIONED" ? var.read_capacity : null
    write_capacity  = var.billing_mode == "PROVISIONED" ? var.write_capacity : null
  }

  # TTL for automatic cleanup of old records
  ttl {
    attribute_name = "expiryTime"
    enabled        = true
  }

  # Point-in-time recovery
  point_in_time_recovery {
    enabled = var.enable_point_in_time_recovery
  }

  # Server-side encryption
  server_side_encryption {
    enabled = true
  }

  # Streams for change data capture
  stream_enabled   = true
  stream_view_type = "NEW_AND_OLD_IMAGES"

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.environment}-order-tracking"
    }
  )
}

# Payment Transactions Table
resource "aws_dynamodb_table" "payment_transactions" {
  name           = "${var.project_name}-${var.environment}-payment-transactions"
  billing_mode   = var.billing_mode
  read_capacity  = var.billing_mode == "PROVISIONED" ? var.read_capacity : null
  write_capacity = var.billing_mode == "PROVISIONED" ? var.write_capacity : null
  hash_key       = "transactionId"
  range_key      = "timestamp"

  attribute {
    name = "transactionId"
    type = "S"
  }

  attribute {
    name = "timestamp"
    type = "N"
  }

  attribute {
    name = "orderId"
    type = "S"
  }

  attribute {
    name = "customerId"
    type = "S"
  }

  # Global Secondary Index for order queries
  global_secondary_index {
    name            = "OrderIndex"
    hash_key        = "orderId"
    range_key       = "timestamp"
    projection_type = "ALL"
    read_capacity   = var.billing_mode == "PROVISIONED" ? var.read_capacity : null
    write_capacity  = var.billing_mode == "PROVISIONED" ? var.write_capacity : null
  }

  # Global Secondary Index for customer queries
  global_secondary_index {
    name            = "CustomerIndex"
    hash_key        = "customerId"
    range_key       = "timestamp"
    projection_type = "ALL"
    read_capacity   = var.billing_mode == "PROVISIONED" ? var.read_capacity : null
    write_capacity  = var.billing_mode == "PROVISIONED" ? var.write_capacity : null
  }

  # Point-in-time recovery
  point_in_time_recovery {
    enabled = var.enable_point_in_time_recovery
  }

  # Server-side encryption
  server_side_encryption {
    enabled = true
  }

  # Streams
  stream_enabled   = true
  stream_view_type = "NEW_AND_OLD_IMAGES"

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.environment}-payment-transactions"
    }
  )
}

# Session Table (for distributed sessions)
resource "aws_dynamodb_table" "sessions" {
  name           = "${var.project_name}-${var.environment}-sessions"
  billing_mode   = var.billing_mode
  read_capacity  = var.billing_mode == "PROVISIONED" ? var.read_capacity * 2 : null
  write_capacity = var.billing_mode == "PROVISIONED" ? var.write_capacity * 2 : null
  hash_key       = "sessionId"

  attribute {
    name = "sessionId"
    type = "S"
  }

  # TTL for automatic session cleanup
  ttl {
    attribute_name = "expiryTime"
    enabled        = true
  }

  # Point-in-time recovery
  point_in_time_recovery {
    enabled = var.enable_point_in_time_recovery
  }

  # Server-side encryption
  server_side_encryption {
    enabled = true
  }

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.environment}-sessions"
    }
  )
}

# CloudWatch Alarms for DynamoDB
resource "aws_cloudwatch_metric_alarm" "order_tracking_read_throttle" {
  alarm_name          = "${var.project_name}-${var.environment}-order-tracking-read-throttle"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "UserErrors"
  namespace           = "AWS/DynamoDB"
  period              = 300
  statistic           = "Sum"
  threshold           = 10
  alarm_description   = "DynamoDB order tracking read throttling"
  alarm_actions       = var.alarm_actions

  dimensions = {
    TableName = aws_dynamodb_table.order_tracking.name
  }

  tags = local.common_tags
}

resource "aws_cloudwatch_metric_alarm" "payment_transactions_write_throttle" {
  alarm_name          = "${var.project_name}-${var.environment}-payment-transactions-write-throttle"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "UserErrors"
  namespace           = "AWS/DynamoDB"
  period              = 300
  statistic           = "Sum"
  threshold           = 10
  alarm_description   = "DynamoDB payment transactions write throttling"
  alarm_actions       = var.alarm_actions

  dimensions = {
    TableName = aws_dynamodb_table.payment_transactions.name
  }

  tags = local.common_tags
}
