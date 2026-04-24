data "archive_file" "placeholder_flights_notification" {
  type        = "zip"
  source_file = "${path.module}/placeholder/placeholder.py"
  output_path = "${path.module}/placeholder/placeholder_flights_notification.zip"
}

resource "aws_lambda_function" "flights_notification" {
  function_name = "${var.project_name}-flights-notification"
  role          = aws_iam_role.lambda_execution_role.arn
  runtime       = "java17"
  handler       = "org.springframework.cloud.function.adapter.aws.FunctionInvoker"
  memory_size   = 512
  timeout       = 60

  filename         = data.archive_file.placeholder_flights_notification.output_path
  source_code_hash = data.archive_file.placeholder_flights_notification.output_base64sha256

  lifecycle {
    ignore_changes = [filename, source_code_hash]
  }

  environment {
    variables = {
      FLIGHTRADAR_API_LAMBDA_NAME = aws_lambda_function.flightradar_api_scheduled_flights.function_name
    }
  }

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}
