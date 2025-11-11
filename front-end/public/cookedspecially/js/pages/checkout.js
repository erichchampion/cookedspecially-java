// Checkout Page Controller

import customerApi from '../modules/customer-api.js';
import orderApi from '../modules/order-api.js';
import cart from '../cart.js';
import config from '../../config/config.js';

// State
let selectedAddressId = null;
let customerId = null;

// Initialize page
document.addEventListener('DOMContentLoaded', async () => {
    // Check authentication
    const token = localStorage.getItem('cookedspecially_token');
    const user = localStorage.getItem('cookedspecially_user');

    if (!token || !user) {
        window.location.href = 'login.html?return=checkout.html';
        return;
    }

    const userData = JSON.parse(user);
    customerId = userData.id;

    // Check if cart is empty
    if (cart.isEmpty()) {
        window.location.href = 'cart.html';
        return;
    }

    // Load addresses
    await loadAddresses();

    // Load order summary
    loadOrderSummary();

    // Setup event listeners
    setupEventListeners();
});

// Load saved addresses
async function loadAddresses() {
    try {
        const result = await customerApi.getAddresses(customerId);

        if (result.success && result.data && result.data.length > 0) {
            renderAddresses(result.data);
            // Select first address by default
            selectedAddressId = result.data[0].id;
            document.querySelector('.address-card').classList.add('selected');
        }
    } catch (error) {
        console.error('Error loading addresses:', error);
    }
}

// Render addresses
function renderAddresses(addresses) {
    const container = document.getElementById('saved-addresses');

    container.innerHTML = addresses.map(addr => `
        <div class="address-card" data-address-id="${addr.id}">
            <strong>${addr.line1}</strong><br>
            ${addr.line2 ? addr.line2 + '<br>' : ''}
            ${addr.city}, ${addr.state} ${addr.zipcode}
        </div>
    `).join('');

    // Add click handlers
    container.querySelectorAll('.address-card').forEach(card => {
        card.addEventListener('click', () => {
            container.querySelectorAll('.address-card').forEach(c => c.classList.remove('selected'));
            card.classList.add('selected');
            selectedAddressId = parseInt(card.dataset.addressId);
        });
    });
}

// Load order summary
function loadOrderSummary() {
    const items = cart.getItems();
    const container = document.getElementById('order-items');

    container.innerHTML = items.map(item => `
        <div class="order-item">
            <span>${item.name} x${item.quantity}</span>
            <span>₹${(item.price * item.quantity).toFixed(2)}</span>
        </div>
    `).join('');

    // Update totals
    document.getElementById('checkout-subtotal').textContent = `₹${cart.getSubtotal().toFixed(2)}`;
    document.getElementById('checkout-delivery').textContent = `₹${cart.getDeliveryFee().toFixed(2)}`;
    document.getElementById('checkout-total').textContent = `₹${cart.getTotal().toFixed(2)}`;
}

// Setup event listeners
function setupEventListeners() {
    // Add new address
    document.getElementById('add-address-btn').addEventListener('click', () => {
        document.getElementById('new-address-form').classList.remove('hidden');
    });

    // Cancel new address
    document.getElementById('cancel-address-btn').addEventListener('click', () => {
        document.getElementById('new-address-form').classList.add('hidden');
        clearAddressForm();
    });

    // Save new address
    document.getElementById('save-address-btn').addEventListener('click', async () => {
        await saveNewAddress();
    });

    // Place order
    document.getElementById('place-order-btn').addEventListener('click', async () => {
        await placeOrder();
    });
}

// Save new address
async function saveNewAddress() {
    const addressData = {
        line1: document.getElementById('address-line1').value,
        line2: document.getElementById('address-line2').value,
        city: document.getElementById('address-city').value,
        state: document.getElementById('address-state').value,
        zipcode: document.getElementById('address-zipcode').value
    };

    if (!addressData.line1 || !addressData.city || !addressData.state || !addressData.zipcode) {
        showMessage('Please fill all required fields', 'error');
        return;
    }

    const btn = document.getElementById('save-address-btn');
    btn.disabled = true;
    btn.textContent = 'Saving...';

    try {
        const result = await customerApi.addAddress(customerId, addressData);

        if (result.success) {
            showMessage('Address saved successfully', 'success');
            document.getElementById('new-address-form').classList.add('hidden');
            clearAddressForm();
            await loadAddresses();
        } else {
            showMessage('Failed to save address', 'error');
        }
    } catch (error) {
        showMessage('Error saving address', 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Save Address';
    }
}

// Clear address form
function clearAddressForm() {
    document.getElementById('address-line1').value = '';
    document.getElementById('address-line2').value = '';
    document.getElementById('address-city').value = '';
    document.getElementById('address-state').value = '';
    document.getElementById('address-zipcode').value = '';
}

// Place order
async function placeOrder() {
    if (!selectedAddressId) {
        showMessage('Please select a delivery address', 'error');
        return;
    }

    const btn = document.getElementById('place-order-btn');
    btn.disabled = true;
    btn.textContent = 'Placing order...';

    try {
        // Get delivery address text
        const selectedCard = document.querySelector('.address-card.selected');
        const deliveryAddress = selectedCard ? selectedCard.textContent.trim() : '';

        const notes = document.getElementById('delivery-notes').value;

        // Create order data
        const orderData = cart.getOrderData(customerId, deliveryAddress, notes);

        const result = await orderApi.createOrder(orderData);

        if (result.success) {
            // Clear cart
            cart.clear();

            // Show success message
            showMessage('Order placed successfully!', 'success');

            // Redirect to order confirmation
            setTimeout(() => {
                window.location.href = `order-confirmation.html?orderId=${result.data.id}`;
            }, 2000);
        } else {
            showMessage(result.error || 'Failed to place order', 'error');
            btn.disabled = false;
            btn.textContent = 'Place Order';
        }
    } catch (error) {
        console.error('Error placing order:', error);
        showMessage('Error placing order. Please try again.', 'error');
        btn.disabled = false;
        btn.textContent = 'Place Order';
    }
}

// Show message
function showMessage(message, type) {
    const messageEl = document.getElementById('checkout-message');
    messageEl.className = `alert alert-${type}`;
    messageEl.textContent = message;
    messageEl.style.display = 'block';

    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });
}
