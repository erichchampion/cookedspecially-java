/**
 * Author Name :- Abhishek Kumar
 * EmailId :- anshilabhi1991@gmail.com
 * 
 */

angular.module('myApp').service('ChartService',[function() {

	this.plotGraph = function(data,  divId, onSelectCallBack) {
		//data.options.allowHtml=true;
		data.options.width = data.options.width || '100%';
		if(data.dataTable.getNumberOfRows()>20){
			data.options.page='enable';
			data.options.pageSize=20;
			data.options.pagingSymbols={
					prev: 'prev',
					next: 'next'
					}	
		}
		var ele=document.getElementById(divId);
		var chart = new google.visualization[data.chartType](document.getElementById(divId));
		var view = new google.visualization.DataView(data.dataTable);
		if(data.views){
			angular.forEach(data.views, function(value, key){
				view[key](value)
			});
		}
		chart.draw(view, data.options);
		//Add Event Listener
		google.visualization.events.addListener(chart, 'select', function () {
			var selectedData = chart.getSelection(), row, item;
			if(selectedData.length>0){
				row = selectedData[0].row;
				var formatedData={};
				for(var i = 0; i < data.dataTable.getNumberOfColumns() ; i++){
					formatedData[data.dataTable.getColumnLabel(i)]= data.dataTable.getValue(row, i);
				}
				if(onSelectCallBack)
					onSelectCallBack(formatedData);
			}  
		});
		/*google.visualization.events.addListener(chart, 'ready', function () {
		      document.getElementById("chart_image").insertAdjacentHTML('beforeEnd', '<img alt="Chart Image" src="' + chart.getImageURI() + '">');
		  });
		  <a download="custom-filename.jpg" href="/path/to/image" title="ImageName">
    <img alt="ImageName" src="/path/to/image">
</a>

options: {
        theme: 'material',
        titleTextStyle: { fontSize: 12 },
        is3D: false,
        pieSliceText: 'label',
        fontSize: 12,
        pieHole: 0.6,
        pieStartAngle: 180,
        height: 600,
        chartArea: { width: '100%', height: '100%', left: 0, top: 100 },
        legend: { position: 'top', maxLines: 1, textStyle: { fontSize: 12, bold: true, italic: false } }
      }
		  */
		return chart;
	}
	
}]);