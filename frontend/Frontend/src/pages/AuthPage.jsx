import {
  useEffect,
  useMemo,
  useState
} from 'react'

import {
  Link,
  useLocation,
  useNavigate,
  useSearchParams
} from 'react-router-dom'

import {
  ArrowLeft,
  Eye,
  EyeOff,
  Hotel,
  LockKeyhole,
  Mail,
  ShieldCheck
} from 'lucide-react'

import toast from 'react-hot-toast'

import { useAuth } from '../context/AuthContext'

import {
  Button,
  Field,
  Input
} from '../components/ui'

import {
  errorMessage
} from '../lib/utils'

import {
  ROLE_OPTIONS,
  ROLES
} from '../lib/permissions'


export default function AuthPage({
  mode = 'login'
}) {

  const isRegister =
    mode === 'register'


  // ============================================================
  // ROUTING
  // ============================================================

  const [searchParams] =
    useSearchParams()

  const navigate =
    useNavigate()

  const location =
    useLocation()


  // ============================================================
  // AUTH
  // ============================================================

  const {
    login,
    register
  } = useAuth()


  // ============================================================
  // GET ROLE FROM URL
  // ============================================================

  const requestedRole =
    searchParams.get('role')


  const initialRole = useMemo(() => {

    const validRole =
      ROLE_OPTIONS.some(
        role =>
          role.value === requestedRole
      )


    return validRole
      ? requestedRole
      : ROLES.GUEST

  }, [requestedRole])


  const [
    selectedRole,
    setSelectedRole
  ] = useState(initialRole)


  // Update role when URL changes

  useEffect(() => {

    setSelectedRole(initialRole)

  }, [initialRole])


  // ============================================================
  // CURRENT ROLE INFORMATION
  // ============================================================

  const roleInfo =
    ROLE_OPTIONS.find(
      role =>
        role.value === selectedRole
    )


  // ============================================================
  // FORM
  // ============================================================

  const [form, setForm] =
    useState({

      firstName: '',

      lastName: '',

      email: '',

      password: ''

    })


  const [
    showPassword,
    setShowPassword
  ] = useState(false)


  const [
    loading,
    setLoading
  ] = useState(false)


  // ============================================================
  // UPDATE FORM
  // ============================================================

  const update = (
    name,
    value
  ) => {

    setForm(previous => ({

      ...previous,

      [name]: value

    }))
  }


  // ============================================================
  // SUBMIT
  // ============================================================

  const submit = async event => {

    event.preventDefault()

    setLoading(true)


    try {

      // ========================================================
      // REGISTER
      // ========================================================

      if (isRegister) {

        await register({

          firstName:
            form.firstName,

          lastName:
            form.lastName,

          email:
            form.email,

          password:
            form.password,

          // IMPORTANT
          role:
            selectedRole

        })


        toast.success(
          `${roleInfo?.label} account created successfully`
        )


      } else {

        // ======================================================
        // LOGIN
        // ======================================================

        await login({

          email:
            form.email,

          password:
            form.password,

          // Backend verifies actual database role
          expectedRole:
            selectedRole

        })


        toast.success(
          `Signed in as ${roleInfo?.label}`
        )
      }


      // ========================================================
      // DIRECTLY OPEN APP
      // ========================================================

      navigate(
        location.state?.from || '/app',
        {
          replace: true
        }
      )


    } catch (error) {

      toast.error(
        errorMessage(error)
      )


    } finally {

      setLoading(false)

    }
  }


  // ============================================================
  // JSX
  // ============================================================

  return (

    <div
      className="
        grid
        min-h-screen
        bg-slate-950
        lg:grid-cols-[1.05fr_.95fr]
      "
    >

      {/* ====================================================== */}
      {/* LEFT SIDE                                              */}
      {/* ====================================================== */}

      <section
        className="
          relative
          hidden
          overflow-hidden
          lg:flex
          lg:flex-col
          lg:justify-between
          lg:p-12
        "
      >

        <div
          className="
            absolute
            inset-0
            bg-hero-grid
            bg-[size:40px_40px]
            opacity-20
          "
        />


        <div
          className="
            absolute
            -left-32
            top-24
            h-96
            w-96
            rounded-full
            bg-cyan-500/30
            blur-[110px]
          "
        />


        {/* LOGO */}

        <Link
          to="/"
          className="
            relative
            flex
            items-center
            gap-3
            text-white
          "
        >

          <div
            className="
              grid
              h-11
              w-11
              place-items-center
              rounded-2xl
              bg-cyan-400
              text-slate-950
            "
          >

            <Hotel className="h-5 w-5" />

          </div>


          <div>

            <p
              className="
                font-display
                text-xl
                font-extrabold
              "
            >
              StayOps
            </p>


            <p
              className="
                text-[9px]
                font-bold
                uppercase
                tracking-[.25em]
                text-slate-500
              "
            >
              Enterprise Suite
            </p>

          </div>

        </Link>



        {/* INFORMATION */}

        <div className="relative max-w-xl">

          <div
            className="
              mb-6
              inline-flex
              rounded-full
              border
              border-cyan-400/20
              bg-cyan-400/10
              px-4
              py-2
              text-xs
              font-bold
              uppercase
              tracking-[.16em]
              text-cyan-300
            "
          >

            <ShieldCheck className="mr-2 h-4 w-4" />

            Role-secured workspace

          </div>


          <h1
            className="
              font-display
              text-5xl
              font-extrabold
              leading-[1.08]
              tracking-tight
              text-white
            "
          >

            One platform.
            <br />
            Four purpose-built experiences.

          </h1>


          <p
            className="
              mt-6
              text-lg
              leading-8
              text-slate-400
            "
          >

            Admin, Manager, Employee and Guest
            accounts receive their own role-based
            pages and permissions.

          </p>

        </div>


        <p
          className="
            relative
            text-xs
            text-slate-600
          "
        >

          JWT-protected · Role verified by AuthService

        </p>

      </section>



      {/* ====================================================== */}
      {/* AUTHENTICATION SIDE                                    */}
      {/* ====================================================== */}

      <section
        className="
          flex
          min-h-screen
          items-center
          justify-center
          bg-white
          px-5
          py-12
          dark:bg-slate-900
          sm:px-10
        "
      >

        <div className="w-full max-w-md">


          {/* BACK */}

          <Link
            to="/"
            className="
              mb-8
              inline-flex
              items-center
              gap-2
              text-sm
              font-semibold
              text-slate-500
              hover:text-slate-900
              dark:hover:text-white
            "
          >

            <ArrowLeft className="h-4 w-4" />

            Back to home

          </Link>



          {/* ================================================== */}
          {/* HEADING                                            */}
          {/* ================================================== */}

          <div className="mb-7">

            <p
              className="
                text-xs
                font-bold
                uppercase
                tracking-[.2em]
                text-cyan-600
              "
            >

              {isRegister
                ? `${roleInfo?.label} Registration`
                : `${roleInfo?.label} Sign In`
              }

            </p>


            <h1
              className="
                mt-3
                font-display
                text-4xl
                font-extrabold
                tracking-tight
                text-slate-900
                dark:text-white
              "
            >

              {isRegister
                ? `Create ${roleInfo?.label} account`
                : `Sign in as ${roleInfo?.label}`
              }

            </h1>


            <p
              className="
                mt-3
                text-sm
                leading-6
                text-slate-500
              "
            >

              {isRegister
                ? `Your account will be created with the ${roleInfo?.label} role.`
                : `Enter the credentials for your ${roleInfo?.label} account.`
              }

            </p>

          </div>



          {/* ================================================== */}
          {/* ROLE SELECTION                                     */}
          {/* ================================================== */}

          <div
            className="
              mb-6
              grid
              grid-cols-2
              gap-2
            "
          >

            {ROLE_OPTIONS.map(role => (

              <button
                key={role.value}
                type="button"

                onClick={() =>
                  setSelectedRole(
                    role.value
                  )
                }

                className={`
                  rounded-xl
                  border
                  px-3
                  py-3
                  text-left
                  transition

                  ${
                    selectedRole === role.value

                      ? `
                          border-cyan-500
                          bg-cyan-50
                          ring-2
                          ring-cyan-500/20
                          dark:bg-cyan-950/30
                        `

                      : `
                          border-slate-200
                          hover:border-slate-300
                          dark:border-slate-700
                        `
                  }
                `}
              >

                <span
                  className="
                    block
                    text-sm
                    font-bold
                    text-slate-900
                    dark:text-white
                  "
                >

                  {role.label}

                </span>


                <span
                  className="
                    mt-1
                    block
                    text-[11px]
                    leading-4
                    text-slate-500
                  "
                >

                  {role.description}

                </span>

              </button>

            ))}

          </div>



          {/* ================================================== */}
          {/* FORM                                               */}
          {/* ================================================== */}

          <form
            onSubmit={submit}
            className="space-y-5"
          >


            {/* REGISTER NAMES */}

            {isRegister && (

              <div
                className="
                  grid
                  gap-4
                  sm:grid-cols-2
                "
              >

                <Field label="First name">

                  <Input
                    required

                    value={
                      form.firstName
                    }

                    onChange={event =>
                      update(
                        'firstName',
                        event.target.value
                      )
                    }

                    placeholder="First name"
                  />

                </Field>


                <Field label="Last name">

                  <Input
                    required

                    value={
                      form.lastName
                    }

                    onChange={event =>
                      update(
                        'lastName',
                        event.target.value
                      )
                    }

                    placeholder="Last name"
                  />

                </Field>

              </div>

            )}



            {/* EMAIL */}

            <Field label="Email address">

              <div className="relative">

                <Mail
                  className="
                    pointer-events-none
                    absolute
                    left-3.5
                    top-1/2
                    h-4
                    w-4
                    -translate-y-1/2
                    text-slate-400
                  "
                />


                <Input
                  required
                  type="email"
                  className="pl-10"

                  value={
                    form.email
                  }

                  onChange={event =>
                    update(
                      'email',
                      event.target.value
                    )
                  }

                  placeholder="name@example.com"
                />

              </div>

            </Field>



            {/* PASSWORD */}

            <Field
              label="Password"

              hint={
                isRegister
                  ? 'Minimum 8 characters'
                  : undefined
              }
            >

              <div className="relative">

                <LockKeyhole
                  className="
                    pointer-events-none
                    absolute
                    left-3.5
                    top-1/2
                    h-4
                    w-4
                    -translate-y-1/2
                    text-slate-400
                  "
                />


                <Input
                  required

                  minLength={
                    isRegister
                      ? 8
                      : undefined
                  }

                  type={
                    showPassword
                      ? 'text'
                      : 'password'
                  }

                  className="pl-10 pr-11"

                  value={
                    form.password
                  }

                  onChange={event =>
                    update(
                      'password',
                      event.target.value
                    )
                  }

                  placeholder="••••••••"
                />


                <button
                  type="button"

                  onClick={() =>
                    setShowPassword(
                      previous =>
                        !previous
                    )
                  }

                  className="
                    absolute
                    right-3
                    top-1/2
                    -translate-y-1/2
                    rounded-lg
                    p-1
                    text-slate-400
                  "
                >

                  {showPassword

                    ? (
                        <EyeOff className="h-4 w-4" />
                      )

                    : (
                        <Eye className="h-4 w-4" />
                      )
                  }

                </button>

              </div>

            </Field>



            {/* SUBMIT */}

            <Button
              type="submit"
              size="lg"
              loading={loading}
              className="w-full"
            >

              {isRegister

                ? `Register as ${roleInfo?.label}`

                : `Sign in as ${roleInfo?.label}`
              }

            </Button>

          </form>



          {/* ================================================== */}
          {/* SWITCH                                             */}
          {/* ================================================== */}

          <p
            className="
              mt-7
              text-center
              text-sm
              text-slate-500
            "
          >

            {isRegister
              ? 'Already have an account? '
              : 'Need an account? '
            }


            <Link
              className="
                font-bold
                text-cyan-600
              "

              to={
                isRegister

                  ? `/login?role=${selectedRole}`

                  : `/register?role=${selectedRole}`
              }
            >

              {isRegister
                ? `Sign in as ${roleInfo?.label}`
                : `Register as ${roleInfo?.label}`
              }

            </Link>

          </p>

        </div>

      </section>

    </div>
  )
}