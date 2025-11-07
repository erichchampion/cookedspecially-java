<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Manage Credits - Dashboard</title>

    <link rel="stylesheet" href="css/bootstrap.css?compile=false">

    <link rel="stylesheet" href="css/sb-admin.css?compile=false">

    <link rel="stylesheet" href="css/font-awesome.min.css?compile=false">

    <script src="js/angular/jquery-1.10.2.js" type="text/javascript"></script>
    <script src="js/angular/jquery-ui.js" type="text/javascript"></script>
    <script src="js/angular/jquery-ui.min.js" type="text/javascript"></script>

    <script src="js/angular/bootstrap.js" type="text/javascript"></script>

    <script src="js/angular/angular-1.4.5.js"></script>
    <script src="js/angular/angular-animate-1.4.5.js"></script>
    <script src="js/angular/ui-bootstrap-tpls-0.13.4.js"></script>
    <style>
        .strikethrough {
            text-decoration: line-through
        }
    </style>
    <script src="js/angular/manageCredits.js" type="text/javascript"></script>

    <link rel="stylesheet" href="css/posDashboard.css?compile=false">
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

</head>
<body ng-app="app">

<div id="wrapper">
    <div id="page-wrapper">
        <div ng-controller="ManageCredits">

             <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
               <div class="navbar-header col-sm-4">
               <button class="navbar-brand dropdown-toggle ful-selector" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                        Options
                        <span class="caret"></span>
                    </button>
                      <ul class="dropdown-menu fulfillment-dropdown" style="margin-top:15px!important" aria-labelledby="dropdownMenu1">
                       <li><a href="#"  ng-click="showStructureCredit()" >Structure credit</a></li>
                          <li class="dropdown-submenu">
                                    <a tabindex="-1" href="#">Aged Credit</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="#" ng-click="showAgedCredit('1')">One Day</a></li>
                                        <li><a href="#" ng-click="showAgedCredit('3')">Three Days</a></li>
                                        <li><a href="#" ng-click="showAgedCredit('7')">One Week</a></li>
                                        <li><a href="#" ng-click="showAgedCredit('14')">Two Week</a></li>
                                        <li><a href="#" ng-click="showAgedCredit('30')">One Month</a></li>
                                    </ul>
                                </li>
                         </ul>
                </div>
                <div class="navbar-header col-sm-4" style="left: 9%;">
                    <a class="navbar-brand" href="#">{{restaurantName}}</a>
                </div>
                <div class="navbar-header col-sm-4">
                   <div class="dropdown pull-right">
                        <button class="navbar-brand dropdown-toggle ful-selector" type="button" id="dropdownMenu2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                            Welcome {{user.name}}
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu fulfillment-dropdown" aria-labelledby="dropdownMenu2"  style="margin-top: 10px;">
                            <li><a href="j_spring_security_logout">Logout</a></li>
                        </ul>
                    </div>
                </div>
            </nav>

<div class="row" ng-if="$root.showAllBills" >
    <div class="col-sm-10 col-md-offset-1" >
       <div class="panel panel-primary customer-info">

            <div class="panel-heading">
                <h3 class="panel-title text-center">Credit Bills</h3>
            </div>
            <div class="panel-info panel-options">
                <div class="col-lg-6">
           <div class="input-group" >
                        <input type="text" class="form-control" name="searchAllCreditBillsFilter" style="width: 255px;border-radius:4px"
                               ng-model="searchAllCreditBillsFilter" placeholder="Name/Phone/email/address">
                    </div>
        </div>
        <div class="col-lg-6">
        <select class="form-control" id="sel1" style="width: 255px" ng-model="selectedCreditType" ng-change="filterByCreditType(selectedCreditType)"  >
                   <option ng-init=" " ng-repeat="cr in creditTypes|filter :filterNull('banner')" value="{{cr.banner}}">{{cr.banner}}</option>
                </select>
        </div>

        <div class="col-lg-8">
            <div class="input-group pull-right">
               <!--  <div type="button"  class="btn btn-primary pull-right">
                  <img class="dashboard-icon" src="images/delivery.png" alt="Dispatch"  title="Dispatch"/>Dispatch</div> -->
            </div>
        </div>
            </div>

            <div class="panel-body">
                <table class="table">
                    <thead>
                    <th>
                        <a href="#" ng-click="changeListStrCreditBills('banner')">Credit Type</a>
                        <span class="sortorder" ng-show="listStrCreditBillByPredicate === 'banner'" ng-class="{reverse:listStrCreditBillReverse}"></span>
                    </th>
                    <th>
                        <a href="#" ng-click="changeListStrCreditBills('customerDetails')">Customer Details</a>
                        <span class="sortorder" ng-show="listStrCreditBillByPredicate === 'customerDetails'" ng-class="{reverse:listStrCreditBillReverse}"></span>
                    </th>
                    <th>
                        <a href="#" ng-click="changeListStrCreditBills('creationTime')">Statement Date</a>
                        <span class="sortorder" ng-show="listStrCreditBillByPredicate === 'creationTime'" ng-class="{reverse:listStrCreditBillReverse}"></span>
                    </th>
                    <th>
                        <a href="#" ng-click="changeListStrCreditBills('billStatus')">Bill Status</a>
                        <span class="sortorder" ng-show="listStrCreditBillByPredicate === 'billStatus'" ng-class="{reverse:listStrCreditBillReverse}"></span>
                    </th>
 					<th>
                        <a href="#" ng-click="changeListStrCreditBills('billAmount')">Bill Amount</a>
                        <span class="sortorder" ng-show="listStrCreditBillByPredicate === 'billAmount'" ng-class="{reverse:listStrCreditBillReverse}"></span>
                    </th>
                    <th>
                       <a href="#" ng-click="changeListStrCreditBills('paymentStatus')">Action</a>
                        <span class="sortorder" ng-show="listStrCreditBillByPredicate === 'paymentStatus'" ng-class="{reverse:listStrCreditBillReverse}"></span>
					</th>
                    </thead>
                    <tr ng-repeat=" bills in allCreditBills |orderBy:listStrCreditBillByPredicate:listStrCreditBillReverse | filter:searchAllCreditBillsFilter|filter: equalThan('currentBillAmount', 0)|filter:selectedCreditType">

                    <td>{{bills.banner}}</td>
                    <td>{{bills.name + " (" +bills.mobileNo+") "}}<br>{{bills.email}}<br>{{bills.billingAddress}}</td>
                    <td>{{bills.generationTime|date:'MMM dd yyyy'}}</td>
                    <td>{{bills.status}}</td>
                    <td>{{bills.currentBillAmount}}</td>
                    <td>
                        <a ng-click="printCustomerCreditBill(bills.billId)"><img class="dashboard-icon" src="images/print-icon.png" alt="Print"  title="Print"/></a>
                        <a ng-click="dispatchBills(bills.billId)" ng-show="bills.status===('DELIVERED'||'PAID')" style=" pointer-events: none;cursor:default!important;opacity: .4;"  disabled ><img class="dashboard-icon" src="images/delivery.png" alt="already delivered"  title="already delivered"/></a>
                         <a ng-click="dispatchBills(bills.billId)" ng-show="bills.status!==('DELIVERED'||'PAID')" ><img class="dashboard-icon" src="images/delivery.png" alt="Dispatch"  title="Dispatch"/></a>
                         <a ng-click="openPayCreditPanel(bills)" ng-show="($root.user.role==='admin'&& bills.status==='PAID')" style=" pointer-events: none;cursor:default!important;opacity: .4;"><img class="dashboard-icon" src="images/mark-delivered.png" alt="already paid"  title="already Paid"/></a>
                        <a ng-click="openPayCreditPanel(bills)" ng-show="($root.user.role==='admin' && bills.status!=='PAID')"><img class="dashboard-icon" src="images/mark-delivered.png" alt="Mark as Paid"  title="Mark as Paid"/></a>
                     </td>
                    </tr>
                </table>
                
            </div>
        </div>
    </div>
</div>
<div class="row" id="payCreditPanel" ng-if="$root.showPayCredit">
<div class="col-md-12 col-sm-12 col-xs-12" style="margin-top:20px">
<button type="button" class="btn btn-link" ng-click="showStructureCredit()" >Back</button>
</div>
<div class="col-md-8 col-md-offset-2 col-sm-12 col-sm-offset-0 col-xs-12 col-xs-offset-0">
<div class="panel panel-primary">
  <div class="panel-heading">
    <h3 class="panel-title" style="text-align:center">Pay Credit</h3>
  </div>
  <div class="panel-body">
  <form name="payCreditForm" ng-submit="markAsPaid()" class="form-horizontal" >
   <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="amtPay">Amount To pay</label>
    <div class="col-sm-9 col-xs-9">
      <input type="text" ng-model="billData.currentBillAmount" required   class="form-control" id="amtPay" placeholder="Enter amount to pay">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="comments">Comments</label>
    <div class="col-sm-9 col-xs-9">
     <textarea class="form-control" ng-model="billData.comments" required style="resize: none;" rows="5" id="comments"></textarea>
    </div>
  </div>
  <div class="form-group">
  <label class="control-label col-sm-3 col-xs-3" for="paymentMode">Pay by</label>
   <div class="col-sm-9 col-xs-9" ng-init="billData.radioValue='COD'">
    <label class="radio-inline"><input ng-model="billData.radioValue" value ="COD" type="radio"    name="optradio">COD</label>
   <label class="radio-inline"><input ng-model="billData.radioValue"  value ="Online" type="radio"  name="optradio">Online</label>
    <label class="radio-inline"><input ng-model="billData.radioValue" value ="CardMachine" type="radio" name="optradio">Card Machine</label>
    
    </div>
  </div>
  
  <div class="form-group"> 
    <div class="col-sm-offset-3 col-sm-9 col-xs-9 col-xs-offset-3">
      <button type="submit"  ng-disabled="payCreditForm.$invalid" class="btn btn-success">Pay</button>
      <button type="button" ng-click="showStructureCredit()"  class="btn btn-danger">Cancel</button>
    </div>
  </div>
</form>
  </div>
</div>
</div>
</div>

            <script type="text/ng-template" id="selectFulfillmentCenterModal">
                <div class="modal-header">
                    Welcome to {{name}}! Select a Fulfillment Center.
                </div>
                <div class="modal-body">
                    <div ng-repeat="ffc in fulfillmentCenterList" class="cursor-table" ng-click="selectFulfillmentCenter(ffc.fulfillmentCenterId)">
                        {{ffc.fulfillmentCenterName}}
                    </div>
                </div>
            </script>
       
        <jsp:include page="common/order/agedCredit.jsp"></jsp:include>
     </div>
    </div>
</div>
</body>
</html>
