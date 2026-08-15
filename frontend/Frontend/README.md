# StayOps Enterprise Frontend

Industry-style frontend for the Enterprise Hotel Management System microservices project.

## Stack

- React (JavaScript only)
- Vite
- Tailwind CSS
- React Router DOM
- Axios
- Lucide React
- React Hot Toast
- Razorpay Checkout

## Run locally

1. Start Eureka Server and all required backend microservices.
2. Start `ApiGateway` on port `8080`.
3. Open a terminal in the `Frontend` folder.
4. Run:

```bash
npm install
npm run dev
```

Open `http://localhost:5173`.

Vite proxies `/gateway/*` to `http://localhost:8080/*`, so local development does not require browser CORS changes.

## Production API URL

Copy `.env.example` to `.env` and set:

```env
VITE_API_BASE_URL=https://your-api-gateway-domain.com
```

Then build:

```bash
npm run build
```

The production files are generated in `dist/`.

## Implemented frontend modules and API coverage

### Authentication
- Register
- Login
- JWT persistence
- Token validation
- Automatic logout on `401`

### Users
- Create user
- List users
- View user details

### Hotels
- Public hotel directory
- Create hotel
- List hotels
- View hotel details

### Rooms
- Create, list, view, edit and delete rooms
- Query by hotel
- Query by hotel + room status
- Query by hotel + room type
- Update room status

### Bookings
- Create, list, view, edit and delete bookings
- Query by user
- Query by hotel
- Retrieve payment details

### Ratings
- Create and list ratings
- Query by user or hotel

### Departments and employees
- Full department CRUD
- Department filtering by hotel
- Full employee CRUD
- Filter employees by department, hotel or status
- Update employee status

### Payments
- Create Razorpay order
- Open Razorpay Checkout
- Verify payment signature
- Manual verification form
- List payments
- Lookup by payment ID or booking ID

### Invoices
- Generate invoice
- List invoices
- Query by user or booking
- View invoice details

### Notifications
- Create and list notifications
- Filter by user
- Mark one as read
- Mark all as read

### Room service
- Full hotel-service-offering CRUD
- Filter services by hotel and availability
- Update service availability
- Create and list room-service requests
- Filter requests by user, booking, room, employee or status
- Assign an employee
- Update request status
- Cancel and delete a request

### AI assistant
- POST chat endpoint
- GET ask endpoint (used by supported chat flow)
- AI service health test
- Debug live hotel tool data
- Debug live room tool data

## Important integration fix

The original API Gateway configuration did not route `/rooms` to HotelService. The project ZIP includes that missing route so the room frontend can use every implemented RoomController endpoint through port `8080`.

## Razorpay webhook

`POST /payments/razorpay/webhook` is intentionally not called by browser code. It is a server-to-server callback that Razorpay invokes using `X-Razorpay-Signature`; exposing a browser control for it would be incorrect and insecure.
