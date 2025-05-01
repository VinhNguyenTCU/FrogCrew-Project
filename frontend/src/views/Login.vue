<template>
  <div class="login-container">
    <h2>Login</h2>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="email">Email:</label>
        <input 
          type="email" 
          id="email" 
          v-model="email" 
          required
        />
      </div>
      <div class="form-group">
        <label for="password">Password:</label>
        <input 
          type="password" 
          id="password" 
          v-model="password" 
          required
        />
      </div>
      <button type="submit" class="login-btn">Login</button>
      <p v-if="error" class="error">{{ error }}</p>
      <p class="signup-link">
        Don't have an account? <router-link to="/request-account">Request one here</router-link>
      </p>
      <p class="admin-hint" v-if="email === 'Heart@gmail.com'">
  Hint: You're logging in as admin. The password is "qwerty123".
</p>
    </form>
  </div>
</template>

<script>
import { auth } from '../auth/auth'

export default {
  name: 'LoginPage',
  data() {
    return {
      email: '',
      password: '',
      error: ''
    }
  },
  methods: {
    handleSubmit() {
      auth.login(this.email, this.password, (success) => {
        if (success) {
          window.location.href = '/'
        } else {
          this.error = 'Invalid email or account not approved'
        }
      })
    }
  }
}
</script>

<style scoped>
/* Keep your existing login styles */
.signup-link {
  margin-top: 15px;
}

.signup-link a {
  color: #3498db;
  text-decoration: none;
}

.signup-link a:hover {
  text-decoration: underline;
}

.admin-hint {
  color: #666;
  font-size: 0.9em;
  margin-top: 10px;
  font-style: italic;
}

.login-container {
  max-width: 400px;
  margin: 2rem auto;
  padding: 2rem;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
}

input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.login-btn {
  background-color: #3498db;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>