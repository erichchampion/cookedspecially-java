<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<title>Manage Gift-Card</title>

<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="css/sb-admin.css?compile=false">
<link rel="stylesheet" href="css/font-awesome.min.css?compile=false">
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="css/customerManagement.css?compile=false">


<script src="js/angular/jquery-1.10.2.js" type="text/javascript"></script>
<script src="js/angular/jquery-ui.js" type="text/javascript"></script>
<script src="js/angular/jquery-ui.min.js" type="text/javascript"></script>
<script src="js/angular/bootstrap.js" type="text/javascript"></script>
<script src="js/angular/angular-1.4.5.js"></script>
<script src="js/angular/angular-animate-1.4.5.js"></script>
<script src="js/angular/ui-bootstrap-tpls-0.13.4.js"></script>
<script src="js/angular/giftCard.js" type="text/javascript"></script>


<style>
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

#button{
display:inline-block;
}

</style>
<script>
	$(function() {
		var d = new Date();
		d.setMonth(d.getMonth() - 1);
		$("#fromDate").datepicker({
			dateFormat : "yy-mm-dd",
			maxDate : '0'
		})
		$("#toDate").datepicker({
			dateFormat : "yy-mm-dd",
			maxDate : '0'
		})

		$(".datePicker").datepicker({
			dateFormat : "yy-mm-dd",
			maxDate : '0'
		})
		
	});
</script>

</head>
<body ng-app="app">
	<div id="wrapper">
		<div class="spinOverlay"
			style="display: none; position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>

		<div class="spinSpinner"
			style="display: none; position: fixed; width: 60px; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); background-image: url(http://i.imgur.com/uM2gq.gif); height: 60px; z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;"></div>

		<div class="js-spin-overlay"
			style="position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>

		<div class="js-spin-spinner"
			style="position: fixed; width: 60px; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); background-image: url(http://i.imgur.com/uM2gq.gif); height: 60px; z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;"></div>

		<div id="page-wrapper">
			<div ng-controller="GiftCardCtrl">
				<nav class="navbar navbar-inverse navbar-fixed-top"
					role="navigation">
					<div class="navbar-header col-sm-4">
						<div class="dropdown"
							ng-show="$root.user.name===$root.currentOwner">
							<button class="navbar-brand dropdown-toggle ful-selector"
								type="button" id="dropdownMenu1" data-toggle="dropdown"
								aria-haspopup="true" aria-expanded="true">
								Options <span class="caret"></span>
							</button>
							<ul class="dropdown-menu fulfillment-dropdown"
								aria-labelledby="dropdownMenu1">
								<li><a ng-click="listAllGiftCard()">List Gift Card</a></li>
								<li><a ng-click="openCreateGiftCardPage()">Create Gift Card</a></li>
								<li><a ng-click="searchGiftCardForActivation()">Activate Gift Card</a></li>
								<li><a ng-click="createGiftCardPannel()">Create & Activate Gift Card</a></li>
							</ul>
						</div>
					</div>
					<div class="navbar-header col-sm-4" style="left: 9%;">
						<a class="navbar-brand" href="#">{{orgName}}</a>
					</div>
					<div class="navbar-header col-sm-4">
						<div class="dropdown pull-right">
							<button class="navbar-brand dropdown-toggle ful-selector"
								type="button" id="dropdownMenu2" data-toggle="dropdown"
								aria-haspopup="true" aria-expanded="true">
								Welcome {{name}} <span class="caret"></span>
							</button>
							<ul class="dropdown-menu fulfillment-dropdown"
								aria-labelledby="dropdownMenu2" style="margin-top: 10px;">
								<li><a href="j_spring_security_logout">Logout</a></li>
							</ul>
						</div>
					</div>
				</nav>
				
				
         <!-- Gift Card List -->
         <div class="panel panel-primary" id="giftCardListPannel">
						<div class="panel-heading" align="center">Gift Card List</div>
						<div class="panel-body" >
						<div  class="panel-body" style="background-color: #EED8D3">
							<p style="display: inline; padding: 5px">
								From-Date: <input type="text" ng-model="fromDateSelected"
									placeholder="yyyy-MM-dd" id="fromDate" name="fromDate" />
							</p>
							<p style="display: inline; padding: 5px;">
								To-Date: <input type="text" ng-model="toDateSelected"
									placeholder="yyyy-MM-dd" id="toDate" name="toDate" />
							</p>
							<p style="display: inline; padding: 5px;">
								Select Status: <select ng-model="status"
									class="bootstrap-select" id="status">
									<option ng-repeat="status in statusOptions">{{status}}</option>
								</select>
							</p>
							<p style="display: inline; padding: 5px;">
								<button type="button" class="btn btn-primary btn-sm"
									ng-click="listAllGiftCard()">List Gift-Card</button>
							</p>
							</div>
						<div class="table-responsive">
						<table class="table table-bordered" id="giftCardListTable">
							<thead>
								<tr>
									<th>GiftCardNo</th>
									<th>Amount</th>
									<th>Category</th>
									<th>CreatedOn</th>
									<th>ExpireOn</th>
									<th>Status</th>
									<th>MobileNoOfRecipient</th>
									<th>EmailIdOfRecipient</th>
									<th>InvoiceId</th>
									<th>SoldOn</th>
									<th>Message</th>
									<th>PurchaserMobileNo</th>
									<th>RedeemedOn</th>
									<th>Action</th>
								</tr>
							</thead>
							<tbody>
								<tr
									ng-repeat="giftCard in giftCardList|orderBy:giftCard:createdOn | filter: { status: '!PRINT'}">
									<td>{{giftCard.formattedGiftCardId}}</td>
									<td>{{giftCard.amount}}</td>
									<td>{{giftCard.category}}</td>
									<td>{{giftCard.createdOn | date :  "y-MM-dd"}}</td>
									<td>{{getExpireOn(giftCard.createdOn, giftCard.expiryDayCount)|date:'y-MM-dd'}}</td>
									<td>{{giftCard.status}}</td>
									<td>{{giftCard.mobileNoOfRecipient}}</td>
									<td>{{giftCard.emailIdOfRecipient}}</td>
									<td>{{giftCard.invoiceId}}</td>
									<td>{{giftCard.soldOn|date:'y-MM-dd'}}</td>
									<td>{{giftCard.message}}</td>
									<td>{{giftCard.purchaserMobileNo}}</td>
									<td>{{giftCard.redeemedOn|date:'y-MM-dd'}}</td>
									<td>
									<button id="button" ng-show="giftCard.status == 'INACTIVE'" class="btn btn-warning"
														type="button" ng-click="rollBack(giftCard.giftCardId)"
														style="margin-top: 0; float: left;">Roll-Back
														</button>
									 <button id="button" style="display: inline; padding: 5px;" ng-show="giftCard.status == 'ACTIVE'" class="btn btn-danger button"
														type="button" ng-click="deActivate(giftCard.giftCardId, true)"
														style="margin-top: 0; float: left;">DeActivate
														</button>
									 <button id="button" ng-show="giftCard.status == 'ACTIVE'" class="btn"
														type="button" ng-click="printGiftCard(giftCard.formattedGiftCardId, giftCard.amount)"
														style="margin-top: 5; float: left;">Print
														</button>
									</td>
								</tr>
							</tbody>
						</table>
						</div>
						</div>
			
		</div> <!-- class="panel panel-primary" id="giftCardListPannel" -->
	    <!-- /Gift Card List-->
	
	    <!-- createCardListPannel -->
		<div class="panel panel-primary" id="createCardListPannel">
						<div class="panel-heading" align="center">Create Gift Card</div>
						<div class="panel-body">
							<form name="createGiftCard" class="form-horizontal" role="form">
								<div class="form-group">
									<label for="category" class="control-label col-sm-4"
										style="font-weight: normal">Category :</label>
									<div class="col-sm-7">
										<select ng-model="category" class="form-control ng-valid"
											id="category">
											<option ng-repeat="category in categoryOptions">{{category}}</option>
										</select>
									</div>
								</div>
								<div class="form-group">
									<label for="giftCardAmount" class="control-label col-sm-4"
										style="font-weight: normal">Amount :</label>
									<div class="col-sm-7">
										<input type="text" min="0" maxlength="8" class="form-control"
											name="giftCardAmount" id="giftCardAmount" ng-model="amount"
											required decimal-number placeholder="0.00">
									</div>
								</div>
								<div class="form-group">
									<label for="expireDayCount" class="control-label col-sm-4"
										style="font-weight: normal">Expire After Days :</label>
									<div class="col-sm-7">
										<input type="text" min="1" maxlength="8" class="form-control"
											name="expireDayCount" id="expireDayCount"
											ng-model="expireDayCount" required placeholder="365">
									</div>
								</div>
								<div class="form-group">
									<label for="noOfCard" class="control-label col-sm-4"
										style="font-weight: normal">Count :</label>
									<div class="col-sm-7">
										<input type="text" min="0" maxlength="8" class="form-control"
											name="noOfCard" id="noOfCard" ng-model="noOfCard" required
											placeholder="1">
									</div>
								</div>

								<div class="form-group">
									<div class="col-sm-offset-4 col-sm-7">
										<button type="submit" class="btn btn-primary "
											ng-disabled="createGiftCard.$invalid"
											ng-click="createGiftCardForPrint()">Create</button>
									</div>
								</div>
							</form>
						</div>
				<div class="panel panel-info" id="giftCardCreatedListPannel">
							<div class="panel-heading" align="center">Gift Card List</div>
							<div>
								<button class="btn btn-link"
									ng-click="exportToExcel('#tableToExport')" style="float: right">
									<span class="glyphicon glyphicon-share"></span> Export to Excel
								</button>
							</div>
							<div id="tableToExport">
								<table class="table table-bordered">
									<thead>
										<tr>
											<th>GiftCardNo</th>
											<th>Amount</th>
											<th>Category</th>
											<th>CreatedOn</th>
											<th>Status</th>
										</tr>
									</thead>
									<tbody>
										<tr
											ng-repeat="giftCard in giftCardLatestList|orderBy:giftCard:createdOn:true">
											<td>{{giftCard.formattedGiftCardId}}</td>
											<td>{{giftCard.amount}}</td>
											<td>{{giftCard.category}}</td>
											<td>{{giftCard.createdOn|date:'dd MMM yyyy'}}</td>
											<td>{{giftCard.status}}</td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
		</div>
	    <!-- createCardListPannel -->
	    
	    <!-- Activate Gift Card -->
	    <div class="panel panel-primary" id="giftCardActivatePannel">
	    	<div class="panel-heading" align="center">Activate Gift Card</div>
				<div  class="panel-body" id="giftCardActivateAwaitingList">
							<div class="panel-body" style="background-color: #EED8D3">
								<p style="display: inline; padding: 5px">
									From-Date: <input type="text" ng-model="fromDateActivateList"
										placeholder="yyyy-MM-dd" class="datePicker"
										name="fromDateActivateList" />
								</p>
								<p style="display: inline; padding: 5px;">
									To-Date: <input type="text" ng-model="toDateActivateList"
										placeholder="yyyy-MM-dd" class="datePicker"
										name="toDateActivateList" />
								</p>

								<p style="display: inline; padding: 5px;">
									<button type="button" class="btn btn-primary btn-sm"
										ng-click="searchGiftCardForActivation()">List Gift
										Card</button>
								</p>
							</div>
							<table class="table table-bordered">
								<thead>
									<tr>
										<th>GiftCardNo</th>
										<th>Amount</th>
										<th>Category</th>
										<th>CreatedOn</th>
										<th>Status</th>
										<th>Activate</th>
									</tr>
								</thead>
								<tbody>
									<tr
										ng-repeat="giftCard in giftCardList|orderBy:giftCard:createdOn">
										<td>{{giftCard.formattedGiftCardId}}</td>
										<td>{{giftCard.amount}}</td>
										<td>{{giftCard.category}}</td>
										<td>{{giftCard.createdOn|date:'y-MM-dd'}}</td>
										<td>{{giftCard.status}}</td>
										<td>
											<div class="col-sm-5">
												<span class="input-group-btn">
													<button id="actiavte" class="btn btn-success"
														type="button" ng-click="openGiftCardForm(giftCard)"
														style="margin-top: 0; float: right;">Activate
														</button>
												</span>
											</div>
											<div class="col-sm-5">
												<span class="input-group-btn">
													<button id="inActiavte" class="btn btn-danger"
														type="button" ng-click="deActivate(giftCard.giftCardId, false)"
														style="margin-top: 0; float: right;">DeActivate
														</button>
												</span>
											</div>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
				<div class="panel-body" id="giftCardActivationForm">
					<form name="giftCardActivation" class="form-horizontal"
								role="form">
								<div class="form-group">
									<label for="giftCardId" class="control-label col-sm-4"
										style="font-weight: normal">GiftCardId :</label>
									<div class="col-sm-7">
										<input type="text" class="form-control" readonly
											name="giftCardId" id="giftCardId" ng-model="giftCardId">
									</div>
								</div>

								<div class="form-group">
									<label for="giftCardActivationAmountt"
										class="control-label col-sm-4" style="font-weight: normal">Amount
										:</label>
									<div class="col-sm-7">
										<input type="number" class="form-control" min="0"
											name="giftCardActivationAmount" id="giftCardActivationAmount"
											ng-model="giftCardActivationAmount" decimal-number
											placeholder="0.0">
									</div>
								</div>

								<div class="form-group">
										<label for="giftCardCategory" class="control-label col-sm-4"
											style="font-weight: normal">Category :</label>
										<div class="col-sm-7">
											<input type="text" class="form-control" readonly
												name="giftCardCategory" id="giftCardCategory"
												ng-model="giftCardCategory">
										</div>
								</div>
								<div class="form-group">
									<label for="activationMsg" class="control-label col-sm-4"
										style="font-weight: normal">Message :</label>
									<div class="col-sm-7">
										<input type="text" min="0" maxlength="150" class="form-control"
											name="activationMsg" id="activationMsg" ng-model="activationMsg"
											placeholder="Wish you ...">
									</div>
								</div>
								<div class="form-group">
										<label for="giftCardExpireOn" class="control-label col-sm-4"
											style="font-weight: normal">Expire On :</label>
										<div class="col-sm-7">
											<input type="text" class="form-control" readonly
												name="giftCardExpireOn" id="giftCardExpireOn"
												ng-model="giftCardExpireOn">
										</div>
								</div>

								<div class="form-group">
										<label for="giftCreationDate" class="control-label col-sm-4"
											style="font-weight: normal">Created On :</label>
										<div class="col-sm-7">
											<input type="text" class="form-control" readonly
												name="giftCreationDate" id="giftCreationDate" 
												ng-model="giftCreationDate">
										</div>
								</div>

								<div class="form-group">
										<label for="invoiceId" class="control-label col-sm-4"
											style="font-weight: normal">InvoiceId :</label>
										<div class="col-sm-7">
											<input type="text" class="form-control"
												name="invoiceId" id="invoiceId" ng-model="invoiceId"
												required>
										</div>
								</div>


								<div class="form-group">
										<div class="col-sm-offset-4 col-sm-7">
											<button type="submit" class="btn btn-primary"
												ng-disabled="giftCardActivation.$invalid" ng-click="activateGiftCard()">Activate</button>
										</div>
								</div>
							</form>
						</div>			
		</div>
	    <!-- Activate Gift Card -->
	      
	      
	    <!-- Create AND Activate -->
	    <div class="panel panel-primary" id="createAndActivatePannel">
	    	<div class="panel-heading" align="center">Create & Activate Gift Card</div>
	    		<div class="panel-body">
					<form name="createGiftCardAndActivateForm" class="form-horizontal" role="form">
						<div class="form-group">
							<label for="category" class="control-label col-sm-4"
										style="font-weight: normal">Category :</label>
									<div class="col-sm-7">
										<select ng-model="category" class="form-control ng-valid"
											id="category">
											<option ng-repeat="category in categoryOptions">{{category}}</option>
										</select>
									</div>
								</div>
						<div class="form-group">
									<label for="msg" class="control-label col-sm-4"
										style="font-weight: normal">Message :</label>
									<div class="col-sm-7">
										<input type="text" min="0" maxlength="150" class="form-control"
											name="msg" id="msg" ng-model="msg"
											placeholder="Wish you ...">
									</div>
								</div>
						<div class="form-group">
									<label for="giftCardAmount" class="control-label col-sm-4"
										style="font-weight: normal">Amount :</label>
									<div class="col-sm-7">
										<input type="number" min="0" class="form-control"
											name="amount" id="amount" ng-model="amount"
											required decimal-number placeholder="0.00">
									</div>
								</div>
						<div class="form-group">
									<label for="expireDayCount" class="control-label col-sm-4"
										style="font-weight: normal">Expire After Days :</label>
									<div class="col-sm-7">
										<input type="number" min="1" max="365*10" class="form-control"
											name="expireDayCount" id="expireDayCount"
											ng-model="expireDayCount" required placeholder="365">
									</div>
								</div>
								
						<div class="form-group">
										<label for="invoiceId" class="control-label col-sm-4"
											style="font-weight: normal">InvoiceId :</label>
										<div class="col-sm-7">
											<input type="text" class="form-control"
												name="invoiceId" id="invoiceId" ng-model="invoiceId"
												required>
										</div>
								</div>

						<div class="form-group">
									<div class="col-sm-offset-4 col-sm-7">
										<button type="submit" class="btn btn-primary "
											ng-disabled="createGiftCardAndActivateForm.$invalid"
											ng-click="createGiftCardAndActivate()">Create And Activate</button>
									</div>
								</div>
							</form>
				</div>
	    </div>
	    
	     </div> <!-- ng-controller="GiftCardCtrl" -->
	     </div><!-- id="page-wrapper" -->
</div><!-- id="wrapper" -->
</body>
</html>