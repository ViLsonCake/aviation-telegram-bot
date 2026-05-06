data "archive_file" "placeholder_bot_commands" {
  type        = "zip"
  source_file = "${path.module}/placeholder/placeholder.py"
  output_path = "${path.module}/placeholder/placeholder_bot_commands.zip"
}

resource "aws_lambda_function" "bot_commands_processing" {
  function_name = "${var.project_name}-bot-commands-processing"
  role          = aws_iam_role.lambda_execution_role.arn
  runtime       = "java17"
  handler       = "org.springframework.cloud.function.adapter.aws.FunctionInvoker"
  memory_size   = 512
  timeout       = 30
  publish       = true

  snap_start {
    apply_on = "PublishedVersions"
  }

  environment {
    variables = {
      DB_HOST                    = var.db_host
      DB_NAME                    = var.db_name
      DB_USERNAME                = var.db_username
      DB_PASSWORD                = var.db_password
      BOT_TOKEN                  = var.bot_token
      FLIGHTRADAR_API_LAMBDA_NAME = aws_lambda_function.flightradar_api_scheduled_flights.function_name
    }
  }

  filename         = data.archive_file.placeholder_bot_commands.output_path
  source_code_hash = data.archive_file.placeholder_bot_commands.output_base64sha256

  lifecycle {
    ignore_changes = [filename, source_code_hash]
  }

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

resource "aws_lambda_alias" "bot_commands_processing" {
  name             = "live"
  function_name    = aws_lambda_function.bot_commands_processing.function_name
  function_version = aws_lambda_function.bot_commands_processing.version

  lifecycle {
    ignore_changes = [function_version]
  }
}

resource "aws_lambda_function_url" "bot_commands_processing" {
  function_name      = aws_lambda_function.bot_commands_processing.function_name
  qualifier          = aws_lambda_alias.bot_commands_processing.name
  authorization_type = "NONE"
}
