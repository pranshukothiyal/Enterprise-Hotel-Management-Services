import {
  Navigate,
  useLocation
} from 'react-router-dom'

import { useAuth } from '../context/AuthContext'


export default function ProtectedRoute({
  children,
  allowedRoles
}) {

  const {
    isAuthenticated,
    booting,
    user
  } = useAuth()

  const location = useLocation()


  if (booting) {

    return (
      <div className="flex min-h-screen items-center justify-center">
        <p>Loading...</p>
      </div>
    )
  }


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


  if (
    allowedRoles &&
    allowedRoles.length > 0 &&
    !allowedRoles.includes(user?.role)
  ) {

    return (
      <Navigate
        to="/app"
        replace
      />
    )
  }


  return children
}