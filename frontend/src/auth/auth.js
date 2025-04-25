export const auth = {
  isAuthenticated: false,
  user: null,


  // Hardcoded admin account
  adminAccount: {
    firstName: 'Bob',
    lastName: 'Heart',
    fullName: 'Bob Heart',
    email: 'Heart@gmail.com',
    password: 'qwerty123', // In production, never store passwords like this
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
  }
};