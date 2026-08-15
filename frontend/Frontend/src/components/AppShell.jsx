import {
  Bell,
  BedDouble,
  Bot,
  Building2,
  CalendarDays,
  CreditCard,
  FileText,
  Hotel,
  LayoutDashboard,
  LogOut,
  MessageSquareText,
  Star,
  Users,
  UtensilsCrossed
} from 'lucide-react'

import {
  NavLink,
  Outlet
} from 'react-router-dom'

import {
  useAuth
} from '../context/AuthContext'

import {
  pageRoles,
  ROLES
} from '../lib/permissions'


const navigation = [

  {
    key: 'dashboard',
    label: 'Dashboard',
    path: '/app',
    icon: LayoutDashboard,
    roles: pageRoles.dashboard,
    end: true
  },

  {
    key: 'hotels',
    label: 'Hotels',
    path: '/app/hotels',
    icon: Hotel,
    roles: pageRoles.hotels
  },

  {
    key: 'rooms',
    label: 'Rooms',
    path: '/app/rooms',
    icon: BedDouble,
    roles: pageRoles.rooms
  },

  {
    key: 'bookings',
    label: 'Bookings',
    path: '/app/bookings',
    icon: CalendarDays,
    roles: pageRoles.bookings
  },

  {
    key: 'ratings',
    label: 'Ratings',
    path: '/app/ratings',
    icon: Star,
    roles: pageRoles.ratings
  },

  {
    key: 'users',
    label: 'Guests',
    path: '/app/users',
    icon: Users,
    roles: pageRoles.users
  },

  {
    key: 'departments',
    label: 'Departments',
    path: '/app/departments',
    icon: Building2,
    roles: pageRoles.departments
  },

  {
    key: 'employees',
    label: 'Employees',
    path: '/app/employees',
    icon: Users,
    roles: pageRoles.employees
  },

  {
    key: 'hotelServices',
    label: 'Hotel Services',
    path: '/app/hotel-services',
    icon: UtensilsCrossed,
    roles: pageRoles.hotelServices
  },

  {
    key: 'serviceRequests',
    label: 'Service Requests',
    path: '/app/service-requests',
    icon: MessageSquareText,
    roles: pageRoles.serviceRequests
  },

  {
    key: 'payments',
    label: 'Payments',
    path: '/app/payments',
    icon: CreditCard,
    roles: pageRoles.payments
  },

  {
    key: 'invoices',
    label: 'Invoices',
    path: '/app/invoices',
    icon: FileText,
    roles: pageRoles.invoices
  },

  {
    key: 'notifications',
    label: 'Notifications',
    path: '/app/notifications',
    icon: Bell,
    roles: pageRoles.notifications
  },

  {
    key: 'ai',
    label: 'AI Assistant',
    path: '/app/ai-assistant',
    icon: Bot,
    roles: pageRoles.ai
  }
]


const roleNames = {

  [ROLES.ADMIN]:
    'Administrator',

  [ROLES.MANAGER]:
    'Hotel Manager',

  [ROLES.EMPLOYEE]:
    'Employee',

  [ROLES.GUEST]:
    'Guest'
}


function getNavigationLabel(
  item,
  role
) {

  // ==========================================================
  // GUEST LABELS
  // ==========================================================

  if (role === ROLES.GUEST) {

    switch (item.key) {

      case 'hotels':
        return 'Explore Hotels'

      case 'rooms':
        return 'Available Rooms'

      case 'bookings':
        return 'My Bookings'

      case 'ratings':
        return 'My Ratings'

      case 'payments':
        return 'My Payments'

      case 'invoices':
        return 'My Invoices'

      case 'notifications':
        return 'My Notifications'

      case 'ai':
        return 'AI Concierge'

      default:
        return item.label
    }
  }


  // ==========================================================
  // MANAGER LABELS
  // ==========================================================

  if (role === ROLES.MANAGER) {

    switch (item.key) {

      case 'hotels':
        return 'My Hotel'

      case 'bookings':
        return 'Hotel Bookings'

      case 'payments':
        return 'Hotel Payments'

      case 'invoices':
        return 'Hotel Invoices'

      case 'ratings':
        return 'Hotel Ratings'

      default:
        return item.label
    }
  }


  return item.label
}


export default function AppShell() {

  const {
    user,
    logout
  } = useAuth()


  const visibleNavigation =
    navigation.filter(
      item =>
        item.roles?.includes(
          user?.role
        )
    )


  return (

    <div className="min-h-screen bg-slate-50 dark:bg-slate-950">


      {/* ====================================================== */}
      {/* SIDEBAR                                                */}
      {/* ====================================================== */}

      <aside className="fixed inset-y-0 left-0 z-40 hidden w-72 flex-col border-r border-slate-200 bg-white dark:border-slate-800 dark:bg-slate-900 lg:flex">


        {/* LOGO */}

        <div className="flex h-20 items-center gap-3 border-b border-slate-200 px-6 dark:border-slate-800">

          <div className="grid h-11 w-11 place-items-center rounded-2xl bg-cyan-400 text-slate-950">

            <Hotel className="h-5 w-5" />

          </div>


          <div>

            <p className="font-display text-xl font-extrabold">

              StayOps

            </p>

            <p className="text-[10px] font-bold uppercase tracking-[.2em] text-slate-400">

              Enterprise Suite

            </p>

          </div>

        </div>



        {/* USER */}

        <div className="px-5 pt-5">

          <div className="rounded-2xl bg-slate-100 p-4 dark:bg-slate-800">

            <p className="text-[10px] font-bold uppercase tracking-[.18em] text-slate-400">

              Signed in as

            </p>

            <p className="mt-1 font-bold">

              {
                roleNames[user?.role]
                || user?.role
              }

            </p>

            <p className="mt-1 truncate text-xs text-slate-500">

              {user?.email}

            </p>


            {user?.role === ROLES.MANAGER &&
              user?.hotelId && (

                <p className="mt-2 text-xs font-semibold text-cyan-600">

                  Hotel: {user.hotelId}

                </p>

              )}

          </div>

        </div>



        {/* NAVIGATION */}

        <nav className="flex-1 space-y-1 overflow-y-auto p-4">

          {visibleNavigation.map(item => {

            const Icon =
              item.icon


            return (

              <NavLink
                key={item.key}
                to={item.path}
                end={item.end}

                className={({ isActive }) => `

                  flex
                  items-center
                  gap-3
                  rounded-xl
                  px-4
                  py-3
                  text-sm
                  font-semibold
                  transition

                  ${
                    isActive

                      ? `
                          bg-cyan-50
                          text-cyan-700
                          dark:bg-cyan-950/40
                          dark:text-cyan-300
                        `

                      : `
                          text-slate-600
                          hover:bg-slate-100
                          dark:text-slate-400
                          dark:hover:bg-slate-800
                        `
                  }

                `}
              >

                <Icon className="h-5 w-5" />

                {
                  getNavigationLabel(
                    item,
                    user?.role
                  )
                }

              </NavLink>

            )

          })}

        </nav>



        {/* LOGOUT */}

        <div className="border-t border-slate-200 p-4 dark:border-slate-800">

          <button
            type="button"
            onClick={logout}

            className="flex w-full items-center gap-3 rounded-xl px-4 py-3 text-sm font-semibold text-rose-600 transition hover:bg-rose-50 dark:hover:bg-rose-950/30"
          >

            <LogOut className="h-5 w-5" />

            Logout

          </button>

        </div>

      </aside>



      {/* ====================================================== */}
      {/* CONTENT                                                */}
      {/* ====================================================== */}

      <div className="lg:pl-72">


        {/* MOBILE HEADER */}

        <header className="flex h-16 items-center justify-between border-b border-slate-200 bg-white px-5 dark:border-slate-800 dark:bg-slate-900 lg:hidden">

          <div className="flex items-center gap-2">

            <Hotel className="h-5 w-5 text-cyan-600" />

            <span className="font-bold">

              StayOps

            </span>

          </div>


          <button
            type="button"
            onClick={logout}

            className="text-sm font-semibold text-rose-600"
          >

            Logout

          </button>

        </header>


        <main className="min-h-screen p-5 sm:p-8">

          <Outlet />

        </main>

      </div>

    </div>
  )
}