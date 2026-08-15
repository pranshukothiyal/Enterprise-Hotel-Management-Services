import { useEffect, useState } from 'react'
import { CreditCard } from 'lucide-react'
import toast from 'react-hot-toast'

import CrudPage, {
  moneyColumn,
  statusColumn
} from '../components/CrudPage'

import {
  Badge,
  Button,
  Modal,
  Select,
  statusTone
} from '../components/ui'

import {
  bookingsApi,
  departmentsApi,
  employeesApi,
  hotelsApi,
  invoicesApi,
  offeringsApi,
  ratingsApi,
  roomsApi,
  usersApi
} from '../api/services'

import {
  errorMessage,
  formatCurrency,
  formatDate,
  formatDateTime
} from '../lib/utils'

import {
  ROLES,
  permissions,
  hasPermission
} from '../lib/permissions'

import {
  useAuth
} from '../context/AuthContext'


// ============================================================
// CONSTANTS
// ============================================================

const roomStatuses = [
  'AVAILABLE',
  'RESERVED',
  'OCCUPIED',
  'MAINTENANCE',
  'UNAVAILABLE'
]

const roomTypes = [
  'STANDARD',
  'DELUXE',
  'SUITE',
  'EXECUTIVE'
]

const employeeStatuses = [
  'ACTIVE',
  'INACTIVE',
  'ON_LEAVE',
  'TERMINATED'
]

const bookingStatuses = [
  'PENDING',
  'CONFIRMED',
  'CHECKED_IN',
  'CHECKED_OUT',
  'CANCELLED'
]

const serviceStatuses = [
  'AVAILABLE',
  'UNAVAILABLE'
]


// ============================================================
// HELPERS
// ============================================================

function normalizeArray(value) {

  if (Array.isArray(value)) {
    return value
  }

  if (Array.isArray(value?.content)) {
    return value.content
  }

  if (Array.isArray(value?.data)) {
    return value.data
  }

  return []
}


const options = (
  rows = [],
  valueKey,
  labelKey
) =>

  rows.map(item => ({

    value:
      item?.[valueKey],

    label:
      `${item?.[labelKey] ?? 'Unknown'} (${item?.[valueKey] ?? '-'})`
  }))


// ============================================================
// REFERENCE DATA
// ============================================================

function useReferenceData(
  includeDepartments = false
) {

  const {
    user
  } = useAuth()


  const [
    context,
    setContext
  ] = useState({
    hotels: [],
    departments: []
  })


  useEffect(() => {

    let active = true


    async function loadReferenceData() {

      let hotels = []
      let departments = []


      // ======================================================
      // HOTELS
      // ======================================================

      try {

        if (
          user?.role === ROLES.MANAGER
          &&
          user?.hotelId
        ) {

          const hotel =
            await hotelsApi.get(
              user.hotelId
            )


          hotels =
            hotel
              ? [hotel]
              : []

        } else {

          hotels =
            normalizeArray(
              await hotelsApi.list()
            )
        }


      } catch (error) {

        console.warn(
          'Hotels could not be loaded:',
          error
        )
      }


      // ======================================================
      // DEPARTMENTS
      // ======================================================

      if (includeDepartments) {

        try {

          if (
            user?.role === ROLES.MANAGER
            &&
            user?.hotelId
          ) {

            departments =
              normalizeArray(

                await departmentsApi.byHotel(
                  user.hotelId
                )
              )

          } else if (
            user?.role === ROLES.ADMIN
          ) {

            departments =
              normalizeArray(
                await departmentsApi.list()
              )
          }


        } catch (error) {

          console.warn(
            'Departments could not be loaded:',
            error
          )
        }
      }


      if (active) {

        setContext({
          hotels,
          departments
        })
      }
    }


    loadReferenceData()


    return () => {

      active = false
    }

  }, [
    includeDepartments,
    user?.role,
    user?.hotelId
  ])


  return context
}


// ============================================================
// QUICK STATUS
// ============================================================

function QuickStatus({
  value,
  values,
  onChange,
  refresh
}) {

  const [
    loading,
    setLoading
  ] = useState(false)


  return (

    <Select
      aria-label="Update status"

      value={
        value || ''
      }

      disabled={
        loading
      }

      className="w-36 py-1.5 text-xs"

      onClick={
        event =>
          event.stopPropagation()
      }

      onChange={
        async event => {

          setLoading(true)


          try {

            await onChange(
              event.target.value
            )


            toast.success(
              'Status updated'
            )


            await refresh()


          } catch (error) {

            toast.error(
              errorMessage(error)
            )


          } finally {

            setLoading(false)
          }
        }
      }
    >

      {
        values.map(
          item => (

            <option
              key={item}
              value={item}
            >

              {item}

            </option>
          )
        )
      }

    </Select>
  )
}


// ============================================================
// BOOKING PAYMENT DETAILS
// ============================================================

function PaymentDetailsAction({
  booking
}) {

  const [
    details,
    setDetails
  ] = useState(null)


  const [
    loading,
    setLoading
  ] = useState(false)


  return (
    <>

      <Button
        variant="ghost"
        size="sm"
        title="Payment details"
        loading={loading}

        onClick={
          async () => {

            setLoading(true)


            try {

              const response =
                await bookingsApi.paymentDetails(
                  booking.bookingId
                )


              setDetails(
                response
              )


            } catch (error) {

              toast.error(
                errorMessage(error)
              )


            } finally {

              setLoading(false)
            }
          }
        }
      >

        <CreditCard className="h-4 w-4" />

      </Button>


      <Modal
        open={
          Boolean(details)
        }

        onClose={
          () =>
            setDetails(null)
        }

        title="Booking payment details"

        footer={

          <Button
            variant="secondary"

            onClick={
              () =>
                setDetails(null)
            }
          >

            Close

          </Button>
        }
      >

        <div className="grid gap-4 sm:grid-cols-2">

          <Detail
            label="Booking ID"

            value={
              details?.bookingId
            }
          />


          <Detail
            label="Amount"

            value={
              formatCurrency(
                details?.totalAmount
              )
            }
          />


          <Detail
            label="Booking status"

            value={

              <Badge
                tone={
                  statusTone(
                    details?.bookingStatus
                  )
                }
              >

                {details?.bookingStatus}

              </Badge>
            }
          />

        </div>

      </Modal>

    </>
  )
}


// ============================================================
// DETAIL
// ============================================================

function Detail({
  label,
  value
}) {

  return (

    <div className="rounded-2xl border border-slate-200 p-4 dark:border-slate-800">

      <p className="text-xs font-bold uppercase tracking-wide text-slate-400">

        {label}

      </p>


      <div className="mt-2 break-all text-sm font-semibold">

        {value || '—'}

      </div>

    </div>
  )
}


// ============================================================
// HOTELS
// ============================================================

export function HotelsPage() {

  const {
    user
  } = useAuth()


  const isGuest =
    user?.role === ROLES.GUEST


  const isManager =
    user?.role === ROLES.MANAGER


  const canCreate =
    hasPermission(
      user?.role,
      permissions.hotelCreate
    )


  const loadRows =
    async () => {


      if (isManager) {

        if (!user?.hotelId) {

          return []
        }


        const hotel =
          await hotelsApi.get(
            user.hotelId
          )


        return hotel
          ? [hotel]
          : []
      }


      return normalizeArray(
        await hotelsApi.list()
      )
    }


  return (

    <CrudPage
      title={
        isGuest
          ? 'Explore Hotels'
          : isManager
          ? 'My Hotel'
          : 'Hotels'
      }

      idKey="id"

      loadRows={
        loadRows
      }

      getRow={
        hotelsApi.get
      }

      createRow={
        canCreate
          ? hotelsApi.create
          : undefined
      }

      allowEdit={false}
      allowDelete={false}

      searchKeys={[
        'id',
        'name',
        'location',
        'about'
      ]}

      fields={[
        {
          name: 'id',
          label: 'Hotel ID',
          required: true,
          placeholder: 'HTL-001'
        },

        {
          name: 'name',
          label: 'Hotel name',
          required: true
        },

        {
          name: 'location',
          label: 'Location',
          required: true
        },

        {
          name: 'about',
          label: 'About',
          type: 'textarea',
          full: true
        }
      ]}

      columns={[
        {
          key: 'id',
          label: 'Hotel ID'
        },

        {
          key: 'name',
          label: 'Name'
        },

        {
          key: 'location',
          label: 'Location'
        },

        {
          key: 'about',
          label: 'About'
        }
      ]}

      createLabel="Add hotel"
    />
  )
}


// ============================================================
// USERS / GUESTS
// ============================================================

export function UsersPage() {

  const {
    user
  } = useAuth()


  const isAdmin =
    user?.role === ROLES.ADMIN


  const isManager =
    user?.role === ROLES.MANAGER


  const loadRows =
    async () => {


      // ======================================================
      // ADMIN
      // ======================================================

      if (isAdmin) {

        return normalizeArray(
          await usersApi.list()
        )
      }


      // ======================================================
      // HOTEL MANAGER
      // ======================================================
      //
      // Manager does NOT call:
      //
      // /users
      // /users/{id}
      //
      // Guest IDs come from Hotel bookings.
      // ======================================================

      if (isManager) {

        if (!user?.hotelId) {

          return []
        }


        const bookings =
          normalizeArray(

            await bookingsApi.byHotel(
              user.hotelId
            )
          )


        const guests =
          new Map()


        bookings.forEach(
          booking => {

            const guestId =
              booking?.userId


            if (!guestId) {

              return
            }


            const existing =
              guests.get(
                guestId
              )


            if (existing) {

              existing.bookingCount += 1

              return
            }


            guests.set(
              guestId,
              {
                userId:
                  guestId,

                name:
                  booking?.userName
                  ??
                  booking?.guestName
                  ??
                  'Hotel Guest',

                email:
                  booking?.userEmail
                  ??
                  booking?.email
                  ??
                  '—',

                about:
                  `Guest with booking at hotel ${user.hotelId}`,

                bookingCount:
                  1
              }
            )
          }
        )


        return [
          ...guests.values()
        ]
      }


      return []
    }


  return (

    <CrudPage
      title={
        isManager
          ? 'Hotel Guests'
          : 'Guests'
      }

      idKey="userId"

      loadRows={
        loadRows
      }

      getRow={
        isAdmin
          ? usersApi.get
          : undefined
      }

      createRow={
        isAdmin
          ? usersApi.create
          : undefined
      }

      allowEdit={false}
      allowDelete={false}

      searchKeys={[
        'userId',
        'name',
        'email',
        'about'
      ]}

      fields={
        isAdmin
          ? [
              {
                name: 'userId',
                label: 'User ID',
                required: true
              },

              {
                name: 'name',
                label: 'Name',
                required: true
              },

              {
                name: 'email',
                label: 'Email',
                type: 'email',
                required: true
              },

              {
                name: 'about',
                label: 'About',
                type: 'textarea',
                full: true
              }
            ]
          : []
      }

      columns={[
        {
          key: 'userId',
          label: 'User ID'
        },

        {
          key: 'name',
          label: 'Name'
        },

        {
          key: 'email',
          label: 'Email'
        },

        ...(
          isManager
            ? [
                {
                  key: 'bookingCount',
                  label: 'Bookings'
                }
              ]
            : [
                {
                  key: 'about',
                  label: 'About'
                }
              ]
        )
      ]}

      createLabel="Add guest"
    />
  )
}


// ============================================================
// ROOMS
// ============================================================

export function RoomsPage() {

  const {
    user
  } = useAuth()


  const context =
    useReferenceData(false)


  const isGuest =
    user?.role === ROLES.GUEST


  const isManager =
    user?.role === ROLES.MANAGER


  const isEmployee =
    user?.role === ROLES.EMPLOYEE


  const canCreate =
    hasPermission(
      user?.role,
      permissions.roomCreate
    )


  const canEdit =
    hasPermission(
      user?.role,
      permissions.roomEdit
    )


  const canDelete =
    hasPermission(
      user?.role,
      permissions.roomDelete
    )


  const canUpdateStatus =
    hasPermission(
      user?.role,
      permissions.roomStatusUpdate
    )


  const loadRows =
    async filters => {


      // ======================================================
      // MANAGER
      // ======================================================

      if (isManager) {

        if (!user?.hotelId) {

          return []
        }


        if (filters.status) {

          return normalizeArray(

            await roomsApi.byHotelStatus(
              user.hotelId,
              filters.status
            )
          )
        }


        if (filters.roomType) {

          return normalizeArray(

            await roomsApi.byHotelType(
              user.hotelId,
              filters.roomType
            )
          )
        }


        return normalizeArray(

          await roomsApi.byHotel(
            user.hotelId
          )
        )
      }


      // ======================================================
      // EMPLOYEE
      // ======================================================

      if (isEmployee) {

        if (!user?.hotelId) {

          return []
        }


        return normalizeArray(

          await roomsApi.byHotel(
            user.hotelId
          )
        )
      }


      // ======================================================
      // GUEST
      // ======================================================

      if (isGuest) {

        let rows = []


        if (filters.hotelId) {

          rows =
            normalizeArray(

              await roomsApi.byHotel(
                filters.hotelId
              )
            )

        } else {

          rows =
            normalizeArray(
              await roomsApi.list()
            )
        }


        return rows.filter(
          room =>
            room.roomStatus ===
            'AVAILABLE'
        )
      }


      // ======================================================
      // ADMIN
      // ======================================================

      if (filters.hotelId) {

        if (filters.status) {

          return normalizeArray(

            await roomsApi.byHotelStatus(
              filters.hotelId,
              filters.status
            )
          )
        }


        if (filters.roomType) {

          return normalizeArray(

            await roomsApi.byHotelType(
              filters.hotelId,
              filters.roomType
            )
          )
        }


        return normalizeArray(

          await roomsApi.byHotel(
            filters.hotelId
          )
        )
      }


      return normalizeArray(
        await roomsApi.list()
      )
    }


  return (

    <CrudPage
      title={
        isGuest
          ? 'Available Rooms'
          : 'Rooms'
      }

      idKey="roomId"

      context={
        context
      }

      getRow={
        roomsApi.get
      }

      loadRows={
        loadRows
      }

      createRow={
        canCreate

          ? payload =>

              roomsApi.create(

                isManager
                  ? user.hotelId
                  : payload.__hotelId,

                payload.room
              )

          : undefined
      }

      updateRow={
        canEdit

          ? (
              id,
              payload
            ) =>

              roomsApi.update(
                id,
                payload.room
              )

          : undefined
      }

      deleteRow={
        canDelete
          ? roomsApi.remove
          : undefined
      }

      allowEdit={
        canEdit
      }

      allowDelete={
        canDelete
      }

      prepareForm={
        row => ({

          ...row,

          hotelId:
            row.hotel?.id
            ??
            row.hotelId
            ??
            ''
        })
      }

      toPayload={
        form => ({

          __hotelId:
            isManager
              ? user.hotelId
              : form.hotelId,

          room: {

            roomNumber:
              form.roomNumber,

            roomType:
              form.roomType,

            pricePerNight:
              Number(
                form.pricePerNight
              ),

            capacity:
              Number(
                form.capacity
              ),

            floorNumber:
              form.floorNumber === ''
                ? null
                : Number(
                    form.floorNumber
                  ),

            roomStatus:
              form.roomStatus
          }
        })
      }

      filters={[
        ...(
          isManager
          || isEmployee

            ? []

            : [
                {
                  name: 'hotelId',
                  label: 'Hotel',

                  options: () =>
                    options(
                      context.hotels,
                      'id',
                      'name'
                    )
                }
              ]
        ),

        ...(
          isGuest

            ? []

            : [
                {
                  name: 'status',
                  label: 'Status',
                  options: roomStatuses
                },

                {
                  name: 'roomType',
                  label: 'Room type',
                  options: roomTypes
                }
              ]
        )
      ]}

      searchKeys={[
        'roomId',
        'roomNumber',
        'roomType',
        'roomStatus',
        'hotel.name',
        'hotel.location'
      ]}

      fields={[
        ...(
          isManager

            ? []

            : [
                {
                  name: 'hotelId',
                  label: 'Hotel',
                  type: 'select',
                  required: true,

                  options: () =>
                    options(
                      context.hotels,
                      'id',
                      'name'
                    )
                }
              ]
        ),

        {
          name: 'roomNumber',
          label: 'Room number',
          required: true
        },

        {
          name: 'roomType',
          label: 'Room type',
          type: 'select',
          required: true,
          options: roomTypes
        },

        {
          name: 'pricePerNight',
          label: 'Price per night',
          type: 'number',
          step: '0.01',
          min: '0.01',
          required: true
        },

        {
          name: 'capacity',
          label: 'Capacity',
          type: 'number',
          min: '1',
          required: true
        },

        {
          name: 'floorNumber',
          label: 'Floor',
          type: 'number'
        },

        {
          name: 'roomStatus',
          label: 'Status',
          type: 'select',
          options: roomStatuses,
          defaultValue: 'AVAILABLE'
        }
      ]}

      columns={[
        {
          key: 'roomNumber',
          label: 'Room'
        },

        {
          key: 'hotel.name',
          label: 'Hotel'
        },

        {
          key: 'roomType',
          label: 'Type'
        },

        moneyColumn(
          'pricePerNight',
          'Nightly price'
        ),

        {
          key: 'capacity',
          label: 'Capacity'
        },

        ...(
          isGuest

            ? []

            : [
                {
                  key: 'floorNumber',
                  label: 'Floor'
                },

                statusColumn(
                  'roomStatus'
                )
              ]
        )
      ]}

      customActions={
        canUpdateStatus

          ? (
              row,
              refresh
            ) => (

              <QuickStatus
                value={
                  row.roomStatus
                }

                values={
                  roomStatuses
                }

                refresh={
                  refresh
                }

                onChange={
                  status =>

                    roomsApi.updateStatus(
                      row.roomId,
                      status
                    )
                }
              />
            )

          : undefined
      }

      createLabel="Add room"
    />
  )
}


// ============================================================
// BOOKINGS
// ============================================================

export function BookingsPage() {

  const {
    user
  } = useAuth()


  const context =
    useReferenceData(false)


  const isGuest =
    user?.role === ROLES.GUEST


  const isManager =
    user?.role === ROLES.MANAGER


  const canCreate =
    hasPermission(
      user?.role,
      permissions.bookingCreate
    )


  const canEdit =
    hasPermission(
      user?.role,
      permissions.bookingEdit
    )


  const canDelete =
    hasPermission(
      user?.role,
      permissions.bookingDelete
    )


  const loadRows =
    filters => {


      // ======================================================
      // GUEST
      // ======================================================

      if (isGuest) {

        if (!user?.userId) {

          return Promise.resolve([])
        }


        return bookingsApi.byUser(
          user.userId
        )
      }


      // ======================================================
      // MANAGER
      // ======================================================

      if (isManager) {

        if (!user?.hotelId) {

          return Promise.resolve([])
        }


        return bookingsApi.byHotel(
          user.hotelId
        )
      }


      // ======================================================
      // ADMIN
      // ======================================================

      if (filters.userId) {

        return bookingsApi.byUser(
          filters.userId
        )
      }


      if (filters.hotelId) {

        return bookingsApi.byHotel(
          filters.hotelId
        )
      }


      return bookingsApi.list()
    }


  return (

    <CrudPage
      title={
        isGuest
          ? 'My Bookings'
          : isManager
          ? 'Hotel Bookings'
          : 'Bookings'
      }

      idKey="bookingId"

      context={
        context
      }

      getRow={
        bookingsApi.get
      }

      loadRows={
        loadRows
      }

      createRow={
        canCreate
          ? bookingsApi.create
          : undefined
      }

      updateRow={
        canEdit
          ? bookingsApi.update
          : undefined
      }

      deleteRow={
        canDelete
          ? bookingsApi.remove
          : undefined
      }

      allowEdit={
        canEdit
      }

      allowDelete={
        canDelete
      }

      toPayload={
        form => ({

          userId:
            isGuest
              ? user.userId
              : form.userId,

          hotelId:
            isManager
              ? user.hotelId
              : form.hotelId,

          checkInDate:
            form.checkInDate,

          checkOutDate:
            form.checkOutDate,

          totalAmount:
            Number(
              form.totalAmount
            ),

          bookingStatus:
            isGuest
              ? 'PENDING'
              : (
                  form.bookingStatus
                  ||
                  'PENDING'
                )
        })
      }

      filters={
        isGuest || isManager

          ? []

          : [
              {
                name: 'userId',
                label: 'User ID',
                type: 'text'
              },

              {
                name: 'hotelId',
                label: 'Hotel',

                options: () =>
                  options(
                    context.hotels,
                    'id',
                    'name'
                  )
              }
            ]
      }

      searchKeys={[
        'bookingId',
        'userId',
        'hotelId',
        'bookingStatus',
        'checkInDate',
        'checkOutDate'
      ]}

      fields={[
        ...(
          isGuest

            ? []

            : [
                {
                  name: 'userId',
                  label: 'User ID',
                  required: true
                }
              ]
        ),

        ...(
          isManager

            ? []

            : [
                {
                  name: 'hotelId',
                  label: 'Hotel',
                  type: 'select',
                  required: true,

                  options: () =>
                    options(
                      context.hotels,
                      'id',
                      'name'
                    )
                }
              ]
        ),

        {
          name: 'checkInDate',
          label: 'Check-in',
          type: 'date',
          required: true
        },

        {
          name: 'checkOutDate',
          label: 'Check-out',
          type: 'date',
          required: true
        },

        {
          name: 'totalAmount',
          label: 'Total amount',
          type: 'number',
          min: '0',
          step: '0.01',
          required: true
        },

        ...(
          isGuest

            ? []

            : [
                {
                  name: 'bookingStatus',
                  label: 'Status',
                  type: 'select',
                  options: bookingStatuses,
                  defaultValue: 'PENDING'
                }
              ]
        )
      ]}

      columns={[
        {
          key: 'bookingId',
          label: 'Booking ID'
        },

        ...(
          isGuest

            ? []

            : [
                {
                  key: 'userId',
                  label: 'Guest'
                }
              ]
        ),

        {
          key: 'hotelId',
          label: 'Hotel'
        },

        {
          key: 'checkInDate',
          label: 'Check-in',
          render: formatDate
        },

        {
          key: 'checkOutDate',
          label: 'Check-out',
          render: formatDate
        },

        moneyColumn(
          'totalAmount'
        ),

        statusColumn(
          'bookingStatus'
        )
      ]}

      customActions={
        row => (

          <PaymentDetailsAction
            booking={row}
          />
        )
      }

      createLabel="Create booking"
    />
  )
}


// ============================================================
// RATINGS
// ============================================================

export function RatingsPage() {

  const {
    user
  } = useAuth()


  const isGuest =
    user?.role === ROLES.GUEST


  const isManager =
    user?.role === ROLES.MANAGER


  const canCreate =
    hasPermission(
      user?.role,
      permissions.ratingCreate
    )


  const loadRows =
    filters => {


      if (isGuest) {

        if (!user?.userId) {

          return Promise.resolve([])
        }


        return ratingsApi.byUser(
          user.userId
        )
      }


      if (isManager) {

        if (!user?.hotelId) {

          return Promise.resolve([])
        }


        return ratingsApi.byHotel(
          user.hotelId
        )
      }


      if (filters.userId) {

        return ratingsApi.byUser(
          filters.userId
        )
      }


      if (filters.hotelId) {

        return ratingsApi.byHotel(
          filters.hotelId
        )
      }


      return ratingsApi.list()
    }


  return (

    <CrudPage
      title={
        isGuest
          ? 'My Ratings'
          : isManager
          ? 'Hotel Ratings'
          : 'Ratings'
      }

      idKey="ratingId"

      loadRows={
        loadRows
      }

      createRow={
        canCreate
          ? ratingsApi.create
          : undefined
      }

      allowEdit={false}
      allowDelete={false}

      toPayload={
        form => ({

          ...(
            form.ratingId

              ? {
                  ratingId:
                    form.ratingId
                }

              : {}
          ),

          userId:
            isGuest
              ? user.userId
              : form.userId,

          hotelId:
            isManager
              ? user.hotelId
              : form.hotelId,

          rating:
            Number(
              form.rating
            ),

          feedback:
            form.feedback
        })
      }

      filters={
        isGuest || isManager

          ? []

          : [
              {
                name: 'userId',
                label: 'User ID',
                type: 'text'
              },

              {
                name: 'hotelId',
                label: 'Hotel ID',
                type: 'text'
              }
            ]
      }

      searchKeys={[
        'ratingId',
        'userId',
        'hotelId',
        'feedback'
      ]}

      fields={[
        {
          name: 'ratingId',
          label: 'Rating ID',
          placeholder:
            'Optional MongoDB ID'
        },

        ...(
          isGuest

            ? []

            : [
                {
                  name: 'userId',
                  label: 'User ID',
                  required: true
                }
              ]
        ),

        ...(
          isManager

            ? []

            : [
                {
                  name: 'hotelId',
                  label: 'Hotel ID',
                  required: true
                }
              ]
        ),

        {
          name: 'rating',
          label: 'Rating (1-5)',
          type: 'number',
          min: '1',
          max: '5',
          required: true
        },

        {
          name: 'feedback',
          label: 'Feedback',
          type: 'textarea',
          full: true
        }
      ]}

      columns={[
        {
          key: 'ratingId',
          label: 'Rating ID'
        },

        ...(
          isGuest

            ? []

            : [
                {
                  key: 'userId',
                  label: 'Guest'
                }
              ]
        ),

        {
          key: 'hotelId',
          label: 'Hotel'
        },

        {
          key: 'rating',
          label: 'Score',

          render:
            value => (

              <span className="font-bold text-amber-500">

                ★ {value}/5

              </span>
            )
        },

        {
          key: 'feedback',
          label: 'Feedback'
        }
      ]}

      createLabel="Add rating"
    />
  )
}


// ============================================================
// DEPARTMENTS
// ============================================================

export function DepartmentsPage() {

  const {
    user
  } = useAuth()


  const context =
    useReferenceData(false)


  const isManager =
    user?.role === ROLES.MANAGER


  const canCreate =
    hasPermission(
      user?.role,
      permissions.departmentCreate
    )


  const canEdit =
    hasPermission(
      user?.role,
      permissions.departmentEdit
    )


  const canDelete =
    hasPermission(
      user?.role,
      permissions.departmentDelete
    )


  const loadRows =
    filters => {


      if (isManager) {

        if (!user?.hotelId) {

          return Promise.resolve([])
        }


        return departmentsApi.byHotel(
          user.hotelId
        )
      }


      if (filters.hotelId) {

        return departmentsApi.byHotel(
          filters.hotelId
        )
      }


      return departmentsApi.list()
    }


  return (

    <CrudPage
      title={
        isManager
          ? 'Hotel Departments'
          : 'Departments'
      }

      idKey="departmentId"

      context={
        context
      }

      getRow={
        departmentsApi.get
      }

      loadRows={
        loadRows
      }

      createRow={
        canCreate
          ? departmentsApi.create
          : undefined
      }

      updateRow={
        canEdit
          ? departmentsApi.update
          : undefined
      }

      deleteRow={
        canDelete
          ? departmentsApi.remove
          : undefined
      }

      allowEdit={
        canEdit
      }

      allowDelete={
        canDelete
      }

      filters={
        isManager

          ? []

          : [
              {
                name: 'hotelId',
                label: 'Hotel',

                options: () =>
                  options(
                    context.hotels,
                    'id',
                    'name'
                  )
              }
            ]
      }

      searchKeys={[
        'departmentId',
        'departmentName',
        'hotelId',
        'description'
      ]}

      fields={[
        {
          name: 'departmentId',
          label: 'Department ID',
          hint:
            'Optional; backend generates DEP-UUID when blank'
        },

        {
          name: 'departmentName',
          label: 'Department name',
          required: true
        },

        ...(
          isManager

            ? []

            : [
                {
                  name: 'hotelId',
                  label: 'Hotel',
                  type: 'select',
                  required: true,

                  options: () =>
                    options(
                      context.hotels,
                      'id',
                      'name'
                    )
                }
              ]
        ),

        {
          name: 'description',
          label: 'Description',
          type: 'textarea',
          full: true
        }
      ]}

      toPayload={
        form => ({

          ...form,

          hotelId:
            isManager
              ? user.hotelId
              : form.hotelId
        })
      }

      columns={[
        {
          key: 'departmentId',
          label: 'Department ID'
        },

        {
          key: 'departmentName',
          label: 'Name'
        },

        {
          key: 'hotelId',
          label: 'Hotel ID'
        },

        {
          key: 'description',
          label: 'Description'
        }
      ]}

      createLabel="Add department"
    />
  )
}


// ============================================================
// EMPLOYEES
// ============================================================

export function EmployeesPage() {

  const {
    user
  } = useAuth()


  const context =
    useReferenceData(true)


  const isManager =
    user?.role === ROLES.MANAGER


  const canCreate =
    hasPermission(
      user?.role,
      permissions.employeeCreate
    )


  const canEdit =
    hasPermission(
      user?.role,
      permissions.employeeEdit
    )


  const canDelete =
    hasPermission(
      user?.role,
      permissions.employeeDelete
    )


  const canUpdateStatus =
    hasPermission(
      user?.role,
      permissions.employeeStatusUpdate
    )


  const loadRows =
    filters => {


      if (isManager) {

        if (!user?.hotelId) {

          return Promise.resolve([])
        }


        return employeesApi.byHotel(
          user.hotelId
        )
      }


      if (filters.departmentId) {

        return employeesApi.byDepartment(
          filters.departmentId
        )
      }


      if (filters.hotelId) {

        return employeesApi.byHotel(
          filters.hotelId
        )
      }


      if (filters.status) {

        return employeesApi.byStatus(
          filters.status
        )
      }


      return employeesApi.list()
    }


  return (

    <CrudPage
      title={
        isManager
          ? 'Hotel Employees'
          : 'Employees'
      }

      idKey="employeeId"

      context={
        context
      }

      getRow={
        employeesApi.get
      }

      loadRows={
        loadRows
      }

      createRow={
        canCreate
          ? employeesApi.create
          : undefined
      }

      updateRow={
        canEdit
          ? employeesApi.update
          : undefined
      }

      deleteRow={
        canDelete
          ? employeesApi.remove
          : undefined
      }

      allowEdit={
        canEdit
      }

      allowDelete={
        canDelete
      }

      prepareForm={
        row => ({

          ...row,

          departmentId:
            row.department?.departmentId
            ??
            ''
        })
      }

      toPayload={
        form => ({

          employeeId:
            form.employeeId,

          employeeName:
            form.employeeName,

          email:
            form.email,

          phone:
            form.phone,

          designation:
            form.designation,

          salary:
            form.salary === ''
              ? null
              : Number(
                  form.salary
                ),

          employeeStatus:
            form.employeeStatus
            ||
            'ACTIVE',

          department: {

            departmentId:
              form.departmentId
          }
        })
      }

      filters={[
        {
          name: 'departmentId',
          label: 'Department',

          options: () =>
            options(
              context.departments,
              'departmentId',
              'departmentName'
            )
        },

        ...(
          isManager

            ? []

            : [
                {
                  name: 'hotelId',
                  label: 'Hotel',

                  options: () =>
                    options(
                      context.hotels,
                      'id',
                      'name'
                    )
                }
              ]
        ),

        {
          name: 'status',
          label: 'Status',
          options: employeeStatuses
        }
      ]}

      searchKeys={[
        'employeeId',
        'employeeName',
        'email',
        'designation',
        'employeeStatus',
        'department.departmentName'
      ]}

      fields={[
        {
          name: 'employeeId',
          label: 'Employee ID',
          hint:
            'Optional; backend generates EMP-UUID when blank'
        },

        {
          name: 'employeeName',
          label: 'Employee name',
          required: true
        },

        {
          name: 'email',
          label: 'Email',
          type: 'email',
          required: true
        },

        {
          name: 'phone',
          label: 'Phone'
        },

        {
          name: 'designation',
          label: 'Designation',
          required: true
        },

        {
          name: 'salary',
          label: 'Salary',
          type: 'number',
          min: '0',
          step: '0.01'
        },

        {
          name: 'employeeStatus',
          label: 'Status',
          type: 'select',
          options: employeeStatuses,
          defaultValue: 'ACTIVE'
        },

        {
          name: 'departmentId',
          label: 'Department',
          type: 'select',
          required: true,

          options: () =>
            options(
              context.departments,
              'departmentId',
              'departmentName'
            )
        }
      ]}

      columns={[
        {
          key: 'employeeId',
          label: 'Employee ID'
        },

        {
          key: 'employeeName',
          label: 'Name'
        },

        {
          key: 'email',
          label: 'Email'
        },

        {
          key: 'designation',
          label: 'Designation'
        },

        moneyColumn(
          'salary',
          'Salary'
        ),

        {
          key: 'department.departmentName',
          label: 'Department'
        },

        statusColumn(
          'employeeStatus'
        )
      ]}

      customActions={
        canUpdateStatus

          ? (
              row,
              refresh
            ) => (

              <QuickStatus
                value={
                  row.employeeStatus
                }

                values={
                  employeeStatuses
                }

                refresh={
                  refresh
                }

                onChange={
                  status =>

                    employeesApi.updateStatus(
                      row.employeeId,
                      status
                    )
                }
              />
            )

          : undefined
      }

      createLabel="Add employee"
    />
  )
}


// ============================================================
// HOTEL SERVICES
// ============================================================

export function HotelServicesPage() {

  const {
    user
  } = useAuth()


  const context =
    useReferenceData(false)


  const isManager =
    user?.role === ROLES.MANAGER


  const canCreate =
    hasPermission(
      user?.role,
      permissions.serviceCreate
    )


  const canEdit =
    hasPermission(
      user?.role,
      permissions.serviceEdit
    )


  const canDelete =
    hasPermission(
      user?.role,
      permissions.serviceDelete
    )


  const canUpdateStatus =
    hasPermission(
      user?.role,
      permissions.serviceStatusUpdate
    )


  const loadRows =
    filters => {


      if (isManager) {

        if (!user?.hotelId) {

          return Promise.resolve([])
        }


        if (
          filters.available ===
          'true'
        ) {

          return offeringsApi.availableByHotel(
            user.hotelId
          )
        }


        return offeringsApi.byHotel(
          user.hotelId
        )
      }


      if (filters.hotelId) {

        if (
          filters.available ===
          'true'
        ) {

          return offeringsApi.availableByHotel(
            filters.hotelId
          )
        }


        return offeringsApi.byHotel(
          filters.hotelId
        )
      }


      return offeringsApi.list()
    }


  return (

    <CrudPage
      title="Hotel Services"

      idKey="serviceId"

      context={
        context
      }

      getRow={
        offeringsApi.get
      }

      loadRows={
        loadRows
      }

      createRow={
        canCreate
          ? offeringsApi.create
          : undefined
      }

      updateRow={
        canEdit
          ? offeringsApi.update
          : undefined
      }

      deleteRow={
        canDelete
          ? offeringsApi.remove
          : undefined
      }

      allowEdit={
        canEdit
      }

      allowDelete={
        canDelete
      }

      toPayload={
        form => ({

          ...form,

          hotelId:
            isManager
              ? user.hotelId
              : form.hotelId,

          price:
            Number(
              form.price
            )
        })
      }

      filters={[
        ...(
          isManager

            ? []

            : [
                {
                  name: 'hotelId',
                  label: 'Hotel',

                  options: () =>
                    options(
                      context.hotels,
                      'id',
                      'name'
                    )
                }
              ]
        ),

        {
          name: 'available',
          label: 'Availability filter',

          options: [
            {
              value: 'true',
              label: 'Available only'
            }
          ]
        }
      ]}

      searchKeys={[
        'serviceId',
        'hotelId',
        'serviceName',
        'description',
        'serviceStatus'
      ]}

      fields={[
        {
          name: 'serviceId',
          label: 'Service ID',
          hint:
            'Optional; backend generates SVC-UUID when blank'
        },

        ...(
          isManager

            ? []

            : [
                {
                  name: 'hotelId',
                  label: 'Hotel',
                  type: 'select',
                  required: true,

                  options: () =>
                    options(
                      context.hotels,
                      'id',
                      'name'
                    )
                }
              ]
        ),

        {
          name: 'serviceName',
          label: 'Service name',
          required: true
        },

        {
          name: 'price',
          label: 'Price',
          type: 'number',
          min: '0',
          step: '0.01',
          required: true
        },

        {
          name: 'serviceStatus',
          label: 'Status',
          type: 'select',
          options: serviceStatuses,
          defaultValue: 'AVAILABLE'
        },

        {
          name: 'description',
          label: 'Description',
          type: 'textarea',
          full: true
        }
      ]}

      columns={[
        {
          key: 'serviceId',
          label: 'Service ID'
        },

        {
          key: 'serviceName',
          label: 'Service'
        },

        {
          key: 'hotelId',
          label: 'Hotel ID'
        },

        moneyColumn(
          'price',
          'Price'
        ),

        statusColumn(
          'serviceStatus'
        ),

        {
          key: 'createdAt',
          label: 'Created',
          render: formatDateTime
        }
      ]}

      customActions={
        canUpdateStatus

          ? (
              row,
              refresh
            ) => (

              <QuickStatus
                value={
                  row.serviceStatus
                }

                values={
                  serviceStatuses
                }

                refresh={
                  refresh
                }

                onChange={
                  status =>

                    offeringsApi.updateStatus(
                      row.serviceId,
                      status
                    )
                }
              />
            )

          : undefined
      }

      createLabel="Add service"
    />
  )
}


// ============================================================
// INVOICES
// ============================================================

export function InvoicesPage() {

  const {
    user
  } = useAuth()


  const isGuest =
    user?.role === ROLES.GUEST


  const isManager =
    user?.role === ROLES.MANAGER


  const canCreate =
    hasPermission(
      user?.role,
      permissions.invoiceCreate
    )


  const loadRows =
    async filters => {


      // ======================================================
      // GUEST
      // ======================================================

      if (isGuest) {

        if (!user?.userId) {

          return []
        }


        return normalizeArray(

          await invoicesApi.list({

            userId:
              user.userId
          })
        )
      }


      // ======================================================
      // MANAGER
      // ======================================================

      if (isManager) {

        if (!user?.hotelId) {

          return []
        }


        const bookings =
          normalizeArray(

            await bookingsApi.byHotel(
              user.hotelId
            )
          )


        const responses =
          await Promise.allSettled(

            bookings.map(
              booking =>

                invoicesApi.list({

                  bookingId:
                    booking.bookingId
                })
            )
          )


        const allInvoices =
          responses.flatMap(
            response => {


              if (
                response.status
                !== 'fulfilled'
              ) {

                return []
              }


              return normalizeArray(
                response.value
              )
            }
          )


        const uniqueInvoices =
          new Map()


        allInvoices.forEach(
          invoice => {


            const key =
              invoice.invoiceId
              ??
              invoice.invoiceNumber
              ??
              `${invoice.bookingId}-${invoice.userId}`


            uniqueInvoices.set(
              key,
              invoice
            )
          }
        )


        return [
          ...uniqueInvoices.values()
        ]
      }


      // ======================================================
      // ADMIN
      // ======================================================

      return normalizeArray(

        await invoicesApi.list({

          userId:
            filters.userId
            || undefined,

          bookingId:
            filters.userId
              ? undefined
              : (
                  filters.bookingId
                  || undefined
                )
        })
      )
    }


  return (

    <CrudPage
      title={
        isGuest
          ? 'My Invoices'
          : isManager
          ? 'Hotel Invoices'
          : 'Invoices'
      }

      idKey="invoiceId"

      getRow={
        isGuest
          ? undefined
          : invoicesApi.get
      }

      loadRows={
        loadRows
      }

      createRow={
        canCreate
          ? invoicesApi.create
          : undefined
      }

      allowEdit={false}
      allowDelete={false}

      toPayload={
        form => ({

          bookingId:
            form.bookingId,

          userId:
            form.userId,

          amount:
            Number(
              form.amount
            ),

          ...(
            form.tax !== ''

              ? {
                  tax:
                    Number(
                      form.tax
                    )
                }

              : {}
          ),

          ...(
            form.dueDate

              ? {
                  dueDate:
                    form.dueDate
                }

              : {}
          )
        })
      }

      filters={
        isGuest || isManager

          ? []

          : [
              {
                name: 'userId',
                label: 'User ID',
                type: 'text'
              },

              {
                name: 'bookingId',
                label: 'Booking ID',
                type: 'text'
              }
            ]
      }

      searchKeys={[
        'invoiceId',
        'invoiceNumber',
        'bookingId',
        'userId',
        'status'
      ]}

      fields={
        isGuest

          ? []

          : [
              {
                name: 'bookingId',
                label: 'Booking ID',
                required: true
              },

              {
                name: 'userId',
                label: 'User ID',
                required: true
              },

              {
                name: 'amount',
                label: 'Base amount',
                type: 'number',
                min: '0',
                step: '0.01',
                required: true
              },

              {
                name: 'tax',
                label: 'Tax',
                type: 'number',
                min: '0',
                step: '0.01',
                hint:
                  'Leave blank for automatic 18% tax'
              },

              {
                name: 'dueDate',
                label: 'Due date',
                type: 'date',
                hint:
                  'Leave blank for 30 days from issue'
              }
            ]
      }

      columns={[
        {
          key: 'invoiceNumber',
          label: 'Invoice number'
        },

        {
          key: 'bookingId',
          label: 'Booking'
        },

        ...(
          isGuest

            ? []

            : [
                {
                  key: 'userId',
                  label: 'Guest'
                }
              ]
        ),

        moneyColumn(
          'amount',
          'Base'
        ),

        moneyColumn(
          'tax',
          'Tax'
        ),

        moneyColumn(
          'totalAmount',
          'Total'
        ),

        statusColumn(
          'status'
        ),

        {
          key: 'issuedAt',
          label: 'Issued',
          render: formatDateTime
        },

        {
          key: 'dueDate',
          label: 'Due',
          render: formatDate
        }
      ]}

      createLabel="Generate invoice"
    />
  )
}