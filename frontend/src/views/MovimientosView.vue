<template>
  <div class="space-y-6">
    <!-- Formulario -->
    <div class="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
      <div class="bg-gradient-to-r from-blue-900 to-blue-700 px-6 py-4">
        <h2 class="text-white font-semibold text-lg flex items-center gap-2">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"/>
          </svg>
          Registrar Movimiento
        </h2>
      </div>

      <div class="p-6">
        <transition name="fade">
          <div v-if="mensaje.texto"
            :class="[
              'flex items-center gap-3 px-4 py-3 rounded-xl mb-5 text-sm font-medium border',
              mensaje.tipo === 'error'
                ? 'bg-red-50 text-red-700 border-red-200'
                : 'bg-green-50 text-green-700 border-green-200'
            ]">
            <svg class="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path v-if="mensaje.tipo === 'error'" fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
              <path v-else fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
            </svg>
            {{ mensaje.texto }}
          </div>
        </transition>

        <form @submit.prevent="registrar">
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-5">
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Tipo <span class="text-red-500">*</span>
              </label>
              <select v-model="form.tipoMovimiento" required
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition bg-white">
                <option value="DEPOSITO">Depósito</option>
                <option value="RETIRO">Retiro</option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Valor <span class="text-red-500">*</span>
              </label>
              <div class="relative">
                <span class="absolute inset-y-0 left-3 flex items-center text-gray-400 text-sm">$</span>
                <input v-model.number="form.valor" type="number" step="0.01" min="0.01" required
                  class="w-full pl-7 pr-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
              </div>
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Cuenta ID <span class="text-red-500">*</span>
              </label>
              <input v-model.number="form.cuentaId" type="number" required
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
            </div>
          </div>

          <div class="mt-6 pt-5 border-t border-gray-100">
            <button type="submit"
              :class="[
                'inline-flex items-center gap-2 px-5 py-2.5 text-white text-sm font-semibold rounded-lg transition-colors shadow-sm',
                form.tipoMovimiento === 'DEPOSITO'
                  ? 'bg-green-600 hover:bg-green-700'
                  : 'bg-red-500 hover:bg-red-600'
              ]">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path v-if="form.tipoMovimiento === 'DEPOSITO'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"/>
              </svg>
              Registrar {{ form.tipoMovimiento === 'DEPOSITO' ? 'Depósito' : 'Retiro' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Tabla -->
    <div class="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
      <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100">
        <h2 class="font-semibold text-gray-800 text-lg">Movimientos</h2>
        <button @click="cargar"
          class="inline-flex items-center gap-2 text-sm text-blue-700 hover:text-blue-900 font-medium transition-colors">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
          </svg>
          Actualizar
        </button>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="bg-gray-50 text-gray-500 text-xs uppercase tracking-wider">
              <th class="px-5 py-3 text-left font-semibold">ID</th>
              <th class="px-5 py-3 text-left font-semibold">Fecha</th>
              <th class="px-5 py-3 text-left font-semibold">Tipo</th>
              <th class="px-5 py-3 text-right font-semibold">Valor</th>
              <th class="px-5 py-3 text-right font-semibold">Saldo Resultante</th>
              <th class="px-5 py-3 text-left font-semibold">Cuenta ID</th>
              <th class="px-5 py-3 text-left font-semibold">Nro. Cuenta</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
            <tr v-for="m in movimientos" :key="m.id" class="hover:bg-blue-50/40 transition-colors">
              <td class="px-5 py-3.5 text-gray-400 font-mono text-xs">#{{ m.id }}</td>
              <td class="px-5 py-3.5 text-gray-500 text-xs">{{ formatFecha(m.fecha) }}</td>
              <td class="px-5 py-3.5">
                <span :class="[
                  'inline-flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full',
                  m.tipoMovimiento === 'DEPOSITO'
                    ? 'bg-green-100 text-green-700'
                    : 'bg-red-100 text-red-600'
                ]">
                  {{ m.tipoMovimiento === 'DEPOSITO' ? '▲ Depósito' : '▼ Retiro' }}
                </span>
              </td>
              <td class="px-5 py-3.5 text-right font-semibold"
                :class="m.tipoMovimiento === 'DEPOSITO' ? 'text-green-600' : 'text-red-500'">
                {{ m.tipoMovimiento === 'RETIRO' ? '-' : '+' }}$ {{ m.valor?.toFixed(2) }}
              </td>
              <td class="px-5 py-3.5 text-right text-gray-700 font-medium">
                $ {{ m.saldo?.toFixed(2) }}
              </td>
              <td class="px-5 py-3.5 text-gray-500">#{{ m.cuentaId }}</td>
              <td class="px-5 py-3.5 font-mono text-gray-600">{{ m.numeroCuenta }}</td>
            </tr>
            <tr v-if="movimientos.length === 0">
              <td colspan="7" class="px-5 py-12 text-center text-gray-400">
                <svg class="w-10 h-10 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                    d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"/>
                </svg>
                No hay movimientos registrados
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import movimientosApi from '../api/movimientosApi'
import cuentasApi from '../api/cuentasApi'
import clientesApi from '../api/clientesApi'

const movimientos = ref([])
const mensaje = ref({ texto: '', tipo: '' })
const form = ref({ tipoMovimiento: 'DEPOSITO', valor: null, cuentaId: null })

const cargar = async () => { const { data } = await movimientosApi.listar(); movimientos.value = data }

const registrar = async () => {
  // Validar que la cuenta exista, esté activa y su cliente esté activo
  let cuenta
  try {
    const { data } = await cuentasApi.buscarPorId(form.value.cuentaId)
    cuenta = data
  } catch {
    mostrar('No se encontró la cuenta especificada', 'error')
    return
  }

  if (!cuenta.estado) {
    mostrar('La cuenta está desactivada. No se pueden registrar movimientos.', 'error')
    return
  }

  try {
    const { data: cliente } = await clientesApi.buscarPorId(cuenta.clienteId)
    if (!cliente.estado) {
      mostrar('El cliente propietario de esta cuenta está inactivo. No se pueden registrar movimientos.', 'error')
      return
    }
  } catch {
    mostrar('No se pudo verificar el estado del cliente', 'error')
    return
  }

  try {
    await movimientosApi.registrar(form.value)
    mostrar('Movimiento registrado exitosamente', 'ok')
    form.value = { tipoMovimiento: 'DEPOSITO', valor: null, cuentaId: null }
    cargar()
  } catch (e) {
    mostrar(e.response?.data?.message || 'Error al registrar', 'error')
  }
}

const formatFecha = (f) => f ? new Date(f).toLocaleString('es-EC') : '—'
const mostrar = (texto, tipo) => {
  mensaje.value = { texto, tipo }
  setTimeout(() => mensaje.value = { texto: '', tipo: '' }, 4000)
}

onMounted(cargar)
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s, transform 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-6px); }
</style>
