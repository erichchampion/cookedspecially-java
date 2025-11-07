angular.module('app', ['ngAnimate', 'ui.bootstrap']);

angular.module('app').controller('addNewTillCtrl', function($scope, $modalInstance, $http, ffCenter, restaurant) {

    $scope.addTillApi = "/CookedSpecially/till/add";

    $scope.ffCenter = ffCenter;
    $scope.restaurant = restaurant;
    defaultSettings();

    function defaultSettings() {
        $scope.showErr = false;
        $scope.tillName = "";
        $scope.tillBalance = 0.00;
    }

    $scope.submit = function(){
        $scope.tillAddSuccess = false;
        $scope.tillAddError = false;

        if($scope.tillName.trim() === "" || $scope.tillBalance == ""){
            $scope.showErr = true;
            return;
        }

        var data = {tillName:$scope.tillName,openingBalance:$scope.tillBalance,fulfillmentCenterId:$scope.ffCenter.fulfillmentCenterId};

        $http.put($scope.addTillApi,data).then(function(res){

            if(res.data.error){
                $scope.tillAddError = true;
                $scope.errMsg = res.data.error;

                return;
            }

            defaultSettings();
            $scope.tillAddSuccess = true;
        },function(err){
            $scope.tillAddError = true;
        });
    };

    $scope.cancel = function(){
        $modalInstance.dismiss('cancel');
    };
});

angular.module('app').controller('editTillCtrl', function($scope, $modalInstance, $http, till) {

    $scope.editTillApi = "/CookedSpecially/till/edit";

    $scope.till = till;

    $scope.submit = function(){
        $scope.tillEditSuccess = false;
        $scope.tillEditError = false;

        if(!$scope.till.tillName || $scope.till.tillName.trim() === ""){
            $scope.showErr = true;
            return;
        }

        $http.post($scope.editTillApi,$scope.till).then(function(res){
            if(res.data.error){
                $scope.tillEditError = true;
                $scope.errMsg = res.data.error;
                return;
            }
            $scope.tillEditSuccess = true;
        },function(err){
            $scope.tillEditError = true;
        });
    };

    $scope.cancel = function(){
        $modalInstance.dismiss('cancel');
    };
});

angular.module('app').controller('fetchTransactionsCtrl', function($scope, $modalInstance, $http, till) {

    $scope.fetchTransactionsApi = "/CookedSpecially/till/fetchTransactions";

    $scope.till = till;
    $scope.transactions = [];

    var today = new Date().setHours(0,0,0,0);
    $scope.fromDate = new Date(today);
    $scope.toDate = new Date(today);

    $scope.fetchTransactions = function(){
        $scope.showFromDateErr = false;
        $scope.showToDateErr = false;
        $scope.showErr = false;

        if(!$scope.fromDate){
            $scope.showFromDateErr = true;
            return;
        }
        if(!$scope.toDate){
            $scope.showToDateErr = true;
            return;
        }
        if($scope.fromDate > $scope.toDate){
            $scope.showErr = true;
            $scope.errMsg = "To date can not be less than from date";
            return;
        }

        $scope.todate = $scope.toDate.setHours(23,59,59,999);
        var obj = {tillId: till.tillId, fromTime:$scope.fromDate.getTime(), toTime:$scope.toDate.getTime()};
        $http.post($scope.fetchTransactionsApi,obj).then(function(res){
            if(res.data.error){
                $scope.showError = true;
                $scope.errMsg = res.data.error;
                return;
            }
            $scope.transactions = res.data.transactions;
        },function(err){
            $scope.showError = true;
            $scope.errMsg = err.message;
        });
    };

    $scope.cancel = function(){
        $modalInstance.dismiss('cancel');
    };
});

angular.module('app').controller('updateTillCashCtrl', function($scope, $modalInstance, $http, till) {

    $scope.updateTillCashApi = "/CookedSpecially/till/updateCash";

    $scope.till = till;

    $scope.submit = function(){
        $scope.updateSuccess = false;
        $scope.updateError = false;

        if(!$scope.transactionType){
            $scope.showErr = true;
            return;
        }

        if(!$scope.amount || $scope.amount <= 0){
            $scope.showAmountErr = true;
            return;
        }

        var data = {tillId:$scope.till.tillId, amount:$scope.amount, category:$scope.transactionType, remarks:$scope.remarks, checkId:1234};

        $http.put($scope.updateTillCashApi,data).then(function(res){
            if(res.data.error){
                $scope.updateError = true;
                $scope.errMsg = res.data.error;
                return;
            }
            $scope.till.balance = $scope.transactionType==='CASH_CREDITED'?($scope.till.balance+$scope.amount):($scope.till.balance-$scope.amount);

            $scope.updateSuccess = true;
            $scope.amount = 0;
            $scope.transactionType = undefined;
            $scope.remarks = "";
            $scope.showErr = false;
            $scope.showAmountErr = false;

        },function(err){
            $scope.updateError = true;
            $scope.errMsg = "Oops! There was some error. Please report it to IT.";
        });
    };

    $scope.cancel = function(){
        $modalInstance.dismiss('cancel');
    };
});


angular.module('app').controller('tillController', function ($rootScope, $scope, $filter, $interval, $http, $modal) {

    $scope.employeeDetailsApi = "/CookedSpecially/user/getEmployeeDetails";
    $scope.tillListApi = "/CookedSpecially/till/list";

    function fetchEmployeeDetails(){
        $http.get($scope.employeeDetailsApi).then(function(res){
            var emp = res.data;
            $rootScope.user = emp;
            $rootScope.restaurantName = emp.orgName;

            fetchTillList();
        });
    }

    function fetchTillList(){
        $http.get($scope.tillListApi).then(function(res){
            var data = res.data;
            $scope.tillList = data;

            if(data && data.restaurantList.length > 0 && data.restaurantList[0].fulfillmentCenterList.length > 0){
                data.restaurantList[0].showFulfillmentCenterList = true;
                data.restaurantList[0].fulfillmentCenterList[0].showTillList = true;
            }
        });
    }

    $scope.addTill = function(ffCenter,restaurant){
        var modalInstance = $modal.open({
            templateUrl: 'addNewTill.html',
            controller: 'addNewTillCtrl',
            resolve: {
                ffCenter: function() {
                    return ffCenter;
                },
                restaurant: function(){
                    return restaurant;
                }
            }
        });

        modalInstance.result.then(function(res){
            fetchTillList();
        },function(){
            fetchTillList();
        });
    };

    $scope.editTill = function(till){
        var modalInstance = $modal.open({
            templateUrl: 'editTill.html',
            controller: 'editTillCtrl',
            resolve: {
                till: function() {
                    return JSON.parse(JSON.stringify(till));
                }
            }
        });

        modalInstance.result.then(function(res){
            fetchTillList();
        },function(){
            fetchTillList();
        });
    };

    $scope.fetchTransactions = function(till){
        var modalInstance = $modal.open({
            templateUrl: 'fetchTransactions.html',
            controller: 'fetchTransactionsCtrl',
            resolve: {
                till: function() {
                    return JSON.parse(JSON.stringify(till));
                }
            }
        });
    };

    $scope.updateTillCash = function(till){
        var modalInstance = $modal.open({
            templateUrl: 'updateTillCash.html',
            controller: 'updateTillCashCtrl',
            resolve: {
                till: function() {
                    return JSON.parse(JSON.stringify(till));
                }
            }
        });
        modalInstance.result.then(function(res){
            fetchTillList();
        },function(){
            fetchTillList();
        });
    };

    fetchEmployeeDetails();

});