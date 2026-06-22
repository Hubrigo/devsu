<template>
  <div class="space-y-6">
    <!-- Formulario -->
    <div class="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
      <div class="bg-gradient-to-r from-blue-900 to-blue-700 px-6 py-4">
        <h2 class="text-white font-semibold text-lg flex items-center gap-2">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
          </svg>
          {{ editando ? 'Editar Cliente' : 'Nuevo Cliente' }}
        </h2>
      </div>

      <div class="p-6">
        <!-- Alerta -->
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

        <form @submit.prevent="guardar" novalidate>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
            <!-- Nombre -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Nombre <span class="text-red-500">*</span>
              </label>
              <input v-model="form.nombre" required
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
            </div>

            <!-- Identificación -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Identificación <span class="text-red-500">*</span>
              </label>
              <input v-model="form.identificacion" :disabled="editando" required
                maxlength="10"
                @input="soloNumeros('identificacion', $event)"
                :class="inputClass('identificacion')" />
              <p v-if="errores.identificacion" class="mt-1 text-xs text-red-600">
                {{ errores.identificacion }}
              </p>
              <p v-else class="mt-1 text-xs text-gray-400">Solo números, máximo 10 dígitos</p>
            </div>

            <!-- Género -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">Género</label>
              <select v-model="form.genero"
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition bg-white">
                <option value="">-- Seleccionar --</option>
                <option value="M">Masculino</option>
                <option value="F">Femenino</option>
              </select>
            </div>

            <!-- Edad -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">Edad</label>
              <input v-model.number="form.edad" type="number" min="0" max="120"
                @input="validarEdad"
                :class="inputClass('edad')" />
              <p v-if="errores.edad" class="mt-1 text-xs text-red-600">
                {{ errores.edad }}
              </p>
            </div>

            <!-- Dirección -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">Dirección</label>
              <input v-model="form.direccion"
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition" />
            </div>

            <!-- Teléfono -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">Teléfono</label>
              <input v-model="form.telefono"
                maxlength="10"
                @input="soloNumeros('telefono', $event); validarTelefono()"
                :class="inputClass('telefono')" />
              <p v-if="errores.telefono" class="mt-1 text-xs text-red-600">
                {{ errores.telefono }}
              </p>
              <p v-else class="mt-1 text-xs text-gray-400">Debe iniciar con 3, máximo 10 dígitos</p>
            </div>

            <!-- Contraseña -->
            <div class="sm:col-span-2 sm:max-w-sm">
              <label class="block text-sm font-semibold text-gray-700 mb-1">
                Contraseña <span class="text-red-500">*</span>
              </label>
              <div class="relative">
                <input v-model="form.contrasena"
                  :type="mostrarPassword ? 'text' : 'password'"
                  @input="validarContrasena"
                  :class="inputClass('contrasena')" />
                <button type="button" @click="mostrarPassword = !mostrarPassword"
                  class="absolute inset-y-0 right-0 px-3 flex items-center text-gray-400 hover:text-gray-600">
                  <svg v-if="!mostrarPassword" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                  </svg>
                  <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"/>
                  </svg>
                </button>
              </div>
              <!-- Indicador de fortaleza -->
              <div v-if="form.contrasena" class="mt-2">
                <div class="flex gap-1 mb-1">
                  <div v-for="i in 4" :key="i"
                    :class="['h-1.5 flex-1 rounded-full transition-all duration-300', barraFuerza(i)]"/>
                </div>
                <p :class="['text-xs font-medium', fuerzaInfo.textColor]">
                  {{ fuerzaInfo.label }}
                </p>
              </div>
              <p v-if="errores.contrasena" class="mt-1 text-xs text-red-600">
                {{ errores.contrasena }}
              </p>
              <p v-else-if="!editando" class="mt-1 text-xs text-gray-400">
                Mín. 8 caracteres, mayúscula, minúscula, número y símbolo
              </p>
            </div>

            <!-- Estado -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-1">Estado</label>
              <select v-model="form.estado"
                class="w-full px-3 py-2.5 rounded-lg border border-gray-300 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition bg-white">
                <option :value="true">Activo</option>
                <option :value="false">Inactivo</option>
              </select>
            </div>
          </div>

          <div class="flex gap-3 mt-6 pt-5 border-t border-gray-100">
            <button type="submit"
              class="inline-flex items-center gap-2 px-5 py-2.5 bg-blue-700 hover:bg-blue-800 text-white text-sm font-semibold rounded-lg transition-colors shadow-sm">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
              </svg>
              {{ editando ? 'Actualizar' : 'Crear Cliente' }}
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
        <h2 class="font-semibold text-gray-800 text-lg">Clientes registrados</h2>
        <span class="bg-blue-100 text-blue-700 text-xs font-semibold px-2.5 py-1 rounded-full">
          {{ clientes.length }} total
        </span>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="bg-gray-50 text-gray-500 text-xs uppercase tracking-wider">
              <th class="px-5 py-3 text-left font-semibold">ID</th>
              <th class="px-5 py-3 text-left font-semibold">Nombre</th>
              <th class="px-5 py-3 text-left font-semibold">Identificación</th>
              <th class="px-5 py-3 text-left font-semibold">Dirección</th>
              <th class="px-5 py-3 text-left font-semibold">Teléfono</th>
              <th class="px-5 py-3 text-left font-semibold">Estado</th>
              <th class="px-5 py-3 text-left font-semibold">Acciones</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
            <tr v-for="c in clientes" :key="c.id" class="hover:bg-blue-50/40 transition-colors">
              <td class="px-5 py-3.5 text-gray-400 font-mono text-xs">#{{ c.id }}</td>
              <td class="px-5 py-3.5 font-medium text-gray-800">{{ c.nombre }}</td>
              <td class="px-5 py-3.5 text-gray-600 font-mono">{{ c.identificacion }}</td>
              <td class="px-5 py-3.5 text-gray-500">{{ c.direccion || '—' }}</td>
              <td class="px-5 py-3.5 text-gray-600">{{ c.telefono || '—' }}</td>
              <td class="px-5 py-3.5">
                <span :class="[
                  'inline-flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full',
                  c.estado ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-600'
                ]">
                  <span :class="['w-1.5 h-1.5 rounded-full', c.estado ? 'bg-green-500' : 'bg-red-400']"/>
                  {{ c.estado ? 'Activo' : 'Inactivo' }}
                </span>
              </td>
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
            <tr v-if="clientes.length === 0">
              <td colspan="7" class="px-5 py-12 text-center text-gray-400">
                <svg class="w-10 h-10 mx-auto mb-3 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                    d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"/>
                </svg>
                No hay clientes registrados
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import clientesApi from '../api/clientesApi'

const clientes = ref([])
const editando = ref(false)
const editId = ref(null)
const formOriginal = ref(null)
const mostrarPassword = ref(false)
const mensaje = ref({ texto: '', tipo: '' })
const errores = ref({})

const form = ref({
  nombre: '', identificacion: '', genero: '', edad: null,
  direccion: '', telefono: '', contrasena: '', estado: true
})

// --- Helpers de clase ---
const inputClass = (campo) => [
  'w-full px-3 py-2.5 rounded-lg border text-sm focus:outline-none focus:ring-2 focus:border-transparent transition',
  errores.value[campo]
    ? 'border-red-400 focus:ring-red-300 bg-red-50'
    : 'border-gray-300 focus:ring-blue-500'
]

// --- Fuerza de contraseña ---
const fuerzaScore = computed(() => {
  const p = form.value.contrasena
  if (!p) return 0
  let s = 0
  if (p.length >= 8) s++
  if (/[A-Z]/.test(p)) s++
  if (/[a-z]/.test(p)) s++
  if (/[0-9]/.test(p)) s++
  if (/[^A-Za-z0-9]/.test(p)) s++
  return s
})

const fuerzaInfo = computed(() => {
  const s = fuerzaScore.value
  if (!form.value.contrasena) return { label: '', textColor: '' }
  if (s <= 2) return { label: 'Débil', textColor: 'text-red-500' }
  if (s <= 3) return { label: 'Media', textColor: 'text-yellow-500' }
  return { label: 'Fuerte', textColor: 'text-green-600' }
})

const barraFuerza = (i) => {
  const s = fuerzaScore.value
  if (!form.value.contrasena) return 'bg-gray-200'
  const colorMap = s <= 2 ? 'bg-red-400' : s <= 3 ? 'bg-yellow-400' : 'bg-green-500'
  const filled = Math.ceil((s / 5) * 4)
  return i <= filled ? colorMap : 'bg-gray-200'
}

// --- Validaciones en tiempo real ---
const soloNumeros = (campo, event) => {
  form.value[campo] = event.target.value.replace(/\D/g, '')
  if (campo === 'identificacion') validarIdentificacion()
}

const validarIdentificacion = () => {
  const v = form.value.identificacion
  if (!v) { errores.value.identificacion = 'La identificación es requerida'; return }
  if (!/^\d+$/.test(v)) { errores.value.identificacion = 'Solo se permiten números'; return }
  if (v.length > 10) { errores.value.identificacion = 'Máximo 10 dígitos'; return }
  delete errores.value.identificacion
}

const validarEdad = () => {
  const edad = form.value.edad
  if (edad !== null && edad !== '' && (isNaN(edad) || edad < 0 || edad > 120)) {
    errores.value.edad = 'Ingrese una edad válida (0–120)'
  } else {
    delete errores.value.edad
  }
}

const validarTelefono = () => {
  const t = form.value.telefono
  if (!t) { delete errores.value.telefono; return }
  // Al editar, solo validar formato si el valor fue modificado
  if (editando.value && t === formOriginal.value?.telefono) {
    delete errores.value.telefono; return
  }
  if (!t.startsWith('3')) { errores.value.telefono = 'Debe iniciar con el dígito 3'; return }
  if (t.length > 10) { errores.value.telefono = 'Máximo 10 dígitos'; return }
  delete errores.value.telefono
}

const validarContrasena = () => {
  const p = form.value.contrasena
  if (!p && !editando.value) { errores.value.contrasena = 'La contraseña es requerida'; return }
  if (!p) { delete errores.value.contrasena; return }
  if (p.length < 8) { errores.value.contrasena = 'Mínimo 8 caracteres'; return }
  if (!/[A-Z]/.test(p)) { errores.value.contrasena = 'Debe incluir al menos una mayúscula'; return }
  if (!/[a-z]/.test(p)) { errores.value.contrasena = 'Debe incluir al menos una minúscula'; return }
  if (!/[0-9]/.test(p)) { errores.value.contrasena = 'Debe incluir al menos un número'; return }
  if (!/[^A-Za-z0-9]/.test(p)) { errores.value.contrasena = 'Debe incluir al menos un símbolo (!@#$...)'; return }
  delete errores.value.contrasena
}

const validarTodo = () => {
  // Al editar, la identificación es de solo lectura: omitir su validación
  if (!editando.value) validarIdentificacion()
  else delete errores.value.identificacion
  validarEdad()
  validarTelefono()
  validarContrasena()
  if (!form.value.nombre.trim()) errores.value.nombre = 'El nombre es requerido'
  else delete errores.value.nombre
  return Object.keys(errores.value).length === 0
}

// --- CRUD ---
const cargar = async () => {
  const { data } = await clientesApi.listar()
  clientes.value = data
}

const guardar = async () => {
  if (!validarTodo()) {
    mostrar('Revisa los campos con errores', 'error')
    return
  }
  try {
    if (editando.value) {
      const payload = { ...form.value }
      if (!payload.contrasena) delete payload.contrasena
      await clientesApi.actualizarParcial(editId.value, payload)
      mostrar('Cliente actualizado exitosamente', 'ok')
    } else {
      await clientesApi.crear(form.value)
      mostrar('Cliente creado exitosamente', 'ok')
    }
    cancelar(); cargar()
  } catch (e) {
    mostrar(e.response?.data?.message || 'Error al guardar', 'error')
  }
}

const editar = (c) => {
  editando.value = true; editId.value = c.id
  form.value = { ...c, contrasena: '' }
  formOriginal.value = { ...c }
  errores.value = {}
  mostrarPassword.value = false
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const cancelar = () => {
  editando.value = false; editId.value = null; formOriginal.value = null
  form.value = { nombre: '', identificacion: '', genero: '', edad: null, direccion: '', telefono: '', contrasena: '', estado: true }
  errores.value = {}
  mostrarPassword.value = false
}

const toggleEstado = async (c) => {
  const accion = c.estado ? 'desactivar' : 'activar'
  if (!confirm(`¿Deseas ${accion} este cliente?`)) return
  try {
    await clientesApi.actualizarParcial(c.id, { estado: !c.estado })
    mostrar(`Cliente ${c.estado ? 'desactivado' : 'activado'} exitosamente`, 'ok')
    cargar()
  } catch (e) {
    mostrar(e.response?.data?.message || 'Error al actualizar estado', 'error')
  }
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
