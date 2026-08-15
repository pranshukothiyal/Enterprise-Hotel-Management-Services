import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { Eye, MoreHorizontal, Pencil, RefreshCw, Trash2 } from 'lucide-react'
import toast from 'react-hot-toast'
import {
  AddButton,
  Badge,
  Button,
  Card,
  ConfirmDialog,
  DataTable,
  EmptyState,
  Field,
  Input,
  Modal,
  PageHeader,
  SearchBox,
  Select,
  Spinner,
  Textarea,
  statusTone
} from './ui'
import {
  compactPayload,
  errorMessage,
  getNested,
  humanize
} from '../lib/utils'

const initialFromFields = (fields) =>
  Object.fromEntries(fields.map((field) => [field.name, field.defaultValue ?? '']))

function FormControl({ field, value, onChange, context }) {
  const common = {
    value: value ?? '',
    onChange: (event) => onChange(field.name, event.target.value),
    required: field.required,
    placeholder: field.placeholder
  }
  const options =
    typeof field.options === 'function' ? field.options(context) : field.options

  if (field.type === 'textarea')
    return <Textarea {...common} rows={field.rows || 3} />

  if (field.type === 'select')
    return (
      <Select {...common}>
        <option value="">Select {field.label.toLowerCase()}</option>
        {(options || []).map((option) => {
          const item =
            typeof option === 'string'
              ? { value: option, label: humanize(option) }
              : option
          return (
            <option key={item.value} value={item.value}>
              {item.label}
            </option>
          )
        })}
      </Select>
    )

  return (
    <Input
      {...common}
      type={field.type || 'text'}
      min={field.min}
      max={field.max}
      step={field.step}
    />
  )
}

export default function CrudPage({
  eyebrow = 'Management',
  title,
  description,
  idKey,
  fields,
  columns,
  loadRows,
  getRow,
  createRow,
  updateRow,
  deleteRow,
  prepareForm = (row) => row,
  toPayload = compactPayload,
  context = {},
  filters = [],
  searchKeys = [],
  customActions,
  createLabel = 'Add record',
  allowCreate = true,
  allowEdit = true,
  allowDelete = true,
  rowSubtitle
}) {
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [detailLoading, setDetailLoading] = useState(false)
  const [query, setQuery] = useState('')
  const [filterValues, setFilterValues] = useState(
    Object.fromEntries(
      filters.map((filter) => [filter.name, filter.defaultValue || ''])
    )
  )
  const [modal, setModal] = useState(null)
  const [form, setForm] = useState(initialFromFields(fields))
  const [selected, setSelected] = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const loadRowsRef = useRef(loadRows)
  loadRowsRef.current = loadRows
  const filtersKey = JSON.stringify(filterValues)

  const refresh = useCallback(async () => {
    setLoading(true)
    try {
      setRows((await loadRowsRef.current(filterValues)) || [])
    } catch (error) {
      toast.error(errorMessage(error))
      setRows([])
    } finally {
      setLoading(false)
    }
  }, [filtersKey])

  useEffect(() => {
    refresh()
  }, [refresh])

  const filteredRows = useMemo(() => {
    const needle = query.trim().toLowerCase()
    if (!needle) return rows
    return rows.filter((row) =>
      searchKeys.some((key) =>
        String(getNested(row, key) ?? '')
          .toLowerCase()
          .includes(needle)
      )
    )
  }, [rows, query, searchKeys])

  const openCreate = () => {
    setForm(initialFromFields(fields))
    setSelected(null)
    setModal('form')
  }

  const openEdit = (row) => {
    setSelected(row)
    setForm({ ...initialFromFields(fields), ...prepareForm(row) })
    setModal('form')
  }

  const closeModal = () => {
    if (!saving) {
      setModal(null)
      setSelected(null)
    }
  }

  const setValue = (name, value) =>
    setForm((current) => ({ ...current, [name]: value }))

  const submit = async (event) => {
    event.preventDefault()
    setSaving(true)
    try {
      const payload = toPayload(form, selected)
      if (selected && updateRow) await updateRow(selected[idKey], payload)
      else await createRow(payload)
      toast.success(
        selected ? 'Record updated successfully' : 'Record created successfully'
      )
      setModal(null)
      setSelected(null)
      await refresh()
    } catch (error) {
      toast.error(errorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  const confirmDelete = async () => {
    setSaving(true)
    try {
      await deleteRow(deleteTarget[idKey])
      toast.success('Record deleted')
      setDeleteTarget(null)
      await refresh()
    } catch (error) {
      toast.error(errorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  const openView = async (row) => {
    setSelected(row)
    setModal('view')
    if (!getRow) return
    setDetailLoading(true)
    try {
      setSelected(await getRow(row[idKey]))
    } catch (error) {
      toast.error(errorMessage(error))
    } finally {
      setDetailLoading(false)
    }
  }

  const actions = (row) => (
    <div className="inline-flex items-center gap-1">
      <Button
        variant="ghost"
        size="sm"
        onClick={() => openView(row)}
        title="View"
      >
        <Eye className="h-4 w-4" />
      </Button>
      {customActions?.(row, refresh)}
      {allowEdit && updateRow && (
        <Button
          variant="ghost"
          size="sm"
          onClick={() => openEdit(row)}
          title="Edit"
        >
          <Pencil className="h-4 w-4" />
        </Button>
      )}
      {allowDelete && deleteRow && (
        <Button
          variant="ghost"
          size="sm"
          onClick={() => setDeleteTarget(row)}
          title="Delete"
          className="text-rose-600"
        >
          <Trash2 className="h-4 w-4" />
        </Button>
      )}
    </div>
  )

  return (
    <>
      <PageHeader
        eyebrow={eyebrow}
        title={title}
        description={description}
        action={
          allowCreate && createRow ? (
            <AddButton onClick={openCreate}>{createLabel}</AddButton>
          ) : null
        }
      />
      <Card className="p-4 sm:p-5">
        <div className="mb-5 flex flex-col gap-3 xl:flex-row xl:items-end xl:justify-between">
          <div className="flex flex-wrap items-end gap-3 flex-1">
            <Field label="Search">
              <SearchBox
                value={query}
                onChange={setQuery}
                placeholder={`Search ${title.toLowerCase()}…`}
              />
            </Field>
            {filters.map((filter) => (
              <Field key={filter.name} label={filter.label}>
                {filter.type === 'text' ? (
                  <Input
                    value={filterValues[filter.name]}
                    placeholder={
                      filter.placeholder ||
                      `Filter by ${filter.label.toLowerCase()}`
                    }
                    onChange={(e) =>
                      setFilterValues((v) => ({
                        ...v,
                        [filter.name]: e.target.value
                      }))
                    }
                  />
                ) : (
                  <Select
                    value={filterValues[filter.name]}
                    onChange={(e) =>
                      setFilterValues((v) => ({
                        ...v,
                        [filter.name]: e.target.value
                      }))
                    }
                  >
                    <option value="">{filter.allLabel || 'All'}</option>
                    {(typeof filter.options === 'function'
                      ? filter.options(context)
                      : filter.options || []
                    ).map((option) => {
                      const item =
                        typeof option === 'string'
                          ? { value: option, label: humanize(option) }
                          : option
                      return (
                        <option key={item.value} value={item.value}>
                          {item.label}
                        </option>
                      )
                    })}
                  </Select>
                )}
              </Field>
            ))}
          </div>
          <Button variant="secondary" onClick={refresh} className="h-10">
            <RefreshCw className="h-4 w-4" />
            Refresh
          </Button>
        </div>
        {loading ? (
          <Spinner />
        ) : filteredRows.length ? (
          <DataTable
            rows={filteredRows.map((row) => ({ ...row, id: row[idKey] }))}
            columns={columns}
            actions={actions}
          />
        ) : (
          <EmptyState
            title={`No ${title.toLowerCase()} found`}
            action={
              allowCreate && createRow ? (
                <AddButton onClick={openCreate}>{createLabel}</AddButton>
              ) : null
            }
          />
        )}
      </Card>

      <Modal
        open={modal === 'form'}
        onClose={closeModal}
        title={selected ? `Edit ${title.replace(/s$/, '')}` : createLabel}
        subtitle={
          selected
            ? `${idKey}: ${selected[idKey]}`
            : 'Complete the required fields and save.'
        }
        footer={
          <>
            <Button variant="secondary" onClick={closeModal}>
              Cancel
            </Button>
            <Button type="submit" form="crud-form" loading={saving}>
              {selected ? 'Save changes' : 'Create'}
            </Button>
          </>
        }
      >
        <form id="crud-form" onSubmit={submit} className="grid gap-4 sm:grid-cols-2">
          {fields.map((field) => (
            <Field
              key={field.name}
              label={field.label}
              hint={field.hint}
              className={field.full ? 'sm:col-span-2' : ''}
            >
              <FormControl
                field={field}
                value={form[field.name]}
                onChange={setValue}
                context={context}
              />
            </Field>
          ))}
        </form>
      </Modal>

      <Modal
        open={modal === 'view'}
        onClose={closeModal}
        title={`${title.replace(/s$/, '')} details`}
        subtitle={selected && rowSubtitle?.(selected)}
        size="lg"
        footer={
          <Button variant="secondary" onClick={closeModal}>
            Close
          </Button>
        }
      >
        {detailLoading ? (
          <Spinner label="Loading full record…" />
        ) : (
          selected && (
            <div className="grid gap-3 sm:grid-cols-2">
              {columns.map((column) => {
                const value = getNested(selected, column.key)
                return (
                  <div
                    key={column.key}
                    className="rounded-2xl border border-slate-200 p-4 dark:border-slate-800"
                  >
                    <p className="text-xs font-bold uppercase tracking-wide text-slate-400">
                      {column.label}
                    </p>
                    <div className="mt-2 break-words text-sm font-medium">
                      {column.render
                        ? column.render(value, selected)
                        : value ?? '—'}
                    </div>
                  </div>
                )
              })}
            </div>
          )
        )}
      </Modal>

      <ConfirmDialog
        open={Boolean(deleteTarget)}
        title={`Delete ${title.replace(/s$/, '')}`}
        message={`This will permanently remove ${
          deleteTarget?.[idKey] || 'this record'
        }. This action cannot be undone.`}
        onCancel={() => setDeleteTarget(null)}
        onConfirm={confirmDelete}
        loading={saving}
      />
    </>
  )
}

export const statusColumn = (key, label = 'Status') => ({
  key,
  label,
  render: (value) => (
    <Badge tone={statusTone(value)}>{value || 'UNKNOWN'}</Badge>
  )
})
export const moneyColumn = (key, label = 'Amount') => ({
  key,
  label,
  render: (value) =>
    new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(Number(value || 0))
})