# Auto Scaling Configuration for ECS Services

# Order Service Auto Scaling Target
resource "aws_appautoscaling_target" "order_service" {
  max_capacity       = var.max_capacity
  min_capacity       = var.min_capacity
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.order_service.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

# Order Service Auto Scaling Policy - CPU
resource "aws_appautoscaling_policy" "order_service_cpu" {
  name               = "${var.project_name}-${var.environment}-order-service-cpu"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.order_service.resource_id
  scalable_dimension = aws_appautoscaling_target.order_service.scalable_dimension
  service_namespace  = aws_appautoscaling_target.order_service.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }
    target_value       = 70.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60
  }
}

# Order Service Auto Scaling Policy - Memory
resource "aws_appautoscaling_policy" "order_service_memory" {
  name               = "${var.project_name}-${var.environment}-order-service-memory"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.order_service.resource_id
  scalable_dimension = aws_appautoscaling_target.order_service.scalable_dimension
  service_namespace  = aws_appautoscaling_target.order_service.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageMemoryUtilization"
    }
    target_value       = 80.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60
  }
}

# Payment Service Auto Scaling Target
resource "aws_appautoscaling_target" "payment_service" {
  max_capacity       = var.max_capacity
  min_capacity       = var.min_capacity
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.payment_service.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

# Payment Service Auto Scaling Policy - CPU
resource "aws_appautoscaling_policy" "payment_service_cpu" {
  name               = "${var.project_name}-${var.environment}-payment-service-cpu"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.payment_service.resource_id
  scalable_dimension = aws_appautoscaling_target.payment_service.scalable_dimension
  service_namespace  = aws_appautoscaling_target.payment_service.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }
    target_value       = 70.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60
  }
}

# Payment Service Auto Scaling Policy - Memory
resource "aws_appautoscaling_policy" "payment_service_memory" {
  name               = "${var.project_name}-${var.environment}-payment-service-memory"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.payment_service.resource_id
  scalable_dimension = aws_appautoscaling_target.payment_service.scalable_dimension
  service_namespace  = aws_appautoscaling_target.payment_service.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageMemoryUtilization"
    }
    target_value       = 80.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60
  }
}

# Restaurant Service Auto Scaling Target
resource "aws_appautoscaling_target" "restaurant_service" {
  max_capacity       = var.max_capacity
  min_capacity       = var.min_capacity
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.restaurant_service.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

# Restaurant Service Auto Scaling Policy - CPU
resource "aws_appautoscaling_policy" "restaurant_service_cpu" {
  name               = "${var.project_name}-${var.environment}-restaurant-service-cpu"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.restaurant_service.resource_id
  scalable_dimension = aws_appautoscaling_target.restaurant_service.scalable_dimension
  service_namespace  = aws_appautoscaling_target.restaurant_service.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }
    target_value       = 70.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60
  }
}

# Restaurant Service Auto Scaling Policy - Memory
resource "aws_appautoscaling_policy" "restaurant_service_memory" {
  name               = "${var.project_name}-${var.environment}-restaurant-service-memory"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.restaurant_service.resource_id
  scalable_dimension = aws_appautoscaling_target.restaurant_service.scalable_dimension
  service_namespace  = aws_appautoscaling_target.restaurant_service.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageMemoryUtilization"
    }
    target_value       = 80.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60
  }
}

# Notification Service Auto Scaling Target
resource "aws_appautoscaling_target" "notification_service" {
  max_capacity       = var.max_capacity
  min_capacity       = var.min_capacity
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.notification_service.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

# Notification Service Auto Scaling Policy - CPU
resource "aws_appautoscaling_policy" "notification_service_cpu" {
  name               = "${var.project_name}-${var.environment}-notification-service-cpu"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.notification_service.resource_id
  scalable_dimension = aws_appautoscaling_target.notification_service.scalable_dimension
  service_namespace  = aws_appautoscaling_target.notification_service.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }
    target_value       = 70.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60
  }
}

# Notification Service Auto Scaling Policy - Memory
resource "aws_appautoscaling_policy" "notification_service_memory" {
  name               = "${var.project_name}-${var.environment}-notification-service-memory"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.notification_service.resource_id
  scalable_dimension = aws_appautoscaling_target.notification_service.scalable_dimension
  service_namespace  = aws_appautoscaling_target.notification_service.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageMemoryUtilization"
    }
    target_value       = 80.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60
  }
}
