# KeyVault Service

A Spring Boot-based secure key vault service that stores secrets, supports JWT authentication, and structured logging to console and Discord.

## Features

- JWT-based authentication (method-level)
- AES/GCM encryption for secrets
- One-time passcodes (OTP) with expiration
- Scheduled cleanup of expired OTPs
- Structured logging with MDC (trace_id, host, success, details)
- SQLite database

## Configuration

### `application.properties`

```properties
spring.datasource.url=jdbc:sqlite:database.sqlite
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update

# JWT config
keyvault.jwt.secret=your-secret-key
keyvault.jwt.expiration=3600000

# Discord webhook (optional)
logging.discord.webhook-url=https://discord.com/api/webhooks/YOUR_WEBHOOK
