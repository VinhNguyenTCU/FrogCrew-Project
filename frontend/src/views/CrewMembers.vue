<template>
  <div class="crew-members-container">
    <h1>Crew Members</h1>
    
    <div class="crew-table">
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Roles</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="member in approvedCrewMembers" :key="member.email">
            <td>{{ member.fullName }}</td>
            <td>{{ member.email }}</td>
            <td>{{ member.phone }}</td>
            <td>{{ member.roles.join(', ') }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { auth } from '../auth/auth'

export default {
  name: 'CrewMembersPage',
  computed: {
    approvedCrewMembers() {
      return auth.approvedAccounts.filter(account => 
        account.roles.includes('crew_member')
      );
    }
  }
}
</script>

<style scoped>
.crew-members-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.crew-table {
  margin-top: 20px;
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f8f9fa;
  font-weight: bold;
}

tr:hover {
  background-color: #f5f5f5;
}
</style>