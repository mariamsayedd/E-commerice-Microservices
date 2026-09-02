# E-Commerce Microservices

A Spring Boot / Spring Cloud e-commerce system built as three independent microservices — **wallet**, **shop**, and **inventory** — communicating through service discovery and Feign, fronted by a single API Gateway, with centralized configuration and resilience built in.

## Architecture

```
                              ┌─────────────────┐
                              │  Eureka Server   │  :8761
                              │ (service registry)│
                              └────────┬─────────┘
                                       │ all services register here
        ┌──────────────────────────────┼──────────────────────────────┐
        │                              │                              │
┌───────▼────────┐           ┌─────────▼────────┐          ┌──────────▼───────┐
│  wallet-service │           │   shop-service    │          │ inventory-service │
│     :8081       │◄──Feign───│      :8082        │──Feign──►│      :8083        │
│ walletdb        │  (payment)│  shopdb            │  (sync)  │  inventorydb      │
└─────────────────┘           └───────────────────┘          └───────────────────┘
        ▲                              ▲                              ▲
        └──────────────────────────────┼──────────────────────────────┘
                                       │ routed through
                              ┌────────┴─────────┐
                              │   API Gateway     │  :8765
                              │ (single entry point)│
                              └────────▲─────────┘
                                       │
                              ┌────────┴─────────┐
                              │  Config Server    │  :8888
                              │ (reads ejada-      │
                              │  config-repo)      │
                              └───────────────────┘
```

Each service owns its own MySQL database — no shared tables, no cross-database foreign keys. Cross-service references (e.g. a `Product` existing in both `shopdb` and `inventorydb`) are kept in sync through Feign calls, not database joins. shop-service also makes a plain outbound call to **Cloudinary** for product image storage — external to the service mesh above.

## Tech Stack

| Layer | Technology |
|---|---|
| Language / build | Java 21, Maven |
| Framework | Spring Boot 4.1.1 |
| Persistence | Spring Data JPA, MySQL (one database per service) |
| Auth | Spring Security, JWT — issued by wallet-service, independently verified by shop/inventory via a shared signing secret |
| Service discovery | Spring Cloud Netflix Eureka |
| Inter-service calls | Spring Cloud OpenFeign |
| Resilience | Resilience4j (circuit breakers) |
| Gateway | Spring Cloud Gateway (routing + Eureka-based load balancing) |
| Config | Spring Cloud Config Server, backed by a separate `ejada-config-repo` |
| Media storage | Cloudinary (product images) |

## Services

| Service | Port | Responsibility |
|---|---|---|
| `eureka-server` | 8761 | Service registry |
| `config-server` | 8888 | Centralized configuration |
| `wallet-service` | 8081 | User auth, wallet balance, deposits/withdrawals, transaction history |
| `shop-service` | 8082 | Categories, products, cart, orders, payment, product image uploads |
| `inventory-service` | 8083 | Stock tracking, synced from shop-service via Feign |
| `api-gateway` | 8765 | Single entry point, routes to all of the above |

## Data Model

| Database | Owned by | Tables |
|---|---|---|
| `walletdb` | wallet-service | `users`, `wallets`, `wallet_transactions` |
| `shopdb` | shop-service | `categories`, `products`, `carts`, `cart_items`, `orders`, `order_items`, `payments` |
| `inventorydb` | inventory-service | `products` (local copy), `inventory` |

Reference schema lives in `database/` — documentation only, not meant to be re-run against an existing database.

## Authentication & Authorization

- wallet-service issues JWTs on register/login containing `user_id` and `role` (`USER` or `ADMIN`).
- shop-service and inventory-service independently verify the same JWT rather than trusting any client-supplied identity header.
- Catalog-modifying endpoints (create/update/delete on categories, products, inventory, and image uploads) require `role = ADMIN`. Browsing is open to everyone. Cart/order/payment actions require any valid logged-in user.
- The first admin account is promoted directly in the database — self-registration cannot set its own role.

## Product Images

Images are uploaded to Cloudinary and referenced by URL — `products.image_url` stores a link, never the file itself.

```
POST /shop-api/images        (admin only, multipart/form-data, field: file)
→ { "imageUrl": "https://res.cloudinary.com/.../products/xyz.jpg" }
```

That URL is then passed as `imageUrl` when creating or updating a product. Once uploaded, the link is permanent and public — viewable by anyone with no authentication, independent of whether any of our services are running.

## Prerequisites

- Java 21
- MySQL 8+, running locally
- A free [Cloudinary](https://cloudinary.com) account (for product image uploads)
- The `ejada-config-repo` cloned locally

## Running the Project

Start all six applications in order, waiting for each to finish starting before launching the next:

```bash
cd eureka-server && ./mvnw spring-boot:run
cd config-server && ./mvnw spring-boot:run
cd wallet-service && ./mvnw spring-boot:run
cd shop-service && ./mvnw spring-boot:run
cd inventory-service && ./mvnw spring-boot:run
cd api-gateway && ./mvnw spring-boot:run
```

Before starting:
- Set `spring.cloud.config.server.git.uri` in `config-server/application.properties` to your local path for `ejada-config-repo`.
- Set `jwt.secret` (matching across wallet-service, shop-service, and inventory-service) and Cloudinary credentials in `shop-service/application.properties`.

**Sanity checks once running:**
- `http://localhost:8761` — all 5 non-Eureka services show as `UP`
- `http://localhost:8888/wallet-service/default` — returns wallet-service's config as JSON
- `http://localhost:8765/actuator/health` — Gateway reports `UP`

## Testing

All requests are routed through the API Gateway (`localhost:8765`) using a path prefix per service:

| Prefix | Routes to |
|---|---|
| `/wallet-api/**` | wallet-service |
| `/shop-api/**` | shop-service |
| `/inventory-api/**` | inventory-service |

A Postman collection covering every endpoint — including negative/security cases (missing token, wrong role, over-withdrawal) — is included separately.

## Resilience

shop-service's calls to wallet-service (payment) and inventory-service (product sync) are wrapped in Resilience4j circuit breakers:

```
GET /shop-api/actuator/circuitbreakers
GET /shop-api/actuator/circuitbreakerevents
```

Stopping the target service and retrying trips the breaker to `OPEN` (fails fast instead of hanging); it moves to `HALF_OPEN` after a cooldown and back to `CLOSED` once calls succeed again.
