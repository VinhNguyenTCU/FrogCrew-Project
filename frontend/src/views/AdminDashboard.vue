<template>
    <div class="admin-dashboard">
      <h1>Admin Dashboard</h1>
      
      <div class="admin-sections">
        <!-- Account Approval Section -->
        <div class="admin-section">
          <h2>Pending Account Requests</h2>
          <div v-if="pendingAccounts.length === 0" class="empty-message">
            No pending account requests
          </div>
          <table v-else class="admin-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Roles</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="account in pendingAccounts" :key="account.email">
                <td>{{ account.fullName }}</td>
                <td>{{ account.email }}</td>
                <td>{{ account.phone }}</td>
                <td>{{ formatRoles(account.roles) }}</td>
                <td>
                  <button @click="approveAccount(account.email)" class="approve-btn">Approve</button>
                  <button @click="rejectAccount(account.email)" class="reject-btn">Reject</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <!-- User Management Section -->
        <div class="admin-section">
          <h2>User Management</h2>
          <div class="search-box">
            <input 
              type="text" 
              v-model="userSearch" 
              placeholder="Search users..."
            >
          </div>
          <table class="admin-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Roles</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in filteredUsers" :key="user.email">
                <td>{{ user.fullName }}</td>
                <td>{{ user.email }}</td>
                <td>
                  <select v-model="user.roles" multiple @change="updateUserRoles(user)">
                    <option 
                      v-for="role in allRoles" 
                      :value="role.value" 
                      :key="role.value"
                    >
                      {{ role.label }}
                    </option>
                  </select>
                </td>
                <td>{{ user.status || 'active' }}</td>
                <td>
                  <button 
                    v-if="user.email !== adminEmail"
                    @click="toggleUserStatus(user)"
                    class="status-btn"
                    :class="{ 'deactivate': user.status !== 'inactive', 'activate': user.status === 'inactive' }"
                  >
                    {{ user.status === 'inactive' ? 'Activate' : 'Deactivate' }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { auth } from '../auth/auth'
  
  export default {
    name: 'AdminDashboard',
    data() {
      return {
        userSearch: '',
        adminEmail: 'Heart@gmail.com',
        allRoles: [
          { value: 'crew_member', label: 'Crew Member' },
          { value: 'pilot', label: 'Pilot' },
          { value: 'flight_attendant', label: 'Flight Attendant' },
          { value: 'ground_staff', label: 'Ground Staff' },
          { value: 'admin', label: 'Admin' }
        ]
      }
    },
    computed: {
      pendingAccounts() {
        return auth.pendingAccounts
      },
      approvedAccounts() {
        return auth.approvedAccounts
      },
      filteredUsers() {
        const searchTerm = this.userSearch.toLowerCase()
        return [
          { ...auth.adminAccount, status: 'active' }, // Include admin account
          ...this.approvedAccounts
        ].filter(user => 
          user.fullName.toLowerCase().includes(searchTerm) ||
          user.email.toLowerCase().includes(searchTerm)
        )
      }
    },
    methods: {
      formatRoles(roles) {
        return roles.map(role => {
          const roleObj = this.allRoles.find(r => r.value === role)
          return roleObj ? roleObj.label : role
        }).join(', ')
      },
      approveAccount(email) {
        auth.approveAccount(email)
        // In a real app, you would show a success message
        alert('Account approved successfully!')
      },
      rejectAccount(email) {
        auth.pendingAccounts = auth.pendingAccounts.filter(a => a.email !== email)
        localStorage.setItem('pendingAccounts', JSON.stringify(auth.pendingAccounts))
        alert('Account request rejected!')
      },
      updateUserRoles(user) {
        // Don't allow removing admin role from admin account
        if (user.email === this.adminEmail && !user.roles.includes('admin')) {
          user.roles.push('admin')
          alert('Cannot remove admin role from primary admin account')
          return
        }
        
        // Update in approved accounts
        const accountIndex = auth.approvedAccounts.findIndex(a => a.email === user.email)
        if (accountIndex !== -1) {
          auth.approvedAccounts[accountIndex].roles = user.roles
          localStorage.setItem('approvedAccounts', JSON.stringify(auth.approvedAccounts))
        }
        
        // Update current user if it's them
        if (auth.user?.email === user.email) {
          auth.user.roles = user.roles
          localStorage.setItem('user', JSON.stringify(auth.user))
        }
      },
      toggleUserStatus(user) {
        if (user.email === this.adminEmail) {
          alert('Cannot deactivate primary admin account')
          return
        }
        
        user.status = user.status === 'inactive' ? 'active' : 'inactive'
        
        // Update in approved accounts
        const accountIndex = auth.approvedAccounts.findIndex(a => a.email === user.email)
        if (accountIndex !== -1) {
          auth.approvedAccounts[accountIndex].status = user.status
          localStorage.setItem('approvedAccounts', JSON.stringify(auth.approvedAccounts))
        }
      }
    }
  }
  </script>
  
  <style scoped>
  .admin-dashboard {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .admin-sections {
    display: grid;
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .admin-section {
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }
  
  .empty-message {
    padding: 20px;
    text-align: center;
    color: #666;
  }
  
  .admin-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 15px;
  }
  
  .admin-table th, 
  .admin-table td {
    padding: 12px 15px;
    border-bottom: 1px solid #eee;
    text-align: left;
  }
  
  .admin-table th {
    background-color: #f8f9fa;
  }
  
  .admin-table select {
    min-width: 150px;
    padding: 5px;
  }
  
  .approve-btn {
    background-color: #28a745;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 4px;
    cursor: pointer;
    margin-right: 5px;
  }
  
  .reject-btn {
    background-color: #dc3545;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .status-btn {
    border: none;
    padding: 5px 10px;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .deactivate {
    background-color: #ffc107;
    color: black;
  }
  
  .activate {
    background-color: #17a2b8;
    color: white;
  }
  
  .search-box {
    margin-bottom: 15px;
  }
  
  .search-box input {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  </style>