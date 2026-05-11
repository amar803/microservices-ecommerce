resource "aws_eks_cluster" "this" {
  name     = "${var.name_prefix}-eks"
  role_arn = var.cluster_role_arn

  vpc_config {
    subnet_ids = var.public_subnets
  }

  tags = {
    Name = "${var.name_prefix}-eks"
  }
}
