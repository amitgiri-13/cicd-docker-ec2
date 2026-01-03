output "subnet_id" {
    description = "Public subnet id"
    value = aws_subnet.subnet.id
}

output "security_group_id" {
    description = "Security group id"
    value = aws_security_group.sg.id 
}

