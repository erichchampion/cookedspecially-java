# RDS Module Variables

variable "project_name" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID"
  type        = string
}

variable "private_db_subnet_ids" {
  description = "Private database subnet IDs"
  type        = list(string)
}

variable "app_security_group_ids" {
  description = "Application security group IDs that need DB access"
  type        = list(string)
}

variable "database_name" {
  description = "Name of the database to create"
  type        = string
  default     = "cookedspecially"
}

variable "master_username" {
  description = "Master username for the database"
  type        = string
  default     = "admin"
}

variable "engine_version" {
  description = "MySQL engine version"
  type        = string
  default     = "8.0.35"
}

variable "instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.medium"
}

variable "allocated_storage" {
  description = "Allocated storage in GB"
  type        = number
  default     = 100
}

variable "max_allocated_storage" {
  description = "Maximum allocated storage for autoscaling in GB"
  type        = number
  default     = 500
}

variable "multi_az" {
  description = "Enable Multi-AZ deployment"
  type        = bool
  default     = true
}

variable "backup_retention_period" {
  description = "Backup retention period in days"
  type        = number
  default     = 7
}

variable "backup_window" {
  description = "Preferred backup window"
  type        = string
  default     = "03:00-04:00"
}

variable "maintenance_window" {
  description = "Preferred maintenance window"
  type        = string
  default     = "sun:04:00-sun:05:00"
}

variable "monitoring_interval" {
  description = "Enhanced monitoring interval in seconds (0 to disable)"
  type        = number
  default     = 60
}

variable "performance_insights_enabled" {
  description = "Enable Performance Insights"
  type        = bool
  default     = true
}

variable "auto_minor_version_upgrade" {
  description = "Enable automatic minor version upgrades"
  type        = bool
  default     = true
}

variable "max_connections" {
  description = "Maximum number of database connections"
  type        = string
  default     = "200"
}

variable "create_read_replica" {
  description = "Create read replica(s)"
  type        = bool
  default     = false
}

variable "read_replica_count" {
  description = "Number of read replicas to create"
  type        = number
  default     = 1
}

variable "read_replica_instance_class" {
  description = "Instance class for read replicas"
  type        = string
  default     = "db.t3.medium"
}

variable "kms_key_id" {
  description = "KMS key ID for encryption (optional)"
  type        = string
  default     = null
}

variable "alarm_actions" {
  description = "List of ARNs to notify when alarm triggers"
  type        = list(string)
  default     = []
}
