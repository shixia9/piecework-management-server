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

## 🚀 Getting Started

1. Clone the repository
2. Configure your environment in application.yml (MySQL, Redis, OSS, etc.)
3. Run the application
