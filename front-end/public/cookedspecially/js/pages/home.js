// Homepage Controller

import cart from '../cart.js';

// Initialize cart display
cart.updateCartUI();

// Check if user is logged in
const token = localStorage.getItem('cookedspecially_token');
const user = localStorage.getItem('cookedspecially_user');

if (token && user) {
    const userMenu = document.getElementById('user-menu');
    const userData = JSON.parse(user);
    userMenu.textContent = userData.first_name || 'My Account';
    userMenu.href = 'account.html';
}
