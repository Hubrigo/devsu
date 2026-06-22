import axios from 'axios'

const api = axios.create({ baseURL: '/api/movimientos' })

export default {
  listar: () => api.get(''),
  buscarPorId: (id) => api.get(`/${id}`),
  registrar: (data) => api.post('', data),
  actualizar: (id, data) => api.put(`/${id}`, data)
}
