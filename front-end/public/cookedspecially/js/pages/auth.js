// Authentication Page Controller

import customerApi from '../modules/customer-api.js';
import cart from '../cart.js';

// Initialize page
document.addEventListener('DOMContentLoaded', () => {
    // Update cart UI
    cart.updateCartUI();

    // Setup tabs
    setupTabs();

    // Setup forms
    setupSignInForm();
    setupSignUpForm();

    // Redirect if already logged in
    if (localStorage.getItem('cookedspecially_token')) {
        const returnUrl = new URLSearchParams(window.location.search).get('return') || 'account.html';
        window.location.href = returnUrl;
    }
});

// Setup tabs
function setupTabs() {
    const tabs = document.querySelectorAll('.auth-tab');
    const forms = document.querySelectorAll('.auth-form');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const targetTab = tab.dataset.tab;

            // Update active tab
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            // Update active form
            forms.forEach(f => f.classList.remove('active'));
            document.getElementById(`${targetTab}-form`).classList.add('active');

            // Clear message
            clearMessage();
        });
    });
}

// Setup sign in form
function setupSignInForm() {
    const form = document.getElementById('signin-form');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = document.getElementById('signin-email').value;
        const password = document.getElementById('signin-password').value;

        // Show loading
        const btn = form.querySelector('button[type="submit"]');
        const originalText = btn.textContent;
        btn.disabled = true;
        btn.textContent = 'Signing in...';

        try {
            const result = await customerApi.login({ email, password });

            if (result.success) {
                // Save token and user data
                if (result.data.token) {
                    localStorage.setItem('cookedspecially_token', result.data.token);
                }

                if (result.data.customer) {
                    localStorage.setItem('cookedspecially_user', JSON.stringify(result.data.customer));
                }

                showMessage('Sign in successful! Redirecting...', 'success');

                // Redirect
                setTimeout(() => {
                    const returnUrl = new URLSearchParams(window.location.search).get('return') || 'account.html';
                    window.location.href = returnUrl;
                }, 1000);
            } else {
                showMessage(result.error || 'Invalid email or password', 'error');
                btn.disabled = false;
                btn.textContent = originalText;
            }
        } catch (error) {
            showMessage('Sign in failed. Please try again.', 'error');
            btn.disabled = false;
            btn.textContent = originalText;
        }
    });
}

// Setup sign up form
function setupSignUpForm() {
    const form = document.getElementById('signup-form');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = {
            first_name: document.getElementById('signup-firstname').value,
            last_name: document.getElementById('signup-lastname').value,
            email: document.getElementById('signup-email').value,
            phone: document.getElementById('signup-phone').value,
            password: document.getElementById('signup-password').value
        };

        // Show loading
        const btn = form.querySelector('button[type="submit"]');
        const originalText = btn.textContent;
        btn.disabled = true;
        btn.textContent = 'Creating account...';

        try {
            const result = await customerApi.register(formData);

            if (result.success) {
                showMessage('Account created successfully!', 'success');

                // Auto sign in
                setTimeout(async () => {
                    const loginResult = await customerApi.login({
                        email: formData.email,
                        password: formData.password
                    });

                    if (loginResult.success) {
                        if (loginResult.data.token) {
                            localStorage.setItem('cookedspecially_token', loginResult.data.token);
                        }
                        if (loginResult.data.customer) {
                            localStorage.setItem('cookedspecially_user', JSON.stringify(loginResult.data.customer));
                        }

                        const returnUrl = new URLSearchParams(window.location.search).get('return') || 'account.html';
                        window.location.href = returnUrl;
                    }
                }, 1000);
            } else {
                showMessage(result.error || 'Registration failed. Please try again.', 'error');
                btn.disabled = false;
                btn.textContent = originalText;
            }
        } catch (error) {
            showMessage('Registration failed. Please try again.', 'error');
            btn.disabled = false;
            btn.textContent = originalText;
        }
    });
}

// Show message
function showMessage(message, type) {
    const messageEl = document.getElementById('auth-message');
    messageEl.className = `alert alert-${type}`;
    messageEl.textContent = message;
    messageEl.style.display = 'block';
}

// Clear message
function clearMessage() {
    const messageEl = document.getElementById('auth-message');
    messageEl.style.display = 'none';
}
