import http from './http'

const data = (request) =>
  request.then((response) => response.data)


// ============================================================
// AUTH
// ============================================================

export const authApi = {

  login: (payload) =>
    data(
      http.post(
        '/api/v1/auth/authenticate',
        payload
      )
    ),

  register: (payload) =>
    data(
      http.post(
        '/api/v1/auth/register',
        payload
      )
    ),

  validate: () =>
    data(
      http.get(
        '/api/v1/auth/validate'
      )
    ),
}


// ============================================================
// USERS
// ============================================================

export const usersApi = {

  list: () =>
    data(
      http.get('/users')
    ),

  get: (id) =>
    data(
      http.get(`/users/${id}`)
    ),

  create: (payload) =>
    data(
      http.post(
        '/users',
        payload
      )
    ),
}


// ============================================================
// HOTELS
// ============================================================

export const hotelsApi = {

  list: () =>
    data(
      http.get('/hotels')
    ),

  get: (id) =>
    data(
      http.get(`/hotels/${id}`)
    ),

  create: (payload) =>
    data(
      http.post(
        '/hotels',
        payload
      )
    ),
}


// ============================================================
// ROOMS
// ============================================================

export const roomsApi = {

  list: () =>
    data(
      http.get('/rooms')
    ),

  get: (id) =>
    data(
      http.get(`/rooms/${id}`)
    ),

  byHotel: (hotelId) =>
    data(
      http.get(
        `/rooms/hotels/${hotelId}`
      )
    ),

  byHotelStatus: (
    hotelId,
    status
  ) =>
    data(
      http.get(
        `/rooms/hotels/${hotelId}/status/${status}`
      )
    ),

  byHotelType: (
    hotelId,
    roomType
  ) =>
    data(
      http.get(
        `/rooms/hotels/${hotelId}/type/${roomType}`
      )
    ),

  create: (
    hotelId,
    payload
  ) =>
    data(
      http.post(
        `/rooms/hotels/${hotelId}`,
        payload
      )
    ),

  update: (
    id,
    payload
  ) =>
    data(
      http.put(
        `/rooms/${id}`,
        payload
      )
    ),

  updateStatus: (
    id,
    status
  ) =>
    data(
      http.patch(
        `/rooms/${id}/status/${status}`
      )
    ),

  remove: (id) =>
    data(
      http.delete(
        `/rooms/${id}`
      )
    ),
}


// ============================================================
// BOOKINGS
// ============================================================

export const bookingsApi = {

  list: () =>
    data(
      http.get('/bookings')
    ),

  get: (id) =>
    data(
      http.get(`/bookings/${id}`)
    ),

  byUser: (userId) =>
    data(
      http.get(
        `/bookings/users/${userId}`
      )
    ),

  byHotel: (hotelId) =>
    data(
      http.get(
        `/bookings/hotels/${hotelId}`
      )
    ),

  paymentDetails: (id) =>
    data(
      http.get(
        `/bookings/${id}/payment-details`
      )
    ),

  create: (payload) =>
    data(
      http.post(
        '/bookings',
        payload
      )
    ),

  update: (
    id,
    payload
  ) =>
    data(
      http.put(
        `/bookings/${id}`,
        payload
      )
    ),

  remove: (id) =>
    data(
      http.delete(
        `/bookings/${id}`
      )
    ),
}


// ============================================================
// RATINGS
// ============================================================

export const ratingsApi = {

  list: () =>
    data(
      http.get('/ratings')
    ),

  byUser: (userId) =>
    data(
      http.get(
        `/ratings/users/${userId}`
      )
    ),

  byHotel: (hotelId) =>
    data(
      http.get(
        `/ratings/hotels/${hotelId}`
      )
    ),

  create: (payload) =>
    data(
      http.post(
        '/ratings',
        payload
      )
    ),
}


// ============================================================
// DEPARTMENTS
// ============================================================

export const departmentsApi = {

  list: () =>
    data(
      http.get('/departments')
    ),

  get: (id) =>
    data(
      http.get(
        `/departments/${id}`
      )
    ),

  byHotel: (hotelId) =>
    data(
      http.get(
        `/departments/hotel/${hotelId}`
      )
    ),

  create: (payload) =>
    data(
      http.post(
        '/departments',
        payload
      )
    ),

  update: (
    id,
    payload
  ) =>
    data(
      http.put(
        `/departments/${id}`,
        payload
      )
    ),

  remove: (id) =>
    data(
      http.delete(
        `/departments/${id}`
      )
    ),
}


// ============================================================
// EMPLOYEES
// ============================================================

export const employeesApi = {

  list: () =>
    data(
      http.get('/employees')
    ),

  get: (id) =>
    data(
      http.get(
        `/employees/${id}`
      )
    ),

  byDepartment: (departmentId) =>
    data(
      http.get(
        `/employees/department/${departmentId}`
      )
    ),

  byHotel: (hotelId) =>
    data(
      http.get(
        `/employees/hotel/${hotelId}`
      )
    ),

  byStatus: (status) =>
    data(
      http.get(
        `/employees/status/${status}`
      )
    ),

  create: (payload) =>
    data(
      http.post(
        '/employees',
        payload
      )
    ),

  update: (
    id,
    payload
  ) =>
    data(
      http.put(
        `/employees/${id}`,
        payload
      )
    ),

  updateStatus: (
    id,
    status
  ) =>
    data(
      http.patch(
        `/employees/${id}/status`,
        null,
        {
          params: {
            status
          }
        }
      )
    ),

  remove: (id) =>
    data(
      http.delete(
        `/employees/${id}`
      )
    ),
}


// ============================================================
// HOTEL SERVICES
// ============================================================

export const offeringsApi = {

  list: () =>
    data(
      http.get('/hotelservices')
    ),

  get: (id) =>
    data(
      http.get(
        `/hotelservices/${id}`
      )
    ),

  byHotel: (hotelId) =>
    data(
      http.get(
        `/hotelservices/hotel/${hotelId}`
      )
    ),

  availableByHotel: (hotelId) =>
    data(
      http.get(
        `/hotelservices/hotel/${hotelId}/available`
      )
    ),

  create: (payload) =>
    data(
      http.post(
        '/hotelservices',
        payload
      )
    ),

  update: (
    id,
    payload
  ) =>
    data(
      http.put(
        `/hotelservices/${id}`,
        payload
      )
    ),

  updateStatus: (
    id,
    status
  ) =>
    data(
      http.patch(
        `/hotelservices/${id}/status`,
        null,
        {
          params: {
            status
          }
        }
      )
    ),

  remove: (id) =>
    data(
      http.delete(
        `/hotelservices/${id}`
      )
    ),
}


// ============================================================
// ROOM SERVICE REQUESTS
// ============================================================

export const roomRequestsApi = {

  list: () =>
    data(
      http.get(
        '/roomservicerequests'
      )
    ),

  get: (id) =>
    data(
      http.get(
        `/roomservicerequests/${id}`
      )
    ),

  byUser: (userId) =>
    data(
      http.get(
        `/roomservicerequests/user/${userId}`
      )
    ),

  byBooking: (bookingId) =>
    data(
      http.get(
        `/roomservicerequests/booking/${bookingId}`
      )
    ),

  byRoom: (roomId) =>
    data(
      http.get(
        `/roomservicerequests/room/${roomId}`
      )
    ),

  byEmployee: (employeeId) =>
    data(
      http.get(
        `/roomservicerequests/employee/${employeeId}`
      )
    ),

  byStatus: (status) =>
    data(
      http.get(
        `/roomservicerequests/status/${status}`
      )
    ),

  create: (payload) =>
    data(
      http.post(
        '/roomservicerequests',
        payload
      )
    ),

  assign: (
    id,
    employeeId
  ) =>
    data(
      http.patch(
        `/roomservicerequests/${id}/assign/${employeeId}`
      )
    ),

  updateStatus: (
    id,
    status
  ) =>
    data(
      http.patch(
        `/roomservicerequests/${id}/status`,
        null,
        {
          params: {
            status
          }
        }
      )
    ),

  cancel: (id) =>
    data(
      http.patch(
        `/roomservicerequests/${id}/cancel`
      )
    ),

  remove: (id) =>
    data(
      http.delete(
        `/roomservicerequests/${id}`
      )
    ),
}


// ============================================================
// INVOICES
// ============================================================

export const invoicesApi = {

  list: (params = {}) =>
    data(
      http.get(
        '/invoices',
        {
          params
        }
      )
    ),

  get: (id) =>
    data(
      http.get(
        `/invoices/${id}`
      )
    ),

  create: (payload) =>
    data(
      http.post(
        '/invoices',
        payload
      )
    ),
}


// ============================================================
// PAYMENTS
// ============================================================

export const paymentsApi = {

  list: () =>
    data(
      http.get('/payments')
    ),

  get: (id) =>
    data(
      http.get(
        `/payments/${id}`
      )
    ),

  byBooking: (bookingId) =>
    data(
      http.get(
        `/payments/booking/${bookingId}`
      )
    ),

  createOrder: (bookingId) =>
    data(
      http.post(
        '/payments/razorpay/orders',
        {
          bookingId
        }
      )
    ),

  verify: (payload) =>
    data(
      http.post(
        '/payments/razorpay/verify',
        payload
      )
    ),
}


// ============================================================
// NOTIFICATIONS
// ============================================================

export const notificationsApi = {

  list: (userId) =>
    data(
      http.get(
        '/notifications',
        {
          params:
            userId
              ? { userId }
              : {}
        }
      )
    ),

  create: (payload) =>
    data(
      http.post(
        '/notifications',
        payload
      )
    ),

  markRead: (id) =>
    data(
      http.patch(
        `/notifications/${id}/read`
      )
    ),

  markAllRead: () =>
    data(
      http.patch(
        '/notifications/read-all'
      )
    ),
}


// ============================================================
// AI
// ============================================================

export const aiApi = {

  chat: (message) =>
    data(
      http.post(
        '/api/ai/chat',
        {
          message
        }
      )
    ),

  ask: (message) =>
    data(
      http.get(
        '/api/ai/ask',
        {
          params: {
            message
          }
        }
      )
    ),

  health: () =>
    data(
      http.get(
        '/api/ai/test'
      )
    ),

  debugHotels: () =>
    data(
      http.get(
        '/api/ai/debug/hotels'
      )
    ),

  debugRooms: () =>
    data(
      http.get(
        '/api/ai/debug/rooms'
      )
    ),
}