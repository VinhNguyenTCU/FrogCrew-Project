<template>
  <div class="schedule-container">
    <div class="calendar-controls">
      <div class="calendar-header">
        <h1>Event Calendar</h1>
        <div v-if="isAdmin" class="admin-controls">
          <button @click="showEventModal = true" class="add-event-btn">
            + Add Event
          </button>
        </div>
      </div>
      
      <div class="navigation-controls">
        <button @click="previousWeek" class="nav-btn">◀ Previous</button>
        <h2>{{ currentWeekRange }}</h2>
        <button @click="nextWeek" class="nav-btn">Next ▶</button>
      </div>
      
      <div class="sport-filter">
        <label>Filter by Sport:</label>
        <select v-model="selectedSport" @change="filterEvents">
          <option value="all">All Sports</option>
          <option v-for="sport in sportsList" :value="sport" :key="sport">
            {{ sport }}
          </option>
        </select>
      </div>
    </div>

    <div class="calendar-week">
      <div class="day-header-row">
        <div class="day-header" v-for="day in weekDays" :key="day.date">
          <div class="weekday">{{ day.weekday }}</div>
          <div class="date" :class="{ 'today': day.isToday }">
            {{ day.date }}
          </div>
        </div>
      </div>
      
      <div class="calendar-grid">
        <div class="time-column">
          <div class="time-slot" v-for="time in timeSlots" :key="time">
            {{ time }}
          </div>
        </div>
        
        <div class="day-column" v-for="day in weekDays" :key="day.dateString">
          <div class="day-events">
            <div
              class="calendar-event"
              v-for="event in getEventsForDay(day.dateString)"
              :key="event.id"
              :style="{
                backgroundColor: getSportColor(event.sport),
                top: calculateEventPosition(event.startTime),
                height: calculateEventDuration(event.startTime, event.endTime)
              }"
              @click="selectEvent(event)"
            >
              <div class="event-content">
                <div class="event-time">{{ formatTimeRange(event.startTime, event.endTime) }}</div>
                <div class="event-title">{{ event.title }}</div>
                <div class="event-sport">{{ event.sport }}</div>
                <div v-if="event.assignedCrew.length > 0" class="event-crew">
                  <span v-for="crew in event.assignedCrew" :key="crew">
                    {{ getCrewName(crew) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Event Modal -->
    <div v-if="showEventModal" class="modal-overlay">
      <div class="modal-content">
        <h2>{{ editingEvent ? 'Edit Event' : 'Create New Event' }}</h2>
        <form @submit.prevent="saveEvent">
          <div class="form-group">
            <label>Event Title</label>
            <input v-model="newEvent.title" required>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>Sport</label>
              <select v-model="newEvent.sport" required>
                <option v-for="sport in sportsList" :value="sport" :key="sport">
                  {{ sport }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label>Date</label>
              <input type="date" v-model="newEvent.date" required>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>Start Time</label>
              <input type="time" v-model="newEvent.startTime" required>
            </div>
            
            <div class="form-group">
              <label>End Time</label>
              <input type="time" v-model="newEvent.endTime" required>
            </div>
          </div>
          
          <div class="form-group">
            <label>Location</label>
            <input v-model="newEvent.location" required>
          </div>
          
          <div class="form-group" v-if="isAdmin">
            <label>Assign Crew Members</label>
            <div class="crew-selection">
              <div v-for="crew in availableCrew" :key="crew.id" class="crew-checkbox">
                <input
                  type="checkbox"
                  :id="'crew-'+crew.id"
                  :value="crew.id"
                  v-model="newEvent.assignedCrew"
                >
                <label :for="'crew-'+crew.id">{{ crew.name }}</label>
              </div>
            </div>
          </div>
          
          <div class="form-actions">
            <button type="submit" class="save-btn">Save</button>
            <button type="button" @click="cancelEvent" class="cancel-btn">Cancel</button>
            <button
              v-if="editingEvent && isAdmin"
              type="button"
              @click="deleteEvent"
              class="delete-btn"
            >
              Delete
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { auth } from '../auth/auth'
import eventSync from '@/mixins/eventSync'

export default {
  mixins: [eventSync],
  name: 'ScheduleView',
  data() {
    const sportsList = [
        "Women's Soccer",
        "Men's Soccer",
        "Women's Basketball",
        "Men's Basketball",
        "Volleyball",
        "Baseball",
        "Softball",
        "Tennis"
    ]
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    
    return {
      currentDate: new Date(today),
      selectedSport: 'all',
      showEventModal: false,
      editingEvent: null,
      newEvent: this.getEmptyEvent(),

      sportsList,
        newEvent: {
        title: '',
        sport: sportsList[0], // Initialize with first sport
        date: new Date().toISOString().split('T')[0],
        startTime: '09:00',
        endTime: '10:00',
        location: '',
        assignedCrew: []
      },
      
      sportColors: {
        "Women's Soccer": "#4CAF50",
        "Men's Soccer": "#8BC34A",
        "Women's Basketball": "#2196F3",
        "Men's Basketball": "#3F51B5",
        "Volleyball": "#FF9800",
        "Baseball": "#795548",
        "Softball": "#FF5722",
        "Tennis": "#9C27B0"
      },
      timeSlots: [
        '6:00 AM', '7:00 AM', '8:00 AM', '9:00 AM', '10:00 AM',
        '11:00 AM', '12:00 PM', '1:00 PM', '2:00 PM', '3:00 PM',
        '4:00 PM', '5:00 PM', '6:00 PM', '7:00 PM', '8:00 PM'
      ],
      allEvents: [], // Will be loaded from API
      availableCrew: [] // Will be loaded from API
    }
  },
  computed: {
    isAdmin() {
      return auth.isAdmin()
    },
    currentWeekRange() {
      const start = new Date(this.currentDate)
      start.setDate(start.getDate() - start.getDay()) // Start of week (Sunday)
      
      const end = new Date(start)
      end.setDate(end.getDate() + 6) // End of week (Saturday)
      
      return `${this.formatDate(start)} - ${this.formatDate(end)}`
    },
    weekDays() {
      const startDate = new Date(this.currentDate)
      startDate.setDate(startDate.getDate() - startDate.getDay()) // Start on Sunday
      
      const days = []
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      
      for (let i = 0; i < 7; i++) {
        const date = new Date(startDate)
        date.setDate(date.getDate() + i)
        
        const dateString = date.toISOString().split('T')[0]
        const isToday = date.toDateString() === today.toDateString()
        
        days.push({
          date: date.getDate(),
          weekday: date.toLocaleDateString('en-US', { weekday: 'short' }),
          dateString,
          isToday
        })
      }
      
      return days
    },
    filteredEvents() {
      if (this.selectedSport === 'all') {
        return this.allEvents
      }
      return this.allEvents.filter(event => event.sport === this.selectedSport)
    }
  },
  created() {
    this.loadEvents()
    this.loadCrewMembers()
  },
  methods: {
    getEmptyEvent() {return { ...this.newEvent }},
    loadEvents() {
      //API call
      this.allEvents = auth.getEvents()
    },
    saveEvent() {
    const savedEvent = auth.saveEvent(this.newEvent)
    
    if (this.editingEvent) {
      // Update in local array
      const index = this.allEvents.findIndex(e => e.id === this.editingEvent.id)
      if (index !== -1) {
        this.allEvents.splice(index, 1, savedEvent)
      }
    } else {
      this.allEvents.push(savedEvent)
    }
    
    this.cancelEvent()
  },
    loadCrewMembers() {
      
      this.availableCrew = [
        { id: '1', name: 'Alex Johnson' },
        { id: '2', name: 'Sam Wilson' },
        { id: '3', name: 'Taylor Smith' },
        { id: '4', name: 'Jordan Lee' }
      ]
    },
    getFormattedDate(date) {
      return date.toISOString().split('T')[0]
    },
    getEventsForDay(dateString) {
      return this.filteredEvents.filter(event => event.date === dateString)
    },
    calculateEventPosition(startTime) {
      const [hours, minutes] = startTime.split(':').map(Number)
      const totalMinutes = hours * 60 + minutes
      return `${((totalMinutes - 360) / 15) * 25}px` // 6:00 AM is 360 minutes
    },
    calculateEventDuration(startTime, endTime) {
      const [startH, startM] = startTime.split(':').map(Number)
      const [endH, endM] = endTime.split(':').map(Number)
      const duration = (endH * 60 + endM) - (startH * 60 + startM)
      return `${(duration / 15) * 25}px` // Each 15 minutes = 25px
    },
    formatTimeRange(startTime, endTime) {
      return `${this.formatTime(startTime)} - ${this.formatTime(endTime)}`
    },
    formatTime(time) {
      const [hours, minutes] = time.split(':').map(Number)
      const period = hours >= 12 ? 'PM' : 'AM'
      const displayHours = hours % 12 || 12
      return `${displayHours}:${minutes.toString().padStart(2, '0')} ${period}`
    },
    formatDate(date) {
      return date.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric'
      })
    },
    getSportColor(sport) {
      return this.sportColors[sport] || '#607D8B'
    },
    getCrewName(crewId) {
      const crew = this.availableCrew.find(c => c.id === crewId)
      return crew ? crew.name : crewId
    },
    previousWeek() {
      const newDate = new Date(this.currentDate)
      newDate.setDate(newDate.getDate() - 7)
      this.currentDate = newDate
    },
    nextWeek() {
      const newDate = new Date(this.currentDate)
      newDate.setDate(newDate.getDate() + 7)
      this.currentDate = newDate
    },
    selectEvent(event) {
      if (!this.isAdmin) return
      
      this.editingEvent = event
      this.newEvent = { ...event }
      this.showEventModal = true
    },
    // saveEvent() {
    //   if (this.editingEvent) {
    //     // Update existing event
    //     const index = this.allEvents.findIndex(e => e.id === this.editingEvent.id)
    //     if (index !== -1) {
    //       this.allEvents.splice(index, 1, { ...this.newEvent, id: this.editingEvent.id })
    //     }
    //   } else {
    //     // Add new event
    //     this.allEvents.push({
    //       ...this.newEvent,
    //       id: Date.now().toString()
    //     })
    //   }
      
    //   this.cancelEvent()
    // },
    // deleteEvent() {
    //   if (this.editingEvent) {
    //     this.allEvents = this.allEvents.filter(e => e.id !== this.editingEvent.id)
    //     this.cancelEvent()
    //   }
    // },
    deleteEvent() {
    if (this.editingEvent) {
      auth.deleteEvent(this.editingEvent.id)
      this.allEvents = this.allEvents.filter(e => e.id !== this.editingEvent.id)
      this.cancelEvent()
    }
  },
    cancelEvent() {
      this.showEventModal = false
      this.editingEvent = null
      this.newEvent = this.getEmptyEvent()
    },
    filterEvents() {
      // Computed properties handle the filtering
    }
  }
}
</script>

<style scoped>
.schedule-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.calendar-controls {
  margin-bottom: 20px;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.admin-controls {
  display: flex;
  gap: 10px;
}

.add-event-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.navigation-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 15px;
}

.nav-btn {
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  padding: 5px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.sport-filter {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.sport-filter select {
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.calendar-week {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
}

.day-header-row {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  background-color: #f5f5f5;
}

.day-header {
  padding: 10px;
  text-align: center;
  border-right: 1px solid #ddd;
}

.day-header:last-child {
  border-right: none;
}

.weekday {
  font-weight: bold;
  margin-bottom: 5px;
}

.date {
  font-size: 1.2em;
}

.date.today {
  background-color: #2196F3;
  color: white;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.calendar-grid {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  height: 800px;
  overflow-y: auto;
}

.time-column {
  display: flex;
  flex-direction: column;
}

.time-slot {
  height: 100px;
  border-bottom: 1px solid #eee;
  padding: 5px;
  font-size: 0.8em;
  color: #666;
}

.day-column {
  position: relative;
  border-right: 1px solid #eee;
}

.day-column:last-child {
  border-right: none;
}

.day-events {
  position: relative;
  height: 2400px; /* 24 hours * 100px per hour */
}

.calendar-event {
  position: absolute;
  left: 5px;
  right: 5px;
  padding: 8px;
  border-radius: 4px;
  color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
  cursor: pointer;
  overflow: hidden;
}

.calendar-event:hover {
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}

.event-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.event-time {
  font-weight: bold;
  font-size: 0.8em;
  margin-bottom: 5px;
}

.event-title {
  font-weight: bold;
  margin-bottom: 5px;
  flex-grow: 1;
}

.event-sport {
  font-size: 0.8em;
  opacity: 0.9;
}

.event-crew {
  font-size: 0.7em;
  margin-top: 5px;
  display: flex;
  flex-wrap: wrap;
  gap: 3px;
}

/* Modal Styles */
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
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-row .form-group {
  flex: 1;
}

.crew-selection {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.crew-checkbox {
  display: flex;
  align-items: center;
  gap: 5px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.save-btn {
  background-color: #4CAF50;
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

.delete-btn {
  background-color: #f44336;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

@media (max-width: 768px) {
  .calendar-grid {
    grid-template-columns: 60px repeat(7, 1fr);
  }
  
  .day-header-row {
    grid-template-columns: 60px repeat(7, 1fr);
  }
  
  .time-slot {
    font-size: 0.7em;
    padding: 2px;
  }
  
  .crew-selection {
    grid-template-columns: 1fr;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>