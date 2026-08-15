# EHMS .NET 10 System Testing

This folder is a **separate .NET 10 test project** for the supplied Enterprise Hotel Management System.

It does **not** replace or rewrite the React frontend or Spring Boot microservices.

It tests them externally:

- React frontend: `http://localhost:5173`
- API Gateway: `http://localhost:8080`
- Eureka: `http://localhost:8761`
- Spring Boot services: ports `8081`–`8090` and AuthService `8099`

## What is covered

### Direct service health
- API Gateway
- UserService
- HotelService
- RatingService
- BookingService
- PaymentService
- NotificationService
- EmployeeService
- InvoiceService
- RoomService
- AI Assistant
- AuthService
- Eureka dashboard

### API Gateway routing
- `/users`
- `/hotels`
- `/rooms`
- `/ratings`
- `/bookings`
- `/payments`
- `/notifications`
- `/employees`
- `/departments`
- `/invoices`
- `/hotelservices`
- `/roomservicerequests`
- `/api/ai/test`

### JWT authentication
- registration
- login
- JWT returned
- GUEST role returned
- `/api/v1/auth/validate`
- `401` without JWT for `/users`, `/bookings`, `/payments`
- success with valid JWT
- invalid JWT rejection

### Booking/Payment integration
- create booking
- fetch BookingService payment details
- optional Razorpay Test Mode order creation

### Spring AI
- AI service health/test endpoint
- optional real `/api/ai/chat` call through the Gateway

### React + Playwright
- landing page
- public Explore page
- Vite `/gateway` proxy actually reaches `/hotels` and `/rooms`
- anonymous `/app` redirects to `/login`
- registration through the React form
- successful redirect to dashboard
- invalid login remains on login page

## Requirements

- .NET 10 SDK
- PowerShell 7 (`pwsh`)
- All Spring Boot services running
- Eureka running
- API Gateway running
- React/Vite frontend running
- MySQL/PostgreSQL/MongoDB services required by your microservices
- Ollama only when the optional AI chat test is enabled

## Start your existing project first

Backend expected URLs:

- Eureka: `http://localhost:8761`
- Gateway: `http://localhost:8080`
- AuthService: `http://localhost:8099`

Frontend:

```powershell
cd Frontend
npm run dev
```

Expected frontend URL:

`http://localhost:5173`

## First run

From this test folder:

```powershell
dotnet --version
```

It must start with `10.`.

Then:

```powershell
.\run-tests.ps1
```

The script:

1. restores NuGet packages
2. builds the .NET tests
3. installs Playwright Chromium
4. runs the complete test suite

## Manual commands

```powershell
dotnet restore
dotnet build
pwsh .\bin\Debug\net10.0\playwright.ps1 install chromium
dotnet test --settings .runsettings
```

## Run only API tests

```powershell
dotnet test --filter "FullyQualifiedName~Tests.Api"
```

## Run only UI tests

```powershell
dotnet test --filter "FullyQualifiedName~Tests.Ui"
```

## Optional real Spring AI chat test

The default suite checks that AI-ASSISTANT-SERVICE is reachable without forcing a long LLM call.

To test Ollama + Spring AI + live hotel tools:

```powershell
$env:EHMS_RUN_AI_CHAT = "true"
dotnet test --filter "FullyQualifiedName~AiIntegrationTests"
```

Make sure Ollama and your configured model are running first.

## Optional Razorpay integration test

The default suite tests PaymentService and BookingService without creating an external Razorpay order.

To enable the Razorpay Test Mode order test:

```powershell
$env:EHMS_RUN_RAZORPAY = "true"
dotnet test --filter "FullyQualifiedName~BookingPaymentIntegrationTests"
```

Use only Razorpay Test Mode credentials.

## Change URLs

Defaults:

```text
EHMS_GATEWAY_URL=http://localhost:8080
EHMS_FRONTEND_URL=http://localhost:5173
```

Override when required:

```powershell
$env:EHMS_GATEWAY_URL = "http://localhost:8080"
$env:EHMS_FRONTEND_URL = "http://localhost:5173"
dotnet test
```

## Important

These are **system/integration/end-to-end tests**.

They intentionally test the React and Spring Boot applications as running black-box applications. There is no C# project reference to the Java or React source code, which is exactly what allows the .NET 10 test project to stay compatible with both technologies.
