/**
 * Author Abhishek Kumar
 * anshilabhi1991@gmail.com
 */

'use strict';

angular.module('myApp').controller('core-Controller', ['$scope','$timeout', '$location', 'UtilService', 'EmployeeService', 'ChartService', 'ReportService', function($scope,$timeout, $location, UtilService, EmployeeService, ChartService, ReportService){
	
	UtilService.showSpinner(true);
	$scope.getOrgChartButton = "Show OrgChart";
	
	function plotOrgChart(){
		$scope.org_data=ReportService.formatOrgData($scope.employeeProfile);
		$scope.org_data.chartType="OrgChart";
		$scope.orgChart=ChartService.plotGraph($scope.org_data, 'orgChart_11',  $scope.onSelectOrgHandler);
	}

	function initController(){
		$scope.reportHeaderName="#####  Report Dashboard  #####";
		//Get Employee Profile And Organization Info
		EmployeeService.getEmployeeDtails().then(function(response){
			console.log("Employee Profile=="+response.data);
			if(response.data){
				$scope.employeeProfile=response.data;
				$scope.restaurant=$scope.employeeProfile.restaurantList[0];
				$scope.ffc=$scope.restaurant.fulfillmentCenterList[0];
				if(response.data.role=="admin"){
					$scope.report_level=['Organization', 'Restaurant', 'FulfillmentCenter'];
					$location.path('/Organization');
					$scope.selectedReportLevel = 'Organization';
				}
				else if(response.data.role=="restaurantManager"){
					$scope.report_level=['Restaurant', 'FulfillmentCenter'];
					$location.path('/Restaurant');
					$scope.selectedReportLevel='Restaurant';
				}
				else{
					$scope.report_level=['FulfillmentCenter'];
					$location.path('/FulfillmentCenter');
					$scope.selectedReportLevel='FulfillmentCenter';
				}
				plotOrgChart();
				$scope.selectedReportLevel=$scope.report_level[0];
				UtilService.showSpinner(false);
			}else
				onError("Failed to get Employee Profile!");
		}, function(response){
			onError(response);
		});   
	} 
	
	function onError(response){
		console.log(response);
		UtilService.openLoginPage();
	}
	
	$scope.onSelectOrgHandler = function (dataSelected) {
		var selected = dataSelected.Name.split('^')[1].split('-');
		$scope.getSelected(selected[0],$scope.employeeProfile, selected[1]);
	};

	$scope.getSelected = function(cat, employeeDtails, id){
		UtilService.showSpinner(true);
		$timeout(function (item) {
			if(cat != "ORG"){
				for(var i = 0, size = employeeDtails.restaurantList.length; i < size ; i++){
					var resturant = employeeDtails.restaurantList[i];
					if(cat=="REST" && resturant.restaurantId==id){
						$scope.restaurant = resturant;
						$scope.selectedReportLevel='Restaurant';
						$location.path('/Restaurant');
						UtilService.showSpinner(false);
						return;
					}else if(cat=="FFC"){
						for(var j = 0, size_f = resturant.fulfillmentCenterList.length; j < size_f ; j++){
							var ffc = resturant.fulfillmentCenterList[j];
							if(resturant.fulfillmentCenterList[j].fulfillmentCenterId==id){
								$scope.ffc = resturant.fulfillmentCenterList[j];
								$scope.restaurant = resturant;
								UtilService.showSpinner(false);
								$scope.selectedReportLevel='FulfillmentCenter';
								$location.path('/FulfillmentCenter');
								return;
							}

						}
					}
				}
			}else{
				$scope.selectedReportLevel = 'Organization';
				$location.path('/Organization');
				UtilService.showSpinner(false);
			}
		}, 1000);
	}

	$scope.toggleOrgChart = function(){
		var elem = document.getElementById("toggle_org_chart");
		if(elem.text == "Show OrgChart"){
			$scope.getOrgChartButton="Hide OrgChart";	
		}else{
			$scope.getOrgChartButton = "Show OrgChart";
		}
		$("#orgChart_11").toggle();
	}
	
	$scope.onRequiredReportListChange = function(){
		$location.path('/'+$scope.selectedReportLevel);
	}
	initController();
	$("#orgChart_11").hide();
}]);