<template>
  <div class="schedule-container">
    <h1>Schedule</h1>
    
    <!-- Search and Filter Controls -->
    <div class="controls">
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="Search schedules..." 
          @input="filterClasses"
        />
        <span class="search-icon">🔍</span>
      </div>
      
      <div class="filter-group">
        <label>Filter by:</label>
        <select v-model="selectedDay" @change="filterClasses">
          <option value="">All Days</option>
          <option v-for="day in days" :value="day" :key="day">{{ day }}</option>
        </select>
        
        <select v-model="selectedLevel" @change="filterClasses">
          <option value="">All Levels</option>
          <option v-for="level in levels" :value="level" :key="level">{{ level }}</option>
        </select>
      </div>
    </div>
    
    <!-- Schedule Table -->
    <div class="table-responsive">
      <table class="schedule-table">
        <thead>
          <tr>
            <th @click="sortBy('className')">
              Class Name
              <span v-if="sortColumn === 'className'">
                {{ sortDirection === 'asc' ? '↑' : '↓' }}
              </span>
            </th>
            <th @click="sortBy('instructor')">
              Instructor
              <span v-if="sortColumn === 'instructor'">
                {{ sortDirection === 'asc' ? '↑' : '↓' }}
              </span>
            </th>
            <th @click="sortBy('day')">
              Day
              <span v-if="sortColumn === 'day'">
                {{ sortDirection === 'asc' ? '↑' : '↓' }}
              </span>
            </th>
            <th @click="sortBy('time')">
              Time
              <span v-if="sortColumn === 'time'">
                {{ sortDirection === 'asc' ? '↑' : '↓' }}
              </span>
            </th>
            <th @click="sortBy('level')">
              Level
              <span v-if="sortColumn === 'level'">
                {{ sortDirection === 'asc' ? '↑' : '↓' }}
              </span>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="classItem in paginatedClasses" :key="classItem.id">
            <td>{{ classItem.className }}</td>
            <td>{{ classItem.instructor }}</td>
            <td>{{ classItem.day }}</td>
            <td>{{ classItem.time }}</td>
            <td>
              <span class="level-badge" :class="classItem.level.toLowerCase()">
                {{ classItem.level }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Pagination -->
    <div class="pagination">
      <button 
        @click="prevPage" 
        :disabled="currentPage === 1"
      >
        Previous
      </button>
      <span>Page {{ currentPage }} of {{ totalPages }}</span>
      <button 
        @click="nextPage" 
        :disabled="currentPage === totalPages"
      >
        Next
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ScheduleView',
  data() {
    return {
      classes: [
        { id: 1, className: 'Yoga Basics', instructor: 'Sarah Johnson', day: 'Monday', time: '9:00 AM - 10:00 AM', level: 'Beginner' },
        { id: 2, className: 'Power Yoga', instructor: 'Mike Chen', day: 'Tuesday', time: '6:00 PM - 7:00 PM', level: 'Advanced' },
        { id: 3, className: 'Meditation', instructor: 'Priya Patel', day: 'Wednesday', time: '7:00 AM - 7:30 AM', level: 'All Levels' },
        { id: 4, className: 'Pilates', instructor: 'Emma Wilson', day: 'Thursday', time: '5:30 PM - 6:30 PM', level: 'Intermediate' },
        { id: 5, className: 'Zumba', instructor: 'Carlos Mendez', day: 'Friday', time: '6:00 PM - 7:00 PM', level: 'Beginner' },
        { id: 6, className: 'HIIT', instructor: 'Jamal Brown', day: 'Saturday', time: '8:00 AM - 9:00 AM', level: 'Advanced' },
        { id: 7, className: 'Yoga Flow', instructor: 'Sarah Johnson', day: 'Sunday', time: '10:00 AM - 11:00 AM', level: 'Intermediate' },
      ],
      searchQuery: '',
      selectedDay: '',
      selectedLevel: '',
      days: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'],
      levels: ['Beginner', 'Intermediate', 'Advanced', 'All Levels'],
      filteredClasses: [],
      sortColumn: '',
      sortDirection: 'asc',
      currentPage: 1,
      itemsPerPage: 5
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.filteredClasses.length / this.itemsPerPage)
    },
    paginatedClasses() {
      const start = (this.currentPage - 1) * this.itemsPerPage
      const end = start + this.itemsPerPage
      return this.filteredClasses.slice(start, end)
    }
  },
  created() {
    this.filteredClasses = [...this.classes]
  },
  methods: {
    filterClasses() {
      this.filteredClasses = this.classes.filter(classItem => {
        const matchesSearch = classItem.className.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                            classItem.instructor.toLowerCase().includes(this.searchQuery.toLowerCase())
        
        const matchesDay = this.selectedDay ? classItem.day === this.selectedDay : true
        const matchesLevel = this.selectedLevel ? classItem.level === this.selectedLevel : true
        
        return matchesSearch && matchesDay && matchesLevel
      })
      
      this.currentPage = 1 // Reset to first page when filtering
      this.sortClasses()
    },
    sortBy(column) {
      if (this.sortColumn === column) {
        this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc'
      } else {
        this.sortColumn = column
        this.sortDirection = 'asc'
      }
      this.sortClasses()
    },
    sortClasses() {
      this.filteredClasses.sort((a, b) => {
        let modifier = 1
        if (this.sortDirection === 'desc') modifier = -1
        
        if (a[this.sortColumn] < b[this.sortColumn]) return -1 * modifier
        if (a[this.sortColumn] > b[this.sortColumn]) return 1 * modifier
        return 0
      })
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
      }
    }
  }
}
</script>

<style scoped>
.schedule-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-box {
  position: relative;
}

.search-box input {
  padding: 8px 30px 8px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 250px;
}

.search-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
}

.filter-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.filter-group select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.table-responsive {
  overflow-x: auto;
}

.schedule-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.schedule-table th, 
.schedule-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.schedule-table th {
  background-color: #f8f9fa;
  cursor: pointer;
  user-select: none;
}

.schedule-table th:hover {
  background-color: #e9ecef;
}

.schedule-table tr:hover {
  background-color: #f5f5f5;
}

.level-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8em;
  font-weight: bold;
}

.level-badge.beginner {
  background-color: #d4edda;
  color: #155724;
}

.level-badge.intermediate {
  background-color: #fff3cd;
  color: #856404;
}

.level-badge.advanced {
  background-color: #f8d7da;
  color: #721c24;
}

.level-badge.all-levels {
  background-color: #d1ecf1;
  color: #0c5460;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .controls {
    flex-direction: column;
  }
  
  .search-box input {
    width: 100%;
  }
  
  .filter-group {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>