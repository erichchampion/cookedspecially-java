angular.module('app').controller('CreditDispatch', function ($rootScope, $scope, $filter, $interval, $http) {
$scope.paymentMethodsList=$rootScope.paymentMethodsList;
	console.log($scope.paymentMethodsList);
	/*function getCreditBillsList(){
	$http.get("/CookedSpecially/customer/listCustomerHavingCredit/"+32)
		.then(function successCallback(response) {
			$scope.allCreditBills=response.data;
				}, 
				function errorCallback(response) {
					$scope.errorMsg = response.data;
					console.log($scope.errorMsg);
				});
	}
	getCreditBillsList();*/
});