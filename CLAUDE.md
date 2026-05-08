# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

NongSanShop is a Spring Boot 3.0.9 e-commerce application (Java 17) for agricultural products. It uses Thymeleaf for server-side rendering, JPA/Hibernate for persistence, and Spring Security for authentication.

## Build & Run Commands

```bash
# Run development server (port 8081)
./mvnw spring-boot:run

# Build production JAR
./mvnw clean package -DskipTests

# Run tests
./mvnw test
```

## Architecture

### Package Structure
- `controller/` - User-facing controllers (Home, Shop, Cart, Orders, WishList)
- `controller/admin/` - Admin dashboard controllers (AdminHomeController, category, product, order management)
- `controller/rest/` - REST API controllers
- `controller/common/` - BaseController provides shared @ModelAttribute methods
- `entity/` - JPA entities (Product, User, Order, Category, etc.)
- `service/` + `service/impl/` - Business logic layer
- `repository/` - Spring Data JPA repositories
- `config/` - Configuration classes (Security, Cloudinary, PayPal, VNPay, etc.)
- `dto/` - Data Transfer Objects

### Key Patterns

**BaseController** (`controller/common/BaseController`) - All controllers extend this. It provides:
- `@ModelAttribute("cartItemCount")` - Cart item count from session
- `@ModelAttribute("currentUser")` - Currently authenticated User from SecurityContext
- `@ModelAttribute("showChatWidget")` - Boolean for chat widget visibility

**AbstractBase** (`entity/AbstractBase`) - Base entity with:
- `id`, `createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `activeFlag`, `deleteFlag`
- All entities extend this (Product, User, Order, Category, etc.)

**Session-based Cart** - Cart stored in HttpSession as `CartDTO`. CartService handles add/update/remove operations.

### Security
- `/admin/**` requires ADMIN role
- `/cart/**`, `/orders/**`, `/wishlist/**` require authentication
- All other routes are public
- Login page: `/login`, default success: `/`

### Database
- MySQL 8, database name: `duonghoastore`
- `spring.jpa.hibernate.ddl-auto=update` (auto-creates tables)
- Connection: `root` / `123456` (from application.properties)

### Payment Integrations
- **PayPal** - Sandbox mode via `PaypalConfiguration` (hardcoded credentials)
- **VNPay** - Via `VNPayConfig`
- **VietQR** - Via `vietqr.account.*` properties
- **Cloudinary** - Image storage via `CloudinaryConfig`

### External Services
- **Email** - SMTP via `spring.mail.*` properties (Gmail)
- **OpenAI** - Chatbot via `openai.api.key` property

## Important Notes

- Server runs on **port 8081** (not default 8080)
- Templates are in `src/main/resources/templates/` with `admin/` and `user/` subdirectories
- Static resources in `src/main/resources/static/`
- DevTools restart is disabled (`spring.devtools.restart.enabled=false`)