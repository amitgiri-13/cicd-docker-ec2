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
