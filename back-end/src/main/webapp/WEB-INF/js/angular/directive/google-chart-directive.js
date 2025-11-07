/**
 * Abhishek Kumar
 */

var app = app || angular.module('myApp', []);
app.directive("googleChart",function(){  
	return{
		restrict : "EA",
		link: function($scope, $elem, $attr){
			var model;
			var initChart = function() {
				model = $scope.$eval($attr.ngModel);
				if (model) {
					var dt = model.dataTable,
					options = {height: '100%', width: '100%' , allowHtml:true};
					chartType = $attr.googleChart;

					//options.title = $scope[$attr.ngModel].title;
					if($scope[$attr.ngModel].title)
						options.title = "$scope[$attr.ngModel].title";

					var googleChart = new google.visualization[chartType]($elem[0]);
					googleChart.draw(dt,options);

					// Watches, to refresh the chart when its data, title or dimensions change

					google.visualization.events.addListener(googleChart, 'select', function () {
						var selectedItem = googleChart.getSelection();
						if (selectedItem) {
							$scope.$apply(function () {
								$scope.$eval(selectCallBack)({
									selectedRowIndex: selectedItem
								});
							});
						}
					}); 
				};
			};

			$scope.$watch($attr.trigger, function(val){
				if (val === true) {
					initChart(); 
				}
			});


		}
	}
});