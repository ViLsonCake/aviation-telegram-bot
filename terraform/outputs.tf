output "rds_endpoint" {
  description = "RDS PostgreSQL endpoint (host:port)"
  value       = aws_db_instance.main.endpoint
}

output "bot_commands_processing_function_url" {
  description = "Function URL of the bot-commands-processing Lambda"
  value       = aws_lambda_function_url.bot_commands_processing.function_url
}

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

output "flightradar_api_airport_function_arn" {
  description = "ARN of the flightradar-api Airport Lambda function"
  value       = aws_lambda_function.flightradar_api_airport.arn
}

output "flightradar_api_airport_function_name" {
  description = "Name of the flightradar-api Airport Lambda function"
  value       = aws_lambda_function.flightradar_api_airport.function_name
}

output "flightradar_api_scheduled_flights_function_arn" {
  description = "ARN of the flightradar-api Scheduled Flights Lambda function"
  value       = aws_lambda_function.flightradar_api_scheduled_flights.arn
}

output "flightradar_api_scheduled_flights_function_name" {
  description = "Name of the flightradar-api Scheduled Flights Lambda function"
  value       = aws_lambda_function.flightradar_api_scheduled_flights.function_name
}

output "flightradar_api_specific_aircraft_function_arn" {
  description = "ARN of the flightradar-api Specific Aircraft Lambda function"
  value       = aws_lambda_function.flightradar_api_get_specific_aircraft.arn
}

output "flightradar_api_specific_aircraft_function_name" {
  description = "Name of the flightradar-api Specific Aircraft Lambda function"
  value       = aws_lambda_function.flightradar_api_get_specific_aircraft.function_name
}
