angular.module('app').controller('allOrdersController', function ($rootScope, $scope, $filter, $interval, $http) {
	
	$scope.showChangePaymentMethodOption = true;
    $rootScope.ordersOfDay = "today";
    $scope.listOrderByPredicate = "invoiceId";
    $scope.listOrderReverse = true;

    $scope.changeListOrder = function(predicate) {
        $scope.listOrderReverse = ($scope.listOrderByPredicate === predicate) ? !$scope.listOrderReverse : false;
        $scope.listOrderByPredicate = predicate;
    };
    
   $scope.getPaytmStatus=function(checkId){
	   $scope.checkId=checkId;
	   $http.get("/CookedSpecially/restaurant/requestOrderStatus?checkId="+$scope.checkId)
		.then(function successCallback(response) {
				console.log(response.data);
				if(!response.data.orderStatus &&response.data.orderStatus=="TXN_SUCCESS"){
					alert(response.data.responseMsz);
					refreshOrderList();
				}
				else{
					alert(response.data.responseMsz);
				}
				}, 
				function errorCallback(response) {
					$scope.errorMsg = response.data;
					console.log($scope.errorMsg);
				});
   }

    $scope.printOrder = function(order){
        if(!order || order.invoiceId == undefined)
            return;

        var billapi= "/CookedSpecially/order/generateCheckForPrint?templateName=saladdaysbill&checkId="+order.checkId;

        var bill = $http.get(billapi).then(function (dataObj) {

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

        },function(err) {
            console.log('error in getting check');
            console.log(err);
        });
    };

    $rootScope.showFutureOrders = function(){
        if($rootScope.ordersOfDay == "today"){
            $rootScope.ordersOfDay = "future";
        }else{
            $rootScope.ordersOfDay = "today";
        }
        $scope.allOrdersList = [];
        fetchAllOrders();
        promise = undefined;
        promise = $interval(fetchAllOrders,60000);
    };

    var fetchAllOrders = function(){
        $scope.allOrdersApi = "/CookedSpecially/order/getOrdersByType?orderType=new&orderType=pending&orderType=ready" +
            "&orderType=outdelivery&orderType=delivered&orderType=cancelled";
        if($rootScope.ordersOfDay){
            $scope.allOrdersApi += "&ordersOfDay="+$rootScope.ordersOfDay;
        }
        if($rootScope.restaurantId){
            $scope.allOrdersApi += "&restaurantId="+$rootScope.restaurantId;
        }
        $http.get($scope.allOrdersApi).then(function(response){
            var data = response.data;
            if(!$scope.allOrdersList) {
                $scope.allOrdersList = data;
            }else{
                $.each(data,function(){
                	$scope.paymentMethodList = $rootScope.paymentMethodsList;
                    var order = this
                    this.showChangePaymentMethodOption=true;
                    if($scope.paymentMethodList!=undefined){
                    this.updatedPaymentMethod = $scope.paymentMethodList[0].name;
                    }
                    if($rootScope.filterOrdersForFFCenter && ($rootScope.filterOrdersForFFCenter.fulfillmentCenterId||$rootScope.filterOrdersForFFCenter.fulfillmentcenterId) != order.fulfillmentCenterId){
                    	console.log($rootScope.filterOrdersForFFCenter.fulfillmentcenterId+"return"+order.fulfillmentCenterId);
                    	return ;
                    }
                    var desiredOrderList = $.grep($scope.allOrdersList, function (o) { return o.checkId === order.checkId});
                    if(desiredOrderList.length > 0){
                        var existingOrder = desiredOrderList[0];
                        var index = $scope.allOrdersList.indexOf(existingOrder);
                        $scope.allOrdersList.splice(index, 1);
                        if(existingOrder.showDetails)
                            $scope.toggleDetails(order);
                    }
                    $scope.allOrdersList.push(order);
                });
            }
        })
    };

    
    $scope.getEditAccess = function(order,allowEdit) {
    	var allowEdit=allowEdit;
	    var req = {
		method : 'POST',
		url : '/CookedSpecially/order/allowEdit',
		headers : {'Content-Type' : 'application/x-www-form-urlencoded'},
		data :$.param({ checkId : order.checkId,
			    username : $rootScope.user.username,
			    allowEdit :allowEdit
		})
 }
 $http(req).then(function successCallback(response) {
	 if(response.data.status==="true")
		 refreshOrderList();
	 else
		 {
		 alert(response.data.message)
		 }
		},
		function errorCallback(response) {
		$scope.errorMsg = response.data;
 });

};
    
    var updateOrderStatus = function(orderId,status,refund) {

        var statusApi = "/CookedSpecially/order/setOrderStatus?orderId=" + orderId + "&status=" + status + "&money=0"+"&remarks=" + $rootScope.cancelEditRemarks+"&refund="+refund;

        var setOrderStatus = $.ajax(statusApi, {
            isLocal: false
        });
        setOrderStatus.then(function (data) {

        }, function (err) {
            
            console.log(err);
        });
        setOrderStatus.complete(function () {
        });
    };

   $rootScope.cancelOrder = function (order) {
    	var order=order;
	    var req = {
		method : 'POST',
		url : '/CookedSpecially/order/validateTillAccess',
		headers : {'Content-Type' : 'application/x-www-form-urlencoded'},
		data :$.param({ checkId : order.checkId,
			    username : $rootScope.user.username,
		})
 }
 $http(req).then(function successCallback(response) {
	 if(response.data.status==="true"){
		 cancelOrders(order);
	 }
	 else
		 {
		 alert(response.data.message)
		 }
		},
		function errorCallback(response) {
		$scope.errorMsg = response.data;
		console.log($scope.errorMsg);
 });
 };
var cancelOrders=function(order){
	
	 var shouldCancel = confirm("Do you want to cancel this order? It can't be reverted.");
     if(!shouldCancel)
         return;
     else{
     if(order.status=="DELIVERED" && order.paymentMethod!=="Third Party Paid"){
    	 $scope.cOrder=order;
    	 $(".spinOverlay").show();
    	 $("#appPopUp").show();
     }
     else if(order.paymentMethod=="PG"||order.paymentMethod=="MOBIKWIK_WALLET" || order.paymentMethod=="PAYTM")
    	 {
    	 $scope.cOrder=order;
    	 $(".spinOverlay").show();
    	 $("#appPopUp").show();
    	 }
     else
    	 {
    	 updateOrderStatus(order.id, "Cancelled","undefined");
    	    order.cancelled = true;
    	    order.status = "CANCELLED";
    	 }
     }
    
     
    // order.remarks=$rootScope.cancelEditRemarks;
}
$rootScope.customerCreditRefund=function(){
	$(".spinOverlay").hide();
	 $("#appPopUp").hide();
	 var order=$scope.cOrder;
	 updateOrderStatus(order.id, "Cancelled","CREDIT");
	    order.cancelled = true;
	    order.status = "CANCELLED";
}
    $scope.getFriendlyName = function (orderStatus) {
        var statusMap = {NEW:"New, yet to process", PENDING:"Kitchen, in process",
            READY:"Prepared, yet to dispatch",OUTDELIVERY:"Dispatched",DELIVERED:"Delivered",CANCELLED:"Cancelled"};
        return statusMap[orderStatus];
    };

    $rootScope.editOrder = function(order){
    	var order=order;
    	if(order.status==='OUTDELIVERY'){
	    var req = {
		method : 'POST',
		url : '/CookedSpecially/order/validateTillAccess',
		headers : {'Content-Type' : 'application/x-www-form-urlencoded'},
		data :$.param({ checkId : order.checkId,
			    username : $rootScope.user.username,
		})
 }
 $http(req).then(function successCallback(response) {
	 if(response.data.status==="true"){
		 editOrders(order);
	 }
	 else{
		 alert(response.data.message)
		 }
		},
		function errorCallback(response) {
		$scope.errorMsg = response.data;
		console.log($scope.errorMsg);
 });
    	}	
    	else
    		{
    		editOrders(order);
    		}
    };
var editOrders=function(order){
    $rootScope.currentOrder = order;
    $rootScope.currentOrder.isExistingOrder = true;
    $rootScope.currentOrder.keepOriginalDeliveryTime = true;
    $rootScope.currentOrder.keepOriginalOrderSource = true;
    $rootScope.currentOrder.keepOriginalPaymentMethod = true;
    $rootScope.currentOrder.user = {};
    $rootScope.currentOrder.user.phoneNo = order.customerMobNo;
    $rootScope.currentOrder.user.name = order.customerName;
    $rootScope.currentOrder.user.email = order.customerEmail;
    $rootScope.currentOrder.user.address = order.deliveryAddress;
    $rootScope.currentOrder.user.deliveryArea = order.deliveryArea;
    $rootScope.currentOrder.user.customerId = order.customerId;

    if(order.deliveryCharges == 0){
        $rootScope.currentOrder.deliveryChargesWaivedOff = true;
        var areas = $.grep($rootScope.allDeliveryAreas, function(area){ return area.name == $rootScope.currentOrder.user.deliveryArea });
        if(areas.length > 0)
            $rootScope.currentOrder.deliveryCharges = areas[0].deliveryCharges;
    }

    $rootScope.updateOrderAmount();
    $rootScope.showAllOrders = false;
    $rootScope.showPos=true;
}
    $scope.allowEditOrder = function(order,hasAccess){
    	
    	if(!hasAccess || order.status === 'CANCELLED'||(order.status==='DELIVERED'&& order.paymentStatus==='Cancel'))
            return false;
    	
    	if((order.paymentStatus=='Paid' && (order.paymentMethod=='PAYTM' || order.paymentMethod=='PG' || order.paymentMethod=='MOBIKWIK')))
            return false;
    	

        
        var allow = false;

        if(["admin","restaurantManager","fulfillmentCenterManager"].indexOf($rootScope.user.role) >= 0){
            allow = true;
        }  else if("deliveryManager" === $rootScope.user.role){
            if(order.status === 'OUTDELIVERY')
                allow = true;
        }
        else if("Call_Center_Associate" === $rootScope.user.role){
            if(!(order.status === 'DELIVERED' || order.status === 'OUTDELIVERY'))
                allow = true;
        }
        return allow;
    };

    $scope.allowCancelOrder = function(order,hasAccess){
        if(!hasAccess || order.status === 'CANCELLED'||(order.status==='DELIVERED'&& order.paymentStatus==='Cancel'))
            return false;

        var allow = false;

        if(["admin","restaurantManager","fulfillmentCenterManager"].indexOf($rootScope.user.role) >= 0){
            allow = true;
        } else if("deliveryManager" === $rootScope.user.role){
            if(order.status === 'OUTDELIVERY')
                allow = true;
        }
        else if("Call_Center_Associate" === $rootScope.user.role){
            if(!(order.status === 'DELIVERED' || order.status === 'OUTDELIVERY'))
                allow = true;
        }
        return allow;
    };

    $scope.toggleDetails = function(order){
        order.showDetails = !order.showDetails;

        if(!order.subTotal){
            var total = 0;
            $.each(order.items, function(){ total += this.quantity*this.price });
            order.subTotal = total;
        }
        
        if(order.taxJsonObj!=null){
        	var obj = JSON.parse(order.taxJsonObj);
        	$.each(obj,function(){
        	});
        	
        }
        
        if(!order.total){
            order.total = order.subTotal+ order.deliveryCharges;
            $.each(order.discountList, function(){
                this.amountForCurrentOrder = this.type=="PERCENTAGE"?(this.value*order.total/100):this.value;
                order.total -= this.amountForCurrentOrder;
            });
        }
        if(!order.payableAmount && $rootScope.taxList){
            order.payableAmount = order.total;
            $.each($rootScope.taxList,function(){
                order.payableAmount += this.chargeType=="PERCENTAGE"?(order.total*this.taxValue/100):this.taxValue;
            });
        }
        if(order.amountSaved == undefined && $rootScope.allDeliveryAreas){
            order.amountSaved = 0;
            $.each(order.discountList, function(){
                order.amountSaved += this.amountForCurrentOrder;
            });
            if(order.deliveryCharges == 0) {
                var areas = $.grep($rootScope.allDeliveryAreas, function (area) {
                    return area.name === order.deliveryArea
                });
                if (areas.length > 0) {
                    order.amountSaved += areas[0].deliveryCharges;
                    order.waivedOffDeliveryCharges = areas[0].deliveryCharges;
                }
            }
        }
    };

    $scope.openCancelPopUp=function(){
    	
    }
    
$scope.savePaymentMethod=function(order){
	
		console.log(order);
		if (!order.showChangePaymentMethodOption)
			order.paymentMethod = order.updatedPaymentMethod;
		$scope.showLoading();
		var req = {
				method : 'POST',
				url : '/CookedSpecially/order/setOrderPaymentType?orderId='+order.id+"&paymentType="+order.paymentMethod+"&tillId="+$rootScope.saleRegisterId+"&remarks="+$rootScope.remarks
			}
			$http(req).then(function successCallback(response) {
				            if(response.data.status=="success")
				            	{
				            	// $("#collectCashOverlay").show();
				            		//$("#collectCashSpinner").show();
				            	$scope.salesSummaryData();
				            	//$rootScope.getDispatchOrder();
				            	order.showChangePaymentMethodOption=true;
				            	//$("#collectCashOverlay").hide();
				        		//$("#collectCashSpinner").hide();
				            	//$modalInstance.close();
				            	$scope.hideLoading();
				            	}
				            else
				            	{
				            	  alert(response.data.message);
				            	$scope.hideLoading();
				            	}
				            //$scope.hideLoading();
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
            promise =  $interval(fetchAllOrders,60000);
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
            fetchAllOrders();
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
    
});