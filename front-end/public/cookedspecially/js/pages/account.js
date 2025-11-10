// Account Page Controller

import orderApi from '../modules/order-api.js';
import cart from '../cart.js';

// Initialize page
document.addEventListener('DOMContentLoaded', async () => {
    // Update cart UI
    cart.updateCartUI();

    // Check authentication
    const token = localStorage.getItem('cookedspecially_token');
    const user = localStorage.getItem('cookedspecially_user');

    if (!token || !user) {
        window.location.href = 'login.html';
        return;
    }

    const userData = JSON.parse(user);

    // Display user info
    document.getElementById('user-name').textContent = `${userData.first_name} ${userData.last_name}`;
    document.getElementById('user-email').textContent = userData.email;
    document.getElementById('user-phone').textContent = userData.phone || 'Not provided';

    // Load orders
    await loadOrders(userData.id);

    // Setup logout
    document.getElementById('logout-btn').addEventListener('click', () => {
        localStorage.removeItem('cookedspecially_token');
        localStorage.removeItem('cookedspecially_user');
        window.location.href = 'index.html';
    });
});

// Load orders
async function loadOrders(customerId) {
    const loading = document.getElementById('loading-orders');
    const ordersList = document.getElementById('orders-list');
    const noOrders = document.getElementById('no-orders');

    try {
        const result = await orderApi.getOrdersByCustomer(customerId);

        if (result.success && result.data && result.data.length > 0) {
            renderOrders(result.data);
            ordersList.classList.remove('hidden');
        } else {
            noOrders.classList.remove('hidden');
        }
    } catch (error) {
        console.error('Error loading orders:', error);
        noOrders.classList.remove('hidden');
    } finally {
        loading.classList.add('hidden');
    }
}

// Render orders
function renderOrders(orders) {
    const container = document.getElementById('orders-list');

    // Sort by date (newest first)
    const sortedOrders = orders.sort((a, b) =>
        new Date(b.created_at) - new Date(a.created_at)
    );

    container.innerHTML = sortedOrders.map(order => `
        <div class="card mb-3">
            <div class="card-content">
                <div class="grid grid-2" style="gap: 2rem;">
                    <div>
                        <h3>Order #${order.id}</h3>
                        <p class="text-muted">${formatDate(order.created_at)}</p>
                        <p><strong>Status:</strong> <span class="badge-${getStatusColor(order.status)}">${order.status}</span></p>
                    </div>
                    <div class="text-right">
                        <div class="card-price">₹${order.total_amount || order.subtotal || '0.00'}</div>
                        <p class="text-muted">${order.items ? order.items.length : 0} items</p>
                    </div>
                </div>

                ${order.delivery_address ? `
                    <div class="mt-3">
                        <strong>Delivery Address:</strong><br>
                        ${order.delivery_address}
                    </div>
                ` : ''}
            </div>
        </div>
    `).join('');
}

// Format date
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Get status color
function getStatusColor(status) {
    const colors = {
        'PENDING': 'warning',
        'CONFIRMED': 'info',
        'PREPARING': 'info',
        'READY': 'success',
        'OUT_FOR_DELIVERY': 'info',
        'DELIVERED': 'success',
        'CANCELLED': 'error'
    };
    return colors[status] || 'info';
}
