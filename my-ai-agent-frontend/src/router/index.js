import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'
import MyAppChat from '../views/MyAppChat.vue'
import SuperAgent from '../views/SuperAgent.vue'

const routes = [
  { path: '/', component: HomePage },
  { path: '/my-app', component: MyAppChat },
  { path: '/super-agent', component: SuperAgent }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
