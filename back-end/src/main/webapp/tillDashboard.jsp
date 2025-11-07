<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Till Dashboard</title>

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
    <script src="js/angular/tillDashboard.js" type="text/javascript"></script>

    <link rel="stylesheet" href="css/tillDashboard.css?compile=false">
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

</head>
<body ng-app="app">

<div id="wrapper">
    <%--
        <div class="js-spin-overlay"
             style="position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>

        <div class="js-spin-spinner"
             style="position: fixed; width: 60px; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); background-image: url(http://i.imgur.com/uM2gq.gif); height: 60px; z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;"></div>
    --%>

    <div class="session-expired-message"
         style="display: none ;position: fixed;  padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 40%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;">
        Session Expired. Please login again to continue.
    </div>
    <div class="message-box" id="message-box"
         style="display: none ;position: fixed;  padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 40%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;">
        Hello!!
    </div>

    <div id="page-wrapper">
        <div ng-controller="tillController">

            <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
                <div class="navbar-header col-sm-4">
                    <div class="dropdown">
                        <button class="navbar-brand dropdown-toggle ful-selector" type="button" id="dropdownMenu1"
                                data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                            Options
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu fulfillment-dropdown" aria-labelledby="dropdownMenu1">

                        </ul>
                    </div>
                </div>
                <div class="navbar-header col-sm-4" style="left: 9%;">
                    <a class="navbar-brand" href="#">{{$root.restaurantName}}</a>
                </div>
                <div class="navbar-header col-sm-4">
                    <div class="dropdown pull-right">
                        <button class="navbar-brand dropdown-toggle ful-selector" type="button" id="dropdownMenu2"
                                data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                            Welcome {{$root.user.name}}
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu fulfillment-dropdown" aria-labelledby="dropdownMenu2"
                            style="margin-top: 10px;">
                            <li><a href="j_spring_security_logout">Logout</a></li>
                        </ul>
                    </div>
                </div>
            </nav>


            <div class="row">
                <div class="panel panel-primary order-info">
                    <div class="panel-heading">
                        <h3 class="panel-title text-center">Manage Tills</h3>
                    </div>

                    <div class="panel-body">
                        <div class="table-responsive">
                            <table class="table table-hover table-condensed">
                                <thead>
                                <tr>
                                    <th width="15%">Restaurant</th>
                                    <th width="15%">Fulfillment Center</th>
                                    <th width="15%">Till</th>
                                    <th width="10%" style="text-align: right">Balance</th>
                                    <th width="25%" style="text-align: center">Status</th>
                                    <th width="20%" style="text-align: center">Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr class="restaurant-row" ng-repeat-start="restaurant in tillList.restaurantList" ng-click="restaurant.showFulfillmentCenterList = !restaurant.showFulfillmentCenterList">
                                    <td colspan="6">{{restaurant.restaurantName}}</td>
                                </tr>

                                <tr class="ffCenter-row" ng-show="restaurant.showFulfillmentCenterList" ng-repeat-start="ffCenter in restaurant.fulfillmentCenterList">
                                    <td ng-click="ffCenter.showTillList = !ffCenter.showTillList"></td>
                                    <td ng-click="ffCenter.showTillList = !ffCenter.showTillList" colspan="4">{{ffCenter.fulfillmentCenterName}}</td>
                                    <td style="text-align: center">
                                        <input type="button" class="btn btn-default btn-primary" ng-click="addTill(ffCenter,restaurant)" value="Add New Till"/>
                                    </td>
                                </tr>

                                <tr ng-show="restaurant.showFulfillmentCenterList && ffCenter.showTillList" ng-repeat="till in ffCenter.tillList">
                                    <td></td>
                                    <td></td>
                                    <td>{{till.tillName}}</td>
                                    <td style="text-align: right">{{till.balance.toFixed(2)}}</td>
                                    <td style="text-align: center">Status</td>
                                    <td style="text-align: center">
                                        <a class="icon-anchor-tag" ng-click="editTill(till)">
                                            <img class="dashboard-icon" src="images/editOrder-icon.png" alt="Edit Till" data-toggle="tooltip"
                                                 title="Edit Till"/>
                                        </a>
                                        <a class="icon-anchor-tag" ng-click="fetchTransactions(till)">
                                            <img class="dashboard-icon" src="images/list-icon.png" alt="List Transactions" data-toggle="tooltip"
                                                 title="List Transactions"/>
                                        </a>
                                        <a class="icon-anchor-tag" ng-click="handoverTill(till.tillId)">
                                            <img class="dashboard-icon" src="images/handover-icon.png" alt="Handover Till" data-toggle="tooltip"
                                                 title="Handover Till"/>
                                        </a>
                                        <a class="icon-anchor-tag" ng-click="updateTillCash(till)">
                                            <img class="dashboard-icon" src="images/money-icon.png" alt="Add/Remove Money" data-toggle="tooltip"
                                                 title="Add/Remove Money"/>
                                        </a>
                                    </td>
                                </tr>
                                <tr ng-repeat-end></tr>
                                <tr ng-repeat-end></tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <script type="text/ng-template" id="cashCollectionModal.html" style="width:80%">
                <div class="modal-header modal-header-order" ></div>
                <div class="modal-body table"></div>
            </script>


            <script type="text/ng-template" id="addNewTill.html" style="width:80%">
                <div class="modal-header modal-header-order">
                    <b>Add New Till</b>
                    <div ng-if="tillAddSuccess" class="alert alert-success" role="alert">Till added Successfully!!</div>
                    <div ng-if="tillAddError" class="alert alert-warning" role="alert">{{errMsg}}</div>
                </div>
                <div class="modal-body table">
                    <div class="header"></div>
                    <div class="row">
                        <div class="col-lg-12">
                            <form name="tillForm">
                                <div class="form-group">
                                    <label for="tillName">Till Name *</label>
                                    <input type="text" class="form-control" id="tillName" name="tillName" placeholder="Name" ng-model="tillName" required>
                                </div>
                                <span class="error error-msg" ng-show="tillForm.tillName.$error.required && showErr">Till name is required!</span>

                                <div class="form-group">
                                    <label for="balance">Opening Balance *</label>
                                    <input type="number" class="form-control" id="balance" name="balance" placeholder="Balance" ng-model="tillBalance" required>
                                </div>
                                <span class="error error-msg" ng-show="tillForm.balance.$error.required && showErr">Opening Balance is required!</span>
                                <span class="error error-msg" ng-show="tillForm.balance.$error.number && showErr">Invalid opening balance!</span>

                                <div class="form-group">
                                    <label for="ffCenter">Fulfillment Center</label>
                                    <input type="text" class="form-control" id="ffCenter" ng-model="ffCenter.fulfillmentCenterName" readonly>
                                </div>
                                <div class="form-group">
                                    <label for="restaurant">Restaurant</label>
                                    <input type="text" class="form-control" id="restaurant" ng-model="restaurant.restaurantName" readonly>
                                </div>
                                <input type="button" class="btn btn-primary" ng-click="submit()" value="Add" />
                                <input type="button" class="btn btn-danger" ng-click="cancel()" value="Cancel" />
                            </form>
                        </div>
                    </div>
                </div>
            </script>

            <script type="text/ng-template" id="editTill.html" style="width:80%">
                <div class="modal-header modal-header-order">
                    <b>Edit Till</b>
                    <div ng-if="tillEditSuccess" class="alert alert-success" role="alert">Till Updated Successfully!!</div>
                    <div ng-if="tillEditError" class="alert alert-warning" role="alert">{{errMsg}}</div>
                </div>
                <div class="modal-body table">
                    <div class="header"></div>
                    <div class="row">
                        <div class="col-lg-12">
                            <form name="tillForm">
                                <div class="form-group">
                                    <label for="tillName1">Till Name *</label>
                                    <input type="text" class="form-control" id="tillName1" name="tillName" placeholder="Name" ng-model="till.tillName" required>
                                </div>
                                <span class="error error-msg" ng-show="tillForm.tillName.$error.required && showErr">Till name is required!</span>

                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" ng-model="till.isActive"> <b>Active</b>
                                    </label>
                                </div>
                                <input type="button" class="btn btn-primary" ng-click="submit()" value="Update" />
                                <input type="button" class="btn btn-danger" ng-click="cancel()" value="Cancel" />
                            </form>
                        </div>
                    </div>
                </div>
            </script>

            <script type="text/ng-template" id="fetchTransactions.html" style="width:100%">
                <div class="modal-header modal-header-order">
                    <div class="col-sm-12">
                        <form name="tillTransactions">
                            <div class="col-sm-2">
                                <span><b>Fetch Till's Transactions</b></span>
                            </div>

                            <div class="col-sm-2">
                                <span><b>Till : {{till.tillName}}</b></span>
                            </div>

                            <div class="col-sm-2">
                                <div class="col-sm-2">
                                    <span><b>From</b></span>
                                </div>
                                <div class="col-sm-10">
                                    <input type="date" class="form-control" name="fromDate" ng-model="fromDate" required>
                                    <span class="error error-msg" ng-show="showFromDateErr">From Date is required!</span>
                                </div>
                            </div>

                            <div class="col-sm-2">
                                <div class="col-sm-2">
                                    <span><b>To</b></span>
                                </div>
                                <div class="col-sm-10">
                                    <input type="date" class="form-control" name="toDate" ng-model="toDate" required>
                                    <span class="error error-msg" ng-show="showToDateErr">To Date is required!</span>
                                    <span class="error error-msg" ng-show="showErr">{{errMsg}}</span>
                                </div>
                            </div>

                            <div class="col-sm-4">
                                <input type="button" class="btn btn-info" value="Fetch" ng-click="fetchTransactions()">
                                <input type="button" class="btn btn-info" value="Download" ng-click="downloadTillTransactions()">
                                <input type="button" class="btn btn-danger" value="Cancel" ng-click="cancel()">
                            </div>
                        </form>
                    </div>
                </div>

                <div class="modal-body table">
                    <div class="header">
                        <div class="col-sm-12">

                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="table-responsive">
                                <table class="table table-hover table-condensed">
                                    <thead>
                                    <tr>
                                        <th width="15%">Date</th>
                                        <th width="15%">Transaction Id</th>
                                        <th width="15%">Check Id</th>
                                        <th width="20%">Type</th>
                                        <th width="25%">Cashier</th>
                                        <th width="10%">Amount</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr ng-repeat="t in transactions">
                                        <td>{{t.transactionDate}}</td>
                                        <td>{{t.transactionId}}</td>
                                        <td>{{t.checkId}}</td>
                                        <td>{{t.type}}</td>
                                        <td>{{t.cashierName}}</td>
                                        <td>{{t.amount}}</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                </div>
            </script>

            <script type="text/ng-template" id="updateTillCash.html" style="width:80%">
                <div class="modal-header modal-header-order">
                    <div class="col-sm-12">
                        <div class="col-sm-4">
                            <span><b>Update Cash</b></span>
                        </div>
                        <div class="col-sm-4">
                            <span><b>Till : {{till.tillName}}</b></span>
                        </div>
                    </div>
                </div>
                <div class="modal-body table">
                    <div class="header">
                        <div ng-if="updateSuccess" class="alert alert-success" role="alert">Cash updated Successfully!</div>
                        <div ng-if="updateError" class="alert alert-warning" role="alert">{{errMsg}}</div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12">
                            <form name="tillForm">
                                <div class="form-group">
                                    <div class="col-lg-6">
                                        <label for="selectTransactionType">I want to *</label>
                                    </div>
                                    <div class="col-lg-6">
                                        <select class="form-control" id="selectTransactionType" name="transactionType" ng-model="transactionType" required>
                                            <option value="CASH_CREDITED">Add Cash</option>
                                            <option value="CASH_DEBITED">Withdraw Cash</option>
                                        </select>
                                        <span class="error error-msg" ng-show="tillForm.transactionType.$error.required && showErr">Transaction Type is required!</span>
                                    </div>
                                </div>
                                <br/><br/>

                                <div class="form-group">
                                    <div class="col-lg-6">
                                        <label for="currentBalance">Current balance</label>
                                    </div>
                                    <div class="col-lg-6">
                                        <input type="text" class="form-control" id="currentBalance" name="currentBalance" ng-model="till.balance" readonly>
                                    </div>
                                </div>
                                <br/><br/>

                                <div class="form-group">
                                    <div class="col-lg-6">
                                        <label for="amount">Enter amount to be added/withdrawn *</label>
                                    </div>
                                    <div class="col-lg-6">
                                        <input type="number" class="form-control" id="amount" name="amount" ng-model="amount" required>
                                        <span class="error error-msg" ng-show="showAmountErr">Amount is required and should be greater than zero!</span>
                                    </div>
                                </div>
                                <br/><br/>

                                <div class="form-group">
                                    <div class="col-lg-6">
                                        <label for="modifiedBalance">Modified balance</label>
                                    </div>
                                    <div class="col-lg-6">
                                        <input type="text" class="form-control" id="modifiedBalance" name="modifiedBalance" value="{{transactionType==='CASH_CREDITED'?(till.balance+amount):(till.balance-amount)}}" readonly>
                                    </div>
                                </div>
                                <br/><br/>

                                <div class="form-group">
                                    <div class="col-lg-6">
                                        <label for="remarks">Remarks</label>
                                    </div>
                                    <div class="col-lg-6">
                                        <textarea class="form-control" id="remarks" name="remarks" ng-model="remarks" />
                                    </div>
                                </div>
                                <br/><br/>

                                <div class="col-lg-6">
                                </div>
                                <div class="col-lg-6">
                                    <input type="button" class="btn btn-primary" ng-click="submit()" value="Confirm" />
                                    <input type="button" class="btn btn-danger" ng-click="cancel()" value="Cancel" />
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </script>

        </div>
    </div>
</div>
</body>
</html>