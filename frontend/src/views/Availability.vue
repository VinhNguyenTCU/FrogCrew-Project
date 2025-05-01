<template>
    <div class="availability-container">
      <div class="tabs">
        <button 
          @click="activeTab = 'availability'"
          :class="{ 'active': activeTab === 'availability' }"
        >
          Available Shifts
        </button>
        <button 
          @click="activeTab = 'upcoming'"
          :class="{ 'active': activeTab === 'upcoming' }"
        >
          My Upcoming Shifts
        </button>
      </div>
  
      <div v-if="activeTab === 'availability'" class="availability-table">
        <h2>Available Shifts</h2>
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Event</th>
              <th>Sport</th>
              <th>Time</th>
              <th>Location</th>
              <th>Availability</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="event in availableEvents" :key="event.id">
              <td>{{ formatDate(event.date) }}</td>
              <td>{{ event.title }}</td>
              <td>{{ event.sport }}</td>
              <td>{{ formatTimeRange(event.startTime, event.endTime) }}</td>
              <td>{{ event.location }}</td>
              <td class="availability-actions">
                <button 
                  v-if="!event.responded"
                  @click="respondToEvent(event, true)"
                  class="available-btn"
                  :disabled="processingEvent === event.id"
                >
                <span v-if="processingEvent === event.id">Processing...</span>
                <span v-else>Yes</span>
                </button>
                <button 
                  v-if="!event.responded"
                  @click="respondToEvent(event, false)"
                  class="unavailable-btn"
                >
                  No
                </button>
                <div v-if="event.responded" class="response-message">
                  {{ event.available ? 'Available' : 'Unavailable' }}
                  <button 
                    v-if="!event.available"
                    @click="changeResponse(event)"
                    class="change-btn"
                  >
                    Change
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="availableEvents.length === 0">
              <td colspan="6" class="no-events">No shifts available to respond to</td>
            </tr>
          </tbody>
        </table>
      </div>
  
      <div v-if="activeTab === 'upcoming'" class="upcoming-shifts">
        <h2>My Upcoming Shifts</h2>
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Event</th>
              <th>Sport</th>
              <th>Time</th>
              <th>Location</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="shift in upcomingShifts" :key="shift.id">
              <td>{{ formatDate(shift.date) }}</td>
              <td>{{ shift.title }}</td>
              <td>{{ shift.sport }}</td>
              <td>{{ formatTimeRange(shift.startTime, shift.endTime) }}</td>
              <td>{{ shift.location }}</td>
              <td>
                <span :class="{
                  'pending': !shift.assigned,
                  'confirmed': shift.assigned
                }">
                  {{ shift.assigned ? 'Confirmed' : 'Pending Assignment' }}
                </span>
              </td>
            </tr>
            <tr v-if="upcomingShifts.length === 0">
              <td colspan="6" class="no-events">No upcoming shifts</td>
            </tr>
          </tbody>
        </table>
      </div>
  
      <!-- Comment Modal -->
      <div v-if="showCommentModal" class="modal-overlay">
        <div class="modal-content">
          <h3>Add Comment for {{ currentEvent.title }}</h3>
          <textarea 
            v-model="commentText" 
            placeholder="Optional comment about your availability..."
          ></textarea>
          <div class="modal-actions">
            <button @click="submitResponse" class="submit-btn">Submit</button>
            <button @click="cancelComment" class="cancel-btn">Cancel</button>
          </div>
        </div>
      </div>
  
      <!-- Change Response Confirmation -->
      <div v-if="showChangeModal" class="modal-overlay">
        <div class="modal-content">
          <h3>Change Availability</h3>
          <p>Are you sure you want to change your availability for {{ currentEvent.title }}?</p>
          <div class="modal-actions">
            <button @click="confirmChangeResponse" class="confirm-btn">Yes, Change</button>
            <button @click="cancelChange" class="cancel-btn">Cancel</button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import eventSync from '@/mixins/eventSync'
    import { auth } from '../auth/auth'
  
  export default {
    mixins: [eventSync],
    name: 'AvailabilityView',
    data() {
      return {
        activeTab: 'availability',
        allEvents: [],
        availabilityResponses: JSON.parse(localStorage.getItem('availabilityResponses')) || {},
        commentText: '',
        currentEvent: null,
        showCommentModal: false,
        showChangeModal: false,
        changingToAvailable: false
      }
    },
    computed: {
     availableEvents() {
        return this.allEvents
        .filter(event => {
            // Only future events
            const eventDate = new Date(event.date)
            const today = new Date()
            today.setHours(0, 0, 0, 0)
            if (eventDate < today) return false

            // Check if user already responded
            const response = this.availabilityResponses[event.id]
            if (response?.hasResponded) {
            // Only show if they said "No" (can change response)
            return !response.available
            }
            return true
        })
        .sort((a, b) => new Date(a.date) - new Date(b.date))
  },
      upcomingShifts() {
        const userId = auth.user?.id
        if (!userId) return []
        
        return this.allEvents
          .filter(event => {
            // Events where user is either assigned or marked available
            const isAssigned = event.assignedCrew?.includes(userId) || false
            const isAvailable = this.availabilityResponses[event.id]?.available || false
            return isAssigned || isAvailable
          })
          .map(event => ({
            ...event,
            assigned: event.assignedCrew?.includes(userId) || false
          }))
          .sort((a, b) => new Date(a.date) - new Date(b.date))
      }
    },
    created() {
      this.loadEvents()
    },
    methods: {
        async respondToEvent(event, isAvailable) {
            this.processingEvent = event.id
            try {
            this.currentEvent = { ...event }
            if (isAvailable) {
                this.showCommentModal = true
            } else {
                await this.saveResponse(event, false, '')
            }
            } finally {
            this.processingEvent = null
            }
        },
     loadEvents() {
        this.allEvents = auth.getEvents()
        .map(event => ({
        ...event,
        date: event.date.split('T')[0] // Ensure consistent date format
      }))
    },
      getFormattedDate(date) {
        return date.toISOString().split('T')[0]
      },
      formatDate(dateStr) {
        const date = new Date(dateStr)
        return date.toLocaleDateString('en-US', {
          month: 'short',
          day: 'numeric',
          year: 'numeric'
        })
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
      respondToEvent(event, isAvailable) {
        this.currentEvent = { ...event }
        if (isAvailable) {
        this.showCommentModal = true
        } else {
        // Immediately save "No" response
        this.saveResponse(event, false, '')
        }
     },
     async saveResponse(event, isAvailable, comment) {
        try {
        const userId = auth.user?.id
        if (!userId) throw new Error('User not authenticated')

        // 1. Update availability responses
        const updatedResponses = {
            ...this.availabilityResponses,
            [event.id]: {
            userId,
            hasResponded: true,
            available: isAvailable,
            comment: comment || '',
            respondedAt: new Date().toISOString()
            }
        }

        // 2. Update localStorage
        localStorage.setItem('availabilityResponses', JSON.stringify(updatedResponses))

        // 3. Update local state
        this.availabilityResponses = updatedResponses

        // 4. If available, add to admin notifications
        if (isAvailable) {
            const notifications = JSON.parse(localStorage.getItem('availabilityNotifications')) || []
            notifications.push({
            eventId: event.id,
            userId,
            userName: auth.user.name,
            comment,
            timestamp: new Date().toISOString()
            })
            localStorage.setItem('availabilityNotifications', JSON.stringify(notifications))
        }

        // 5. Reset UI
        this.showCommentModal = false
        this.commentText = ''
        this.currentEvent = null

        // 6. Force update the events list
        this.allEvents = [...this.allEvents]

        } catch (error) {
        console.error('Failed to save response:', error)
        alert('Failed to save your availability. Please try again.')
            }
    },
      changeResponse(event) {
        this.currentEvent = event
        this.showChangeModal = true
      },
      confirmChangeResponse() {
        this.showChangeModal = false
        this.showCommentModal = true
        this.changingToAvailable = true
      },
      submitResponse() {
        if (this.currentEvent) {
          this.saveResponse(this.currentEvent, true, this.commentText)
        }
      },
      cancelComment() {
        this.showCommentModal = false
        this.commentText = ''
        this.currentEvent = null
      },
      cancelChange() {
        this.showChangeModal = false
        this.currentEvent = null
      }
    }
  }
  </script>
  
  <style scoped>
  .availability-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .tabs {
    display: flex;
    margin-bottom: 20px;
    border-bottom: 1px solid #ddd;
  }
  
  .tabs button {
    padding: 10px 20px;
    background: none;
    border: none;
    cursor: pointer;
    font-size: 16px;
    border-bottom: 3px solid transparent;
  }
  
  .tabs button.active {
    border-bottom: 3px solid #3498db;
    font-weight: bold;
  }
  
  table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 15px;
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
  
  .availability-actions {
    display: flex;
    gap: 10px;
  }
  
  .available-btn {
    background-color: #4CAF50;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .unavailable-btn {
    background-color: #f44336;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .change-btn {
    background-color: #3498db;
    color: white;
    border: none;
    padding: 3px 6px;
    border-radius: 4px;
    cursor: pointer;
    margin-left: 10px;
    font-size: 12px;
  }
  
  .response-message {
    display: flex;
    align-items: center;
  }
  
  .pending {
    color: #FF9800;
    font-weight: bold;
  }
  
  .confirmed {
    color: #4CAF50;
    font-weight: bold;
  }
  
  .no-events {
    text-align: center;
    padding: 20px;
    color: #666;
    font-style: italic;
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
    max-width: 500px;
  }
  
  .modal-content h3 {
    margin-top: 0;
  }
  
  .modal-content textarea {
    width: 100%;
    min-height: 100px;
    padding: 10px;
    margin: 10px 0;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  
  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
  
  .submit-btn, .confirm-btn {
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
  </style>