// Restaurant Service Page Controller

import restaurantApi from '../modules/restaurant-api.js';
import { displayResult, displayError, displayTable, getFormData, clearForm, checkHealth } from '../utils.js';

// Initialize page
document.addEventListener('DOMContentLoaded', () => {
    // Check service health on load
    checkHealth(restaurantApi, 'service-status');

    // Health check button
    const healthBtn = document.getElementById('check-health');
    if (healthBtn) {
        healthBtn.addEventListener('click', () => {
            checkHealth(restaurantApi, 'service-status');
        });
    }

    // Create restaurant form
    const createForm = document.getElementById('create-restaurant-form');
    if (createForm) {
        createForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = getFormData(createForm);

            try {
                const result = await restaurantApi.createRestaurant(data);
                if (result.success) {
                    displayResult('create-result', result.data);
                    clearForm(createForm);
                } else {
                    displayError('create-result', result.error);
                }
            } catch (error) {
                displayError('create-result', error.message);
            }
        });
    }

    // List restaurants
    const listBtn = document.getElementById('list-restaurants');
    if (listBtn) {
        listBtn.addEventListener('click', async () => {
            try {
                const result = await restaurantApi.listRestaurants();
                if (result.success) {
                    displayTable('restaurants-list', result.data.content || result.data, [
                        { field: 'id', label: 'ID' },
                        { field: 'name', label: 'Name' },
                        { field: 'address', label: 'Address' },
                        { field: 'phone', label: 'Phone' },
                        { field: 'cuisine_type', label: 'Cuisine' }
                    ]);
                } else {
                    displayError('restaurants-list', result.error);
                }
            } catch (error) {
                displayError('restaurants-list', error.message);
            }
        });
    }

    // Get restaurant by ID
    const getBtn = document.getElementById('get-restaurant-btn');
    if (getBtn) {
        getBtn.addEventListener('click', async () => {
            const id = document.getElementById('get-restaurant-id').value;
            if (!id) {
                displayError('get-restaurant-result', 'Please enter a restaurant ID');
                return;
            }

            try {
                const result = await restaurantApi.getRestaurant(id);
                if (result.success) {
                    displayResult('get-restaurant-result', result.data);
                } else {
                    displayError('get-restaurant-result', result.error);
                }
            } catch (error) {
                displayError('get-restaurant-result', error.message);
            }
        });
    }

    // Get menu
    const getMenuBtn = document.getElementById('get-menu-btn');
    if (getMenuBtn) {
        getMenuBtn.addEventListener('click', async () => {
            const id = document.getElementById('menu-restaurant-id').value;
            if (!id) {
                displayError('menu-result', 'Please enter a restaurant ID');
                return;
            }

            try {
                const result = await restaurantApi.getMenu(id);
                if (result.success) {
                    displayTable('menu-result', result.data, [
                        { field: 'id', label: 'ID' },
                        { field: 'name', label: 'Name' },
                        { field: 'description', label: 'Description' },
                        { field: 'price', label: 'Price', format: (val) => `$${val}` },
                        { field: 'category', label: 'Category' }
                    ]);
                } else {
                    displayError('menu-result', result.error);
                }
            } catch (error) {
                displayError('menu-result', error.message);
            }
        });
    }

    // Create menu item
    const createMenuForm = document.getElementById('create-menu-item-form');
    if (createMenuForm) {
        createMenuForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = getFormData(createMenuForm);
            const restaurantId = data.restaurant_id;
            delete data.restaurant_id;

            try {
                const result = await restaurantApi.createMenuItem(restaurantId, data);
                if (result.success) {
                    displayResult('create-menu-item-result', result.data);
                    clearForm(createMenuForm);
                } else {
                    displayError('create-menu-item-result', result.error);
                }
            } catch (error) {
                displayError('create-menu-item-result', error.message);
            }
        });
    }
});
