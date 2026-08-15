import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'

import {
  ArrowLeft,
  BedDouble,
  Hotel,
  MapPin,
  Search,
  Star
} from 'lucide-react'

import {
  hotelsApi,
  roomsApi
} from '../api/services'

import {
  Badge,
  Button,
  Card,
  Input,
  Spinner,
  statusTone
} from '../components/ui'

import {
  formatCurrency
} from '../lib/utils'


export default function ExplorePage() {

  const [hotels, setHotels] = useState([])
  const [rooms, setRooms] = useState([])
  const [loading, setLoading] = useState(true)
  const [query, setQuery] = useState('')


  // ============================================================
  // LOAD HOTELS + ROOMS
  // ============================================================

  useEffect(() => {

    Promise.allSettled([
      hotelsApi.list(),
      roomsApi.list()
    ])
      .then(([hotelResult, roomResult]) => {

        if (
          hotelResult.status === 'fulfilled'
          &&
          Array.isArray(hotelResult.value)
        ) {
          setHotels(hotelResult.value)
        }


        if (
          roomResult.status === 'fulfilled'
          &&
          Array.isArray(roomResult.value)
        ) {
          setRooms(roomResult.value)
        }

      })
      .finally(() => {

        setLoading(false)

      })

  }, [])


  // ============================================================
  // SEARCH
  // ============================================================

  const visible = useMemo(() => {

    const search =
      query
        .trim()
        .toLowerCase()


    if (!search) {
      return hotels
    }


    return hotels.filter((hotel) => {

      const name =
        hotel?.name || ''

      const location =
        hotel?.location || ''


      return `${name} ${location}`
        .toLowerCase()
        .includes(search)

    })

  }, [hotels, query])


  // ============================================================
  // UI
  // ============================================================

  return (

    <div className="min-h-screen bg-white dark:bg-slate-950">


      {/* ====================================================== */}
      {/* HEADER                                                 */}
      {/* ====================================================== */}

      <header className="border-b border-slate-200 bg-white dark:border-slate-800 dark:bg-slate-950">

        <div className="mx-auto flex h-20 max-w-7xl items-center justify-between px-5 sm:px-8">


          {/* LEFT SIDE */}

          <div className="flex items-center gap-4">


            <Link to="/">

              <Button
                variant="ghost"
                size="sm"
              >

                <ArrowLeft className="h-4 w-4" />

                Back

              </Button>

            </Link>


            <Link
              to="/"
              className="flex items-center gap-3"
            >

              <div className="grid h-10 w-10 place-items-center rounded-xl bg-cyan-400 text-slate-950">

                <Hotel className="h-5 w-5" />

              </div>


              <div>

                <p className="font-display text-lg font-extrabold text-slate-900 dark:text-white">

                  StayOps

                </p>

                <p className="text-[9px] font-bold uppercase tracking-[.2em] text-slate-400">

                  Hotel Directory

                </p>

              </div>

            </Link>

          </div>


          {/* LOGIN */}

          <Link to="/login?role=GUEST">

            <Button>

              Sign in to book

            </Button>

          </Link>

        </div>

      </header>



      {/* ====================================================== */}
      {/* MAIN                                                   */}
      {/* ====================================================== */}

      <main className="mx-auto max-w-7xl px-5 py-12 sm:px-8">


        {/* INTRODUCTION */}

        <div className="max-w-3xl">

          <p className="text-xs font-bold uppercase tracking-[.2em] text-cyan-600">

            Public directory

          </p>


          <h1 className="mt-3 font-display text-4xl font-extrabold tracking-tight text-slate-900 dark:text-white sm:text-5xl">

            Find your next stay.

          </h1>


          <p className="mt-4 text-slate-600 dark:text-slate-400">

            Live hotels and rooms exposed by
            HotelService through the API Gateway.

          </p>

        </div>



        {/* ====================================================== */}
        {/* SEARCH                                                 */}
        {/* ====================================================== */}

        <div className="relative mt-8 max-w-xl">

          <Search className="absolute left-4 top-1/2 h-5 w-5 -translate-y-1/2 text-slate-400" />


          <Input
            value={query}
            onChange={(event) =>
              setQuery(event.target.value)
            }
            className="py-3.5 pl-12"
            placeholder="Search by hotel or location"
          />

        </div>



        {/* ====================================================== */}
        {/* LOADING                                                */}
        {/* ====================================================== */}

        {loading ? (

          <div className="mt-12">

            <Spinner />

          </div>

        ) : (

          <div className="mt-10 space-y-8">


            {/* ================================================== */}
            {/* HOTELS                                             */}
            {/* ================================================== */}

            {visible.map((hotel) => {


              /*
               * Your room backend may return either:
               *
               * room.hotel.id
               *
               * OR
               *
               * room.hotelId
               *
               * Therefore support both.
               */

              const hotelRooms =
                rooms.filter((room) => {

                  const roomHotelId =
                    room?.hotel?.id ??
                    room?.hotelId


                  return (
                    roomHotelId !== undefined
                    &&
                    hotel?.id !== undefined
                    &&
                    String(roomHotelId)
                    ===
                    String(hotel.id)
                  )

                })


              return (

                <Card
                  key={hotel.id}
                  className="overflow-hidden"
                >


                  <div className="grid lg:grid-cols-[300px_1fr]">


                    {/* HOTEL IMAGE/GRADIENT AREA */}

                    <div className="flex min-h-56 flex-col justify-between bg-gradient-to-br from-cyan-400 via-blue-500 to-indigo-700 p-7 text-white">


                      <Hotel className="h-9 w-9" />


                      <div>

                        <h2 className="font-display text-2xl font-extrabold">

                          {hotel.name}

                        </h2>


                        <p className="mt-2 flex items-center gap-2 text-sm text-white/80">

                          <MapPin className="h-4 w-4 shrink-0" />

                          {hotel.location}

                        </p>

                      </div>

                    </div>



                    {/* HOTEL INFORMATION */}

                    <div className="flex flex-col justify-between p-6 sm:p-8">


                      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">


                        <p className="max-w-2xl text-sm leading-7 text-slate-600 dark:text-slate-400">

                          {
                            hotel.about
                            ||
                            'A connected StayOps property.'
                          }

                        </p>


                        <div className="flex shrink-0 items-center gap-1 font-bold text-slate-900 dark:text-white">

                          <Star className="h-4 w-4 fill-amber-400 text-amber-400" />

                          4.8

                        </div>

                      </div>



                      {/* ========================================== */}
                      {/* ROOMS                                      */}
                      {/* ========================================== */}

                      <div className="mt-6 grid gap-3 sm:grid-cols-2 xl:grid-cols-3">


                        {hotelRooms.length > 0 ? (

                          hotelRooms
                            .slice(0, 6)
                            .map((room) => (

                              <div
                                key={
                                  room.roomId
                                  ??
                                  room.id
                                  ??
                                  room.roomNumber
                                }
                                className="rounded-2xl border border-slate-200 bg-slate-50/50 p-4 dark:border-slate-800 dark:bg-slate-900/50"
                              >


                                <div className="flex items-center justify-between">


                                  <BedDouble className="h-5 w-5 text-cyan-600" />


                                  <Badge
                                    tone={
                                      statusTone(
                                        room.roomStatus
                                        ||
                                        'UNKNOWN'
                                      )
                                    }
                                  >

                                    {
                                      room.roomStatus
                                      ||
                                      'UNKNOWN'
                                    }

                                  </Badge>

                                </div>



                                <p className="mt-4 font-bold text-slate-900 dark:text-white">

                                  Room {room.roomNumber}

                                </p>



                                <p className="mt-1 text-xs text-slate-500">

                                  {
                                    room.roomType
                                    ||
                                    'Room'
                                  }

                                  {' · '}

                                  {
                                    room.capacity
                                    ??
                                    '-'
                                  }

                                  {' guests'}

                                </p>



                                <p className="mt-3 text-sm font-extrabold text-slate-900 dark:text-white">

                                  {
                                    room.pricePerNight !== undefined
                                    &&
                                    room.pricePerNight !== null

                                      ? formatCurrency(
                                          room.pricePerNight
                                        )

                                      : 'Price unavailable'
                                  }


                                  {
                                    room.pricePerNight !== undefined
                                    &&
                                    room.pricePerNight !== null
                                    &&
                                    (

                                      <span className="font-normal text-slate-400">

                                        {' '}
                                        / night

                                      </span>

                                    )
                                  }

                                </p>

                              </div>

                            ))

                        ) : (

                          <div className="col-span-full rounded-2xl border border-dashed border-slate-300 p-6 text-sm text-slate-500 dark:border-slate-700">

                            No rooms returned for this hotel.

                          </div>

                        )}

                      </div>

                    </div>

                  </div>

                </Card>

              )

            })}



            {/* ================================================== */}
            {/* EMPTY SEARCH RESULT                                */}
            {/* ================================================== */}

            {visible.length === 0 && (

              <Card className="p-12 text-center text-slate-500">

                No hotels match your search.

              </Card>

            )}

          </div>

        )}

      </main>

    </div>

  )
}