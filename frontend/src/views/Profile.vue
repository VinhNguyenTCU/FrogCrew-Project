<template>
  <div class="profile-container">
    <h1>User Profile</h1>
    
    <div class="profile-content">
      <!-- Left Section - Profile Picture -->
      <div class="profile-left">
        <div class="profile-picture">
          <img :src="user.photoUrl || defaultPhoto" alt="Profile Picture">
          <button @click="triggerFileInput" class="change-photo-btn">Change Photo</button>
          <input 
            type="file" 
            ref="fileInput"
            @change="handlePhotoChange"
            accept="image/*"
            style="display: none"
          >
        </div>
        <h2>{{ user.fullName }}</h2>
        <p class="member-role">{{ formattedRoles }}</p>
        <p class="member-since">Member since {{ formattedJoinDate }}</p>
      </div>
      
      <!-- Right Section - Profile Form -->
      <div class="profile-right">
        <form @submit.prevent="updateProfile">
          <div class="form-group">
            <label>First Name</label>
            <input type="text" v-model="user.firstName" required>
          </div>
          
          <div class="form-group">
            <label>Last Name</label>
            <input type="text" v-model="user.lastName" required>
          </div>
          
          <div class="form-group">
            <label>Email Address</label>
            <input type="email" v-model="user.email" required disabled>
          </div>
          
          <div class="form-group">
            <label>Phone Number</label>
            <input type="tel" v-model="user.phone" required>
          </div>
          
          <div class="form-actions">
            <button type="submit" class="save-btn">Save Changes</button>
            <button type="button" @click="resetForm" class="cancel-btn">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { auth } from '../auth/auth'
import defaultPhoto from '@/assets/default-profile.png'

export default {
  name: 'ProfilePage',
  data() {
    return {
      user: {
        firstName: '',
        lastName: '',
        fullName: '',
        email: '',
        phone: '',
        roles: [],
        photoUrl: '',
        joinDate: new Date()
      },
      defaultPhoto: defaultPhoto,
      originalUserData: {}
    }
  },
  computed: {
    formattedJoinDate() {
      return new Date(this.user.joinDate).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long'
      })
    },
    formattedRoles() {
      return this.user.roles.map(role => {
        // Convert role values to display names
        const roleNames = {
          'crew_member': 'Crew Member',
          'pilot': 'Pilot',
          'flight_attendant': 'Flight Attendant',
          'ground_staff': 'Ground Staff'
        };
        return roleNames[role] || role;
      }).join(', ');
    }
  },
  created() {
    this.loadUserData()
    this.originalUserData = JSON.parse(JSON.stringify(this.user))
  },
  methods: {
    loadUserData() {
      if (auth.user) {
        this.user = { 
          ...auth.user,
          firstName: auth.user.fullName?.split(' ')[0] || '',
          lastName: auth.user.fullName?.split(' ').slice(1).join(' ') || ''
        }
      }
    },
    updateProfile() {
      // Update user data
      const updatedUser = {
        ...this.user,
        fullName: `${this.user.firstName} ${this.user.lastName}`
      };
      
      // Update in auth system
      auth.user = updatedUser;
      localStorage.setItem('user', JSON.stringify(updatedUser));
      
      // Update in approved accounts
      const accountIndex = auth.approvedAccounts.findIndex(a => a.email === this.user.email);
      if (accountIndex !== -1) {
        auth.approvedAccounts[accountIndex] = updatedUser;
        localStorage.setItem('approvedAccounts', JSON.stringify(auth.approvedAccounts));
      }
      
      alert('Profile updated successfully!');
      this.originalUserData = JSON.parse(JSON.stringify(this.user));
    },
    // Keep existing methods for photo handling and reset
    triggerFileInput() { /* ... */ },
    handlePhotoChange(event) { /* ... */ },
    resetForm() { /* ... */ }
  }
}
</script>

<style scoped>
.member-role {
  font-weight: bold;
  color: #3498db;
  margin: 5px 0;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-content {
  display: flex;
  gap: 40px;
  margin-top: 30px;
}

.profile-left {
  width: 300px;
  text-align: center;
}

.profile-right {
  flex: 1;
}

.profile-picture {
  position: relative;
  margin-bottom: 20px;
}

.profile-picture img {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  object-fit: cover;
  border: 5px solid #f0f0f0;
}

.change-photo-btn {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.7);
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.member-since {
  color: #777;
  font-style: italic;
  margin-top: 5px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 30px;
}

.save-btn {
  background-color: #3498db;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.cancel-btn {
  background-color: #e0e0e0;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
  }
  
  .profile-left {
    width: 100%;
  }
}
</style>