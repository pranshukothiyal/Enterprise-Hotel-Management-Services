import axios from 'axios'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/gateway',
  timeout: 90000,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('ehms_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config?.url?.includes('/auth/')) {
      localStorage.removeItem('ehms_token')
      localStorage.removeItem('ehms_user')
      window.dispatchEvent(new Event('ehms:unauthorized'))
    }
    return Promise.reject(error)
  },
)

export default http
