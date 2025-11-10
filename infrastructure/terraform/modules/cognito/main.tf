# Cognito Module - User Authentication and Authorization

locals {
  common_tags = {
    Module      = "Cognito"
    Environment = var.environment
  }
}

# Cognito User Pool
resource "aws_cognito_user_pool" "main" {
  name = "${var.project_name}-${var.environment}-users"

  # Username configuration
  username_attributes      = ["email"]
  auto_verified_attributes = ["email"]

  # Password policy
  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_uppercase = true
    require_numbers   = true
    require_symbols   = true
  }

  # MFA configuration
  mfa_configuration = var.enable_mfa ? "OPTIONAL" : "OFF"

  # Account recovery
  account_recovery_setting {
    recovery_mechanism {
      name     = "verified_email"
      priority = 1
    }
  }

  # User attributes
  schema {
    name                = "email"
    attribute_data_type = "String"
    mutable             = false
    required            = true

    string_attribute_constraints {
      min_length = 1
      max_length = 256
    }
  }

  schema {
    name                = "name"
    attribute_data_type = "String"
    mutable             = true
    required            = true

    string_attribute_constraints {
      min_length = 1
      max_length = 256
    }
  }

  schema {
    name                = "phone_number"
    attribute_data_type = "String"
    mutable             = true
    required            = false

    string_attribute_constraints {
      min_length = 10
      max_length = 20
    }
  }

  # Email configuration
  email_configuration {
    email_sending_account = "COGNITO_DEFAULT"
  }

  # Verification messages
  verification_message_template {
    default_email_option = "CONFIRM_WITH_CODE"
    email_subject        = "Your ${var.project_name} verification code"
    email_message        = "Your verification code is {####}"
  }

  # Lambda triggers (optional - add as needed)
  # lambda_config {
  #   pre_sign_up    = aws_lambda_function.pre_signup.arn
  #   post_confirmation = aws_lambda_function.post_confirmation.arn
  # }

  tags = merge(
    local.common_tags,
    {
      Name = "${var.project_name}-${var.environment}-user-pool"
    }
  )
}

# User Pool Domain
resource "aws_cognito_user_pool_domain" "main" {
  domain       = "${var.project_name}-${var.environment}-auth"
  user_pool_id = aws_cognito_user_pool.main.id
}

# User Pool Client - Web Application
resource "aws_cognito_user_pool_client" "web" {
  name         = "${var.project_name}-${var.environment}-web-client"
  user_pool_id = aws_cognito_user_pool.main.id

  # OAuth settings
  allowed_oauth_flows_user_pool_client = true
  allowed_oauth_flows                  = ["code", "implicit"]
  allowed_oauth_scopes                 = ["email", "openid", "profile", "aws.cognito.signin.user.admin"]
  callback_urls                        = var.callback_urls
  logout_urls                          = var.logout_urls
  supported_identity_providers         = ["COGNITO"]

  # Token validity
  id_token_validity      = 60   # 60 minutes
  access_token_validity  = 60   # 60 minutes
  refresh_token_validity = 30   # 30 days

  token_validity_units {
    id_token      = "minutes"
    access_token  = "minutes"
    refresh_token = "days"
  }

  # Security
  prevent_user_existence_errors = "ENABLED"

  # Attributes
  read_attributes  = ["email", "name", "phone_number"]
  write_attributes = ["name", "phone_number"]

  # Explicit auth flows
  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_SRP_AUTH"
  ]
}

# User Pool Client - Mobile App
resource "aws_cognito_user_pool_client" "mobile" {
  name         = "${var.project_name}-${var.environment}-mobile-client"
  user_pool_id = aws_cognito_user_pool.main.id

  # OAuth settings
  allowed_oauth_flows_user_pool_client = true
  allowed_oauth_flows                  = ["code"]
  allowed_oauth_scopes                 = ["email", "openid", "profile", "aws.cognito.signin.user.admin"]
  callback_urls                        = var.mobile_callback_urls
  logout_urls                          = var.mobile_logout_urls
  supported_identity_providers         = ["COGNITO"]

  # Token validity
  id_token_validity      = 60   # 60 minutes
  access_token_validity  = 60   # 60 minutes
  refresh_token_validity = 30   # 30 days

  token_validity_units {
    id_token      = "minutes"
    access_token  = "minutes"
    refresh_token = "days"
  }

  # Security
  prevent_user_existence_errors = "ENABLED"

  # Attributes
  read_attributes  = ["email", "name", "phone_number"]
  write_attributes = ["name", "phone_number"]

  # Explicit auth flows
  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_SRP_AUTH"
  ]
}

# User Pool Groups
resource "aws_cognito_user_pool_group" "customers" {
  name         = "customers"
  user_pool_id = aws_cognito_user_pool.main.id
  description  = "Customer users"
  precedence   = 10
}

resource "aws_cognito_user_pool_group" "restaurant_owners" {
  name         = "restaurant-owners"
  user_pool_id = aws_cognito_user_pool.main.id
  description  = "Restaurant owner users"
  precedence   = 5
}

resource "aws_cognito_user_pool_group" "admins" {
  name         = "admins"
  user_pool_id = aws_cognito_user_pool.main.id
  description  = "Admin users"
  precedence   = 1
}

# Identity Pool for AWS resource access (optional)
resource "aws_cognito_identity_pool" "main" {
  identity_pool_name               = "${var.project_name}-${var.environment}-identity-pool"
  allow_unauthenticated_identities = false

  cognito_identity_providers {
    client_id               = aws_cognito_user_pool_client.web.id
    provider_name           = aws_cognito_user_pool.main.endpoint
    server_side_token_check = false
  }

  cognito_identity_providers {
    client_id               = aws_cognito_user_pool_client.mobile.id
    provider_name           = aws_cognito_user_pool.main.endpoint
    server_side_token_check = false
  }

  tags = local.common_tags
}

# Identity Pool Roles
resource "aws_cognito_identity_pool_roles_attachment" "main" {
  identity_pool_id = aws_cognito_identity_pool.main.id

  roles = {
    "authenticated" = aws_iam_role.authenticated.arn
  }
}

# IAM Role for Authenticated Users
resource "aws_iam_role" "authenticated" {
  name = "${var.project_name}-${var.environment}-cognito-authenticated"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Federated = "cognito-identity.amazonaws.com"
        }
        Action = "sts:AssumeRoleWithWebIdentity"
        Condition = {
          StringEquals = {
            "cognito-identity.amazonaws.com:aud" = aws_cognito_identity_pool.main.id
          }
          "ForAnyValue:StringLike" = {
            "cognito-identity.amazonaws.com:amr" = "authenticated"
          }
        }
      }
    ]
  })

  tags = local.common_tags
}

# IAM Policy for Authenticated Users
resource "aws_iam_role_policy" "authenticated" {
  name = "${var.project_name}-${var.environment}-cognito-authenticated-policy"
  role = aws_iam_role.authenticated.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:PutObject"
        ]
        Resource = "arn:aws:s3:::${var.project_name}-${var.environment}-*/*"
      }
    ]
  })
}
