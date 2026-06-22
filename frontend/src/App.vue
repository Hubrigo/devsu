<template>
  <div class="min-h-screen bg-gray-50">
    <nav class="bg-gradient-to-r from-blue-950 to-blue-800 shadow-xl">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center h-16">
          <div class="flex items-center mr-auto gap-2">
            <svg class="w-7 h-7 text-blue-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/>
            </svg>
            <span class="text-white font-bold text-xl tracking-wide">Devsu Banking</span>
          </div>
          <div class="flex items-center gap-1">
            <router-link
              v-for="link in navLinks"
              :key="link.to"
              :to="link.to"
              class="text-blue-200 hover:text-white hover:bg-blue-700 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-150"
              active-class="bg-blue-700 !text-white"
            >
              {{ link.label }}
            </router-link>
          </div>
        </div>
      </div>
    </nav>
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <router-view />
    </main>
  </div>
</template>

<script setup>
const navLinks = [
  { to: '/clientes', label: 'Clientes' },
  { to: '/cuentas', label: 'Cuentas' },
  { to: '/movimientos', label: 'Movimientos' },
  { to: '/reportes', label: 'Reportes' }
]
</script>
