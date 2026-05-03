import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index.js'
import 'highlight.js/styles/github-dark.css'

createApp(App).use(router).mount('#app')
