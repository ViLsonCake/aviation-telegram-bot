resource "aws_s3_bucket" "lambda_artifacts" {
  bucket        = "${var.project_name}-lambda-artifacts"
  force_destroy = true

  tags = {
    Project   = var.project_name
    ManagedBy = "terraform"
  }
}

resource "aws_s3_bucket_versioning" "lambda_artifacts" {
  bucket = aws_s3_bucket.lambda_artifacts.id

  versioning_configuration {
    status = "Enabled"
  }
}