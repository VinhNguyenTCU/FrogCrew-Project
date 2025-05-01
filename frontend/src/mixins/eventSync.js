import { auth } from '../auth/auth'

export default {
  data() {
    return {
      allEvents: []
    }
  },
  created() {
    this.loadEvents()
    window.addEventListener('storage', this.handleStorageChange)
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.handleStorageChange)
  },
  methods: {
    loadEvents() {
      this.allEvents = auth.getEvents()
    },
    handleStorageChange(event) {
      if (event.key === 'events') {
        this.loadEvents()
      }
    }
  }
}