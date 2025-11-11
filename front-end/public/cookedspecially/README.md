# CookedSpecially - Consumer Website

A modern, consumer-facing e-commerce website for CookedSpecially, built with ES6 JavaScript and plain CSS. Provides a complete online ordering experience with menu browsing, shopping cart, checkout, and account management.

## Features

- **Menu Browsing**: Browse salads, protein bowls, juices, and more with filtering
- **Shopping Cart**: Add items, adjust quantities, and view totals
- **User Authentication**: Register, login, and manage account
- **Checkout Process**: Select delivery address and place orders
- **Order Tracking**: View order history and status
- **Responsive Design**: Mobile-friendly interface
- **Zero Build Step**: Pure ES6 modules, no npm or webpack required

## Tech Stack

- **Frontend**: ES6 JavaScript Modules, Plain CSS
- **Backend APIs**: CookedSpecially Microservices Architecture
  - Restaurant Service (Menu Management)
  - Customer Service (Authentication)
  - Order Service (Order Processing)
  - Payment Service (Transactions)
  - Loyalty Service (Rewards)

## Project Structure

```
cookedspecially/
├── index.html                 # Homepage
├── menu.html                  # Menu browsing
├── cart.html                  # Shopping cart
├── login.html                 # Authentication
├── checkout.html              # Checkout process
├── account.html               # Account & orders
├── css/
│   └── main.css              # All styles
├── js/
│   ├── cart.js               # Shopping cart logic
│   ├── modules/              # API client modules
│   │   ├── api-client.js
│   │   ├── restaurant-api.js
│   │   ├── customer-api.js
│   │   ├── order-api.js
│   │   ├── payment-api.js
│   │   └── loyalty-api.js
│   └── pages/                # Page controllers
│       ├── home.js
│       ├── menu.js
│       ├── cart-page.js
│       ├── auth.js
│       ├── checkout.js
│       └── account.js
└── config/
    └── config.js             # Environment configuration
```

## Local Development

### Prerequisites

1. **Microservices Running**: All backend services must be running on their respective ports
   - Restaurant Service: http://localhost:8081
   - Order Service: http://localhost:8082
   - Payment Service: http://localhost:8083
   - Customer Service: http://localhost:8084
   - Notification Service: http://localhost:8085
   - Loyalty Service: http://localhost:8086

2. **Web Server**: Any static file server (Python, Node.js, or VS Code Live Server)

### Running the Website

#### Option 1: Python HTTP Server
```bash
cd front-end/public/cookedspecially
python3 -m http.server 8000
```
Open http://localhost:8000

#### Option 2: Node.js HTTP Server
```bash
cd front-end/public/cookedspecially
npx http-server -p 8000
```
Open http://localhost:8000

#### Option 3: VS Code Live Server
1. Install "Live Server" extension
2. Right-click `index.html`
3. Select "Open with Live Server"

### Configuration

Edit `config/config.js` to configure API endpoints:

```javascript
const config = {
    environment: 'local',  // or 'aws' for production

    local: {
        restaurant: 'http://localhost:8081/api/v1',
        order: 'http://localhost:8082/api/v1',
        // ...
    },

    aws: {
        restaurant: 'https://restaurant-api.cookedspecially.com/api/v1',
        // ... your AWS endpoints
    }
};
```

## AWS Deployment

### Using S3 + CloudFront

1. **Configure for Production**:
   ```javascript
   // config/config.js
   environment: 'aws'  // Change to aws
   ```

2. **Create S3 Bucket**:
   ```bash
   aws s3 mb s3://cookedspecially-www
   aws s3 website s3://cookedspecially-www \
       --index-document index.html \
       --error-document index.html
   ```

3. **Upload Files**:
   ```bash
   cd front-end/public/cookedspecially
   aws s3 sync . s3://cookedspecially-www \
       --exclude ".git/*" \
       --exclude "README.md"
   ```

4. **Configure Public Access**:
   ```json
   {
       "Version": "2012-10-17",
       "Statement": [{
           "Effect": "Allow",
           "Principal": "*",
           "Action": "s3:GetObject",
           "Resource": "arn:aws:s3:::cookedspecially-www/*"
       }]
   }
   ```

5. **Create CloudFront Distribution**:
   - Origin: S3 bucket website endpoint
   - Default root object: `index.html`
   - Custom error response: 404 → /index.html (for SPA routing)
   - SSL certificate: Use ACM or default CloudFront

6. **Configure CORS** (if needed):
   Ensure backend services allow requests from your CloudFront domain.

### Using AWS Amplify

```bash
npm install -g @aws-amplify/cli
cd front-end/public/cookedspecially
amplify init
amplify add hosting
amplify publish
```

## Features Guide

### 1. Menu Browsing
- Browse all menu items
- Filter by category (Vegetarian, Non-Vegetarian, Juices, Fruit)
- View item details with images and descriptions
- Add items to cart with custom quantities and special instructions

### 2. Shopping Cart
- View all cart items
- Adjust quantities
- Remove items
- See subtotal, delivery fee, and total
- Minimum order amount: ₹199
- Delivery fee: ₹50 (free for orders above minimum)

### 3. User Authentication
- Register new account
- Sign in with email/password
- Account persistence with localStorage
- Automatic redirect to login for protected pages

### 4. Checkout Process
- Select/add delivery address
- Add delivery instructions
- Choose payment method (COD)
- Review order summary
- Place order

### 5. Account Management
- View profile information
- View order history
- Track order status
- Sign out

## API Integration

The site integrates with the following microservices:

### Restaurant Service
- `GET /restaurants/{id}/menu` - Get restaurant menu
- Used by: Menu page

### Customer Service
- `POST /customers/register` - Register new customer
- `POST /customers/login` - Customer login
- `GET /customers/{id}/addresses` - Get customer addresses
- `POST /customers/{id}/addresses` - Add new address
- Used by: Login page, Checkout page

### Order Service
- `POST /orders` - Create new order
- `GET /orders/customer/{id}` - Get customer orders
- `GET /orders/{id}` - Get order details
- Used by: Checkout page, Account page

## Browser Compatibility

- Chrome 61+
- Firefox 60+
- Safari 11+
- Edge 16+

Requires native ES6 module support.

## Troubleshooting

### CORS Errors
If you see CORS errors:
- Ensure backend services have CORS configured for your origin
- Check Spring Boot `@CrossOrigin` annotations
- Verify CloudFront origin settings

### Authentication Issues
- Check localStorage for `cookedspecially_token`
- Verify Customer Service is running
- Check browser console for API errors

### Cart Not Persisting
- Check browser localStorage
- Verify no errors in browser console
- Try clearing localStorage and refreshing

### Menu Not Loading
- Verify Restaurant Service is running on port 8081
- Check `config/config.js` environment setting
- Check browser network tab for API call failures

## Development Tips

1. **Live Reload**: Use VS Code Live Server or browser-sync for auto-refresh
2. **Debugging**: Open browser DevTools → Network tab to see API calls
3. **State Management**: Cart state is in localStorage, user state in localStorage
4. **API Errors**: Check browser console for detailed error messages

## Customization

### Branding
- Update logo text in all HTML files (currently "CookedSpecially")
- Modify CSS variables in `css/main.css` for colors:
  ```css
  --primary-color: #27ae60;  /* Main brand color */
  --secondary-color: #e67e22; /* Accent color */
  ```

### Menu Categories
- Update filter buttons in `menu.html`
- Modify filter logic in `js/pages/menu.js`

### Delivery Settings
- Change delivery fee and minimum order in `config/config.js`:
  ```javascript
  app: {
      deliveryFee: 50,
      minOrderAmount: 199
  }
  ```

## Production Checklist

Before deploying to production:

- [ ] Update `config/config.js` environment to `'aws'`
- [ ] Add production API endpoints
- [ ] Test all flows (register, login, order, checkout)
- [ ] Configure CORS on backend services
- [ ] Set up SSL certificate
- [ ] Test on mobile devices
- [ ] Configure error logging
- [ ] Set up monitoring
- [ ] Add analytics (Google Analytics, etc.)
- [ ] Create privacy policy and terms pages
- [ ] Test payment integration (when enabled)

## Support

For issues or questions:
- Check browser console for errors
- Verify backend services are running
- Check API responses in Network tab
- Review this README and configuration

## License

MIT License - See project root LICENSE file
