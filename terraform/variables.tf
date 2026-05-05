variable "aws_region" {
  description = "AWS region where all resources are deployed"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Project name used as a prefix for all resource names"
  type        = string
  default     = "aviation-telegram-bot"
}

variable "db_host" {
  description = "Hostname of the RDS PostgreSQL instance"
  type        = string
  sensitive   = true
}

variable "bot_token" {
  description = "Telegram bot token"
  type        = string
  sensitive   = true
}

variable "db_name" {
  description = "Database name for the RDS PostgreSQL instance"
  type        = string
  sensitive   = true
}

variable "db_username" {
  description = "Master username for the RDS PostgreSQL instance"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "Master password for the RDS PostgreSQL instance"
  type        = string
  sensitive   = true
}

variable "flights_notification_schedule" {
  description = "EventBridge schedule expression for the flights-notification-lambda trigger"
  type        = string
  default     = "rate(15 minutes)"
}

variable "flights_status_change_schedule" {
  description = "EventBridge schedule expression for the flight status change check trigger"
  type        = string
  default     = "rate(5 minutes)"
}

variable "alert_email" {
  description = "Email address to receive CloudWatch alarm notifications"
  type        = string
  sensitive   = true
}
