variable "instance_type" {
    type = string
    description = "Type of ec2 instance"
    default = "t3.micro"

    validation {
        condition = contains(["t2.micro", "t3.micro"], var.instance_type)
        error_message = "Instance Type must be t2.micro or t3.micro"
    }
}

variable "keypair" {
  type = string
  description = "Key Pair for EC2"
  default = "terraformkey"
}

variable "volume_type" {
    type = string
    description = "EBS volume type"
    default = "gp2"

    validation {
      condition = contains(["gp2", "gp3"], var.volume_type)
      error_message = "Volume type must be gp2 or gp3"   
      }
}

variable "volume_size" {
    type = number
    description = "EBS volume size"
    default = 20
}

variable "delete_on_termination" {
    type = bool
    description = "Delete on termination for EBS volume"
    default = true
}

variable "instance_name" {
  type = string
  description = "Name of ec2"
  default = "TaskEC2"
}

variable "subnet_id" {
    type = string
    description = "subnet id for ec2"
}

variable "security_group_id" {
    type = string
    description = "security group id for ec2"
}

variable "user_data" {
  type = string
  description = "user data for ec2 instance"
}