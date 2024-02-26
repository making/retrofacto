/// <reference types="vitest" />
import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    test: {
        globals: true,
        environment: 'jsdom',
    },
    server: {
        proxy: {
            "/boards": {
                target: "http://localhost:8080",
                changeOrigin: true,
                secure: false,
            },
            "/token": {
                target: "http://localhost:8080",
                changeOrigin: true,
                secure: false,
            }
        }
    }
})
