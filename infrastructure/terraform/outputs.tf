output "vpc_id" {
  description = "VPC identifier"
  value       = module.network.vpc_id
}

output "eks_cluster_name" {
  description = "EKS cluster name"
  value       = module.compute.cluster_name
}
