import {
  Link
} from 'react-router-dom'

import {
  BedDouble,
  Bot,
  Building2,
  CalendarDays,
  CreditCard,
  FileText,
  Hotel,
  MessageSquareText,
  ShieldCheck,
  Star,
  Users,
  UtensilsCrossed
} from 'lucide-react'

import {
  useAuth
} from '../context/AuthContext'

import {
  ROLES
} from '../lib/permissions'

import {
  Card
} from '../components/ui'


// ============================================================
// DASHBOARD CARD
// ============================================================

function DashboardCard({
  title,
  description,
  path,
  icon: Icon
}) {

  return (

    <Link to={path}>

      <Card className="h-full p-6 transition hover:-translate-y-1 hover:shadow-lg">

        <div className="grid h-11 w-11 place-items-center rounded-xl bg-cyan-100 text-cyan-700 dark:bg-cyan-950 dark:text-cyan-300">

          <Icon className="h-5 w-5" />

        </div>


        <h3 className="mt-5 font-display text-lg font-bold">
          {title}
        </h3>


        <p className="mt-2 text-sm leading-6 text-slate-500">
          {description}
        </p>

      </Card>

    </Link>
  )
}


// ============================================================
// COMMON DASHBOARD LAYOUT
// ============================================================

function Layout({
  eyebrow,
  title,
  description,
  cards
}) {

  return (

    <div>

      <div className="max-w-3xl">

        <p className="text-xs font-bold uppercase tracking-[.2em] text-cyan-600">
          {eyebrow}
        </p>


        <h1 className="mt-3 font-display text-4xl font-extrabold">
          {title}
        </h1>


        <p className="mt-3 text-slate-500">
          {description}
        </p>

      </div>


      <div className="mt-10 grid gap-5 sm:grid-cols-2 xl:grid-cols-3">

        {cards.map(card => (

          <DashboardCard
            key={card.path}
            {...card}
          />

        ))}

      </div>

    </div>
  )
}


// ============================================================
// ADMIN DASHBOARD
// ============================================================

function AdminDashboard() {

  const cards = [

    {
      title: 'Hotels',
      description:
        'Create and administer every hotel.',
      path: '/app/hotels',
      icon: Hotel
    },

    {
      title: 'Rooms',
      description:
        'Manage room inventory across hotels.',
      path: '/app/rooms',
      icon: BedDouble
    },

    {
      title: 'Bookings',
      description:
        'View and manage all platform bookings.',
      path: '/app/bookings',
      icon: CalendarDays
    },

    {
      title: 'Guests',
      description:
        'View guest profile records.',
      path: '/app/users',
      icon: Users
    },

    {
      title: 'Departments',
      description:
        'Manage departments across hotels.',
      path: '/app/departments',
      icon: Building2
    },

    {
      title: 'Employees',
      description:
        'Manage employees across the platform.',
      path: '/app/employees',
      icon: Users
    },

    {
      title: 'Hotel Services',
      description:
        'Manage hotel service offerings.',
      path: '/app/hotel-services',
      icon: UtensilsCrossed
    },

    {
      title: 'Service Requests',
      description:
        'Manage hotel operational requests.',
      path: '/app/service-requests',
      icon: MessageSquareText
    },

    {
      title: 'Payments',
      description:
        'View payments across the platform.',
      path: '/app/payments',
      icon: CreditCard
    },

    {
      title: 'Invoices',
      description:
        'View and generate invoices.',
      path: '/app/invoices',
      icon: FileText
    },

    {
      title: 'Ratings',
      description:
        'Review feedback across hotels.',
      path: '/app/ratings',
      icon: Star
    },

    {
      title: 'AI Assistant',
      description:
        'Use the StayOps AI assistant.',
      path: '/app/ai-assistant',
      icon: Bot
    }
  ]


  return (

    <Layout
      eyebrow="Platform administration"
      title="Admin Dashboard"
      description="Manage the complete StayOps platform."
      cards={cards}
    />
  )
}


// ============================================================
// HOTEL MANAGER DASHBOARD
// ============================================================

function ManagerDashboard({
  user
}) {

  /*
   * A Hotel Manager must be assigned to a hotel.
   *
   * Never fall back to system-wide access when hotelId is null.
   */
  if (!user?.hotelId) {

    return (

      <div className="rounded-3xl border border-amber-200 bg-amber-50 p-8 dark:border-amber-900 dark:bg-amber-950/30">

        <Hotel className="h-8 w-8 text-amber-600" />


        <h1 className="mt-4 text-2xl font-bold">
          No hotel assigned
        </h1>


        <p className="mt-2 max-w-xl text-sm leading-6 text-slate-600 dark:text-slate-300">

          Your Hotel Manager account is active, but no hotel has
          been assigned to it yet.

          A manager is not given access to every hotel when the
          hotel assignment is missing.

        </p>

      </div>
    )
  }


  const cards = [

    {
      title: 'My Hotel',
      description:
        'View your assigned hotel.',
      path: '/app/hotels',
      icon: Hotel
    },

    {
      title: 'Rooms',
      description:
        'Manage rooms belonging to your hotel.',
      path: '/app/rooms',
      icon: BedDouble
    },

    {
      title: 'Hotel Bookings',
      description:
        'Manage bookings belonging to your hotel.',
      path: '/app/bookings',
      icon: CalendarDays
    },

    {
      title: 'Guests',
      description:
        'View guests who have booked your hotel.',
      path: '/app/users',
      icon: Users
    },

    {
      title: 'Departments',
      description:
        'Manage departments belonging to your hotel.',
      path: '/app/departments',
      icon: Building2
    },

    {
      title: 'Employees',
      description:
        'Manage employees belonging to your hotel.',
      path: '/app/employees',
      icon: Users
    },

    {
      title: 'Hotel Services',
      description:
        'Manage services offered by your hotel.',
      path: '/app/hotel-services',
      icon: UtensilsCrossed
    },

    {
      title: 'Service Requests',
      description:
        'Manage operational requests for your hotel.',
      path: '/app/service-requests',
      icon: MessageSquareText
    },

    {
      title: 'Hotel Payments',
      description:
        'View payments associated with your hotel.',
      path: '/app/payments',
      icon: CreditCard
    },

    {
      title: 'Hotel Invoices',
      description:
        'View invoices associated with your hotel bookings.',
      path: '/app/invoices',
      icon: FileText
    },

    {
      title: 'Hotel Ratings',
      description:
        'Review feedback belonging to your hotel.',
      path: '/app/ratings',
      icon: Star
    },

    {
      title: 'AI Assistant',
      description:
        'Get assistance with hotel operations.',
      path: '/app/ai-assistant',
      icon: Bot
    }
  ]


  return (

    <Layout
      eyebrow={`Hotel ${user.hotelId}`}
      title="Manager Dashboard"
      description="Manage only the operations belonging to your assigned hotel."
      cards={cards}
    />
  )
}


// ============================================================
// EMPLOYEE DASHBOARD
// ============================================================

function EmployeeDashboard() {

  const cards = [

    {
      title: 'Rooms',
      description:
        'View operational room information for your hotel.',
      path: '/app/rooms',
      icon: BedDouble
    },

    {
      title: 'Notifications',
      description:
        'View notifications relevant to your work.',
      path: '/app/notifications',
      icon: ShieldCheck
    },

    {
      title: 'AI Assistant',
      description:
        'Get assistance with hotel operations.',
      path: '/app/ai-assistant',
      icon: Bot
    }
  ]


  return (

    <Layout
      eyebrow="Employee workspace"
      title="Employee Dashboard"
      description="Only the operational tools required for your work are shown here."
      cards={cards}
    />
  )
}


// ============================================================
// GUEST DASHBOARD
// ============================================================

function GuestDashboard({
  user
}) {

  const cards = [

    {
      title: 'Explore Hotels',
      description:
        'Browse hotels available on StayOps.',
      path: '/app/hotels',
      icon: Hotel
    },

    {
      title: 'Available Rooms',
      description:
        'View available rooms and prices.',
      path: '/app/rooms',
      icon: BedDouble
    },

    {
      title: 'My Bookings',
      description:
        'View bookings belonging only to your account.',
      path: '/app/bookings',
      icon: CalendarDays
    },

    {
      title: 'My Payments',
      description:
        'Pay for and view payments belonging to your bookings.',
      path: '/app/payments',
      icon: CreditCard
    },

    {
      title: 'My Invoices',
      description:
        'View invoices belonging only to you.',
      path: '/app/invoices',
      icon: FileText
    },

    {
      title: 'My Ratings',
      description:
        'View and submit your own hotel feedback.',
      path: '/app/ratings',
      icon: Star
    },

    {
      title: 'My Notifications',
      description:
        'View notifications intended for your account.',
      path: '/app/notifications',
      icon: ShieldCheck
    },

    {
      title: 'AI Concierge',
      description:
        'Ask questions about hotels, rooms and stays.',
      path: '/app/ai-assistant',
      icon: Bot
    }
  ]


  return (

    <Layout
      eyebrow={
        user?.email
          ? user.email
          : 'Guest'
      }
      title="Welcome to StayOps"
      description="Everything you need for your own hotel stay."
      cards={cards}
    />
  )
}


// ============================================================
// MAIN DASHBOARD
// ============================================================

export default function DashboardPage() {

  const {
    user
  } = useAuth()


  switch (
    user?.role
  ) {

    case ROLES.ADMIN:

      return (
        <AdminDashboard />
      )


    case ROLES.MANAGER:

      return (

        <ManagerDashboard
          user={user}
        />
      )


    case ROLES.EMPLOYEE:

      return (
        <EmployeeDashboard />
      )


    case ROLES.GUEST:

      return (

        <GuestDashboard
          user={user}
        />
      )


    default:

      return (

        <div className="rounded-2xl border border-rose-200 bg-rose-50 p-6 text-rose-700 dark:border-rose-900 dark:bg-rose-950/30 dark:text-rose-300">

          Unknown account role.

        </div>
      )
  }
}