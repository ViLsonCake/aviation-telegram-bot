resource "aws_cloudwatch_event_rule" "flights_notification_schedule" {
  name                = "${var.project_name}-flights-notification-schedule"
  schedule_expression = var.flights_notification_schedule

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

resource "aws_cloudwatch_event_target" "flights_notification_target" {
  rule  = aws_cloudwatch_event_rule.flights_notification_schedule.name
  arn   = aws_lambda_function.flights_notification.arn
  input = jsonencode({ type = "SCHEDULED_FLIGHTS" })
}

resource "aws_lambda_permission" "allow_eventbridge_flights_notification" {
  statement_id  = "AllowEventBridgeInvokeFlightsNotification"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.flights_notification.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.flights_notification_schedule.arn
}