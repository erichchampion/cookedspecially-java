// Admin Service Page Controller

import adminApi from '../modules/admin-api.js';
import { displayResult, displayError, displayTable, getFormData, clearForm, checkHealth, formatDate } from '../utils.js';

// Initialize page
document.addEventListener('DOMContentLoaded', () => {
    // Check service health on load
    checkHealth(adminApi, 'service-status');

    // Health check button
    const healthBtn = document.getElementById('check-health');
    if (healthBtn) {
        healthBtn.addEventListener('click', () => {
            checkHealth(adminApi, 'service-status');
        });
    }

    // Login form
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = getFormData(loginForm);

            try {
                const result = await adminApi.login(data);
                if (result.success) {
                    displayResult('login-result', result.data);
                    // Save token if returned
                    if (result.data.token) {
                        localStorage.setItem('admin_token', result.data.token);
                    }
                } else {
                    displayError('login-result', result.error);
                }
            } catch (error) {
                displayError('login-result', error.message);
            }
        });
    }

    // Create user form
    const createUserForm = document.getElementById('create-user-form');
    if (createUserForm) {
        createUserForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = getFormData(createUserForm);

            try {
                const result = await adminApi.createUser(data);
                if (result.success) {
                    displayResult('create-user-result', result.data);
                    clearForm(createUserForm);
                } else {
                    displayError('create-user-result', result.error);
                }
            } catch (error) {
                displayError('create-user-result', error.message);
            }
        });
    }

    // List users
    const listUsersBtn = document.getElementById('list-users-btn');
    if (listUsersBtn) {
        listUsersBtn.addEventListener('click', async () => {
            try {
                const result = await adminApi.listUsers();
                if (result.success) {
                    displayTable('users-list', result.data, [
                        { field: 'id', label: 'ID' },
                        { field: 'username', label: 'Username' },
                        { field: 'email', label: 'Email' },
                        { field: 'first_name', label: 'First Name' },
                        { field: 'last_name', label: 'Last Name' },
                        { field: 'is_active', label: 'Active' },
                        { field: 'created_at', label: 'Created', format: formatDate }
                    ]);
                } else {
                    displayError('users-list', result.error);
                }
            } catch (error) {
                displayError('users-list', error.message);
            }
        });
    }

    // Get user by ID
    const getUserBtn = document.getElementById('get-user-btn');
    if (getUserBtn) {
        getUserBtn.addEventListener('click', async () => {
            const id = document.getElementById('get-user-id').value;
            if (!id) {
                displayError('get-user-result', 'Please enter a user ID');
                return;
            }

            try {
                const result = await adminApi.getUser(id);
                if (result.success) {
                    displayResult('get-user-result', result.data);
                } else {
                    displayError('get-user-result', result.error);
                }
            } catch (error) {
                displayError('get-user-result', error.message);
            }
        });
    }

    // Generate OTP
    const generateOtpBtn = document.getElementById('generate-otp-btn');
    if (generateOtpBtn) {
        generateOtpBtn.addEventListener('click', async () => {
            const userId = document.getElementById('otp-user-id').value;
            const type = document.getElementById('otp-type').value;

            if (!userId) {
                displayError('otp-result', 'Please enter a user ID');
                return;
            }

            try {
                const result = await adminApi.generateOtp(userId, type);
                if (result.success) {
                    displayResult('otp-result', result.data);
                } else {
                    displayError('otp-result', result.error);
                }
            } catch (error) {
                displayError('otp-result', error.message);
            }
        });
    }

    // Validate OTP
    const validateOtpBtn = document.getElementById('validate-otp-btn');
    if (validateOtpBtn) {
        validateOtpBtn.addEventListener('click', async () => {
            const userId = document.getElementById('validate-user-id').value;
            const code = document.getElementById('validate-code').value;

            if (!userId || !code) {
                displayError('validate-result', 'Please enter both user ID and OTP code');
                return;
            }

            try {
                const result = await adminApi.validateOtp(userId, code);
                if (result.success) {
                    displayResult('validate-result', result.data);
                } else {
                    displayError('validate-result', result.error);
                }
            } catch (error) {
                displayError('validate-result', error.message);
            }
        });
    }

    // List roles
    const listRolesBtn = document.getElementById('list-roles-btn');
    if (listRolesBtn) {
        listRolesBtn.addEventListener('click', async () => {
            try {
                const result = await adminApi.listRoles();
                if (result.success) {
                    displayTable('roles-list', result.data, [
                        { field: 'id', label: 'ID' },
                        { field: 'name', label: 'Name' },
                        { field: 'description', label: 'Description' },
                        { field: 'is_system_role', label: 'System Role' }
                    ]);
                } else {
                    displayError('roles-list', result.error);
                }
            } catch (error) {
                displayError('roles-list', error.message);
            }
        });
    }

    // Create employee form
    const createEmployeeForm = document.getElementById('create-employee-form');
    if (createEmployeeForm) {
        createEmployeeForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = getFormData(createEmployeeForm);

            try {
                const result = await adminApi.createEmployee(data);
                if (result.success) {
                    displayResult('create-employee-result', result.data);
                    clearForm(createEmployeeForm);
                } else {
                    displayError('create-employee-result', result.error);
                }
            } catch (error) {
                displayError('create-employee-result', error.message);
            }
        });
    }

    // Get employees by restaurant
    const getEmployeesBtn = document.getElementById('get-employees-btn');
    if (getEmployeesBtn) {
        getEmployeesBtn.addEventListener('click', async () => {
            const restaurantId = document.getElementById('restaurant-employees-id').value;
            if (!restaurantId) {
                displayError('employees-result', 'Please enter a restaurant ID');
                return;
            }

            try {
                const result = await adminApi.getEmployeesByRestaurant(restaurantId);
                if (result.success) {
                    displayTable('employees-result', result.data, [
                        { field: 'id', label: 'ID' },
                        { field: 'user_id', label: 'User ID' },
                        { field: 'position', label: 'Position' },
                        { field: 'employment_type', label: 'Type' },
                        { field: 'status', label: 'Status' },
                        { field: 'hire_date', label: 'Hire Date', format: formatDate }
                    ]);
                } else {
                    displayError('employees-result', result.error);
                }
            } catch (error) {
                displayError('employees-result', error.message);
            }
        });
    }
});
