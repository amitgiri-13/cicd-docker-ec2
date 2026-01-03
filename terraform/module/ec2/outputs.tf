output "public_ip" {
  description = "Public ip of ec2"
  value = aws_instance.lab_ec2.public_ip
}