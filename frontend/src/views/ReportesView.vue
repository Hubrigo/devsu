<template>
  <div class="space-y-6">
    <!-- Filtros -->
    <div class="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
      <div class="bg-gradient-to-r from-blue-900 to-blue-700 px-6 py-4">
        <h2 class="text-white font-semibold text-lg flex items-center gap-2">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
          </svg>
          Estado de Cuenta
        </h2>
      </div>

      <div class="p-6">
        <transition name="fade">
          <div v-if="error"
            class="flex items-center gap-3 px-4 py-3 rounded-xl mb-5 text-sm font-medium border bg-red-50 text-red-700 border-red-200">
            <svg class="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
            </svg>
            {{ error }}
          </div>
        </transition>

        <form @submit.prevent="consultar">
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-5 items-end">
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Cliente ID <span class="text-red-500">*</span>
              </label>
              <input v-model.number="filtro.clienteId" type="number" required
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Fecha Inicio <span class="text-red-500">*</span>
              </label>
              <input v-model="filtro.fechaInicio" type="date" required
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Fecha Fin <span class="text-red-500">*</span>
              </label>
              <input v-model="filtro.fechaFin" type="date" required
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
            </div>
          </div>

          <div class="mt-6 pt-5 border-t border-gray-100">
            <button type="submit" :disabled="cargando"
              class="inline-flex items-center gap-2 px-5 py-2.5 bg-blue-700 hover:bg-blue-800 disabled:bg-blue-400 text-white text-sm font-semibold rounded-lg transition-colors shadow-sm">
              <svg v-if="cargando" class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
              </svg>
              <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
              </svg>
              {{ cargando ? 'Consultando...' : 'Generar Reporte' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Resultados -->
    <div v-if="reporte.length > 0" class="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
      <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100">
        <h2 class="font-semibold text-gray-800 text-lg">Resultado del Reporte</h2>
        <span class="bg-blue-100 text-blue-700 text-xs font-semibold px-2.5 py-1 rounded-full">
          {{ reporte.length }} movimientos
        </span>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="bg-gray-50 text-gray-500 text-xs uppercase tracking-wider">
              <th class="px-5 py-3 text-left font-semibold">Fecha</th>
              <th class="px-5 py-3 text-left font-semibold">Cliente</th>
              <th class="px-5 py-3 text-left font-semibold">Nro. Cuenta</th>
              <th class="px-5 py-3 text-left font-semibold">Tipo</th>
              <th class="px-5 py-3 text-right font-semibold">Saldo Inicial</th>
              <th class="px-5 py-3 text-left font-semibold">Estado</th>
              <th class="px-5 py-3 text-right font-semibold">Movimiento</th>
              <th class="px-5 py-3 text-right font-semibold">Saldo Disponible</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
            <tr v-for="(r, i) in reporte" :key="i" class="hover:bg-blue-50/40 transition-colors">
              <td class="px-5 py-3.5 text-gray-500 text-xs whitespace-nowrap">{{ r.fecha }}</td>
              <td class="px-5 py-3.5 font-medium text-gray-800">{{ r.cliente }}</td>
              <td class="px-5 py-3.5 font-mono text-gray-600">{{ r.numeroCuenta }}</td>
              <td class="px-5 py-3.5">
                <span :class="[
                  'inline-block text-xs font-semibold px-2.5 py-1 rounded-full',
                  r.tipo === 'AHORROS' ? 'bg-emerald-100 text-emerald-700' : 'bg-sky-100 text-sky-700'
                ]">
                  {{ r.tipo === 'AHORROS' ? 'Ahorros' : 'Corriente' }}
                </span>
              </td>
              <td class="px-5 py-3.5 text-right text-gray-700">$ {{ r.saldoInicial?.toFixed(2) }}</td>
              <td class="px-5 py-3.5">
                <span :class="[
                  'inline-flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full',
                  r.estado ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-600'
                ]">
                  <span :class="['w-1.5 h-1.5 rounded-full', r.estado ? 'bg-green-500' : 'bg-red-400']"/>
                  {{ r.estado ? 'Activa' : 'Inactiva' }}
                </span>
              </td>
              <td class="px-5 py-3.5 text-right font-semibold"
                :class="r.movimiento >= 0 ? 'text-green-600' : 'text-red-500'">
                {{ r.movimiento >= 0 ? '+' : '' }}$ {{ r.movimiento?.toFixed(2) }}
              </td>
              <td class="px-5 py-3.5 text-right font-bold text-gray-800">
                $ {{ r.saldoDisponible?.toFixed(2) }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Sin resultados -->
    <div v-else-if="consultado && reporte.length === 0"
      class="bg-white rounded-2xl shadow-md border border-gray-100 p-12 text-center">
      <svg class="w-12 h-12 mx-auto mb-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
          d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
      </svg>
      <p class="text-gray-500 text-sm">No se encontraron movimientos en el rango de fechas indicado.</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import reportesApi from '../api/reportesApi'

const filtro = ref({ clienteId: null, fechaInicio: '', fechaFin: '' })
const reporte = ref([])
const error = ref('')
const cargando = ref(false)
const consultado = ref(false)

const consultar = async () => {
  error.value = ''; cargando.value = true; consultado.value = false
  try {
    const { data } = await reportesApi.generar({
      clienteId: filtro.value.clienteId,
      fechaInicio: filtro.value.fechaInicio,
      fechaFin: filtro.value.fechaFin
    })
    reporte.value = data
    consultado.value = true
  } catch (e) {
    error.value = e.response?.data?.message || 'Error al generar el reporte'
  } finally {
    cargando.value = false
  }
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s, transform 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-6px); }
</style>
