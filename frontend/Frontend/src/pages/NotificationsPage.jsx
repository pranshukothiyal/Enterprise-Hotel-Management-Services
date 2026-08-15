import {
  useCallback,
  useEffect,
  useMemo,
  useState
} from 'react'

import {
  Bell,
  BellRing,
  CheckCheck,
  MailPlus,
  RefreshCw,
  Search
} from 'lucide-react'

import toast from 'react-hot-toast'

import {
  notificationsApi
} from '../api/services'

import {
  Badge,
  Button,
  Card,
  DataTable,
  EmptyState,
  Field,
  Input,
  Modal,
  PageHeader,
  Select,
  Spinner,
  Textarea
} from '../components/ui'

import {
  errorMessage,
  formatDateTime
} from '../lib/utils'

import {
  useAuth
} from '../context/AuthContext'

import {
  ROLES,
  permissions,
  hasPermission
} from '../lib/permissions'


export default function NotificationsPage() {

  const {
    user
  } = useAuth()


  // ==========================================================
  // STATE
  // ==========================================================

  const [
    rows,
    setRows
  ] = useState([])


  const [
    loading,
    setLoading
  ] = useState(true)


  const [
    saving,
    setSaving
  ] = useState(false)


  const [
    userId,
    setUserId
  ] = useState('')


  const [
    query,
    setQuery
  ] = useState('')


  const [
    open,
    setOpen
  ] = useState(false)


  const [
    form,
    setForm
  ] = useState({
    userId: '',
    message: '',
    type: 'SYSTEM'
  })


  // ==========================================================
  // ROLE
  // ==========================================================

  const isManagement =
    [
      ROLES.ADMIN,
      ROLES.MANAGER
    ].includes(
      user?.role
    )


  const isGuest =
    user?.role === ROLES.GUEST


  const isEmployee =
    user?.role === ROLES.EMPLOYEE


  const canCreate =
    hasPermission(
      user?.role,
      permissions.notificationCreate
    )


  // ==========================================================
  // CURRENT USER ID
  // ==========================================================
  //
  // IMPORTANT:
  //
  // We DO NOT call:
  //
  // usersApi.list()
  //
  // anymore.
  //
  // AuthService already returns:
  //
  // user.userId
  //
  // ==========================================================

  const currentUserId =
    user?.userId ?? null


  // ==========================================================
  // LOAD NOTIFICATIONS
  // ==========================================================

  const refresh =
    useCallback(
      async () => {

        setLoading(true)


        try {

          // ==================================================
          // ADMIN / MANAGER
          // ==================================================
          //
          // Management users may optionally enter a userId
          // to filter notifications.
          //
          // If the input is empty, whatever behavior your
          // current NotificationService provides for
          // notificationsApi.list('') will be used.
          //
          // ==================================================

          if (isManagement) {

            const response =
              await notificationsApi.list(
                userId || undefined
              )


            setRows(
              Array.isArray(response)
                ? response
                : []
            )


            return
          }


          // ==================================================
          // GUEST / EMPLOYEE
          // ==================================================
          //
          // They only load notifications linked to their
          // authenticated userId.
          //
          // ==================================================

          if (
            isGuest
            ||
            isEmployee
          ) {

            if (!currentUserId) {

              setRows([])

              return
            }


            const response =
              await notificationsApi.list(
                currentUserId
              )


            setRows(
              Array.isArray(response)
                ? response
                : []
            )


            return
          }


          // Unknown role
          setRows([])


        } catch (error) {

          console.error(
            'Failed to load notifications:',
            error
          )


          toast.error(
            errorMessage(error)
          )


          setRows([])


        } finally {

          setLoading(false)
        }

      },
      [
        currentUserId,
        isEmployee,
        isGuest,
        isManagement,
        userId
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

  const visible =
    useMemo(
      () =>

        rows.filter(
          notification => {

            if (!query) {

              return true
            }


            const text =
              `
                ${notification.message || ''}
                ${notification.type || ''}
              `
                .toLowerCase()


            return text.includes(
              query.toLowerCase()
            )
          }
        ),

      [
        rows,
        query
      ]
    )


  // ==========================================================
  // UNREAD COUNT
  // ==========================================================

  const unread =
    rows.filter(
      notification =>
        !notification.isRead
    ).length


  // ==========================================================
  // CREATE NOTIFICATION
  // ==========================================================

  const create =
    async event => {

      event.preventDefault()


      if (!canCreate) {

        return
      }


      if (!form.userId?.trim()) {

        toast.error(
          'User ID is required'
        )

        return
      }


      if (!form.message?.trim()) {

        toast.error(
          'Message is required'
        )

        return
      }


      setSaving(true)


      try {

        await notificationsApi.create({

          userId:
            form.userId.trim(),

          message:
            form.message.trim(),

          type:
            form.type
        })


        toast.success(
          'Notification created'
        )


        setOpen(false)


        setForm({
          userId: '',
          message: '',
          type: 'SYSTEM'
        })


        await refresh()


      } catch (error) {

        toast.error(
          errorMessage(error)
        )


      } finally {

        setSaving(false)
      }
    }


  // ==========================================================
  // MARK SINGLE NOTIFICATION AS READ
  // ==========================================================

  const read =
    async id => {

      if (!id) {

        return
      }


      try {

        await notificationsApi.markRead(
          id
        )


        toast.success(
          'Marked as read'
        )


        /*
         * Update frontend immediately instead of waiting
         * for another request.
         */
        setRows(
          currentRows =>
            currentRows.map(
              notification => {

                if (
                  notification.notificationId
                  !== id
                ) {

                  return notification
                }


                return {
                  ...notification,
                  isRead: true
                }
              }
            )
        )


      } catch (error) {

        toast.error(
          errorMessage(error)
        )
      }
    }


  // ==========================================================
  // MARK ALL READ
  // ==========================================================

  const readAll =
    async () => {

      if (
        rows.length === 0
      ) {

        return
      }


      setSaving(true)


      try {

        await notificationsApi
          .markAllRead()


        toast.success(
          'All notifications marked as read'
        )


        setRows(
          currentRows =>
            currentRows.map(
              notification => ({
                ...notification,
                isRead: true
              })
            )
        )


      } catch (error) {

        toast.error(
          errorMessage(error)
        )


      } finally {

        setSaving(false)
      }
    }


  // ==========================================================
  // COLUMNS
  // ==========================================================

  const columns = [

    {
      key: 'notificationId',
      label: 'Notification ID'
    },


    ...(
      isManagement

        ? [
            {
              key: 'userId',
              label: 'User'
            }
          ]

        : []
    ),


    {
      key: 'message',
      label: 'Message'
    },


    {
      key: 'type',
      label: 'Type',

      render:
        value => (

          <Badge
            tone={
              value === 'PAYMENT'
                ? 'green'

                : value === 'BOOKING'
                ? 'blue'

                : 'slate'
            }
          >

            {value || 'SYSTEM'}

          </Badge>
        )
    },


    {
      key: 'isRead',
      label: 'State',

      render:
        value => (

          <Badge
            tone={
              value
                ? 'green'
                : 'amber'
            }
          >

            {
              value
                ? 'READ'
                : 'UNREAD'
            }

          </Badge>
        )
    },


    {
      key: 'createdAt',
      label: 'Created',
      render: formatDateTime
    }
  ]


  // ==========================================================
  // UI
  // ==========================================================

  return (
    <>

      <PageHeader
        eyebrow="Communication"

        title={
          isManagement
            ? 'Notifications'
            : 'My Notifications'
        }

        action={

          <div className="flex gap-2">


            <Button
              variant="secondary"

              onClick={
                readAll
              }

              loading={
                saving
              }

              disabled={
                rows.length === 0
              }
            >

              <CheckCheck className="h-4 w-4" />

              Mark all read

            </Button>


            {
              canCreate && (

                <Button
                  onClick={() =>
                    setOpen(true)
                  }
                >

                  <MailPlus className="h-4 w-4" />

                  New notification

                </Button>
              )
            }

          </div>
        }
      />


      {/* ==================================================== */}
      {/* USER ID WARNING */}
      {/* ==================================================== */}

      {
        !isManagement
        &&
        !currentUserId
        && (

          <Card className="mb-6 border-amber-200 bg-amber-50 p-5 dark:border-amber-900 dark:bg-amber-950/30">

            <p className="font-semibold text-amber-800 dark:text-amber-300">

              Your account does not currently have a linked userId.

            </p>


            <p className="mt-1 text-sm text-amber-700 dark:text-amber-400">

              Account-specific notifications cannot be loaded until the account is linked.

            </p>

          </Card>
        )
      }


      {/* ==================================================== */}
      {/* STATS */}
      {/* ==================================================== */}

      <div className="mb-6 grid gap-4 sm:grid-cols-2">

        <Card className="p-5">

          <div className="flex items-center gap-3">

            <BellRing className="h-5 w-5 text-amber-500" />


            <div>

              <p className="text-xs text-slate-500">
                Unread notifications
              </p>


              <p className="text-2xl font-extrabold">
                {unread}
              </p>

            </div>

          </div>

        </Card>


        <Card className="p-5">

          <div className="flex items-center gap-3">

            <Bell className="h-5 w-5 text-cyan-600" />


            <div>

              <p className="text-xs text-slate-500">
                Total notifications
              </p>


              <p className="text-2xl font-extrabold">
                {rows.length}
              </p>

            </div>

          </div>

        </Card>

      </div>


      {/* ==================================================== */}
      {/* SEARCH / FILTER */}
      {/* ==================================================== */}

      <div className="mb-6 flex flex-col gap-3 sm:flex-row">

        <div className="relative flex-1">

          <Search
            className="
              absolute
              left-3
              top-1/2
              h-4
              w-4
              -translate-y-1/2
              text-slate-400
            "
          />


          <Input
            className="pl-9"

            value={
              query
            }

            onChange={
              event =>
                setQuery(
                  event.target.value
                )
            }

            placeholder="Search notifications"
          />

        </div>


        {
          isManagement && (

            <Input
              value={
                userId
              }

              onChange={
                event =>
                  setUserId(
                    event.target.value
                  )
              }

              placeholder="Filter by exact user ID"
            />
          )
        }


        <Button
          variant="secondary"

          onClick={
            refresh
          }
        >

          <RefreshCw className="h-4 w-4" />

          Refresh

        </Button>

      </div>


      {/* ==================================================== */}
      {/* DATA */}
      {/* ==================================================== */}

      {
        loading

          ? (

              <Spinner />

            )

          : visible.length

          ? (

              <DataTable
                rows={

                  visible.map(
                    row => ({

                      ...row,

                      id:
                        row.notificationId
                    })
                  )
                }

                columns={
                  columns
                }

                actions={
                  row =>

                    !row.isRead

                      ? (

                          <Button
                            variant="secondary"
                            size="sm"

                            onClick={() =>
                              read(
                                row.notificationId
                              )
                            }
                          >

                            Mark read

                          </Button>

                        )

                      : null
                }
              />

            )

          : (

              <EmptyState
                icon={
                  Bell
                }

                title="No notifications"

                description={
                  currentUserId || isManagement

                    ? 'There are no notifications to display.'

                    : 'Your account is not linked to a notification user ID.'
                }
              />

            )
      }


      {/* ==================================================== */}
      {/* CREATE MODAL */}
      {/* ==================================================== */}

      {
        canCreate && (

          <Modal
            open={
              open
            }

            onClose={() =>
              setOpen(false)
            }

            title="Create notification"

            footer={
              <>

                <Button
                  variant="secondary"

                  onClick={() =>
                    setOpen(false)
                  }
                >

                  Cancel

                </Button>


                <Button
                  loading={
                    saving
                  }

                  type="submit"

                  form="notification-form"
                >

                  Send notification

                </Button>

              </>
            }
          >

            <form
              id="notification-form"

              onSubmit={
                create
              }

              className="space-y-4"
            >


              <Field label="User ID">

                <Input
                  required

                  value={
                    form.userId
                  }

                  onChange={
                    event =>
                      setForm(
                        previous => ({

                          ...previous,

                          userId:
                            event.target.value
                        })
                      )
                  }
                />

              </Field>


              <Field label="Type">

                <Select
                  value={
                    form.type
                  }

                  onChange={
                    event =>
                      setForm(
                        previous => ({

                          ...previous,

                          type:
                            event.target.value
                        })
                      )
                  }
                >

                  {
                    [
                      'BOOKING',
                      'PAYMENT',
                      'SYSTEM'
                    ].map(
                      value => (

                        <option
                          key={
                            value
                          }

                          value={
                            value
                          }
                        >

                          {value}

                        </option>
                      )
                    )
                  }

                </Select>

              </Field>


              <Field label="Message">

                <Textarea
                  required

                  value={
                    form.message
                  }

                  onChange={
                    event =>
                      setForm(
                        previous => ({

                          ...previous,

                          message:
                            event.target.value
                        })
                      )
                  }
                />

              </Field>

            </form>

          </Modal>
        )
      }

    </>
  )
}