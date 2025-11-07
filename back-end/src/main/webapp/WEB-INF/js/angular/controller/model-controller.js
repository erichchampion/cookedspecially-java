/**
 * Author Abhishek Kumar
 * anshilabhi1991@gmail.com
 */

'use strict';

angular.module('myApp').controller('ModalInstanceCtrl', ['$scope', '$modalInstance','$timeout','Excel','ReportService','title', 'filename', 'data', function ($scope, $modalInstance,$timeout, Excel,ReportService, title, filename, data) {  
  
$scope.title = title;  
$scope.filename = filename;
$scope.data = data;
  
$scope.close = function () {  
$modalInstance.close();  
};  

$scope.exportToExcel=function(tableId, name, fileName){
	
	var filename = fileName || $scope.filename || $scope.title || "Invoice_List_Delivered";
	var wb=Excel.createWorkbook(filename); 
	var data=ReportService.formateExcelBlobData($scope.data);
	Excel.dataArrayToExcel(data, wb, name);
	wb.save();
	/*var exportHref=Excel.tableToExcel(tableId, name);
	var a = document.createElement('a');
	a.href = exportHref;
	a.download = fileName || $scope.filename || $scope.title || "Invoice_List_Delivered";
	$timeout(function(){a.click();},100);*/
}
    
}]);  