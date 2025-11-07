var app = angular.module('app', ['ui.bootstrap']);
app.controller('selectFfcCtrl', function($scope, $modalInstance,fulfillmentCenterList, name ) {

    $scope.fulfillmentCenterList = fulfillmentCenterList;
    $scope.name = name;

    $scope.selectFfc = function(ffc){
        $modalInstance.close(ffc);
    };
});
app.controller('orderController', function ($scope, $rootScope, $filter, $interval, $http, $modal) {

    $rootScope.filterOrdersForFFCenter = undefined;
    $rootScope.showAllOrders = false;
    $rootScope.showKitchenScreen=true;
    $scope.newOrderList = [];
    $scope.orderInProcessList = [];
  var  setOrdersDay;
    var defaultSettings = function(){
        $scope.orderFilter = "";
        $scope.checkAllValueNewOrder = "Select All";
        $scope.checkAllValueOrderInProcess = "Select All";
        $scope.showOptionsNewOrder = false;
        $scope.showOptionsOrdersInProcess = false;
        $rootScope.ordersOfDay = "today";
       // alert($rootScope.ordersOfDay);
       // localStorage.setItem("setOrdersDay",$rootScope.ordersOfDay);
        
    };

    var updateOrderStatus = function(orderId,status) {

        var statusApi =  "/CookedSpecially/order/setOrderStatus?orderId=" + orderId + "&status=" + status+"&money=0" ;

        var setOrderStatus = $.ajax(statusApi, {
            isLocal: false
        });
        setOrderStatus.then(function (data) {

        },function(err) {
            console.log('error');
            console.log(err);
        });
        setOrderStatus.complete(function() {
            refreshOrderList();
            resetPromise(true);
        });
    };

    var fetchOrders = function(orderType,callbackFunc){
        var api = "/CookedSpecially/order/getOrdersByType?orderType="+orderType+"&ordersOfDay="+$rootScope.ordersOfDay;
        return $http.get(api);
    };

    var updateList = function(list, newList){

        var tempList = [];
        $.each(newList,function(){
            var order = this;
            var existingOrder = $.grep(list,function(o){ return o.id == order.id });

            if(existingOrder[0] != undefined){
                order.showDetails = existingOrder[0].showDetails;
                order.orderCheckbox = existingOrder[0].orderCheckbox;
            }
            // Erich: include TakeAway and Table orders when order.fulfillmentCenterId == 0
            if($rootScope.filterOrdersForFFCenter == undefined || $rootScope.filterOrdersForFFCenter.fulfillmentCenterId == order.fulfillmentCenterId || order.fulfillmentCenterId == 0)
                tempList.push(order);
        });

        list = list.filter( function( order ) {
            return $.grep($scope.cancelledOrderList,function(o){ return order.id == o.id }).length > 0;
        });

        $.each(list,function(){
            this.cancelled=true;
            tempList.push(this);
        });

        return tempList;
    };

    $scope.hideCancelledOrder = function(order,list){
        removeOrdersFromList(list,[order]);
    };

    $scope.showFutureOrders = function(){
        if($rootScope.ordersOfDay == "today"){
            $rootScope.ordersOfDay = "future";
            localStorage.setItem("setOrdersDay",$rootScope.ordersOfDay);
        }else{
            $rootScope.ordersOfDay = "today";
            localStorage.setItem("setOrdersDay",$rootScope.ordersOfDay);
        }
        $scope.cancelledOrderList = [];
        $scope.newOrderList = [];
        $scope.orderInProcessList = [];
        refreshOrderList();
    };

    var refreshOrderList = function () {

        fetchOrders("Cancelled").then(function(response){
            $scope.cancelledOrderList = response.data;
        },function(err){
            console.log(err.data);
            $scope.tempList = [];
        });

        fetchOrders("new").then(function(response){

            $scope.newOrderList = updateList($scope.newOrderList, response.data);

            $(".js-spin-overlay").hide();
            $(".js-spin-spinner").hide();
            $(".session-expired-message").hide();
        },function(err){
            $(".js-spin-overlay").show();
            $(".session-expired-message").show();
            $scope.tempList = [];
        });

        fetchOrders("pending").then(function(response){

           $scope.orderInProcessList = updateList($scope.orderInProcessList, response.data);

        },function(err){
            console.log(err.data);
            $scope.tempList = [];
        });
    };

    var promise =  $interval(refreshOrderList,30000);

    function resetPromise(restart){
        if (angular.isDefined(promise)) {
            $interval.cancel(promise);
            promise = undefined;
        }
        if(restart)
            promise =  $interval(refreshOrderList,30000);
    }

    $scope.$on('$destroy', function(){
        resetPromise(false);
    });

    $scope.toggleDetails = function(order){
        order.showDetails = !order.showDetails;
    };

    $scope.showOptions = function (list,option) {
        var flag = false;
        for (var i = 0; i < list.length; i++) {
            if (list[i].orderCheckbox) {
                flag = true;
                break;
            }
        }
        $scope[option] = flag;
    };

    var getSelectedOrders = function(inputList){

        var list =[];
        for (var i = 0; i < inputList.length; i++) {
            if (inputList[i].orderCheckbox) {
                list.push(inputList[i]);
            }
        }
        return list;
    };

    var removeOrdersFromList = function(list, orders){

        for (var i=0; i<orders.length; i++){
            var index = list.indexOf(orders[i]);
            list .splice(index, 1);
        }
    };

    $scope.cancelOrder = function(order){

        updateOrderStatus(order.id,"Cancelled");

        for (var i = 0; i < $scope.newOrderList.length; i++) {
            if ($scope.newOrderList[i].id == order.id) {
                $scope.newOrderList.splice(i, 1);
                break;
            }
        }
    };

    $scope.claimOrder = function(order){
        updateOrderStatus(order.id,"Pending");

        for (var i = 0; i < $scope.newOrderList.length; i++) {
            if ($scope.newOrderList[i].id == order.id) {
                $scope.newOrderList.splice(i, 1);
                break;
            }
        }
        order.orderCheckbox =false;
        $scope.orderInProcessList.push(order);
    };

    $scope.cancelOrders = function(){
        var list =  getSelectedOrders($scope.newOrderList);
        $.each(list,function(){ $scope.cancelOrder(this); });
        //alert($rootScope.ordersOfDay);
        if($rootScope.ordersOfDay == "today"){
        	defaultSettings();
            $rootScope.ordersOfDay = "today";
        }else{
        	defaultSettings();
            $rootScope.ordersOfDay = "future";
        }
        
    };


    $scope.claimOrders = function(){
        //http claim call
        var list = getSelectedOrders($scope.newOrderList);
        $.each(list,function(){ $scope.claimOrder(this); });
        if($rootScope.ordersOfDay == "today"){
        	defaultSettings();
            $rootScope.ordersOfDay = "today";
        }else{
        	defaultSettings();
            $rootScope.ordersOfDay = "future";
        }
    };

    $scope.checkAll = function(option){

        var flag = ($scope[option] == "Select All");
        var orders;
        if(option === 'checkAllValueNewOrder')
            orders = $scope.$eval("newOrderList | filter:orderFilter");
        else if(option === 'checkAllValueOrderInProcess')
            orders = $scope.$eval("orderInProcessList | filter:orderInProcessFilter");

        for (var i=0; i<orders.length; i++){
            if(orders[i].cancelled)
                continue;
            orders[i].orderCheckbox = flag;
        }
        if(option == 'checkAllValueNewOrder')
            $scope.showOptions($scope.newOrderList,'showOptionsNewOrder');
        else if(option == 'checkAllValueOrderInProcess')
            $scope.showOptions($scope.orderInProcessList,'showOptionsOrdersInProcess');

        if(flag)
            $scope[option] = "UnSelect";
        else
            $scope[option] = "Select All";
    };

    $scope.unclaimOrder = function(order){
        updateOrderStatus(order.id,"New");

        for (var i = 0; i < $scope.orderInProcessList.length; i++) {
            if ($scope.orderInProcessList[i].id == order.id) {
                $scope.orderInProcessList.splice(i, 1);
                break;
            }
        }
        order.orderCheckbox =false;

        $scope.newOrderList.push(order);
    };

    $scope.readyOrder = function(order){
        updateOrderStatus(order.id,"Ready");

        for (var i = 0; i < $scope.orderInProcessList.length; i++) {
            if ($scope.orderInProcessList[i].id == order.id) {
                $scope.orderInProcessList.splice(i, 1);
                break;
            }
        }
    };

    $scope.unclaimOrders = function(){

        var list = getSelectedOrders($scope.orderInProcessList);
        $.each(list,function(){ $scope.unclaimOrder(this); })
        if($rootScope.ordersOfDay == "today"){
        	defaultSettings();
            $rootScope.ordersOfDay = "today";
        }else{
        	defaultSettings();
            $rootScope.ordersOfDay = "future";
        }
    };

    $scope.readyOrders = function(){

        var list =  getSelectedOrders($scope.orderInProcessList);
        $.each(list,function(){ $scope.readyOrder(this); })
        defaultSettings();
    };

    $scope.printOrder = function(order){

        var content = "<body style=\" font-size: large;\"><div style=\"float:left\"><div><b>Name: </b>"+
            order.customerName+"</div><div><b>Order Type : </b>" +
            order.orderType+"</div> </div>" + "<div style=\"float:right\"><div><b>Delivery Time : </b>"+
            $scope.$eval(""+order.deliveryTime+"| date:\"h:mma\"")+"</div><div>" ;

        if(order.orderType == "Table")
            content = content + "<b>Table : </b>"+order.table;
        else if (order.orderType == 'TakeAway')
            content = content+"<b>Customer : </b>"+order.customerName;
        else (order.orderType == 'Delivery')
        content = content+"<b>Area : </b>"+order.deliveryArea;
        content = content+"</div></div><hr style='width: 200%;'>";

        content = content+"<table class=\"table\">"+
            "<thead><th class=\"col-lg-1\">Quantity</th>"+
            "<th class=\"col-lg-11\">Item</th></thead>";

        $.each(order.items,function(){
            var item = this;
            content = content + "<tr><td class=\"col-lg-1\">"+item.quantity;

            if(item.instructions != undefined && item.instructions != "")
                content = content + "<span><br><br></span>";

            $.each(item.addOns, function(){
                content = content +"<span><br>"+this.quantity+"</span>";
            });
            
            if(item.dishSizeName!="" && item.dishSizeName!=undefined){
        		content = content+"</td>"+"<td class=\"col-lg-11\">"+item.name +"<b>("+item.dishSizeName+") </b>";
            }else {
            	content = content+"</td>"+"<td class=\"col-lg-11\">"+item.name; 
            }
            if(item.instructions != undefined && item.instructions != "")
                content = content + "<span><br>("+item.instructions+")<br></span>";

            $.each(item.addOns, function(){
                content = content +"<span><br>"+this.name+"</span>";
            });

            content = content +"</td></tr>" ;
        });

        content = content + "</table></div>";
        content = content + "<hr style='width: 100%;'>";

        var frame1 = $('<iframe />');
        frame1[0].name = "frame1";
        frame1.css({ "position": "absolute", "top": "-1000000px" });
        $("body").append(frame1);
        var frameDoc = frame1[0].contentWindow ? frame1[0].contentWindow : frame1[0].contentDocument.document ? frame1[0].contentDocument.document : frame1[0].contentDocument;
        frameDoc.document.open();
        frameDoc.document.write(content);
        frameDoc.document.close();
        setTimeout(function () {
            window.frames["frame1"].focus();
            window.frames["frame1"].print();
            frame1.remove();
        }, 500);

    };


    $scope.filterOrdersByFFCenter = function(ffCenter){
        if(ffCenter != "ALL") {
            $rootScope.filterOrdersForFFCenter = ffCenter;
        }
        else if($scope.fulfillmentCenterList.length > 1) {
            $rootScope.filterOrdersForFFCenter = undefined;
        }
        refreshOrderList();
    };
    
    var setFfc;
    var setPage;
    var setFfcList;
    var fetchRestaurantInfo = function(){

        $http.get($scope.restaurantInfoApi).then(function(response){
            var data = response.data;
            $scope.city = data.city;
            $rootScope.restaurantId = data.restaurantId;
            $scope.restaurantName = data.restaurantName;
            $scope.currency = data.currency;
            $scope.fulfillmentCenterList = data.fulfillmentCenter;
            $http.get($scope.employeeDetailsApi).then(function(res){
                var emp = res.data;
                $rootScope.user = emp;
			      if (window.performance) {
			    	  console.info("window.performance work's fine on this browser");
			    	}

			    	if (performance.navigation.type == 1) {
			    	  console.info( "This page is reloaded" );
			    	  var getOrdersDay=localStorage.getItem("setOrdersDay");
			    	  var getFfc=localStorage.getItem("setFfc");
			    	  var getPage=localStorage.getItem("setPage");
			    	  var getFfcList=localStorage.getItem("setFfcList");
			    	  $scope.fulfillmentCenterList=JSON.parse(getFfcList);
			    	  if(getOrdersDay=="today"){
			    		  $rootScope.ordersOfDay = "today";
			    	  }
			    	  if(getOrdersDay=="future"){
			    		  $rootScope.ordersOfDay = "future";
			    	  }
			    	  if(getPage=="allOrder")
			    		  $scope.showAllOrderScrn();
                      if(getPage=="kitchen")
                    	  $scope.showKitchenScrn();
			    	  $scope.filterOrdersByFFCenter(JSON.parse(getFfc));
			    	  refreshOrderList();
			    	  
			    	} else {
			    	  console.info( "This page is not reloaded");
			    	  var restList = $.grep(emp.restaurantList , function(restaurant){
		                    return restaurant.restaurantId === $rootScope.restaurantId });
		                if(restList.length > 0){
		                	$rootScope.restaurantName=restList[0].restaurantName;
		                    $scope.fulfillmentCenterList = restList[0].fulfillmentCenterList;
		                    console.log($scope.fulfillmentCenterList.length);
		                    if($scope.fulfillmentCenterList.length>1){
		                    	localStorage.setItem("setFfcList",JSON.stringify($scope.fulfillmentCenterList));
		                    	$scope.getFfcList();
		                    }
		                    else{
		                    	refreshOrderList();
		                    	$scope.filterOrdersByFFCenter($scope.fulfillmentCenterList[0]);
		                    	localStorage.setItem("setFfc",JSON.stringify($scope.fulfillmentCenterList[0]));
		                   }
		                }
			    	}
            });
        });
    };
    
    $scope.getFfcList = function(){
    	$(".js-spin-overlay").hide();
        $(".js-spin-spinner").hide();
        var fulfillmentCenterList = $scope.fulfillmentCenterList;

        var modalInstance = $modal.open({
            templateUrl: 'selectFfcModal',
            controller: 'selectFfcCtrl',
            backdrop: 'static',
            resolve: {
            	fulfillmentCenterList: function(){ return fulfillmentCenterList; },
                name : function(){ return $rootScope.restaurantName; }
            }
        });
        modalInstance.result.then(function(ffc) {
        	refreshOrderList();
        	$scope.filterOrdersByFFCenter(ffc);
        	localStorage.setItem("setFfc",JSON.stringify(ffc));
        }, function(err) {
            //do nothing
        });
    };
    $scope.showKitchenScrn=function(){
    	$rootScope.showAllOrders = false;
        $rootScope.showKitchenScreen=true;
        localStorage.setItem("setPage",'kitchen');
    }
    $scope.showAllOrderScrn=function(){
    	$rootScope.showAllOrders = true;
        $rootScope.showKitchenScreen=false;
        localStorage.setItem("setPage",'allOrder');
    }
    $scope.getTopDishesReport=function(topDishes){
    	$scope.currentDate=new Date();
    	if(topDishes==="today"){
    	 window.location.href='reports/topDishes.xls?restaurantId='+$rootScope.restaurantId;
    	}
    	else if(topDishes==="lastWeek"){
    		var to = $scope.currentDate.setTime($scope.currentDate.getTime() - ($scope.currentDate.getDay() ? $scope.currentDate.getDay()-1 : 7) * 24 * 60 * 60 * 1000);
            var from = $scope.currentDate.setTime($scope.currentDate.getTime() - 7 * 24 * 60 * 60 * 1000);
            $scope.firstDayWeek=new Date(from)
            $scope.lastDayWeek=new Date(to)
            $scope.startDate=$scope.$eval("firstDayWeek|date:'yyyy-MM-dd'");
            $scope.lastDate=$scope.$eval("lastDayWeek|date:'yyyy-MM-dd'");
            console.log($scope.startDate);
            console.log($scope.lastDate);
            window.location.href='reports/topDishes.xls?restaurantId='+$rootScope.restaurantId+'&startDate='+$scope.startDate+'&endDate='+$scope.lastDate;
    	}
    	else if(topDishes==="lastMonth")
    		{
    		var y=$scope.currentDate.getFullYear();
    		var m=$scope.currentDate.getMonth()-1;
    		$scope.firstDayMonth = new Date(y, m, 1);
       	    $scope.lastDayMonth = new Date(y, m+1, 1);
       	    $scope.startDate=$scope.$eval("firstDayMonth|date:'yyyy-MM-dd'");
            $scope.lastDate=$scope.$eval("lastDayMonth|date:'yyyy-MM-dd'");
            console.log($scope.startDate);
            console.log($scope.lastDate);
            window.location.href='reports/topDishes.xls?restaurantId='+$rootScope.restaurantId+'&startDate='+$scope.startDate+'&endDate='+$scope.lastDate;
    		}
    	else if(topDishes==="lastSixMonths")
    		{
    		var y=$scope.currentDate.getFullYear();
    		var m=$scope.currentDate.getMonth()-6;
            var s=$scope.currentDate.getMonth()-1;
       	    $scope.firstDaySixMonth = new Date(y, m, 1);
       	    $scope.lastDaySixMonth = new Date(y, s+1, 1);
       	    $scope.startDate=$scope.$eval("firstDaySixMonth|date:'yyyy-MM-dd'");
            $scope.lastDate=$scope.$eval("lastDaySixMonth|date:'yyyy-MM-dd'");
            window.location.href='reports/topDishes.xls?restaurantId='+$rootScope.restaurantId+'&startDate='+$scope.startDate+'&endDate='+$scope.lastDate;
    		}
    };
    
    $scope.restaurantInfoApi = "/CookedSpecially/restaurant/getrestaurantinfo";
    $scope.employeeDetailsApi = "/CookedSpecially/user/getEmployeeDetails";

    defaultSettings();
    fetchRestaurantInfo();
});
app.filter('titleCase', function () {
    return function (input) {

        var res = ""
        var st = input.split(" ")
        for (var t = 0; t < st.length; t++) {
            res = res + st[t].charAt(0).toUpperCase() + st[t].substr(1, st[t].length).toLowerCase() + " ";
        }
        res = res.trim();
        return res;
    }

});
