import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Aurora from '../components/Aurora'
import AccordionGallery from '../components/AccordionGallery'
import {
  ArrowRight,
  BedDouble,
  Bot,
  CheckCircle2,
  Hotel,
  ShieldCheck,
  Sparkles,
  Star,
  Users
} from 'lucide-react'

import { hotelsApi } from '../api/services'
import { Badge, Button, Card } from '../components/ui'
import { ROLE_OPTIONS } from '../lib/permissions'

const hotelCards = [
  {
    id: 1,
    img: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=600&q=80',
    title: 'Grand Meridian Hotel',
    location: 'Bengaluru, Karnataka',
    status: 'Available',
    rooms: '128 Rooms',
    rating: '4.8',
    type: 'Luxury Hotel'
  },
  {
    id: 2,
    img: 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=600&q=80',
    title: 'Harbour Heights',
    location: 'Mumbai, Maharashtra',
    status: 'Available',
    rooms: '96 Rooms',
    rating: '4.7',
    type: 'Business Hotel'
  },
  {
    id: 3,
    img: 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=600&q=80',
    title: 'Mountain Crest Resort',
    location: 'Manali, Himachal Pradesh',
    status: 'High Demand',
    rooms: '74 Rooms',
    rating: '4.9',
    type: 'Resort'
  },
  {
    id: 4,
    img: 'https://images.unsplash.com/photo-1564501049412-61c2a3083791?auto=format&fit=crop&w=600&q=80',
    title: 'Royal Orchid Suites',
    location: 'Pune, Maharashtra',
    status: 'Available',
    rooms: '112 Rooms',
    rating: '4.6',
    type: 'Premium Hotel'
  }
]
const features = [
  {
    icon: BedDouble,
    title: 'Live room operations',
    text:
      'Manage inventory, room status, pricing, capacity, and hotel-level availability from one workspace.'
  },
  {
    icon: ShieldCheck,
    title: 'Secure guest journeys',
    text:
      'JWT-backed access, booking workflows, payment verification, invoices, and real-time notifications.'
  },
  {
    icon: Bot,
    title: 'AI concierge',
    text:
      'Answer hotel and room questions using live backend data without inventing availability or prices.'
  },
  {
    icon: Users,
    title: 'Workforce coordination',
    text:
      'Organize departments, employees, room-service assignments, and operational status changes.'
  }
]
const accordionItems = [
  {
    image: 'https://picsum.photos/id/1015/900/1200',
    label: 'Canyon',
    link: '#'
  },
  {
    image: 'https://picsum.photos/id/1018/900/1200',
    label: 'Ridgeline',
    link: '#'
  },
  {
    image: 'https://picsum.photos/id/1039/900/1200',
    label: 'Falls',
    link: '#'
  },
  {
    image: 'https://picsum.photos/id/1043/900/1200',
    label: 'Harbour',
    link: '#'
  },
  {
    image: 'https://picsum.photos/id/1044/900/1200',
    label: 'Skyline',
    link: '#'
  }
]

export default function LandingPage() {

  const [hotels, setHotels] = useState([])


  // ============================================================
  // LOAD HOTELS
  // ============================================================

  useEffect(() => {

    hotelsApi
      .list()
      .then((response) => {

        if (Array.isArray(response)) {
          setHotels(response)
        }

      })
      .catch((error) => {

        console.error(
          'Unable to load hotels:',
          error
        )

      })

  }, [])


  return (

    <div className="min-h-screen overflow-hidden bg-white text-slate-950 dark:bg-slate-950 dark:text-white">


      {/* ====================================================== */}
      {/* HEADER                                                 */}
      {/* ====================================================== */}

      <header className="fixed inset-x-0 top-0 z-40 border-b border-slate-200/70 bg-white/80 backdrop-blur-xl dark:border-slate-800 dark:bg-slate-950/80">

        <div className="mx-auto flex h-20 max-w-7xl items-center justify-between px-5 sm:px-8">


          {/* LOGO */}

          <Link
            to="/"
            className="flex items-center gap-3"
          >

            <div className="grid h-10 w-10 place-items-center rounded-2xl bg-gradient-to-br from-cyan-400 to-blue-600 text-slate-950">

              <Hotel className="h-5 w-5" />

            </div>


            <div>

              <div className="font-display text-xl font-extrabold">
                StayOps
              </div>

              <div className="text-[9px] font-bold uppercase tracking-[.25em] text-slate-400">
                Enterprise Hotel Suite
              </div>

            </div>

          </Link>



          {/* NAVIGATION */}

          <nav className="hidden items-center gap-7 text-sm font-semibold text-slate-600 dark:text-slate-300 md:flex">

            <a
              href="#login-options"
              className="hover:text-cyan-600"
            >
              Login / Register
            </a>


            <a
              href="#platform"
              className="hover:text-cyan-600"
            >
              Platform
            </a>


            <a
              href="#properties"
              className="hover:text-cyan-600"
            >
              Properties
            </a>


            <a
              href="#capabilities"
              className="hover:text-cyan-600"
            >
              Capabilities
            </a>

          </nav>



          {/* HEADER ACTIONS */}

          <div className="flex items-center gap-2">

            <a href="#login-options">

              <Button variant="secondary">
                Sign in
              </Button>

            </a>


            <Link
              to="/register?role=GUEST"
              className="hidden sm:block"
            >

              <Button>

                Guest Register

                <ArrowRight className="h-4 w-4" />

              </Button>

            </Link>

          </div>

        </div>

      </header>



      {/* ====================================================== */}
      {/* MAIN                                                   */}
      {/* ====================================================== */}

      <main>


        {/* ==================================================== */}
        {/* HERO                                                 */}
        {/* ==================================================== */}
{/* ==================================================== */}
{/* HERO                                                 */}
{/* ==================================================== */}

<section className="relative isolate min-h-[760px] overflow-hidden pt-20">

  {/* AURORA BACKGROUND */}

  <div className="absolute inset-0 -z-30 bg-slate-950">

    <Aurora
      colorStops={[
        '#22D3EE',
        '#3B82F6',
        '#8B5CF6'
      ]}
      amplitude={1.15}
      blend={0.58}
      speed={0.55}
    />

  </div>


  {/* DARK OVERLAY */}

  <div
    className="
      absolute
      inset-0
      -z-20
      bg-[linear-gradient(90deg,rgba(2,6,23,0.97)_0%,rgba(2,6,23,0.86)_42%,rgba(2,6,23,0.52)_72%,rgba(2,6,23,0.82)_100%)]
    "
  />


  {/* GRID EFFECT */}

  <div
    className="
      absolute
      inset-0
      -z-10
      opacity-[0.16]
    "
    style={{
      backgroundImage:
        `
        linear-gradient(
          rgba(255,255,255,.08) 1px,
          transparent 1px
        ),
        linear-gradient(
          90deg,
          rgba(255,255,255,.08) 1px,
          transparent 1px
        )
        `,
      backgroundSize:
        '48px 48px'
    }}
  />


  {/* BACKGROUND GLOW */}

  <div
    className="
      absolute
      left-[10%]
      top-24
      -z-10
      h-72
      w-72
      rounded-full
      bg-cyan-400/10
      blur-3xl
    "
  />

  <div
    className="
      absolute
      bottom-10
      right-[5%]
      -z-10
      h-96
      w-96
      rounded-full
      bg-violet-500/10
      blur-3xl
    "
  />


  {/* HERO CONTENT */}

  <div
    className="
      mx-auto
      grid
      min-h-[680px]
      max-w-7xl
      items-center
      gap-14
      px-5
      py-16
      sm:px-8
      lg:grid-cols-[1.05fr_.95fr]
      lg:py-20
    "
  >

    {/* ============================================ */}
    {/* LEFT SIDE                                    */}
    {/* ============================================ */}

    <div className="max-w-3xl">

      {/* BADGE */}

      <div
        className="
          inline-flex
          items-center
          gap-2
          rounded-full
          border
          border-cyan-300/20
          bg-white/[0.06]
          px-4
          py-2
          text-xs
          font-bold
          uppercase
          tracking-[.18em]
          text-cyan-200
          backdrop-blur-xl
        "
      >

        <Sparkles className="h-4 w-4" />

        Enterprise hospitality platform

      </div>


      {/* MAIN HEADING */}

      <h1
        className="
          mt-7
          font-display
          text-5xl
          font-black
          tracking-[-0.045em]
          text-white
          sm:text-6xl
          lg:text-7xl
        "
      >

        Run every hotel operation

        <span
          className="
            block
            bg-gradient-to-r
            from-cyan-300
            via-blue-400
            to-violet-400
            bg-clip-text
            text-transparent
          "
        >

          from one intelligent platform.

        </span>

      </h1>


      {/* DESCRIPTION */}

      <p
        className="
          mt-7
          max-w-2xl
          text-base
          leading-8
          text-slate-300
          sm:text-lg
        "
      >

        StayOps connects guests, hotel teams,
        bookings, payments, room operations and
        AI assistance through one secure enterprise
        workspace powered by React and Spring Boot
        microservices.

      </p>


      {/* CTA */}

      <div
        className="
          mt-9
          flex
          flex-col
          gap-3
          sm:flex-row
        "
      >

        <Link to="/register?role=GUEST">

          <Button
            className="
              h-12
              rounded-xl
              px-6
              text-sm
              font-bold
              shadow-lg
              shadow-cyan-500/20
            "
          >

            Explore as Guest

            <ArrowRight className="h-4 w-4" />

          </Button>

        </Link>


        <a href="#login-options">

          <Button
            variant="secondary"
            className="
              h-12
              rounded-xl
              border-white/10
              bg-white/10
              px-6
              text-white
              hover:bg-white/15
            "
          >

            Choose your portal

          </Button>

        </a>

      </div>


      {/* ENTERPRISE TECHNOLOGIES */}

      <div
        className="
          mt-10
          grid
          max-w-2xl
          gap-3
          sm:grid-cols-3
        "
      >

        {[
          [
            'JWT',
            'Role-based security'
          ],

          [
            'Eureka',
            'Service discovery'
          ],

          [
            'AI',
            'Live hotel intelligence'
          ]

        ].map(
          ([value, label]) => (

            <div
              key={value}
              className="
                rounded-2xl
                border
                border-white/10
                bg-white/[0.055]
                p-4
                backdrop-blur-xl
                transition
                duration-300
                hover:-translate-y-1
                hover:border-cyan-300/30
                hover:bg-white/[0.08]
              "
            >

              <p
                className="
                  text-xl
                  font-extrabold
                  text-white
                "
              >

                {value}

              </p>

              <p
                className="
                  mt-1
                  text-xs
                  leading-5
                  text-slate-400
                "
              >

                {label}

              </p>

            </div>

          )
        )}

      </div>

    </div>


    {/* ============================================ */}
    {/* RIGHT SIDE DASHBOARD MOCKUP                  */}
    {/* ============================================ */}

    <div
      className="
        relative
        mx-auto
        w-full
        max-w-xl
      "
    >

      {/* GLOW */}

      <div
        className="
          absolute
          -inset-6
          rounded-[2.5rem]
          bg-gradient-to-br
          from-cyan-400/20
          via-blue-500/10
          to-violet-500/20
          blur-3xl
        "
      />


      {/* DASHBOARD CARD */}

      <div
        className="
          relative
          overflow-hidden
          rounded-[2rem]
          border
          border-white/15
          bg-slate-950/75
          p-3
          shadow-[0_35px_100px_rgba(0,0,0,.60)]
          backdrop-blur-2xl
        "
      >

        {/* BROWSER BAR */}

        <div
          className="
            flex
            items-center
            justify-between
            rounded-t-[1.35rem]
            border-b
            border-white/10
            bg-white/[0.045]
            px-4
            py-3
          "
        >

          <div className="flex gap-1.5">

            <span className="h-2.5 w-2.5 rounded-full bg-rose-400" />

            <span className="h-2.5 w-2.5 rounded-full bg-amber-300" />

            <span className="h-2.5 w-2.5 rounded-full bg-emerald-400" />

          </div>


          <div
            className="
              rounded-lg
              border
              border-white/10
              bg-white/[0.04]
              px-4
              py-1.5
              text-[10px]
              font-medium
              text-slate-400
            "
          >

            stayops / command-center

          </div>

          <div className="w-10" />

        </div>


        {/* DASHBOARD BODY */}

        <div
          className="
            rounded-b-[1.35rem]
            bg-slate-900
            p-5
          "
        >

          <div
            className="
              flex
              items-center
              justify-between
              gap-4
            "
          >

            <div>

              <p
                className="
                  text-[10px]
                  font-bold
                  uppercase
                  tracking-[.18em]
                  text-cyan-400
                "
              >

                Live operations

              </p>

              <h3
                className="
                  mt-1
                  text-xl
                  font-bold
                  text-white
                "
              >

                Hotel Command Center

              </h3>

            </div>


            <Badge tone="green">

              Systems Live

            </Badge>

          </div>


          {/* STAT CARDS */}

          <div
            className="
              mt-5
              grid
              grid-cols-2
              gap-3
            "
          >

            {[
              [
                '84%',
                'Occupancy'
              ],

              [
                '128',
                'Bookings'
              ],

              [
                '17',
                'Open requests'
              ],

              [
                '₹8.4L',
                'Revenue'
              ]

            ].map(
              ([value, label]) => (

                <div
                  key={label}
                  className="
                    rounded-2xl
                    border
                    border-white/10
                    bg-white/[0.045]
                    p-4
                  "
                >

                  <p
                    className="
                      text-2xl
                      font-black
                      text-white
                    "
                  >

                    {value}

                  </p>

                  <p
                    className="
                      mt-1
                      text-xs
                      text-slate-400
                    "
                  >

                    {label}

                  </p>

                </div>

              )
            )}

          </div>


          {/* BOOKING CHART */}

          <div
            className="
              mt-3
              rounded-2xl
              border
              border-white/10
              bg-white/[0.035]
              p-4
            "
          >

            <div
              className="
                flex
                items-center
                justify-between
              "
            >

              <p
                className="
                  text-xs
                  font-semibold
                  text-white
                "
              >

                Booking activity

              </p>

              <span
                className="
                  text-[10px]
                  font-bold
                  text-emerald-300
                "
              >

                +18.6%

              </span>

            </div>


            <div
              className="
                mt-5
                flex
                h-28
                items-end
                gap-2
              "
            >

              {[
                35,
                48,
                42,
                67,
                55,
                78,
                64,
                86,
                73,
                91,
                82,
                96

              ].map(
                (
                  height,
                  index
                ) => (

                  <div
                    key={index}
                    className="
                      flex-1
                      rounded-t-md
                      bg-gradient-to-t
                      from-blue-600
                      to-cyan-300
                      transition-all
                      duration-500
                      hover:from-violet-500
                      hover:to-cyan-300
                    "
                    style={{
                      height:
                        `${height}%`
                    }}
                  />

                )
              )}

            </div>

          </div>


          {/* ACTIVITY CARDS */}

          <div
            className="
              mt-3
              grid
              gap-3
              sm:grid-cols-2
            "
          >

            <div
              className="
                rounded-2xl
                border
                border-white/10
                bg-white/[0.035]
                p-4
              "
            >

              <div
                className="
                  flex
                  items-center
                  gap-3
                "
              >

                <div
                  className="
                    grid
                    h-10
                    w-10
                    place-items-center
                    rounded-xl
                    bg-cyan-400/15
                    text-cyan-300
                  "
                >

                  <BedDouble className="h-5 w-5" />

                </div>

                <div>

                  <p
                    className="
                      text-sm
                      font-semibold
                      text-white
                    "
                  >

                    Room 402

                  </p>

                  <p
                    className="
                      text-[11px]
                      text-slate-400
                    "
                  >

                    Ready for check-in

                  </p>

                </div>

              </div>

            </div>


            <div
              className="
                rounded-2xl
                border
                border-white/10
                bg-white/[0.035]
                p-4
              "
            >

              <div
                className="
                  flex
                  items-center
                  gap-3
                "
              >

                <div
                  className="
                    grid
                    h-10
                    w-10
                    place-items-center
                    rounded-xl
                    bg-violet-400/15
                    text-violet-300
                  "
                >

                  <Bot className="h-5 w-5" />

                </div>

                <div>

                  <p
                    className="
                      text-sm
                      font-semibold
                      text-white
                    "
                  >

                    AI Concierge

                  </p>

                  <p
                    className="
                      text-[11px]
                      text-slate-400
                    "
                  >

                    Live tools enabled

                  </p>

                </div>

              </div>

            </div>

          </div>

        </div>

      </div>


      {/* FLOATING STATUS */}

      <div
        className="
          absolute
          -bottom-6
          -left-4
          hidden
          items-center
          gap-3
          rounded-2xl
          border
          border-white/15
          bg-slate-950/90
          p-4
          shadow-2xl
          backdrop-blur-xl
          sm:flex
        "
      >

        <div
          className="
            grid
            h-10
            w-10
            place-items-center
            rounded-xl
            bg-emerald-400/15
            text-emerald-300
          "
        >

          <CheckCircle2 className="h-5 w-5" />

        </div>


      </div>

    </div>

  </div>


  {/* BOTTOM FADE */}

  <div
    className="
      absolute
      inset-x-0
      bottom-0
      h-28
      bg-gradient-to-t
      from-white
      to-transparent
      dark:from-slate-950
    "
  />

</section>
  



        {/* ==================================================== */}
        {/* ROLE LOGIN / REGISTER                                */}
        {/* ==================================================== */}

        <section
          id="login-options"
          className="scroll-mt-24 px-5 pb-24 sm:px-8"
        >

          <div className="mx-auto max-w-7xl">


            {/* SECTION HEADING */}

            <div className="mx-auto max-w-3xl text-center">

              <p className="text-xs font-bold uppercase tracking-[.2em] text-cyan-600">

                Select your portal

              </p>


              <h2 className="mt-3 font-display text-4xl font-extrabold tracking-tight">

                Continue according to your role

              </h2>


              <p className="mt-4 text-slate-600 dark:text-slate-400">

                Choose Admin, Manager, Employee, or Guest.
                Login to an existing account or register
                a new account for the selected role.

              </p>

            </div>



            {/* ROLE CARDS */}

            <div className="mt-12 grid gap-5 sm:grid-cols-2 lg:grid-cols-4">

              {ROLE_OPTIONS.map((role) => (

                <div
                  key={role.value}
                  className="
                    group
                    rounded-2xl
                    border
                    border-slate-200
                    bg-white
                    p-6
                    shadow-sm
                    transition
                    hover:-translate-y-1
                    hover:border-cyan-400
                    hover:shadow-xl
                    dark:border-slate-800
                    dark:bg-slate-900
                  "
                >


                  {/* ICON + ROLE BADGE */}

                  <div className="flex items-center justify-between">

                    <div className="grid h-12 w-12 place-items-center rounded-xl bg-cyan-100 text-cyan-700 dark:bg-cyan-950 dark:text-cyan-300">

                      <ShieldCheck className="h-6 w-6" />

                    </div>


                    <Badge tone="blue">

                      {role.label}

                    </Badge>

                  </div>



                  {/* ROLE TITLE */}

                  <h3 className="mt-5 font-display text-xl font-bold">

                    {role.label}

                  </h3>



                  {/* ROLE DESCRIPTION */}

                  <p className="mt-2 min-h-[72px] text-sm leading-6 text-slate-500 dark:text-slate-400">

                    {role.description}

                  </p>



                  {/* BUTTONS */}

                  <div className="mt-5 grid grid-cols-2 gap-3">


                    {/* LOGIN */}

                    <Link
                      to={`/login?role=${role.value}`}
                      className="block"
                    >

                      <Button
                        variant="secondary"
                        className="w-full"
                      >

                        Login

                      </Button>

                    </Link>



                    {/* REGISTER */}

                    <Link
                      to={`/register?role=${role.value}`}
                      className="block"
                    >

                      <Button className="w-full">

                        Register

                      </Button>

                    </Link>

                  </div>

                </div>

              ))}

            </div>



            {/* SMALL GUEST SHORTCUT */}

            <div className="mt-8 text-center text-sm text-slate-500">

              Looking for a guest account?{' '}

              <Link
                to="/register?role=GUEST"
                className="font-bold text-cyan-600 hover:text-cyan-700"
              >

                Register as Guest

              </Link>

            </div>

          </div>

        </section>



        {/* ==================================================== */}
        {/* PLATFORM PREVIEW                                     */}
        {/* ==================================================== */}

        <section
          id="platform"
          className="px-5 pb-24 sm:px-8"
        >

          <div className="mx-auto max-w-7xl">


            <div className="rounded-[2rem] border border-slate-200 bg-slate-950 p-3 shadow-2xl shadow-slate-900/20 dark:border-slate-800">


              <div className="grid overflow-hidden rounded-[1.4rem] bg-slate-900 lg:grid-cols-[240px_1fr]">


                {/* SIDEBAR PREVIEW */}

                <div className="hidden border-r border-slate-800 p-5 lg:block">




                  

                </div>



                {/* DASHBOARD PREVIEW */}

                <div className="p-5 sm:p-8">


                



                



                  {/* GRAPH + ACTIVITY */}
{/* ACCORDION GALLERY */}

<div className="ml-4 mt-4 overflow-hidden rounded-2xl border border-slate-700 bg-slate-800 p-5">
  <div className="mb-5">
    <p className="text-sm font-semibold text-white">
      Property Experience
    </p>

    <p className="mt-1 text-xs text-slate-400">
      Explore hotel destinations and property highlights
    </p>
  </div>

  <AccordionGallery items={accordionItems} />
</div>

                </div>

              </div>

            </div>

          </div>

        </section>



        {/* ==================================================== */}
        {/* CAPABILITIES                                         */}
        {/* ==================================================== */}

        <section
          id="capabilities"
          className="bg-slate-50 px-5 py-24 dark:bg-slate-900/40 sm:px-8"
        >

          <div className="mx-auto max-w-7xl">


<section
  id="capabilities"
  className="bg-slate-50 px-5 py-24 dark:bg-slate-900/40 sm:px-8"
>
  <div className="mx-auto max-w-7xl">

    {/* SECTION HEADER */}

    <div className="max-w-2xl">

      <p className="text-xs font-bold uppercase tracking-[.2em] text-cyan-600">
        Connected hotel operations
      </p>

      <h2 className="mt-3 font-display text-4xl font-extrabold tracking-tight">
        Manage every property from one platform.
      </h2>

      <p className="mt-4 text-slate-600 dark:text-slate-300">
        StayOps brings hotels, rooms, guests, bookings, payments,
        and operational data together through a unified management platform.
      </p>

    </div>


    {/* HOTEL CARDS */}

    <div className="mt-12 grid grid-cols-1 gap-5 lg:grid-cols-2">

      {hotelCards.map((hotel) => (

        <div
          key={hotel.id}
          className="
            group
            flex
            items-center
            rounded-2xl
            border
            border-slate-200
            bg-white
            p-3
            transition-all
            duration-300
            hover:-translate-y-1
            hover:border-cyan-300
            hover:shadow-xl
            dark:border-slate-800
            dark:bg-slate-900
            dark:hover:border-cyan-500/40
          "
        >

          {/* HOTEL IMAGE */}

          <div className="h-36 w-36 shrink-0 overflow-hidden rounded-xl sm:h-40 sm:w-40">

            <img
              src={hotel.img}
              alt={hotel.title}
              className="
                h-full
                w-full
                object-cover
                transition-transform
                duration-500
                group-hover:scale-110
              "
            />

          </div>


          {/* HOTEL DATA */}

          <div className="ml-5 flex-1">

            <div className="flex items-start justify-between gap-3">

              <div>

                <h3 className="font-display text-lg font-bold text-slate-900 dark:text-white">
                  {hotel.title}
                </h3>

                <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
                  {hotel.location}
                </p>

              </div>

              <span
                className="
                  rounded-full
                  bg-emerald-100
                  px-2.5
                  py-1
                  text-[10px]
                  font-bold
                  text-emerald-700
                  dark:bg-emerald-950
                  dark:text-emerald-300
                "
              >
                {hotel.status}
              </span>

            </div>


            {/* HOTEL TYPE */}

            <p className="mt-4 text-sm font-semibold text-cyan-600 dark:text-cyan-400">
              {hotel.type}
            </p>


            {/* HOTEL STATS */}

            <div className="mt-3 flex flex-wrap items-center gap-2 text-xs text-slate-500 dark:text-slate-400">

              <span>
                {hotel.rooms}
              </span>

              <div className="size-1 rounded-full bg-slate-400" />

              <span>
                Rating {hotel.rating}
              </span>

              <div className="size-1 rounded-full bg-slate-400" />

              <span>
                Live Operations
              </span>

            </div>

          </div>

        </div>

      ))}

    </div>


    {/* MANAGEMENT DATA STRIP */}

    <div className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">

      {[
        ['128+', 'Rooms Managed'],
        ['84%', 'Average Occupancy'],
        ['2,450+', 'Bookings Processed'],
        ['₹8.4L', 'Revenue Tracked']
      ].map(([value, label]) => (

        <div
          key={label}
          className="
            rounded-2xl
            border
            border-slate-200
            bg-white
            p-5
            dark:border-slate-800
            dark:bg-slate-900
          "
        >

          <p className="text-2xl font-black text-slate-900 dark:text-white">
            {value}
          </p>

          <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
            {label}
          </p>

        </div>

      ))}

    </div>

  </div>
</section>  

          </div>

        </section>



        {/* ==================================================== */}
        {/* PROPERTIES                                           */}
        {/* ==================================================== */}

        <section
          id="properties"
          className="px-5 py-24 sm:px-8"
        >

          <div className="mx-auto max-w-7xl">


            <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-end">


              <div>

                <p className="text-xs font-bold uppercase tracking-[.2em] text-cyan-600">

                  Live property directory

                </p>


                <h2 className="mt-3 font-display text-4xl font-extrabold tracking-tight">

                  Hotels from your backend.

                </h2>

              </div>



              <Link to="/explore">

                <Button variant="secondary">

                  View all properties

                  <ArrowRight className="h-4 w-4" />

                </Button>

              </Link>

            </div>



            <div className="mt-10 grid gap-5 md:grid-cols-2 lg:grid-cols-3">


              {(hotels.length

                ? hotels.slice(0, 6)

                : [

                    {
                      id: 'preview-1',
                      name: 'Grand Meridian',
                      location: 'Bengaluru',
                      about:
                        'Connect your backend to display live properties.'
                    },

                    {
                      id: 'preview-2',
                      name: 'Harbour Heights',
                      location: 'Mumbai',
                      about:
                        'Live hotel data appears here automatically.'
                    },

                    {
                      id: 'preview-3',
                      name: 'Mountain Crest',
                      location: 'Manali',
                      about:
                        'Explore rooms, pricing and availability.'
                    }

                  ]

              ).map((hotel, index) => (

                <Card
                  key={
                    hotel.id ??
                    hotel.hotelId ??
                    `${hotel.name}-${index}`
                  }

                  className="overflow-hidden"
                >


                  {/* PROPERTY IMAGE PLACEHOLDER */}

                  <div
                    className={`h-44 bg-gradient-to-br ${
                      [
                        'from-cyan-400 to-blue-700',
                        'from-violet-400 to-fuchsia-700',
                        'from-amber-300 to-rose-600'
                      ][index % 3]
                    } p-6`}
                  >


                    <div className="flex items-start justify-between">


                      <div className="rounded-xl bg-white/20 p-3 backdrop-blur">

                        <Hotel className="h-6 w-6 text-white" />

                      </div>


                      <Badge tone="green">
                        Live
                      </Badge>

                    </div>

                  </div>



                  {/* PROPERTY INFORMATION */}

                  <div className="p-6">


                    <div className="flex items-start justify-between gap-4">


                      <div>

                        <h3 className="font-display text-xl font-bold">

                          {hotel.name || 'Hotel'}

                        </h3>


                        <p className="mt-1 text-sm text-slate-500">

                          {hotel.location || 'Location unavailable'}

                        </p>

                      </div>



                      <div className="flex items-center gap-1 text-sm font-bold">

                        <Star className="h-4 w-4 fill-amber-400 text-amber-400" />

                        4.8

                      </div>

                    </div>



                    <p className="mt-4 line-clamp-2 text-sm leading-6 text-slate-600 dark:text-slate-400">

                      {
                        hotel.about
                        ||
                        'A connected property in the StayOps hotel network.'
                      }

                    </p>

                  </div>

                </Card>

              ))}

            </div>

          </div>

        </section>

      </main>



      {/* ====================================================== */}
      {/* FOOTER                                                 */}
      {/* ====================================================== */}

      <footer className="border-t border-slate-200 bg-slate-950 px-5 py-10 text-slate-400 dark:border-slate-800 sm:px-8">


        <div className="mx-auto flex max-w-7xl flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">


          <div className="flex items-center gap-3">


            <div className="grid h-9 w-9 place-items-center rounded-xl bg-cyan-400 text-slate-950">

              <Hotel className="h-4 w-4" />

            </div>


            <span className="font-display font-bold text-white">

              StayOps

            </span>

          </div>



          <p className="text-sm">

            Enterprise Hotel Management System · React + Tailwind CSS

          </p>

        </div>

      </footer>

    </div>
  )
}