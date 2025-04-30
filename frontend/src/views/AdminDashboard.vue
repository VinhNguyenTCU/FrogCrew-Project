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
                <td>
                  <button 
                    v-if="user.email !== adminEmail"
                    @click="confirmDeleteUser(user)"
                    class="delete-btn"
                  >
                    Delete User
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="showDeleteModal" class="modal-overlay">
            <div class="modal-content">
              <h3>Confirm User Deletion</h3>
              <p>Are you sure you want to permanently delete {{ userToDelete.fullName }}?</p>
              <div class="modal-actions">
                <button @click="deleteUser" class="confirm-delete-btn">Delete Permanently</button>
                <button @click="cancelDelete" class="cancel-btn">Cancel</button>
              </div>
            </div>
          </div>
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
        ],
        showDeleteModal: false,
        userToDelete: null
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
        alert('Account approved successfully!')
      },
      rejectAccount(email) {
        auth.pendingAccounts = auth.pendingAccounts.filter(a => a.email !== email)
        localStorage.setItem('pendingAccounts', JSON.stringify(auth.pendingAccounts))
        alert('Account request rejected!')
      },
      updateUserRoles(user) {
        // Cannot remove admin role from admin
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
      },
      confirmDeleteUser(user) {
        this.userToDelete = user
        this.showDeleteModal = true
      },

      cancelDelete() {
        this.showDeleteModal = false
        this.userToDelete = null
      },
      deleteUser() {
      if (!this.userToDelete) return

      // Remove from approved accounts
      auth.approvedAccounts = auth.approvedAccounts.filter(
        user => user.email !== this.userToDelete.email
      )

      // Remove from pending accounts if exists
      auth.pendingAccounts = auth.pendingAccounts.filter(
        user => user.email !== this.userToDelete.email
      )

      // Update local storage
      localStorage.setItem('approvedAccounts', JSON.stringify(auth.approvedAccounts))
      localStorage.setItem('pendingAccounts', JSON.stringify(auth.pendingAccounts))

      // If deleted user is currently logged in, log them out
      if (auth.user?.email === this.userToDelete.email) {
        auth.logout(() => {
          this.$router.push('/login')
        })
      }

      this.showDeleteModal = false
      this.userToDelete = null
    },

    // Update the updateUserRoles method to handle storage
    updateUserRoles(user) {
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
  
  /* .deactivate {
    background-color: #ffc107;
    color: black;
  }
  
  .activate {
    background-color: #17a2b8;
    color: white;
  } */
  
  .search-box {
    margin-bottom: 15px;
  }
  
  .search-box input {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
  }

  .delete-btn {
  background-color: #f44336;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.confirm-delete-btn {
  background-color: #f44336;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}
  </style>