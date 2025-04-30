export const auth = {
  isAuthenticated: false,
  user: null,


  // Hardcoded admin account
  adminAccount: {
    firstName: 'Bob',
    lastName: 'Heart',
    fullName: 'Bob Heart',
    email: 'Heart@gmail.com',
    password: 'qwerty123', 
    phone: '',
    roles: ['admin', 'crew_member'],
    photoUrl: '',
    joinDate: new Date('2023-01-01').toISOString()
  },


  // Temporary storage for account requests
  pendingAccounts: JSON.parse(localStorage.getItem('pendingAccounts')) || [],
  approvedAccounts: JSON.parse(localStorage.getItem('approvedAccounts')) || [],

  requestAccount(userData, callback) {
    this.pendingAccounts.push({
      ...userData,
      status: 'pending',
      roles: ['crew_member'],
      createdAt: new Date().toISOString()
    });
    localStorage.setItem('pendingAccounts', JSON.stringify(this.pendingAccounts));
    callback(true);
  },

  approveAccount(email) {
    const account = this.pendingAccounts.find(a => a.email === email);
    if (account) {
      this.approvedAccounts.push({
        ...account,
        status: 'approved'
      });
      this.pendingAccounts = this.pendingAccounts.filter(a => a.email !== email);
      localStorage.setItem('pendingAccounts', JSON.stringify(this.pendingAccounts));
      localStorage.setItem('approvedAccounts', JSON.stringify(this.approvedAccounts));
    }
  },

  login(email, password, callback) {
    // Check if admin login
    if (email === this.adminAccount.email && password === this.adminAccount.password) {
      this.isAuthenticated = true;
      this.user = { ...this.adminAccount };
      localStorage.setItem('isAuthenticated', 'true');
      localStorage.setItem('user', JSON.stringify(this.user));
      callback(true);
      return;
    }

    // Regular user login
    const account = this.approvedAccounts.find(a => a.email === email);
    if (account) {
      this.isAuthenticated = true;
      this.user = account;
      localStorage.setItem('isAuthenticated', 'true');
      localStorage.setItem('user', JSON.stringify(account));
      callback(true);
    } else {
      callback(false);
    }
  },

  isAdmin() {
    return this.isAuthenticated && this.user?.roles?.includes('admin');
  },

  logout(callback) {
    this.isAuthenticated = false;
    this.user = null;
    localStorage.removeItem('isAuthenticated');
    localStorage.removeItem('user');
    callback();
  },

  checkAuth() {
    const isAuth = localStorage.getItem('isAuthenticated') === 'true';
    this.isAuthenticated = isAuth;
    this.user = JSON.parse(localStorage.getItem('user'));
    return isAuth;
  },

  // Event management methods
  getEvents() {
    return JSON.parse(localStorage.getItem('events')) || []
  },
  
  saveEvent(event) {
    const events = this.getEvents()
    const existingIndex = events.findIndex(e => e.id === event.id)
    
    if (existingIndex >= 0) {
      events[existingIndex] = event // Update existing
    } else {
      event.id = Date.now().toString() // Generate ID for new events
      events.push(event) // Add new
    }
    
    localStorage.setItem('events', JSON.stringify(events))
    return event
  },
  
  deleteEvent(eventId) {
    const events = this.getEvents().filter(e => e.id !== eventId)
    localStorage.setItem('events', JSON.stringify(events))
  }
};