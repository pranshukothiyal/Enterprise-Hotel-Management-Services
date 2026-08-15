import {
  useCallback,
  useEffect,
  useMemo,
  useState
} from 'react'

import {
  CreditCard,
  RefreshCw,
  Search
} from 'lucide-react'

import toast from 'react-hot-toast'

import {
  bookingsApi,
  paymentsApi
} from '../api/services'

import {
  Badge,
  Button,
  Card,
  DataTable,
  EmptyState,
  Input,
  PageHeader,
  Spinner,
  statusTone
} from '../components/ui'

import {
  useAuth
} from '../context/AuthContext'

import {
  ROLES
} from '../lib/permissions'

import {
  errorMessage,
  formatCurrency,
  formatDateTime
} from '../lib/utils'


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


// ============================================================
// RAZORPAY SCRIPT
// ============================================================

function loadRazorpayScript() {

  return new Promise(resolve => {

    if (window.Razorpay) {

      resolve(true)

      return
    }


    const existing =
      document.querySelector(
        'script[src="https://checkout.razorpay.com/v1/checkout.js"]'
      )


    if (existing) {

      const handleLoad =
        () => resolve(true)

      const handleError =
        () => resolve(false)


      existing.addEventListener(
        'load',
        handleLoad,
        {
          once: true
        }
      )

      existing.addEventListener(
        'error',
        handleError,
        {
          once: true
        }
      )

      return
    }


    const script =
      document.createElement(
        'script'
      )


    script.src =
      'https://checkout.razorpay.com/v1/checkout.js'

    script.async = true


    script.onload =
      () => resolve(true)

    script.onerror =
      () => resolve(false)


    document.body.appendChild(
      script
    )
  })
}


// ============================================================
// MAIN PAGE
// ============================================================

export default function PaymentsPage() {

  const {
    user
  } = useAuth()


  const [
    payments,
    setPayments
  ] = useState([])


  const [
    bookings,
    setBookings
  ] = useState([])


  const [
    loading,
    setLoading
  ] = useState(true)


  const [
    payingBookingId,
    setPayingBookingId
  ] = useState(null)


  const [
    query,
    setQuery
  ] = useState('')


  // ==========================================================
  // ROLE
  // ==========================================================

  const isAdmin =
    user?.role === ROLES.ADMIN


  const isManager =
    user?.role === ROLES.MANAGER


  const isGuest =
    user?.role === ROLES.GUEST


  // ==========================================================
  // GET PAYMENT RECORDS FOR BOOKINGS
  // ==========================================================
  //
  // IMPORTANT:
  //
  // Previously every booking caused:
  //
  // GET /payments/booking/{bookingId}
  //
  // even when the booking was PENDING or CANCELLED.
  //
  // That produced a large number of unnecessary 400/404
  // responses when no payment existed.
  //
  // ==========================================================

  const loadPaymentsForBookings =
    async bookingRows => {

      const rows =
        normalizeArray(
          bookingRows
        )


      // ======================================================
      // ONLY CHECK RELEVANT BOOKINGS
      // ======================================================

      const candidates =
        rows
          .filter(
            booking =>
              Boolean(
                booking?.bookingId
              )
          )
          .filter(
            booking => {

              const bookingStatus =
                String(
                  booking?.bookingStatus
                  ?? ''
                )
                  .trim()
                  .toUpperCase()


              /*
               * No automatic payment lookup for these.
               *
               * CANCELLED:
               * Cannot be paid.
               *
               * PENDING:
               * Usually no payment record exists yet.
               */
              return ![
                'CANCELLED',
                'PENDING'
              ].includes(
                bookingStatus
              )
            }
          )


      if (
        candidates.length === 0
      ) {

        return []
      }


      // ======================================================
      // LOOK UP EACH EXISTING PAYMENT SAFELY
      // ======================================================

      const results =
        await Promise.allSettled(

          candidates.map(
            async booking => {

              try {

                const payment =
                  await paymentsApi.byBooking(
                    booking.bookingId
                  )


                return payment


              } catch (error) {

                /*
                 * A booking may legitimately have no payment.
                 *
                 * Do not show an error toast for each missing
                 * payment because that would flood the UI.
                 */

                return null
              }
            }
          )
        )


      // ======================================================
      // NORMALIZE RESPONSES
      // ======================================================

      return results

        .filter(
          result =>
            result.status ===
            'fulfilled'
        )

        .flatMap(
          result => {

            const value =
              result.value


            if (!value) {

              return []
            }


            if (
              Array.isArray(value)
            ) {

              return value
            }


            if (
              Array.isArray(
                value?.content
              )
            ) {

              return value.content
            }


            if (
              Array.isArray(
                value?.data
              )
            ) {

              return value.data
            }


            return [
              value
            ]
          }
        )

        .filter(Boolean)
    }


  // ==========================================================
  // REFRESH
  // ==========================================================

  const refresh =
    useCallback(
      async () => {

        setLoading(true)


        try {

          // ==================================================
          // ADMIN
          // ==================================================
          //
          // Admin can view the complete payment list.
          //
          // ==================================================

          if (isAdmin) {

            const response =
              await paymentsApi.list()


            setPayments(
              normalizeArray(
                response
              )
            )


            setBookings([])

            return
          }


          // ==================================================
          // HOTEL MANAGER
          // ==================================================
          //
          // Manager first gets ONLY bookings for assigned
          // hotel.
          //
          // Payment lookups are then limited to those bookings.
          //
          // ==================================================

          if (isManager) {

            if (!user?.hotelId) {

              setPayments([])

              setBookings([])

              return
            }


            const response =
              await bookingsApi.byHotel(
                user.hotelId
              )


            const hotelBookings =
              normalizeArray(
                response
              )


            setBookings(
              hotelBookings
            )


            const hotelPayments =
              await loadPaymentsForBookings(
                hotelBookings
              )


            setPayments(
              hotelPayments
            )


            return
          }


          // ==================================================
          // GUEST
          // ==================================================
          //
          // Guest sees only payments attached to their own
          // bookings.
          //
          // ==================================================

          if (isGuest) {

            if (!user?.userId) {

              setPayments([])

              setBookings([])

              return
            }


            const response =
              await bookingsApi.byUser(
                user.userId
              )


            const guestBookings =
              normalizeArray(
                response
              )


            setBookings(
              guestBookings
            )


            const guestPayments =
              await loadPaymentsForBookings(
                guestBookings
              )


            setPayments(
              guestPayments
            )


            return
          }


          // ==================================================
          // OTHER ROLE
          // ==================================================

          setPayments([])

          setBookings([])


        } catch (error) {

          console.error(
            'Unable to load payments:',
            error
          )


          toast.error(
            errorMessage(error)
          )


          setPayments([])

          setBookings([])


        } finally {

          setLoading(false)
        }

      },
      [
        isAdmin,
        isManager,
        isGuest,
        user?.hotelId,
        user?.userId
      ]
    )


  // ==========================================================
  // INITIAL LOAD
  // ==========================================================

  useEffect(() => {

    refresh()

  }, [
    refresh
  ])


  // ==========================================================
  // SEARCH
  // ==========================================================

  const visiblePayments =
    useMemo(
      () => {

        const search =
          query
            .trim()
            .toLowerCase()


        if (!search) {

          return payments
        }


        return payments.filter(
          payment =>

            `
              ${payment?.paymentId || ''}
              ${payment?.bookingId || ''}
              ${payment?.paymentStatus || ''}
              ${payment?.paymentMode || ''}
              ${payment?.transactionId || ''}
              ${payment?.currency || ''}
            `
              .toLowerCase()
              .includes(
                search
              )
        )

      },
      [
        payments,
        query
      ]
    )


  // ==========================================================
  // FIND PAYMENT FOR BOOKING
  // ==========================================================

  const paymentForBooking =
    bookingId =>

      payments.find(
        payment =>

          String(
            payment?.bookingId
            ?? ''
          )
          ===
          String(
            bookingId
            ?? ''
          )
      )


  // ==========================================================
  // PAY BOOKING
  // ==========================================================

  const payBooking =
    async booking => {

      if (!booking?.bookingId) {

        toast.error(
          'Booking ID is missing'
        )

        return
      }


      const bookingStatus =
        String(
          booking?.bookingStatus
          ?? ''
        )
          .trim()
          .toUpperCase()


      // ======================================================
      // CANCELLED BOOKING
      // ======================================================

      if (
        bookingStatus ===
        'CANCELLED'
      ) {

        toast.error(
          'Cancelled bookings cannot be paid'
        )

        return
      }


      const existingPayment =
        paymentForBooking(
          booking.bookingId
        )


      // ======================================================
      // ALREADY CAPTURED
      // ======================================================

      if (
        String(
          existingPayment?.paymentStatus
          ?? ''
        )
          .toUpperCase()
        === 'CAPTURED'
      ) {

        toast.success(
          'This booking is already paid'
        )

        return
      }


      setPayingBookingId(
        booking.bookingId
      )


      try {

        // ====================================================
        // LOAD RAZORPAY
        // ====================================================

        const loaded =
          await loadRazorpayScript()


        if (!loaded) {

          throw new Error(
            'Unable to load Razorpay checkout.'
          )
        }


        // ====================================================
        // CREATE ORDER
        // ====================================================

        const order =
          await paymentsApi.createOrder(
            booking.bookingId
          )


        if (!order) {

          throw new Error(
            'Payment service did not return an order.'
          )
        }


        if (!order?.keyId) {

          throw new Error(
            'Razorpay key ID is missing.'
          )
        }


        if (
          !order?.razorpayOrderId
        ) {

          throw new Error(
            'Razorpay order ID is missing.'
          )
        }


        // ====================================================
        // RAZORPAY OPTIONS
        // ====================================================

        const razorpayOptions = {

          key:
            order.keyId,


          amount:
            order.amount,


          currency:
            order.currency
            ||
            'INR',


          order_id:
            order.razorpayOrderId,


          name:
            'StayOps',


          description:
            `Booking ${booking.bookingId}`,


          // ==================================================
          // PAYMENT SUCCESS CALLBACK
          // ==================================================

          handler:
            async response => {

              try {

                await paymentsApi.verify({

                  razorpayOrderId:
                    response
                      .razorpay_order_id,

                  razorpayPaymentId:
                    response
                      .razorpay_payment_id,

                  razorpaySignature:
                    response
                      .razorpay_signature
                })


                toast.success(
                  'Payment completed successfully'
                )


                setPayingBookingId(
                  null
                )


                await refresh()


              } catch (error) {

                setPayingBookingId(
                  null
                )


                toast.error(
                  errorMessage(error)
                )
              }
            },


          // ==================================================
          // USER CLOSES PAYMENT WINDOW
          // ==================================================

          modal: {

            ondismiss: () => {

              setPayingBookingId(
                null
              )
            }
          }
        }


        const razorpay =
          new window.Razorpay(
            razorpayOptions
          )


        razorpay.open()


      } catch (error) {

        setPayingBookingId(
          null
        )


        toast.error(
          errorMessage(error)
        )
      }
    }


  // ==========================================================
  // ADMIN COLUMNS
  // ==========================================================

  const adminColumns = [

    {
      key: 'paymentId',
      label: 'Payment ID'
    },

    {
      key: 'bookingId',
      label: 'Booking'
    },

    {
      key: 'amount',
      label: 'Amount',

      render:
        value =>
          formatCurrency(
            value
          )
    },

    {
      key: 'currency',
      label: 'Currency'
    },

    {
      key: 'paymentMode',
      label: 'Mode'
    },

    {
      key: 'paymentStatus',
      label: 'Status',

      render:
        value => (

          <Badge
            tone={
              statusTone(
                value
              )
            }
          >

            {value || '—'}

          </Badge>
        )
    },

    {
      key: 'transactionId',
      label: 'Transaction'
    },

    {
      key: 'paymentDate',
      label: 'Payment Date',

      render:
        formatDateTime
    }
  ]


  // ==========================================================
  // MANAGER COLUMNS
  // ==========================================================

  const managerColumns = [

    {
      key: 'paymentId',
      label: 'Payment ID'
    },

    {
      key: 'bookingId',
      label: 'Booking'
    },

    {
      key: 'amount',
      label: 'Amount',

      render:
        value =>
          formatCurrency(
            value
          )
    },

    {
      key: 'currency',
      label: 'Currency'
    },

    {
      key: 'paymentMode',
      label: 'Mode'
    },

    {
      key: 'paymentStatus',
      label: 'Status',

      render:
        value => (

          <Badge
            tone={
              statusTone(
                value
              )
            }
          >

            {value || '—'}

          </Badge>
        )
    },

    {
      key: 'paymentDate',
      label: 'Payment Date',

      render:
        formatDateTime
    }
  ]


  // ==========================================================
  // GUEST COLUMNS
  // ==========================================================

  const guestColumns = [

    {
      key: 'paymentId',
      label: 'Payment ID'
    },

    {
      key: 'bookingId',
      label: 'Booking'
    },

    {
      key: 'amount',
      label: 'Amount',

      render:
        value =>
          formatCurrency(
            value
          )
    },

    {
      key: 'paymentMode',
      label: 'Mode'
    },

    {
      key: 'paymentStatus',
      label: 'Status',

      render:
        value => (

          <Badge
            tone={
              statusTone(
                value
              )
            }
          >

            {value || '—'}

          </Badge>
        )
    },

    {
      key: 'paymentDate',
      label: 'Paid On',

      render:
        formatDateTime
    }
  ]


  // ==========================================================
  // MANAGER WITHOUT HOTEL
  // ==========================================================

  if (
    isManager
    &&
    !user?.hotelId
  ) {

    return (
      <>

        <PageHeader
          eyebrow="Finance"

          title="Hotel Payments"

          description="Payments belonging to your assigned hotel."
        />


        <Card className="p-8">

          <EmptyState
            icon={CreditCard}

            title="No hotel assigned"

            description="Your Hotel Manager account must be assigned to a hotel before payment information can be displayed."
          />

        </Card>

      </>
    )
  }


  // ==========================================================
  // GUEST WITHOUT USER ID
  // ==========================================================

  if (
    isGuest
    &&
    !user?.userId
  ) {

    return (
      <>

        <PageHeader
          eyebrow="Payments"

          title="My Payments"

          description="Only payments belonging to your bookings are displayed."
        />


        <Card className="p-8">

          <EmptyState
            icon={CreditCard}

            title="Guest profile is not linked"

            description="Your authentication account does not contain a UserService user ID, so payment information cannot be loaded."
          />

        </Card>

      </>
    )
  }


  // ==========================================================
  // PAGE
  // ==========================================================

  return (
    <>

      <PageHeader
        eyebrow="Finance"

        title={
          isAdmin
            ? 'Payments'

            : isManager
            ? 'Hotel Payments'

            : 'My Payments'
        }

        description={
          isAdmin
            ? 'Payment transactions across the StayOps platform.'

            : isManager
            ? 'Only payments associated with bookings from your assigned hotel.'

            : 'View and pay for your hotel bookings.'
        }

        action={

          <Button
            variant="secondary"

            disabled={
              loading
            }

            onClick={
              refresh
            }
          >

            <RefreshCw
              className={
                loading
                  ? 'h-4 w-4 animate-spin'
                  : 'h-4 w-4'
              }
            />

            Refresh

          </Button>
        }
      />


      {/* ==================================================== */}
      {/* SEARCH */}
      {/* ==================================================== */}

      <div className="relative mb-6">

        <Search
          className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400"
        />


        <Input
          value={
            query
          }

          onChange={
            event =>
              setQuery(
                event.target.value
              )
          }

          className="pl-9"

          placeholder={
            isGuest
              ? 'Search my payments'
              : 'Search payments'
          }
        />

      </div>


      {/* ==================================================== */}
      {/* GUEST BOOKING CARDS */}
      {/* ==================================================== */}

      {
        isGuest && (

          <div className="mb-8">

            <h2 className="mb-4 text-lg font-bold">

              My bookings

            </h2>


            {
              bookings.length === 0

                ? (

                    <Card className="p-6">

                      <EmptyState
                        icon={CreditCard}

                        title="No bookings found"

                        description="You do not have any bookings available for payment."
                      />

                    </Card>
                  )

                : (

                    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">

                      {
                        bookings.map(
                          booking => {

                            const payment =
                              paymentForBooking(
                                booking.bookingId
                              )


                            const paymentStatus =
                              String(
                                payment?.paymentStatus
                                ?? ''
                              )
                                .trim()
                                .toUpperCase()


                            const bookingStatus =
                              String(
                                booking?.bookingStatus
                                ?? 'PENDING'
                              )
                                .trim()
                                .toUpperCase()


                            const captured =
                              paymentStatus ===
                              'CAPTURED'


                            const cancelled =
                              bookingStatus ===
                              'CANCELLED'


                            return (

                              <Card
                                key={
                                  booking.bookingId
                                }

                                className="p-5"
                              >

                                <p className="text-xs font-bold uppercase tracking-wide text-slate-400">

                                  Booking

                                </p>


                                <p className="mt-1 break-all font-bold">

                                  {
                                    booking.bookingId
                                  }

                                </p>


                                <div className="mt-3">

                                  <Badge
                                    tone={
                                      statusTone(
                                        bookingStatus
                                      )
                                    }
                                  >

                                    {bookingStatus}

                                  </Badge>

                                </div>


                                <p className="mt-4 text-sm text-slate-500">

                                  Amount

                                </p>


                                <p className="text-xl font-extrabold">

                                  {
                                    formatCurrency(
                                      booking.totalAmount
                                    )
                                  }

                                </p>


                                {
                                  payment && (

                                    <div className="mt-4">

                                      <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">

                                        Payment status

                                      </p>


                                      <div className="mt-1">

                                        <Badge
                                          tone={
                                            statusTone(
                                              paymentStatus
                                            )
                                          }
                                        >

                                          {
                                            paymentStatus
                                            ||
                                            'UNKNOWN'
                                          }

                                        </Badge>

                                      </div>

                                    </div>
                                  )
                                }


                                <div className="mt-5">

                                  {
                                    captured

                                      ? (

                                          <Badge tone="green">

                                            PAID

                                          </Badge>

                                        )

                                      : cancelled

                                      ? (

                                          <Badge tone="rose">

                                            CANCELLED

                                          </Badge>

                                        )

                                      : (

                                          <Button
                                            loading={
                                              payingBookingId
                                              ===
                                              booking.bookingId
                                            }

                                            disabled={
                                              Boolean(
                                                payingBookingId
                                                &&
                                                payingBookingId
                                                !==
                                                booking.bookingId
                                              )
                                            }

                                            onClick={
                                              () =>
                                                payBooking(
                                                  booking
                                                )
                                            }
                                          >

                                            <CreditCard className="h-4 w-4" />

                                            Pay Now

                                          </Button>
                                        )
                                  }

                                </div>

                              </Card>
                            )
                          }
                        )
                      }

                    </div>
                  )
            }

          </div>
        )
      }


      {/* ==================================================== */}
      {/* PAYMENT TABLE */}
      {/* ==================================================== */}

      <Card className="p-5">

        {
          loading

            ? (

                <div className="flex min-h-40 items-center justify-center">

                  <Spinner />

                </div>

              )

            : visiblePayments.length > 0

            ? (

                <DataTable
                  rows={
                    visiblePayments.map(
                      payment => ({

                        ...payment,

                        id:
                          payment.paymentId
                          ??
                          `${payment.bookingId}-${payment.razorpayOrderId ?? 'payment'}`
                      })
                    )
                  }

                  columns={
                    isAdmin
                      ? adminColumns

                      : isManager
                      ? managerColumns

                      : guestColumns
                  }
                />

              )

            : (

                <EmptyState
                  icon={CreditCard}

                  title={
                    isGuest
                      ? 'No payments yet'

                      : isManager
                      ? 'No hotel payments found'

                      : 'No payments found'
                  }

                  description={
                    isGuest
                      ? 'Payment transactions for your bookings will appear here.'

                      : isManager
                      ? 'No payment transactions are currently available for bookings from your assigned hotel.'

                      : 'There are no payment transactions to display.'
                  }
                />
              )
        }

      </Card>

    </>
  )
}