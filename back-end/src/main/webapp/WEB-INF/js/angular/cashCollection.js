angular.module('app').controller('cashCollectionController', function ($rootScope, $scope, $filter, $interval, $http) {

    $rootScope.ordersOfDay = "today";
    
    $rootScope.showFutureOrders = function(){
        if($rootScope.ordersOfDay == "today"){
            $rootScope.ordersOfDay = "future";
        }else{
            $rootScope.ordersOfDay = "today";
        }
        fetchDispatchedOrders();
        promise = undefined;
        promise = $interval(fetchDispatchedOrders,30000);
    };

    var getDeliveryBoyList = function() {
		$http.get("/CookedSpecially/order/allDeliveryBoy.json")
					.then(
							function(data) {
								$scope.deliveryBoyData = data.data.deliveryBoy;
								console.log($scope.deliveryBoyData);
							},
							function(err) {
								console.log("Error in fetching delivery people list.");
								console.log(err);
							});
	};
    
    var fetchDispatchedOrders = function(){
			$http.get('/CookedSpecially/order/getDispatchedCancelOrders?orderType=cancelled&ordersOfDay='+$rootScope.ordersOfDay+'&restaurantId='+$rootScope.restaurantId)
				.then(
						function successCallback(response) {
						$scope.DispatchedCancelOrders=response.data;
						console.log($scope.DispatchedCancelOrders);
						}, 
						function errorCallback(response) {
							$scope.errorMsg = response.data;
							console.log($scope.errorMsg);
						});
	 
		  var orderType="outdelivery";
			$http.get("/CookedSpecially/order/getOrdersByType?orderType="
					+ orderType
					+ "&ordersOfDay="
					+ $rootScope.ordersOfDay+'&restaurantId='+$rootScope.restaurantId)
				.then(
						function successCallback(response) {
						$scope.dispatchOrdersData=response.data;
						 var dispatchData=$scope.DispatchedCancelOrders
							for (var i = 0; i < dispatchData.length; i++) {
								$scope.dispatchOrdersData.push(dispatchData[i]);
								}
						 console.log($scope.dispatchOrdersData);
						}, 
						function errorCallback(response) {
							$scope.errorMsg = response.data;
							console.log($scope.errorMsg);
						});
		
    };
    
    var orderList=$scope.dispatchOrdersData;
    $scope.paymentMethodList = $rootScope.paymentMethodsList;
	$rootScope.totalAmountSum = 0;
	$scope.isTotalCashCollected=false;
	$scope.isShowButton=false;
	$scope.isShowDetail=false;
	//$scope.deliveryBoyList = deliveryBoyList.data.deliveryBoy;
	$scope.deliveryBoySelectFilter = "";
	$.each(orderList,function() {
						this.showChangePaymentMethodOption = true;
							this.updatedPaymentMethod = $scope.paymentMethodList[0].name;
						if(this.status==="CANCELLED" ||(this.paymentMethod!=="COD" && this.changeAmount!==0)){
							this.totalAmount=this.changeAmount;
						}
						else if(this.paymentMethod!=="COD" && this.changeAmount===0)
							this.totalAmount=this.changeAmount;
						else
							this.totalAmount = this.orderAmount + this.changeAmount+this.creditBalance;
						this.deliveryStatus = "delivered";
						$rootScope.totalAmountSum += this.totalAmount;
					});
	$scope.updateTotalAmountSum = function() {
		$scope.isTotalCashCollected=true;
		if($scope.isShowDetail!==true){
			$scope.isShowButton=true;
		}
		
		$rootScope.totalAmountSum = 0;
		$.each($scope.$eval("orderList | filter:deliveredOrderFilter | filter:filterByDeliveryBoy"),function() {
							$rootScope.totalAmountSum += this.totalAmount;
		});
		
	};
	$scope.showDetailOrder=function(){
		$scope.isShowDetail=true;
		$scope.isShowButton=false;
	}
	$scope.hideDetailOrder=function(){
		$scope.isShowDetail=false;
		$scope.isShowButton=true;
	}
	$scope.orderList = orderList;
	$scope.selectAllValue = "Select All";
	$scope.showDeliveredOptions = false;
	$scope.showOptions = function() {
		var flag = false;
		for (var i = 0; i < $scope.orderList.length; i++) {
			if ($scope.orderList[i].orderCheckbox) {
				flag = true;
				break;
			}
		}
		$scope.showDeliveredOptions = flag;

		if (flag)
			$scope.selectAllValue = "UnSelect";
		else
			$scope.selectAllValue = "Select All";
	};
	$scope.checkAll = function() {
		var flag = ($scope.selectAllValue == "Select All");
		var orders = $scope.orderList;
		if (flag)
			orders = $scope
					.$eval("orderList | filter:deliveredOrderFilter | filter:filterByDeliveryBoy");
		$.each(orders, function() {
			this.orderCheckbox = flag;
		});
		$scope.showOptions();
	};
	$scope.updateOrderAmount = function(order) {
		if (order.deliveryStatus == 'delivered') {
			order.totalAmount = order.orderAmount
					+ order.changeAmount;
		} else if (order.deliveryStatus == 'cancelled') {
			order.totalAmount = order.changeAmount;
		};
		$scope.updateTotalAmountSum();
	};
	$scope.markOrdersDeliveredfilterByDeliveryBoy=function(){
		$scope.checkAll();
		$scope.markOrdersDelivered();
		$scope.selectAllValue = "Select All";
		$scope.showDeliveredOptions = false;
	}
	
	$scope.markOrdersDelivered = function() {
		var ordersToProcess = $scope
				.$eval("orderList | filter:deliveredOrderFilter | filter:filterByDeliveryBoy");
		ordersToProcess = $.grep(ordersToProcess, function(o) {
			return o.orderCheckbox
		});
		if (ordersToProcess.length < 1)
			return;
		for (var i = 0; i < ordersToProcess.length; i++) {
			var index = $scope.orderList
					.indexOf(ordersToProcess[i]);
			$scope.orderList.splice(index, 1);
		}
		$.each(ordersToProcess,function() {
			if(angular.isUndefinedOrNull($scope.saleRegisterId)===true){
				$modalInstance.close();
				alert("Please, relogin");
			}
			else{
							var order = this;
							if (!order.showChangePaymentMethodOption){
								alert("Please save the payment Type")
							}
							else{
								//order.paymentMethod = order.updatedPaymentMethod;
							console.log($scope.saleRegisterId);
							var statusApi = "/CookedSpecially/order/setOrderStatus?orderId="
									+ order.id
									+ "&status="
									+ order.deliveryStatus
									+ "&money="
									+ order.totalAmount
									+ "&paymentType="
									+ order.paymentMethod
									+"&tillId="
                                    +$scope.saleRegisterId
                                    +"&remarks="
                                    +$scope.cancelEditRemarks;
							var orderStatus = $.ajax(statusApi,
									{
										isLocal : false
									});
							orderStatus.then(function(data) {
								$rootScope.totalAmountSum=$rootScope.totalAmountSum -order.totalAmount;
												//$scope.deliveredOrderFilter = "";
												//$scope.deliveryBoySelectFilter = ""
													$rootScope.salesSummaryData();
											},
											function(err) {
												console.log('error');
												console.log(err);
												$scope.orderList.push(order);
											});
			}
		}
			
						});
	};
	/*function getDeliveryBoyFirstName(boy){
		var temp=boy.split(" ");
		return temp[0];
}*/

	$scope.filterByDeliveryBoy = function(order) {
		
		if ($scope.deliveryBoySelectFilter == "" || $scope.deliveryBoySelectFilter=="ALL")
			return true;
		return $scope.deliveryBoySelectFilter==order.deliveryAgentId;
		//return order.deliveryAgent == $scope.deliveryBoySelectFilter;
	};
	
	
	$scope.savePaymentMethod=function(order){
		$scope.showLoading();
		console.log(order);
		$scope.showLoading();
		var req = {
				method : 'POST',
				url : '/CookedSpecially/order/setOrderPaymentType?orderId='+order.id+"&paymentType="+order.updatedPaymentMethod+"&tillId="+$rootScope.saleRegisterId+"&remarks="+$rootScope.remarks
			}
			$http(req).then(function successCallback(response) {
				            if(response.data.status=="success")
				            	{
				            	
				            	 $("#collectCashOverlay").show();
				                 $("#collectCashSpinner").show();
				            	$scope.salesSummaryData();
				            	$rootScope.getDispatchOrder();
				            	$rootScope.totalAmountSum=$rootScope.totalAmountSum-order.totalAmount;
								order.paymentMethod=order.updatedPaymentMethod;
				            	order.showChangePaymentMethodOption=true;
				            	
				            	order.updatedPaymentMethod = $scope.paymentMethodList[0].name;
							if(order.status==="CANCELLED" ||(order.paymentMethod!=="COD" && order.changeAmount!==0)){
								order.totalAmount=order.changeAmount;
							}
							else if(order.paymentMethod!=="COD" && order.changeAmount===0)
								order.totalAmount=order.changeAmount;
							else
								order.totalAmount = order.orderAmount + order.changeAmount+order.creditBalance;
				            	$("#collectCashOverlay").hide();
				        		$("#collectCashSpinner").hide();
				        		$scope.hideLoading();
				            	//$modalInstance.close();
				            	}
				            else
				            	{
				            	  alert(response.data.message);
				            	  order.showChangePaymentMethodOption=true;
				            	  $scope.hideLoading();
				            	}
				            $rootScope.totalAmountSum=$rootScope.totalAmountSum+order.totalAmount;
							},
							function errorCallback(response) {
								$scope.errorMsg = response.status;
								console.log($scope.errorMsg);
							});
		
	}
    


    var promise = undefined;
    $scope.$watch(function(){
        return $rootScope.showAllOrders;
    },function(newVal,oldVal){
        if($rootScope.showAllOrders){
            refreshOrderList();
            promise =  $interval(fetchDispatchedOrders,30000);
        }else{
            $interval.cancel(promise);
            promise = undefined;
        }
    });

    $scope.$watch(function(){
        return $rootScope.filterOrdersForFFCenter;
    },function(newVal,oldVal){
        refreshOrderList();
    });

    $scope.$watch(function(){
        return $rootScope.ordersOfDay;
    },function(newVal,oldVal){
        refreshOrderList();
    });

    $scope.$watch(function(){
        return $rootScope.restaurantId;
    },function(newVal,oldVal){
        refreshOrderList();
    });

    function refreshOrderList(){
        if($rootScope.showAllOrders) {
            $scope.allOrdersList = [];
            fetchDispatchedOrders();
        }
    }

    $scope.$on('$destroy', function(){
        if (angular.isDefined(promise)) {
            $interval.cancel(promise);
            promise = undefined;
            $(".js-spin-overlay").show();
            $(".session-expired-message").show();
        }
    });
    $scope.showLoading=function(){
    	$(".spinSpinner").show();
    	$(".spinOverlay").show();
    }
    $scope.hideLoading=function(){
    	$(".spinSpinner").hide();
    	$(".spinOverlay").hide();
    }
    $rootScope.isPartial=false;
    getDeliveryBoyList();
});