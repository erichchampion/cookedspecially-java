// Cart Page Controller

import cart from '../cart.js';
import config from '../../config/config.js';

// Initialize page
document.addEventListener('DOMContentLoaded', () => {
    renderCart();

    // Listen for cart updates
    window.addEventListener('cartUpdated', renderCart);

    // Check authentication
    checkAuth();
});

// Render cart
function renderCart() {
    const cartItems = cart.getItems();
    const emptyCart = document.getElementById('empty-cart');
    const cartContent = document.getElementById('cart-content');

    if (cartItems.length === 0) {
        emptyCart.classList.remove('hidden');
        cartContent.classList.add('hidden');
        return;
    }

    emptyCart.classList.add('hidden');
    cartContent.classList.remove('hidden');

    // Render items
    renderCartItems(cartItems);

    // Update summary
    updateSummary();
}

// Render cart items
function renderCartItems(items) {
    const container = document.getElementById('cart-items');

    container.innerHTML = items.map(item => `
        <div class="cart-item" data-item-id="${item.id}">
            <img src="${item.imageUrl || 'images/placeholder.jpg'}"
                 alt="${item.name}"
                 class="cart-item-image"
                 onerror="this.src='images/placeholder.jpg'">

            <div class="cart-item-details">
                <div class="cart-item-name">${item.name}</div>
                <div class="cart-item-price">₹${item.price} each</div>
                ${item.specialInstructions ? `<div class="text-muted" style="font-size: 0.9rem;">Note: ${item.specialInstructions}</div>` : ''}
            </div>

            <div class="cart-item-actions">
                <div class="quantity-controls">
                    <button class="quantity-btn decrease-btn" data-item-id="${item.id}">−</button>
                    <span class="quantity-value">${item.quantity}</span>
                    <button class="quantity-btn increase-btn" data-item-id="${item.id}">+</button>
                </div>
                <div style="font-weight: 600; margin-top: 1rem;">₹${(item.price * item.quantity).toFixed(2)}</div>
                <button class="remove-btn" data-item-id="${item.id}">Remove</button>
            </div>
        </div>
    `).join('');

    // Add event listeners
    container.querySelectorAll('.increase-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const itemId = parseInt(btn.dataset.itemId);
            const item = items.find(i => i.id === itemId);
            if (item) {
                cart.updateQuantity(itemId, item.quantity + 1);
            }
        });
    });

    container.querySelectorAll('.decrease-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const itemId = parseInt(btn.dataset.itemId);
            const item = items.find(i => i.id === itemId);
            if (item && item.quantity > 1) {
                cart.updateQuantity(itemId, item.quantity - 1);
            }
        });
    });

    container.querySelectorAll('.remove-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const itemId = parseInt(btn.dataset.itemId);
            if (confirm('Remove this item from cart?')) {
                cart.removeItem(itemId);
            }
        });
    });
}

// Update order summary
function updateSummary() {
    const subtotal = cart.getSubtotal();
    const deliveryFee = cart.getDeliveryFee();
    const total = cart.getTotal();

    document.getElementById('subtotal').textContent = `₹${subtotal.toFixed(2)}`;
    document.getElementById('delivery-fee').textContent = `₹${deliveryFee.toFixed(2)}`;
    document.getElementById('total').textContent = `₹${total.toFixed(2)}`;

    // Show minimum order notice
    const minOrderNotice = document.getElementById('min-order-notice');
    const minAmount = document.getElementById('min-amount');

    if (subtotal < config.app.minOrderAmount) {
        minOrderNotice.classList.remove('hidden');
        minAmount.textContent = config.app.minOrderAmount;

        // Disable checkout button
        const checkoutBtn = document.querySelector('a[href="checkout.html"]');
        if (checkoutBtn) {
            checkoutBtn.classList.add('btn-outline');
            checkoutBtn.classList.remove('btn-primary');
            checkoutBtn.style.pointerEvents = 'none';
            checkoutBtn.style.opacity = '0.6';
        }
    } else {
        minOrderNotice.classList.add('hidden');

        // Enable checkout button
        const checkoutBtn = document.querySelector('a[href="checkout.html"]');
        if (checkoutBtn) {
            checkoutBtn.classList.remove('btn-outline');
            checkoutBtn.classList.add('btn-primary');
            checkoutBtn.style.pointerEvents = 'auto';
            checkoutBtn.style.opacity = '1';
        }
    }

    // Update cart count in header
    cart.updateCartUI();
}

// Check authentication
function checkAuth() {
    const token = localStorage.getItem('cookedspecially_token');
    const user = localStorage.getItem('cookedspecially_user');
    const userMenu = document.getElementById('user-menu');

    if (token && user) {
        const userData = JSON.parse(user);
        userMenu.textContent = userData.first_name || 'My Account';
        userMenu.href = 'account.html';
    }
}
