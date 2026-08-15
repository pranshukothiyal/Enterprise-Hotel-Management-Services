import React from 'react'

import {
  AlertTriangle,
  ChevronLeft,
  ChevronRight,
  Loader2,
  Plus,
  Search,
  X
} from 'lucide-react'

import {
  cn,
  getNested,
  humanize
} from '../lib/utils'


// ============================================================
// BUTTON
// ============================================================

export function Button({
  children,
  variant = 'primary',
  size = 'md',
  className,
  loading,
  ...props
}) {

  const variants = {

    primary:
      'bg-slate-950 text-white hover:bg-slate-800 dark:bg-cyan-400 dark:text-slate-950 dark:hover:bg-cyan-300',

    secondary:
      'border border-slate-300 bg-white text-slate-700 hover:bg-slate-50 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200 dark:hover:bg-slate-800',

    danger:
      'bg-rose-600 text-white hover:bg-rose-500',

    ghost:
      'text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-800'
  }


  const sizes = {

    sm:
      'px-3 py-2 text-xs',

    md:
      'px-4 py-2.5 text-sm',

    lg:
      'px-5 py-3 text-sm'
  }


  return (

    <button
      className={cn(
        'inline-flex items-center justify-center gap-2 rounded-xl font-semibold transition disabled:cursor-not-allowed disabled:opacity-50',
        variants[variant],
        sizes[size],
        className
      )}

      disabled={
        loading ||
        props.disabled
      }

      {...props}
    >

      {loading && (

        <Loader2 className="h-4 w-4 animate-spin" />

      )}

      {children}

    </button>
  )
}


// ============================================================
// CARD
// ============================================================

export function Card({
  children,
  className
}) {

  return (

    <div
      className={cn(
        'surface',
        className
      )}
    >

      {children}

    </div>
  )
}


// ============================================================
// BADGE
// ============================================================

export function Badge({
  children,
  tone = 'slate',
  className
}) {

  const tones = {

    slate:
      'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-200',

    green:
      'bg-emerald-100 text-emerald-700 dark:bg-emerald-950 dark:text-emerald-300',

    amber:
      'bg-amber-100 text-amber-700 dark:bg-amber-950 dark:text-amber-300',

    red:
      'bg-rose-100 text-rose-700 dark:bg-rose-950 dark:text-rose-300',

    blue:
      'bg-sky-100 text-sky-700 dark:bg-sky-950 dark:text-sky-300',

    purple:
      'bg-violet-100 text-violet-700 dark:bg-violet-950 dark:text-violet-300'
  }


  return (

    <span
      className={cn(
        'inline-flex rounded-full px-2.5 py-1 text-xs font-semibold',
        tones[tone] || tones.slate,
        className
      )}
    >

      {humanize(children)}

    </span>
  )
}


// ============================================================
// STATUS TONE
// ============================================================

export const statusTone = (
  value
) => {

  const status =
    String(
      value || ''
    )
      .toUpperCase()


  if (
    [
      'AVAILABLE',
      'ACTIVE',
      'CAPTURED',
      'PAID',
      'COMPLETED',
      'ACCEPTED'
    ].includes(status)
  ) {

    return 'green'
  }


  if (
    [
      'PENDING',
      'CREATED',
      'AUTHORIZED',
      'RESERVED',
      'IN_PROGRESS',
      'ON_LEAVE'
    ].includes(status)
  ) {

    return 'amber'
  }


  if (
    [
      'FAILED',
      'CANCELLED',
      'TERMINATED',
      'OVERDUE',
      'UNAVAILABLE'
    ].includes(status)
  ) {

    return 'red'
  }


  if (
    [
      'OCCUPIED'
    ].includes(status)
  ) {

    return 'blue'
  }


  return 'slate'
}


// ============================================================
// FIELD
// ============================================================

export function Field({
  label,
  error,
  hint,
  children,
  className
}) {

  return (

    <label
      className={cn(
        'block',
        className
      )}
    >

      <span className="label-base">

        {label}

      </span>


      {children}


      {hint && (

        <span className="mt-1 block text-xs text-slate-500">

          {hint}

        </span>

      )}


      {error && (

        <span className="mt-1 block text-xs font-medium text-rose-600">

          {error}

        </span>

      )}

    </label>
  )
}


// ============================================================
// INPUT
// ============================================================

export function Input({
  className,
  ...props
}) {

  return (

    <input
      className={cn(
        'input-base',
        className
      )}

      {...props}
    />
  )
}


// ============================================================
// SELECT
// ============================================================

export function Select({
  className,
  children,
  ...props
}) {

  return (

    <select
      className={cn(
        'input-base',
        className
      )}

      {...props}
    >

      {children}

    </select>
  )
}


// ============================================================
// TEXTAREA
// ============================================================

export function Textarea({
  className,
  ...props
}) {

  return (

    <textarea
      className={cn(
        'input-base min-h-24 resize-y',
        className
      )}

      {...props}
    />
  )
}


// ============================================================
// MODAL
// ============================================================

export function Modal({
  open,
  onClose,
  title,
  subtitle,
  children,
  footer,
  size = 'lg'
}) {

  if (!open) {
    return null
  }


  const widths = {

    md:
      'max-w-xl',

    lg:
      'max-w-2xl',

    xl:
      'max-w-4xl'
  }


  return (

    <div
      className="fixed inset-0 z-50 flex items-end justify-center bg-slate-950/60 p-0 backdrop-blur-sm sm:items-center sm:p-4"

      onMouseDown={event => {

        if (
          event.target ===
          event.currentTarget
        ) {

          onClose?.()

        }

      }}
    >

      <div
        className={cn(
          'max-h-[92vh] w-full overflow-hidden rounded-t-3xl bg-white shadow-2xl dark:bg-slate-900 sm:rounded-3xl',
          widths[size] ||
          widths.lg
        )}
      >


        {/* HEADER */}

        <div className="flex items-start justify-between border-b border-slate-200 px-5 py-4 dark:border-slate-800">


          <div>

            <h2 className="font-display text-xl font-bold">

              {title}

            </h2>


            {subtitle && (

              <p className="mt-1 text-sm text-slate-500">

                {subtitle}

              </p>

            )}

          </div>


          <Button
            variant="ghost"
            size="sm"
            onClick={onClose}
            aria-label="Close"
          >

            <X className="h-5 w-5" />

          </Button>

        </div>



        {/* CONTENT */}

        <div className="max-h-[calc(92vh-145px)] overflow-y-auto p-5">

          {children}

        </div>



        {/* FOOTER */}

        {footer && (

          <div className="flex flex-wrap justify-end gap-3 border-t border-slate-200 px-5 py-4 dark:border-slate-800">

            {footer}

          </div>

        )}

      </div>

    </div>
  )
}


// ============================================================
// CONFIRM DIALOG
// ============================================================

export function ConfirmDialog({
  open,
  title = 'Confirm action',
  message,
  onCancel,
  onConfirm,
  loading
}) {

  return (

    <Modal
      open={open}
      onClose={onCancel}
      title={title}
      size="md"

      footer={
        <>

          <Button
            variant="secondary"
            onClick={onCancel}
          >

            Cancel

          </Button>


          <Button
            variant="danger"
            loading={loading}
            onClick={onConfirm}
          >

            Confirm

          </Button>

        </>
      }
    >

      <div className="flex gap-4">


        <div className="rounded-2xl bg-rose-100 p-3 text-rose-600 dark:bg-rose-950">

          <AlertTriangle className="h-6 w-6" />

        </div>


        <p className="pt-1 text-sm leading-6 text-slate-600 dark:text-slate-300">

          {message}

        </p>

      </div>

    </Modal>
  )
}


// ============================================================
// PAGE HEADER
// ============================================================

export function PageHeader({
  eyebrow,
  title,
  description,
  action
}) {

  return (

    <div className="mb-6 flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">


      <div>

        {eyebrow && (

          <p className="mb-2 text-xs font-bold uppercase tracking-[.2em] text-cyan-600 dark:text-cyan-400">

            {eyebrow}

          </p>

        )}


        <h1 className="font-display text-3xl font-extrabold tracking-tight text-slate-950 dark:text-white">

          {title}

        </h1>


        {description && (

          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-500 dark:text-slate-400">

            {description}

          </p>

        )}

      </div>


      {action && (

        <div>

          {action}

        </div>

      )}

    </div>
  )
}


// ============================================================
// SEARCH BOX
// ============================================================

export function SearchBox({
  value,
  onChange,
  placeholder = 'Search…'
}) {

  return (

    <div className="relative">


      <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />


      <Input
        value={value}

        onChange={event =>
          onChange(
            event.target.value
          )
        }

        placeholder={placeholder}

        className="pl-9"
      />

    </div>
  )
}


// ============================================================
// EMPTY STATE
// ============================================================
// FIXED:
// "description" is now properly declared as a prop.
// ============================================================

export function EmptyState({
  icon: Icon = Search,
  title = 'No records found',
  description = '',
  action
}) {

  return (

    <div className="flex min-h-56 flex-col items-center justify-center rounded-2xl border border-dashed border-slate-300 p-8 text-center dark:border-slate-700">


      <div className="mb-4 rounded-2xl bg-slate-100 p-4 dark:bg-slate-800">

        {Icon && (

          <Icon className="h-6 w-6 text-slate-400" />

        )}

      </div>


      <h3 className="font-semibold text-slate-900 dark:text-white">

        {title}

      </h3>


      {description && (

        <p className="mt-1 max-w-sm text-sm leading-6 text-slate-500 dark:text-slate-400">

          {description}

        </p>

      )}


      {action && (

        <div className="mt-5">

          {action}

        </div>

      )}

    </div>
  )
}


// ============================================================
// SPINNER
// ============================================================

export function Spinner({
  label = 'Loading data…'
}) {

  return (

    <div className="flex min-h-56 items-center justify-center gap-3 text-sm text-slate-500">

      <Loader2 className="h-5 w-5 animate-spin text-cyan-500" />

      {label}

    </div>
  )
}


// ============================================================
// DATA TABLE
// ============================================================

export function DataTable({
  rows = [],
  columns = [],
  actions,
  pageSize = 10
}) {

  const [
    page,
    setPage
  ] = React.useState(1)


  React.useEffect(() => {

    setPage(1)

  }, [rows])


  const pages =
    Math.max(
      1,
      Math.ceil(
        rows.length /
        pageSize
      )
    )


  const visible =
    rows.slice(
      (page - 1) *
        pageSize,

      page *
        pageSize
    )


  return (

    <div className="overflow-hidden rounded-2xl border border-slate-200 dark:border-slate-800">


      {/* TABLE */}

      <div className="overflow-x-auto">

        <table className="min-w-full divide-y divide-slate-200 text-left text-sm dark:divide-slate-800">


          {/* HEADER */}

          <thead className="bg-slate-50 dark:bg-slate-950/50">

            <tr>

              {columns.map(column => (

                <th
                  key={column.key}

                  className="whitespace-nowrap px-4 py-3 text-xs font-bold uppercase tracking-wide text-slate-500"
                >

                  {column.label}

                </th>

              ))}


              {actions && (

                <th className="px-4 py-3 text-right text-xs font-bold uppercase tracking-wide text-slate-500">

                  Actions

                </th>

              )}

            </tr>

          </thead>



          {/* BODY */}

          <tbody className="divide-y divide-slate-100 bg-white dark:divide-slate-800 dark:bg-slate-900">

            {visible.map((
              row,
              index
            ) => (

              <tr
                key={
                  row.id ||
                  row._id ||
                  index
                }

                className="transition hover:bg-slate-50/80 dark:hover:bg-slate-800/40"
              >

                {columns.map(column => {

                  const value =
                    getNested(
                      row,
                      column.key
                    )


                  return (

                    <td
                      key={column.key}

                      className="max-w-xs px-4 py-3.5 align-top text-slate-700 dark:text-slate-200"
                    >

                      {column.render

                        ? column.render(
                            value,
                            row
                          )

                        : (
                            value ??
                            '—'
                          )
                      }

                    </td>
                  )

                })}


                {actions && (

                  <td className="whitespace-nowrap px-4 py-3 text-right">

                    {actions(row)}

                  </td>

                )}

              </tr>

            ))}

          </tbody>

        </table>

      </div>



      {/* PAGINATION */}

      {rows.length > pageSize && (

        <div className="flex items-center justify-between border-t border-slate-200 bg-slate-50 px-4 py-3 text-xs text-slate-500 dark:border-slate-800 dark:bg-slate-950/50">


          <span>

            Page {page} of {pages}
            {' · '}
            {rows.length} records

          </span>


          <div className="flex gap-2">


            <Button
              variant="secondary"
              size="sm"

              disabled={
                page === 1
              }

              onClick={() =>
                setPage(previous =>
                  Math.max(
                    1,
                    previous - 1
                  )
                )
              }
            >

              <ChevronLeft className="h-4 w-4" />

            </Button>


            <Button
              variant="secondary"
              size="sm"

              disabled={
                page === pages
              }

              onClick={() =>
                setPage(previous =>
                  Math.min(
                    pages,
                    previous + 1
                  )
                )
              }
            >

              <ChevronRight className="h-4 w-4" />

            </Button>

          </div>

        </div>

      )}

    </div>
  )
}


// ============================================================
// ADD BUTTON
// ============================================================

export function AddButton({
  children = 'Add record',
  ...props
}) {

  return (

    <Button {...props}>

      <Plus className="h-4 w-4" />

      {children}

    </Button>
  )
}