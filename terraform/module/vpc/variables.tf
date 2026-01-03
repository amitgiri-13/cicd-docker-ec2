variable "vpc_name" {
  type = string
  description = "Name of the VPC"
}

variable "igw_name" {
  type = string
  description = "Name of InternetGateway"
  default = "ModuleIGW"
}

variable "public_subnet_name" {
    type = string
    description = "Name of subnet"
    default = "ModuleSubnet"
}

variable "public_route_table_name" {
    type = string
    description = "Name of Route Table"
    default = "ModuleRouteTable"
}

variable "security_group_name" {
    type = string
    description = "Name of Security group"
    default = "ModuleSecurityGroup"
}