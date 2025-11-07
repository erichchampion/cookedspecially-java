/**
 * Author Name :- Abhishek Kumar
 * EmailId :- anshilabhi1991@gmail.com
 * 
 */

/* All URL THAT WILL USED TO GET DATA FROM SERVER MUST BE STORED HERE */
angular.module('myApp').constant('Constants', {
    ORG_URLS: {
    		GET_ORG_INFO:  '/CookedSpecially/organization/getOrganizationInfo' },
    EMP_URLS:{
    		GET_EMP_PROFILE:  '/CookedSpecially/user/getEmployeeDetails',
    		GET_OPEN_SALE_REGISTER: '/CookedSpecially/salesregister/list'
    },
    REPORT_URL: {
    		ORG_LEV:{},
    		REST_LEV:{},
    		FFC_LEVE:{
    			GET_DELIVERY_REPORT: '/CookedSpecially/analysisAndReport/deliveryBoy',
        		GET_INVOICE_LIST_DELIVERED_BY_DB: '/CookedSpecially/analysisAndReport/listInvoiceDeliveredByDeliveryBoy',
        			},
        	TOP_DISHES: "/CookedSpecially/analysisAndReport/topDishes",
        	CAT_LIST: "/CookedSpecially/analysisAndReport/listDishCategory"
    },
    DATE: {
    		TIMEZONE: new Date().getTimezoneOffset(),
    		CURRENT_DATE:new Date()	
    },
    METHODS: {
    		GET: 'GET',
    		POST: 'POST',
    		PUT: 'PUT'
    },
});