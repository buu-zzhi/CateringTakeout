<<<<<<< HEAD
# CangqiongTakeout

## Overview
CangqiongTakeout is a modular Spring Boot 3.x project that implements the backend for a takeout platform. The Maven aggregator in `parent/` manages three modules:

- `common/` – cross-cutting constants, context utilities, JWT helpers, and HTTP/JSON utilities that are reused across services.
- `pojo/` – domain entities, DTO/VO projections, and generic API response wrappers shared between services and clients.
- `Server/` – the main web application exposing admin and user APIs, background tasks, and integrations (database, Redis cache, OSS, WebSocket, etc.).

## Module layout
| Module | Highlights |
| --- | --- |
| `parent` | Inherits from `spring-boot-starter-parent` 3.5.7, enforces Java 22 compilation, and declares common dependencies such as Swagger/Knife4j, Redis starter, HttpClient, and Fastjson (`parent/pom.xml`). |
| `common` | Adds JJWT libraries and provides shared code like `BaseContext`, constants, custom Jackson mapper, exception hierarchy, and helper utilities under `com.example` (`common/src/main/java`). |
| `pojo` | Houses all business DTO/entity/VO classes (orders, dishes, set meals, statistics, etc.) plus standardized `Result` and `PageResult` wrappers (`pojo/src/main/java/com/example`). |
| `Server` | Spring Boot service layer with controllers, services, mappers, configuration, scheduled tasks, and integration adapters (`Server/src/main/java/com/example`). |

## Tech stack
- Java 22 toolchain (module parent) with Spring Boot 3.5.7; `Server` sets `java.version` to 21 for runtime compatibility.
- Persistence via MyBatis (XML mappers plus mapper interfaces), PageHelper for pagination, and MySQL Connector/J.
- Redis cache with custom `RedisTemplate` configuration plus Spring Cache annotations enabled in `ServerApplication`.
- JWT authentication powered by shared `JwtUtils` and injected properties (`sky.jwt.*`), enforced through separate admin/user interceptors.
- Knife4j + Swagger annotations for API documentation.
- Aliyun OSS integration for media uploads and Apache POI for Excel exports.
- WebSocket endpoints (`WebSocketServer`) for pushing order status updates; scheduling infrastructure available for timed tasks such as `OrderTask`.

## Server module architecture
- **Entry point**: `ServerApplication` bootstraps the service, enabling Spring Cache (scheduling can be toggled when cron jobs are needed).
- **Configuration**: `WebConfig` registers message converters and JWT interceptors; `RedisConfiguration` tunes Redis serialization; `WebSocketConfig` exposes the `/ws/{sid}` endpoint; properties classes bind `sky.jwt` and `sky.wechat` settings.
- **Controllers**:
  - `controller/admin`: management APIs for categories, dishes, set meals, employees, orders, reports, shop toggle, OSS upload, and workspace dashboards.
  - `controller/user`: customer-facing endpoints for catalog browsing, shopping cart, address book, and order submission.
  - `controller/notify`: payment callback handling.
- **Services & business logic**: Interfaces under `service/` with `impl/` implementations orchestrate validation, caching, and persistence. `OrderTask` handles payment timeout and delivery auto-completion. `WebSocketServer` broadcasts notifications.
- **Persistence layer**: MyBatis mapper interfaces (`mapper/`) and their XML statements (`src/main/resources/com/example/mapper`) encapsulate SQL for all aggregates.
- **Exception & cross-cutting support**: `GlobalExceptionHandler`, interceptors, and shared constants ensure consistent error handling and auditing.

## Configuration highlights (`Server/src/main/resources/application.yml`)
- Defines the `spring.datasource` connection to `sky_take_out` MySQL and configures multipart upload limits.
- Configures Redis host/database plus MyBatis settings (camel-case mapping and SQL logging).
- Provides `sky.jwt.*` secrets, TTLs, and header names for both admin and user auth flows.
- Stores WeChat mini-app `appid` and `secret` for login/payment plus Aliyun OSS credentials/endpoint used during media uploads.

## Tests and utilities
JUnit test classes under `Server/src/test/java/com/example/test` cover JWT generation, HTTP client utilities, Redis connectivity, OSS/POI helpers, and other shared behaviors to validate integrations outside the main runtime.

## Build & run
1. `cd parent`
2. `mvn clean install` – builds and installs `common` and `pojo` so the server can depend on them.
3. `cd ../Server`
4. `mvn spring-boot:run` (or `mvn clean package` followed by `java -jar target/Server-0.0.1-SNAPSHOT.jar`)

Ensure MySQL, Redis, WeChat credentials, and Aliyun OSS settings in `application.yml` are aligned with your environment before launching.

## Directory snapshot
```
CangqiongTakeout
├── parent/          # Maven aggregator POM
├── common/          # Shared constants, context, utilities
├── pojo/            # DTO/VO/entity definitions
└── Server/          # Spring Boot backend service
```
=======
# CangQiongTakeout
外卖管理平台项目 CangQiongTakeout
>>>>>>> 86555021aa0510856faad0cd2ca6fcc5ed41f196
