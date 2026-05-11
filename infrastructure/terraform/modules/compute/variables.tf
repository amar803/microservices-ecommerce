variable "name_prefix" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "public_subnets" {
  type = list(string)
}

variable "cluster_role_arn" {
  description = "IAM role ARN for EKS cluster"
  type        = string
  default     = "arn:aws:iam::123456789012:role/placeholder-eks-role"
}
