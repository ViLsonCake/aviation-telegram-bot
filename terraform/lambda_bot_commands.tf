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
