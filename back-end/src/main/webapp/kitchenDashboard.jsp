<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">

  <title>Kitchen Dashboard</title>

  <link rel="stylesheet" href="css/bootstrap.css?compile=false">

  <link rel="stylesheet" href="css/sb-admin.css?compile=false">

  <link rel="stylesheet" href="css/font-awesome.min.css?compile=false">

  <script src="js/angular/jquery-1.10.2.js" type="text/javascript"></script>

  <script src="js/angular/bootstrap.js" type="text/javascript"></script>

  <!-- <script src="js/angular/angular-1.2.16.js" type="text/javascript"></script> -->
  <style>
    .strikethrough {
      text-decoration: line-through
    }
  </style>
   <script src="js/angular/angular-1.4.5.js"></script>
    <script src="js/angular/angular-animate-1.4.5.js"></script>
    <script src="js/angular/ui-bootstrap-tpls-0.13.4.js"></script>
  <script src="js/angular/kitchenDashboard.js" type="text/javascript"></script>
  <script src="js/angular/posCallCenter.js" type="text/javascript"></script>
  <link rel="stylesheet" href="css/kitchenDashboard.css?compile=false">

    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

</head>
<body ng-app="app">

<div id="wrapper">
    <div class="js-spin-overlay"
         style="position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>

    <div class="js-spin-spinner"
         style="position: fixed; width: 60px; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); background-image: url(http://i.imgur.com/uM2gq.gif); height: 60px; z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;"></div>

    <div class="session-expired-message"
         style="display: none ;position: fixed;  padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 40%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;">
        Session Expired. Please login again to continue.
    </div>

    <div id="page-wrapper">
        <div ng-controller="orderController">

        <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <div class="navbar-header col-sm-2">
                <div class="dropdown">
                    <button class="navbar-brand dropdown-toggle ful-selector" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                        Options
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu fulfillment-dropdown" aria-labelledby="dropdownMenu1">
                        <li class="dropdown-submenu">
                            <a tabindex="-1" href="#">Filter orders</a>
                            <ul class="dropdown-menu">
                                <!-- <li class="dropdown-submenu">
                                    <a tabindex="-1" href="#">Fulfillment Center</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="#" ng-repeat="fulfillmentCenter in fulfillmentCenterList" ng-click="filterOrdersByFFCenter(fulfillmentCenter)">{{fulfillmentCenter.name}}</a></li>
                                        <li role="separator" class="divider"></li>
                                        <li><a href="#" ng-click="filterOrdersByFFCenter('ALL')">All</a></li>
                                    </ul>
                                </li> -->
                                <li class="dropdown-submenu">
                                    <a tabindex="-1" href="#">Delivery Day</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="#" ng-click="showFutureOrders()">{{$root.ordersOfDay==='today'?'Future':"Today"}}</a></li>
                                    </ul>
                                </li>
                            </ul>
                        </li>
                        <li role="separator" class="divider"></li>
                        <li><a href="#" ng-click="showKitchenScrn()">Kitchen Screen</a></li>
                        <li><a href="#" ng-click="showAllOrderScrn()">All Orders</a></li>
                         <li class="dropdown-submenu">
                                    <a tabindex="-1" href="#">Top Dishes</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="#" ng-click="getTopDishesReport('today')">Today</a></li>
                                        <li><a href="#" ng-click="getTopDishesReport('lastWeek')">Last Week</a></li>
                                        <li><a href="#" ng-click="getTopDishesReport('lastMonth')">Last Month</a></li>
                                        <li><a href="#" ng-click="getTopDishesReport('lastSixMonths')">Last Six Months</a></li>
                                    </ul>
                                </li>
                    </ul>
                </div>
            </div>
            <div ng-show="fulfillmentCenterList.length>1" class="navbar-header col-sm-6" style="left: 22%;">
                <a class="navbar-brand" ng-click="getFfcList()" href="#">{{restaurantName}}{{$root.filterOrdersForFFCenter?(' - '+$root.filterOrdersForFFCenter.fulfillmentCenterName):''}}</a>
            </div>
             <div ng-show="fulfillmentCenterList.length<=1" class="navbar-header col-sm-6" style="left: 22%;">
                <a class="navbar-brand" href="#">{{restaurantName}}{{$root.filterOrdersForFFCenter?(' - '+$root.filterOrdersForFFCenter.fulfillmentCenterName):''}}</a>
            </div>
            <div class="navbar-header col-sm-4">
                <div class="dropdown pull-right">
                    <button class="navbar-brand dropdown-toggle ful-selector" type="button" id="dropdownMenu2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                        Welcome {{$root.user.name}}
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu fulfillment-dropdown" aria-labelledby="dropdownMenu2"  style="margin-top: 10px;">
                        <li><a href="j_spring_security_logout">Logout</a></li>
                    </ul>
                </div>
            </div>
        </nav>

        <div class="row" ng-if="($root.showKitchenScreen)">
            <div class="col-lg-6">
                    <jsp:include page="common/order/orderTable.jsp">
                        <jsp:param name="tableHeader" value='{{"New order(s)"+($root.ordersOfDay==="today"?"":" - Future")}}' />
                        <jsp:param name="filterName" value="orderFilter" />
                        <jsp:param name="checkAllValue" value="checkAllValueNewOrder" />
                        <jsp:param name="submitBtnClickMethod" value="claimOrders()" />
                        <jsp:param name="submitBtnDisabledMethod" value="!(showOptionsNewOrder)" />
                        <jsp:param name="submitBtnValue" value="Claim" />
                        <jsp:param name="list" value="newOrderList" />
                        <jsp:param name="orderBy" value="deliveryTime" />
                        <jsp:param name="checkBoxChangeMethod" value="showOptions(newOrderList,'showOptionsNewOrder')" />
                        <jsp:param name="orderSubmitBtnClick" value="claimOrder(order)" />
                        <jsp:param name="orderSubmitBtnValue" value="Claim" />
                        <jsp:param name="submitBtnImage" value="images/direction_arrow_blue_right_T.png" />
                        <jsp:param name="showOrderPrintBtn" value="false" />
                        <jsp:param name="showOrderCancelBtn" value="false" />
                        <jsp:param name="showTopCancelBtn" value="false" />
                        <jsp:param name="hideCancelledOrderMethod" value="hideCancelledOrder(order,newOrderList)" />
                    </jsp:include>
            </div>

            <div class="col-lg-6">
                    <jsp:include page="common/order/orderTable.jsp">
                        <jsp:param name="tableHeader" value='{{"Order(s) in process"+($root.ordersOfDay==="today"?"":" - Future")}}' />
                        <jsp:param name="filterName" value="orderInProcessFilter" />
                        <jsp:param name="checkAllValue" value="checkAllValueOrderInProcess" />
                        <jsp:param name="cancelBtnClickMethod" value="unclaimOrders()" />
                        <jsp:param name="cancelBtnDisabledMethod" value="!(showOptionsOrdersInProcess)" />
                        <jsp:param name="cancelBtnValue" value="Un-Claim" />
                        <jsp:param name="submitBtnClickMethod" value="readyOrders()" />
                        <jsp:param name="submitBtnDisabledMethod" value="!(showOptionsOrdersInProcess)" />
                        <jsp:param name="submitBtnValue" value="Ready" />
                        <jsp:param name="list" value="orderInProcessList" />
                        <jsp:param name="orderBy" value="deliveryTime" />
                        <jsp:param name="checkBoxChangeMethod" value="showOptions(orderInProcessList,'showOptionsOrdersInProcess')" />
                        <jsp:param name="orderCancelBtnClick" value="unclaimOrder(order)" />
                        <jsp:param name="orderCancelBtnValue" value="Un-Claim" />
                        <jsp:param name="submitBtnImage" value="images/ok_checkmark_green_T.png" />
                        <jsp:param name="orderSubmitBtnClick" value="readyOrder(order)" />
                        <jsp:param name="orderSubmitBtnValue" value="Ready" />
                        <jsp:param name="showOrderCancelBtn" value="true" />
                        <jsp:param name="showOrderPrintBtn" value="true" />
                        <jsp:param name="showTopCancelBtn" value="true" />
                        <jsp:param name="hideCancelledOrderMethod" value="hideCancelledOrder(order,orderInProcessList)" />
                    </jsp:include>
            </div>
        </div>
        </div>
         <script type="text/ng-template" id="selectFfcModal">
                <div class="modal-header">
                    Welcome to {{name}}! Select a Fulfillment Center.
                </div>
                <div class="modal-body">
                    <div ng-repeat="ffc in fulfillmentCenterList" class="cursor-table" ng-click="selectFfc(ffc)">
                        {{ffc.fulfillmentCenterName}}
                    </div>
                </div>
            </script>
       
        <jsp:include page="common/order/allOrders.jsp">
            <jsp:param name="allowEditOrder" value="false" />
            <jsp:param name="allowCancelOrder" value="false" />
            <jsp:param name="showOrderAmountCalculation" value="false" />
        </jsp:include>
    </div>
</div>

</body>
</html>
