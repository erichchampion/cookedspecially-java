/**
 * Author Name :- Abhishek Kumar
 * EmailId :- anshilabhi1991@gmail.com
 * 
 */

angular.module('myApp').controller('ORG-Controller',  ['$scope','$filter','$timeout', 'ReportService','UtilService','ChartService','Excel', function($scope, $filter,$timeout, ReportService, UtilService, ChartService, Excel){
	$scope.getReport = function(){
		try{
			UtilService.clearEle(['report-chart-pri','report-chart-sec']);
		if($scope.$parent.employeeProfile.orgId==undefined || $scope.$parent.employeeProfile.orgId==null || $scope.$parent.employeeProfile.orgId==undefined){
			alert("Please Select Restaurant");
			return;
		}
		switch ($scope.report.name) {
		case 'Top-Dishes':
			var max=10;
			ReportService.plotDishReport(max, $scope.$parent.employeeProfile.orgId, $scope.fromDateSelected, $scope.toDateSelected,"ORG", $scope);
			break;
		default:
			alert("Developement is in progress for this report, please select another report name!");

		}
		}catch (e) {
			alert("Failed to connect to Server please check internet connection and Try again!");
		}
	}
	
	$scope.exportToExcel=function(tableId, name, fileName){
		/*var exportHref=Excel.tableToExcel(tableId, name);
		var a = document.createElement('a');
		a.href = exportHref;
		a.download = fileName;
		$timeout(function(){a.click();},100);*/
		try{
		var filename = fileName || $scope.report.name ||"download";
		var wb=Excel.createWorkbook(filename); 
		var data=ReportService.formateExcelBlobData($scope.data);
		Excel.dataArrayToExcel(data, wb, name);
		wb.save();
		}catch (e) {
			alert("Failed to connect to Server please check internet connection and Try again!");
		}
	}
	
	angular.element( document.querySelector('report-chart-pri')).remove();
	angular.element( document.querySelector('report-chart-sec')).remove();
	$scope.export_enable=false;
	$scope.data=[];
	$scope.reportList=[{name:'Top-Dishes', type:'Table'}];
	$scope.report=$scope.reportList[0];	
	$scope.toDateSelected=$filter('date')(new Date(),'yyyy-MM-dd');
	$scope.fromDateSelected=$filter('date')(new Date(),'yyyy-MM-dd');
}]);