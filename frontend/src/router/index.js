import { createRouter, createWebHistory } from 'vue-router'
import { auth } from '@/auth/auth'
import { createApp } from 'vue'
import Home from '@/views/Home.vue'
import Schedule from '@/views/Schedule.vue'
import Login from '@/views/Login.vue'
import Profile from '@/views/Profile.vue'
import RequestAccount from '@/views/RequestAccount.vue'
import CrewMembers from '@/views/CrewMembers.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import Availability from '@/views/Availability.vue'
import App from '@/App.vue'



const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/schedule',
    name: 'Schedule',
    component: Schedule
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true } // Protect this route
  },
  {
    path: '/request-account',
    name: 'RequestAccount',
    component: RequestAccount
  },
  {
    path: '/crew-members',
    name: 'CrewMembers',
    component: CrewMembers,
    meta: { requiresAuth: true }
  },
  {
    path: '/availability',
    name: 'Availability',
    component: Availability,
    meta: { 
      requiresAuth: true,
      requiresCrew: true
     }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  auth.checkAuth()
  
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin && !auth.isAdmin()) {
    next('/') // Redirect to home if not admin
  } else {
    next()
  }

  if (to.meta.requiresCrew && (auth.isAdmin() || !auth.isCrewMember())) {
    next('/')
    return
  }
})


export default router


createApp(App).use(router).mount("#app")