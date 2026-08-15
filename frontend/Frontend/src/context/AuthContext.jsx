import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState
} from 'react'

import {
  authApi
} from '../api/services'


const AuthContext =
  createContext(null)


const TOKEN_KEY =
  'ehms_token'

const USER_KEY =
  'ehms_user'


function readStoredUser() {

  try {

    const value =
      localStorage.getItem(
        USER_KEY
      )


    if (!value) {
      return null
    }


    return JSON.parse(value)


  } catch {

    return null

  }
}


export function AuthProvider({
  children
}) {

  const [
    user,
    setUser
  ] = useState(
    readStoredUser
  )


  const [
    booting,
    setBooting
  ] = useState(true)


  const clearSession = () => {

    localStorage.removeItem(
      TOKEN_KEY
    )

    localStorage.removeItem(
      USER_KEY
    )

    setUser(null)

  }


  const saveSession = (
    response
  ) => {

    if (!response?.token) {

      throw new Error(
        'Authentication response did not contain a token.'
      )
    }


    if (!response?.role) {

      throw new Error(
        'Authentication response did not contain a role.'
      )
    }


    const sessionUser = {

      // AuthService account ID
      id:
        response.id
        ?? null,

      email:
        response.email
        ?? '',

      role:
        response.role,


      // UserService profile ID
      userId:
        response.userId
        ?? null,


      // EmployeeService employee ID
      employeeId:
        response.employeeId
        ?? null,


      // Assigned hotel for HOTEL_MANAGER / EMPLOYEE
      hotelId:
        response.hotelId
        ?? null,


      departmentId:
        response.departmentId
        ?? null
    }


    localStorage.setItem(
      TOKEN_KEY,
      response.token
    )


    localStorage.setItem(
      USER_KEY,
      JSON.stringify(
        sessionUser
      )
    )


    setUser(
      sessionUser
    )


    return sessionUser
  }


  const login =
    async payload => {

      const response =
        await authApi.login(
          payload
        )


      saveSession(
        response
      )


      return response
    }


  const register =
    async payload => {

      const response =
        await authApi.register(
          payload
        )


      saveSession(
        response
      )


      return response
    }


  const logout = () => {

    clearSession()

  }


  useEffect(() => {

    const token =
      localStorage.getItem(
        TOKEN_KEY
      )


    if (!token) {

      clearSession()

      setBooting(false)

      return
    }


    authApi
      .validate()
      .then(() => {

        setBooting(false)

      })
      .catch(() => {

        clearSession()

        setBooting(false)

      })

  }, [])


  const value =
    useMemo(
      () => ({

        user,

        booting,

        isAuthenticated:
          Boolean(
            user &&
            localStorage.getItem(
              TOKEN_KEY
            )
          ),

        login,

        register,

        logout

      }),

      [
        user,
        booting
      ]
    )


  return (

    <AuthContext.Provider
      value={value}
    >

      {children}

    </AuthContext.Provider>

  )
}


export function useAuth() {

  const context =
    useContext(
      AuthContext
    )


  if (!context) {

    throw new Error(
      'useAuth must be used inside AuthProvider.'
    )
  }


  return context
}