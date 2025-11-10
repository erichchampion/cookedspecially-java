# ECS Fargate Module - Container Orchestration
# Creates ECS cluster, services, task definitions, and ALB

locals {
  common_tags = {
    Module      = "ECS-Fargate"
    Environment = var.environment
  }
}

# ECS Cluster
resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-${var.environment}-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.environment}-ecs-cluster"
    }
  )
}

# ECS Cluster Capacity Providers
resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name = aws_ecs_cluster.main.name

  capacity_providers = ["FARGATE", "FARGATE_SPOT"]

  default_capacity_provider_strategy {
    base              = 1
    weight            = 100
    capacity_provider = "FARGATE"
  }
}

# Application Load Balancer
resource "aws_lb" "main" {
  name               = "${var.project_name}-${var.environment}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = var.public_subnet_ids

  enable_deletion_protection = var.environment == "prod" ? true : false
  enable_http2              = true
  enable_cross_zone_load_balancing = true

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.environment}-alb"
    }
  )
}

# ALB Security Group
resource "aws_security_group" "alb" {
  name_prefix = "${var.project_name}-${var.environment}-alb-"
  description = "Security group for Application Load Balancer"
  vpc_id      = var.vpc_id

  ingress {
    description = "HTTP from internet"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS from internet"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.environment}-alb-sg"
    }
  )

  lifecycle {
    create_before_destroy = true
  }
}

# ALB Target Group - Order Service
resource "aws_lb_target_group" "order_service" {
  name        = "${var.project_name}-${var.environment}-order-tg"
  port        = 8081
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/actuator/health"
    matcher             = "200"
  }

  deregistration_delay = 30

  tags = merge(
    local.common_tags,
    {
      Name    = "${var.project_name}-${var.environment}-order-tg"
      Service = "order-service"
    }
  )
}

# ALB Target Group - Payment Service
resource "aws_lb_target_group" "payment_service" {
  name        = "${var.project_name}-${var.environment}-payment-tg"
  port        = 8082
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/actuator/health"
    matcher             = "200"
  }

  deregistration_delay = 30

  tags = merge(
    local.common_tags,
    {
      Name    = "${var.project_name}-${var.environment}-payment-tg"
      Service = "payment-service"
    }
  )
}

# ALB Target Group - Restaurant Service
resource "aws_lb_target_group" "restaurant_service" {
  name        = "${var.project_name}-${var.environment}-restaurant-tg"
  port        = 8083
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/actuator/health"
    matcher             = "200"
  }

  deregistration_delay = 30

  tags = merge(
    local.common_tags,
    {
      Name    = "${var.project_name}-${var.environment}-restaurant-tg"
      Service = "restaurant-service"
    }
  )
}

# ALB Target Group - Notification Service
resource "aws_lb_target_group" "notification_service" {
  name        = "${var.project_name}-${var.environment}-notification-tg"
  port        = 8084
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/actuator/health"
    matcher             = "200"
  }

  deregistration_delay = 30

  tags = merge(
    local.common_tags,
    {
      Name    = "${var.project_name}-${var.environment}-notification-tg"
      Service = "notification-service"
    }
  )
}

# ALB Listener - HTTP (redirect to HTTPS in prod)
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "Not Found"
      status_code  = "404"
    }
  }
}

# ALB Listener Rules - Order Service
resource "aws_lb_listener_rule" "order_service" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.order_service.arn
  }

  condition {
    path_pattern {
      values = ["/api/v1/orders*"]
    }
  }
}

# ALB Listener Rules - Payment Service
resource "aws_lb_listener_rule" "payment_service" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 101

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.payment_service.arn
  }

  condition {
    path_pattern {
      values = ["/api/v1/payments*"]
    }
  }
}

# ALB Listener Rules - Restaurant Service
resource "aws_lb_listener_rule" "restaurant_service" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 102

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.restaurant_service.arn
  }

  condition {
    path_pattern {
      values = ["/api/v1/restaurants*", "/api/v1/menu-items*"]
    }
  }
}

# ALB Listener Rules - Notification Service
resource "aws_lb_listener_rule" "notification_service" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 103

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.notification_service.arn
  }

  condition {
    path_pattern {
      values = ["/api/v1/notifications*"]
    }
  }
}

# ECS Task Execution Role
resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${var.project_name}-${var.environment}-ecs-task-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = local.common_tags
}

# Attach AWS managed policy for ECS task execution
resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Additional permissions for Secrets Manager and SSM Parameter Store
resource "aws_iam_role_policy" "ecs_task_execution_additional" {
  name = "${var.project_name}-${var.environment}-ecs-task-execution-additional"
  role = aws_iam_role.ecs_task_execution_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "secretsmanager:GetSecretValue",
          "ssm:GetParameters",
          "kms:Decrypt"
        ]
        Resource = "*"
      }
    ]
  })
}

# ECS Task Role (permissions for application to access AWS services)
resource "aws_iam_role" "ecs_task_role" {
  name = "${var.project_name}-${var.environment}-ecs-task-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = local.common_tags
}

# ECS Task Role Policy - AWS Services Access
resource "aws_iam_role_policy" "ecs_task_role_policy" {
  name = "${var.project_name}-${var.environment}-ecs-task-policy"
  role = aws_iam_role.ecs_task_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:DeleteObject",
          "s3:ListBucket"
        ]
        Resource = [
          "arn:aws:s3:::${var.project_name}-${var.environment}-*",
          "arn:aws:s3:::${var.project_name}-${var.environment}-*/*"
        ]
      },
      {
        Effect = "Allow"
        Action = [
          "dynamodb:GetItem",
          "dynamodb:PutItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem",
          "dynamodb:Query",
          "dynamodb:Scan"
        ]
        Resource = "arn:aws:dynamodb:${var.aws_region}:*:table/${var.project_name}-${var.environment}-*"
      },
      {
        Effect = "Allow"
        Action = [
          "sns:Publish"
        ]
        Resource = "arn:aws:sns:${var.aws_region}:*:${var.project_name}-${var.environment}-*"
      },
      {
        Effect = "Allow"
        Action = [
          "sqs:SendMessage",
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ]
        Resource = "arn:aws:sqs:${var.aws_region}:*:${var.project_name}-${var.environment}-*"
      },
      {
        Effect = "Allow"
        Action = [
          "ses:SendEmail",
          "ses:SendRawEmail"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "cloudwatch:PutMetricData"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "secretsmanager:GetSecretValue"
        ]
        Resource = "arn:aws:secretsmanager:${var.aws_region}:*:secret:${var.project_name}/${var.environment}/*"
      }
    ]
  })
}

# Security Group for ECS Tasks
resource "aws_security_group" "ecs_tasks" {
  name_prefix = "${var.project_name}-${var.environment}-ecs-tasks-"
  description = "Security group for ECS tasks"
  vpc_id      = var.vpc_id

  ingress {
    description     = "Allow traffic from ALB"
    from_port       = 0
    to_port         = 65535
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  egress {
    description = "Allow all outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.environment}-ecs-tasks-sg"
    }
  )

  lifecycle {
    create_before_destroy = true
  }
}

# CloudWatch Log Groups
resource "aws_cloudwatch_log_group" "order_service" {
  name              = "/ecs/${var.project_name}-${var.environment}/order-service"
  retention_in_days = var.log_retention_days

  tags = merge(
    local.common_tags,
    {
      Service = "order-service"
    }
  )
}

resource "aws_cloudwatch_log_group" "payment_service" {
  name              = "/ecs/${var.project_name}-${var.environment}/payment-service"
  retention_in_days = var.log_retention_days

  tags = merge(
    local.common_tags,
    {
      Service = "payment-service"
    }
  )
}

resource "aws_cloudwatch_log_group" "restaurant_service" {
  name              = "/ecs/${var.project_name}-${var.environment}/restaurant-service"
  retention_in_days = var.log_retention_days

  tags = merge(
    local.common_tags,
    {
      Service = "restaurant-service"
    }
  )
}

resource "aws_cloudwatch_log_group" "notification_service" {
  name              = "/ecs/${var.project_name}-${var.environment}/notification-service"
  retention_in_days = var.log_retention_days

  tags = merge(
    local.common_tags,
    {
      Service = "notification-service"
    }
  )
}

# Note: ECS Task Definitions and Services are created in separate files
# to allow for easier updates and customization per service
