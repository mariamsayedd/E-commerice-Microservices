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

Each service owns its own MySQL database — no shared tables, no cross-database foreign keys. Cross-service references (e.g. a `Product` existing in both `shopdb` and `inventorydb`) are kept in sync through Feign calls, not database-level joins.

## Tech Stack

- Java 21, Spring Boot 4.1.1, Maven
- Spring Data JPA + MySQL (one database per service)
- Spring Security + JWT (wallet-service issues tokens; shop/inventory verify them independently using a shared signing secret)
- Spring Cloud Netflix Eureka (service discovery)
- Spring Cloud OpenFeign (inter-service calls)
- Resilience4j (circuit breakers on shop-service's calls to wallet-service and inventory-service)
- Spring Cloud Gateway (single routed entry point, load-balanced via Eureka)
- Spring Cloud Config Server (centralized `application.properties`, backed by a separate git repo: `ejada-config-repo`)

## Repository Layout

```
wallet-service/       User registration/login, wallet balance, deposit/withdraw, transaction history
shop-service/         Categories, products, cart, orders, payment
inventory-service/    Product stock tracking, synced automatically from shop-service via Feign
eureka-server/        Service registry
api-gateway/          Single entry point — routes /wallet-api, /shop-api, /inventory-api
config-server/        Serves centralized config, backed by the separate ejada-config-repo
database/             Reference SQL schema for all three databases (documentation only — see note below)
```

**Note on `database/`:** these `.sql` files document the schema as designed. Two columns (`users.role` and `products.image_url`) were added later via `ALTER TABLE` directly against the running databases — the files here have been updated to match, but they are not meant to be re-run against an existing database.

## Data Model

Full ERD available separately. Summary of ownership:

| Database | Owned by | Tables |
|---|---|---|
| `walletdb` | wallet-service | `users`, `wallets`, `wallet_transactions` |
| `shopdb` | shop-service | `categories`, `products`, `carts`, `cart_items`, `orders`, `order_items`, `payments` |
| `inventorydb` | inventory-service | `products` (local copy), `inventory` |

## Authentication & Authorization

- wallet-service issues JWTs on register/login, containing `user_id` and `role` (`USER` or `ADMIN`).
- shop-service and inventory-service independently verify the same JWT (shared `jwt.secret`) rather than trusting a client-supplied header — no service trusts client-claimed identity.
- Catalog-modifying endpoints (create/update/delete on categories, products, and inventory records) require `role = ADMIN`. Browsing (GET) is open to everyone. Cart/order/payment actions require any valid logged-in user's token.
- The first admin account is promoted directly in the database (`UPDATE users SET role='ADMIN' WHERE email=...`) — self-registration deliberately cannot set its own role, since that would let anyone become an admin on signup.

## Running the Project

Start all six applications, in this order, waiting for each to finish starting before launching the next:

```bash
cd eureka-server && ./mvnw spring-boot:run
cd config-server && ./mvnw spring-boot:run
cd wallet-service && ./mvnw spring-boot:run
cd shop-service && ./mvnw spring-boot:run
cd inventory-service && ./mvnw spring-boot:run
cd api-gateway && ./mvnw spring-boot:run
```

Config Server needs the separate `ejada-config-repo` cloned locally, with its path set in `config-server/src/main/resources/application.properties`.

## Testing

All requests are routed through the API Gateway (`localhost:8765`), using a path prefix per service:

| Prefix | Routes to |
|---|---|
| `/wallet-api/**` | wallet-service |
| `/shop-api/**` | shop-service |
| `/inventory-api/**` | inventory-service |

## Resilience

`shop-service`'s calls to `wallet-service` (payment) and `inventory-service` (product sync) are wrapped in Resilience4j circuit breakers. Current breaker states are visible at:

```
GET /shop-api/actuator/circuitbreakers
GET /shop-api/actuator/circuitbreakerevents
```

Stopping the target service and retrying a call several times trips the breaker to `OPEN` (fails fast instead of hanging); it transitions to `HALF_OPEN` after a cooldown and back to `CLOSED` once calls succeed again.
