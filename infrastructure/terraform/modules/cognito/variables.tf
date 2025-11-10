# Cognito Module Variables

variable "project_name" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "enable_mfa" {
  description = "Enable multi-factor authentication"
  type        = bool
  default     = false
}

variable "callback_urls" {
  description = "Allowed callback URLs for web client"
  type        = list(string)
  default     = ["http://localhost:3000/callback"]
}

variable "logout_urls" {
  description = "Allowed logout URLs for web client"
  type        = list(string)
  default     = ["http://localhost:3000/"]
}

variable "mobile_callback_urls" {
  description = "Allowed callback URLs for mobile client"
  type        = list(string)
  default     = ["cookedspecially://callback"]
}

variable "mobile_logout_urls" {
  description = "Allowed logout URLs for mobile client"
  type        = list(string)
  default     = ["cookedspecially://logout"]
}
