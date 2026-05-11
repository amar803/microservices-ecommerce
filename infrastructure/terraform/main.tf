provider "aws" {
  region = var.aws_region
}

locals {
  name_prefix = "${var.project_name}-${var.environment}"
}

module "network" {
  source      = "./modules/network"
  name_prefix = local.name_prefix
  vpc_cidr    = var.vpc_cidr
}

module "compute" {
  source         = "./modules/compute"
  name_prefix    = local.name_prefix
  vpc_id         = module.network.vpc_id
  public_subnets = module.network.public_subnet_ids
}
