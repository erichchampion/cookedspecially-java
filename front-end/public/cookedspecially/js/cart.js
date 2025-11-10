// Shopping Cart Management

import config from '../config/config.js';

class Cart {
    constructor() {
        this.items = [];
        this.load();
    }

    // Load cart from localStorage
    load() {
        const savedCart = localStorage.getItem(config.storage.cart);
        if (savedCart) {
            try {
                this.items = JSON.parse(savedCart);
            } catch (error) {
                console.error('Error loading cart:', error);
                this.items = [];
            }
        }
    }

    // Save cart to localStorage
    save() {
        localStorage.setItem(config.storage.cart, JSON.stringify(this.items));
        this.updateCartUI();
    }

    // Add item to cart
    addItem(menuItem, quantity = 1, specialInstructions = '') {
        const existingItem = this.items.find(item => item.id === menuItem.id);

        if (existingItem) {
            existingItem.quantity += quantity;
            existingItem.specialInstructions = specialInstructions || existingItem.specialInstructions;
        } else {
            this.items.push({
                id: menuItem.id,
                name: menuItem.name,
                price: menuItem.price,
                imageUrl: menuItem.imageUrl || menuItem.smallImageUrl,
                quantity,
                specialInstructions,
                restaurantId: menuItem.restaurantId || config.app.defaultRestaurantId
            });
        }

        this.save();
        this.showAddedNotification(menuItem.name);
    }

    // Update item quantity
    updateQuantity(itemId, quantity) {
        const item = this.items.find(i => i.id === itemId);
        if (item) {
            if (quantity <= 0) {
                this.removeItem(itemId);
            } else {
                item.quantity = quantity;
                this.save();
            }
        }
    }

    // Remove item from cart
    removeItem(itemId) {
        this.items = this.items.filter(item => item.id !== itemId);
        this.save();
    }

    // Clear entire cart
    clear() {
        this.items = [];
        this.save();
    }

    // Get all items
    getItems() {
        return this.items;
    }

    // Get item count
    getItemCount() {
        return this.items.reduce((total, item) => total + item.quantity, 0);
    }

    // Calculate subtotal
    getSubtotal() {
        return this.items.reduce((total, item) => total + (item.price * item.quantity), 0);
    }

    // Calculate delivery fee
    getDeliveryFee() {
        return this.getSubtotal() >= config.app.minOrderAmount ? config.app.deliveryFee : 0;
    }

    // Calculate total
    getTotal() {
        return this.getSubtotal() + this.getDeliveryFee();
    }

    // Check if cart is empty
    isEmpty() {
        return this.items.length === 0;
    }

    // Update cart icon in header
    updateCartUI() {
        const cartCount = document.querySelector('.cart-count');
        const itemCount = this.getItemCount();

        if (cartCount) {
            cartCount.textContent = itemCount;
            cartCount.style.display = itemCount > 0 ? 'flex' : 'none';
        }

        // Dispatch custom event for cart update
        window.dispatchEvent(new CustomEvent('cartUpdated', {
            detail: {
                items: this.items,
                count: itemCount,
                subtotal: this.getSubtotal(),
                total: this.getTotal()
            }
        }));
    }

    // Show notification when item added
    showAddedNotification(itemName) {
        const notification = document.createElement('div');
        notification.className = 'cart-notification';
        notification.innerHTML = `
            <div class="notification-content">
                <span>✓ ${itemName} added to cart</span>
            </div>
        `;

        document.body.appendChild(notification);

        // Add styles if not already present
        if (!document.getElementById('cart-notification-styles')) {
            const style = document.createElement('style');
            style.id = 'cart-notification-styles';
            style.textContent = `
                .cart-notification {
                    position: fixed;
                    top: 100px;
                    right: 20px;
                    background: #27ae60;
                    color: white;
                    padding: 1rem 1.5rem;
                    border-radius: 8px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
                    z-index: 10000;
                    animation: slideIn 0.3s ease;
                }
                @keyframes slideIn {
                    from {
                        transform: translateX(400px);
                        opacity: 0;
                    }
                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
            `;
            document.head.appendChild(style);
        }

        // Remove notification after 3 seconds
        setTimeout(() => {
            notification.style.animation = 'slideIn 0.3s ease reverse';
            setTimeout(() => notification.remove(), 300);
        }, 3000);
    }

    // Get cart data for order creation
    getOrderData(customerId, deliveryAddress, notes = '') {
        return {
            customer_id: customerId,
            restaurant_id: this.items[0]?.restaurantId || config.app.defaultRestaurantId,
            delivery_address: deliveryAddress,
            notes,
            items: this.items.map(item => ({
                menu_item_id: item.id,
                quantity: item.quantity,
                special_instructions: item.specialInstructions || ''
            }))
        };
    }
}

// Create singleton instance
const cart = new Cart();

export default cart;
