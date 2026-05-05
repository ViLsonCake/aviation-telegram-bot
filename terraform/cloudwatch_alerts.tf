resource "aws_cloudwatch_log_metric_filter" "flightradar_api_client_errors" {
  name           = "${var.project_name}-flightradar-api-client-errors"
  log_group_name = "/aws/lambda/${aws_lambda_function.flightradar_api_scheduled_flights.function_name}"
  pattern        = "%\\s40[13]\\s%"

  metric_transformation {
    name          = "FlightRadarApiClientErrors"
    namespace     = "AviationBot"
    value         = "1"
    default_value = "0"
  }
}

resource "aws_sns_topic" "alerts" {
  name = "${var.project_name}-alerts"

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

resource "aws_sns_topic_subscription" "alert_email" {
  topic_arn = aws_sns_topic.alerts.arn
  protocol  = "email"
  endpoint  = var.alert_email
}

resource "aws_cloudwatch_metric_alarm" "flightradar_api_client_errors" {
  alarm_name          = "${var.project_name}-flightradar-api-client-errors"
  alarm_description   = "FlightRadar API returned 401 or 403"
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = 1
  metric_name         = "FlightRadarApiClientErrors"
  namespace           = "AviationBot"
  period              = 300
  statistic           = "Sum"
  threshold           = 1
  treat_missing_data  = "notBreaching"
  alarm_actions       = [aws_sns_topic.alerts.arn]

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}
