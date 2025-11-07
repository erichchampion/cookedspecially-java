/**
 * Author Name :- Abhishek Kumar
 * EmailId :- anshilabhi1991@gmail.com
 * 
 */

angular.module('myApp').controller('FFC-Controller', ['$scope','$filter','$timeout', 'ReportService','UtilService','ChartService','Excel', function($scope, $filter,$timeout, ReportService, UtilService, ChartService, Excel){

	$scope.getReport = function(){
		angular.element( document.querySelector('chart-div-3')).remove();
		angular.element( document.querySelector('chart-div-4')).remove();
		//try{
		UtilService.clearEle($scope.chartDiv);
		if($scope.ffc==undefined || $scope.ffc==null || $scope.ffc.fulfillmentCenterId==undefined){
			alert("Please Select Restaurant -> FFC.");
			return;
		}
		switch ($scope.report.name) {
		case 'Delivery-Boy':
			ReportService.getDeliveryBoyReportData($scope.ffc.fulfillmentCenterId, $scope.fromDateSelected, $scope.toDateSelected).then(function(response){
				if(!UtilService.isValidArray(Object.assign([], response.data))){
					alert("No Data Found for given date range, Please select some other range.");
					return;
				}
				$scope.data=Object.assign([], response.data);
				var data_g=ReportService.formateTabledata(Object.assign([], response.data), [0]);
				data_g.chartType=$scope.report.type;
				ChartService.plotGraph(data_g, 'chart-grid-411', $scope.onSelectdeliveryBoyHandler);
				$scope.export_enable=true;
			},function(response){
				alert("Failed to get Delivery Person Data, Please try again!");
			}); 
			break;
		case 'Top-Dishes':
			var max=10;
			ReportService.plotDishReport(max, $scope.ffc.fulfillmentCenterId, $scope.fromDateSelected, $scope.toDateSelected, "FFC",$scope);
			break;
		default:
			alert("Developement is in progress for this report, please select another report name!");

		}
		/*}catch (e) {
			alert("Failed to connect to Server please check internet connection and Try again!");
		}*/
	}

	$scope.onSelectdeliveryBoyHandler = function (deliveryBoySelected) {
		try{
			var fromDate = $scope.fromDateSelected;
			var toDate = $scope.toDateSelected;
			$scope.selectedDeliveryBoy=deliveryBoySelected;
			ReportService.getInvoiceListDeliveredBuDB($scope.ffc.fulfillmentCenterId, deliveryBoySelected.UserId, fromDate, toDate).then(function(response){
				$scope.invoiceList=response.data;
				var title="Orders Delivered by "+$scope.selectedDeliveryBoy.Name+"("+$scope.selectedDeliveryBoy.MobileNo+")";
				$scope.modalInstance = UtilService.openModal('js/angular/template/modelInstance.html', 'ModalInstanceCtrl', 'myApp-model-class', null, null, title, "Delivery_Report_Of_"+$scope.selectedDeliveryBoy.Name+"_"+$scope.selectedDeliveryBoy.MobileNo, Object.assign([], response.data));
				$scope.modalInstance.rendered.then(function () {
					var data_g = ReportService.formateTabledata(Object.assign([], response.data), []);
					data_g.chartType="Table";
					ChartService.plotGraph(data_g, 'model-data', null);
					/*var wb=Excel.createWorkbook(); 
				var data=ReportService.formateExcelBlobData(Object.assign([], response.data));
				Excel.dataArrayToExcel(data, wb, "Test");
				wb.save();*/
				});
			},function(response){
				alert("Failed to get List of Invoice Delivered By Delivery Person!");
			}); 
		}catch (e) {
			alert("Failed to connect to Server please check internet connection and Try again!");
		}
	};

	$scope.exportToExcel=function(tableId, name, fileName){
		/*var exportHref=Excel.tableToExcel(tableId, name);
		var a = document.createElement('a');
		a.href = exportHref;
		a.download = fileName;
		$timeout(function(){a.click();},100);*/
//		try{
			var filename = fileName || $scope.report.name ||"download";
			var wb=Excel.createWorkbook(filename); 
			var data=ReportService.formateExcelBlobData($scope.data);
			Excel.dataArrayToExcel(data, wb, name);
			wb.save();
//		}catch (e) {
//			alert("Failed to connect to Server please check internet connection and Try again!");
//		}
	}


	angular.element(document.querySelector('chart-div-3')).remove();
	angular.element(document.querySelector('chart-div-4')).remove();
	angular.element(document.querySelector('chart-div-1')).remove();
	/*$("#chart-div-3").hide();
	$("#chart-div-4").hide();
	$("#chart-grid-1").hide();*/
	$scope.export_enable=false;
	$scope.data=[];
	$scope.reportList=[{name:'Delivery-Boy', type:'Table'},{name:'Top-Dishes', type:'Table'}];
	$scope.chartDiv=['chart-grid-11', 'chart-grid-21', 'chart-grid-22', 'chart-grid-311', 'chart-grid-312', 'chart-grid-321', 'chart-grid-322', 'chart-grid-411'];
	$scope.report=$scope.reportList[0];
	$scope.toDateSelected=$filter('date')(new Date(),'yyyy-MM-dd');
	$scope.fromDateSelected=$filter('date')(new Date(),'yyyy-MM-dd');

}]);
