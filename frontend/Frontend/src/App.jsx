import {
  Navigate,
  Route,
  Routes
} from 'react-router-dom'

import { Toaster } from 'react-hot-toast'

import LandingPage from './pages/LandingPage'
import ExplorePage from './pages/ExplorePage'
import AuthPage from './pages/AuthPage'
import DashboardPage from './pages/DashboardPage'

import AppShell from './components/AppShell'
import ProtectedRoute from './components/ProtectedRoute'

import PaymentsPage from './pages/PaymentsPage'
import NotificationsPage from './pages/NotificationsPage'
import ServiceRequestsPage from './pages/ServiceRequestsPage'
import AIAssistantPage from './pages/AIAssistantPage'
import NotFoundPage from './pages/NotFoundPage'

import {
  BookingsPage,
  DepartmentsPage,
  EmployeesPage,
  HotelServicesPage,
  HotelsPage,
  InvoicesPage,
  RatingsPage,
  RoomsPage,
  UsersPage
} from './pages/ManagementPages'

import { useAuth } from './context/AuthContext'

import {
  pageRoles
} from './lib/permissions'


function GuestOnly({ children }) {

  const {
    isAuthenticated,
    booting
  } = useAuth()

  if (booting) {
    return null
  }

  if (isAuthenticated) {
    return (
      <Navigate
        to="/app"
        replace
      />
    )
  }

  return children
}


function RoleProtectedRoute({
  children,
  allowedRoles
}) {

  return (
    <ProtectedRoute
      allowedRoles={allowedRoles}
    >
      {children}
    </ProtectedRoute>
  )
}


export default function App() {

  return (
    <>
      <Routes>

        {/* PUBLIC LANDING PAGE */}

        <Route
          path="/"
          element={<LandingPage />}
        />


        {/* EXPLORE */}

        <Route
          path="/explore"
          element={<ExplorePage />}
        />


        {/* LOGIN */}

        <Route
          path="/login"
          element={
            <GuestOnly>
              <AuthPage mode="login" />
            </GuestOnly>
          }
        />


        {/* REGISTER */}

        <Route
          path="/register"
          element={
            <GuestOnly>
              <AuthPage mode="register" />
            </GuestOnly>
          }
        />


        {/* PROTECTED APP */}

        <Route
          path="/app"
          element={
            <ProtectedRoute>
              <AppShell />
            </ProtectedRoute>
          }
        >

          <Route
            index
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.dashboard}
              >
                <DashboardPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="hotels"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.hotels}
              >
                <HotelsPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="rooms"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.rooms}
              >
                <RoomsPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="bookings"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.bookings}
              >
                <BookingsPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="ratings"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.ratings}
              >
                <RatingsPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="users"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.users}
              >
                <UsersPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="departments"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.departments}
              >
                <DepartmentsPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="employees"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.employees}
              >
                <EmployeesPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="hotel-services"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.hotelServices}
              >
                <HotelServicesPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="service-requests"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.serviceRequests}
              >
                <ServiceRequestsPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="payments"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.payments}
              >
                <PaymentsPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="invoices"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.invoices}
              >
                <InvoicesPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="notifications"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.notifications}
              >
                <NotificationsPage />
              </RoleProtectedRoute>
            }
          />


          <Route
            path="ai-assistant"
            element={
              <RoleProtectedRoute
                allowedRoles={pageRoles.ai}
              >
                <AIAssistantPage />
              </RoleProtectedRoute>
            }
          />

        </Route>


        {/* FALLBACK */}

        <Route
          path="*"
          element={<NotFoundPage />}
        />

      </Routes>


      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000
        }}
      />
    </>
  )
}