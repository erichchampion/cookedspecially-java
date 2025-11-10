# ECS Services and Task Definitions

# Order Service Task Definition
resource "aws_ecs_task_definition" "order_service" {
  family                   = "${var.project_name}-${var.environment}-order-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.service_cpu
  memory                   = var.service_memory
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = "order-service"
      image     = "${var.ecr_repository_url}/order-service:${var.image_tag}"
      essential = true

      portMappings = [
        {
          containerPort = 8081
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = var.environment
        },
        {
          name  = "AWS_REGION"
          value = var.aws_region
        },
        {
          name  = "DB_HOST"
          value = var.db_host
        },
        {
          name  = "REDIS_HOST"
          value = var.redis_host
        },
        {
          name  = "KAFKA_BOOTSTRAP_SERVERS"
          value = var.kafka_bootstrap_servers
        }
      ]

      secrets = [
        {
          name      = "DB_PASSWORD"
          valueFrom = "${var.secrets_arn_prefix}/db-password"
        },
        {
          name      = "JWT_SECRET"
          valueFrom = "${var.secrets_arn_prefix}/jwt-secret"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.order_service.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "curl -f http://localhost:8081/actuator/health || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 60
      }
    }
  ])

  tags = merge(
    local.common_tags,
    {
      Service = "order-service"
    }
  )
}

# Order Service ECS Service
resource "aws_ecs_service" "order_service" {
  name            = "${var.project_name}-${var.environment}-order-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.order_service.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_app_subnet_ids
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.order_service.arn
    container_name   = "order-service"
    container_port   = 8081
  }

  deployment_configuration {
    maximum_percent         = 200
    minimum_healthy_percent = 100

    deployment_circuit_breaker {
      enable   = true
      rollback = true
    }
  }

  enable_execute_command = true

  tags = merge(
    local.common_tags,
    {
      Service = "order-service"
    }
  )

  depends_on = [aws_lb_listener.http]
}

# Payment Service Task Definition
resource "aws_ecs_task_definition" "payment_service" {
  family                   = "${var.project_name}-${var.environment}-payment-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.service_cpu
  memory                   = var.service_memory
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = "payment-service"
      image     = "${var.ecr_repository_url}/payment-service:${var.image_tag}"
      essential = true

      portMappings = [
        {
          containerPort = 8082
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = var.environment
        },
        {
          name  = "AWS_REGION"
          value = var.aws_region
        },
        {
          name  = "DB_HOST"
          value = var.db_host
        },
        {
          name  = "REDIS_HOST"
          value = var.redis_host
        }
      ]

      secrets = [
        {
          name      = "DB_PASSWORD"
          valueFrom = "${var.secrets_arn_prefix}/db-password"
        },
        {
          name      = "STRIPE_API_KEY"
          valueFrom = "${var.secrets_arn_prefix}/stripe-api-key"
        },
        {
          name      = "JWT_SECRET"
          valueFrom = "${var.secrets_arn_prefix}/jwt-secret"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.payment_service.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "curl -f http://localhost:8082/actuator/health || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 60
      }
    }
  ])

  tags = merge(
    local.common_tags,
    {
      Service = "payment-service"
    }
  )
}

# Payment Service ECS Service
resource "aws_ecs_service" "payment_service" {
  name            = "${var.project_name}-${var.environment}-payment-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.payment_service.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_app_subnet_ids
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.payment_service.arn
    container_name   = "payment-service"
    container_port   = 8082
  }

  deployment_configuration {
    maximum_percent         = 200
    minimum_healthy_percent = 100

    deployment_circuit_breaker {
      enable   = true
      rollback = true
    }
  }

  enable_execute_command = true

  tags = merge(
    local.common_tags,
    {
      Service = "payment-service"
    }
  )

  depends_on = [aws_lb_listener.http]
}

# Restaurant Service Task Definition
resource "aws_ecs_task_definition" "restaurant_service" {
  family                   = "${var.project_name}-${var.environment}-restaurant-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.service_cpu
  memory                   = var.service_memory
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = "restaurant-service"
      image     = "${var.ecr_repository_url}/restaurant-service:${var.image_tag}"
      essential = true

      portMappings = [
        {
          containerPort = 8083
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = var.environment
        },
        {
          name  = "AWS_REGION"
          value = var.aws_region
        },
        {
          name  = "DB_HOST"
          value = var.db_host
        },
        {
          name  = "REDIS_HOST"
          value = var.redis_host
        }
      ]

      secrets = [
        {
          name      = "DB_PASSWORD"
          valueFrom = "${var.secrets_arn_prefix}/db-password"
        },
        {
          name      = "JWT_SECRET"
          valueFrom = "${var.secrets_arn_prefix}/jwt-secret"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.restaurant_service.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "curl -f http://localhost:8083/actuator/health || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 60
      }
    }
  ])

  tags = merge(
    local.common_tags,
    {
      Service = "restaurant-service"
    }
  )
}

# Restaurant Service ECS Service
resource "aws_ecs_service" "restaurant_service" {
  name            = "${var.project_name}-${var.environment}-restaurant-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.restaurant_service.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_app_subnet_ids
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.restaurant_service.arn
    container_name   = "restaurant-service"
    container_port   = 8083
  }

  deployment_configuration {
    maximum_percent         = 200
    minimum_healthy_percent = 100

    deployment_circuit_breaker {
      enable   = true
      rollback = true
    }
  }

  enable_execute_command = true

  tags = merge(
    local.common_tags,
    {
      Service = "restaurant-service"
    }
  )

  depends_on = [aws_lb_listener.http]
}

# Notification Service Task Definition
resource "aws_ecs_task_definition" "notification_service" {
  family                   = "${var.project_name}-${var.environment}-notification-service"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.service_cpu
  memory                   = var.service_memory
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = "notification-service"
      image     = "${var.ecr_repository_url}/notification-service:${var.image_tag}"
      essential = true

      portMappings = [
        {
          containerPort = 8084
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = var.environment
        },
        {
          name  = "AWS_REGION"
          value = var.aws_region
        },
        {
          name  = "REDIS_HOST"
          value = var.redis_host
        }
      ]

      secrets = [
        {
          name      = "JWT_SECRET"
          valueFrom = "${var.secrets_arn_prefix}/jwt-secret"
        },
        {
          name      = "FIREBASE_CREDENTIALS"
          valueFrom = "${var.secrets_arn_prefix}/firebase-credentials"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.notification_service.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "curl -f http://localhost:8084/actuator/health || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 60
      }
    }
  ])

  tags = merge(
    local.common_tags,
    {
      Service = "notification-service"
    }
  )
}

# Notification Service ECS Service
resource "aws_ecs_service" "notification_service" {
  name            = "${var.project_name}-${var.environment}-notification-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.notification_service.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_app_subnet_ids
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.notification_service.arn
    container_name   = "notification-service"
    container_port   = 8084
  }

  deployment_configuration {
    maximum_percent         = 200
    minimum_healthy_percent = 100

    deployment_circuit_breaker {
      enable   = true
      rollback = true
    }
  }

  enable_execute_command = true

  tags = merge(
    local.common_tags,
    {
      Service = "notification-service"
    }
  )

  depends_on = [aws_lb_listener.http]
}
