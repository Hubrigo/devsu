import axios from 'axios'

const api = axios.create({ baseURL: '/api/cuentas' })

export default {
  listar: () => api.get(''),
  buscarPorId: (id) => api.get(`/${id}`),
  crear: (data) => api.post('', data),
  actualizar: (id, data) => api.put(`/${id}`, data),
  actualizarParcial: (id, data) => api.patch(`/${id}`, data)
}
