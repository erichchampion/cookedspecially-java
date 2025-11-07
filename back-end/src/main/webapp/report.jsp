<title>Report Dash-board</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="css/sb-admin.css?compile=false">
<link rel="stylesheet" href="css/font-awesome.min.css?compile=false">
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="css/customerManagement.css?compile=false">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.2/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.3.1/css/datepicker3.min.css" />
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.3.0/css/font-awesome.min.css" />


<script src="js/angular/lib/FileSaver.js"></script>
<script src="js/angular/lib/xlsx/jszip.js"></script>
<script src="js/angular/lib/xlsx/shim.js"></script>
<script src="js/angular/lib/xlsx/xlsx.js"></script>

<script src="js/angular/jquery-1.10.2.js" type="text/javascript"></script>
<script src="js/angular/jquery-ui.js" type="text/javascript"></script>
<script src="js/angular/jquery-ui.min.js" type="text/javascript"></script>
<script src="js/angular/bootstrap.js" type="text/javascript"></script>

<script src="js/angular/lib/angularjs/1.6.4/angular.min.js"></script>
<script src="js/angular/ui-bootstrap-tpls-0.13.4.js"></script>
<script src="js/angular/lib/angularjs/1.6.4/angular-animate.min.js"></script>
<script src="js/angular/lib/angularjs/1.6.4/angular-route.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.3.1/js/bootstrap-datepicker.min.js"></script>



<script src="js/angular/lib/google-chart/jsapi.js"></script>

<script src="js/angular/utils/rout.js" type="text/javascript"></script>

<script src="js/angular/lib/bootstrap/moment.js" type="text/javascript"></script>
<script src="js/angular/lib/bootstrap/bootstrap.min.js"
	type="text/javascript"></script>

<script src="js/angular/controller/org-controller.js"
	type="text/javascript"></script>
<script src="js/angular/controller/ffc-controller.js"
	type="text/javascript"></script>

<script src="js/angular/controller/rest-controller.js"
	type="text/javascript"></script>

<script src="js/angular/controller/core-controller.js"
	type="text/javascript"></script>

<script src="js/angular/controller/model-controller.js"
	type="text/javascript"></script>

<script src="js/angular/utils/constant.js" type="text/javascript"></script>
<script src="js/angular/service/report-service.js"
	type="text/javascript"></script>
<script src="js/angular/service/chart-service.js" type="text/javascript"></script>
<script src="js/angular/service/employee-service.js"
	type="text/javascript"></script>

<script src="js/angular/service/org-service.js" type="text/javascript"></script>

<script src="js/angular/factory/request.js" type="text/javascript"></script>

<script src="js/angular/service/util-service.js" type="text/javascript"></script>
<script src="js/angular/factory/excel.js" type="text/javascript"></script>

<script src="js/angular/directive/header.js" type="text/javascript"></script>
<script src="js/angular/directive/spinner.js" type="text/javascript"></script>
<script src="js/angular/directive/datepicker.js" type="text/javascript"></script>

<style>
.myApp-model-class .modal-header-primary {
	color: #fff;
	padding: 9px 15px;
	border-bottom: 1px solid #eee;
	background-color: #428bca;
	-webkit-border-top-left-radius: 5px;
	-webkit-border-top-right-radius: 5px;
	-moz-border-radius-topleft: 5px;
	-moz-border-radius-topright: 5px;
	border-top-left-radius: 5px;
	border-top-right-radius: 5px;
}

.strikethrough {
	text-decoration: line-through
}

input, select {
	width: 200px;
	border: 1px solid #000;
	padding: 5px;
	margin: 0;
	height: 28px;
	-moz-box-sizing: border-box;
	-webkit-box-sizing: border-box;
	box-sizing: border-box;
}

input {
	text-indent: 4px;
}

#button {
	display: inline-block;
}

table {
	border-collapse: separate !important;
}

.google-visualization-table-td {
	text-align: center;
	cursor:pointer;
}

.google-visualization-table-th {
	text-align: center;
	height: 30px;
	font-size: 14px;
	color: green;
}

.google-visualization-table-div-page {
	text-align: right;
	height: 30px;
}

.google-visualization-table-div-page [role="button"] {
	line-height: 18px;
	font-size: 14px;
	color: black;
	vertical-align: top;
}

.google-visualization-table-div-page {
	line-height: 20px;
}

.chart-sec .row > div{
    outline: 1px solid #337ab7;
}

</style>

</head>
<body>
	<div id="wrapper">
		<div id="page-wrapper">
			<div id="ReportController1" ng-controller="core-Controller">
				<spinner></spinner>
				<header></header>
				<div class="panel panel-primary" id="aprent-pannel"
					style="margin-top: 50px !important">
					<div class="panel-heading" align="center">
						<img src="images/chart.png" width="30" height="30"> <font
							color="#FFFF00">{{reportHeaderName}}</font> <img
							src="images/chart.png" width="30" height="30">
					</div>
					<div class="panel-body">
						<div id="orgChart_11"></div>

						<div style="background-color: #EED8D3; padding-top: 15px">
							<div class="container-fluid" align="center">
								<div class="row" align="center">
									<div class="col-md-4">
										<div class="input-group" id='reportLevel'>
											<span class="input-group-addon" style="font-weight: 550;">Required
												Report Level:</span> <select ng-model="selectedReportLevel"
												class="form-control"
												ng-options="level for level in report_level"
												ng-change="onRequiredReportListChange()">
											</select>
										</div>
									</div>
									<div class="col-md-4">
										<div class="input-group" id='restaurantSelect'>
											<span class="input-group-addon" style="font-weight: 550;"
												ng-show="selectedReportLevel=='Restaurant' || selectedReportLevel=='FulfillmentCenter'">Restaurant:</span>
											<select ng-model="restaurant" class="form-control"
												ng-show="selectedReportLevel=='Restaurant' || selectedReportLevel=='FulfillmentCenter'"
												ng-options="restaurant as restaurant.restaurantName for restaurant in employeeProfile.restaurantList"></select>
										</div>
									</div>
									<div class="col-md-4">
										<div class="input-group" id='ffcSelect'>
											<span ng-show="selectedReportLevel=='FulfillmentCenter'"
												style="font-weight: 550;" class="input-group-addon">
												FFC: </span> <select
												ng-show="selectedReportLevel=='FulfillmentCenter'"
												class="form-control" ng-model="ffc"
												ng-options="ffc as ffc.fulfillmentCenterName for ffc in restaurant.fulfillmentCenterList"></select>
										</div>
									</div>
								</div>
							</div>
							<hr style="border: 1px solid #337ab7">
							<div ng-view=""></div>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>