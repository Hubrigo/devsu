import { createRouter, createWebHistory } from 'vue-router'
import ClientesView from '../views/ClientesView.vue'
import CuentasView from '../views/CuentasView.vue'
import MovimientosView from '../views/MovimientosView.vue'
import ReportesView from '../views/ReportesView.vue'

const routes = [
  { path: '/', redirect: '/clientes' },
  { path: '/clientes', component: ClientesView },
  { path: '/cuentas', component: CuentasView },
  { path: '/movimientos', component: MovimientosView },
  { path: '/reportes', component: ReportesView }
]

export default createRouter({ history: createWebHistory(), routes })
