<template>
  <div class="app">
    <!-- Navigation Bar - Hidden on Login Page -->
    <nav class="navbar" v-if="!isLoginPage && !isRequestPage">
      <ul>
        <li><router-link to="/" class="nav-button">Home</router-link></li>
        <li><router-link to="/schedule" class="nav-button">Schedule</router-link></li>
        <li v-if="isAuthenticated && !isAdmin">
          <router-link to="/availability" class="nav-button">Availability</router-link>
        </li>
        <li v-if="isAuthenticated">
          <router-link to="/crew-members" class="nav-button">Crew Members</router-link>
        </li>
        <li v-if="isAuthenticated">
          <router-link to="/profile" class="nav-button">Profile</router-link>
        </li>
        <!-- Admin link only for admin users -->
        <li v-if="isAdmin">
          <router-link to="/admin" class="nav-button">Admin</router-link>
        </li>
      </ul>
    </nav>

    <!-- Main Content -->
    <div class="main-content" :class="{ 'full-width': isLoginPage }">
      <!-- Auth Button (Hidden on Login Page) -->
      <button 
        v-if="!isLoginPage && !isAuthenticated" 
        @click="goToLogin" 
        class="login-button"
      >
        Log In
      </button>
      <button 
        v-if="!isLoginPage && isAuthenticated" 
        @click="logout" 
        class="login-button"
      >
        Log Out ({{ username }})
      </button>

      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import { auth } from '@/auth/auth'

export default {
  name: 'App',
  data() {
    return {
      isAuthenticated: false,
      username: null
    }
  },
  computed: {
    isLoginPage() {
      return this.$route.path === '/login'
    },
    isRequestPage() {
      return this.$route.path === '/request-account'
    },
    isAdmin() {
      return auth.isAdmin()
    }
  },
  created() {
    this.checkAuth()
  },
  methods: {
    checkAuth() {
      this.isAuthenticated = auth.checkAuth()
      this.username = auth.username
    },
    goToLogin() {
      this.$router.push('/login')
    },
    logout() {
      auth.logout(() => {
        this.checkAuth()
        this.$router.push('/login')
      })
    }
  }
}
</script>

<style src="@/assets/styles.css"></style>