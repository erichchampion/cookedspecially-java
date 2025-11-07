/**
 * Author Name :- Abhishek Kumar
 * EmailId :- anshilabhi1991@gmail.com
 * 
 */

angular.module('myApp').factory('RequestFactory',['$http','$q', function($http, $q) {
	var factory = {}; 

	var _shoot = function(method, url, data){
        return $http({
            url : url,
            method: method.toUpperCase(), 
            headers: {"Content-Type":"application/json"},
            data: data
        })
    }
	
	factory.shootRequest = function(method, url, data) {
		var deferred = $q.defer();
		_shoot(method, url, data).then(function (response){
			deferred.resolve({'RESULT': 'SUCCESS', 'data': response.data});
		   },function (response){
			   deferred.resolve({'RESULT': 'ERROR', 'data': response.data});
		   });
         return deferred.promise;
     }
	return factory;
}]);