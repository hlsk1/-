import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  base: '/',
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  build: {
    outDir: '../resources/static',
    emptyOutDir: false,
    assetsDir: 'assets'
  },
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8081',
      '/uploads': 'http://localhost:8081',
      '/CarImages': 'http://localhost:8081'
    }
  }
})
