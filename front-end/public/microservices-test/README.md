# CookedSpecially Microservices Testing Dashboard

A comprehensive testing interface for the CookedSpecially microservices architecture. Built with ES6 JavaScript and plain CSS (no frameworks) for easy deployment and maximum compatibility.

## Features

- **Service Health Monitoring**: Real-time health checks for all 10 microservices
- **Interactive Testing**: Test each service's endpoints through an intuitive UI
- **Dashboard Overview**: Visual status indicators for all services
- **Modular Architecture**: ES6 modules for clean, maintainable code
- **Zero Dependencies**: No build step or npm packages required
- **Dual Deployment**: Run locally or deploy to AWS

## Architecture

The application consists of:

### Services
1. **Restaurant Service** (Port 8081) - Restaurant, menu, and dish management
2. **Order Service** (Port 8082) - Order creation and management
3. **Payment Service** (Port 8083) - Payment processing and transactions
4. **Customer Service** (Port 8084) - Customer profiles and authentication
5. **Notification Service** (Port 8085) - Email, SMS, and push notifications
6. **Loyalty Service** (Port 8086) - Coupons, promotions, and loyalty programs
7. **Kitchen Service** (Port 8087) - Kitchen operations and till management
8. **Reporting Service** (Port 8088) - Business analytics and reports
9. **Integration Hub Service** (Port 8089) - Zomato and social media integrations
10. **Admin Service** (Port 8090) - User management and RBAC

### Directory Structure
```
microservices-test/
├── index.html                  # Main dashboard
├── pages/                      # Service-specific test pages
│   ├── restaurants.html
│   ├── orders.html
│   ├── customers.html
│   ├── admin.html
│   └── reports.html
├── css/                        # Stylesheets
│   ├── main.css               # Base styles
│   └── dashboard.css          # Dashboard-specific styles
├── js/
│   ├── dashboard.js           # Dashboard controller
│   ├── utils.js               # Utility functions
│   ├── modules/               # API client modules
│   │   ├── api-client.js     # Base API client
│   │   ├── restaurant-api.js
│   │   ├── order-api.js
│   │   ├── customer-api.js
│   │   ├── admin-api.js
│   │   ├── payment-api.js
│   │   ├── loyalty-api.js
│   │   ├── kitchen-api.js
│   │   ├── reporting-api.js
│   │   ├── integration-api.js
│   │   └── notification-api.js
│   └── pages/                 # Page controllers
│       ├── restaurants.js
│       ├── orders.js
│       ├── customers.js
│       ├── admin.js
│       └── reports.js
└── config/
    └── config.js              # Environment configuration
```

## Local Development

### Prerequisites

All 10 microservices must be running. You can start them using:

```bash
# From the project root
cd services

# Start each service (or use your preferred method)
# Example for Restaurant Service:
cd restaurant-service
mvn spring-boot:run

# Repeat for all services...
```

Or use Docker Compose (if available):

```bash
docker-compose up
```

### Running the Frontend

Since this is a static site with no build step, you have several options:

#### Option 1: Python HTTP Server
```bash
cd front-end/public/microservices-test
python3 -m http.server 8000
```
Then open http://localhost:8000

#### Option 2: Node.js HTTP Server
```bash
cd front-end/public/microservices-test
npx http-server -p 8000
```
Then open http://localhost:8000

#### Option 3: VS Code Live Server
1. Install the "Live Server" extension
2. Right-click on `index.html`
3. Select "Open with Live Server"

### Configuration

Edit `config/config.js` to configure service endpoints:

```javascript
const config = {
    environment: 'local',  // or 'aws'
    services: {
        restaurant: {
            baseUrl: 'http://localhost:8081/api/v1',
            healthCheck: '/actuator/health'
        },
        // ... more services
    }
};
```

For AWS deployment, change `environment` to `'aws'` and update the service URLs.

## AWS Deployment

### Using S3 + CloudFront

1. **Build the configuration for AWS**:
   ```bash
   # Edit config/config.js and set environment to 'aws'
   # Update all baseUrl values to your AWS service endpoints
   ```

2. **Create S3 bucket**:
   ```bash
   aws s3 mb s3://cookedspecially-microservices-test
   aws s3 website s3://cookedspecially-microservices-test \
       --index-document index.html \
       --error-document index.html
   ```

3. **Upload files**:
   ```bash
   cd front-end/public/microservices-test
   aws s3 sync . s3://cookedspecially-microservices-test \
       --exclude ".git/*" \
       --exclude "README.md"
   ```

4. **Configure bucket policy** (for public access):
   ```json
   {
       "Version": "2012-10-17",
       "Statement": [{
           "Sid": "PublicReadGetObject",
           "Effect": "Allow",
           "Principal": "*",
           "Action": "s3:GetObject",
           "Resource": "arn:aws:s3:::cookedspecially-microservices-test/*"
       }]
   }
   ```

5. **Create CloudFront distribution** (optional, for HTTPS and CDN):
   - Origin: Your S3 bucket website endpoint
   - Default root object: `index.html`
   - Viewer protocol policy: Redirect HTTP to HTTPS

### Using Amplify

1. **Install Amplify CLI**:
   ```bash
   npm install -g @aws-amplify/cli
   ```

2. **Initialize and deploy**:
   ```bash
   cd front-end/public/microservices-test
   amplify init
   amplify add hosting
   amplify publish
   ```

## API Endpoints Tested

### Restaurant Service
- `POST /restaurants` - Create restaurant
- `GET /restaurants/{id}` - Get restaurant
- `GET /restaurants` - List restaurants
- `GET /restaurants/{id}/menu` - Get menu
- `POST /restaurants/{id}/menu` - Create menu item

### Order Service
- `POST /orders` - Create order
- `GET /orders/{id}` - Get order
- `GET /orders` - List orders
- `GET /orders/customer/{id}` - Get customer orders
- `PATCH /orders/{id}/status` - Update order status

### Customer Service
- `POST /customers/register` - Register customer
- `POST /customers/login` - Customer login
- `GET /customers/{id}` - Get customer
- `GET /customers` - List customers
- `GET /customers/{id}/addresses` - Get addresses

### Admin Service
- `POST /auth/login` - Admin login
- `POST /users` - Create user
- `GET /users` - List users
- `POST /auth/otp/generate` - Generate OTP
- `POST /employees` - Create employee

### Reporting Service
- `GET /reports/dashboard/restaurant/{id}` - Dashboard summary
- `GET /reports/sales/restaurant/{id}` - Sales report
- `GET /reports/sales/daily/restaurant/{id}` - Daily sales
- `GET /reports/sales/top-items/restaurant/{id}` - Top selling items

## Usage

1. **Start all microservices** (see Local Development section)

2. **Open the dashboard** in your browser

3. **Check service health**: The dashboard will automatically check all services on load

4. **Test individual services**: Click "Test Service" on any service card to access detailed testing pages

5. **Run API tests**: Fill out forms and click buttons to test various endpoints

6. **View results**: Results are displayed in formatted JSON or tables below each test section

## Troubleshooting

### CORS Issues
If you encounter CORS errors:
- Ensure services are configured to allow requests from your frontend origin
- Check that Spring Boot services have proper CORS configuration:
  ```java
  @CrossOrigin(origins = "http://localhost:8000")
  ```

### Service Offline
If a service shows as offline:
- Verify the service is running: `curl http://localhost:8081/actuator/health`
- Check the service port matches the configuration
- Review service logs for startup errors

### Authentication Issues
Some endpoints require authentication:
- Use the Login forms to obtain tokens
- Tokens are stored in localStorage
- Check browser console for auth errors

## Browser Compatibility

- Chrome 61+
- Firefox 60+
- Safari 11+
- Edge 16+

ES6 modules require a modern browser with native module support.

## Development

### Adding a New Service

1. **Add service to config**:
   ```javascript
   // config/config.js
   newservice: {
       baseUrl: 'http://localhost:8099/api/v1',
       healthCheck: '/actuator/health'
   }
   ```

2. **Create API client**:
   ```javascript
   // js/modules/newservice-api.js
   import ApiClient from './api-client.js';

   class NewServiceApi extends ApiClient {
       constructor() {
           super('newservice');
       }
       // Add methods...
   }

   export default new NewServiceApi();
   ```

3. **Create test page**: Copy an existing page template

4. **Create page controller**: Copy an existing controller template

5. **Update dashboard**: Add service card to `index.html`

## License

MIT License - See project root LICENSE file

## Support

For issues or questions:
- Check service logs in `logs/` directory
- Review browser console for JavaScript errors
- Verify network tab for API request/response details
