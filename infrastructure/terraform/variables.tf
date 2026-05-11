variable "project_name" {
  description = "Project identifier used for resource naming"
  type        = string
  default     = "microservices-ecommerce"
}

variable "environment" {
  description = "Target environment name"
  type        = string
  default     = "dev"
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.20.0.0/16"
}
