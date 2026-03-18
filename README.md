# RevPay_P3
Full-stack digital payments platform built with Spring Boot microservices, Angular, and AWS — featuring JWT auth, service discovery, API Gateway, and role-based access for personal and business users.

# RevPay — Cloud-Native Microservices Payment Platform

> A full-stack digital payments platform modernized from a Spring Boot monolith into a secure, scalable microservices architecture deployed on AWS with Kubernetes orchestration.

---

## Overview

RevPay enables secure digital payments and money management for both personal and business users. Personal users can manage wallets, send and request money, view transaction history, and receive real-time notifications. Business users get additional capabilities including invoice creation, payment acceptance, business loan applications with EMI management, and analytics dashboards.

---

## Architecture

```
Angular Frontend
      ↓
API Gateway (routing · JWT validation · rate limiting · circuit breaker)
      ↓
Eureka Server (service discovery · health checks · load balancing)
      ↓
┌─────────────┬──────────────────┬────────────────┬──────────────┬───────────────────┐
│ User Service│ Wallet Service   │ Transaction Svc│ Invoice Svc  │ Loan Service      │
│ Port 8081   │ Port 8082        │ Port 8083      │ Port 8085    │ Port 8084         │
└─────────────┴──────────────────┴────────────────┴──────────────┴───────────────────┘
                                                               Notification Service
      ↓
Config Server · OpenFeign (inter-service) · MySQL on AWS RDS
```

Each service owns its own database schema and lifecycle.

---

## Services

| Service | Port | Responsibility |
|---|---|---|
| `api-gateway` | 8080 | Routing, JWT validation, rate limiting, Resilience4j circuit breaker |
| `eureka-server` | 8761 | Service registry and health monitoring |
| `user-service` | 8081 | Auth, registration, JWT generation, PIN validation, RBAC |
| `wallet-service` | 8082 | Wallet balance, add/withdraw funds, payment methods |
| `transaction-service` | 8083 | Send/request money, transaction history, CSV/PDF export |
| `invoice-service` | 8085 | Invoice creation, line items, payment acceptance (Business) |
| `loan-service` | 8084 | Loan applications, document upload, EMI schedule, repayments |
| `notification-service` | 8086 | Real-time alerts, low-balance detection, notification history |

---

## Tech Stack

**Backend:** Java 17 · Spring Boot 3 · Spring Cloud · Spring Security · OpenFeign · Resilience4j

**Frontend:** Angular · TypeScript · HTTP Interceptors · Route Guards

**Infrastructure:** Docker · Kubernetes · AWS EC2 · AWS RDS · Maven · MySQL

**Security:** JWT (HS256) · BCrypt · Role-Based Access Control (PERSONAL / BUSINESS / ADMIN)

---

## Security Flow

1. User logs in → User Service verifies BCrypt password → generates JWT token
2. Angular stores token in localStorage via HTTP Interceptor
3. Every request automatically includes `Authorization: Bearer {token}`
4. API Gateway validates JWT signature and expiry on every request
5. Gateway injects `X-User-Id` header and forwards to the target service
6. Each microservice reads claims from the token — no database query for auth
7. Role-based access enforced at both Angular (guards) and service (RBAC) level

---

## Key Features

**Personal users**
- Wallet management and fund top-up
- Send and request money with transaction PIN security
- Full transaction history with filters, search, and CSV/PDF export
- Real-time notifications and low-balance alerts

**Business users**
- Invoice creation with line items and status tracking
- Payment acceptance from clients
- Business loan applications with document upload
- EMI schedule management and repayment tracking
- Revenue analytics and top-customer dashboards

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8+
- Node.js 18+ and Angular CLI
- Docker (optional)

### Run locally

```bash
# 1. Start Eureka Server first
cd eureka-server && mvn spring-boot:run

# 2. Start Config Server
cd config-server && mvn spring-boot:run

# 3. Start each microservice
cd user-service && mvn spring-boot:run
cd wallet-service && mvn spring-boot:run
cd transaction-service && mvn spring-boot:run
# ... repeat for other services

# 4. Start API Gateway
cd api-gateway && mvn spring-boot:run

# 5. Start Angular frontend
cd frontend && npm install && ng serve
```

### With Docker

```bash
docker-compose up --build
```

---

## Project Structure

```
revpay/
├── api-gateway/
├── eureka-server/
├── config-server/
├── user-service/
├── wallet-service/
├── transaction-service/
├── invoice-service/
├── loan-service/
├── notification-service/
└── frontend/           # Angular application
    ├── core/
    │   ├── interceptors/   # JWT attachment
    │   ├── guards/         # Route protection
    │   ├── services/       # Shared services
    │   └── models/         # DTOs and interfaces
    └── features/
        ├── auth/           # Login, register
        ├── personal/       # Personal user features
        ├── business/       # Business user features
        └── admin/          # Admin panel
```

---

## Environment Variables

Each service reads configuration from the Config Server. Key variables:

```properties
JWT_SECRET=your_jwt_secret_key
DB_USERNAME=revpay
DB_PASSWORD=your_db_password
EUREKA_URL=http://localhost:8761/eureka
```

---

## License

MIT License — feel free to use this project for learning and portfolio purposes.
