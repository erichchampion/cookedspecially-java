
angular.module('components', [])
.directive('uppercased', function() {
 return {
     require: 'ngModel',        
     link: function(scope, element, attrs, modelCtrl) {
         modelCtrl.$parsers.push(function(input) {
             return input ? input.toUpperCase() : "";
         });
         element.css("text-transform","uppercase");
     }
 };
});

angular.module('app', ['ngAnimate', 'ui.bootstrap','components']);

angular.module('app').controller('selectRestaurantCtrl', function($scope, $modalInstance,restaurantList, name ) {

    $scope.restaurantList = restaurantList;
    $scope.name = name;

    $scope.selectRestaurant = function(restaurantId){
        $modalInstance.close(restaurantId);
    };
});

angular.module('app').controller('AddressInstanceCtrl', function($scope, $modalInstance, addressList) {

    $scope.addressList = addressList;

    $scope.selectAddress = function(address){
        $modalInstance.close(address);
    };

    $scope.done = function() {
        $modalInstance.close();
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('app').controller('ItemInstructionCtrl', function($scope, $modalInstance, type,instruction) {

    $scope.type = type;

    if(instruction == undefined)
        $scope.instruction = '';
    else
        $scope.instruction = instruction;

    $scope.submitInstruction = function(){
        $modalInstance.close($scope.instruction);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('app').controller('placeOrderCtrl',
    function($rootScope,$scope,$modalInstance,$http, paymentMethodList,orderSourceList,currentOrder) {
    $scope.deliveryTimesApi = "/CookedSpecially/order/time.json?tz="+$scope.timeZone+"&day=";

   /* var todayTimes = $http.get($scope.deliveryTimesApi+"Today").then(function(response){
        return response.data;
    });

    var tomorrowTimes = $http.get($scope.deliveryTimesApi+"tomorrow").then(function(response){
        return response.data;
    });*/
    var areaData =  $.grep($rootScope.allDeliveryAreas, function(area){
    	if(area.name == $rootScope.currentOrder.user.deliveryArea){
    	 return  area;
    	}else {
    		return false;
    	}});
    if(areaData==false){
    	alert("Invalid Delivery Area");
    	return false
    }

        $scope.paymentMethodList = paymentMethodList;
        $scope.orderSourceList = [{"name":"POS"}];
        $scope.orderSourceList = $scope.orderSourceList.concat(orderSourceList);
        $scope.deliveryDayList = ["Today","Tomorrow"]; //hard coded :(
        $scope.currentOrder = currentOrder;
        $scope.paymentMethod = currentOrder.paymentMethod;
        $scope.instructions = currentOrder.instructions;
        $scope.orderSource = currentOrder.orderSource||"POS";
        $scope.todayTimes = {};
        $scope.tomorrowTimes = {};
        $scope.activeDeliveryArea=0;

        /*todayTimes.then(function(todayData){*/
            $scope.todayTimes = areaData[0].todayTimeJson;
            setOrderDateAndTime();
        /*});*/
        /*tomorrowTimes.then(function(tomorrowData){*/
            $scope.tomorrowTimes = areaData[0].tomorrowTimeJson;
            setOrderDateAndTime();
       /* });*/

        function setOrderDateAndTime(){
            if(currentOrder.deliveryTime) {

                if(currentOrder.deliveryDay == $scope.tomorrowTimes.date)
                    $scope.deliveryDay = "Tomorrow";
                else
                    $scope.deliveryDay = "Today";

                if($scope.deliveryDay == "Today" &&  $scope.todayTimes.dateList
                    && $scope.todayTimes.dateList.length > 0  && $scope.todayTimes.dateList.indexOf(currentOrder.deliveryTime) < 0 )
                    $scope.deliveryTime = $scope.todayTimes.dateList[0];
                else
                    $scope.deliveryTime = currentOrder.deliveryTime;

                return true;
            }
            if($scope.todayTimes!=null && $scope.todayTimes.dateList && $scope.todayTimes.dateList.length > 0){
                $scope.deliveryDayList = ["Today","Tomorrow"];
                $scope.deliveryDay = "Today";
                $scope.deliveryTime = $scope.todayTimes.dateList[0];
            }
            else if($scope.tomorrowTimes.dateList && $scope.tomorrowTimes.dateList.length > 0){
            	$scope.deliveryDayList = ["Tomorrow"];
                $scope.deliveryDay = "Tomorrow";
                $scope.deliveryTime = $scope.tomorrowTimes.dateList[0];
            }
        }
		$scope.CurrentDate = new Date();
        $scope.placeOrder = function(flag){

            if(!$scope.deliveryDay || !$scope.deliveryTime || !$scope.orderSource || !$scope.paymentMethod ||
                (!currentOrder.keepOriginalPaymentMethod && $.grep($scope.paymentMethodList, function(method){ return method.name==$scope.paymentMethod}).length == 0) ||
                (!currentOrder.keepOriginalOrderSource && $.grep($scope.orderSourceList, function(source){ return source.name==$scope.orderSource}).length == 0)){
                $scope.showErr = true;
                return false;
            }
            var checkedValue = null;
            var inputElements = document.getElementsByClassName('paidStatus');
            for(var i=0; inputElements[i]; ++i){
                  if(inputElements[i].checked){
                       checkedValue = inputElements[i].value;
                       break;
                  }
            }

            var obj = {};
            obj.deliveryDay = $scope.deliveryDay=="Today"?$scope.todayTimes.date:$scope.tomorrowTimes.date;
            obj.deliveryTime = $scope.deliveryTime;
			var  isChecked = $("#placeToday").is(':checked');
			if(isChecked){
            obj.deliveryTime  = $scope.$eval("CurrentDate| date:'HH:mm'");
            obj.deliveryDay   = $scope.todayTimes.date;
			}
            if($scope.currentOrder.isExistingOrder && $scope.currentOrder.keepOriginalDeliveryTime){
                obj.deliveryTime = $scope.$eval("currentOrder.deliveryTime| date:'HH:mm'");
                obj.deliveryDateTime=$scope.$eval("currentOrder.deliveryTime| date:'MM-dd-yyyy HH:mm'");
            }
            if(checkedValue == "Paid"){obj.paidStatus = "Paid";}
            obj.orderSource = $scope.orderSource;
            obj.paymentMethod = $scope.paymentMethod;
            obj.instructions = $scope.instructions;
            obj.shouldPlaceOrder = flag;
            $modalInstance.close(obj);
            $scope.deliveryTime = undefined;
        };

        $scope.changeOrder = function() {
            $scope.placeOrder(false);
        };
    });

angular.module('app').controller('orderController', function ($rootScope, $scope, $filter, $interval, $http, $modal) {
    $scope.items = [];
    $scope.customizeAddOn=[];
    var data = new Array();
    
    $scope.selectMenu=function(menu){
        $scope.selectedMenu = menu;
       $.grep(menu.sections,function(section){
    	   $.grep(section.items,function(item){

    	   if(item.dishSize.length>0){
    		   $.grep(item.dishSize,function(size){
    			   item.dummyId=item.itemId+""+size.dishSizeId;
    			   item.price=size.price;
    			   item.dishSizeId =size.dishSizeId;
    			   item.dishSizeName = size.name;
    			  data.push(angular.copy(item));
    		   });
    	   }else{
    		  data.push(item);
    	   }
    	   });
       })
        $scope.showMenuSections = true;
        $scope.showMenuItems = false;
        $scope.showMenuItemsSizes =false;
        $scope.isCustomized=false;
    };

    $scope.hideMenu=function(){
	 $scope.showMenuSections = false;
     $scope.showMenuItems = false;
     $scope.showMenuItemsSizes =false;
 }
    $scope.selectSection=function(section){
    	$scope.selectedSection = section;
        $scope.showMenuItems = true;
        $scope.showMenuSections = false;
        $scope.showMenuItemsSizes =false;
        $scope.isCustomized =false;
    };

    $scope.selectSectionItem = function(section){
    	 $scope.selectedSection = section;
         $scope.showMenuItems = true;
         $scope.showMenuSections = false;
         $scope.showMenuItemsSizes =false;
          $scope.isCustomized =false;
    	}

       $scope.selectDishSize = function(item){
    	   var proceed=true;
    	if(item.dishSize.length>0){
    		$scope.dishSizeItem = item;
            $scope.showMenuItems = false;
            $scope.showMenuItemsSizes =true;
            proceed=false;
    	}

    	if(item.customizeLimits!='' && item.customizeLimits!='0' && item.customizeLimits!=null){
    		$scope.showMenuSections = false;
       		$scope.showMenuItems = false;
        	$scope.showMenuItemsSizes =false;
       		$scope.isCustomized=true;
			$scope.customizedItem = item;
			if(item.customizeLimits.length>0){
				$scope.custItemList=[];

				for(var i=0;i<item.customizeLimits.length;i++){
					$scope.custItem = {};
					$scope.custItem.custItemHadder = item.customizeLimits[i].type;
					$scope.custItem.addOnList=[];
					if(item.addOn.length>0){
						for(var j=0;j<item.addOn.length;j++){
							var resultAddOn;
							$.grep($scope.dishAddOn, function(currAddon){
								if(currAddon.addOnId==item.addOn[j]){
									if(item.customizeLimits[i].type==currAddon.addOnType){
									     istrue=false;
										 resultAddOn = currAddon;
									}
							}
    						});
							if(resultAddOn!=undefined){
									$scope.custItem.addOnList.push(resultAddOn);
									resultAddOn=undefined;
						}
					}
					}
					for(var k=0;k<item.customizeLimits[i].min;k++){
						$scope.custItem.sNo=item.customizeLimits[i].type+""+k;
						$scope.custItemList.push(angular.copy($scope.custItem));
					}
				}
			}
            proceed=false;
    	}
    	if(proceed){
    		item.$$hashKey=item.itemId;
    		$scope.addItemToOrder(item);
    	}
    }

     /*$scope.findAddon = function(id){
    	  var istrue=true;

    	  if(istrue){
    		  return false;
    	  }
      }*/
       
       var setDishStockCount= function(item){
       	var menus=$scope.menus;
       	for(var	i=0; i<menus.length;i++){
       	for(var j=0;j<menus[i].sections.length;j++){
       		for(var k=0; k<menus[i].sections[j].items.length;k++){
       	    		var stockCount=0;
       	    		if(menus[i].sections[j].items[k].manageStock && menus[i].sections[j].items[k].remainingStock.length>0 ){
	       	    		for(var l=0;l<menus[i].sections[j].items[k].remainingStock.length;l++){
	       	    		angular.forEach(menus[i].sections[j].items[k].remainingStock[l], function(value, key) {
	       	    			for(var m=0;m<$rootScope.restaurantFFCList.length;m++){
	       	    				if($rootScope.restaurantFFCList[m].id==key){
	       		    				stockCount+=value;
	       		    			}
	       		    		}
	       	    			});
	       	    		if(stockCount>0){
	       	    			menus[i].sections[j].items[k].stockCount=stockCount;
	       	    			menus[i].sections[j].items[k].actualStockCount=stockCount;
	       	    		}
       	    	}
       		}else if(menus[i].sections[j].items[k].manageStock && menus[i].sections[j].items[k].dishSize.length>0 ){
       			for(var n=0;n<menus[i].sections[j].items[k].dishSize.length;n++){
       	    			stockCount=0;
       	    			for(var l=0;l<menus[i].sections[j].items[k].dishSize[n].availableStock.length;l++){
    	       	    		angular.forEach(menus[i].sections[j].items[k].dishSize[n].availableStock[l], function(value, key) {
    	       	    			for(var m=0;m<$rootScope.restaurantFFCList.length;m++){
    	       	    				if($rootScope.restaurantFFCList[m].id==key){
    	       		    				stockCount+=value;
    	       		    			}
    	       		    		}
    	       	    			});
    	       	    		if(stockCount>0){
    	       	    			menus[i].sections[j].items[k].dishSize[n].stockCount=stockCount;
    	       	    			menus[i].sections[j].items[k].dishSize[n].actualStockCount=stockCount;
    	       	    		}
           	    	}
       	    		}
       		}
       		}
       	}
       }
       }
       
       $scope.submitCustomizeItem = function(item){
       	var instrucitons="";
       	for(var i=0;i<item.customizeLimits.length;i++){
       		for(var j=0;j<item.customizeLimits[i].min;j++){
       			var e = document.getElementById(item.customizeLimits[i].type+""+j);
				var dish = e.options[e.selectedIndex].value;
				instrucitons+=""+dish+",";
       		}
       	}
       	instrucitons = instrucitons.substr(0,instrucitons.length);
       	item.instructions=instrucitons;
        $scope.addItemToOrder(item,undefined,true);
       }

    $scope.addItemToOrder = function(item,dishSize,customizedDish){

        if($rootScope.currentOrder.items != undefined && $rootScope.currentOrder.items.length > 0 ) {
            var existingItem = $rootScope.currentOrder.items[0];
            var isItemOfSameMenu = $.grep($scope.selectedMenu.sections, function (section) {
                    return $.grep(section.items, function (secItem) {
                            return secItem.itemId == existingItem.itemId;
                        }).length > 0;
                }).length > 0;

            /*if(!isItemOfSameMenu){
                alert("Items from menu \""+$scope.selectedMenu.name +"\" can't be clubbed with other menus. Please place another order for this.");
                return false;
            }*/
        }

        var existingItem = $.grep($rootScope.currentOrder.items, function(currItem){
        	if(dishSize!=undefined){
        		return currItem.dishSizeId+""+currItem.itemId == dishSize.dishSizeId+""+item.itemId;
        	}else if(customizedDish){
        		return currItem.instructions == item.instructions;
        	}else{
        		return currItem.itemId == item.itemId;
        	}
        });
        item.dishSizeName = "";
        if(existingItem && !existingItem[0]) {
        	if(dishSize !=undefined){
        		item.dishSizeId = dishSize.dishSizeId;
        		item.price = dishSize.price;
        		item.dishSizeName = dishSize.name;
        	}
        	item.quantity = 1;
        	item.stockCount-=1;
        	if(item.dishSize.length>0) {
	        	for(var i=0;i<item.dishSize.length;i++){
	        		if(item.dishSizeId==item.dishSize[i].dishSizeId){
	        			item.dishSize[i].stockCount-=1;
	        		}
	        	}
        	}
    		$rootScope.currentOrder.items.push(angular.copy(item));
        	}
        else{
            $scope.increaseQuantity(existingItem[0]);
        }
        //$rootScope.updateOrderAmount();
        $rootScope.checkCouponScopeAndupdateOrderAmount();
    };

    var getDishStockReference = function(item){
    	var menus=$scope.menus;
    	for(var	i=0; i<menus.length;i++){
    	for(var j=0;j<menus[i].sections.length;j++){
    		for(var k=0; k<menus[i].sections[j].items.length;k++){
    			if(menus[i].sections[j].items[k].itemId==item.itemId && menus[i].sections[j].items[k].dishSize.length==0 ){
	    			if(menus[i].sections[j].items[k].stockCount!=undefined && menus[i].sections[j].items[k].actualStockCount>0){
	    				return menus[i].sections[j].items[k];
	    			}
    			}else if(menus[i].sections[j].items[k].dishSize.length>0){
    				for(var l=0; l<menus[i].sections[j].items[k].dishSize.length;l++){
    					if(menus[i].sections[j].items[k].itemId+""+menus[i].sections[j].items[k].dishSize[l].dishSizeId==item.dummyId){
    					if(menus[i].sections[j].items[k].dishSize[l].stockCount!=undefined && menus[i].sections[j].items[k].dishSize[l].actualStockCount>0){
    	    				return menus[i].sections[j].items[k];
    	    			}
    				}
    				}
    			}
    		}
    	}
    }
    }
    
    $scope.increaseQuantity = function(item){
        item.quantity = item.quantity + 1;
        var tempItem = getDishStockReference(item);
        console.log(tempItem);
        if(tempItem!=undefined){
	        if(tempItem.stockCount>0){
	        	tempItem.stockCount=tempItem.stockCount - 1;
	        }else if(tempItem.dishSize.length>0) {
	        	for(var i=0;i<tempItem.dishSize.length;i++){
	        		if(item.dishSizeId==tempItem.dishSize[i].dishSizeId){
	        			 if(tempItem.dishSize[i].stockCount>0){
	        				 tempItem.dishSize[i].stockCount=tempItem.dishSize[i].stockCount - 1;
	        			 }
	        		}
	        	}
	        }
        }
        $rootScope.checkCouponScopeAndupdateOrderAmount();
    };
    
    $scope.decreaseQuantity = function(item){
        if(item.quantity > 1){
           item.quantity = item.quantity - 1;
           if(item.manageStock){
            var tempItem = getDishStockReference(item);
            if(tempItem!=undefined){
	            if(tempItem.actualStockCount!=tempItem.stockCount && item.quantity < tempItem.actualStockCount && tempItem.stockCount!="NaN"){
	            	tempItem.stockCount+=1;
	            }else if(tempItem.dishSize.length>0) {
		        	for(var i=0;i<tempItem.dishSize.length;i++){
		        		if(item.dishSizeId==tempItem.dishSize[i].dishSizeId){
		        			 if(tempItem.dishSize[i].actualStockCount!=tempItem.dishSize[i].stockCount && item.quantity < tempItem.dishSize[i].actualStockCount){
		        				 tempItem.dishSize[i].stockCount+=1;
		        			 }
		        		}
		        	}
		        }
            }
        }
        }
       // $rootScope.updateOrderAmount();
        $rootScope.checkCouponScopeAndupdateOrderAmount();
    };
    $scope.deleteItemFromOrderList = function(item,dishSizeId){
    	if(item.manageStock){
	    	var tempItem = getDishStockReference(item);
	    	if( tempItem!=undefined && (Number(tempItem.stockCount) || tempItem.stockCount=="0")){
			    	if(tempItem.actualStockCount<item.quantity){
			        	tempItem.stockCount=tempItem.actualStockCount;
			        }else if(tempItem.actualStockCount>=(tempItem.stockCount+item.quantity)){
			        	tempItem.stockCount+=item.quantity;
			        }
	    	 }else if(tempItem!=undefined && tempItem.dishSize.length>0) {
	    		 for(var i=0;i<tempItem.dishSize.length;i++){
		        	if(item.dishSizeId==tempItem.dishSize[i].dishSizeId){
		        		if(tempItem.dishSize[i].actualStockCount<item.quantity){
				        	tempItem.dishSize[i].stockCount=tempItem.dishSize[i].actualStockCount;
				        }else if(tempItem.dishSize[i].actualStockCount>=(tempItem.dishSize[i].stockCount+item.quantity)){
				        	tempItem.dishSize[i].stockCount+=item.quantity;
				      }
		        	}
		        	}
		     }
    	}
    	item.quantity = undefined;
        item.instructions = undefined;
        for (var i = 0; i < $rootScope.currentOrder.items.length; i++) {
        	if(dishSizeId!=undefined){
        		if ($rootScope.currentOrder.items[i].itemId+""+$rootScope.currentOrder.items[i].dishSizeId == item.itemId+""+dishSizeId) {
                    $rootScope.currentOrder.items.splice(i, 1);
                    break;
        	}
        	}
        	else{
            if ($rootScope.currentOrder.items[i].itemId == item.itemId) {
                $rootScope.currentOrder.items.splice(i, 1);
                break;
            }
            }
        }
        $rootScope.checkCouponScopeAndupdateOrderAmount();
    };
    
    $scope.searchCustomer = function(){

            if($rootScope.currentOrder.user.phoneNo < 1000000000 || $rootScope.currentOrder.user.phoneNo > 9999999999 || isNaN($rootScope.currentOrder.user.phoneNo) ){
            $scope.userNotFoundMsg = "Please enter valid 10 digit phone number!";
            $rootScope.currentOrder.user.name = undefined;
            $rootScope.currentOrder.user.email = undefined;
            $rootScope.currentOrder.user.address = undefined;
            $rootScope.currentOrder.user.deliveryArea = "Select Delivery Area";
            $rootScope.currentOrder.user.customerId = -1;
            return;
        }
        $rootScope.countryCode = $("#countryCode option:selected").text();
		var phoneNumber= $rootScope.currentOrder.user.phoneNo;
		$rootScope.currentOrder.user.phoneNo = $rootScope.countryCode+''+$rootScope.currentOrder.user.phoneNo;
		$http.get($scope.searchCustomerApi+$rootScope.restaurantId+"&phone="+encodeURIComponent($rootScope.currentOrder.user.phoneNo)).then(function(response){
				$rootScope.currentOrder.user.phoneNo = phoneNumber;
            if(response.data.exactMatch && response.data.customerAddress.length > 0 ) {
                $scope.userNotFoundMsg = "";
                $rootScope.newUser="";
                var customer = response.data.customers[0];
                console.log(response.data.customers[0]);
                $rootScope.custCredit=customer.credit;

                console.log($rootScope.custCredit);
                if($rootScope.custCredit!==null){
                 $rootScope.cCreditBalance=$rootScope.custCredit.creditBalance
                $rootScope.billingType=customer.credit.creditType.billingCycle;
				}
                $rootScope.currentOrder.user.name = ((customer.firstName?customer.firstName:"") + ' ' + (customer.lastName?customer.lastName:"")).trim();
                $rootScope.currentOrder.user.email = customer.email;
                $rootScope.currentOrder.user.address = undefined;
                $rootScope.currentOrder.user.deliveryArea = "Select Delivery Area";
                $rootScope.currentOrder.user.customerId = customer.customerId;

                var modalInstance = $modal.open({
                    templateUrl: 'customerMultipleAddressModal',
                    controller: 'AddressInstanceCtrl',
                    resolve: {
                        addressList: function() {
                            return response.data.customerAddress.slice(0);
                        }
                    }
                });

                modalInstance.result.then(function(obj) {
                    if(obj != undefined){
                        $rootScope.currentOrder.user.address = obj.customerAddress;
                        $rootScope.currentOrder.user.deliveryArea = obj.deliveryArea;
                        $scope.updateDeliveryCharges();
                        $scope.runningOrderArray=[];
                    	$http.get("/CookedSpecially/order/getOrdersByType?orderType=new&orderType=pending&orderType=ready&orderType=outdelivery&ordersOfDay=today&ordersOfDay=future&restaurantId="
                				+ $rootScope.restaurantId)
                	.then(function successCallback(response) {
                		$scope.phoneNo=$rootScope.countryCode +$rootScope.currentOrder.user.phoneNo
                			for(var i=0;i<response.data.length;i++){
                				if(response.data[i].customerMobNo==$scope.phoneNo){
                					$scope.runningOrderArray.push(response.data[i]);
                					console.log($scope.runningOrderArray);
                					if($scope.runningOrderArray.length>0){
                						 $("#runningOverlay").show();
                	                        $("#runningOrderPopUp").show();
                					}
                				}
                			  }
                			}, 
                			function errorCallback(response) {
                				$scope.errorMsg = response.data;
                				console.log($scope.errorMsg);
                			});
                       
                        
                    }
                }, function() {
                });
            }
            else{
            	$rootScope.newUser="NewUser";
                $scope.userNotFoundMsg = "New User";
                $rootScope.currentOrder.user.name = undefined;
                $rootScope.currentOrder.user.email = undefined;
                $rootScope.currentOrder.user.address = undefined;
                $rootScope.currentOrder.user.deliveryArea = "Select Delivery Area";
                var customer = response.data.customers[0];
                $rootScope.currentOrder.user.customerId = customer.customerId;
            }
        });
		
        $scope.updateDeliveryCharges();
    };
    
    $scope.hideRunningOrder=function(){
    	$("#runningOverlay").hide();
        $("#runningOrderPopUp").hide();
    }

    $scope.updateDeliveryCharges = function(){
        var areas = $.grep($rootScope.allDeliveryAreas, function(area){ return area.name == $rootScope.currentOrder.user.deliveryArea });
        if(areas.length > 0){
        	 $scope.activeDeliveryAreaID = areas[0].id;
            $rootScope.currentOrder.deliveryCharges = areas[0].deliveryCharges;
        }
        else
            $rootScope.currentOrder.deliveryCharges = 0;

     //   $rootScope.updateOrderAmount();
        $rootScope.checkCouponScopeAndupdateOrderAmount();
    };

    $scope.waiveOffDeliveryCharges = function (flag){
        $rootScope.currentOrder.deliveryChargesWaivedOff = flag;
       // $rootScope.updateOrderAmount();
        $rootScope.checkCouponScopeAndupdateOrderAmount();
    };

    $scope.getTotalOrderAmount = function(){

        var total = 0;
        if($rootScope.currentOrder && $rootScope.currentOrder.items.length > 0)
            $.each($rootScope.currentOrder.items, function(){ total += this.quantity*this.price });
        if($rootScope.currentOrder!=undefined){	
        	$rootScope.currentOrder.deliveryChargesWaivedOff ? 0 : total += $rootScope.currentOrder.deliveryCharges;
        }
        return total;
    };

    $scope.addItemInstructions = function(item){

        var modalInstance = $modal.open({
            templateUrl: 'instructionModal',
            controller: 'ItemInstructionCtrl',
            resolve: {
                type: function() {
                    return 'dish';
                },
                instruction:function(){
                    return item.instructions;
                }
            }
        });

        modalInstance.result.then(function(instruction) {
            item.instructions = instruction;
        }, function() {
        });
    };

    $scope.isValidDateTime= function(value){
            var stamp = value.split(" ");
            var validDate = !/Invalid|NaN/.test(new Date(stamp[0]).toString());
            var validTime = /^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?$/i.test(stamp[1]);
            if(validDate && validTime){
            	return true;
            }else{
            	return false;
            }
    	}
    $scope.placeOrder = function(){
    	var paymentType;
    	
    	if ($rootScope.newUser==="NewUser") {
            	paymentType = $filter('filter')($scope.paymentType,'!CUSTOMER CREDIT');
            	$rootScope.currentOrder.keepOriginalPaymentMethod==true?$rootScope.currentOrder.paymentMethod:$rootScope.currentOrder.paymentMethod="COD";
            } else {
            	if($rootScope.custCredit===null){
            		paymentType = $filter('filter')($scope.paymentType,'!CUSTOMER CREDIT');
            		$rootScope.currentOrder.keepOriginalPaymentMethod==true?$rootScope.currentOrder.paymentMethod:$rootScope.currentOrder.paymentMethod="COD";
            	}
            	else if($rootScope.custCredit!==null &&$rootScope.billingType!=='ONE_OFF'){
            		paymentType=$scope.paymentType;
            		$rootScope.currentOrder.keepOriginalPaymentMethod==true?$rootScope.currentOrder.paymentMethod:$rootScope.currentOrder.paymentMethod="CUSTOMER CREDIT";
            	}else if($rootScope.billingType=='ONE_OFF'){
            		if($rootScope.custCredit.creditBalance<0){
        				if(-($rootScope.custCredit.creditBalance)>Number($rootScope.currentOrder.grandTotal.toFixed())){
        					paymentType = $filter('filter')($scope.paymentType,'CUSTOMER CREDIT');
        					$rootScope.currentOrder.keepOriginalPaymentMethod==true?$rootScope.currentOrder.paymentMethod:$rootScope.currentOrder.paymentMethod="CUSTOMER CREDIT";
        				}

        				else{
        					paymentType = $filter('filter')($scope.paymentType,'!CUSTOMER CREDIT');
            				$rootScope.currentOrder.keepOriginalPaymentMethod==true?$rootScope.currentOrder.paymentMethod:$rootScope.currentOrder.paymentMethod="COD";
        				}
            		}
            		else if($rootScope.currentOrder.keepOriginalPaymentMethod==true){
    					paymentType=$scope.paymentType;
    					$rootScope.currentOrder.keepOriginalPaymentMethod==true?$rootScope.currentOrder.paymentMethod:$rootScope.currentOrder.paymentMethod="COD";
    				}
            		else{
            				paymentType = $filter('filter')($scope.paymentType,'!CUSTOMER CREDIT');
            				$rootScope.currentOrder.keepOriginalPaymentMethod==true?$rootScope.currentOrder.paymentMethod:$rootScope.currentOrder.paymentMethod="COD";

            		}
            	}
            	else
            		{
            		paymentType = $filter('filter')($scope.paymentType,'!CUSTOMER CREDIT');
            		$rootScope.currentOrder.keepOriginalPaymentMethod==true?$rootScope.currentOrder.paymentMethod:$rootScope.selectedPaymentMethod="COD";
            		}

            }
        if($rootScope.currentOrder.items.length < 1 ){
            $scope.placeOrderErrMsg = "Please select at least one item";
            $scope.showPlaceOrderErr = true;
            return false;
        }
        else if($rootScope.currentOrder.user.customerId < 1 || $rootScope.currentOrder.user.phoneNo == undefined || $rootScope.currentOrder.user.name == undefined || $rootScope.currentOrder.user.name.trim() == "" ||
            $rootScope.currentOrder.user.address == undefined || $rootScope.currentOrder.user.deliveryArea == "Select Delivery Area"){
            $scope.placeOrderErrMsg = "Please enter customer details";
            $scope.showPlaceOrderErr = true;
            return false;
        }else{
            $scope.placeOrderErrMsg = "" ;
            $scope.showPlaceOrderErr = false ;
        }
        
        

        var modalInstance = $modal.open({
            templateUrl: 'placeOrderModal',
            controller: 'placeOrderCtrl',
            resolve: {
                paymentMethodList : function(){
                    return paymentType;
                },
                orderSourceList : function(){
                    return $scope.orderSources;
                },
                currentOrder : function(){
                    return $rootScope.currentOrder;
                }
            }
        });

        modalInstance.result.then(function(response) {
        	
            $rootScope.currentOrder.instructions = response.instructions;
            $rootScope.currentOrder.deliveryDay = response.deliveryDay;
            $rootScope.currentOrder.deliveryTime = response.deliveryTime;
            $rootScope.currentOrder.paymentMethod = response.paymentMethod;
            $rootScope.currentOrder.orderSource = response.orderSource;
            $rootScope.currentOrder.paidStatus = response.paidStatus;

            if( response.shouldPlaceOrder == 'true' || response.shouldPlaceOrder == true ) {

                $rootScope.currentOrder.deliveryCharges = $rootScope.currentOrder.deliveryChargesWaivedOff?0:$rootScope.currentOrder.deliveryCharges;
                $rootScope.currentOrder.discountAmount = $rootScope.currentOrder.finalDiscountFixValue;
                $rootScope.currentOrder.discountPercentage = Number($rootScope.currentOrder.finalDiscountPercValue.toFixed(2));
                $rootScope.currentOrder.finalOrderAmount = Number($rootScope.currentOrder.grandTotal.toFixed());

                if(response.deliveryDateTime!=undefined){
                	$rootScope.currentOrder.deliveryDateTime = response.deliveryDateTime;
                }
                else{
                	$rootScope.currentOrder.deliveryDateTime = $rootScope.currentOrder.deliveryDay + " "+$rootScope.currentOrder.deliveryTime;
                }
            	if(!$scope.isValidDateTime($rootScope.currentOrder.deliveryDateTime)){
            		alert("Delivery date time is not correct!."+$rootScope.currentOrder.deliveryDateTime);
            		return false;
            	}
            	
            	showOverlay(true);
                $("#place-order-message").get(0).innerHTML = "Placing order, Please wait...";
                $("#place-order-message").show();
            	
                var api = $scope.placeOrderApi;
                if($rootScope.currentOrder.isExistingOrder)
                    api = $scope.editOrderApi;

                var userDetails = $rootScope.currentOrder.user;
                delete $rootScope.currentOrder.deliveryDay;
                delete $rootScope.currentOrder.deliveryTime;
                delete $rootScope.currentOrder.user;
                delete $rootScope.currentOrder.finalDiscountFixValue;
                delete $rootScope.currentOrder.finalDiscountPercValue;
                delete $rootScope.currentOrder.grandTotal;
                applyTaxForDeliveryArea=true;

                $.each($rootScope.currentOrder.items,function(){
                    delete this.addOn;
                    delete this.alcoholic;
                    delete this.description;
                    delete this.disabled;
                    delete this.displayPrice;
                    delete this.restaurantId;
                    delete this.shortDescription;
                    delete this.userId;
                    delete this.vegetarian;
                    delete this.imageUrl;
                });
                var data = {};
                data.order = $rootScope.currentOrder;
                data.customer = {};
                data.customer.name = userDetails.name;
                if($rootScope.currentOrder.isExistingOrder){
                	data.customer.phone =userDetails.phoneNo;
                }else {
                data.customer.phone = $rootScope.countryCode+''+userDetails.phoneNo;
                }//
                data.customer.email = userDetails.email;
                data.customer.address = userDetails.address;
                data.customer.deliveryArea = userDetails.deliveryArea;
                data.customer.city = $rootScope.city;
                data.customer.id = userDetails.customerId;
				
                data.order.couponCode =[];
                if($rootScope.totalCouponAppliedList.length>0){
                	for(var i=0;i<$rootScope.totalCouponAppliedList.length;i++){
                		data.order.couponCode.push($rootScope.totalCouponAppliedList[i].couponCode);
                	}
                }
				 delete $rootScope.currentOrder.isExistingOrder;
                $http.post(api+$rootScope.restaurantId,data).then(function(resp){
                    if(resp.data.status=="error"){
                    	$("#place-order-message").get(0).innerHTML = resp.data.error+"<br/><br/>" +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
                        "<a href='#' onclick='$(\"#place-order-message\").hide();$(\".js-spin-overlay\").hide();'><b>OK</b></a>";

                    }else if(resp.data.status=="Failure"){
                    	$("#place-order-message").get(0).innerHTML = resp.data.error+"<br/><br/>" +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
                        "<a href='#' onclick='$(\"#place-order-message\").hide();$(\".js-spin-overlay\").hide();'><b>OK</b></a>";

                    }
                    else{
                    	showOverlay(true);
                    	$("#place-order-message").get(0).innerHTML = "ORDER PLACED!!<br/><br/>" +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
                        "<a href='#' onclick='$(\"#place-order-message\").hide();$(\".js-spin-overlay\").hide();'><b>OK</b></a>";
                    	fetchMenu();
                    }
                    resetItemInstructions(data.order.items);
                    $rootScope.currentOrder.deliveryCharges=0;
                    var count=0;
                    delete $rootScope.orderTaxList;
                    /* if($rootScope.orderTaxList.length>0){
                      	 $.each($rootScope.orderTaxList, function(){
               				 $rootScope.orderTaxList[count].value =0 ;
               				 count++;
               			 });
                      	}*/
                    $scope.default();

                },function(err){
                    //error call back
                    $("#place-order-message").get(0).innerHTML = "Oh ho, Something went wrong!! Please report to IT.<br/><br/><a href='#' onclick='$(\"#place-order-message\").hide();$(\".js-spin-overlay\").hide();'><b>OK</b></a>";
                    showOverlay(true);
                    console.log("Error...");
                    console.log(err);
                });
            }
        }, function(response) {
            //do nothing
        });
        return false;
    };

    $scope.default = function(){
       defaultSettings();
        if($scope.menus ){
            if($scope.menus.length == 1) {
                $scope.showMenuSections = true;
                $scope.showMenuItemsSizes =false;
                $scope.selectedMenu = $scope.menus[0];
            }
            resetItemInstructions($rootScope.currentOrder.items);
        }

    };

    var resetItemInstructions = function(items){
        if(items != undefined && items.length > 0 ){
            $.each(items,function(){
                this.instructions = "";
            });
        }
        else {
            $.each($scope.menus, function(){
                $.each(this.sections, function(){
                    $.each(this.items, function () {
                        this.instructions = "";
                    });
                });
            });
        }
    };
    $rootScope.checkCouponScopeAndupdateOrderAmount = function(){
    	 $rootScope.updateOrderAmount();
    	 if($rootScope.totalCouponAppliedList.length>0){
    		 	for(var j=0;j<$rootScope.totalCouponAppliedList.length;j++){
    		 		var minAmount = 0;
	    		 //	var errorMessage=coupon.error;
	    		 if($rootScope.totalCouponAppliedList[j].flatRules==undefined){
	    		 	discount = $rootScope.totalCouponAppliedList[j].rules.discountValue;
	    		 	minAmount = $rootScope.totalCouponAppliedList[j].rules.minOrderPayment;
	    		 }else if($rootScope.totalCouponAppliedList[j].rules==undefined){
	    			 discount = $rootScope.totalCouponAppliedList[j].flatRules.discountValue;
	    			 minAmount = $rootScope.totalCouponAppliedList[j].flatRules.minOrderPayment;
	    		 }
    		 	 if(!(($scope.getTotalOrderAmount())>=minAmount)){
    		 			alert("Your order Order Amount is less then the minimum amount required for the coupon, to apply this coupon amount should be greater then or equals to "+minAmount);
    		 			$scope.removeCouponCode($rootScope.totalCouponAppliedList[j]);
    		 		}
    		 	}
    	 }
    }
    
    $rootScope.updateOrderAmountAfterStockChange = function(item){
    	var tempItem = getDishStockReference(item);
    	if((Number(tempItem.stockCount)|| tempItem.stockCount=="0")&& tempItem!=undefined){
	    	if(tempItem.actualStockCount<item.quantity){
	        	tempItem.stockCount=0;
	        }else if(tempItem.actualStockCount>=item.quantity){
	        	tempItem.stockCount=tempItem.actualStockCount;
	        	tempItem.stockCount-=item.quantity;
	        }
    	}else if(tempItem!=undefined && tempItem.dishSize.length>0) {
   		 for(var i=0;i<tempItem.dishSize.length;i++){
	        	if(item.dishSizeId==tempItem.dishSize[i].dishSizeId){
	        		if(tempItem.dishSize[i].actualStockCount<item.quantity){
			        	tempItem.dishSize[i].stockCount=0;
			        }else if(tempItem.dishSize[i].actualStockCount>=item.quantity){
			        	tempItem.dishSize[i].stockCount=tempItem.dishSize[i].actualStockCount;
			        	tempItem.dishSize[i].stockCount-=item.quantity;
			        	
			      }
	        	}
	        	}
	     }
    	$rootScope.updateOrderAmount();
    }
    
    $rootScope.updateOrderAmount = function () {
    	var applyTaxForDeliveryArea=true;
    	var dupName="empty";
    	var flag=true;
        var subTotal = $scope.getTotalOrderAmount();
        var taxAmt = 0;
        var  counter = 0;
        var specialTaxVal;
        var specialTaxName;
        var specialTaxCharge;
        var discAmount=0;
        var count = 0;
        $scope.couponAppliedList={}
        $rootScope.couponAppliedListArray = [];
        $rootScope.couponSum=0;
        $rootScope.orderTaxList= [];
        $scope.discountList = {};
        $rootScope.orderDiscountList= [];
        
        if($rootScope.currentOrder.couponApplied!=null && $rootScope.currentOrder.couponApplied.length>0){
        	var notDup=true;
        	for(var i=0;i<$rootScope.currentOrder.couponApplied.length;i++){
        		for(var j=0;j<$rootScope.totalCouponAppliedList.length;j++){
        			if($rootScope.totalCouponAppliedList[j].couponCode==$rootScope.currentOrder.couponApplied[i].couponCode){
        				notDup=false;
        				continue;
        			}
        		}
        		if(notDup){
	    			var coup = $rootScope.currentOrder.couponApplied[i];
        			$rootScope.totalCouponAppliedList.push(coup);
        	}
        	}
        }
        $.each($rootScope.currentOrder.items,function(){
        	var item = this;
        	var tempTotal = subTotal;
        	var itemPrice=item.price*item.quantity;
        	if($rootScope.totalCouponAppliedList.length>0){
        		itemPrice  = $scope.applyCoupon(item,$rootScope.totalCouponAppliedList,subTotal)*item.quantity;
        	}else{
        		itemPrice=item.price*item.quantity;
        	}
        	

          $.each($rootScope.currentOrder.discountList, function(){
        	     var discountAmt = 0;
                 var discount = this;

                 if(discount.type === 'PERCENTAGE') {
                     discountAmt = (discount.value*itemPrice) / 100;
                 }
                 else if(discount.type === 'ABSOLUTE') {
                	 var percentage = (discount.value/subTotal)*100;
                	 discountAmt = (percentage * itemPrice) / 100;
                     //discountAmt = Number(discount.value);
                 }
                 var disAg=0
                 if($rootScope.orderDiscountList.length>0){
                	 var disCount=0;
                	 $.each($rootScope.orderDiscountList ,function(){
                	 if($rootScope.orderDiscountList[disCount].name==discount.name && $rootScope.orderDiscountList[disCount].type == discount.type ){
                		 $rootScope.orderDiscountList[disCount].value += discountAmt;
                		 disAg++;
                	 }
                	 disCount++;
                	 });
                 }
                 if(disAg!=1){
                	 $scope.discountList.name = discount.name;
                	 $scope.discountList.value = discountAmt;
                	 $scope.discountList.type = discount.type;
                	 $scope.discountList.rawVal = discount.value;
	                 $rootScope.orderDiscountList.push(angular.copy($scope.discountList));
                 }
                 itemPrice -=discountAmt;
                 tempTotal -= discountAmt;
                 discAmount +=discountAmt;
                 discount.amountForCurrentOrder = discAmount;
             });

          $rootScope.currentOrder.finalDiscountFixValue = discAmount;
          $rootScope.currentOrder.finalDiscountPercValue = ((discAmount)*100)/subTotal;
          $rootScope.currentOrder.totalOrderAmountAfterDiscount =subTotal - discAmount - $rootScope.couponSum;

        $.each($rootScope.taxList ,function(){
        	 var specialTaxAmount =0;
             var defaultTaxAmount =0;
        	counter=0;
            var tax = this;
             if(tax.dishType=="Default"){
            	 $.each($rootScope.taxList ,function(){
            		 var compTax = this;
            		 if(compTax.dishType==item.itemType){
            			 if(tax.taxTypeId == compTax.overridden){
                            specialTaxVal = compTax.taxValue;
                            specialTaxCharge = compTax.chargeType;
                            specialTaxName = compTax.name;
                            counter++;
            		 }
            		 }
            	 });
            	 if(counter!=1){
            		 if(applyTaxForDeliveryArea && $rootScope.currentOrder.deliveryCharges!=0){
            			 applyTaxForDeliveryArea=false;
            			 $rootScope.currentOrder.deliveryChargesWaivedOff ? false : itemPrice += $rootScope.currentOrder.deliveryCharges;
            		 }
            		 defaultTaxAmount  = Number(tax.chargeType=="PERCENTAGE"?(itemPrice*tax.taxValue/100):tax.taxValue*item.quantity);
            		 $scope.defaultTax.name=tax.name;
            		 $scope.defaultTax.value=defaultTaxAmount ;
            		 var count =0;
            		 var countAg=0;
            		 $.each($rootScope.orderTaxList,function() {
            			 if($rootScope.orderTaxList[count].name==tax.name){
            				 $rootScope.orderTaxList[count].value +=defaultTaxAmount ;
            				 countAg++;
            			 }
            			 count++;
            		 });
            		 if(countAg==0){
            			 $rootScope.orderTaxList.push(angular.copy($scope.defaultTax));
            		}
            	 }else if(counter==1) {
            		 specialTaxAmount = Number(specialTaxCharge=="PERCENTAGE"?(itemPrice*specialTaxVal/100):specialTaxVal*item.quantity);
            		 $scope.specialTax.name=specialTaxName;
            		 $scope.specialTax.value=specialTaxAmount ;
            		 var count =0;
            		 var countAg=0;
            		 $.each($rootScope.orderTaxList,function() {
            			 if($rootScope.orderTaxList[count].name==specialTaxName){
            				 $rootScope.orderTaxList[count].value +=specialTaxAmount ;
            				 countAg++;
            			 }
            			 count++;
            		 });
            		 if(countAg==0){
            			 $rootScope.orderTaxList.push(angular.copy($scope.specialTax));
            		}
            		 if(applyTaxForDeliveryArea && $rootScope.currentOrder.deliveryCharges!=0){
            			 applyTaxForDeliveryArea=false;
            			 if(!$rootScope.currentOrder.deliveryChargesWaivedOff){;
	            			 defaultTaxAmount  = Number(tax.chargeType=="PERCENTAGE"?($rootScope.currentOrder.deliveryCharges*tax.taxValue/100):tax.taxValue*item.quantity);
	                		 $scope.defaultTax.name=tax.name;
	                		 $scope.defaultTax.value=defaultTaxAmount;
	                		 $rootScope.orderTaxList.push(angular.copy($scope.defaultTax));
            			 }
            		 }
            		 
            	 }
            }
            });
        counter=0;
        }) ;
        var taxAm =0;
        var i=0;
        $.each($rootScope.orderTaxList,function() {
			 taxAm +=$rootScope.orderTaxList[i].value;
			 i++;
		 });
        if($rootScope.currentOrder.items.length==0){
        	$rootScope.currentOrder.totalOrderAmountAfterDiscount=0;
        }
        $rootScope.currentOrder.taxAmount =taxAm;
        $rootScope.currentOrder.grandTotal = $rootScope.currentOrder.totalOrderAmountAfterDiscount +taxAm;
        //$rootScope.currentOrder.deliveryChargesWaivedOff ? false : $rootScope.currentOrder.grandTotal += $rootScope.currentOrder.deliveryCharges;
    };
    
    $scope.addCoupon = function(couponCode){
    	var data = {};
    	 	data.restaurantID = $rootScope.restaurantId;  
            data.customerId= $rootScope.currentOrder.user.customerId;
  			data.couponCode = couponCode;  
            data.orderSource = "POS";
            data.deliveryAreaId=$scope.activeDeliveryAreaID;
            if($rootScope.currentOrder.grandTotal==0 || $rootScope.currentOrder.grandTotal==undefined){
            	alert("Please add items to cart");
            	return false;
            }
            if($rootScope.totalCouponAppliedList.length>=1){
            	alert("You can apply only one coupon for now!");
            	return false;
            }
            data.OrderAmount= $scope.getTotalOrderAmount();
  			if($rootScope.totalCouponAppliedList.length>0){
  				
  				for(var i=0;i<$rootScope.totalCouponAppliedList.length;i++){
  					if($rootScope.totalCouponAppliedList[i].couponCode==couponCode){
						alert("Coupon code already applied !!");
						return false;
					}
  				}
  			}
   		 	$http.post($scope.validateCouponApi,data).then(function(resp){
                  if(resp.data.isValid){
                	  if(resp.data.isCouponApplicable){
                		  var minAmount=0;
                		   if(resp.data.flatRules==undefined){
	    		 				minAmount = resp.data.rules.minOrderPayment;
	    		 			}else if(resp.data.rules==undefined){
	    			 			minAmount = resp.data.flatRules.minOrderPayment;
	    		 			}
                		  if($scope.getTotalOrderAmount()>=minAmount){
                		  	$rootScope.totalCouponAppliedList.push(resp.data);
                		  	$rootScope.updateOrderAmount();
                		  }else{
                			 alert(resp.data.error);
                		  }
                	  }else {
                		  alert(resp.data.error);
                	  }
                  }else{
                	  alert(resp.data.error);
                  }
                  
                },function(err){
                    //error call back
                   
                    console.log("Error...");
                    console.log(err);
                });
    
   	}
    
    
    
     $scope.applyCoupon=function(item,couponList,subTotal){
    	 var itemPrice=item.price;
    	 var couponAmount=0;
    	 for(var i=0;i<couponList.length;i++){
    		  var coupon = couponList[i];
	    	 if(coupon.flatRules==undefined && coupon.rules.isAbsoluteDiscount){
	    		 
	    	 }else if(coupon.rules==undefined && coupon.flatRules.isAbsoluteDiscount){
	    		 
	    	 }
	    	 else{
	    		 var discount =0;
	    		 var minAmount = 0;
	    		 var errorMessage=coupon.error;
	    		 if(coupon.flatRules==undefined){
	    		 	discount = coupon.rules.discountValue;
	    		 	minAmount = coupon.rules.minOrderPayment;
	    		 }else if(coupon.rules==undefined){
	    			 discount = coupon.flatRules.discountValue;
	    			 minAmount = coupon.flatRules.minOrderPayment;
	    		 }
	    		 couponAmount=0;
		    		 couponAmount =Number((itemPrice*discount)/100);
		    		 itemPrice -= couponAmount;
		    		 if($rootScope.couponAppliedListArray.length>0){
		    			 var noCouponFound=true;
		    			for(var i=0;i<$rootScope.couponAppliedListArray.length;i++){
		    				 var coup = $rootScope.couponAppliedListArray[i];
		    				  if(coup.couponName == coupon.couponName){
		    					  coup.amount += Number((couponAmount*item.quantity));
		    					  $rootScope.couponSum+=Number((couponAmount*item.quantity));;
		    					  noCouponFound=false;
		    				  }
		    			  }
		    			  if(noCouponFound){
		    				  $scope.couponAppliedList.couponName=coupon.couponName;
		    				  $scope.couponAppliedList.couponCode=coupon.couponCode;
		    			 	  $scope.couponAppliedList.amount= Number(couponAmount*item.quantity);
		    			 	  $rootScope.couponSum +=$scope.couponAppliedList.amount;
		    			 	  
		    			 	  $rootScope.couponAppliedListArray.push(couponAppliedList);
		    			  }
		    		 }else{
		    			 $scope.couponAppliedList.couponName=coupon.couponName;
		    			 $scope.couponAppliedList.couponCode=coupon.couponCode;
		    			 $scope.couponAppliedList.amount= Number(couponAmount*item.quantity);
		    			 $rootScope.couponSum=$scope.couponAppliedList.amount;
		    			 $rootScope.couponAppliedListArray.push($scope.couponAppliedList);
		    		 }
		    		
		    		 
		    	 }
    	 }
    	 return itemPrice;
     }
    
    var dupName = "empty";
    $scope.addDiscount=function(discounts){
    	var flag =true;

    	if(discounts.name==undefined){
         	  discounts.name = "discounts";
    	}
    	dupName = discounts.name;
    	$.each($rootScope.currentOrder.discountList, function(){
            var discount = this;
            if(dupName==discount.name){
	           	 alert("Duplicate discount name!. Please add unique name to apply different discount.");
	           	 flag=false;
    	    }
    	});
    	if(flag){
        $rootScope.currentOrder.discountList.push(discounts);
        $scope.updateAvailableDiscounts();

       // $rootScope.updateOrderAmount();
        $rootScope.checkCouponScopeAndupdateOrderAmount();
    	}
    };
    $scope.addOtherDisc = function(otherDiscName,otherDiscValue,otherDiscType){
        if(otherDiscName == "" || isNaN(otherDiscValue))
            return false;

        var discount = {id:(new Date()).getTime(),name:otherDiscName,type:otherDiscType,value:otherDiscValue};
        $scope.addDiscount(discount);

        //$scope.otherDiscName = undefined;
       // $scope.otherDiscType = "PERCENTAGE";
        //$scope.otherDiscValue = undefined;
    };

    $scope.updateAvailableDiscounts = function(){
        var temp=$scope.allDiscounts.slice(0);

        for(var i=0; i< $rootScope.currentOrder.discountList.length; i++){
            temp = removeDiscountFromArray(temp,$rootScope.currentOrder.discountList[i]);
        }

        $scope.availableDiscounts = temp;
    };

    $scope.removeDiscount = function(discount){
    	removeDiscountFromArray($rootScope.currentOrder.discountList,discount);
        $scope.updateAvailableDiscounts();
        //$rootScope.updateOrderAmount();
        $rootScope.checkCouponScopeAndupdateOrderAmount();
    };

    function removeDiscountFromArray(arr,item){
        for (var i = 0; i < arr.length; i++) {
            if (arr[i].name == item.name && arr[i].type ==item.type ) {
                arr.splice(i, 1);
                break;
            }
        }
        return arr;
    }
    
    $scope.removeCouponCode = function(coupon){
       $rootScope.totalCouponAppliedList =  removeCouponCodeFromArray($rootScope.totalCouponAppliedList,coupon);
       if($rootScope.currentOrder.couponApplied!=null && $rootScope.currentOrder.couponApplied.length>0){
       	$rootScope.currentOrder.couponApplied = removeCouponCodeFromArray($rootScope.currentOrder.couponApplied,coupon);
       }
  	   $rootScope.updateOrderAmount();
    }
   function  removeCouponCodeFromArray(arr,coupon){
     for (var i = 0; i < arr.length; i++) {
            if (arr[i].couponCode == coupon.couponCode) {
                arr.splice(i, 1);
                break;
            }
        }
        return arr;
    }

    function removeItemFromArray(arr,item){
        for (var i = 0; i < arr.length; i++) {
            if (arr[i].id == item.id) {
                arr.splice(i, 1);
                break;
            }
        }
        return arr;
    }

    $scope.$on('$destroy', function(){
    	$(".spinOverlay").show();
        showSessionExpiredMessage(true);
        resetPromise(false);
    });

    $scope.allDiscounts = [];
    $rootScope.showAllOrders = false;
    $rootScope.showCustomerProfile=false;
    $rootScope.showPos=true;

    $scope.statusApi = "/CookedSpecially/customer/isSessionValid";
    $scope.menuApi =  "/CookedSpecially/menu/getallposmenusjson/";
    $scope.deliveryAreaApi =  "/CookedSpecially/restaurant/getDeliveryAreas?pos=true&restaurantId=";
    $scope.searchCustomerApi = "/CookedSpecially/customer/getCustomerInfo.json?restaurantId=";
    $scope.placeOrderApi = "/CookedSpecially/order/placeOrderFromPos?restaurantId=";
    $scope.discountInfoApi = "/CookedSpecially/restaurant/getDiscountOptions?restaurantId=";
    $scope.organizationInfoApi = "/CookedSpecially/organization/getOrganizationInfo";
    $scope.employeeDetailsApi = "/CookedSpecially/user/getEmployeeDetails";
    $scope.editOrderApi = "/CookedSpecially/order/editOrder?restaurantId=";
    $scope.restaurantInfoApi = "/CookedSpecially/restaurant/getrestaurantinfo";
    $scope.validateCouponApi = "/CookedSpecially/coupon/getCouponDef.json?";
    function fetchEmployeeDetails(){
        
    	
        $http.get($scope.employeeDetailsApi).then(function(res){
        	//showOverlay(true);
            var emp = res.data;
            $scope.organizationName=emp.orgName;
			$rootScope.orgId=res.data.orgId;
			$rootScope.userRole=emp.role;
            $rootScope.user = emp;
            if($rootScope.user.restaurantList.length>1 && $rootScope.userRole=="Call_Center_Associate"){
            	$scope.selectRestaurant();
            }
            else{
            	$http.get($scope.restaurantInfoApi).then(function(response){
                    var data = response.data;
                    console.log(data);
                    $rootScope.restaurantId = data.restaurantId;
                    console.log($rootScope.restaurantId);
                    $scope.restaurantDetails(data);
                   // $scope.city = data.city;
                   
                    //$scope.restaurantName = data.restaurantName;
                   // $scope.currency = data.currency;
                   // $scope.fulfillmentCenterList = data.fulfillmentCenter;
                });
            	//$rootScope.restaurantId = $rootScope.user.restaurantList[0].restaurantId;
            	//var restaurant=$rootScope.user.restaurantList[0];
            	
            }
           // showOverlay(false);
        });
    }
    $scope.restaurantDetails=function(restaurant){
        $rootScope.city = restaurant.city;
        $rootScope.restaurantName = restaurant.restaurantName;
        $rootScope.currency = restaurant.currency;
        $rootScope.restCountryCode =restaurant.countryCode;
        $rootScope.taxList = restaurant.taxList;
        $rootScope.timeZone=restaurant.timeZone;
        $rootScope.restaurantFFCList=restaurant.fulfillmentCenter;
        $scope.menus = undefined;
        fetchMenu();
        fetchDeliveryArea();
        fetchDiscounts();
        $scope.default();
    }
    $scope.selectRestaurant = function(){
        showOverlay(false);
        showSpinner(false);
        var restaurantList = $rootScope.user.restaurantList;

        var modalInstance = $modal.open({
            templateUrl: 'selectRestaurantModal',
            controller: 'selectRestaurantCtrl',
            backdrop: 'static',
            resolve: {
                restaurantList: function(){ return restaurantList; },
                name : function(){ return $rootScope.user.orgName; }
            }
        });

        modalInstance.result.then(function(restaurantId) {
            showSpinner(true);
            showOverlay(true);
            $rootScope.restaurantId = restaurantId;
            var restaurant = $.grep(restaurantList, function(rest){ return rest.restaurantId == restaurantId; })[0];
            $scope.restaurantDetails(restaurant);
            showSpinner(false);
            showOverlay(false);
        }, function(err) {
            //do nothing
        });
    };

    function fetchOrganizationalDetails() {

        var fetchOrganizationInfo = $http.get($scope.organizationInfoApi).then(function (response) {
            return response.data;
        });

        fetchOrganizationInfo.then(function (response) {
            $scope.orderSources = response.orderSource;
            $scope.paymentType = response.paymentType;
        });
    }

    var defaultSettings = function(){

        $scope.showMenuSections = false;
        $scope.showMenuItems = false;
        $scope.showMenuItemsSizes =false;
        $rootScope.cCreditBalance="";
        $rootScope.currentOrder = {};
        $rootScope.currentOrder.instructions = "";
        $rootScope.currentOrder.deliveryDay = "Today";
        $rootScope.currentOrder.deliveryTime = undefined;
        $rootScope.currentOrder.deliveryDateTime=undefined;
        $rootScope.currentOrder.paymentMethod = "COD";
        $rootScope.currentOrder.orderSource = "POS";
        $rootScope.currentOrder.items = [];
        $rootScope.currentOrder.itemSize = [];
        $rootScope.currentOrder.user = {};
        $rootScope.currentOrder.user.phoneNo = undefined;
        $rootScope.currentOrder.user.name = undefined;
        $rootScope.currentOrder.user.email = undefined;
        $rootScope.currentOrder.user.address = undefined;
        $rootScope.currentOrder.user.deliveryArea = "Select Delivery Area";
        $rootScope.currentOrder.user.customerId = -1;
        $rootScope.currentOrder.deliveryCharges = 0;
        $rootScope.currentOrder.deliveryChargesWaivedOff = false;
        $rootScope.currentOrder.taxAmount = 0;
        $rootScope.currentOrder.discountList = [];
        $rootScope.currentOrder.finalDiscountPercValue = 0;
        $rootScope.currentOrder.finalDiscountFixValue = 0;
        $rootScope.currentOrder.totalOrderAmountAfterDiscount = 0 ;
        $rootScope.currentOrder.grandTotal = 0;
        $rootScope.currentOrder.subTotal =0;
		$rootScope.countryCode=0;
        $scope.showPlaceOrderErr = false;
        $scope.placeOrderErrMsg = "";
        $scope.userNotFoundMsg = "";
        $scope.todayTimes = {};
        $scope.tomorrowTimes = {};
        $scope.custItem = {};
        $scope.availableDiscounts = $scope.allDiscounts.slice(0);
        $scope.otherDiscName = undefined;
        $scope.otherDiscType = "PERCENTAGE";
        $scope.otherDiscValue = undefined;
        $scope.defaultTax={};
        $scope.specialTax={};
        $scope.discountList={}
        $rootScope.orderTaxList= [];
        $rootScope.orderDiscountList= [];
        $scope.couponAppliedList={}
        $rootScope.couponAppliedListArray = [];
        $rootScope.totalCouponAppliedList= [];
        $rootScope.couponSum=0;
        
    };

    fetchOrganizationalDetails();
    fetchEmployeeDetails();

    var fetchMenu = function(){

        $http.get($scope.menuApi+$rootScope.restaurantId).then(function(response){
            var data = response.data;
            $scope.currency = data.currency;
            $scope.dishAddOn = data.dishAddOn;
            $scope.menus = data.menus;
        	setDishStockCount();
            if($scope.menus.length == 1){
                $scope.showMenuSections = true;
                $scope.selectedMenu = $scope.menus[0];
            }
            showSpinner(false);
            showOverlay(false);
        });
    };

    var fetchDeliveryArea = function(){
        $http.get($scope.deliveryAreaApi+$rootScope.restaurantId).then(function(response){
            $rootScope.allDeliveryAreas = response.data;
        });
    };

    var fetchDiscounts = function(){
        $http.get($scope.discountInfoApi+$rootScope.restaurantId).then(function(response){
            if(response.data.length > 0){
                $scope.allDiscounts  = response.data;
                $.each($scope.allDiscounts,function(){
                    delete this.restaurantId;
                });
                $scope.availableDiscounts = $scope.allDiscounts.slice(0);
            }
        });
    };

    var checkStatus = function(){
        $http.get($scope.statusApi).then(function(response){
        	console.log(response.data);
            if(response.data.valid == false){
            	$(".spinOverlay").show();
                showSessionExpiredMessage(true);
                resetPromise(false);
            } else{
            	$(".spinOverlay").hide();
                showSessionExpiredMessage(false);
            }
        },function(err){
            resetPromise(false);
            $(".spinOverlay").show();
            showSessionExpiredMessage(true);
        });
    };

    var promise =  $interval(checkStatus,10000);

    function resetPromise(restart){
        if (angular.isDefined(promise)) {
            $interval.cancel(promise);
            promise = undefined;
        }
        if(restart)
            promise =  $interval(checkStatus,10000);
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

    $scope.showPosScreen=function(){
    	$rootScope.showAllOrders = false;
        $rootScope.showCustomerProfile=false;
        $rootScope.showPos=true;
    }
    $scope.showAllOrderScreen=function(){
    	$rootScope.showAllOrders = true;
        $rootScope.showCustomerProfile=false;
        $rootScope.showPos=false;
    }
    $scope.showCustomerMamagement=function(){
    	$rootScope.showAllOrders = false;
        $rootScope.showCustomerProfile=true;
        $rootScope.showPos=false;
    }


});