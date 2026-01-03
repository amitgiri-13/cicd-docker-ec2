provider "aws" {
  region = "us-east-1"
}

module "vpc" {
  source   = "./module/vpc"
  vpc_name = var.vpc_name
}

module "ec2" {
  source            = "./module/ec2"
  security_group_id = module.vpc.security_group_id
  subnet_id         = module.vpc.subnet_id
  user_data         = <<-EOF
              #!/bin/bash
              set -ex

              # Set non-interactive mode
              export DEBIAN_FRONTEND=noninteractive

              # Update & install prerequisites
              apt-get update -y
              apt-get install -y \
                  apt-transport-https \
                  ca-certificates \
                  curl \
                  gnupg-agent \
                  software-properties-common

              # Add Docker GPG key
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -

              # Add Docker repo
              add-apt-repository \
                "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
                $(lsb_release -cs) \
                stable"

              # Install Docker
              apt-get update -y
              apt-get install -y docker-ce docker-ce-cli containerd.io

              # Enable Docker service
              systemctl enable docker
              systemctl start docker

              # Add ubuntu user to docker group
              usermod -aG docker ubuntu

              # Test Docker installation
              docker --version
              EOF
}   