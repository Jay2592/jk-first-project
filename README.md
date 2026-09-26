<<<<<<< HEAD
Project scaffold: Java microservices + ETL + secure REST APIs

This repository contains a starter scaffold that demonstrates:
- Spring Boot microservice (REST + Actuator)
- Spring Batch for ETL jobs
- Kafka and RabbitMQ clients configured via Spring
- JDBC + PostgreSQL with notes on SQL optimization and tuning
- Apache Iceberg integration notes (client dependencies included)
- Docker and docker-compose for local dev (Kafka, ZK, RabbitMQ, Postgres, MinIO)
- CI/CD hints (GitHub Actions example included below)
- AWS deployment notes (ECS/EKS, RDS, MSK, S3/MinIO for Iceberg)

Quick start (local):
1. Build: mvn -DskipTests package
2. Start required infra locally (see docker-compose.yml): docker compose up -d
3. Run app: mvn spring-boot:run

CI/CD (example GitHub Actions):
- Build and test with maven
- Build Docker image and push to registry
- Deploy to AWS (ECS/EKS) with infra-as-code (Terraform/CloudFormation)

Notes:
- Iceberg usually runs with Spark/EMR and an object store (S3, MinIO). The pom includes iceberg-spark3-runtime as a provided dependency; adapt when adding Spark jobs.
- For SQL optimization/tuning: use prepared statements, proper indexes, EXPLAIN ANALYZE, connection pool sizing (HikariCP), and RDS parameter tuning in AWS.

Next steps (suggested):
- Split into modules: service-api, service-batch, service-ingest, common-lib
- Add .github/workflows/ci.yml (sample included in this README)

Sample GitHub Actions job (paste into .github/workflows/ci.yml):

name: CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: temurin
      - name: Build with Maven
        run: mvn -B -DskipTests package
      - name: Run tests
        run: mvn test

For AWS deployment, use:
- RDS (Postgres) or Aurora
- MSK (managed Kafka) or Amazon MQ for Rabbit
- S3 as the object store for Iceberg tables (or MinIO for on-prem/dev)

Contact: add microservice modules, CI workflows, and Terraform as needed.
=======
# jk-first-project
>>>>>>> 7dcf3575b21abff237d0f90399f7b7a88ee7bd74
