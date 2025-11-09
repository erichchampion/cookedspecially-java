# ECS Fargate Module Outputs

output "cluster_id" {
  description = "ECS Cluster ID"
  value       = aws_ecs_cluster.main.id
}

output "cluster_name" {
  description = "ECS Cluster name"
  value       = aws_ecs_cluster.main.name
}

output "alb_dns_name" {
  description = "ALB DNS name"
  value       = aws_lb.main.dns_name
}

output "alb_arn" {
  description = "ALB ARN"
  value       = aws_lb.main.arn
}

output "alb_zone_id" {
  description = "ALB Zone ID"
  value       = aws_lb.main.zone_id
}

output "ecs_security_group_id" {
  description = "ECS tasks security group ID"
  value       = aws_security_group.ecs_tasks.id
}

output "alb_security_group_id" {
  description = "ALB security group ID"
  value       = aws_security_group.alb.id
}

output "task_execution_role_arn" {
  description = "ECS task execution role ARN"
  value       = aws_iam_role.ecs_task_execution_role.arn
}

output "task_role_arn" {
  description = "ECS task role ARN"
  value       = aws_iam_role.ecs_task_role.arn
}

# Service outputs
output "order_service_name" {
  description = "Order service name"
  value       = aws_ecs_service.order_service.name
}

output "payment_service_name" {
  description = "Payment service name"
  value       = aws_ecs_service.payment_service.name
}

output "restaurant_service_name" {
  description = "Restaurant service name"
  value       = aws_ecs_service.restaurant_service.name
}

output "notification_service_name" {
  description = "Notification service name"
  value       = aws_ecs_service.notification_service.name
}
