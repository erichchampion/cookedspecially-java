angular.module('app', ['ngAnimate', 'ui.bootstrap']);

angular.module('app').controller('selectFulfillmentCenterCtrl', function($scope, $modalInstance,fulfillmentCenterList, name ) {

    $scope.fulfillmentCenterList = fulfillmentCenterList;
    $scope.name = name;

    $scope.selectFulfillmentCenter = function(ffcId){
        $modalInstance.close(ffcId);
    };
});
angular.module('app').controller('ManageCredits', function ($rootScope, $scope, $filter, $interval, $http ,$modal) {
	$scope.listCreditBillByPredicate = "lastTransaction";
    $scope.listCreditBillReverse = true; 
    $scope.changeListCreditBills = function(predicate) {
        $scope.listCreditBillReverse = ($scope.listCreditBillByPredicate === predicate) ? !$scope.listCreditBillReverse : false;
        $scope.listCreditBillByPredicate = predicate;
    };
   $scope.listStrCreditBillByPredicate="creationTime";
   $scope.listStrCreditBillReverse = true; 
   $scope.changeListStrCreditBills = function(predicate) {
       $scope.listStrCreditBillReverse = ($scope.listStrCreditBillByPredicate === predicate) ? !$scope.listStrCreditBillReverse : false;
       $scope.listStrCreditBillByPredicate = predicate;
   };
	var fetchRestaurantInfo = function(){
	        $http.get($scope.restaurantInfoApi).then(function(response){
	            var data = response.data;
	            console.log(data);
	            $scope.city = data.city;
	            $rootScope.restaurantId = data.restaurantId;
	            $scope.currency = data.currency;
	            $http.get($scope.employeeDetailsApi).then(function(res){
	                var emp = res.data;
	                $rootScope.user = emp;
	                console.log(emp);
	                var restList = $.grep(emp.restaurantList , function(restaurant){
	                    return restaurant.restaurantId === $rootScope.restaurantId });
	                if(restList.length > 0){
	                	$rootScope.restaurantName=restList[0].restaurantName;
	                    $scope.fulfillmentCenterList = restList[0].fulfillmentCenterList;
	                    console.log($scope.fulfillmentCenterList.length);
	                    if($scope.fulfillmentCenterList.length>1){
	                    	$scope.getFulfillmentCenterList();
	                    }
	                    else{
	                    	$rootScope.ffcId=$scope.fulfillmentCenterList[0].fulfillmentCenterId;
	                    	$rootScope.getAllCreditsList();
	                   }
	                }
	             });
	        });
	        
	        $http.get("/CookedSpecially/customer/listCustomerCreditTypes")
			.then(function successCallback(response) {
					$scope.creditTypes=response.data;
					console.log($scope.creditTypes);
					$scope.selectedCreditType="";
					}, 
					function errorCallback(response) {
						$scope.errorMsg = response.data;
						console.log($scope.errorMsg);
					});
	    };
	    $scope.filterNull = function(prop){
	        return function(item){
	          return (item[prop]!=null );
	        }
	    }
	    
	    $scope.equalThan = function(prop, val){
	        return function(item){
	          return (item[prop] > val || item[prop]<val);
	        }
	    }
	    $rootScope.getAllCreditsList=function(){
	    	$http.get($scope.listAllCreditBillsApi+$rootScope.ffcId).then(function(response){
	    		 var data = response.data;
	    		 $rootScope.allCreditBills=data;
		            console.log($rootScope.allCreditBills);
	    	});
	    }
	    
	    $scope.getFulfillmentCenterList = function(){
	        //showOverlay(false);
	        //showSpinner(false);
	        var fulfillmentCenterList = $scope.fulfillmentCenterList;

	        var modalInstance = $modal.open({
	            templateUrl: 'selectFulfillmentCenterModal',
	            controller: 'selectFulfillmentCenterCtrl',
	            backdrop: 'static',
	            resolve: {
	            	fulfillmentCenterList: function(){ return fulfillmentCenterList; },
	                name : function(){ return $rootScope.restaurantName; }
	            }
	        });
	        modalInstance.result.then(function(ffcId) {
	        	$rootScope.ffcId=ffcId;
				$rootScope.getAllCreditsList();
	        }, function(err) {
	            //do nothing
	        });
	    };
	    
	    $scope.dispatchBills=function(billId){
	    	var req = {
					method : 'POST',
					url : $scope.markDeliveredApi,
					headers: {'Content-Type' : 'application/json'},
					data:{creditBillId:billId}
			       }
			 $http(req).then(function successCallback(response) {
				  if(response.data.result==="SUCCESS"){
					  console.log(response.data);
					   $rootScope.getAllCreditsList();
						alert(response.data.message);
				  }
				  else
					  {
					  alert(response.data.message);
					  }
					},
					function errorCallback(response) {
					$scope.errorMsg = response.data;
					console.log($scope.errorMsg);
			 });
	    }
	    $scope.openPayCreditPanel=function(bill){
	    	$rootScope.agedCreditBills=false;
            $rootScope.showAllBills=false;
            $rootScope.showPayCredit=true;
	    	$rootScope.billData=bill;
	    }
	    
	    
        $scope.markAsPaid=function(){
        	var req = {
					method : 'PUT',
					url : $scope.payCreditBillApi,
					headers: {'Content-Type' : 'application/json'},
					data :{ creditBillId:$scope.billData.billId,
						  paymentType:$scope.billData.radioValue, 
						  remark:$scope.billData.comments,
						  billAmount:$scope.billData.currentBillAmount
				      }
			       }
			 $http(req).then(function successCallback(response) {
				 console.log(response.data);
				 if(response.data.result==="SUCCESS"){
					 console.log(response.data);
					    $rootScope.getAllCreditsList();
						alert(response.data.message);
						$rootScope.showStructureCredit();
				 }
				 else
					 {
					 alert(response.data.message);	
					 }
						
						},
					function errorCallback(response) {
					$scope.errorMsg = response.data;
					console.log($scope.errorMsg);
			 });
	    }
        //Print Bill
        $scope.printCustomerCreditBill=function(statementId){
        	$http.get($scope.generateCustomerCreditBillPrintApi+statementId).then(function(dataObj){
        		 var frame1 = $('<iframe />');
                 frame1[0].name = "frame1";
                 frame1.css({ "position": "absolute", "top": "-1000000px" });
                 $("body").append(frame1);
                 var frameDoc = frame1[0].contentWindow ? frame1[0].contentWindow : frame1[0].contentDocument.document ? frame1[0].contentDocument.document : frame1[0].contentDocument;
                 frameDoc.document.open();
                 frameDoc.document.write(dataObj.data);
                 frameDoc.document.close();
                 setTimeout(function () {
                     window.frames["frame1"].focus();
                     window.frames["frame1"].print();
                     frame1.remove();
                 }, 2);
	    	});
        }
        
        //Aged Credits
        $scope.getAgedCreditList=function(ageCreditCount){
        	$http.get($scope.listAgedCreditApi+$rootScope.ffcId+"/"+ageCreditCount).then(function(response){
	    		 $scope.agedCreditData = response.data;
		            console.log($scope.agedCreditData);
	    	});
        }
        $scope.markStructureCredit=function(custId){
        	var req = {
					method : 'POST',
					url : $scope.generateCreditBillApi,
					headers: {'Content-Type' : 'application/json'},
					data:{customerId:custId}
					
			       }
			 $http(req).then(function successCallback(response) {
				 if(response.data){
					 console.log(response.data);
					 $rootScope.getAllCreditsList();
						alert("Sucessfully statement generated.");	
				 }
				 else
					 {
					 console(response.data);	
					 }
						
					},
					function errorCallback(response) {
					$scope.errorMsg = response.data;
					console.log($scope.errorMsg);
			 });
        	
        }
        $rootScope.showStructureCredit=function(){
        	$rootScope.agedCreditBills=false;
            $rootScope.showAllBills=true;
            $rootScope.showPayCredit=false;
           
        }
        
        $rootScope.showAgedCredit=function(ageCreditCount){
        	$rootScope.agedCreditBills=true;
            $rootScope.showAllBills=false;
            $rootScope.showPayCredit=false;
            $scope.getAgedCreditList(ageCreditCount);
        }
        $rootScope.showPayCredit=false;
        $rootScope.agedCreditBills=false;
        $rootScope.showAllBills=true;
        
	    $scope.restaurantInfoApi = "/CookedSpecially/restaurant/getrestaurantinfo";
	    $scope.employeeDetailsApi = "/CookedSpecially/user/getEmployeeDetails";
	    $scope.listAllCreditBillsApi="/CookedSpecially/customer/listAllCustomerCreditBills/"
	    $scope.markDeliveredApi="/CookedSpecially/customer/markCreditBillAsDelivered"
	    $scope.payCreditBillApi="/CookedSpecially/customer/payCreditBill"
	    $scope.listAgedCreditApi="/CookedSpecially/customer/listAgedOneOffCreditHolder/";
	    $scope.generateCreditBillApi="/CookedSpecially/customer/generateCreditBill";
	    $scope.generateCustomerCreditBillPrintApi="customer/generateCustomerCreditBillPrint?statementId=";
	    fetchRestaurantInfo();
});