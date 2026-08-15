import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Spinner } from './ui'

export default function ProtectedRoute({
  children,
  allowedRoles
}) {
  const {
    isAuthenticated,
    booting,
    hasAnyRole
  } = useAuth()

  const location = useLocation()

  // Wait while authentication is being checked
  if (booting) {
    return (
      <div className="grid min-h-screen place-items-center">
        <Spinner label="Validating your session..." />
      </div>
    )
  }

  // User is not logged in
  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        replace
        state={{
          from: location.pathname
        }}
      />
    )
  }

  // User is logged in but does not have required role
  if (
    allowedRoles?.length &&
    !hasAnyRole(allowedRoles)
  ) {
    return (
      <Navigate
        to="/unauthorized"
        replace
      />
    )
  }

  return children
}