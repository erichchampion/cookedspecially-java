// Menu Page Controller

import restaurantApi from '../modules/restaurant-api.js';
import cart from '../cart.js';
import config from '../../config/config.js';

// State
let allMenuItems = [];
let currentFilter = 'all';
let selectedItem = null;

// Initialize
document.addEventListener('DOMContentLoaded', async () => {
    // Update cart UI
    cart.updateCartUI();

    // Load menu items
    await loadMenu();

    // Setup filter buttons
    setupFilters();

    // Setup modal
    setupModal();

    // Check authentication
    checkAuth();
});

// Load menu from API
async function loadMenu() {
    const loading = document.getElementById('loading');
    const menuGrid = document.getElementById('menu-grid');
    const emptyState = document.getElementById('empty-state');

    try {
        loading.classList.remove('hidden');

        const profile = config.getCurrentProfile();
        const restaurantId = profile.id;
        const result = await restaurantApi.getMenu(restaurantId);

        if (result.success && result.data) {
            // Flatten the menu structure (assuming nested sections)
            allMenuItems = result.data.flatMap(section =>
                section.items || []
            );

            renderMenuItems(allMenuItems);
            menuGrid.classList.remove('hidden');
        } else {
            emptyState.classList.remove('hidden');
        }
    } catch (error) {
        console.error('Error loading menu:', error);
        emptyState.classList.remove('hidden');
    } finally {
        loading.classList.add('hidden');
    }
}

// Render menu items
function renderMenuItems(items) {
    const menuGrid = document.getElementById('menu-grid');
    const emptyState = document.getElementById('empty-state');
    const profile = config.getCurrentProfile();

    if (!items || items.length === 0) {
        menuGrid.classList.add('hidden');
        emptyState.classList.remove('hidden');
        return;
    }

    menuGrid.classList.remove('hidden');
    emptyState.classList.add('hidden');

    menuGrid.innerHTML = items.map(item => `
        <div class="card menu-item" data-item-id="${item.itemId || item.id}">
            ${item.vegetarian ? '<span class="menu-item-badge veg">VEG</span>' : '<span class="menu-item-badge non-veg">NON-VEG</span>'}
            <img src="${item.smallImageUrl || item.imageUrl || 'images/placeholder.jpg'}"
                 alt="${item.name}"
                 class="card-image"
                 onerror="this.src='images/placeholder.jpg'">
            <div class="card-content">
                <h3 class="card-title">${item.name}</h3>
                <p class="card-description">${truncateText(stripHTML(item.shortDescription || item.description), 80)}</p>
            </div>
            <div class="card-footer">
                <span class="card-price">${profile.settings.currencySymbol}${item.price || item.displayPrice}</span>
                <button class="btn btn-primary btn-sm view-item-btn">View Details</button>
            </div>
        </div>
    `).join('');

    // Add click handlers
    document.querySelectorAll('.view-item-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const card = e.target.closest('.menu-item');
            const itemId = parseInt(card.dataset.itemId);
            const item = allMenuItems.find(i => (i.itemId || i.id) === itemId);
            if (item) showItemModal(item);
        });
    });
}

// Setup filter buttons
function setupFilters() {
    const filterBtns = document.querySelectorAll('.filter-btn');

    filterBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            // Update active state
            filterBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            // Apply filter
            const filter = btn.dataset.filter;
            currentFilter = filter;
            applyFilter(filter);
        });
    });
}

// Apply filter
function applyFilter(filter) {
    let filteredItems = allMenuItems;

    if (filter === 'vegetarian') {
        filteredItems = allMenuItems.filter(item => item.vegetarian === true);
    } else if (filter === 'non-vegetarian') {
        filteredItems = allMenuItems.filter(item => item.vegetarian === false);
    } else if (filter === 'juice') {
        filteredItems = allMenuItems.filter(item =>
            item.itemType && item.itemType.toLowerCase().includes('juice')
        );
    } else if (filter === 'fruit') {
        filteredItems = allMenuItems.filter(item =>
            item.itemType && item.itemType.toLowerCase().includes('fruit')
        );
    }

    renderMenuItems(filteredItems);
}

// Show item detail modal
function showItemModal(item) {
    selectedItem = item;
    const profile = config.getCurrentProfile();

    document.getElementById('modal-item-name').textContent = item.name;
    document.getElementById('modal-item-description').innerHTML = item.description || item.shortDescription;
    document.getElementById('modal-item-price').textContent = `${profile.settings.currencySymbol}${item.price || item.displayPrice}`;
    document.getElementById('modal-item-image').src = item.imageUrl || item.smallImageUrl || 'images/placeholder.jpg';
    document.getElementById('modal-quantity').value = 1;
    document.getElementById('modal-instructions').value = '';

    document.getElementById('item-modal').classList.add('active');
}

// Setup modal
function setupModal() {
    const modal = document.getElementById('item-modal');
    const closeBtn = document.getElementById('close-modal');
    const form = document.getElementById('add-to-cart-form');

    // Close modal
    closeBtn.addEventListener('click', () => {
        modal.classList.remove('active');
    });

    // Close on background click
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.remove('active');
        }
    });

    // Handle form submission
    form.addEventListener('submit', (e) => {
        e.preventDefault();

        const quantity = parseInt(document.getElementById('modal-quantity').value);
        const instructions = document.getElementById('modal-instructions').value;

        if (selectedItem) {
            cart.addItem(selectedItem, quantity, instructions);
            modal.classList.remove('active');
        }
    });
}

// Check authentication
function checkAuth() {
    const storageKeys = config.storage();
    const token = localStorage.getItem(storageKeys.token);
    const user = localStorage.getItem(storageKeys.user);
    const userMenu = document.getElementById('user-menu');

    if (token && user) {
        const userData = JSON.parse(user);
        userMenu.textContent = userData.first_name || 'My Account';
        userMenu.href = 'account.html';
    }
}

// Utility functions
function stripHTML(html) {
    const tmp = document.createElement('div');
    tmp.innerHTML = html;
    return tmp.textContent || tmp.innerText || '';
}

function truncateText(text, maxLength) {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}
