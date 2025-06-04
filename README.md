# Piecework Management System

A modular Spring Boot-based industrial piecework management platform designed to support enterprise users, employees, and system administrators. It integrates role-based access control, data visualization, real-time statistics, and RESTful APIs.

## 📦 Features

- 🧩 **Modular Architecture** (`sys`, `enterprise`, `app`)
- 🔐 **Role-based Security** with Apache Shiro + JWT
- ⚡ **MyBatis Plus** for efficient database operations
- 🧠 **Validation & AOP** based parameter checking and logging
- 📊 **Swagger 2 & Knife4j** for interactive API documentation
- 📸 **Captcha (Kaptcha)** for login verification
- 🐇 **RabbitMQ** for asynchronous message processing
- 🧮 **Redis** caching support
- 📥 **Aliyun OSS** for file storage
- 🧾 **PDF & Excel export** support with iText and Apache POI

## ⚙️ Tech Stack

| Category              | Technology                    |
|-----------------------|-------------------------------|
| Backend Framework     | Spring Boot 2.5.2             |
| ORM                   | MyBatis-Plus 3.3.1            |
| Database              | MySQL 8.0 + Druid             |
| Security              | Apache Shiro + JWT            |
| API Docs              | Swagger2 + Knife4j            |
| Validation            | Hibernate Validator           |
| Caching               | Redis                         |
| Messaging Queue       | RabbitMQ                      |
| File Storage          | Aliyun OSS                    |
| Utilities             | Hutool, Apache Commons        |
| PDF/Excel Export      | iText, Apache POI             |
| Image Captcha         | Kaptcha                       |
| Logging Enhancer      | P6Spy                         |

## 📦 Database Schema

### 🧩 Table: `captcha`

Stores temporary login/verification codes.

| Column      | Type      | Description      |
|-------------|-----------|------------------|
| `uuid`      | CHAR(36)  | Unique identifier |
| `code`      | VARCHAR(6)| Captcha content   |
| `expire_time` | DATE    | Expiration date   |

> Used for login security and user verification.

### 🏢 Table: `enterprise`

Holds tenant enterprise information for SaaS-based system operation.

| Column           | Type          | Description                         |
|------------------|---------------|-------------------------------------|
| `id`             | BIGINT        | Primary key                         |
| `name`           | VARCHAR(20)   | Enterprise name                     |
| `code`           | VARCHAR(255)  | Unified Social Credit Code          |
| `adress`         | VARCHAR(255)  | Contact address                     |
| `contact`        | VARCHAR(255)  | Contact person                      |
| `phone`          | VARCHAR(50)   | Contact phone                       |
| `rent_start_time`| DATETIME      | Rental start date                   |
| `rent_end_time`  | DATETIME      | Rental end date                     |
| `account_limit`  | INT           | Max app accounts                    |
| `account_used`   | INT           | Used app accounts                   |
| `function`       | VARCHAR(255)  | Enabled algorithm modules (e.g. steel, barcode) |
| `status`         | TINYINT       | 0: disabled, 1: active              |
| `storage_limit`  | FLOAT         | Image storage limit (MB)           |
| `storage_used`   | FLOAT         | Storage used (MB)                  |
| `create_user_id` | BIGINT        | Created by                          |
| `create_time`    | DATETIME      | Create time                         |
| `updater_id`     | BIGINT        | Last updated by                     |
| `update_time`    | DATETIME      | Last update time                    |

> Supports multi-tenant platform capabilities.

### 🖼️ Table: `image`

Stores uploaded image metadata and AI recognition results.

| Column           | Type         | Description                           |
|------------------|--------------|---------------------------------------|
| `id`             | BIGINT       | Primary key                           |
| `url`            | VARCHAR(255) | Image path                            |
| `label_string`   | TEXT         | AI recognition results (JSON)         |
| `user_id`        | BIGINT       | Uploader user ID                      |
| `create_time`    | DATETIME     | Upload time                           |
| `update_time`    | DATETIME     | Last update                           |
| `width`          | INT          | Image width                           |
| `height`         | INT          | Image height                          |
| `name`           | VARCHAR(48)  | Image name                            |
| `check`          | VARCHAR(24)  | Processing status                     |
| `num`            | BIGINT       | Total detected items                  |
| `manual_num_list`| VARCHAR(500) | Manual count per item (JSON format)   |
| `enterprise_id`  | BIGINT       | Linked enterprise ID                  |
| `steel_id`       | BIGINT       | Related steel task (if applicable)    |

> Image data is used for algorithm-based recognition (e.g. steel counting).


## 🚀 Getting Started

1. Clone the repository
2. Configure your environment in application.yml (MySQL, Redis, OSS, etc.)
3. Run the application
