/**
 * Abhishek Kumar
 */

var chart_data;
var org_data;
var cssClassNames = {
	    'headerRow': 'italic-darkblue-font large-font bold-font left-text',
	    'tableRow': '',
	    'oddTableRow': 'beige-background',
	    'selectedTableRow': 'orange-background large-font',
	    'hoverTableRow': '',
	    'headerCell': 'gold-border',
	    'tableCell': '',
	    'rowNumberCell': 'blue-font'};

	  var options = {'showRowNumber': true, 'allowHtml': true, 'cssClassNames': cssClassNames, width: '100%'};

function drawTable() {
    var data = new google.visualization.DataTable();
    angular.forEach(chart_data[0], function(keys){
    	for (var type in keys) {
    		data.addColumn(type, keys[type]); 
    	   }
    });
    data.addRows(chart_data[1])
    
    var view = new google.visualization.DataView(data);

    chart_data[2].splice(0, 1)
    view.setColumns(chart_data[2]);

    var table = new google.visualization.Table(document.getElementById('table_div'));
    //table.draw(view, {showRowNumber: true, width: '100%'} ); 
    table.draw(view, options); 
    
    google.visualization.events.addListener(table, 'select', function() {
    	 var selectedData = table.getSelection(), row, item;
         row = selectedData[0].row;
         item = data.getValue(row,0);
         alert("You selected delivery person:" + item);    
    	});
   }


function drawOrgChart() {
    var data = new google.visualization.DataTable();
    angular.forEach(org_data[0], function(keys){
    	for (var type in keys) {
    		data.addColumn(type, keys[type]); 
    	   }
    });
    data.addRows(org_data[1])
    
    // Create the chart.
    var chart = new google.visualization.OrgChart(document.getElementById('org_div'));
    // Draw the chart, setting the allowHtml option to true for the tooltips.
    chart.draw(data, {allowHtml:true, showRowNumber: true, width: '100%'});
    
    google.visualization.events.addListener(chart, 'select', function() {
   	 var selectedData = chart.getSelection();
        row = selectedData[0].row;
        item = data.getValue(row,0);
        alert("You selected delivery person:" + item); 
        var v=angular.element(document.getElementById('OrderReportCtrl'));
        angular.element(document.getElementById('OrderReportCtrl')).scope().selectedOnOrgLevel();
   	});
  }

function drawOrgDataChart(data){
	org_data=data;
	google.charts.setOnLoadCallback(drawOrgChart());		
}

function getDeliveryReportGraph(data){
	chart_data=data;
	google.charts.setOnLoadCallback(drawTable());		
}