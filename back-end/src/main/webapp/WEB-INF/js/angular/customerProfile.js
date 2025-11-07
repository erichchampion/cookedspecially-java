angular.module('app').controller('CustomerProfileCtrl',['$scope','$rootScope','$http','$modal' ,function($scope,$rootScope, $http,$modal) {
	
	 $scope.filteredOrder = [] 
	    $scope.currentPage = 1
	    $scope.numPerPage = 50
	    $scope.maxSize = 3;
	 $scope.filteredCreditTransactions = [] 
	    $scope.currentCreditPage = 1
	    $scope.numPerCreditPage = 20
	    $scope.maxCreditPageSize = 3;
	
	$scope.searchByoptions = [{
		   name: 'By phone no.',
		   value: 'phone'
		}/*, {
		   name: 'By email',
		   value: 'email'
		},
		{
			   name: 'By name',
			   value: 'name'
			}*/
		];
	 
	function organizationInfo(){
			 $http.get("/CookedSpecially/organization/getOrganizationInfo?orgId="+$rootScope.orgId)
				.then(
						function successCallback(response) {
						$scope.orgInfo=response.data;
						$scope.restaurantArray=[];
						$scope.restaurantArray=response.data.restaurants;
						$scope.defaultCountryCode=$scope.orgInfo.countryCode;
						$scope.data = {
							    availableOptions: [
							      {value: $scope.defaultCountryCode, name: $scope.defaultCountryCode},
							    ],
							    selectedOption: {value: $scope.defaultCountryCode, name: $scope.defaultCountryCode} 
							    };
						}, 
						function errorCallback(response) {
							$scope.errorMsg = response.data;
							console.log($scope.errorMsg);
						});
		}
	    $scope.showOrderHistory = function(){
	    	$scope.showLoading();
	    	$scope.orderDetailsList = [];
            $("#moreOrdersPanel").show();
            $("#deliveryAddressPanel").hide();
	    	$("#customerInfoSetting").hide();
	    	$("#orderHistoryCol").hide();
	    	$("#customerDetailpanel").hide();
	    	$("#searchPanel").hide();
	    	$("#customerPanel").hide();
	    	 var custId=$scope.customerId;
	    	$http.get("/CookedSpecially/customer/getAllOrderHistory?orgId="
					+ $scope.orgId
					+ "&custId="
					+ custId
					+"&inDetail="
					+ true)
				.then(function successCallback(response) {
					//showSpinner(true);
					 //showOverlay(true);
							$scope.invoiceLinkPrefix=response.data.invoiceLinkPrefix;
							$scope.orderDetailsList=response.data.ordersDetail;
							$scope.totalOrders=response.data.totalOrders;
							$scope.$watch('currentPage + numPerPage', function() {
							    var begin = (($scope.currentPage - 1) * $scope.numPerPage);
							    var end = begin + $scope.numPerPage;
							    $scope.filteredOrder = $scope.orderDetailsList.slice(begin, end);
							  });
						    //showSpinner(false);
							 //showOverlay(false);
							$scope.hideLoading();
						}, 
						function errorCallback(response) {
							$scope.errorMsg = response.data;
							console.log($scope.errorMsg);
						});
	    	
	    };
	   
	    $scope.hideOrderHistory=function(){
	    	$("#moreOrdersPanel").hide();
	    	$("#deliveryAddressPanel").hide();
	    	$("#customerInfoSetting").show();
	    	$("#orderHistoryCol").show();
	    	$("#customerDetailpanel").show();
	    	$("#searchPanel").show();
	    	$("#customerPanel").hide();
	    } 
	    
	    $scope.getCustomerDetails = function() {
	    	$scope.showLoading();
			var searchText =$scope.data.selectedOption.value+$scope.searchText;
			$http.get("/CookedSpecially/customer/getCustomerData.json?orgId="
					+ $scope.orgId
					+ "&phone="
					+ encodeURIComponent(searchText))
				.then(function successCallback(response) {
					//showSpinner(true);
					 //showOverlay(true);
					 if(response.data.status=="Success"){
						 $scope.addressByRestaurant=response.data.addressByRestaurant;
						$scope.customerList=response.data.customer;
						if(response.data.customer.credit!==null){
							$scope.customerCreditType=response.data.customer.credit.creditType;
							$scope.creditAccountStatus=response.data.customer.credit.status;
						}
						if($scope.customerList.length>1){
							 $("#customerPanel").show();
							}
							else
								{
								 $scope.getOrderHistory($scope.customerList.customerId);
                                 $scope.getCustDetails($scope.customerList.customerId);
								 $scope.getCurrentOrder();
								 $("#customerDetailpanel").show();
								}
						// showSpinner(false);
						// showOverlay(false);
							}
							else
								{
								alert('There is no customer with this'+searchText+'.');
								$("#customerPanel").hide();
								$("#customerDetailpanel").hide();
								$("#customerInfoSetting").hide();
						    	$("#orderHistoryCol").hide();
								$scope.hideLoading();
								
								}
					
						}, 
						function errorCallback(response) {
							$scope.errorMsg = response.data;
							console.log($scope.errorMsg);
						});
			
		}
	    
	    $scope.deleteCustomerAddress=function(address, restId){
	    	var custAddress=address;
	    	var restId=restId;
			    var req = {
				method : 'POST',
				url : '/CookedSpecially/customer/removeDeliveryAddress',
				headers : {'Content-Type' : 'application/json'},
				data :{ id :custAddress.id,
					    customerId : custAddress.customerId,
					    customerAddress:custAddress.customerAddress,
					    deliveryArea:custAddress.deliveryArea,
					    city:custAddress.city,
					    state:custAddress.state
				      }
		       }
		 $http(req).then(function successCallback(response) {
				if(response.data.status=="success"){
					for(i=0; i<$scope.addressByRestaurant.length;i++){
						if($scope.addressByRestaurant[i].restaurantId==restId){
							$scope.customerAddress=$scope.addressByRestaurant[i].customerAddress;
							for(i=0 ;i<$scope.customerAddress.length;i++){
								if($scope.customerAddress[i].id==custAddress.id){
								$scope.customerAddress.splice(i,1);
								}
							}
						}
					}
					alert("The associated address deleted sucessfully.");
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
	    $scope.getTemplate = function (address) {
	    	 if (address.id === $scope.selected.id){
	    	  return 'edit';
	    	 }
	    	 else return 'display';
	    	};
	    
	    	$scope.selected = {};
	    $scope.updateCustomerAddress=function(address ,restaurantId){
	    	 $scope.selected = angular.copy(address);
	    	 $scope.getDeliveryArea(restaurantId);
	    }
	    
	    $scope.closeCreditAccount=function(){
		   
		    var req = {
					method : 'POST',
					url : '/CookedSpecially/customer/removeCustomerCredit',
					headers: {'Content-Type' : 'application/json'},
					data:{customerId:$scope.customerList.customerId}
					
			       }
	 $http(req).then(function successCallback(response) {
			console.log(response.data);
			if(response.data.result=="SUCCESS"){
				alert(response.data.message);
				 $scope.getCustomerDetails();
			}
			else{
				alert(response.data.message);
			}
			},
			function errorCallback(response) {
			$scope.errorMsg = response.data;
			console.log($scope.errorMsg);
	 });
    }
	    
	    $scope.updateCustomerDetails=function(){
		    var req = {
			method : 'POST',
			url : '/CookedSpecially/customer/setCustomerInfo.json',
			headers : {'Content-Type' : 'application/json'},
			data :{ customerId:$scope.customerList.customerId,
				    restaurantId:$rootScope.orgId,
				    firstName:$scope.customerList.firstName,
				    lastName:$scope.customerList.lastName,
				    email:$scope.customerList.email,
				    phone:$scope.customerList.phone, 
				    orgId: $rootScope.orgId
			      }
	       }
	 $http(req).then(function successCallback(response) {
		 if(response.data.status=="success"){
			 alert("Customer details updated sucessfully.");
			 $scope.hideModifyCustomer();
		 }
		 else{
			 alert(response.data.status);
		 }
			
			},
			function errorCallback(response) {
			$scope.errorMsg = response.data;
			console.log($scope.errorMsg);
	 });
    }
	    
	    $scope.saveCustomerAddress=function(address){
	    	
	    		var custAddress=address;
			    var req = {
				method : 'POST',
				url : '/CookedSpecially/customer/updateDeliveryAddress',
				headers : {'Content-Type' : 'application/json'},
				data :{ id :custAddress.id,
					    customerId : custAddress.customerId,
					    customerAddress:custAddress.customerAddress,
					    deliveryArea:custAddress.deliveryArea,
					    city:custAddress.city,
					    state:custAddress.state
				      }
		       }
		 $http(req).then(function successCallback(response) {
				if(response.data.status=="success"){
					alert("The associated address updated sucessfully.");
					$scope.selected = {};
					//$scope.istxtVisible=false;
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
       $scope.cancelCustomerAddress=function(){
    	   $scope.selected = {};
	    }
	    
       $scope.showAddDeliveryAddress=function(restaurantId){
    	   $scope.getDeliveryArea(restaurantId);
    	   $("#addDeliveryAddrPanel"+restaurantId).show();
    	   $("#addDeliveryAddrIcon"+restaurantId).hide();
       }
       $scope.cancelDeliveryAddress=function(restaurantId){
    	   $("#addDeliveryAddrPanel"+restaurantId).hide();
    	   $("#addDeliveryAddrIcon"+restaurantId).show();
       }
       $scope.addDeliveryAddress=function(address){
    	   var address=address;
    	   var restaurantId=address.reataurantId;
    	   var req = {
   				method : 'POST',
   				url : '/CookedSpecially/customer/updateDeliveryAddress',
   				headers : {'Content-Type' : 'application/json'},
   				data :{ customerId : $scope.customerList.customerId,
   					    customerAddress:address.custAddress,
   					    deliveryArea:address.custDeliveryArea,
   					    city:address.city,
   					    state:address.state
   				      }
   		       }
   		 $http(req).then(function successCallback(response) {
   				if(response.data.status=="success"){
   					$scope.refreshCustomer();
   					$scope.showDeliveryAddress();
   					alert(" Address is added sucessfully.");
   					$("#addDeliveryAddrPanel"+restaurantId).hide();
   		    	   $("#addDeliveryAddrIcon"+restaurantId).show();
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
       
       $scope.refreshCustomer=function(){
    	   var searchText=$scope.customerList.phone;
				$http.get("/CookedSpecially/customer/getCustomerData.json?orgId="
						+ $scope.orgId
						+ "&phone="
						+ encodeURIComponent(searchText))
					.then(function successCallback(response) {
						
						$scope.addressByRestaurant=response.data.addressByRestaurant;
						$scope.customerList=response.data.customer;
						$scope.customerCreditType=response.data.customer.credit.creditType;
						$scope.creditAccountStatus=response.data.customer.credit.status;
							}, 
							function errorCallback(response) {
								$scope.errorMsg = response.data;
								console.log($scope.errorMsg);
							});
       }
       
       $scope.getDeliveryArea=function(restaurantId){
    	    var restaurantId=restaurantId;
    	   $http.get("/CookedSpecially/restaurant/getDeliveryAreas?restaurantId="
						+ restaurantId)
					.then(function successCallback(response) {
						$scope.getAllDeliveryArea=response.data;
							}, 
							function errorCallback(response) {
								$scope.errorMsg = response.data;
								console.log($scope.errorMsg);
							});
       }
       
	    $scope.showDeliveryAddress=function(){
	    	$("#deliveryAddressPanel").show();
	    	$("#customerInfoSetting").hide();
	    	$("#orderHistoryCol").hide();
	    	$("#customerDetailpanel").hide();
	    	$("#searchPanel").hide();
	    	$("#customerPanel").hide();
	    }
	    
	    $scope.hideDeliveryAddress=function(){
	    	$("#deliveryAddressPanel").hide();
	    	$("#customerInfoSetting").show();
	    	$("#orderHistoryCol").show();
	    	$("#customerDetailpanel").show();
	    	$("#searchPanel").show();
	    	$("#customerPanel").hide();
	    }
	    
	    $scope.showModifyCustomer=function(){
	    	$("#deliveryAddressPanel").hide();
	    	$("#customerInfoSetting").hide();
	    	$("#orderHistoryCol").hide();
	    	$("#customerDetailpanel").hide();
	    	$("#searchPanel").hide();
	    	$("#customerPanel").hide();
            $("#modifyCustomerPanel").show();
	    	
	    }
	    $scope.hideModifyCustomer=function(){
	    	$("#deliveryAddressPanel").hide();
	    	$("#customerInfoSetting").show();
	    	$("#orderHistoryCol").show();
	    	$("#customerDetailpanel").show();
	    	$("#searchPanel").show();
	    	$("#customerPanel").hide();
	    	$("#modifyCustomerPanel").hide();
	    }
	    $scope.generateOneOffStatement=function(){
        	var req = {
					method : 'POST',
					url : '/CookedSpecially/customer/generateCreditBill',
					headers: {'Content-Type' : 'application/json'},
					data:{customerId:$scope.customerList.customerId}
					
			       }
			 $http(req).then(function successCallback(response) {
				 if(response.data){
					 console.log(response.data);
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
	    
	    $scope.getCustDetails=function(custId){
	    	$http.get("/CookedSpecially/customer/getCreditInfo/"+custId)
			.then(function successCallback(response) {
					$scope.custDetails=response.data;
					}, 
					function errorCallback(response) {
						$scope.errorMsg = response.data;
						console.log($scope.errorMsg);
					});
	    }
	    $scope.getOrderHistory=function(custId){
	    	
            var custId=custId;
            $scope.customerId=custId;
            var orderLimit=10;
	    	$http.get("/CookedSpecially/customer/getLatestOrderHistory?orgId="
					+ $scope.orgId
					+ "&custId="
					+ custId
					+"&orderLimit="
					+ orderLimit
					+"&inDetail="
					+ true)
				.then(
						function successCallback(response) {
							$("#orderHistoryCol").show();
							$scope.invoiceLinkPrefix=response.data.invoiceLinkPrefix;
							$scope.orderDetails=response.data.ordersDetail;
							$("#customerInfoSetting").show();
							$scope.hideLoading();
						}, 
						function errorCallback(response) {
							$scope.errorMsg = response.data;
							console.log($scope.errorMsg);
						});
	    	
	    }
	    
	    $scope.getCreditHistory=function(){
	    	$scope.showLoading();
	    	$("#creditTransactionPanel").show();
	    	$("#deliveryAddressPanel").hide();
	    	$("#customerInfoSetting").hide();
	    	$("#orderHistoryCol").hide();
	    	$("#customerDetailpanel").hide();
	    	$("#searchPanel").hide();
	    	$("#customerPanel").hide();
            $("#modifyCustomerPanel").hide();
            
	    	$http.get("/CookedSpecially/customer/listCustomerCreditBills/"+$scope.customerList.customerId)
			.then(function successCallback(response) {
				console.log(response.data);
				$scope.statementList=response.data.creditBills;
				$scope.unbilledTransactions=response.data.latestStatement;
				$scope.latestStatement=response.data.latestStatement;
				$scope.creditTransactions=response.data.latestStatement.transactions;
				$scope.selectedTransactions="Unbilled";
					//$scope.creditHistory=response.data;
				$scope.$watch('currentCreditPage + numPerCreditPage', function() {
				    var begin = (($scope.currentCreditPage - 1) * $scope.numPerCreditPage);
				    var end = begin + $scope.numPerCreditPage;
				    $scope.filteredCreditTransactions = $scope.creditTransactions.slice(begin, end);
				  });
					$scope.hideLoading();
					}, 
					function errorCallback(response) {
						$scope.errorMsg = response.data;
						console.log($scope.errorMsg);
					});

			
	    }
	    $scope.getTransactionByStatementId=function(statementId){
	    	if(statementId=="Unbilled"){
	    		$scope.latestStatement=$scope.unbilledTransactions;
	    		$scope.creditTransactions=$scope.unbilledTransactions.transactions;
	    	}
	    	else
	    		{
	    	$http.get("/CookedSpecially/customer/getCreditStatement/"+statementId)
			.then(function successCallback(response) {
				console.log(response.data);
				$scope.latestStatement=response.data;
				$scope.creditTransactions=response.data.transactions;
				$scope.$watch('currentCreditPage + numPerCreditPage', function() {
				    var begin = (($scope.currentCreditPage - 1) * $scope.numPerCreditPage);
				    var end = begin + $scope.numPerCreditPage;
				    $scope.filteredCreditTransactions = $scope.creditTransactions.slice(begin, end);
				  });
					$scope.hideLoading();
					}, 
					function errorCallback(response) {
						$scope.errorMsg = response.data;
						console.log($scope.errorMsg);
					});
	    		}
	    	
	    }
	    
	    //Save file
	    $scope.saveCustomerCreditBill=function(statementId){
        	$http.get("customer/generateCustomerCreditBillPrint?statementId="+statementId).then(function(dataObj){
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
	    
	    $scope.hideCreditTransaction=function(){
	    	$("#creditTransactionPanel").hide();
	    	$("#deliveryAddressPanel").hide();
	    	$("#customerInfoSetting").show();
	    	$("#orderHistoryCol").show();
	    	$("#customerDetailpanel").show();
	    	$("#searchPanel").show();
	    	$("#customerPanel").hide();
	    	$("#modifyCustomerPanel").hide();
	    }
	    
	    $scope.getCurrentOrder=function(){
	    	$scope.currentOrderArray=[];
	    	for(i=0;i<$scope.restaurantArray.length;i++){
	    			$http.get("/CookedSpecially/order/getOrdersByType?orderType=new&orderType=pending&orderType=ready&orderType=outdelivery&ordersOfDay=today&ordersOfDay=future&restaurantId="
								+ $scope.restaurantArray[i].restaurantId)
	    			.then(function successCallback(response) {
	    					for(i=0;i<response.data.length;i++){
	    						if(response.data[i].customerMobNo==$scope.customerList.phone){
	    							$scope.currentOrderArray.push(response.data[i]);
	    							$("#showCurrentOrder").show();
	    							$("#hideCurrentOrder").hide();
	    						}
	    					  }
	    					}, 
	    					function errorCallback(response) {
	    						$scope.errorMsg = response.data;
	    						console.log($scope.errorMsg);
	    					});
	    		}
	    	
	    }
	    
	    $scope.showAllCurrentOrder=function(){
	        $("#allcurrentOrderPanel").show();
	    	$("#creditTransactionPanel").hide();
	    	$("#deliveryAddressPanel").hide();
	    	$("#customerInfoSetting").hide();
	    	$("#orderHistoryCol").hide();
	    	$("#customerDetailpanel").hide();
	    	$("#searchPanel").hide();
	    	$("#customerPanel").hide();
            $("#modifyCustomerPanel").hide();
	    }
	    $scope.hideAllCurrentOrder=function(){
	    	$("#creditTransactionPanel").hide();
	    	$("#deliveryAddressPanel").hide();
	    	$("#customerInfoSetting").show();
	    	$("#orderHistoryCol").show();
	    	$("#customerDetailpanel").show();
	    	$("#searchPanel").show();
	    	$("#customerPanel").hide();
	    	$("#modifyCustomerPanel").hide();
	    	$("#allcurrentOrderPanel").hide();
	    }
	    
	   
	    
	    
	    $scope.showAssignCreditPanel=function(mngAcc){
	    	$scope.customerName=$scope.customerList.firstName+" " +$scope.customerList.lastName;
	    	$scope.customerList.phone=$scope.customerList.phone;
	    	$scope.customerList.email=$scope.customerList.email;
	    	if($scope.customerList.credit!==null)
	    	$scope.billingArea=$scope.customerList.credit.billingAddress;
	    	$scope.manageAccount=mngAcc;
	    	$scope.statusData = {
				    availableOptions: [
				    	  {value:"ACTIVE", name: 'ACTIVE'},
				    	  {value:"INACTIVE",name:'INACTIVE'},
				    	  { value:"SUSPENDED",name:'SUSPENDED'}
				    ],
				    selectedOption: {value: $scope.creditAccountStatus, name: $scope.creditAccountStatus} 
				    };
		
	    	$("#assignCreditPanel").show()
    	    $("#allcurrentOrderPanel").hide();
	    	$("#creditTransactionPanel").hide();
	    	$("#deliveryAddressPanel").hide();
	    	$("#customerInfoSetting").hide();
	    	$("#orderHistoryCol").hide();
	    	$("#customerDetailpanel").hide();
	    	$("#searchPanel").hide();
	    	$("#customerPanel").hide();
            $("#modifyCustomerPanel").hide();
            $scope.showAllCustomerCredit();
            $http.get("/CookedSpecially/customer/listCustomerCreditTypes")
			.then(function successCallback(response) {
					$scope.creditTypes=response.data;
					if($scope.customerList.credit!==null){
					for(i=0;i<$scope.creditTypes.length;i++){
						if($scope.creditTypes[i].id==$scope.customerCreditType.id)
							$scope.crtype=$scope.creditTypes[i];
					}}
					}, 
					function errorCallback(response) {
						$scope.errorMsg = response.data;
						console.log($scope.errorMsg);
					});
            
	    }
	    
	    $scope.showAllCustomerCredit=function(){
	    	$http.get("/CookedSpecially/customer/listCustomerCredit")
			.then(function successCallback(response) {
					$scope.allCustomerCredits=response.data;
					}, 
					function errorCallback(response) {
						$scope.errorMsg = response.data;
						console.log($scope.errorMsg);
					});
	    }
	   
	    
	    $scope.openCreditAccount=function()
	    {
	    	var req = {
	 				method : 'PUT',
	 				url : '/CookedSpecially/customer/enableCustomerCredit',
	 				headers : {'Content-Type' : 'application/json'},
	 				data :{ customerId :$scope.customerList.customerId,
	 					    creditTypeId:$scope.crtype.id,
	 					    maxLimit:$scope.crtype.maxLimit,
	 					    ffcId:$scope.billingArea.fulfillmentCenterId,
	 					    billingAddress:$scope.billingAddr+" "+$scope.billingArea.name
	 				      }
	 		       }
	 		 $http(req).then(function successCallback(response) {
	 				if(response.data.result=="SUCCESS"){
	 					window.alert(response.data.message);
	 					$scope.refreshCustomer();
	 					$scope.hideAssignCreditPanel();
	 				}
	 				else
	 				{
	 				alert(response.data.message);	
	 				$scope.showAssignCreditPanel();
	 				}
	 				},
	 				function errorCallback(response) {
	 				$scope.errorMsg = response.data;
	 				console.log($scope.errorMsg);
	 		 });
	    }
	    
	    $scope.updateCreditAccount=function()
	    {
	    	 var req = {
		 				method : 'PUT',
		 				url : '/CookedSpecially/customer/updateCustomerCredit',
		 				headers : {'Content-Type' : 'application/json'},
		 				data :{ customerId :$scope.customerList.customerId,
		 					    creditTypeId:$scope.crtype.id,
		 					    maxLimit:$scope.crtype.maxLimit,
		 					   ffcId:$scope.billingArea.fulfillmentCenterId,
		 					    billingAddress:$scope.billingAddr+" "+$scope.billingArea.name,
		 					    status:$scope.statusData.selectedOption.value,
		 				      }
		 		       }
		 		 $http(req).then(function successCallback(response) {
		 				if(response.data.result=="SUCCESS"){
		 					window.alert(response.data.message);
		 					$scope.refreshCustomer();
		 					$scope.hideAssignCreditPanel();
		 				}
		 				else
		 				{
		 				alert(response.data.message);	
		 				$scope.showAssignCreditPanel();
		 				}
		 				},
		 				function errorCallback(response) {
		 				$scope.errorMsg = response.data;
		 				console.log($scope.errorMsg);
		 		 });
	    }
	    $scope.hideAssignCreditPanel=function(){
	    	    $("#assignCreditPanel").hide()
		    	$("#creditTransactionPanel").hide();
		    	$("#deliveryAddressPanel").hide();
		    	$("#customerInfoSetting").show();
		    	$("#orderHistoryCol").show();
		    	$("#customerDetailpanel").show();
		    	$("#searchPanel").show();
		    	$("#customerPanel").hide();
		    	$("#modifyCustomerPanel").hide();
		    	$("#allcurrentOrderPanel").hide();
	    }
	    
	    $scope.searchBy = function() {
	    	if($scope.searchItem.value=="phone"){
	    	   $("#countryCode").show();
	    	}
	    else
	    	{
	    	 $("#countryCode").hide();
	    	}
           }
	    
	    
	    $scope.changeBillingRestaurant=function(restId){
	    	var restaurantId=restId;
	    	$scope.getDeliveryArea(restaurantId);
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
	    organizationInfo();
}
]);