export const cn = (...classes) => classes.filter(Boolean).join(' ')

export const formatCurrency = (value, currency = 'INR') => {
  const amount = Number(value ?? 0)
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency, maximumFractionDigits: 2 }).format(Number.isFinite(amount) ? amount : 0)
}

export const formatDate = (value, options = {}) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return new Intl.DateTimeFormat('en-IN', { dateStyle: 'medium', ...options }).format(date)
}

export const formatDateTime = (value) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return new Intl.DateTimeFormat('en-IN', { dateStyle: 'medium', timeStyle: 'short' }).format(date)
}

export const humanize = (value = '') => String(value).replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase())

export const getNested = (object, path) => path.split('.').reduce((value, key) => value?.[key], object)

export const compactPayload = (value) => Object.fromEntries(
  Object.entries(value).filter(([, item]) => item !== '' && item !== undefined)
)

export const errorMessage = (error) => {
  const data = error?.response?.data
  if (typeof data === 'string') return data
  return data?.message || data?.error || error?.message || 'Something went wrong. Please try again.'
}

export const makeId = (prefix) => `${prefix}-${crypto.randomUUID()}`
