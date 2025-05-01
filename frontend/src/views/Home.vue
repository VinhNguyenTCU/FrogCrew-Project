<template>
  <div class="homepage">
    <header class="hero-section">
      <h1>Welcome to the Frog Crew System</h1>
      <p class="subtitle">Streamlining team coordination and event management</p>
    </header>

    <main class="content-section">
      <div class="features">
        <div class="feature-card">
          <div class="feature-icon">📅</div>
          <h3>Event Scheduling</h3>
          <p>Manage and view all upcoming games and practices in one place.</p>
        </div>
        
        <div class="feature-card">
          <div class="feature-icon">👥</div>
          <h3>Crew Coordination</h3>
          <p>Easily coordinate staff availability and assignments.</p>
        </div>
        
        <div class="feature-card">
          <div class="feature-icon">📊</div>
          <h3>Admin Dashboard</h3>
          <p>Comprehensive tools for managing the entire sports program.</p>
        </div>
      </div>

      <div class="cta-section">
        <router-link 
          v-if="!isAuthenticated"
          to="/login" 
          class="cta-button"
        >
          Get Started
        </router-link>
        <div v-else class="quick-links">
          <router-link to="/schedule" class="action-button">
            View Schedule
          </router-link>
          <router-link 
            v-if="!isAdmin"
            to="/availability" 
            class="action-button"
          >
            Update Availability
          </router-link>
          <router-link 
            v-if="isAdmin"
            to="/admin" 
            class="action-button"
          >
            Admin Dashboard
          </router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { auth } from '../auth/auth'

export default {
  name: 'HomePage',
  computed: {
    isAuthenticated() {
      return auth.isAuthenticated
    },
    isAdmin() {
      return auth.isAdmin()
    },
    isCrewMember() {
      return auth.isCrewMember()
    }
  }
}
</script>

<style scoped>
.homepage {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.hero-section {
  text-align: center;
  padding: 60px 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 40px;
}

.hero-section h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 15px;
}

.subtitle {
  font-size: 1.2rem;
  color: #7f8c8d;
  max-width: 600px;
  margin: 0 auto;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 50px;
}

.feature-card {
  padding: 25px;
  border-radius: 8px;
  background: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 2.5rem;
  margin-bottom: 15px;
}

.feature-card h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.feature-card p {
  color: #7f8c8d;
  line-height: 1.5;
}

.cta-section {
  text-align: center;
  margin-top: 40px;
}

.cta-button {
  display: inline-block;
  padding: 12px 30px;
  background-color: #3498db;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 1.1rem;
  transition: background-color 0.3s;
}

.cta-button:hover {
  background-color: #2980b9;
}

.quick-links {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.action-button {
  padding: 10px 20px;
  background-color: #2ecc71;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.action-button:hover {
  background-color: #27ae60;
}

@media (max-width: 768px) {
  .hero-section h1 {
    font-size: 2rem;
  }
  
  .features {
    grid-template-columns: 1fr;
  }
  
  .quick-links {
    flex-direction: column;
    align-items: center;
  }
}
</style>