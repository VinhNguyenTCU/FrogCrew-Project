<template>
    <div class="request-account-container">
      <h1>Request User Account</h1>
      
      <form @submit.prevent="submitRequest">
        <div class="form-group">
          <label>First Name</label>
          <input type="text" v-model="form.firstName" required>
        </div>
        
        <div class="form-group">
          <label>Last Name</label>
          <input type="text" v-model="form.lastName" required>
        </div>
        
        <div class="form-group">
          <label>Email Address</label>
          <input type="email" v-model="form.email" required>
        </div>
        
        <div class="form-group">
          <label>Phone Number</label>
          <input type="tel" v-model="form.phone" required>
        </div>
        
        <div class="form-group">
          <label>Roles</label>
          <div class="roles-checkboxes">
            <label v-for="role in availableRoles" :key="role.value">
              <input type="checkbox" v-model="form.roles" :value="role.value">
              {{ role.label }}
            </label>
          </div>
        </div>
        
        <button type="submit" class="submit-btn">Submit Request</button>
        
        <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </form>
    </div>
  </template>
  
  <script>
  import { auth } from '../auth/auth'
  
  export default {
    name: 'RequestAccountPage',
    data() {
      return {
        form: {
          firstName: '',
          lastName: '',
          email: '',
          phone: '',
          roles: ['crew_member'] // Default role
        },
        availableRoles: [
          { value: 'crew_member', label: 'Crew Member' },
          { value: 'pilot', label: 'Pilot' },
          { value: 'flight_attendant', label: 'Flight Attendant' },
          { value: 'ground_staff', label: 'Ground Staff' }
        ],
        successMessage: '',
        errorMessage: ''
      }
    },
    methods: {
      submitRequest() {
        auth.requestAccount(
          {
            ...this.form,
            fullName: `${this.form.firstName} ${this.form.lastName}`
          },
          (success) => {
            if (success) {
              this.successMessage = 'Account request submitted successfully!';
              this.errorMessage = '';
              this.resetForm();
            } else {
              this.errorMessage = 'Failed to submit request. Please try again.';
              this.successMessage = '';
            }
          }
        );
      },
      resetForm() {
        this.form = {
          firstName: '',
          lastName: '',
          email: '',
          phone: '',
          roles: ['crew_member']
        };
      }
    }
  }
  </script>
  
  <style scoped>
  .request-account-container {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: bold;
  }
  
  .form-group input[type="text"],
  .form-group input[type="email"],
  .form-group input[type="tel"] {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  
  .roles-checkboxes {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  
  .roles-checkboxes label {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: normal;
    cursor: pointer;
  }
  
  .submit-btn {
    background-color: #3498db;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
  }
  
  .success-message {
    color: green;
    margin-top: 15px;
  }
  
  .error-message {
    color: red;
    margin-top: 15px;
  }
  </style>