angular.module('app', [ 'ngAnimate', 'ui.bootstrap' ]);
angular.module('app').directive('validNumber', function() {
  return {
    require: '?ngModel',
    link: function(scope, element, attrs, ngModelCtrl) {
      if(!ngModelCtrl) {
        return; 
      }

      ngModelCtrl.$parsers.push(function(val) {
        if (angular.isUndefined(val)) {
            var val = '';
        }
        
          var clean = val.replace(/[^0-9]/g, '');
                                                  if (val !== clean) {
          ngModelCtrl.$setViewValue(clean);
          ngModelCtrl.$render();
        }
        return clean;
      });

      element.bind('keypress', function(event) {
        if(event.keyCode === 32) {
          event.preventDefault();
        }
      });
    }
  };
});

angular.isUndefinedOrNull = function(val) {
  return angular.isUndefined(val) || val === null 
}

angular.module('app').directive('decimalNumber', function() {
 return {
    require: '?ngModel',
    link: function(scope, element, attrs, ngModelCtrl) {
    if(!ngModelCtrl) {
       return; 
    }
    ngModelCtrl.$parsers.push(function(val) {
     if (angular.isUndefined(val)) {
       var val = '';
     }
     var clean = val.replace(/[^0-9\.]/g, '');
     var negativeCheck = clean.split('-');
     var decimalCheck = clean.split('.');
     if(!angular.isUndefined(negativeCheck[1])) {
       negativeCheck[1] = negativeCheck[1].slice(0, negativeCheck[1].length);
       clean =negativeCheck[0] + '-' + negativeCheck[1];
       if(negativeCheck[0].length > 0) {
          clean =negativeCheck[0];
       }
     }
     if(!angular.isUndefined(decimalCheck[1])) {
       decimalCheck[1] = decimalCheck[1].slice(0,2);
       clean =decimalCheck[0] + '.' + decimalCheck[1];
     }
     if (val !== clean) {
       ngModelCtrl.$setViewValue(clean);
       ngModelCtrl.$render();
     }
     return clean;
   });
   element.bind('keypress', function(event) {
    if(event.keyCode === 32) {
     event.preventDefault();
    }
  });
 }
};
});

angular.module('app').controller('SelectSaleRegisterCtrl',function ($scope, $modalInstance,$rootScope, saleRegisterList ,$http) {
$scope.saleRegisterList = saleRegisterList;
$scope.selectSaleRegister=function(saleRegister){
   $modalInstance.close(saleRegister);
}
$rootScope.closeModal=function()
{
         $modalInstance.close();
}
});

angular.module('app').controller('ModalInstanceCtrl',function($scope,$rootScope, $modalInstance, order, deliveryBoyList,paymentMethodList) {
    $scope.showChangePaymentMethodOption = true;
    $scope.order = order;
    $scope.orderAmount = order.orderAmount;
    $scope.creditBalance=order.creditBalance;
    $scope.paymentMethodList = paymentMethodList;
    $scope.updatedPaymentMethod = paymentMethodList[0].name;
    $scope.deliveryBoyList = deliveryBoyList.data.deliveryBoy;
    $scope.paymentMethod = order.paymentMethod;
    $scope.deliveryAmt = 0;
    if($rootScope.currencyGlob=="INR"){
      $scope.deliveryAmt1 = 100 - ((order.orderAmount+order.creditBalance) % 100);
      $scope.deliveryAmt2 = 500 - ((order.orderAmount+order.creditBalance) % 500);
      $scope.deliveryAmt3 = 2000 - ((order.orderAmount+order.creditBalance) % 2000);
    if ($scope.paymentMethod == 'COD' && ($scope.orderAmount+$scope.creditBalance)!=0 && ($scope.orderAmount%2000)!==0){
      $scope.deliveryAmt = $scope.deliveryAmt3;
    }
    }
    
    $scope.getOrderPaymentMethodType = function(order) {
      var reqMethod = order.paymentMethod;
      if (!$scope.showChangePaymentMethodOption)
        reqMethod = $scope.updatedPaymentMethod;
      var method = $.grep($scope.paymentMethodList, function(type) {
        return type.name == reqMethod
      });
      return method[0] && method[0].type === "CASH";
    };

    $scope.setDeliveryAmt = function(amt, changeBox) {
      $scope.deliveryAmt = amt;
      $(".delivery-amt").each(function() {
        $(this).removeClass('delivery-amt-selected')
      });
      $("#" + changeBox).addClass('delivery-amt-selected');
    };

    $scope.disableDispatchBtn = function() {
      var flag1 = $scope.deliveryAmt >= 0;
      var flag2 = $scope.deliveryAgent != undefined;
      return !(flag1 && flag2);
    };

    $scope.dispatch = function() {
      var obj = {};
      obj.agent = $scope.deliveryAgent;
      if ($scope.showChangePaymentMethodOption)
        obj.paymentMethod = $scope.paymentMethod;
      else
        obj.paymentMethod = $scope.updatedPaymentMethod;

      if (($scope.paymentMethod == "COD" || $scope.paymentMethod == "CASH")){
        obj.amount = $scope.deliveryAmt;
      }
      else
        obj.amount = 0;
      $modalInstance.close(obj);
    };
    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };
  });

angular.module('app').controller('ModalListCtrl',function($scope, $rootScope,$modalInstance, orderList, deliveryBoyList,paymentMethodList) {
        $scope.orderList = orderList;
        $scope.deliveryBoyList = deliveryBoyList.data.deliveryBoy;
        $scope.paymentMethodList = paymentMethodList;
        this.deliveryAmt1 =0;
        this.deliveryAmt2=0;
        this.deliveryAmt3 =0;
        this.deliveryAmt = 0;
        $.each(orderList, function() {
          this.showChangePaymentMethodOption = true;
          if($rootScope.currencyGlob=="INR"){
            this.deliveryAmt1 = 100 - ((this.orderAmount+this.creditBalance) % 100);
            this.deliveryAmt2 = 500 - ((this.orderAmount+this.creditBalance) % 500);
            this.deliveryAmt3 = 2000 - ((this.orderAmount+this.creditBalance) % 2000);
            if (this.paymentMethod == 'COD' && (this.orderAmount+this.creditBalance)!==0 && (this.orderAmount%2000)!==0){
            this.deliveryAmt = this.deliveryAmt3;
          }
          }
          this.updatedPaymentMethod = paymentMethodList[0].name;
          
        });
        $scope.setDeliveryAmt = function(order, amt, changeBox) {
          order.deliveryAmt = amt;
          $("#" + order.id + " .delivery-amt").each(function() {
            $(this).removeClass('delivery-amt-selected')
          });
          $("#" + order.id + "-" + changeBox).addClass(
              'delivery-amt-selected');
        };
        $scope.updateDeliveryAmt = function(order) {
          if (order.paymentMethod == 'COD'
              || order.paymentMethod == 'CASH') {
            order.deliveryAmt = order.deliveryAmt2;
          }
        };
        $scope.getOrderPaymentMethodType = function(order) {
          var reqMethod = order.paymentMethod;
          if (!order.showChangePaymentMethodOption)
            reqMethod = order.updatedPaymentMethod;
          var method = $.grep($scope.paymentMethodList, function(
              type) {
            return type.name == reqMethod
          });
          return method[0] && method[0].type === "CASH";
        };

        $scope.disableDispatchBtn = function() {
          return $scope.deliveryAgent == undefined;
        };
        $scope.dispatch = function() {
          $.each($scope.orderList,function() {
                    if (!this.showChangePaymentMethodOption)
                      this.paymentMethod = this.updatedPaymentMethod;
                    if (!(this.paymentMethod == "COD" || this.paymentMethod == "CASH"))
                      this.deliveryAmt = 0;
                    this.deliveryAgent = $scope.deliveryAgent;
                  });
          $modalInstance.close($scope.orderList);
        };
        $scope.cancel = function() {
          $modalInstance.dismiss('cancel');
        };
      });



angular.module('app').controller('LockRegisterCtrl',['$scope','$http',function($scope, $http) {
  $scope.unLockSaleRegister = function() {
     var username = $scope.userName;
     var password = $scope.unlockPassword;
     var req = {
       method : 'POST',
       url : '/CookedSpecially/user/isAuthenticate ',
       headers: {'Content-Type': 'application/x-www-form-urlencoded'},
       data :$.param({
       username : username,
       password : password
   })
   }
   $http(req).then(function successCallback(response) {
      if(response.data['status']=="true")
         $scope.showSalesSummary();
         else{
           alert("Please enter correct password.");
           $scope.errMsg="Please enter correct Password."
         }
       },
      function errorCallback(response) {
      $scope.errorMsg = response;
      console.log($scope.errorMsg);
     });
    };
   }
 ]);
angular.module('app').controller('HandoverCtrl',['$scope','$rootScope','$http',function($scope, $rootScope, $http) {
$rootScope.handoverSaleRegister = function() {
  
  if(($rootScope.user.name===$rootScope.saleRegOwner) && ($rootScope.userName===$scope.handoverUsername)){
    $("#js-overlay").show();
          $('#saleRegisterPopUP').show();
        $("#lblError").text("You are currently logged in. In order to start handover, please ask the Delivery Manager who is taking over to log in.");
  }
  else{
    $(".js-spin-spinner").show();
    $(".js-spin-overlay").show();
  var req = {
    method : 'POST',
    url : '/CookedSpecially/salesregister/handoverPendingSales',
    headers : {'Content-Type' : 'application/json'
    },
    data :{
      userName : $scope.handoverUsername,
      password : $scope.handoverPassword,
      remark:$scope.handoverRemark,
      tillId:$rootScope.saleRegisterId
    }
  }
  $http(req).then(function successCallback(response) {
	  localStorage.setItem("restId",$rootScope.restaurantId);
	  localStorage.setItem("handOver","hand");
    window.location.href= '/CookedSpecially/deliveryDashboard.jsp';
          },
          function errorCallback(response) {
            if(response.status===500){
              $("#js-overlay").show();
                     $('#saleRegisterPopUP').show();
                   $("#lblError").text("Authentication failed! Please check the credentials you provided. If you still run into issues, contact administrator");
            }
            $scope.errorMsg = response.status;
            console.log($scope.errorMsg);
          });
  $(".js-spin-spinner").hide();
  $(".js-spin-overlay").hide();
};
}
}
]);


angular.module('app').controller('EditOrderAuthentication',['$scope','$http','order','$modalInstance','$rootScope','isPayment','ordAmount',function($scope, $http,order,$modalInstance,$rootScope,isPayment,ordAmount) {
$rootScope.closeModal=function()
{
         $modalInstance.close();
}
var order=order;
$scope.requestEditAuthentication = function() {
  var username = $scope.userName;
  var password = $scope.editAuthPassword;
  $rootScope.remarks=$scope.editAuthRemark;
  var req = {
    method : 'POST',
    url : '/CookedSpecially/user/isAuthenticate ',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    data :$.param({
      username : username,
      password : password
    })
  }
  $http(req).then(function successCallback(response) {
    if(response.data['status']=="true"){
      if(isPayment=='partialPayment'){
        $rootScope.isPartial=true;
        $scope.order=order;
        $rootScope.totalAmountSum=$rootScope.totalAmountSum-$scope.order.totalAmount;
        $rootScope.ordrAmount=ordAmount;
          $scope.order.totalAmount=(parseInt($scope.order.changeAmount)+parseInt($rootScope.ordrAmount));
          $rootScope.totalAmountSum=$rootScope.totalAmountSum+$scope.order.totalAmount;
      }
        
      else
        order.showChangePaymentMethodOption = false;
      $modalInstance.close();
    }
    else{
      alert("Please enter correct password.");
      $scope.errMsg="Please enter correct Password."
    }
  },
  function errorCallback(response) {
    $scope.errorMsg = response;
    console.log($scope.errorMsg);
  });
};
}
]);

angular.module('app').controller('EditAccessCtrl',['$scope','$rootScope','$http','order','cancelEditFlag','$modalInstance',function($scope, $rootScope, $http,order,cancelEditFlag,$modalInstance) {
$rootScope.closeModal=function()
{
         $modalInstance.close();
}
var cancelEditFlag=cancelEditFlag;
var checkId=order.checkId;
var fulfillmentCenterId=order.fulfillmentCenterId;
if($rootScope.deliveryManagerEdit===false){
  
$rootScope.requestEditAccess= function() {
    $(".js-spin-spinner").show();
    $(".js-spin-overlay").show();
    $rootScope.cancelEditRemarks=$scope.editAccessRemark;
  var req = {
    method : 'POST',
    url : '/CookedSpecially/user/getEditAccess',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    data :$.param({
      username : $scope.editAccessUsername,
      password : $scope.editAccessPassword,
      remarks:$scope.editAccessRemark,
      ffcId:fulfillmentCenterId,
      checkId:checkId
    })
  }
  $http(req).then(function successCallback(response) {
    if(response.data.status==="true"){
    if(cancelEditFlag==="Cancel"){
      $rootScope.closeModal();
      $rootScope.cancelOrder(order);
      //$modalInstance.close();
      }
      else{
        order.showChangePaymentMethodOption = false;
        $rootScope.closeModal();
    //window.location.href= '/CookedSpecially/posDashboard.jsp';
    //$rootScope.editOrder(order);
    }
    }
    else if(response.data.status==="false"){
      //$rootScope.closeModal();
      $("#js-overlay").show();
             $('#saleRegisterPopUP').show();
           $("#lblError").text(response.data.message);
    }   
  },
          function errorCallback(response) {
            $scope.errorMsg = response.status;
            console.log($scope.errorMsg);
          });
  $(".js-spin-spinner").hide();
  $(".js-spin-overlay").hide();
};
}

}

]);


angular.module('app').controller('salesSummaryCtrl',['$scope','$http','$rootScope',function($scope, $http,$rootScope) {
  $rootScope.openSaleRegister = function() {
    $(".js-spin-spinner").show();
              $(".js-spin-overlay").show();
              var req = {
                method : 'PUT',
                url : '/CookedSpecially/salesregister/open',
                headers : {'Content-Type' : 'application/json'},
                data : { tillId : $rootScope.saleRegisterId,
                       remarks : $rootScope.saleRegisterId
                           + "opened by" +$rootScope.userName
                       }
                        }
              $http(req).then(function successCallback(response) {
                if(response.data['result']==="Success"){
                $rootScope.openingTime =  new Date(response.data['tillDetails'].openingTime);
                $rootScope.salesSummaryData();
                $rootScope.getSaleReList();
                //$rootScope.getSaleRegisterList();
                }
                                  if(response.data['result']==="Error"){
                                  console.log(response.data['message']);
                }
                //$rootScope.showSalesSummary();
                //$rootScope.salesSummaryData(response.data['tillId']);
                $(".js-spin-spinner").hide();
                  $(".js-spin-overlay").hide();
                },
                  function errorCallback(response) {
                $scope.errorMsg = response.data;
                console.log($scope.errorMsg);
              });

            };
            
            //$rootScope.toTime = (new Date).getTime();
             $rootScope.salesSummaryData = function() {
                    $scope.showAddSuccess = false;
                    $scope.showWithdrawSuccess = false;
                    $http.get("/CookedSpecially/salesregister/balanceSummary/"+ $rootScope.saleRegisterId)
                  .then(function successCallback(response) {
                    console.log(response.data);
                    $rootScope.transctionSummary=response.data.transactionSummary;
                    $rootScope.getPendingSalesTotal=0;
                    $rootScope.getCompletedSalesTotal=0;
                    for(var i=0;i<$rootScope.transctionSummary.saleSummary.length;i++)
                    {
                      $rootScope.getPendingSalesTotal+=$rootScope.transctionSummary.saleSummary[i].pendingAmount;
                      $rootScope.getCompletedSalesTotal+=$rootScope.transctionSummary.saleSummary[i].completedAmount;
                      if($rootScope.transctionSummary.saleSummary[i].paymentTypeName=="COD"){
                         $rootScope.completedCod=$rootScope.transctionSummary.saleSummary[i].completedAmount;
                      }
                    }
                    $rootScope.pendingCreditTotal=0;
                    $rootScope.completedCreditTotal=0;
                    for(var i=0;i<$rootScope.transctionSummary.creditSummary.length;i++)
                    {
                      $rootScope.pendingCreditTotal+=$rootScope.transctionSummary.creditSummary[i].pendingAmount;
                      $rootScope.completedCreditTotal+=$rootScope.transctionSummary.creditSummary[i].completedAmount;
                      
                    }
                    $rootScope.currentCashBalance =response.data.Current_Cash_Balance;
                    $rootScope.initialCashBalance=response.data.Initial_Cash_Balance;
                    $rootScope.transactionAddCash=response.data.ADD_CASH;
                    $rootScope.transactionWithdrawCash=response.data.WITHDRAW_CASH;
                    $rootScope.transactionCash=response.data.TRANSACTION_CASH;
                    $rootScope.fulfillmentName=response.data.ffcName;
                    $rootScope.saleRegisterName=response.data.tillName;
                    $rootScope.saleRegisterOwner=response.data.Till_Owner;
                    $rootScope.currentOwner=response.data.Till_Owner;
                    var someString = response.data.Till_Owner;
                    var index = someString.indexOf("(");  // Gets the first index where a space occours
                    $rootScope.saleRegOwner = someString.substr(0, index); 
                    $rootScope.currentOwner=$rootScope.saleRegOwner;
                      }, 
                      function errorCallback(response) {
                        $scope.errorMsg = response.data;
                        console.log($scope.errorMsg);
                      });
            };
            
            $scope.addCash = function() {
                  $("#js-spinner").show();
                  $("#lblText").text("Adding "+$scope.addAmount+" to " +$rootScope.saleRegisterName+" .")
                  $("#js-overlay").show();
                  var req = {
                method : 'PUT',
                url : '/CookedSpecially/salesregister/addCash',
                headers : {'Content-Type' : 'application/json'},
                data : { tillId : $rootScope.saleRegisterId,
                       amount : $scope.addAmount,
                       remarks : $scope.addRemarks
                }
            }
            $http(req).then(function successCallback(response) {
                if(response.data['Error'])
                $scope.successAddText=response.data['Error'];
                else
                $scope.successAddText ="Successfully added and the current balance is "+ response.data['balance'];
                $rootScope.salesSummaryData();
                $("#js-spinner").hide();
                $("#lblText").text("");
                $('#saleRegisterPopUP').show();
                $("#lblError").text($scope.successAddText);
                 // $scope.showAddSuccess = true;
                $scope.addAmount="";
                $scope.addRemarks="";
                    }, 
                function errorCallback(response) {
                $scope.errorMsg = response.data;
                console.log($scope.errorMsg);
            });
            };
            
            $scope.withdrawCash = function() {
                  $("#js-spinner").show();
                  $("#lblText").text("Withdrawing "+$scope.withdrawAmount+" from " +$rootScope.saleRegisterName+" .")
                  $("#js-overlay").show();
                  var req = {
                method : 'PUT',
                url : '/CookedSpecially/salesregister/withdrawCash',
                headers : {'Content-Type' : 'application/json'},
                data :{ tillId : $rootScope.saleRegisterId,
                      amount : $scope.withdrawAmount,
                      remarks : $scope.withdrawRemarks
                }
             }
             $http(req).then(function successCallback(response) {
                if(response.data['Error'])
                $scope.successWithdrawText=response.data['Error'];
                else
                $scope.successWithdrawText ="Successfully withdraw and the current balance is "+ response.data['balance'];
                $rootScope.salesSummaryData();
                $("#js-spinner").hide();
                $("#lblText").text("");
                $('#saleRegisterPopUP').show();
                $("#lblError").text($scope.successWithdrawText);
                //$(".js-spin-overlay").hide();
                //$scope.showWithdrawSuccess = true;
                $scope.withdrawAmount="";
                $scope.withdrawRemarks="";
                },
                function errorCallback(response) {
                $scope.errorMsg = response.data;
                console.log($scope.errorMsg);
             });

            };
            
            $rootScope.closeSaleRegister = function() {
                  $(".js-spin-spinner").show();
                  $(".js-spin-overlay").show();
                  var req = {
                method : 'PUT',
                url : '/CookedSpecially/salesregister/close',
                headers : {'Content-Type' : 'application/json'},
                data :{ tillId : $rootScope.saleRegisterId,
                      remarks : $rootScope.saleRegisterId
                    + " closed successfully by " +$rootScope.userName
                }
            }
              $http(req).then(function successCallback(response) {
                if(response.data['result']==="Success"){
                window.location.href= '/CookedSpecially/j_spring_security_logout';
                }
                if(response.data['result']==="Error"){
                                console.log(response.data['message']);
                  }
                $(".js-spin-spinner").hide();
                $(".js-spin-overlay").hide();
                },
                function errorCallback(response) {
                $scope.errorMsg = response.data;
                console.log($scope.errorMsg);
                });
            };
            
            
            
            
          } ]);

angular.module('app').controller('orderController',function($scope, $rootScope, $filter, $interval, $http, $modal) {
 var getDeliverBoys = function() {
          if ($scope.deliveryBoyList == undefined || $scope.deliveryBoyList.data.deliveryBoy.length==0) {
            if($rootScope.restaurantId!=undefined && $rootScope.parentRestaurantId !=null ){
              $scope.deliveryBoyApi=$scope.deliveryBoyApi+"?restaurantId="+$rootScope.restaurantId;
            }
            $http.get($scope.deliveryBoyApi)
                .then(
                    function(dataObj) {
                      $scope.deliveryBoyList = dataObj;
                    },
                    function(err) {
                      console.log("Error in fetching delivery people list.");
                      console.log(err);
                    });
          }
        };

        var updateOrderStatus = function(orderId, status, money,
            paymentType, deliveryBoy) {
          if(angular.isUndefinedOrNull($scope.saleRegisterId)===true){
            $modalInstance.close();
            alert("Please, relogin");
          }
          else{
          var paidStatus;
          var checkedValue = null;
          var inputElements = document
              .getElementsByClassName('paidStatus');
          for (var i = 0; inputElements[i]; ++i) {
            if (inputElements[i].checked) {
              checkedValue = inputElements[i].value;
              break;
            }
          }

          if (checkedValue == "Paid") {
            paidStatus = "Paid";
          }
          var statusApi = "/CookedSpecially/order/setOrderStatus?orderId="
              + orderId
              + "&status="
              + status
              + "&money="
              + money
              + "&paymentType="
              + paymentType
              + "&deliveryBoy="
              + deliveryBoy
              + "&paidStatus=" + paidStatus
                  +"&tillId="
                              +$rootScope.saleRegisterId;

          var setOrderStatus = $.ajax(statusApi, {
            isLocal : false
          });
          setOrderStatus.then(function(data) {
            if(data.Error){
              $("#js-overlay").show();
              $('#saleRegisterPopUP').show();
              $("#lblError").text(data.Error);
            }
            else
              {
              $scope.salesSummaryData();
              }

          }, function(err) {
            console.log('error');
            console.log(err);
          });
          setOrderStatus.complete(function() {
            refreshOrderList();
            resetPromise(true);
          });
          }
        }
          
        var fetchOrders = function(orderType) {
            var api = "/CookedSpecially/order/getOrdersByType?orderType="
              + orderType
              + "&ordersOfDay="
              + $rootScope.ordersOfDay
              +"&restaurantId="
              +$rootScope.restaurantId;
          
          return $http.get(api);
        };

        var fetchAllOrders = function() {
          var api = "/CookedSpecially/order/getOrdersByType?orderType=new&orderType=pending&ordersOfDay="
              + $rootScope.ordersOfDay;
          return $http.get(api);
        };

        var updateList = function(list, newList) {
              var tempList = [];
              $.each(newList,function() {
                    var order = this;
                    var existingOrder = $.grep(list,
                        function(o) {
                          return order.id == o.id
                        });

                    if (existingOrder[0] != undefined) {
                      order.showDetails = existingOrder[0].showDetails;
                      order.orderCheckbox = existingOrder[0].orderCheckbox;
                    }
					// Erich: include TakeAway and Table orders when order.fulfillmentCenterId == 0
					if($rootScope.filterOrdersForFFCenter == undefined || $rootScope.filterOrdersForFFCenter.fulfillmentCenterId == order.fulfillmentCenterId || order.fulfillmentCenterId == 0) {
                      tempList.push(order);
                    }
                  });

          list = list.filter(function(order) {
            return $.grep($scope.cancelledOrderList,
                function(o) {
                  return order.checkId == o.checkId
                }).length > 0;
          });

          $.each(list, function() {
            this.cancelled = true;
            tempList.push(this);
          });

          return tempList;
        };

        var refreshOrderList = function() {
          
          fetchOrders("Cancelled").then(function(response) {
            $scope.cancelledOrderList = response.data;
          }, function(err) {
            console.log(err.data);
            $scope.tempList = [];
          });

          fetchOrders("ready").then(
              function(response) {

                $scope.readyOrderList = updateList($scope.readyOrderList,response.data);
              }, function(err) {
                $(".js-spin-overlay").show();
                $(".session-expired-message").show();
                window.location.href= '/CookedSpecially/deliveryDashboard.jsp';
                $scope.tempList = [];
              });
          
          fetchAllOrders().then(function(response) {
                    $scope.allOrdersList = updateList($scope.allOrdersList,response.data);
                    $(".js-spin-overlay").hide();
                    $(".js-spin-spinner").hide();
                    $(".session-expired-message").hide();
                  },
                  function(err) {
                    console.log(err.data);
                    $(".js-spin-overlay").show();
                    $(".session-expired-message").show();
                    window.location.href= '/CookedSpecially/deliveryDashboard.jsp';
                    $scope.tempList = [];
                  });
          // $rootScope.salesSummaryData();
           $rootScope.getDispatchCancelled();
           $rootScope.getDispatchOrder();
        };

        $scope.showFutureOrders = function() {
          if ($rootScope.ordersOfDay == "today") {
            $rootScope.ordersOfDay = "future";
          } else {
            $rootScope.ordersOfDay = "today";
          }
          $scope.cancelledOrderList = [];
          $scope.readyOrderList = [];
          $scope.allOrdersList = [];
          refreshOrderList();
          resetPromise(true);
        };

        var promise = $interval(refreshOrderList, 30000);

        function resetPromise(restart) {
          if (angular.isDefined(promise)) {
            $interval.cancel(promise);
            promise = undefined;
          }
          if (restart)
            promise = $interval(refreshOrderList, 30000);
        }

        $scope.$on('$destroy', function() {
          resetPromise(false);
        });

        $scope.toggleDetails = function(order) {
          order.showDetails = !order.showDetails;
        };

        $scope.showOptions = function(list, option) {
          var flag = false;
          for (var i = 0; i < list.length; i++) {
            if (list[i].orderCheckbox) {
              flag = true;
              break;
            }
          }
          $scope[option] = flag;

          if (flag)
            $scope.checkAllValueReadyOrder = "UnSelect";
          else
            $scope.checkAllValueReadyOrder = "Select All";
        };

        var getSelectedOrders = function(inputList) {

          var list = [];
          for (var i = 0; i < inputList.length; i++) {
            if (inputList[i].orderCheckbox) {
              list.push(inputList[i]);
            }
          }
          return list;
        };

        var removeOrderFromList = function(list, order) {

          for (var i = 0; i < list.length; i++) {
            if (list[i].id == order.id) {
              list.splice(i, 1);
              break;
            }
          }
        };

        var removeOrdersFromList = function(list, orders) {

          for (var i = 0; i < orders.length; i++) {
            var index = list.indexOf(orders[i]);
            list.splice(index, 1);
          }
        };

        $scope.dispatchOrder = function(order) {
          var that = this;
          var modalInstance = $modal.open({
            templateUrl : 'myModalContent.html',
            controller : 'ModalInstanceCtrl',
            resolve : {
              order : function() {
                return that.order;
              },
              deliveryBoyList : function() {
                return $scope.deliveryBoyList;
              },
              paymentMethodList : function() {
                return $scope.paymentMethodList;
              }
            }
          });

          modalInstance.result.then(function(obj) {
            that.order.deliveryAgent = obj.agent;
            that.order.deliveryAmt = obj.amount;
            that.order.paymentMethod = obj.paymentMethod;
            dispatch(that.order);
          }, function() {
            console.log('Modal dismissed at: ');
          });
        };

        var dispatch = function(order) {
          if(order.deliveryAmt==undefined){
            order.deliveryAmt=0;
          }
          updateOrderStatus(order.id, "outDelivery",
              order.deliveryAmt, order.paymentMethod,
              order.deliveryAgent);
          removeOrderFromList($scope.readyOrderList, order);
        };

        $scope.dispatchOrders = function() {
          var list = getSelectedOrders($scope.readyOrderList);
          var that = this;
          var modalInstance = $modal.open({
            templateUrl : 'myModalList.html',
            controller : 'ModalListCtrl',
            resolve : {
              orderList : function() {
                return list;
              },
              deliveryBoyList : function() {
                return $scope.deliveryBoyList;
              },
              paymentMethodList : function() {
                return $scope.paymentMethodList;
              }
            }
          });

          modalInstance.result.then(function(orderList) {
            $.each(orderList, function() {
              dispatch(this);
            });
            defaultSettings();
            refreshOrderList();
          }, function() {
            console.log('Modal dismissed');
          });
        };
          $rootScope.getDispatchCancelled=function(){
            $http
            .get('/CookedSpecially/order/getDispatchedCancelOrders?orderType=cancelled&ordersOfDay='+$rootScope.ordersOfDay)
              .then(
                  function successCallback(response) {
                  $rootScope.dispatchCancelledData=response.data;
                  }, 
                  function errorCallback(response) {
                    $scope.errorMsg = response.data;
                    console.log($scope.errorMsg);
                  });
          }
          $rootScope.getDispatchOrder=function(){
            
            var orderType="outdelivery";
            $http
            .get("/CookedSpecially/order/getOrdersByType?orderType="
                + orderType
                + "&ordersOfDay="
                + $rootScope.ordersOfDay
                + "&restaurantId="
                + $rootScope.restaurantId
            )
              .then(
                  function successCallback(response) {
                  $rootScope.dispatchOrdersData=response.data;
                   var dispatchData=$rootScope.dispatchCancelledData
                    for (var i = 0; i < dispatchData.length; i++) {
                      $rootScope.dispatchOrdersData.push(dispatchData[i]);
                      }
                   localStorage.setItem("dispatchedData",JSON.stringify($rootScope.dispatchOrdersData));
                  }, 
                  function errorCallback(response) {
                    $scope.errorMsg = response.data;
                    console.log($scope.errorMsg);
                  });
          }
          
        
        
        $scope.checkAll = function(option) {

          var flag = ($scope[option] == "Select All");
          var orders = $scope.readyOrderList;
          // this will uncheck all items but will check only
          // filtered items
          if (flag)
            orders = $scope
                .$eval("readyOrderList | filter:readyOrderFilter");

          $.each(orders, function() {
            this.orderCheckbox = flag;
          });

          $scope.showOptions(orders, 'showOptionsReadyOrders');
        };

        $scope.printOrder = function(order) {

          if (order == undefined || order.invoiceId == undefined)
            return;
          var billapi = "/CookedSpecially/order/generateCheckForPrint?templateName=saladdaysbill&checkId="
              + order.checkId;

          var bill = $http.get(billapi).then(function(dataObj) {
                    var frame1 = $('<iframe />');
                    frame1[0].name = "frame1";
                    frame1.css({
                      "position" : "absolute",
                      "top" : "-1000000px"
                    });
                    $("body").append(frame1);
                    var frameDoc = frame1[0].contentWindow ? frame1[0].contentWindow
                        : frame1[0].contentDocument.document ? frame1[0].contentDocument.document
                            : frame1[0].contentDocument;
                    frameDoc.document.open();
                    frameDoc.document
                        .write(dataObj.data);
                    frameDoc.document.close();
                    setTimeout(
                        function() {
                          window.frames["frame1"]
                              .focus();
                          window.frames["frame1"]
                              .print();
                          frame1.remove();
                        }, 500);

                  },
                  function(err) {
                    console.log('error in getting check');
                    console.log(err);
                  });
        };

        $scope.hideCancelledOrder = function(order, list) {
          removeOrdersFromList(list, [ order ]);
        };

        $rootScope.filterOrdersByFFCenter = function(ffCenter) {
          if (ffCenter != "ALL") {
            $rootScope.filterOrdersForFFCenter = ffCenter;
          } else if ($scope.fulfillmentCenterList.length > 1) {
            $rootScope.filterOrdersForFFCenter = undefined;
          }
          refreshOrderList();
        };

        var defaultSettings = function() {
          $scope.readyOrderFilter = "";
          $scope.checkAllValueReadyOrder = "Select All";
          $scope.showOptionsReadyOrders = false;
          $rootScope.ordersOfDay = "today";
        };
        $rootScope.getSaleReList=function(){
            $http.get('/CookedSpecially/salesregister/list?userId=$rootScope.user.userId').then(function successCallback(response) {
  	              $scope.restaurantData = response.data.restaurantList; // get data from json
  	              console.log($scope.restaurantData);
  	              $rootScope.saleRegister = [];
  	              $rootScope.fulfillmentList=[];
  	              angular.forEach($scope.restaurantData, function(restaurantList, index) {
  	            	  if(restaurantList.restaurantId==$rootScope.restaurantId){
  	                angular.forEach(restaurantList.fulfillmentcenterList, function(fulfillmentcenterList, index){
  	                  $rootScope.fulfillmentList.push(fulfillmentcenterList);
  	                  angular.forEach(fulfillmentcenterList.tillList, function(tillList, index){
  	                    tillList.restaurantId=restaurantList.resturantId;
  	                    tillList.restaurantName=restaurantList.resturantName;
  	                    $rootScope.saleRegister.push(tillList);
  	                   });
  	                });
  	            	  }
  	              });
                        $rootScope.saleRegisterList=angular.merge($rootScope.fulfillmentList,$rootScope.saleRegister);
                        localStorage.setItem("saleRegList",JSON.stringify($rootScope.saleRegisterList));
                  }, 
                  function errorCallback(response) {
                    $scope.errorMsg = response.data;
                    console.log($scope.errorMsg);
                  });
          }
 var surl;  
 var saleReg;
 var saleRegList;
 var dispatchedData;
 var restId;
 var handOver;
var fetchRestaurantInfo = function() {
	var gethandOver=localStorage.getItem("handOver");
	if(gethandOver=='hand'){
	var getrestId=localStorage.getItem("restId");
	$rootScope.restaurantId=getrestId;
	}
 if($rootScope.restaurantId!=undefined){
   $scope.restaurantInfoApi =$scope.restaurantInfoApi+"?restaurantId="+$rootScope.restaurantId;
 }
  $http.get($scope.restaurantInfoApi).then(function(response) {
     var data = response.data;
     $rootScope.deliveryManagerEdit=data.deliveryManagerEdit;
     $rootScope.roundOff=data.roundOffAmount;
     $scope.city = data.city;
     $scope.restaurantName = data.restaurantName;
     $rootScope.restName = data.restaurantName;
     $rootScope.parentRestaurantId=data.parentRestaurantId
     $scope.currency =data.currency;
     $rootScope.currencyGlob = data.currency;
     $scope.fulfillmentCenterList = data.fulfillmentCenter;
     $scope.paymentMethodList = data.paymentType;
     $rootScope.paymentMethodsList=data.paymentType;
     $rootScope.restaurantId = data.restaurantId;
     $http.get($scope.employeeDetailsApi).then(function(res) {
        getDeliverBoys();
        var emp = res.data;
        console.log(emp);
        $rootScope.user = emp;
        $rootScope.userName=emp.username;
        var getsurl = localStorage.getItem("surl");
       var getsaleReg= localStorage.getItem("saleReg");
       var getsaleRegList=localStorage.getItem("saleRegList");
       var getdispatchedData=localStorage.getItem("dispatchedData");
	      if (window.performance) {
	    	  console.info("window.performance work's fine on this browser");
	    	}
	     
	    	if (performance.navigation.type == 1) {
	    	  console.info( "This page is reloaded" );
	    	  console.log(getsaleReg);
	    	  $rootScope.dispatchOrdersData=JSON.parse(getdispatchedData);
	    	  $http.get('/CookedSpecially/salesregister/list?userId=$rootScope.user.userId').then(function successCallback(response) {
  	              $scope.restaurantData = response.data.restaurantList; // get data from json
  	              console.log($scope.restaurantData);
  	              $rootScope.saleRegister = [];
  	              $rootScope.fulfillmentList=[];
  	              angular.forEach($scope.restaurantData, function(restaurantList, index) {
  	            	  if(restaurantList.restaurantId==$rootScope.restaurantId){
  	                angular.forEach(restaurantList.fulfillmentcenterList, function(fulfillmentcenterList, index){
  	                  $rootScope.fulfillmentList.push(fulfillmentcenterList);
  	                  angular.forEach(fulfillmentcenterList.tillList, function(tillList, index){
  	                    tillList.restaurantId=restaurantList.resturantId;
  	                    tillList.restaurantName=restaurantList.resturantName;
  	                    $rootScope.saleRegister.push(tillList);
  	                   });
  	                });
  	            	  }
  	              });
                        $rootScope.saleRegisterList=angular.merge($rootScope.fulfillmentList,$rootScope.saleRegister);
          	    	  if($rootScope.saleRegisterList.length>1){
          	    		  for(var i=0;i<$rootScope.saleRegisterList.length;i++){
          		    		  if($rootScope.saleRegisterList[i].tillId==JSON.parse(getsaleReg).tillId){
          		    			  $scope.saleRegist=$rootScope.saleRegisterList[i];
          		    			  
          		    		  }
          	    	  }
          	    		  $rootScope.saleRegOnRefresh($scope.saleRegist);
          	    		  $rootScope.salesSummaryData();
          	    	  }
          	    	  else{
          	    		  $rootScope.saleRegOnRefresh(JSON.parse(getsaleReg));
          	    		  $rootScope.salesSummaryData();
          	    	  }
                  }, 
                  function errorCallback(response) {
                    $scope.errorMsg = response.data;
                    console.log($scope.errorMsg);
                  });
	    	  //$rootScope.saleRegisterList=JSON.parse(getsaleRegList);
	    	  
	    	  
	    	  
	    	  if(getsurl=='collect'){
	    		  $rootScope.showCashCollectionScreen();
	    	  }
	    	  if(getsurl=='summary'){
	    		  $rootScope.showSalesSummary();
	    		  
	    	  }
	    	  
               if(getsurl=='lock'){
            	   $rootScope.showLockRegister();
               }	    	   
	    	  
	    	  if(getsurl=='close'){
	    		  $rootScope.ShowCloseSaleRegister();
	    	  }
	    	  
	    	  if(getsurl=='handover'){
	    		  $rootScope.showHandover();
	    	  }
	    	  if(getsurl=='delivery'){
	    		  $rootScope.showDeliveryScreen();
	    	  }
	    	  if(getsurl=='allorders'){
	    		  $rootScope.showAllOrderss();
	    	  }
	    	  
	    	  
	    	} else {
	    	  console.info( "This page is not reloaded");
	    	  $http.get('/CookedSpecially/salesregister/list?userId=$rootScope.user.userId').then(function successCallback(response) {
	              $scope.restaurantData = response.data.restaurantList; // get data from json
	              console.log($scope.restaurantData);
	              $rootScope.saleRegister = [];
	              $rootScope.fulfillmentList=[];
	              angular.forEach($scope.restaurantData, function(restaurantList, index) {
	            	  if(restaurantList.restaurantId==$rootScope.restaurantId){
	                angular.forEach(restaurantList.fulfillmentcenterList, function(fulfillmentcenterList, index){
	                  $rootScope.fulfillmentList.push(fulfillmentcenterList);
	                  angular.forEach(fulfillmentcenterList.tillList, function(tillList, index){
	                    tillList.restaurantId=restaurantList.resturantId;
	                    tillList.restaurantName=restaurantList.resturantName;
	                    $rootScope.saleRegister.push(tillList);
	                   });
	                });
	            	  }
	              });
	              $rootScope.saleRegisterList=angular.merge($rootScope.fulfillmentList,$rootScope.saleRegister);
	              localStorage.setItem("saleRegList",JSON.stringify($rootScope.saleRegisterList));
	              if($rootScope.saleRegisterList.length>1){
	                $rootScope.openSaleRegisterList=[];
	                $rootScope.closeSaleRegisterList=[];
	                $rootScope.openSaleRegDiffLoginList=[];
	                for(var i=0;i<$rootScope.saleRegisterList.length;i++){
	                  if(($rootScope.saleRegisterList[i].status=="OPEN") &&($rootScope.user.name==$rootScope.saleRegisterList[i].currentOwnerName)){
	                    $rootScope.openSaleRegisterList.push($rootScope.saleRegisterList[i]);
	                                            			
	                  }
	                  if($rootScope.saleRegisterList[i].status=="CLOSE"){
		                    $rootScope.closeSaleRegisterList.push($rootScope.saleRegisterList[i]);
		                                            			
		                  }
	                  if(($rootScope.saleRegisterList[i].status=="OPEN") &&($rootScope.user.name!==$rootScope.saleRegisterList[i].currentOwnerName)){
		                    $rootScope.openSaleRegDiffLoginList.push($rootScope.saleRegisterList[i]);                    			
		                  }
	                }
	                
	                console.log($rootScope.openSaleRegisterList);
	                if($rootScope.openSaleRegisterList.length==1){
		                  var saleRegister=$rootScope.openSaleRegisterList[0];
		                	/*if($rootScope.closeSaleRegisterList.length==1)
		                  var saleRegister=$rootScope.closeSaleRegisterList[0];
		                  console.log(saleRegister);*/
		                  localStorage.setItem("saleReg",JSON.stringify(saleRegister));
		                  $rootScope.getSaleRegisterDetails(saleRegister);
	                }
	                else{ 
	                	$rootScope.selectSaleRegiter();
	                 }
	                                          	  
	               }
	              else{
	                var saleRegister=$rootScope.saleRegisterList[0];
	                console.log(saleRegister);
	                localStorage.setItem("saleReg",JSON.stringify(saleRegister));
	                $rootScope.getSaleRegisterDetails(saleRegister);
	           }
	          }, 
	          function errorCallback(response) {
	          $scope.errorMsg = response.data;
	          console.log($scope.errorMsg);
	          });
	              
	    	}
	    	refreshOrderList();
	        $rootScope.getDispatchCancelled();
	        $rootScope.getDispatchOrder();
	        localStorage.removeItem("handOver");            
      //$rootScope.getSaleRegisterList();
      
                             
     
     });
   });
 };
 $rootScope.selectSaleRegiter = function () {
        var saleRegisterList=$rootScope.saleRegisterList;
       /* if($rootScope.isRepeated){*/
            var modalInstance = $modal.open({
              templateUrl: 'selectSaleRegisterModal.html',
              controller: 'SelectSaleRegisterCtrl',
              backdrop: 'static',
              windowClass: 'app-modal-window',
              resolve: {
                saleRegisterList: function () {
                  return $rootScope.saleRegisterList;
                }
              }
            });
             //$rootScope.isRepeated=false;
        
            modalInstance.result.then(function (saleRegister) {
            	localStorage.setItem("saleReg",JSON.stringify(saleRegister));
            	console.log(saleRegister);
                $(".js-spin-spinner").show();
              $(".js-spin-overlay").show();
              $rootScope.currentOwner=saleRegister.currentOwnerName;
              $rootScope.openingTime =  new Date(saleRegister.openningTime);
              $rootScope.saleRegister=saleRegister;
              $rootScope.saleRegisterId=saleRegister.tillId;
              $rootScope.saleRegisterName=saleRegister.tillName;
              if($rootScope.parentRestaurantId==null){
              $rootScope.restName=saleRegister.restaurantName;
              $rootScope.restaurantId=saleRegister.restaurantId;
              fetchRestaurantInfo();
              //getDeliverBoys();
              //defaultSettings();
              }
              var ffcId=saleRegister.fulfillmentcenterId;
              $rootScope.filterOrdersByFFCenter(saleRegister);
              $rootScope.filterOrdersForFFCenterName=saleRegister.fulfillmentcenterName;
              $rootScope.saleRegisterStatus =saleRegister.status;
              if($rootScope.saleRegisterStatus==="OPEN")
                {
                if($rootScope.user.name!==$rootScope.currentOwner){
                 
                  $rootScope.showSalesSummary();
                   $rootScope.IsDisableRegister=true;
                   $rootScope.IsOptionVisible=true;
                 }
                else
                  {
                  $rootScope.showDeliveryScreen();
                  /*$rootScope.showSalesSummary();
                  $rootScope.showSalesSummary();
                  $rootScope.IsDisableRegister=false;
                  $rootScope.IsOptionVisible=false;*/
                  }
                
               $rootScope.salesSummaryData();
                }
              else
                {
                $rootScope.openSaleRegister();
                $rootScope.showDeliveryScreen();
                /*$rootScope.showSalesSummary();*/
               $rootScope.salesSummaryData();
                }
              $(".js-spin-spinner").hide();
              $(".js-spin-overlay").show();
            }, function () {
              $log.info('Modal dismissed at: ' + new Date());
            });
        //}
          };
           
          $rootScope.getSaleRegisterDetails=function(saleRegister){
        	  $rootScope.currentOwner=saleRegister.currentOwnerName;
              $rootScope.openingTime =  new Date(saleRegister.openningTime);
              $rootScope.saleRegister=saleRegister;
              $rootScope.saleRegisterId=saleRegister.tillId;
              $rootScope.saleRegisterName=saleRegister.tillName;
              if($rootScope.parentRestaurantId==null){
              $rootScope.restName=saleRegister.restaurantName;
              $rootScope.restaurantId=saleRegister.restaurantId;
              alert($rootScope.currentOwner);
              fetchRestaurantInfo();
              //getDeliverBoys();
              //defaultSettings();
              }
              var ffcId=saleRegister.fulfillmentcenterId;
              $rootScope.filterOrdersByFFCenter(saleRegister);
              $rootScope.filterOrdersForFFCenterName=saleRegister.fulfillmentcenterName;
              $rootScope.saleRegisterStatus =saleRegister.status;
              if($rootScope.saleRegisterStatus==="OPEN")
                {
                if($rootScope.user.name!==$rootScope.currentOwner){
                 
                  $rootScope.showSalesSummary();
                   $rootScope.IsDisableRegister=true;
                   $rootScope.IsOptionVisible=true;
                 }
                else
                  {
                  $rootScope.showDeliveryScreen();
                  /*$rootScope.showSalesSummary();
                  $rootScope.showSalesSummary();
                  $rootScope.IsDisableRegister=false;
                  $rootScope.IsOptionVisible=false;*/
                  }
                
               $rootScope.salesSummaryData();
                }
              else
                {
                $rootScope.openSaleRegister();
                $rootScope.showDeliveryScreen();
                /*$rootScope.showSalesSummary();*/
               $rootScope.salesSummaryData();
                }
          }
          $rootScope.saleRegOnRefresh=function(saleRegister){
        	  $rootScope.currentOwner=saleRegister.currentOwnerName;
              $rootScope.openingTime =  new Date(saleRegister.openningTime);
              $rootScope.saleRegister=saleRegister;
              $rootScope.saleRegisterId=saleRegister.tillId;
              $rootScope.saleRegisterName=saleRegister.tillName;
              if($rootScope.parentRestaurantId==null){
              $rootScope.restName=saleRegister.restaurantName;
              $rootScope.restaurantId=saleRegister.restaurantId;
              fetchRestaurantInfo();
              //getDeliverBoys();
              //defaultSettings();
              }
              var ffcId=saleRegister.fulfillmentcenterId;
              $rootScope.filterOrdersByFFCenter(saleRegister);
              $rootScope.filterOrdersForFFCenterName=saleRegister.fulfillmentcenterName;
              $rootScope.saleRegisterStatus =saleRegister.status;
             
          }
          
          $rootScope.showPartialPayment=function(order){
            $("#appPartialPopUp").show();
            $("#js-overlay").show();
           // order.totalAmount="";
            $rootScope.ordAmount=order.orderAmount+order.creditBalance;
           $rootScope.partialOrder=order;
           //$rootScope.partialOrder.orderAmount;
           
          }
          $rootScope.closePartialPopUp=function(){
            $("#appPartialPopUp").hide();
            $("#js-overlay").hide();
          }
          $rootScope.showAuthentication=function(order,ordAmount){
            /*$scope.order=order;
            $scope.order.totalAmount=(parseInt($scope.order.changeAmount)+parseInt($scope.order.orderAmount));*/
            $("#appPartialPopUp").hide();
            $("#js-overlay").hide();
            $rootScope.showEditAuthentication(order,ordAmount,"partialPayment");
          }
          $rootScope.showEditAuthentication = function(order,ordAmount,isPayment) {
           // var that=this;
            var isPayment;
                var modalInstance = $modal.open({
                  templateUrl: 'editOrderAuthentication.html',
                  controller: 'EditOrderAuthentication',
                  backdrop: 'static',
                  windowClass: 'appUpdate-modal-window',
                  resolve : {
                  order : function() {
                    return order;
                  },
                       isPayment:function(){
                         return isPayment;
                       },
                  ordAmount:function(){
                           return ordAmount;
                         }
                  }
                });

                modalInstance.result.then(function (order) {
                  $rootScope.closeModal();
                    
                }, function () {
                  $log.info('Modal dismissed at: ' + new Date());
                });
           
              };
              
              
              
              $rootScope.showEditAccess = function(order ,CancelEdit) {
                var that=this;
                var cancelEditFlag=CancelEdit;
                $rootScope.cancelEditFlag=CancelEdit;
                $rootScope.orderToCancel=order;
                if($rootScope.deliveryManagerEdit===false){
                  if(that.order.allowEdit===true){
                    if(cancelEditFlag==="Cancel"){
                      $rootScope.cancelOrder(order);
                      }
                    else
                      {
                      order.showChangePaymentMethodOption = false;
                      }
                  }
                  else{
                    var modalInstance = $modal.open({
                      templateUrl: 'editOrderAccess.html',
                      controller: 'EditAccessCtrl',
                      backdrop: 'static',
                      windowClass: 'appUpdate-modal-window',
                      resolve : {
                      order : function() {
                        return that.order;
                      },
                    cancelEditFlag : function() {
                    return cancelEditFlag;
                  }
                      }
                    });

                    modalInstance.result.then(function (order) {
                      $rootScope.closeModal();
                        
                    }, function () {
                      $log.info('Modal dismissed at: ' + new Date());
                    });
                  }
                }
                else
                  {
                  $("#remarksPopUp").show();
                  $("#js-overlay").show();
                  $("#cancelEditRemarks").text("");
                  }
                  };
         
        $rootScope.creditDispatch=false;
        $rootScope.allCreditBills=false;
        $rootScope.cashCollectionScreen=false;      
        $rootScope.IsOptionVisible=false;
        $rootScope.deliveryScreen=true;
        $rootScope.showAllOrders = false;
        $rootScope.salesSummary=false;
        $rootScope.lockSaleRegister=false;
        $rootScope.IsVisible = true;
        $rootScope.IsNavigationVisible=false;
        $rootScope.IsDisableRegister=false;
        $rootScope.handover=false;
        $rootScope.IsHandoverVisible=false;
        $rootScope.showCashCollectionScreen=function(){
          $rootScope.deliveryScreen=false;
          $rootScope.showAllOrders = false;
          $rootScope.salesSummary=false;
          $rootScope.lockSaleRegister=false;
          $rootScope.handover=false;
          $rootScope.creditDispatch=false;
          $rootScope.allCreditBills=false;
          $rootScope.cashCollectionScreen=true;
          localStorage.setItem("surl",'collect');
          
        }
        $rootScope.showAllOrderss=function(){
          $rootScope.deliveryScreen=false;
          $rootScope.showAllOrders = true;
          $rootScope.salesSummary=false;
          $rootScope.lockSaleRegister=false;
          $rootScope.handover=false;
          $rootScope.creditDispatch=false;
          $rootScope.allCreditBills=false;
          $rootScope.cashCollectionScreen=false;
          localStorage.setItem("surl",'allorders');
        }
        $rootScope.showSalesSummary=function(){
          $rootScope.deliveryScreen=false;
          $rootScope.showAllOrders = false;
          $rootScope.salesSummary=true;
          $rootScope.lockSaleRegister=false;
          $rootScope.IsVisible = false;
          $rootScope.IsNavigationVisible=false;
          $rootScope.IsDisableRegister=false;
          $rootScope.handover=false;
          $rootScope.IsHandoverVisible=false;
          $rootScope.creditDispatch=false;
          $rootScope.allCreditBills=false;
          $rootScope.cashCollectionScreen=false;
          localStorage.setItem("surl",'summary');
          //$rootScope.salesSummaryData();
        }
        $rootScope.showLockRegister=function(){
          $rootScope.deliveryScreen=false;
          $rootScope.showAllOrders = false;
          $rootScope.salesSummary=false;
          $rootScope.lockSaleRegister=true;
          $rootScope.IsNavigationVisible=true;
          $rootScope.handover=false;
          $rootScope.cashCollectionScreen=false;
          $rootScope.creditDispatch=false;
          $rootScope.allCreditBills=false;
          localStorage.setItem("surl",'lock');
        }
        $rootScope.showDeliveryScreen=function(){
          $rootScope.deliveryScreen=true;
          $rootScope.showAllOrders = false;
          $rootScope.salesSummary=false;
          $rootScope.lockSaleRegister=false;
          $rootScope.handover=false;
          $rootScope.cashCollectionScreen=false;
          $rootScope.creditDispatch=false;
          $rootScope.allCreditBills=false;
          localStorage.setItem("surl",'delivery');
        }
        $rootScope.ShowCloseSaleRegister=function(){
          $rootScope.deliveryScreen=false;
          $rootScope.showAllOrders = false;
          $rootScope.salesSummary=true;
          $rootScope.lockSaleRegister=false;
          $rootScope.IsVisible = true;
          //$rootScope.IsNavigationVisible=true;
          $rootScope.IsDisableRegister=true;
          $rootScope.IsHandoverVisible=false;
          $rootScope.handover=false;
          $rootScope.closeModal();
          //$rootScope.salesSummaryData($rootScope.saleRegisterId);
          $rootScope.cashCollectionScreen=false;
          $rootScope.creditDispatch=false;
          $rootScope.allCreditBills=false;
          localStorage.setItem("surl",'close');
        }
        
        $rootScope.handoverSaleRegister=function(){
          $rootScope.showAllOrders = false;
          $rootScope.salesSummary=true;
          $rootScope.lockSaleRegister=false;
          $rootScope.deliveryScreen=false;
          $rootScope.IsVisible = false;
          $rootScope.IsHandoverVisible=true;
          $rootScope.IsDisableRegister=true;
          $rootScope.creditDispatch=false;
          $rootScope.allCreditBills=false;
          //$rootScope.handover=true;
         
        }
        
        
        $rootScope.showHandover=function(){
          $rootScope.showAllOrders = false;
          $rootScope.salesSummary=false;
          $rootScope.lockSaleRegister=false;
          $rootScope.deliveryScreen=false;
          $rootScope.IsVisible = false;
          $rootScope.IsHandoverVisible=false;
          $rootScope.handover=true;
          $rootScope.creditDispatch=false;
          $rootScope.allCreditBills=false;
          localStorage.setItem("surl",'handover');
        }
        
        $rootScope.showAllCreditBills=function(){
          
          $rootScope.deliveryScreen=false;
          $rootScope.showAllOrders = false;
          $rootScope.salesSummary=false;
          $rootScope.lockSaleRegister=false;
          $rootScope.handover=false;
          $rootScope.creditDispatch=false;
          $rootScope.allCreditBills=true;
          $rootScope.cashCollectionScreen=false;
        }
        
        $rootScope.showCreditDispatch=function(){
          
          $rootScope.deliveryScreen=false;
          $rootScope.showAllOrders = false;
          $rootScope.salesSummary=false;
          $rootScope.lockSaleRegister=false;
          $rootScope.handover=false;
          $rootScope.creditDispatch=true;
          $rootScope.allCreditBills=false;
          $rootScope.cashCollectionScreen=false;
        }
        
         $rootScope.OK=function(){
                    $("#js-overlay").hide();
                   $('#saleRegisterPopUP').hide();
                 $("#lblError").text("");
      
                        }
         $rootScope.remarksSubmit=function(){
                    $("#js-overlay").hide();
                    $("#remarksPopUp").hide();
                    if($rootScope.cancelEditFlag==="Cancel"){
              $rootScope.cancelOrder($rootScope.orderToCancel);
              }
            else
              {
              $rootScope.orderToCancel.showChangePaymentMethodOption = false;
              }
                    $("#cancelEditRemarks").text("");
                        }
         
        $rootScope.filterOrdersForFFCenter = undefined;
        $scope.allOrdersList = [];
        $scope.readyOrderList = [];
        $scope.deliveryBoyApi = "/CookedSpecially/order/allDeliveryBoy.json";
        $scope.restaurantInfoApi = "/CookedSpecially/restaurant/getrestaurantinfo";
        $scope.employeeDetailsApi = "/CookedSpecially/user/getEmployeeDetails";
       // $rootScope.isRepeated=true;
        $rootScope.getSaleReList();
        fetchRestaurantInfo();
        defaultSettings();
        //getDeliverBoys();
        
  });