# Multi-Restaurant Configuration Guide

This consumer website supports multiple restaurant brands/families through a single codebase. The configuration system allows you to deploy different branded websites from the same source code.

## Overview

The website can be configured to support any number of restaurant brands, each with their own:
- Branding (logo, colors, tagline)
- Settings (delivery fees, minimum orders, currency)
- Features (loyalty programs, subscriptions, etc.)
- Contact information

## Configuration File

All configuration is in `config/config.js`:

### Restaurant Profiles

Each restaurant profile contains:

```javascript
'restaurant-key': {
    id: 1,                    // Restaurant ID from database
    name: 'Brand Name',
    tagline: 'Your Tagline',
    description: 'SEO description',

    // Theme colors (applied dynamically to CSS)
    theme: {
        primaryColor: '#27ae60',
        secondaryColor: '#e67e22',
        accentColor: '#f39c12'
    },

    // Branding elements
    branding: {
        logoText: 'Brand<span>Name</span>',    // HTML for logo
        favicon: 'images/brand/favicon.ico',
        heroImage: 'images/brand/hero.jpg'
    },

    // Contact information
    contact: {
        phone: '+91 1234567890',
        email: 'hello@brand.com',
        address: 'City, Country'
    },

    // Business settings
    settings: {
        currency: 'INR',
        currencySymbol: '₹',
        deliveryFee: 50,
        minOrderAmount: 199,
        freeDeliveryThreshold: 500  // Free delivery above this amount
    },

    // Feature flags
    features: {
        loyaltyProgram: true,
        subscriptions: false,
        giftCards: true,
        catering: false
    }
}
```

## Adding a New Restaurant

### Step 1: Add Profile to config.js

```javascript
// In config/config.js
restaurantProfiles: {
    // ... existing profiles

    'newrestaurant': {
        id: 3,  // Get from database
        name: 'New Restaurant',
        tagline: 'Fresh Food Daily',
        description: 'We serve amazing food',
        theme: {
            primaryColor: '#3498db',
            secondaryColor: '#e74c3c',
            accentColor: '#f39c12'
        },
        branding: {
            logoText: 'New<span>Restaurant</span>',
            favicon: 'images/newrestaurant/favicon.ico',
            heroImage: 'images/newrestaurant/hero.jpg'
        },
        contact: {
            phone: '+1 555-1234',
            email: 'info@newrestaurant.com',
            address: 'New York, USA'
        },
        settings: {
            currency: 'USD',
            currencySymbol: '$',
            deliveryFee: 5,
            minOrderAmount: 25,
            freeDeliveryThreshold: 50
        },
        features: {
            loyaltyProgram: true,
            subscriptions: true,
            giftCards: false,
            catering: true
        }
    }
}
```

### Step 2: Add Images

Create images in the appropriate directory:
```
images/
  newrestaurant/
    favicon.ico
    hero.jpg
    logo.png
```

### Step 3: Set as Default (Optional)

```javascript
// In config/config.js
defaultRestaurantProfile: 'newrestaurant'
```

### Step 4: Test

Open the website - it will automatically use the new profile.

## Switching Restaurants

### Runtime Switching

Users can switch restaurants using JavaScript:

```javascript
// In browser console or via UI
import config from './config/config.js';
config.setCurrentProfile('newrestaurant');
location.reload();  // Reload to apply theme
```

### URL-Based Switching

You can add a query parameter to switch restaurants:

```javascript
// In your init code
const urlParams = new URLSearchParams(window.location.search);
const restaurant = urlParams.get('restaurant');
if (restaurant) {
    config.setCurrentProfile(restaurant);
}
```

Then visit: `https://yoursite.com?restaurant=newrestaurant`

### Subdomain-Based Switching

For production, map subdomains to restaurant profiles:

```javascript
// In config/config.js init()
const hostname = window.location.hostname;
const restaurantMap = {
    'saladdays.example.com': 'saladdays',
    'cookedspecially.example.com': 'cookedspecially',
    'newrestaurant.example.com': 'newrestaurant'
};

const profile = restaurantMap[hostname];
if (profile) {
    config.setCurrentProfile(profile);
}
```

## Dynamic Features

### Theme Colors

Colors are applied automatically to CSS custom properties:
- `--primary-color`
- `--primary-dark` (auto-generated)
- `--primary-light` (auto-generated)
- `--secondary-color`
- `--accent-color`

### Currency Formatting

All prices use the profile's currency:

```javascript
// In cart.js
formatCurrency(amount) {
    const profile = this.getProfile();
    return `${profile.settings.currencySymbol}${amount.toFixed(2)}`;
}
```

### Delivery Fees

Delivery fees are calculated from profile settings:

```javascript
// Automatic free delivery above threshold
getDeliveryFee() {
    const profile = this.getProfile();
    const subtotal = this.getSubtotal();

    if (subtotal >= profile.settings.freeDeliveryThreshold) {
        return 0;  // Free delivery
    }

    return subtotal >= profile.settings.minOrderAmount
        ? profile.settings.deliveryFee
        : 0;
}
```

### Branding

Branding updates automatically:
- Page title includes restaurant name
- Logo text updates in header
- Meta description updates for SEO

### Storage Isolation

Each restaurant has separate localStorage:
- `saladdays_cart`, `saladdays_token`, etc.
- `cookedspecially_cart`, `cookedspecially_token`, etc.

This allows users to have different carts/sessions for different restaurants.

## Deployment Strategies

### Single Deployment (All Restaurants)

Deploy once, use URL parameters or subdomains to switch:

```nginx
# Nginx config
server {
    server_name saladdays.example.com;
    root /var/www/restaurant-site;

    location / {
        try_files $uri $uri/ /index.html;
    }
}

server {
    server_name cookedspecially.example.com;
    root /var/www/restaurant-site;  # Same files

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

Then detect hostname in JavaScript and switch profile.

### Multiple Deployments (Separate Sites)

Deploy separately with different default profiles:

```bash
# Site 1
cd build/saladdays
sed -i 's/defaultRestaurantProfile: .*/defaultRestaurantProfile: "saladdays"/' config/config.js
aws s3 sync . s3://saladdays-site

# Site 2
cd build/cookedspecially
sed -i 's/defaultRestaurantProfile: .*/defaultRestaurantProfile: "cookedspecially"/' config/config.js
aws s3 sync . s3://cookedspecially-site
```

## Feature Flags

Use feature flags to enable/disable features per restaurant:

```javascript
// In your code
const profile = config.getCurrentProfile();

if (profile.features.loyaltyProgram) {
    // Show loyalty points
}

if (profile.features.subscriptions) {
    // Show subscription options
}
```

## API Integration

The restaurant ID from the profile is used for all API calls:

```javascript
// Menu loading
const profile = config.getCurrentProfile();
const result = await restaurantApi.getMenu(profile.id);

// Order creation
const orderData = {
    customer_id: customerId,
    restaurant_id: profile.id,
    // ...
};
```

## Best Practices

### 1. Use Profile Helper Methods

```javascript
// Good
const profile = config.getCurrentProfile();
const deliveryFee = profile.settings.deliveryFee;

// Avoid
const deliveryFee = 50;  // Hardcoded
```

### 2. Test All Profiles

Before deploying, test with each profile:

```javascript
Object.keys(config.restaurantProfiles).forEach(key => {
    config.setCurrentProfile(key);
    // Test functionality
});
```

### 3. Validate Configuration

Ensure all required fields are present:

```javascript
function validateProfile(profile) {
    const required = ['id', 'name', 'theme', 'settings', 'contact'];
    required.forEach(field => {
        if (!profile[field]) {
            console.error(`Missing required field: ${field}`);
        }
    });
}
```

### 4. Use Environment Variables

For sensitive data, use environment variables:

```javascript
// During build
const config = {
    restaurantProfiles: {
        saladdays: {
            contact: {
                email: process.env.SALADDAYS_EMAIL
            }
        }
    }
};
```

## Troubleshooting

### Colors Not Updating

Clear localStorage and reload:
```javascript
localStorage.clear();
location.reload();
```

### Wrong Restaurant Data

Check the active profile:
```javascript
console.log(localStorage.getItem('active_restaurant_profile'));
console.log(config.getCurrentProfile());
```

### Cart From Wrong Restaurant

Cart is isolated per profile. If you switch profiles, the cart from the previous profile won't show. This is intentional.

To clear all carts:
```javascript
Object.keys(config.restaurantProfiles).forEach(key => {
    const keys = config.getStorageKeys(key);
    localStorage.removeItem(keys.cart);
});
```

## Migration from Legacy

If migrating from legacy saladdays:

1. **Export data**: Get restaurant ID, colors, settings from old site
2. **Create profile**: Add to `restaurantProfiles`
3. **Test**: Verify all features work with new profile
4. **Deploy**: Set as default and deploy
5. **DNS**: Point old domain to new site

## Example: Complete Restaurant Setup

```javascript
// config/config.js
'pizzapalace': {
    id: 5,
    name: 'Pizza Palace',
    tagline: 'Authentic Italian Pizza',
    description: 'Fresh wood-fired pizzas delivered hot to your door',

    theme: {
        primaryColor: '#d32f2f',
        secondaryColor: '#388e3c',
        accentColor: '#ffa000'
    },

    branding: {
        logoText: 'Pizza<span>Palace</span>',
        favicon: 'images/pizzapalace/favicon.ico',
        heroImage: 'images/pizzapalace/hero.jpg'
    },

    contact: {
        phone: '+39 06 1234 5678',
        email: 'orders@pizzapalace.it',
        address: 'Rome, Italy'
    },

    settings: {
        currency: 'EUR',
        currencySymbol: '€',
        deliveryFee: 3,
        minOrderAmount: 15,
        freeDeliveryThreshold: 30
    },

    features: {
        loyaltyProgram: true,
        subscriptions: false,
        giftCards: true,
        catering: true
    }
}
```

## Support

For questions or issues:
- Check this guide
- Review `config/config.js` comments
- Test in browser console
- Check browser localStorage and console for errors
