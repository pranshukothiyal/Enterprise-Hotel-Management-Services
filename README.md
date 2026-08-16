# StayOps - Enterprise Hotel Management Services (EHMS)

An enterprise-grade, distributed hotel management ecosystem designed with a microservices architecture. Built using Spring Boot, React + Vite, and .NET automated testing, the platform provides seamless room discovery, booking orchestration, JWT-authenticated role management, Razorpay payment verification, and an intelligent AI Assistant.

---

## Architecture Overview

```text
[ Frontend (React + Vite + Tailwind) ]
                  │
                  ▼
       [ API Gateway :8080 ] ─── (Spring Cloud Gateway + JWT Security)
                  │
       ┌──────────┴─────────────────────────┐
       ▼                                    ▼
[ Service Discovery :8761 ]        [ Microservices Network ]
(Netflix Eureka Server)           ├── AuthService (:8081)
                                  ├── UserService (:8082)
                                  ├── HotelService (:8083)
                                  ├── BookingService (:8084)
                                  ├── RatingService (:8085)
                                  ├── PaymentService (:8086)
                                  ├── NotificationService (:8087)
                                  ├── EmployeeService (:8088)
                                  ├── InvoiceService (:8089)
                                  ├── RoomService (:8090)
                                  └── AIAssistant (:8091)
```

---

## Project Snapshots

### 1. Platform Landing Page
![StayOps Landing Page](Screenshot%202026-08-16%20050912.png)

### 2. Role-Based Access & Security
**Admin Registration Workspace**
![Admin Registration](Screenshot%202026-08-16%20050950.png)

**Secure Sign-In**
![Admin Login](Screenshot%202026-08-16%20050959.png)

### 3. Core Management Dashboards
**Bookings Management**
![Bookings Dashboard](Screenshot%202026-08-16%20051007.png)

**Rooms Inventory**
![Rooms Dashboard](Screenshot%202026-08-16%20051015.png)

**Feedback & Ratings**
![Ratings Dashboard](Screenshot%202026-08-16%20051026.png)

**Guest Directory**
![Guests Dashboard](Screenshot%202026-08-16%20051039.png)

### 4. Intelligence
**Live Tool-Enabled AI Assistant**
![AI Assistant](Screenshot%202026-08-16%20051052.png)

---

## Tech Stack

| Layer | Technologies |
|---|---|
| **Frontend** | React 18, Vite, Tailwind CSS, Lucide React, Axios, React Router DOM |
| **Backend** | Java 21, Spring Boot 3.x, Spring Cloud Gateway, Netflix Eureka, Spring Security + JWT, OpenFeign |
| **Integrations** | Razorpay Payment Gateway, Spring AI / LLM Tool Calling |
| **Databases** | MySQL, PostgreSQL |
| **Testing** | .NET 10 xUnit Suite, Playwright UI Tests, RestSharp, FluentAssertions |

---

## Microservices Breakdown

| Service | Port | Description |
|---|---|---|
| **EurekaServer** | `8761` | Dynamic service discovery and registry |
| **ApiGateway** | `8080` | Central entry point, route forwarding, JWT validation |
| **AuthService** | `8081` | User registration, authentication, JWT token issuance |
| **UserService** | `8082` | User profiles, role assignments, rating aggregation |
| **HotelService** | `8083` | Hotel metadata, room configurations, availability |
| **BookingService** | `8084` | Reservation management, booking state machine |
| **RatingService** | `8085` | Hotel reviews, feedback ratings, user testimonials |
| **PaymentService** | `8086` | Razorpay order generation, signature verification, payment logging |
| **NotificationService** | `8087` | Automated transactional alerts (Email / SMS) |
| **EmployeeService** | `8088` | Staff directories, shifts, department management |
| **InvoiceService** | `8089` | Billing calculation, PDF invoice compilation |
| **RoomService** | `8090` | In-room dining, maintenance, and service requests |
| **AIAssistant** | `8091` | LLM-powered hotel recommendation and booking assistant |

---

## Getting Started

### Prerequisites
* Java JDK 21
* IntelliJ IDE
* Node.js 18+ and npm
* .NET 10 SDK (for running test suites)
* MySQL Server
* Postgress
* MongoDb

---

### 1. Backend Setup

1. Configure database credentials in `backend/{service-name}/src/main/resources/application.yml`.
2. Start the core discovery registry first:
   ```bash
   cd backend/EurekaServer
   ./mvnw spring-boot:run
   ```
3. Start the API Gateway:
   ```bash
   cd backend/ApiGateway
   ./mvnw spring-boot:run
   ```
4. Run the remaining microservices (`AuthService`, `HotelService`, `BookingService`, etc.) in separate terminals or directly through your IDE run dashboard.

---

### 2. Frontend Setup

```bash
cd frontend
npm install
npm run dev
```
The client dashboard will be available at `http://localhost:5173`.

---

### 3. Automated .NET Testing

Run the full API and UI integration suite from the `.Net Testing` folder:

```powershell
cd ".Net Testing"
dotnet test
```

To execute browser-based Playwright smoke tests in headed mode:
```powershell
./run-ui-headed.ps1
```

---

## Key Features

* **Resilient Microservice Mesh:** Decentralized discovery via Netflix Eureka with declarative inter-service REST clients using OpenFeign.
* **Role-Based Access Control (RBAC):** Stateless JWT authentication securing admin, manager, employee, and guest workflows.
* **Payment Processing:** Full payment lifecycle integration using Razorpay with cryptographic signature verification.
* **AI Hotel Concierge:** Context-aware booking and room recommendation tool built into the assistant service.
* **End-to-End Test Automation:** Cross-platform .NET 10 test suite covering API contracts, security flows, and browser interactions.

---

## Author
**Pranshu**
