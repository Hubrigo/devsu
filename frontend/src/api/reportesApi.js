import axios from 'axios'

const api = axios.create({ baseURL: '/api/reportes' })

export default {
  generar: (params) => api.get('/', { params })
}
