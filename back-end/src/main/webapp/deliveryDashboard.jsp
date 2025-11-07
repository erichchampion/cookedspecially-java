<%@page import="java.util.Calendar"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">

  <title>Delivery Dashboard</title>

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
    #tableSelectRegister  > tbody > tr > td
{
	vertical-align:middle;
}

  </style>
  <script src="js/angular/deliveryDashboard.js" type="text/javascript"></script>
  <script src="js/angular/posCallCenter.js" type="text/javascript"></script>
  <script src="js/angular/cashCollection.js" type="text/javascript"></script>
  <script src="js/angular/creditDispatch.js" type="text/javascript"></script>
  <script src="js/angular/allCreditBills.js" type="text/javascript"></script>

  <link rel="stylesheet" href="css/deliveryDashboard.css?compile=false">
  <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

</head>
<body ng-app="app" >

<div id="wrapper">
<div  id="appPartialPopUp"
         style=" display:none;position: fixed;width:20%;  padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 10px; border-top-right-radius: 10px; border-bottom-right-radius: 10px; border-bottom-left-radius: 10px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 40%; top: 35%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;">
        
        <form name="partialPaymentForm" role="form"><div style="text-align:center;font-size:24px">Partial Payment</div>
        <div style="padding:10px"><label>Partial Amount:</label><input ng-model="ordAmount"  class="form-control" type="text" placeholder="Enter partial Amount"  required /></div>
         <div style="padding:10px"><label>Change Amount : {{partialOrder.changeAmount}}</label> </div>
        <div style="text-align:center">
        <button type="button" class="btn btn-primary" ng-disabled="(partialOrder.orderAmount+partialOrder.creditBalance)<ordAmount" ng-click="showAuthentication(partialOrder,ordAmount)">OK</button>
        <button type="button" class="btn btn-primary" ng-click="closePartialPopUp()">Cancel</button>
        </div>
        </form>
    </div>
    <div  id="appPopUp"
         style=" display:none;position: fixed;width:20%;  padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 10px; border-top-right-radius: 10px; border-bottom-right-radius: 10px; border-bottom-left-radius: 10px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 40%; top: 35%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;">
        <div style="text-align:center;font-size:24px">Salad days</div>
        <div style="padding:10px">The order amount will be added to customer's credit account.</div>
        <div style="text-align:center">
        <button type="button" class="btn btn-primary" ng-click="customerCreditRefund()">OK</button>
        </div>
    </div>
<div class="spinOverlay"
         style=" display:none;position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>

<div class="spinSpinner"
         style=" display:none;position: fixed; width: 60px; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); background-image: url(http://i.imgur.com/uM2gq.gif); height: 60px; z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;"></div>

    <div class="js-spin-overlay"
         style="position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>
      <div id="js-overlay"
         style="display:none;position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>
    <div class="js-spin-spinner"
         style="position: fixed; width: 60px; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); background-image: url(http://i.imgur.com/uM2gq.gif); height: 60px; z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;"></div>
  <div id="js-spinner"
         style="display:none;position: fixed; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;">
         <label id="lblText" style="font-weight:normal">Adding Rs 500 to sale Sales Register.</label>
         </div>
    <div class="session-expired-message"
         style="display: none ;position: fixed;  padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 40%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;">
        Session Expired. Please login again to continue.
    </div>
    <div class="place-order-message" id="saleRegisterPopUP"
         style="display: none ;position: fixed;  padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; max-width:30%; left: 40%;right:30% ;top: 50%; background-position: center; background-repeat: no-repeat no-repeat;">
      <center>  
        <label style="padding:5px;float:left;margin-top:6px;font-weight:normal" id="lblError"></label> <a type="button" style="float:right;opacity:0.4" class="close" ng-click="OK()" >&times;</a>
        	</center>
    </div>
    <div class="place-order-message" id="remarksPopUp"
         style="display: none ;position: fixed; padding:10px ; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; max-width:30%; left: 40%;right:30% ;top: 40%; background-position: center; background-repeat: no-repeat no-repeat;">
       
       <center> 
       <h3>Remarks</h3>
       <form name="formRemarks"  role="form">
       <div style="padding-left:25px;padding-right:25px">
        <textarea style="resize: none;width:100%" type="text" id="cancelEditRemarks" ng-model="cancelEditRemarks"></textarea>
        <button class="btn btn-primary " type="submit"  style="margin-top:10px" ng-disabled="formRemarks.$invalid" ng-submit="remarksSubmit(formRemarks.$valid)" >Submit</button> 
        </div>
        </form>
         </center> 
         
    </div>

    <div id="page-wrapper">
        <div ng-controller="orderController" ng-init="getSaleRegisterList()">
        <fieldset ng-disabled="IsNavigationVisible">
        <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <div class="navbar-header col-sm-2">
            <fieldset ng-disabled="IsOptionVisible" >
                <div class="dropdown" ng-show="$root.user.name===$root.currentOwner">
                    <button class="navbar-brand dropdown-toggle ful-selector" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                        Options
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu fulfillment-dropdown" aria-labelledby="dropdownMenu1">
                       <!--  <li class="dropdown-submenu">
                            <a tabindex="-1" href="#">Filter orders</a>
                            <ul class="dropdown-menu">
                                <li class="dropdown-submenu">
                                    <a tabindex="-1" href="#">Fulfillment Center</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="#" ng-repeat="fulfillmentCenter in fulfillmentCenterList" ng-click="filterOrdersByFFCenter(fulfillmentCenter)">{{fulfillmentCenter.name}}</a></li>
                                        <li role="separator" class="divider"></li>
                                        <li><a href="#" ng-click="filterOrdersByFFCenter('ALL')">All</a></li>
                                    </ul>
                                </li>
                                <li class="dropdown-submenu">
                                    <a tabindex="-1" href="#">Delivery Day</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="#" ng-click="showFutureOrders()">{{$root.ordersOfDay==='today'?'Future':"Today"}}</a></li>
                                    </ul>
                                </li>
                            </ul>
                        </li> -->
                        <li><a href="#" ng-click="showFutureOrders()">{{$root.ordersOfDay==='today'?'Future':"Today"}}</a></li>
                        <!--  <li><a href="#" ng-click="selectSaleRegiter()">Change Sale Register</a></li> -->
                        <li role="separator" class="divider"></li>
                        <li><a  ng-click="$root.showCashCollectionScreen()">Collect Cash</a></li>
                        <li><a   ng-click="showDeliveryScreen()" >Delivery Screen</a></li>
                        <li><a   ng-click="showAllOrderss()" >All Orders</a></li>
                         <li><a  ng-click="showSalesSummary()">Sales Summary</a></li>
                         <li><a  href="analysisAndReport/orderReport/" target="_blank">Report-Dashboard</a></li>
                        <!-- <li role="separator" class="divider"></li>
                         <li><a href="#"  ng-click="showCreditDispatch()" >Credit Dispatch</a></li>
                         <li><a href="#" ng-click="showAllCreditBills()"  >All Credit Bills</a></li>-->
                         
                    </ul>
                </div>
           </fieldset>
            </div>
            <div class="navbar-header col-sm-6" style="left: 22%;">
                <button ng-show="$root.saleRegisterList.length>1" class="btn btn-link btn-lg" href="#" ng-click="selectSaleRegiter()" style="color:white;border:none">{{$root.parentRestaurantId===null?restName:restaurantName}}{{$root.filterOrdersForFFCenter?(' - '+$root.filterOrdersForFFCenter.fulfillmentcenterName):''}} <span  class="caret" ng-click="selectSaleRegiter()"></span></button>
                <button ng-show="$root.saleRegisterList.length==1" class="btn btn-link btn-lg" href="#" style="color:white;border:none">{{$root.parentRestaurantId===null?restName:restaurantName}}{{$root.filterOrdersForFFCenter?(' - '+$root.filterOrdersForFFCenter.fulfillmentcenterName):''}}</button>
            </div>
            <div class="navbar-header col-sm-4">
                <div class="dropdown pull-right">
                    <button class="navbar-brand dropdown-toggle ful-selector" type="button" id="dropdownMenu2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                        Welcome {{$root.user.name}}
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu fulfillment-dropdown" aria-labelledby="dropdownMenu2"  style="margin-top: 10px;">
                        <li><a href="j_spring_security_logout">Logout</a></li>
                        <li><a  ng-click="showLockRegister()" ng-show="$root.user.name===$root.currentOwner"  >{{$root.lockSaleRegister?'Delivery Screen':'Lock Register'}}</a></li>
                        <li><a  ng-hide="($root.user.name!==$root.currentOwner)&&$root.user.role!==('admin' ||'restaurantManager'||'fulfillmentCenterManager')" ng-disabled="getPendingSalesTotal === 0"  ng-click="ShowCloseSaleRegister()" >Close Register</a></li>
                        <li><a ng-hide="($root.user.name!==$root.currentOwner)&&$root.user.role!==('admin' ||'restaurantManager'||'fulfillmentCenterManager')"  ng-click="handoverSaleRegister()">Handover</a></li>
                    </ul>
                </div>
            </div>
        </nav>
</fieldset>

        <div class="row" ng-if="$root.deliveryScreen">
            <div class="col-lg-6">

                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title text-center">Orders in Kitchen{{$root.ordersOfDay==="today"?"":" - Future"}}</h3>
                    </div>

                    <div class="panel-info panel-options">
                        <div class="col-lg-12">
                            <input type="text" style="width: 200px;" ng-model="allOrderFilter"
                                   class="form-control pull-left "
                                   placeholder="Search"/>
                        </div>
                    </div>

                    <div class="panel-body">
                        <table class="table">
                            <thead>
                            <th class="col-lg-4">Customer Name (Mob #)</th>
                            <th class="col-lg-2">Delivery Time</th>
                            <th class="col-lg-4">Delivery Area</th>
                            <th class="col-lg-2"><div class=""></div>Status</th>
                            </thead>
                        </table>
                         <span ng-repeat="order in allOrdersList | filter:allOrderFilter | orderBy:'deliveryTime'">
                            <table class="table cursor-table">

                                <tr ng-style="order.cancelled ?{'background-color':'rgba(255, 0, 0, 0.66)'} : (order.paymentMethod === 'PG_PENDING' ||order.paymentMethod ==='PAYTM_PENDING'||order.paymentMethod === 'WALLET_PENDING' ?{'background-color':'hsla(46, 64%, 58%, 0.64)'}:'')">
                                    <td ng-click="toggleDetails(order)" class="col-lg-4">
                                               {{order.customerName+' ('+order.customerMobNo+')'}}
                                               <span ng-if="order.isfirstOrder===true" style="color:red;font-weight:bold;padding-left:2px">New<span>
                                    </td>
                                    <td class="col-lg-2" ng-click="toggleDetails(order)">{{order.deliveryTime|date:"h:mma"}}</td>
                                    <td class="col-lg-4" ng-click="toggleDetails(order)">{{order.deliveryAddress +', '+ order.deliveryArea}}</td>
                                    <td class="col-lg-2">
                                        <div class="col-lg-6" style="padding-left: 0;" ng-click="toggleDetails(order)">{{order.status=="PENDING"?"Claimed":"Unclaimed"}}</div>
                                        <div class="col-lg-6">
                                            <a ng-click="printOrder(order)" class="pull-right" style="margin-left: 50px;">
                                                <img class="print-icon" src="images/print-icon.png"
                                                     alt="Print" data-toggle="tooltip"
                                                     title="Print"/>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </table>

                            <span ng-show="order.showDetails">
                                <div ng-if="order.cancelled">
                                     <i>This order has been cancelled/removed.</i>
                                     <a class="btn btn-info" ng-click="hideCancelledOrder(order,allOrdersList)">OK</a>
                                </div>
                                <div ng-if="order.paymentMethod === 'PG_PENDING'">
                                    <i>This order's payment is due with payment gateway.</i>
                                </div>
                                <table class="table">
                                    <thead>
                                    <th class="col-lg-1">Quantity</th>
                                    <th class="col-lg-1">Item</th>
                                    <th class="col-lg-10"></th>
                                    </thead>
                                    <tr ng-repeat="item in order.items">
                                        <td class="col-lg-1">{{item.quantity}}
                                            <span ng-show="item.instruction"><br><br></span>
                                            <span ng-repeat="addonn in item.addOns"><br>{{addonn.quantity}}</span>
                                        </td>
                                        <td class="col-lg-11" colspan="2">{{item.name}} {{item.dishSizeName!=''?'-'+item.dishSizeName:''}} 
                                            <span ng-show="item.instructions"><br>{{'('+item.instructions+')'}}<br></span>
                                            <span ng-repeat="addonn in item.addOns"><br>{{addonn.name}}</span>
                                        </td>
                                    </tr>
                                </table>
                            </span>
                        </span>
                    </div>
                </div>
            </div>

            <div class="col-lg-6">
                    <jsp:include page="common/order/orderTable.jsp">
                        <jsp:param name="tableHeader" value='{{"Ready order(s)"+($root.ordersOfDay==="today"?"":" - Future")}}' />
                        <jsp:param name="filterName" value="readyOrderFilter" />
                        <jsp:param name="checkAllValue" value="checkAllValueReadyOrder" />
                        <jsp:param name="cancelBtnClickMethod" value="unclaimOrders()" />
                        <jsp:param name="cancelBtnDisabledMethod" value="!(showOptionsReadyOrders)" />
                        <jsp:param name="cancelBtnValue" value="Un-Claim" />
                        <jsp:param name="submitBtnClickMethod" value="dispatchOrders()" />
                        <jsp:param name="submitBtnDisabledMethod" value="!(showOptionsReadyOrders)" />
                        <jsp:param name="submitBtnValue" value="Dispatch" />
                        <jsp:param name="list" value="readyOrderList" />
                        <jsp:param name="orderBy" value="deliveryTime" />
                        <jsp:param name="checkBoxChangeMethod" value="showOptions(readyOrderList,'showOptionsReadyOrders')" />
                        <jsp:param name="orderCancelBtnClick" value="unclaimOrder(order)" />
                        <jsp:param name="orderCancelBtnValue" value="Un-Claim" />
                        <jsp:param name="submitBtnImage" value="images/delivery.png" />
                        <jsp:param name="orderSubmitBtnClick" value="dispatchOrder(order)" />
                        <jsp:param name="orderSubmitBtnValue" value="Dispatch" />
                        <jsp:param name="showOrderCancelBtn" value="false" />
                        <jsp:param name="showOrderPrintBtn" value="true" />
                        <jsp:param name="showTopCancelBtn" value="false" />
                        <jsp:param name="showPaymentStatus" value="true" />
                        <jsp:param name="hideOrderTypeInfo" value="true" />
                        <jsp:param name="showOrderFullAddress" value="true" />
                        <jsp:param name="hideCancelledOrderMethod" value="hideCancelledOrder(order,readyOrderList)" />
                    </jsp:include>
            </div>
        </div>

        <script type="text/ng-template" id="myModalContent.html">
            <div class="modal-body table">
                <div class="row">
                    <div class="col-lg-5"><b>Payment Method</b></div>
                    <div class="col-lg-1"> : </div>
                    <div class="col-lg-6 delivery-payment-method">
                        <div ng-show="showChangePaymentMethodOption">
                            <b>{{paymentMethod}}</b> &nbsp;&nbsp;&nbsp;
                           <%--	 <a ng-click="showChangePaymentMethodOption = false" class="small-text">Change</a>--%>
                       <%--	<div  ng-if="order.paymentMethod==='PG_PENDING'">
								<span><input type="checkbox" ng-model="paidStatus"  class="paidStatus" value="Paid">Mark as Paid</span>
						</div>--%>
						 </div>
                        <div ng-show="!showChangePaymentMethodOption">
                            <select ng-model="updatedPaymentMethod">
                                <option ng-repeat="method in paymentMethodList">{{method.name}}</option>
                            </select>
                            <a ng-click="showChangePaymentMethodOption = true" class="small-text">Original</a>
                        </div>
                    </div>

                </div>
                <div class="row">
                    <div class="col-lg-5"><b>Order Amount</b></div>
                    <div class="col-lg-1"> : </div>
                    <div class="col-lg-6 delivery-payment-method"><b>{{orderAmount}}</b></div>
                </div>
               <div class="row">
                    <div class="col-lg-5"><b>Credit Amount</b></div>
                    <div class="col-lg-1"> : </div>
                    <div class="col-lg-6 delivery-payment-method"><b>{{creditBalance}}</b></div>
                </div>
                <div class="row">
                    <div class="col-lg-5">Delivery Person</div>
                    <div class="col-lg-1"> : </div>
                    <div class="col-lg-6">
                        <select id="deliveryBoySelect" ng-model="deliveryAgent">
                            <option ng-repeat="boy in deliveryBoyList | orderBy:'name'" value="{{boy.id}}">{{boy.name}}</option>
                        </select>
                    </div>
                </div>
                <div id="cashDiv" class="row" ng-show="getOrderPaymentMethodType(order)&&(orderAmount+creditBalance)!==0">
                    <div class="col-lg-5">Change Options<br>
                        <div id="cash1" class="col-lg-3 delivery-amt" ng-class="{'delivery-amt-selected' : (deliveryAmt===deliveryAmt1)}" ng-click="setDeliveryAmt(deliveryAmt1,'cash1')">{{deliveryAmt1}}</div>
                        <div id="cash2" class="col-lg-3 delivery-amt" ng-class="{'delivery-amt-selected' : (deliveryAmt===deliveryAmt2)}" ng-click="setDeliveryAmt(deliveryAmt2,'cash2')">{{deliveryAmt2}}</div>
                        <div id="cash3" class="col-lg-3 delivery-amt" ng-class="{'delivery-amt-selected' : (deliveryAmt===deliveryAmt3)}" ng-click="setDeliveryAmt(deliveryAmt3,'cash3')">{{deliveryAmt3}}</div>
                        <div id="cash4" class="col-lg-3 delivery-amt" ng-class="{'delivery-amt-selected' : (deliveryAmt===0)}" ng-click="setDeliveryAmt(0,'cash4')">0</div>
                    </div>
                    <div class="col-lg-1"> : </div>
                    <div class="col-lg-6">
                        <input type="text" ng-model="deliveryAmt"  style="width: 50px;">
                    </div>

                </div>
            </div>
            <div class="modal-footer modal-footer-order">
                <div class="col-lg-3"></div>
                <div class="col-lg-5">
                    <input type="button" class="btn btn-default btn-primary" ng-click="dispatch()"
                           ng-disabled="disableDispatchBtn()" value="Dispatch"/>
                    <input type="button" class="btn btn-default btn-danger" ng-click="cancel()" value="Cancel"/>
                </div>
                <div class="col-lg-4"></div>
            </div>
        </script>

        <script type="text/ng-template" id="myModalList.html" style="width:80%">
            <div class="modal-header modal-header-order" >
                <div class="col-sm-7">
                    <div class="row">
                        <div class="col-sm-5">Delivery Person</div>
                        <div class="col-sm-1"> : </div>
                        <div class="col-sm-6">
                            <select ng-model="deliveryAgent">
                                <option ng-repeat="boy in deliveryBoyList | orderBy:'name'" value="{{boy.id}}">{{boy.name}}</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="col-sm-5">
                    <input type="button" class="btn btn-default btn-primary" ng-click="dispatch()"
                           ng-disabled="disableDispatchBtn()" value="Dispatch"/>
                    <input type="button" class="btn btn-default btn-danger" ng-click="cancel()" value="Cancel"/>
                </div>
            </div>

            <div class="modal-body table">
                <div class="header">
                    <div class="col-sm-2"><b>Customer Name(Mob #)</b></div>
                    <div class="col-sm-3"><b>Address</b></div>
                    <div class="col-sm-2"><b>Payment Method</b></div>
                    <div class="col-sm-1"><b>Order Amount</b></div>
                    <div class="col-sm-1"><b>Credit Amount</b></div>
                    <div class="col-sm-3"><b>Change Options</b></div>
                </div><br/><br/>
                <div id="{{order.id}}" class="row" ng-repeat="order in orderList">
                    <div class="col-sm-2">{{order.customerName+' ('+order.customerMobNo+')'}}<span ng-if="order.isfirstOrder===true" style="color:red;font-weight:bold;padding-left:2px">New<span></div>
                    <div class="col-sm-3">{{order.deliveryAddress+', '+order.deliveryArea}}</div>
                  <%--  <div class="col-lg-2 delivery-payment-method">{{order.paymentMethod}}
                        <select ng-model="order.paymentMethod" ng-change="updateDeliveryAmt(order)">
                            <option ng-repeat="method in paymentMethodList">{{method.name}}</option>
                        </select>
                    </div>--%>

                    <div class="col-lg-2 delivery-payment-method">
                        <div ng-show="order.showChangePaymentMethodOption">
                            <b>{{order.paymentMethod}}</b> &nbsp;&nbsp;&nbsp;
                           <%-- <a ng-click="order.showChangePaymentMethodOption = false" class="small-text">Change</a>
							<div  ng-if="order.paymentMethod==='PG_PENDING'">
								<span><input type="checkbox" ng-model="paidStatus"  class="paidStatus" value="Paid">Mark as Paid</span>
							</div>--%>
                        </div>
                        <div ng-show="!order.showChangePaymentMethodOption">
                            <select ng-model="order.updatedPaymentMethod" ng-change="updateDeliveryAmt(order)">
                                <option ng-repeat="method in paymentMethodList">{{method.name}}</option>
                            </select>
                            <a ng-click="order.showChangePaymentMethodOption = true" class="small-text">Original</a>
                        </div>
                    </div>
                    <div class="col-sm-1">{{order.orderAmount}}</div>
                    <div class="col-sm-1">{{order.creditBalance}}</div>
                    <div class="col-sm-3">
                        <div ng-show="getOrderPaymentMethodType(order)&& (order.orderAmount+order.creditBalance)!==0">
                            <div id="{{order.id}}-cash1" class="col-sm-3 delivery-amt" ng-class="{'delivery-amt-selected':order.deliveryAmt==order.deliveryAmt1}" style="width: 10%;" ng-click="setDeliveryAmt(order,order.deliveryAmt1,'cash1')">{{order.deliveryAmt1}}</div>
                            <div id="{{order.id}}-cash2" class="col-sm-3 delivery-amt" ng-class="{'delivery-amt-selected':order.deliveryAmt==order.deliveryAmt2}" style="width: 10%;" ng-click="setDeliveryAmt(order,order.deliveryAmt2,'cash2')">{{order.deliveryAmt2}}</div>
                            <div id="{{order.id}}-cash3" class="col-sm-3 delivery-amt" ng-class="{'delivery-amt-selected':order.deliveryAmt==order.deliveryAmt3}" style="width: 10%;" ng-click="setDeliveryAmt(order,order.deliveryAmt3,'cash3')">{{order.deliveryAmt3}}</div>
                            <div id="{{order.id}}-cash4" class="col-sm-3 delivery-amt" ng-class="{'delivery-amt-selected':order.deliveryAmt==0}" style="width: 10%;" ng-click="setDeliveryAmt(order,0,'cash4')">0</div>
                            <input type="text" ng-model="order.deliveryAmt" value="0" class="modal-list-del-amt-input-box">
                        </div>
                        <div ng-show="!getOrderPaymentMethodType(order)">
                            <input type="text" class="modal-list-del-amt-input-box" value="0" disabled="disabled">
                        </div>
                    </div>

                </div>
            </div>
        </script>
        
        
   <script type="text/ng-template" id="selectSaleRegisterModal.html">
          
    
     <div class="modal-header">
<!--<a type="button" class="close" data-dismiss="modal" href="j_spring_security_logout">&times;</a>-->
                   <h4 class="modal-title" style="text-align:center"> Welcome to {{restName}} ! Select a SaleRegister.</h4>
                </div>
                <div class="modal-body">
                
<table class="table-hover table " id="tableSelectRegister">
											<thead style="background-color:#77ACD9">
												<tr>
													<!--<th>Sale Register</th>-->
                                                    <th>Fulfillment Center</th>
													<th>Balance</th>
                                                     <th>Status</th>
													<th>Action</th>
												</tr>
											</thead>
											<tbody>
												<tr ng-repeat="saleRegister in saleRegisterList" class="cursor-table" >
													<!--<td><button type="button" class="btn btn-link btn-sm" ng-click="selectSaleRegister(saleRegister)" ng-disabled="saleRegister.status!=='OPEN' ||($root.user.name!==saleRegister.currentOwnerName && $root.user.role==='deliveryManager')">{{saleRegister.tillName}}</button></td>-->
													<!--<td>{{saleRegister.tillName}}</td>-->
                                                    <td>{{saleRegister.fulfillmentcenterName}}</td>
                                                    <td>{{saleRegister.balance}}</td>
                                                  <td>{{saleRegister.status === "OPEN" ?saleRegister.status+" ("+ saleRegister.currentOwnerName+")" : saleRegister.status}}</td>
                                                     <td ng-show="saleRegister.status === 'OPEN'">
                                                      <button type="button" class="btn btn-primary btn-sm" ng-click="selectSaleRegister(saleRegister)"  >{{$root.user.name!==saleRegister.currentOwnerName ? "VIEW" : "SELECT"}}
                                                       </button>
                                                       </td>
													<td  ng-show="saleRegister.status !== 'OPEN'"><button type="button" class="btn btn-primary btn-sm"  ng-click="selectSaleRegister(saleRegister)">{{saleRegister.status === "OPEN" ? "SELECT" : "OPEN"}}</button></td>
												</tr>
												
											</tbody>
										</table>
                </div>
<!-- ng-disabled="$root.user.name!==saleRegister.currentOwnerName && $root.user.role==='deliveryManager'"-->


            </script>
            
                    <script type="text/ng-template" id="editOrderAccess.html">
            <div class="modal-header modal-header-order" >
<a type="button" class="close" data-dismiss="modal" ng-click="closeModal()" style="padding-right:20px">&times;</a>
               <center>  <label id="lblHeader" class="modal-title" style="text-align:center;font-size:24"> Update Order Access </label> </center>
            </div>

            <div class="modal-body">
                <div class="col-md-12" 
		style="margin-top: 20px;margin-bottom:15px" >
		<div class="row">
			<div class="col-md-12 ">
			<!--<div><h2 style="text-align:center;padding-bottom:10px">Edit Order Access </h2></div>-->
				<div class="row">
					<div class="col-md-3">
						<img class="img-responsive" src="images/editAccess.png" alt="editAccess">
					</div>
					<div class="col-md-9">
					   <form name="formEditAccess" class="form-horizontal" role="form" ng-submit="$root.requestEditAccess(formEditAccess.$valid)">
                                                     <div class="form-group" >
													<label class=" control-label">FFC, Restaurant Manager please approve access.</label>
												</div>
												<div class="form-group">
													<label for="editAccessUsername" class=" control-label col-sm-4"
														style="font-weight: normal">Username :</label>
													<div class="col-sm-7">
														<input type="text" 
															class="form-control" name="editAccessUsername" id="editAccessUsername"
															ng-model="editAccessUsername" required  >
													</div>
												</div>
												<div class="form-group">
													<label for="editAccessPassword" class=" control-label col-sm-4"
														style="font-weight: normal">Password :</label>
													<div class="col-sm-7">
														<input type="password" 
															class="form-control" name="editAccessPassword" id="editAccessPassword"
															ng-model="editAccessPassword" required  >
													</div>
												</div>
												<div class="form-group">
													<label for="editAccessRemark" class=" control-label col-sm-4"
														style="font-weight: normal">Remarks :</label>
													<div class="col-sm-7">
														<textarea style="resize: none" type="text"
															class="form-control" id="editAccessRemark" ng-model="editAccessRemark"
															required></textarea>
													</div>
												</div>
												<div class="form-group">
													<div class="col-sm-offset-4 col-sm-7">
														<button type="submit" class="btn btn-primary"
															ng-disabled="formEditAccess.$invalid">Submit</button>
													</div>
												</div>
											</form>
					</div>
				</div>

			</div>

		</div>

	</div>
               
            </div>
        </script>
        
         <script type="text/ng-template" id="editOrderAuthentication.html">
            <div class="modal-header modal-header-order" >
<a type="button" class="close" data-dismiss="modal" ng-click="closeModal()" style="padding-right:20px">&times;</a>
               <center>  <label  class="modal-title" style="text-align:center;font-size:24"> Update Order Authentication </label> </center>
            </div>

            <div class="modal-body">
                <div class="col-md-12" 
		style="margin-top: 20px;margin-bottom:15px" >
		<div class="row">
			<div class="col-md-12 ">
				<div class="row">
					<div class="col-md-3">
						<img class="img-responsive" src="images/editAccess.png" alt="editAccess">
					</div>
					<div class="col-md-9">
					   <form name="formEditAuthentication" class="form-horizontal" role="form" ng-submit="requestEditAuthentication(formEditAuthentication.$valid)">
                                                     <div class="form-group" >
													<label class=" control-label">Please authenticate to modify the order.</label>
												</div>
												<div class="form-group">
													<label for="editAuthUsername" class=" control-label col-sm-4"
														style="font-weight: normal">Username :</label>
<label for="editAuthUsername" class=" control-label col-sm-7"
														style="font-weight: normal;text-align:left">{{userName}}</label>
													
												</div>
												<div class="form-group">
													<label for="editAuthPassword" class=" control-label col-sm-4"
														style="font-weight: normal">Password :</label>
													<div class="col-sm-7">
														<input type="password" 
															class="form-control" name="editAuthPassword" id="editAuthPassword"
															ng-model="editAuthPassword" required  >
													</div>
												</div>
												<div class="form-group">
													<label for="editAuthRemark" class=" control-label col-sm-4"
														style="font-weight: normal">Remarks :</label>
													<div class="col-sm-7">
														<textarea style="resize: none" type="text"
															class="form-control" id="editAuthRemark" ng-model="editAuthRemark"
															required></textarea>
													</div>
												</div>
												<div class="form-group">
													<div class="col-sm-offset-4 col-sm-7">
														<button type="submit" class="btn btn-primary"
															ng-disabled="formEditAuthentication.$invalid">Submit</button>
													</div>
												</div>
											</form>
					</div>
				</div>

			</div>

		</div>

	</div>
               
            </div>
        </script>
        
            
    </div>

    <jsp:include page="common/order/allOrders.jsp">
        <jsp:param name="allowEditOrder" value="false" />
        <jsp:param name="allowCancelOrder" value="true" />
        <jsp:param name="showOrderAmountCalculation" value="true" />
    </jsp:include>
    <jsp:include page="common/order/salesSummary.jsp"></jsp:include>
     <jsp:include page="common/order/lockRegister.jsp"></jsp:include>
     <jsp:include page="common/order/handover.jsp"></jsp:include>
     <jsp:include page="common/order/cashCollection.jsp"></jsp:include>
     <jsp:include page="common/order/creditDispatch.jsp"></jsp:include>
     <jsp:include page="common/order/allCreditBills.jsp"></jsp:include>
    </div>
</div>

</body>
</html>