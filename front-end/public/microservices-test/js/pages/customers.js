// Customer Service Page Controller

import customerApi from '../modules/customer-api.js';
import { displayResult, displayError, displayTable, getFormData, clearForm, checkHealth, formatDate } from '../utils.js';

// Initialize page
document.addEventListener('DOMContentLoaded', () => {
    // Check service health on load
    checkHealth(customerApi, 'service-status');

    // Health check button
    const healthBtn = document.getElementById('check-health');
    if (healthBtn) {
        healthBtn.addEventListener('click', () => {
            checkHealth(customerApi, 'service-status');
        });
    }

    // Register customer form
    const registerForm = document.getElementById('register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = getFormData(registerForm);

            try {
                const result = await customerApi.register(data);
                if (result.success) {
                    displayResult('register-result', result.data);
                    clearForm(registerForm);
                } else {
                    displayError('register-result', result.error);
                }
            } catch (error) {
                displayError('register-result', error.message);
            }
        });
    }

    // Login form
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = getFormData(loginForm);

            try {
                const result = await customerApi.login(data);
                if (result.success) {
                    displayResult('login-result', result.data);
                    // Save token if returned
                    if (result.data.token) {
                        localStorage.setItem('customer_token', result.data.token);
                    }
                } else {
                    displayError('login-result', result.error);
                }
            } catch (error) {
                displayError('login-result', error.message);
            }
        });
    }

    // Get customer by ID
    const getBtn = document.getElementById('get-customer-btn');
    if (getBtn) {
        getBtn.addEventListener('click', async () => {
            const id = document.getElementById('get-customer-id').value;
            if (!id) {
                displayError('get-customer-result', 'Please enter a customer ID');
                return;
            }

            try {
                const result = await customerApi.getCustomer(id);
                if (result.success) {
                    displayResult('get-customer-result', result.data);
                } else {
                    displayError('get-customer-result', result.error);
                }
            } catch (error) {
                displayError('get-customer-result', error.message);
            }
        });
    }

    // List customers
    const listBtn = document.getElementById('list-customers-btn');
    if (listBtn) {
        listBtn.addEventListener('click', async () => {
            const page = document.getElementById('list-page').value || 0;
            const size = document.getElementById('list-size').value || 20;

            try {
                const result = await customerApi.listCustomers(page, size);
                if (result.success) {
                    displayTable('customers-list', result.data.content || result.data, [
                        { field: 'id', label: 'ID' },
                        { field: 'email', label: 'Email' },
                        { field: 'first_name', label: 'First Name' },
                        { field: 'last_name', label: 'Last Name' },
                        { field: 'phone', label: 'Phone' },
                        { field: 'created_at', label: 'Registered', format: formatDate }
                    ]);
                } else {
                    displayError('customers-list', result.error);
                }
            } catch (error) {
                displayError('customers-list', error.message);
            }
        });
    }

    // Search customers
    const searchBtn = document.getElementById('search-btn');
    if (searchBtn) {
        searchBtn.addEventListener('click', async () => {
            const query = document.getElementById('search-query').value;
            if (!query) {
                displayError('search-result', 'Please enter a search query');
                return;
            }

            try {
                const result = await customerApi.searchCustomers(query);
                if (result.success) {
                    displayTable('search-result', result.data, [
                        { field: 'id', label: 'ID' },
                        { field: 'email', label: 'Email' },
                        { field: 'first_name', label: 'First Name' },
                        { field: 'last_name', label: 'Last Name' },
                        { field: 'phone', label: 'Phone' }
                    ]);
                } else {
                    displayError('search-result', result.error);
                }
            } catch (error) {
                displayError('search-result', error.message);
            }
        });
    }

    // Get addresses
    const getAddressesBtn = document.getElementById('get-addresses-btn');
    if (getAddressesBtn) {
        getAddressesBtn.addEventListener('click', async () => {
            const customerId = document.getElementById('address-customer-id').value;
            if (!customerId) {
                displayError('addresses-result', 'Please enter a customer ID');
                return;
            }

            try {
                const result = await customerApi.getAddresses(customerId);
                if (result.success) {
                    displayTable('addresses-result', result.data, [
                        { field: 'id', label: 'ID' },
                        { field: 'line1', label: 'Address' },
                        { field: 'city', label: 'City' },
                        { field: 'state', label: 'State' },
                        { field: 'zipcode', label: 'Zip' }
                    ]);
                } else {
                    displayError('addresses-result', result.error);
                }
            } catch (error) {
                displayError('addresses-result', error.message);
            }
        });
    }

    // Add address form
    const addAddressForm = document.getElementById('add-address-form');
    if (addAddressForm) {
        addAddressForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = getFormData(addAddressForm);
            const customerId = data.customer_id;
            delete data.customer_id;

            try {
                const result = await customerApi.addAddress(customerId, data);
                if (result.success) {
                    displayResult('add-address-result', result.data);
                    clearForm(addAddressForm);
                } else {
                    displayError('add-address-result', result.error);
                }
            } catch (error) {
                displayError('add-address-result', error.message);
            }
        });
    }
});
