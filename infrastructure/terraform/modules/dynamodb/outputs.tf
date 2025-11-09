# DynamoDB Module Outputs

output "order_tracking_table_name" {
  description = "Order tracking table name"
  value       = aws_dynamodb_table.order_tracking.name
}

output "order_tracking_table_arn" {
  description = "Order tracking table ARN"
  value       = aws_dynamodb_table.order_tracking.arn
}

output "payment_transactions_table_name" {
  description = "Payment transactions table name"
  value       = aws_dynamodb_table.payment_transactions.name
}

output "payment_transactions_table_arn" {
  description = "Payment transactions table ARN"
  value       = aws_dynamodb_table.payment_transactions.arn
}

output "sessions_table_name" {
  description = "Sessions table name"
  value       = aws_dynamodb_table.sessions.name
}

output "sessions_table_arn" {
  description = "Sessions table ARN"
  value       = aws_dynamodb_table.sessions.arn
}
