export const ROLES = {
  ADMIN: 'ADMIN',
  MANAGER: 'HOTEL_MANAGER',
  EMPLOYEE: 'EMPLOYEE',
  GUEST: 'GUEST'
}


// ============================================================
// ROLE OPTIONS
// ============================================================

export const ROLE_OPTIONS = [
  {
    value: ROLES.ADMIN,
    label: 'Admin',
    description: 'Complete platform administration.'
  },
  {
    value: ROLES.MANAGER,
    label: 'Hotel Manager',
    description: 'Manage the assigned hotel and its operations.'
  },
  {
    value: ROLES.EMPLOYEE,
    label: 'Employee',
    description: 'Access operational information required for work.'
  },
  {
    value: ROLES.GUEST,
    label: 'Guest',
    description: 'Book rooms and manage your own stay.'
  }
]


// ============================================================
// PAGE ACCESS
// ============================================================

export const pageRoles = {

  dashboard: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.EMPLOYEE,
    ROLES.GUEST
  ],


  // Guest can browse hotels.
  // Employee does not need hotel administration.
  hotels: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.GUEST
  ],


  rooms: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.EMPLOYEE,
    ROLES.GUEST
  ],


  // Employee doesn't need booking management.
  bookings: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.GUEST
  ],


  ratings: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.GUEST
  ],


  users: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],


  departments: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],


  employees: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],


  // Removed from Guest and Employee.
  hotelServices: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],


  // Removed from Guest and Employee.
  serviceRequests: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],


  payments: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.GUEST
  ],


  invoices: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.GUEST
  ],


  notifications: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.EMPLOYEE,
    ROLES.GUEST
  ],


  ai: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.EMPLOYEE,
    ROLES.GUEST
  ]
}


// ============================================================
// ACTION PERMISSIONS
// ============================================================

export const permissions = {

  // ---------------- HOTELS ----------------

  hotelCreate: [
    ROLES.ADMIN
  ],

  hotelEdit: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  hotelDelete: [
    ROLES.ADMIN
  ],


  // ---------------- ROOMS ----------------

  roomCreate: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  roomEdit: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  roomDelete: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  roomStatusUpdate: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.EMPLOYEE
  ],


  // ---------------- BOOKINGS ----------------

  bookingCreate: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.GUEST
  ],

  bookingEdit: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  bookingDelete: [
    ROLES.ADMIN
  ],

  bookingCancel: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.GUEST
  ],


  // ---------------- RATINGS ----------------

  ratingCreate: [
    ROLES.GUEST
  ],


  // ---------------- DEPARTMENTS ----------------

  departmentCreate: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  departmentEdit: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  departmentDelete: [
    ROLES.ADMIN
  ],


  // ---------------- EMPLOYEES ----------------

  employeeCreate: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  employeeEdit: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  employeeDelete: [
    ROLES.ADMIN
  ],

  employeeStatusUpdate: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],


  // ---------------- HOTEL SERVICES ----------------

  serviceCreate: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  serviceEdit: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  serviceDelete: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  serviceStatusUpdate: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],


  // ---------------- SERVICE REQUESTS ----------------

  requestCreate: [],

  requestAssign: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  requestStatusUpdate: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  requestCancel: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  requestDelete: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],


  // ---------------- PAYMENTS ----------------

  paymentAccess: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.GUEST
  ],


  // ---------------- INVOICES ----------------

  invoiceCreate: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],


  // ---------------- NOTIFICATIONS ----------------

  notificationCreate: [
    ROLES.ADMIN,
    ROLES.MANAGER
  ],

  notificationRead: [
    ROLES.ADMIN,
    ROLES.MANAGER,
    ROLES.EMPLOYEE,
    ROLES.GUEST
  ]
}


// ============================================================
// HELPERS
// ============================================================

export function hasPermission(
  role,
  allowedRoles = []
) {

  if (!role) {
    return false
  }

  return allowedRoles.includes(role)
}


export function hasRole(
  role,
  allowedRoles = []
) {

  if (!role) {
    return false
  }

  return allowedRoles.includes(role)
}