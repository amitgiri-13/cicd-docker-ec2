output "public_ip" {
  description = "Public ip of ec2"
  value       = module.ec2.public_ip
}