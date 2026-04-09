output "bot_commands_processing_function_arn" {
  description = "ARN of the bot-commands-processing Lambda function"
  value       = aws_lambda_function.bot_commands_processing.arn
}

output "bot_commands_processing_function_name" {
  description = "Name of the bot-commands-processing Lambda function"
  value       = aws_lambda_function.bot_commands_processing.function_name
}

output "flights_notification_function_arn" {
  description = "ARN of the flights-notification Lambda function"
  value       = aws_lambda_function.flights_notification.arn
}

output "flights_notification_function_name" {
  description = "Name of the flights-notification Lambda function"
  value       = aws_lambda_function.flights_notification.function_name
}

output "flightradar_api_function_arn" {
  description = "ARN of the flightradar-api Lambda function"
  value       = aws_lambda_function.flightradar_api.arn
}

output "flightradar_api_function_name" {
  description = "Name of the flightradar-api Lambda function"
  value       = aws_lambda_function.flightradar_api.function_name
}
