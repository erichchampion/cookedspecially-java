angular.module('app', [ 'ngAnimate', 'ui.bootstrap' ]);

angular.module('app').controller('CustomerManagementCtrl',['$scope','$rootScope','$http','$modal' ,function($scope,$rootScope, $http,$modal) {
	
	
	 function getEmployeeDtails()
	 {
		 showSpinner(true);
		 showOverlay(true);
		 $http.get("/CookedSpecially/user/getEmployeeDetails")
				.then(
						function successCallback(response) {
						$scope.empData=response.data;
						$scope.orgName=$scope.empData.orgName;
						$scope.name=$scope.empData.name;
						$rootScope.userRole=$scope.empData.role;
						$rootScope.orgId=$scope.empData.orgId;
						$rootScope.showCustomerProfile=true;
						}, 
						function errorCallback(response) {
							$scope.errorMsg = response.data;
							console.log($scope.errorMsg);
						});
		 showSpinner(false);
		 showOverlay(false);
	 }
	 $scope.showLoading=function(){
	    	$(".spinSpinner").show();
	    	$(".spinOverlay").show();
	    }
	    $scope.hideLoading=function(){
	    	$(".spinSpinner").hide();
	    	$(".spinOverlay").hide();
	    }
	 function showSpinner(flag){
	        if(flag)
	            $(".js-spin-spinner").show();
	        else
	            $(".js-spin-spinner").hide();
	    }
	    function showOverlay(flag){
	        if(flag)
	            $(".js-spin-overlay").show();
	        else
	            $(".js-spin-overlay").hide();
	    }
	    function showSessionExpiredMessage(flag){
	        if(flag)
	            $(".session-expired-message").show();
	        else
	            $(".session-expired-message").hide();
	    }
	    //$scope.isVisible=true;
	 getEmployeeDtails();
}
]);