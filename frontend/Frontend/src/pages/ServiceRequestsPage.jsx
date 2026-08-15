import {
  useEffect,
  useState
} from 'react'

import {
  Ban,
  UserRoundCog
} from 'lucide-react'

import toast from 'react-hot-toast'

import CrudPage, {
  moneyColumn,
  statusColumn
} from '../components/CrudPage'

import {
  Button,
  Modal,
  Select
} from '../components/ui'

import {
  employeesApi,
  offeringsApi,
  roomRequestsApi,
  roomsApi,
  usersApi
} from '../api/services'

import {
  errorMessage,
  formatDateTime
} from '../lib/utils'

import {
  permissions,
  hasPermission,
  ROLES
} from '../lib/permissions'

import {
  useAuth
} from '../context/AuthContext'


const statuses = [
  'PENDING',
  'ACCEPTED',
  'IN_PROGRESS',
  'COMPLETED',
  'CANCELLED'
]


const options = (
  rows,
  valueKey,
  labelKey
) =>
  rows.map(row => ({
    value: row[valueKey],
    label:
      `${row[labelKey]} (${row[valueKey]})`
  }))


// ============================================================
// ACTIONS
// ============================================================

function RequestActions({
  row,
  refresh,
  employees,
  role
}) {

  const [assignOpen, setAssignOpen] =
    useState(false)

  const [employeeId, setEmployeeId] =
    useState(
      row.assignedEmployeeId || ''
    )

  const [loading, setLoading] =
    useState(false)


  const canAssign =
    hasPermission(
      role,
      permissions.requestAssign
    )

  const canUpdateStatus =
    hasPermission(
      role,
      permissions.requestStatusUpdate
    )

  const canCancel =
    hasPermission(
      role,
      permissions.requestCancel
    )


  const run = async (
    task,
    message
  ) => {

    setLoading(true)

    try {

      await task()

      toast.success(message)

      setAssignOpen(false)

      await refresh()

    } catch (error) {

      toast.error(
        errorMessage(error)
      )

    } finally {

      setLoading(false)

    }
  }


  return (
    <>

      {/* STATUS */}

      {canUpdateStatus && (

        <Select
          value={
            row.requestStatus
          }
          disabled={loading}
          className="w-36 py-1.5 text-xs"

          onChange={event =>
            run(
              () =>
                roomRequestsApi
                  .updateStatus(
                    row.requestId,
                    event.target.value
                  ),

              'Request status updated'
            )
          }
        >

          {statuses.map(status => (

            <option
              key={status}
              value={status}
            >

              {status}

            </option>

          ))}

        </Select>

      )}



      {/* ASSIGN */}

      {canAssign && (

        <Button
          variant="ghost"
          size="sm"
          title="Assign employee"

          onClick={() =>
            setAssignOpen(true)
          }
        >

          <UserRoundCog className="h-4 w-4" />

        </Button>

      )}



      {/* CANCEL */}

      {canCancel &&
        ![
          'COMPLETED',
          'CANCELLED'
        ].includes(
          row.requestStatus
        ) && (

          <Button
            variant="ghost"
            size="sm"
            className="text-rose-600"
            title="Cancel request"

            onClick={() =>
              run(
                () =>
                  roomRequestsApi
                    .cancel(
                      row.requestId
                    ),

                'Request cancelled'
              )
            }
          >

            <Ban className="h-4 w-4" />

          </Button>

        )}



      {/* ASSIGN MODAL */}

      {canAssign && (

        <Modal
          open={assignOpen}

          onClose={() =>
            setAssignOpen(false)
          }

          title="Assign employee"

          subtitle={
            row.requestId
          }

          footer={
            <>

              <Button
                variant="secondary"

                onClick={() =>
                  setAssignOpen(false)
                }
              >

                Cancel

              </Button>


              <Button
                loading={loading}
                disabled={!employeeId}

                onClick={() =>
                  run(
                    () =>
                      roomRequestsApi
                        .assign(
                          row.requestId,
                          employeeId
                        ),

                    'Employee assigned'
                  )
                }
              >

                Assign

              </Button>

            </>
          }
        >

          <Select
            value={employeeId}

            onChange={event =>
              setEmployeeId(
                event.target.value
              )
            }
          >

            <option value="">
              Select employee
            </option>


            {options(
              employees,
              'employeeId',
              'employeeName'
            ).map(option => (

              <option
                key={option.value}
                value={option.value}
              >

                {option.label}

              </option>

            ))}

          </Select>

        </Modal>

      )}

    </>
  )
}


// ============================================================
// PAGE
// ============================================================

export default function ServiceRequestsPage() {

  const {
    user
  } = useAuth()


  const [context, setContext] =
    useState({
      offerings: [],
      rooms: [],
      employees: [],
      currentUserId: null,
      currentEmployeeId: null
    })


  useEffect(() => {

    const load = async () => {

      const [
        offeringsResult,
        roomsResult,
        employeesResult,
        usersResult
      ] = await Promise.allSettled([
        offeringsApi.list(),
        roomsApi.list(),
        employeesApi.list(),
        usersApi.list()
      ])


      const offerings =
        offeringsResult.status === 'fulfilled'
          ? offeringsResult.value || []
          : []


      const rooms =
        roomsResult.status === 'fulfilled'
          ? roomsResult.value || []
          : []


      const employees =
        employeesResult.status === 'fulfilled'
          ? employeesResult.value || []
          : []


      const users =
        usersResult.status === 'fulfilled'
          ? usersResult.value || []
          : []


      const currentGuest =
        users.find(
          item =>
            item.email
              ?.toLowerCase() ===
            user?.email
              ?.toLowerCase()
        )


      const currentEmployee =
        employees.find(
          item =>
            item.email
              ?.toLowerCase() ===
            user?.email
              ?.toLowerCase()
        )


      setContext({
        offerings,
        rooms,
        employees,

        currentUserId:
          currentGuest?.userId ||
          user?.userId ||
          null,

        currentEmployeeId:
          currentEmployee?.employeeId ||
          user?.employeeId ||
          null
      })
    }


    load()

  }, [user])


  const canCreate =
    hasPermission(
      user?.role,
      permissions.requestCreate
    )


  const canDelete =
    hasPermission(
      user?.role,
      permissions.requestDelete
    )


  // ==========================================================
  // LOAD ROWS ACCORDING TO ROLE
  // ==========================================================

  const loadRows = filters => {


    // GUEST
    // only own requests

    if (
      user?.role === ROLES.GUEST
    ) {

      if (
        !context.currentUserId
      ) {

        return Promise.resolve([])
      }


      return roomRequestsApi
        .byUser(
          context.currentUserId
        )
    }


    // EMPLOYEE
    // only assigned requests

    if (
      user?.role === ROLES.EMPLOYEE
    ) {

      if (
        !context.currentEmployeeId
      ) {

        return Promise.resolve([])
      }


      return roomRequestsApi
        .byEmployee(
          context.currentEmployeeId
        )
    }


    // ADMIN / MANAGER

    if (filters.userId) {

      return roomRequestsApi
        .byUser(
          filters.userId
        )
    }


    if (filters.bookingId) {

      return roomRequestsApi
        .byBooking(
          filters.bookingId
        )
    }


    if (filters.roomId) {

      return roomRequestsApi
        .byRoom(
          filters.roomId
        )
    }


    if (filters.employeeId) {

      return roomRequestsApi
        .byEmployee(
          filters.employeeId
        )
    }


    if (filters.status) {

      return roomRequestsApi
        .byStatus(
          filters.status
        )
    }


    return roomRequestsApi.list()
  }


  // ==========================================================
  // CREATE PAYLOAD
  // ==========================================================

  const toPayload = form => ({

    userId:
      user?.role === ROLES.GUEST
        ? context.currentUserId
        : form.userId,

    bookingId:
      form.bookingId,

    roomId:
      form.roomId,

    serviceId:
      form.serviceId,

    quantity:
      Number(form.quantity),

    specialInstructions:
      form.specialInstructions
  })


  return (

    <CrudPage
      title={
        user?.role === ROLES.GUEST
          ? 'My Service Requests'

          : user?.role === ROLES.EMPLOYEE
          ? 'Assigned Service Requests'

          : 'Service Requests'
      }

      idKey="requestId"

      context={context}

      getRow={
        roomRequestsApi.get
      }

      loadRows={loadRows}

      createRow={
        canCreate
          ? roomRequestsApi.create
          : undefined
      }

      deleteRow={
        canDelete
          ? roomRequestsApi.remove
          : undefined
      }

      allowEdit={false}

      allowDelete={
        canDelete
      }


      filters={
        [
          ROLES.ADMIN,
          ROLES.MANAGER
        ].includes(user?.role)

          ? [
              {
                name: 'userId',
                label: 'User ID',
                type: 'text'
              },
              {
                name: 'bookingId',
                label: 'Booking ID',
                type: 'text'
              },
              {
                name: 'roomId',
                label: 'Room',
                options: () =>
                  options(
                    context.rooms,
                    'roomId',
                    'roomNumber'
                  )
              },
              {
                name: 'employeeId',
                label: 'Employee',
                options: () =>
                  options(
                    context.employees,
                    'employeeId',
                    'employeeName'
                  )
              },
              {
                name: 'status',
                label: 'Status',
                options: statuses
              }
            ]

          : []
      }


      searchKeys={[
        'requestId',
        'bookingId',
        'roomId',
        'assignedEmployeeId',
        'requestStatus',
        'serviceOffering.serviceName'
      ]}


      toPayload={toPayload}


      fields={[
        ...(user?.role === ROLES.GUEST

          ? []

          : [
              {
                name: 'userId',
                label: 'User ID',
                required: true
              }
            ]
        ),

        {
          name: 'bookingId',
          label: 'Booking ID',
          required: true
        },

        {
          name: 'roomId',
          label: 'Room',
          type: 'select',
          required: true,

          options: () =>
            options(
              context.rooms,
              'roomId',
              'roomNumber'
            )
        },

        {
          name: 'serviceId',
          label: 'Service offering',
          type: 'select',
          required: true,

          options: () =>
            options(
              context.offerings.filter(
                offering =>
                  offering.serviceStatus ===
                  'AVAILABLE'
              ),
              'serviceId',
              'serviceName'
            )
        },

        {
          name: 'quantity',
          label: 'Quantity',
          type: 'number',
          min: '1',
          required: true,
          defaultValue: 1
        },

        {
          name: 'specialInstructions',
          label: 'Special instructions',
          type: 'textarea',
          full: true
        }
      ]}


      columns={[
        {
          key: 'requestId',
          label: 'Request ID'
        },
        {
          key: 'serviceOffering.serviceName',
          label: 'Service'
        },
        {
          key: 'roomId',
          label: 'Room'
        },

        ...(
          user?.role === ROLES.ADMIN ||
          user?.role === ROLES.MANAGER

            ? [
                {
                  key: 'userId',
                  label: 'Guest'
                },
                {
                  key: 'assignedEmployeeId',
                  label: 'Assigned Employee'
                }
              ]

            : user?.role === ROLES.EMPLOYEE

            ? [
                {
                  key: 'assignedEmployeeId',
                  label: 'Employee'
                }
              ]

            : []
        ),

        {
          key: 'quantity',
          label: 'Qty'
        },

        moneyColumn(
          'totalAmount',
          'Total'
        ),

        statusColumn(
          'requestStatus'
        ),

        {
          key: 'requestedAt',
          label: 'Requested',
          render: formatDateTime
        },

        {
          key: 'completedAt',
          label: 'Completed',
          render: formatDateTime
        }
      ]}


      customActions={(
        row,
        refresh
      ) => (

        <RequestActions
          row={row}
          refresh={refresh}
          employees={
            context.employees
          }
          role={
            user?.role
          }
        />

      )}


      createLabel="New service request"
    />

  )
}