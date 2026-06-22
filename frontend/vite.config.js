import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api/clientes': {
        target: 'http://ms-clientes:8081',
        rewrite: (path) => path.replace(/^\/api\/clientes/, '/clientes'),
        changeOrigin: true
      },
      '/api/cuentas': {
        target: 'http://ms-cuentas:8082',
        rewrite: (path) => path.replace(/^\/api\/cuentas/, '/cuentas'),
        changeOrigin: true
      },
      '/api/movimientos': {
        target: 'http://ms-cuentas:8082',
        rewrite: (path) => path.replace(/^\/api\/movimientos/, '/movimientos'),
        changeOrigin: true
      },
      '/api/reportes': {
        target: 'http://ms-cuentas:8082',
        rewrite: (path) => path.replace(/^\/api\/reportes/, '/reportes'),
        changeOrigin: true
      }
    }
  }
})
