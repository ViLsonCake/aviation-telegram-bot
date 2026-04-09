data "archive_file" "placeholder_flights_notification" {
  type        = "zip"
  source_file = "${path.module}/placeholder/placeholder.py"
  output_path = "${path.module}/placeholder/placeholder_flights_notification.zip"
}

resource "aws_lambda_function" "flights_notification" {
  function_name = "${var.project_name}-flights-notification"
  role          = aws_iam_role.lambda_execution_role.arn
  runtime       = "java17"
  handler       = "project.vilsoncake.flightsnotificationlambda.handler.LambdaHandler"
  memory_size   = 512
  timeout       = 60

  filename         = data.archive_file.placeholder_flights_notification.output_path
  source_code_hash = data.archive_file.placeholder_flights_notification.output_base64sha256

  lifecycle {
    ignore_changes = [filename, source_code_hash]
  }

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}
