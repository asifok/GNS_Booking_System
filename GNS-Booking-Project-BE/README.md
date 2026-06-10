# GNS Booking System

A **Spring Boot** REST API for seat booking with OTP-based authentication, Redis-backed seat locking, email notifications via Brevo (Sendinblue), and admin approval workflows. Deployable via Docker with AWS CI/CD support.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Setup](#local-setup)
  - [Running with Docker](#running-with-docker)
- [Configuration](#configuration)
- [Booking Flow](#booking-flow)
- [Admin Features](#admin-features)
- [Deployment](#deployment)

---

## Features

- **OTP-based login** — passwordless authentication via email OTP (stored in Redis, 5-minute expiry)
- **Seat booking** — book up to 5 seats per transaction with real-time availability checks
- **Redis seat locking** — prevents double-booking by locking seats during the booking process
- **Admin approval** — admins confirm or reject bookings and update payment status
- **Seat cancellation** — admins can cancel individual seats and trigger refund notifications
- **Email notifications** — booking confirmations, ticket delivery, and cancellation emails via Brevo SMTP API
- **Swagger UI** — interactive API documentation at `/swagger-ui.html`
- **Multi-profile config** — separate `dev` and `cicd` (production) Spring profiles
- **Docker support** — multi-stage Dockerfile for lean production images
- **AWS CodeBuild** — `buildspec.yaml` for CI/CD pipeline integration

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Database | PostgreSQL |
| Cache / Locking | Redis (Spring Data Redis + Lettuce) |
| ORM | Spring Data JPA / Hibernate |
| Email | Brevo (Sendinblue) SMTP API |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Build Tool | Maven |
| Container | Docker (multi-stage build) |
| CI/CD | AWS CodeBuild |

---

## Architecture Overview

```
Client
  │
  ├── POST /user/send-otp        → Generate OTP → Store in Redis → Send via Brevo
  ├── POST /user/verify-otp      → Validate OTP from Redis → Return admin flag
  │
  ├── GET  /seat/all/get         → Fetch all seats with status & pricing
  │
  ├── POST /booking/save         → Validate seats → Lock in Redis → Save booking (PAYMENT_PENDING)
  ├── GET  /booking/tickets/all/get       → User's booking history
  ├── GET  /booking/tickets/admin/all/get → All bookings (admin only)
  ├── POST /booking/confirm      → Admin approves/rejects → Marks PAID → Sends ticket email
  └── POST /booking/cancel-seats → Admin cancels seats → Updates totals → Sends cancellation emails
```

---

## Project Structure

```
src/main/java/com/gnsbooking/app/
├── config/
│   └── RedisConfig.java              # RedisTemplate bean configuration
├── controller/
│   ├── UserController.java           # OTP send & verify endpoints
│   ├── SeatController.java           # Seat availability endpoint
│   └── BookingController.java        # Booking CRUD & admin endpoints
├── dto/
│   ├── BookingRequest.java
│   ├── ConfirmBookingRequest.java
│   ├── CancelSeatsRequest.java
│   ├── SeatResponse.java
│   ├── TicketResponse.java
│   ├── AllTicketResponse.java
│   ├── SendOtpRequest.java
│   └── VerifyOtpRequest.java
├── entity/
│   ├── UserEntity.java               # User (email + isAdmin flag)
│   ├── SeatEntity.java               # Seat (number, row, section, price, status)
│   ├── BookingEntity.java            # Booking (user, amount, status, approval)
│   └── BookingSeatEntity.java        # Booking–Seat join table
├── repository/
│   ├── UserRepository.java
│   ├── SeatRepository.java
│   ├── BookingRepository.java
│   └── BookingSeatRepository.java
├── serviceI/                         # Service interfaces
├── serviceImpl/
│   ├── UserServiceImpl.java          # OTP logic
│   ├── BookingServiceImpl.java       # Core booking logic
│   ├── SeatServiceImpl.java
│   └── EmailService.java             # Brevo email integration
└── utility/
    └── EmailTemplate.java            # HTML email templates
```

---

## API Endpoints

### User (Authentication)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/user/send-otp` | Send OTP to the given email |
| POST | `/user/verify-otp` | Verify OTP and login (returns `true` if admin) |

**Send OTP request body:**
```json
{ "email": "user@example.com" }
```

**Verify OTP request body:**
```json
{ "email": "user@example.com", "otp": "123456" }
```

---

### Seats

| Method | Endpoint | Description |
|---|---|---|
| GET | `/seat/all/get` | Get all seats with number, section, price, and status |

---

### Bookings

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/booking/save` | User | Create a booking (max 5 seats) |
| GET | `/booking/tickets/all/get?email=` | User | Get logged-in user's bookings |
| GET | `/booking/tickets/admin/all/get?email=` | Admin | Get all bookings |
| POST | `/booking/confirm` | Admin | Approve or reject a booking |
| POST | `/booking/cancel-seats` | Admin | Cancel specific seats in a booking |

**Create booking request body:**
```json
{
  "email": "user@example.com",
  "seatNmbrs": ["A1", "A2", "B3"]
}
```

**Confirm booking request body:**
```json
{
  "email": "admin@example.com",
  "bkngId": 101,
  "isAprvd": true,
  "pymntMthd": "CASH"
}
```

**Cancel seats request body:**
```json
{
  "email": "admin@example.com",
  "bkngId": 101,
  "seatNmbrs": ["A1"]
}
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL (create a database named `gns_db`)
- Redis (running on default port `6379`)
- Brevo account for email (free tier works)

---

### Local Setup

**1. Clone the repository**
```bash
git clone https://github.com/your-username/GNS-Booking-Project.git
cd GNS-Booking-Project
```

**2. Configure `application-dev.properties`**

Edit `src/main/resources/application-dev.properties` with your local credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gns_db
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.data.redis.host=localhost
spring.data.redis.port=6379

brevo.api.key=your_brevo_api_key
```

**3. Set active profile to `dev`**

In `application.properties`:
```properties
spring.profiles.active=dev
```

**4. Run the application**
```bash
mvn spring-boot:run
```

The server starts on **port 9090** (dev profile).

**5. Open Swagger UI**

Navigate to: [http://localhost:9090/swagger-ui.html](http://localhost:9090/swagger-ui.html)

---

### Running with Docker

**Build the image:**
```bash
docker build -t gns-booking-app .
```

**Run the container (production/cicd profile):**
```bash
docker run -p 8080:8080 \
  -e APP_ENV=cicd \
  -e BREVO_API_KEY=your_brevo_key \
  gns-booking-app
```

> The `cicd` profile connects to an externally configured PostgreSQL (e.g. AWS RDS) and Redis instance. Set DB credentials in `application-cicd.properties` or inject them as environment variables.

---

## Configuration

### Profiles

| Profile | Port | Database | Redis | Purpose |
|---|---|---|---|---|
| `dev` | 9090 | localhost | localhost | Local development |
| `cicd` | 8080 | AWS RDS | RedisLabs | CI/CD / Production |

The active profile is controlled by the `APP_ENV` environment variable (defaults to `cicd`):

```properties
# application.properties
spring.profiles.active=${APP_ENV:cicd}
```

### Environment Variables (cicd/production)

| Variable | Description |
|---|---|
| `APP_ENV` | Spring profile to activate (`dev` or `cicd`) |
| `BREVO_API_KEY` | Brevo (Sendinblue) API key for sending emails |

---

## Booking Flow

```
1. User sends OTP  →  OTP stored in Redis (5 min TTL)
2. User verifies OTP  →  Account created on first login
3. User calls /booking/save
      └── Validates seat availability (DB + Redis lock check)
      └── Locks each seat in Redis (5 min TTL)
      └── Creates booking record (status: PAYMENT_PENDING)
      └── Sends booking confirmation email
4. Admin views bookings via /booking/tickets/admin/all/get
5. Admin confirms via /booking/confirm
      └── If approved  →  status: PAID  →  ticket email sent to user
      └── If rejected  →  status: PAYMENT_PENDING
6. Admin can cancel seats via /booking/cancel-seats
      └── Removes seat from booking
      └── Updates total amount
      └── Resets seat status to AVAILABLE
      └── Sends cancellation emails to user and admin
```

---

## Admin Features

A user is flagged as admin via the `isAdmin` boolean on `UserEntity`. There is no self-registration for admins — this must be set directly in the database.

**Grant admin access (SQL):**
```sql
UPDATE user_entity SET is_admin = true WHERE email = 'admin@yourdomain.com';
```

---

## Deployment

### AWS CodeBuild

A `buildspec.yaml` is included for AWS CodeBuild. It runs `mvn clean package` and outputs the JAR:

```
target/otp-login-0.0.1-SNAPSHOT.jar
```

### Suggested Deployment Stack

- **App**: AWS EC2 or ECS (Docker)
- **Database**: AWS RDS (PostgreSQL)
- **Cache**: Redis (RedisLabs or AWS ElastiCache)
- **CI/CD**: AWS CodeBuild → CodePipeline → ECR/ECS

---

## Notes

- Seats must be pre-populated in the `seat_entity` table. The API does not provide a seat creation endpoint.
- The booking system does not currently implement JWT authentication. OTP verification returns an admin flag, and all subsequent requests pass the user's email directly in the request body.
- `@CrossOrigin("*")` is enabled on all controllers — restrict this to your frontend domain in production.

---

*Built with Spring Boot 3.2.5 · Java 17 · PostgreSQL · Redis · Brevo*
