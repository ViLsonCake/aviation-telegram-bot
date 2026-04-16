data "archive_file" "placeholder_flightradar_api" {
  type        = "zip"
  source_file = "${path.module}/placeholder/placeholder.py"
  output_path = "${path.module}/placeholder/placeholder_flightradar_api.zip"
}

resource "aws_lambda_function" "flightradar_api_airport" {
  function_name = "${var.project_name}-flightradar-api-get-airport"
  role          = aws_iam_role.lambda_execution_role.arn
  runtime       = "python3.12"
  handler       = "get_airport_lambda.lambda_handler"
  memory_size   = 256
  timeout       = 30

  filename         = data.archive_file.placeholder_flightradar_api.output_path
  source_code_hash = data.archive_file.placeholder_flightradar_api.output_base64sha256

  lifecycle {
    ignore_changes = [filename, source_code_hash]
  }

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

resource "aws_lambda_function" "flightradar_api_scheduled_flights" {
  function_name = "${var.project_name}-flightradar-api-get-scheduled-flights"
  role          = aws_iam_role.lambda_execution_role.arn
  runtime       = "python3.12"
  handler       = "get_scheduled_flights_lambda.lambda_handler"
  memory_size   = 256
  timeout       = 30

  filename         = data.archive_file.placeholder_flightradar_api.output_path
  source_code_hash = data.archive_file.placeholder_flightradar_api.output_base64sha256

  lifecycle {
    ignore_changes = [filename, source_code_hash]
  }

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

resource "aws_lambda_function" "flightradar_api_get_specific_aircraft" {
  function_name = "${var.project_name}-flightradar-api-get-specific-aircraft"
  role          = aws_iam_role.lambda_execution_role.arn
  runtime       = "python3.12"
  handler       = "get_specific_aircraft_lambda.lambda_handler"
  memory_size   = 256
  timeout       = 30

  filename         = data.archive_file.placeholder_flightradar_api.output_path
  source_code_hash = data.archive_file.placeholder_flightradar_api.output_base64sha256

  lifecycle {
    ignore_changes = [filename, source_code_hash]
  }

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}
