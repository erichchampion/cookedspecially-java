// Multi-Restaurant Configuration System
// This configuration supports multiple restaurant families and dynamic branding

const config = {
    // Set to 'local' for development or 'aws' for production
    environment: 'local',

    // API Endpoints
    local: {
        restaurant: 'http://localhost:8081/api/v1',
        order: 'http://localhost:8082/api/v1',
        payment: 'http://localhost:8083/api/v1',
        customer: 'http://localhost:8084/api/v1',
        notification: 'http://localhost:8085/api/v1',
        loyalty: 'http://localhost:8086/api/v1'
    },

    aws: {
        restaurant: 'https://restaurant-api.example.com/api/v1',
        order: 'https://order-api.example.com/api/v1',
        payment: 'https://payment-api.example.com/api/v1',
        customer: 'https://customer-api.example.com/api/v1',
        notification: 'https://notification-api.example.com/api/v1',
        loyalty: 'https://loyalty-api.example.com/api/v1'
    },

    // Get the current environment's endpoints
    getEndpoints() {
        return this[this.environment];
    },

    // API settings
    api: {
        timeout: 30000,
        retries: 3
    },

    // Restaurant Profiles - Configure different restaurant brands
    restaurantProfiles: {
        // Example: SaladDays profile
        'saladdays': {
            id: 1,
            name: 'Salad Days',
            tagline: 'Handcrafted Premium Salads',
            description: 'Bringing real salads to India',
            theme: {
                primaryColor: '#27ae60',
                secondaryColor: '#e67e22',
                accentColor: '#f39c12'
            },
            branding: {
                logoText: 'Salad<span>Days</span>',
                favicon: 'images/saladdays/favicon.ico',
                heroImage: 'images/saladdays/hero.jpg'
            },
            contact: {
                phone: '+91 1234567890',
                email: 'hello@saladdays.co',
                address: 'Gurgaon, India'
            },
            settings: {
                currency: 'INR',
                currencySymbol: '₹',
                deliveryFee: 50,
                minOrderAmount: 199,
                freeDeliveryThreshold: 500
            },
            features: {
                loyaltyProgram: true,
                subscriptions: true,
                giftCards: false,
                catering: true
            }
        },

        // Example: Another restaurant brand
        'cookedspecially': {
            id: 2,
            name: 'CookedSpecially',
            tagline: 'Fresh, Healthy Meals Delivered',
            description: 'Nutritious meals made with premium ingredients',
            theme: {
                primaryColor: '#2ecc71',
                secondaryColor: '#e74c3c',
                accentColor: '#3498db'
            },
            branding: {
                logoText: 'Cooked<span>Specially</span>',
                favicon: 'images/cookedspecially/favicon.ico',
                heroImage: 'images/cookedspecially/hero.jpg'
            },
            contact: {
                phone: '+91 9876543210',
                email: 'hello@cookedspecially.com',
                address: 'Delhi, India'
            },
            settings: {
                currency: 'INR',
                currencySymbol: '₹',
                deliveryFee: 40,
                minOrderAmount: 299,
                freeDeliveryThreshold: 600
            },
            features: {
                loyaltyProgram: true,
                subscriptions: false,
                giftCards: true,
                catering: false
            }
        }
    },

    // Default restaurant (can be overridden)
    defaultRestaurantProfile: 'saladdays',

    // Storage keys (dynamically prefixed by restaurant)
    getStorageKeys(restaurantKey) {
        const prefix = restaurantKey || this.defaultRestaurantProfile;
        return {
            token: `${prefix}_token`,
            cart: `${prefix}_cart`,
            user: `${prefix}_user`,
            addresses: `${prefix}_addresses`,
            selectedRestaurant: `${prefix}_selected_restaurant`,
            restaurantProfile: 'active_restaurant_profile' // Global key
        };
    },

    // Get current restaurant profile
    getCurrentProfile() {
        const profileKey = localStorage.getItem('active_restaurant_profile') || this.defaultRestaurantProfile;
        return this.restaurantProfiles[profileKey] || this.restaurantProfiles[this.defaultRestaurantProfile];
    },

    // Set current restaurant profile
    setCurrentProfile(profileKey) {
        if (this.restaurantProfiles[profileKey]) {
            localStorage.setItem('active_restaurant_profile', profileKey);
            this.applyTheme(this.restaurantProfiles[profileKey].theme);
            return true;
        }
        return false;
    },

    // Apply theme colors dynamically
    applyTheme(theme) {
        if (!theme) return;

        const root = document.documentElement;
        if (theme.primaryColor) {
            root.style.setProperty('--primary-color', theme.primaryColor);
            root.style.setProperty('--primary-dark', this.darkenColor(theme.primaryColor, 10));
            root.style.setProperty('--primary-light', this.lightenColor(theme.primaryColor, 10));
        }
        if (theme.secondaryColor) {
            root.style.setProperty('--secondary-color', theme.secondaryColor);
        }
        if (theme.accentColor) {
            root.style.setProperty('--accent-color', theme.accentColor);
        }
    },

    // Helper: Darken color
    darkenColor(color, percent) {
        // Simple darkening - in production, use a proper color library
        const num = parseInt(color.replace('#', ''), 16);
        const amt = Math.round(2.55 * percent);
        const R = (num >> 16) - amt;
        const G = (num >> 8 & 0x00FF) - amt;
        const B = (num & 0x0000FF) - amt;
        return '#' + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
            (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
            (B < 255 ? B < 1 ? 0 : B : 255))
            .toString(16).slice(1);
    },

    // Helper: Lighten color
    lightenColor(color, percent) {
        const num = parseInt(color.replace('#', ''), 16);
        const amt = Math.round(2.55 * percent);
        const R = (num >> 16) + amt;
        const G = (num >> 8 & 0x00FF) + amt;
        const B = (num & 0x0000FF) + amt;
        return '#' + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
            (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
            (B < 255 ? B < 1 ? 0 : B : 255))
            .toString(16).slice(1);
    },

    // Get storage instance for current profile
    storage() {
        const profile = this.getCurrentProfile();
        const profileKey = localStorage.getItem('active_restaurant_profile') || this.defaultRestaurantProfile;
        return this.getStorageKeys(profileKey);
    },

    // Initialize configuration on page load
    init() {
        // Apply theme for current profile
        const profile = this.getCurrentProfile();
        if (profile && profile.theme) {
            this.applyTheme(profile.theme);
        }

        // Update page title and branding
        this.updatePageBranding();
    },

    // Update page branding elements
    updatePageBranding() {
        const profile = this.getCurrentProfile();
        if (!profile) return;

        // Update title
        const titleSuffix = ` - ${profile.name}`;
        if (!document.title.includes(titleSuffix)) {
            document.title = document.title.split(' - ')[0] + titleSuffix;
        }

        // Update logo
        const logos = document.querySelectorAll('.logo');
        logos.forEach(logo => {
            logo.innerHTML = profile.branding.logoText;
        });

        // Update meta description
        const metaDesc = document.querySelector('meta[name="description"]');
        if (metaDesc) {
            metaDesc.content = `${profile.name} - ${profile.description}`;
        }
    }
};

// Auto-initialize when loaded
if (typeof document !== 'undefined') {
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => config.init());
    } else {
        config.init();
    }
}

export default config;
