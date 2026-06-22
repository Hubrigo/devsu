<template>
  <div class="space-y-6">
    <!-- Formulario -->
    <div class="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
      <div class="bg-gradient-to-r from-blue-900 to-blue-700 px-6 py-4">
        <h2 class="text-white font-semibold text-lg flex items-center gap-2">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/>
          </svg>
          {{ editando ? 'Editar Cuenta' : 'Nueva Cuenta' }}
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

        <form @submit.prevent="guardar">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Número de Cuenta <span class="text-red-500">*</span>
              </label>
              <input v-model="form.numeroCuenta" :disabled="editando" required
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition disabled:bg-gray-50 disabled:text-gray-500" />
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Tipo <span class="text-red-500">*</span>
              </label>
              <select v-model="form.tipo" required
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition bg-white">
                <option value="AHORROS">Ahorros</option>
                <option value="CORRIENTE">Corriente</option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Saldo Inicial <span class="text-red-500">*</span>
              </label>
              <div class="relative">
                <span class="absolute inset-y-0 left-3 flex items-center text-gray-400 text-sm">$</span>
                <input v-model.number="form.saldoInicial" type="number" step="0.01" min="0" required
                  class="w-full pl-7 pr-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
              </div>
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Cliente ID <span class="text-red-500">*</span>
              </label>
              <input v-model.number="form.clienteId" type="number" required
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
            </div>

            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">Estado</label>
              <select v-model="form.estado"
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition bg-white">
                <option :value="true">Activa</option>
                <option :value="false">Inactiva</option>
              </select>
            </div>
          </div>

          <div class="flex gap-3 mt-6 pt-5 border-t border-gray-100">
            <button type="submit"
              class="inline-flex items-center gap-2 px-5 py-2.5 bg-blue-700 hover:bg-blue-800 text-white text-sm font-semibold rounded-lg transition-colors shadow-sm">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
              </svg>
              {{ editando ? 'Actualizar' : 'Crear Cuenta' }}
            </button>
            <button v-if="editando" type="button" @click="cancelar"
              class="inline-flex items-center gap-2 px-5 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-700 text-sm font-semibold rounded-lg transition-colors">
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Tabla -->
    <div class="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
      <div class="flex items-center justify-between px-6 py-4 border-b border-gray-100">
        <h2 class="font-semibold text-gray-800 text-lg">Cuentas registradas</h2>
        <span class="bg-blue-100 text-blue-700 text-xs font-semibold px-2.5 py-1 rounded-full">
          {{ cuentas.length }} total
        </span>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="bg-gray-50 text-gray-500 text-xs uppercase tracking-wider">
              <th class="px-5 py-3 text-left font-semibold">ID</th>
              <th class="px-5 py-3 text-left font-semibold">Nro. Cuenta</th>
              <th class="px-5 py-3 text-left font-semibold">Tipo</th>
              <th class="px-5 py-3 text-right font-semibold">Saldo Inicial</th>
              <th class="px-5 py-3 text-right font-semibold">Saldo Disponible</th>
              <th class="px-5 py-3 text-left font-semibold">Estado</th>
              <th class="px-5 py-3 text-left font-semibold">Cliente ID</th>
              <th class="px-5 py-3 text-left font-semibold">Acciones</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
            <tr v-for="c in cuentas" :key="c.id" class="hover:bg-blue-50/40 transition-colors">
              <td class="px-5 py-3.5 text-gray-400 font-mono text-xs">#{{ c.id }}</td>
              <td class="px-5 py-3.5 font-mono font-medium text-gray-700">{{ c.numeroCuenta }}</td>
              <td class="px-5 py-3.5">
                <span :class="[
                  'inline-block text-xs font-semibold px-2.5 py-1 rounded-full',
                  c.tipo === 'AHORROS'
                    ? 'bg-emerald-100 text-emerald-700'
                    : 'bg-sky-100 text-sky-700'
                ]">
                  {{ c.tipo === 'AHORROS' ? 'Ahorros' : 'Corriente' }}
                </span>
              </td>
              <td class="px-5 py-3.5 text-right text-gray-700 font-medium">
                $ {{ c.saldoInicial?.toFixed(2) }}
              </td>
              <td class="px-5 py-3.5 text-right font-semibold"
                :class="c.saldoDisponible >= 0 ? 'text-green-700' : 'text-red-600'">
                $ {{ c.saldoDisponible?.toFixed(2) }}
              </td>
              <td class="px-5 py-3.5">
                <span :class="[
                  'inline-flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full',
                  c.estado ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-600'
                ]">
                  <span :class="['w-1.5 h-1.5 rounded-full', c.estado ? 'bg-green-500' : 'bg-red-400']"/>
                  {{ c.estado ? 'Activa' : 'Inactiva' }}
                </span>
              </td>
              <td class="px-5 py-3.5 text-gray-500">#{{ c.clienteId }}</td>
              <td class="px-5 py-3.5">
                <div class="flex gap-2">
                  <button @click="editar(c)"
                    class="px-3 py-1.5 text-xs font-semibold text-blue-700 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors">
                    Editar
                  </button>
                  <button @click="toggleEstado(c)"
                    :class="[
                      'px-3 py-1.5 text-xs font-semibold rounded-lg transition-colors',
                      c.estado
                        ? 'text-red-600 bg-red-50 hover:bg-red-100'
                        : 'text-green-700 bg-green-50 hover:bg-green-100'
                    ]">
                    {{ c.estado ? 'Desactivar' : 'Activar' }}
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="cuentas.length === 0">
              <td colspan="8" class="px-5 py-12 text-center text-gray-400">
                <svg class="w-10 h-10 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                    d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/>
                </svg>
                No hay cuentas registradas
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
import cuentasApi from '../api/cuentasApi'

const cuentas = ref([])
const editando = ref(false)
const editId = ref(null)
const mensaje = ref({ texto: '', tipo: '' })
const form = ref({ numeroCuenta: '', tipo: 'AHORROS', saldoInicial: 0, clienteId: null, estado: true })

const cargar = async () => { const { data } = await cuentasApi.listar(); cuentas.value = data }

const guardar = async () => {
  try {
    editando.value ? await cuentasApi.actualizar(editId.value, form.value) : await cuentasApi.crear(form.value)
    mostrar(editando.value ? 'Cuenta actualizada exitosamente' : 'Cuenta creada exitosamente', 'ok')
    cancelar(); cargar()
  } catch (e) {
    mostrar(e.response?.data?.message || 'Error al guardar', 'error')
  }
}

const editar = (c) => {
  editando.value = true; editId.value = c.id; form.value = { ...c }
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const toggleEstado = async (c) => {
  const accion = c.estado ? 'desactivar' : 'activar'
  if (!confirm(`¿Deseas ${accion} esta cuenta?`)) return
  try {
    await cuentasApi.actualizarParcial(c.id, { estado: !c.estado })
    mostrar(`Cuenta ${c.estado ? 'desactivada' : 'activada'} exitosamente`, 'ok')
    cargar()
  } catch (e) {
    mostrar(e.response?.data?.message || 'Error al actualizar estado', 'error')
  }
}

const cancelar = () => {
  editando.value = false; editId.value = null
  form.value = { numeroCuenta: '', tipo: 'AHORROS', saldoInicial: 0, clienteId: null, estado: true }
}

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
