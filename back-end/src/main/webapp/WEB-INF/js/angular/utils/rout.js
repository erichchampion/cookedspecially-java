/**
 * Author Name :- Abhishek Kumar 
 * EmailId :- anshilabhi1991@gmail.com
 * 
 */

google.load('visualization', '1', {
	packages: ['corechart', 'orgChart', 'Table']
});

google.setOnLoadCallback(function() {
	angular.bootstrap(document, ['myApp']);
});

var app = angular.module('myApp', ['ngRoute', 'ui.bootstrap']);
app.config(function($routeProvider) {
    $routeProvider
    .when("/Organization", {
        templateUrl : "js/angular/template/org.html",
        controller: 'ORG-Controller'
    })
    .when("/Restaurant", {
        templateUrl : "js/angular/template/rest.html",
        controller: 'Restaurant-Controller'
    })
    .when("/FulfillmentCenter", {
        templateUrl : "js/angular/template/ffc.html",
        controller: 'FFC-Controller'
   /* })
    .otherwise({
    	templateUrl : 'js/angular/template/org.html',
    	 controller: 'ORG-Controller'*/
    });
});
