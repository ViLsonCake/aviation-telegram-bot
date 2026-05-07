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
  arn   = aws_lambda_alias.flights_notification.arn
  input = jsonencode({ type = "SCHEDULED_FLIGHTS" })
}

resource "aws_lambda_permission" "allow_eventbridge_flights_notification" {
  statement_id  = "AllowEventBridgeInvokeFlightsNotification"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.flights_notification.function_name
  qualifier     = aws_lambda_alias.flights_notification.name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.flights_notification_schedule.arn
}

resource "aws_cloudwatch_event_rule" "flights_status_change_schedule" {
  name                = "${var.project_name}-flights-status-change-schedule"
  schedule_expression = var.flights_status_change_schedule

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

resource "aws_cloudwatch_event_target" "flights_status_change_target" {
  rule  = aws_cloudwatch_event_rule.flights_status_change_schedule.name
  arn   = aws_lambda_alias.flights_notification.arn
  input = jsonencode({ type = "FLIGHT_STATUS_CHANGE" })
}

resource "aws_lambda_permission" "allow_eventbridge_flights_status_change" {
  statement_id  = "AllowEventBridgeInvokeFlightsStatusChange"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.flights_notification.function_name
  qualifier     = aws_lambda_alias.flights_notification.name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.flights_status_change_schedule.arn
}