<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<title>Point Of Sale - Dashboard</title>
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
<script src="js/angular/posDashboard.js" type="text/javascript"></script>
<script src="js/angular/posCallCenter.js" type="text/javascript"></script>
<script src="js/angular/customerProfile.js" type="text/javascript"></script>
<link rel="stylesheet" href="css/posDashboard.css?compile=false">
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
</head>

<body ng-app="app">
	<div id="wrapper">
		<div id="appPopUp"
			style="display: none; position: fixed; width: 20%; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 10px; border-top-right-radius: 10px; border-bottom-right-radius: 10px; border-bottom-left-radius: 10px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 40%; top: 35%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;">
			<div style="text-align: center; font-size: 24px">Salad days</div>
			<div style="padding: 10px">The order amount will be added to
				customer's credit account.</div>
			<div style="text-align: center">
				<button type="button" class="btn btn-primary"
					ng-click="customerCreditRefund()">OK</button>
			</div>
		</div>
		<div class="spinOverlay"
			style="display: none; position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>
		<div class="spinSpinner"
			style="display: none; position: fixed; width: 60px; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); background-image: url(http://i.imgur.com/uM2gq.gif); height: 60px; z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;"></div>
		<div class="js-spin-overlay"
			style="position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>
		<div class="js-spin-spinner"
			style="position: fixed; width: 60px; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); background-image: url(http://i.imgur.com/uM2gq.gif); height: 60px; z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;"></div>
		<div class="session-expired-message"
			style="display: none; position: fixed; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 40%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;">
			Session Expired. Please login again to continue.</div>
		<div class="place-order-message" id="place-order-message"
			style="display: none; position: fixed; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 40%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;">
			Placing order...Please wait</div>
		<div id="page-wrapper">
			<div id="posController" ng-controller="orderController">
				<nav id="posNavbar" class="navbar navbar-inverse navbar-fixed-top"
					role="navigation">
					<div id="posHeader" class="navbar-header col-sm-4">
						<div class="dropdown">
							<button class="navbar-brand dropdown-toggle ful-selector"
								type="button" id="dropdownMenu1" data-toggle="dropdown"
								aria-haspopup="true" aria-expanded="true">
								Options <span class="caret"></span>
							</button>
							<ul id="ulMenu" class="dropdown-menu fulfillment-dropdown"
								aria-labelledby="dropdownMenu1">
								<li><a href="#" ng-click="showAllOrderScreen()">All
										Orders </a></li>
								<li ng-if="$root.showAllOrders" class="dropdown-submenu"><a
									tabindex="-1" href="#">Filter orders</a>
									<ul class="dropdown-menu">
										<li class="dropdown-submenu"><a tabindex="-1" href="#">Delivery
												Day</a>
											<ul id="ulSubmenu" class="dropdown-menu">
												<li><a href="#" ng-click="$root.showFutureOrders()">{{$root.ordersOfDay==='today'?'Future':"Today"}}</a>
												</li>
											</ul></li>
									</ul></li>
								<li role="separator" class="divider"></li>
								<li><a href="#" ng-click="showPosScreen()">POS</a></li>
								<li><a href="#" ng-click="showCustomerMamagement()">Customer
										Management</a></li>
							</ul>
						</div>
					</div>
					<div id="centerHeader" class="navbar-header col-sm-4"
						style="left: 9%;">
						<div
							style="background-color: transparent; font-size: 18px; margin-top: 10px;">
							<a ng-click="selectRestaurant()" style="color: White">
								{{$root.showCustomerProfile==true?organizationName:$root.restaurantName}}
								<span class="caret"></span>
							</a>
						</div>
					</div>
					<div id="rightMenu" class="navbar-header col-sm-4">
						<div class="dropdown pull-right">
							<button class="navbar-brand dropdown-toggle ful-selector"
								type="button" id="dropdownMenu2" data-toggle="dropdown"
								aria-haspopup="true" aria-expanded="true">
								Welcome {{$root.user.name}} <span class="caret"></span>
							</button>
							<ul id="ulLogout" class="dropdown-menu fulfillment-dropdown"
								aria-labelledby="dropdownMenu2" style="margin-top: 10px;">
								<li><a href="j_spring_security_logout">Logout</a></li>
							</ul>
						</div>
					</div>
				</nav>
				<div id="divPos" class="row" ng-if="$root.showPos">
					<div id="divMenuType" class="col-sm-6">
						<div class="panel panel-primary">
							<div class="panel-heading">
								<h3 class="panel-title text-center">Select Items</h3>
							</div>
							<div class="panel-body">
								<div id="menuList"
									ng-show="!(showMenuSections || showMenuItems || showMenuItemsSizes || isCustomized)">
									<div class="col-sm-4 menu-category" ng-repeat="menu in menus"
										ng-click="selectMenu(menu)">
										<div class="category-name item-single-line">{{menu.name}}</div>
									</div>
								</div>
								<div id="sectionList" ng-show="showMenuSections">
									<div class="col-sm-4 menu-item" ng-hide="menus.length == 1"
										ng-click="hideMenu()">
										<div class="item-detail item-single-line"
											style="background-color: #DAF0E4;">Back</div>
									</div>
									<div class="col-sm-4 menu-category"
										ng-repeat="section in selectedMenu.sections"
										ng-click="selectSection(section)">
										<div class="category-name item-single-line">{{section.name}}</div>
									</div>
								</div>
								<div id="itemList" ng-show="showMenuItems">
									<div class="col-sm-4 menu-item"
										ng-click="selectMenu(selectedMenu)">
										<div class="item-detail item-single-line"
											style="background-color: #DAF0E4;">Back</div>
									</div>
									<div class="col-sm-4 menu-item"
										ng-repeat="item in selectedSection.items"
										ng-click="selectDishSize(item)">
										<div class="item-detail">
											{{item.name}} <br /> <br /> <b>{{'@ '+ currency + ' ' +
												item.price+'/-'}} </b>
										<span  ng-if="item.actualStockCount>0 && item.stockCount!=undefined ">
										 (Stock - <b>{{item.stockCount}}</b> )
										</span>
										</div>
										
									</div>
								</div>
								<div id="itemSizeList" ng-show="showMenuItemsSizes">
									<div class="col-sm-4 menu-item"
										ng-click="selectSectionItem(selectedSection)">
										<div class="item-detail item-single-line"
											style="background-color: #DAF0E4;">Back</div>
									</div>
									<div class="col-sm-4 menu-item"
										ng-repeat="dishSize in dishSizeItem.dishSize"
										ng-click="addItemToOrder(dishSizeItem,dishSize)">
										<div class="item-detail">
											{{dishSizeItem.name}} - <b>{{dishSize.name}}</b> <br /> <br />
											<b>{{'@ '+ currency + ' ' + dishSize.price+'/-'}}</b>
											<span  ng-if="dishSize.actualStockCount>0 && dishSize.stockCount!=undefined ">
										 (Stock - <b>{{dishSize.stockCount}}</b> )
										</span>
										</div>
										
									</div>
								</div>
								<div id="customizedItem" ng-show="isCustomized">
									<div class="col-sm-4 menu-item"
										ng-click="selectSectionItem(selectedSection)">
										<div class="item-detail item-single-line"
											style="background-color: #DAF0E4;">Back</div>
									</div>
									<div class="col-sm-4 menu-item"
										ng-repeat="custItem in custItemList">
										<var></var>
										<div class="item-detail">
											{{custItem.custItemHadder}}
											<div style="padding-top: 10px">
												<select id="{{custItem.sNo}}" class="form-control"
													style="padding: 0px; margin-left: 3%; margin-right: 3%; width: 94%">
													<option
														ng-repeat="addOn in custItem.addOnList track by $index">{{addOn.name}}</option>
												</select>
											</div>
										</div>
									</div>
									<div class="col-sm-4 menu-item"
										ng-click="submitCustomizeItem(customizedItem)">
										<div class="item-detail item-single-line"
											style="background-color: #DAF0E4;">Submit</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div id="customerDetails" class="col-sm-6">
						<div id="custDetailsPannel"
							class="panel panel-primary customer-info">
							<div class="panel-heading">
								<div class="col-sm-10" style="margin-top: 9px;">
									<h3 class="panel-title text-center">Customer Details</h3>
								</div>
								<div class="col-sm-2 input-group">
									<span class="input-group-btn">
										<button id="resetAll" class="btn btn-default" type="button"
											ng-click="default()" style="margin-top: 0;">Reset
											All</button>
									</span>
								</div>
							</div>
							<div class="panel-body">
								<form id="cutomerForm" name="myForm">
									<div class="col-sm-6">
										<div class="input-group">
											<span style="float: left;"> <select
												class="form-control select-box" style="width: 75px"
												id="countryCode">
													<option value="$root.restCountryCode">{{restCountryCode}}</option>
											</select>
											</span> <input id="txtSearch" type="text" class="form-control"
												style="width: 45%" name="phone"
												ng-model="$root.currentOrder.user.phoneNo"
												placeholder="Phone no..."
												ng-keypress="($event.which === 13)?searchCustomer():0"
												required> <span class="input-group-btn"
												style="float: left;">
												<button id="searchCutomer"
													class="btn btn-default phone-search-btn" type="button"
													ng-click="searchCustomer()" style="margin-top: 0;">Find</button>
											</span>
										</div>
										<div class="error-msg">{{userNotFoundMsg}}</div>
										<span class="error error-msg"
											ng-show="myForm.phone.$error.required && showPlaceOrderErr">Phone
											no. is required!</span> <span class="error error-msg"
											ng-show="myForm.phone.$error.number && showPlaceOrderErr">Invalid
											Phone no.!</span>
										<div class="input-group">
											<input id="txtName" type="text"
												ng-model="$root.currentOrder.user.name" name="cname"
												class="form-control" placeholder="Name" required> <span
												class="error error-msg"
												ng-show="myForm.cname.$error.required && showPlaceOrderErr">Name
												is required!</span>
										</div>
										<div class="input-group">
											<input id="txtEmail" type="email"
												ng-model="$root.currentOrder.user.email" name="email"
												class="form-control" placeholder="Email" > <span
												class="error error-msg"
												ng-show="myForm.email.$error.required && showPlaceOrderErr">Email
												is required!</span> <span class="error error-msg"
												ng-show="myForm.email.$error.email && showPlaceOrderErr">Invalid
												email!</span>
										</div>
									</div>
									<div class="col-sm-6">
										<div class="input-group">
											<input id="txtAddress" type="text"
												ng-model="$root.currentOrder.user.address" name="address"
												class="form-control" placeholder="Street Address" required>
											<span class="error error-msg"
												ng-show="myForm.address.$error.required && showPlaceOrderErr">Address
												is required!</span>
										</div>
										<div class="input-group">
											<select id="txtDeliveryAddr"
												class="form-control deliver-area-select-box"
												ng-model="$root.currentOrder.user.deliveryArea"
												ng-change="updateDeliveryCharges()" required>
												<option selected="selected">Select Delivery Area</option>
												<option ng-repeat="area in $root.allDeliveryAreas">{{area.name}}</option>
											</select>
										</div>
										<span class="error error-msg"
											ng-show="deliveryArea == 'Select Delivery Area' && showPlaceOrderErr">Delivery
											Area is required!</span>
										<div id="txtCity" class="input-group">
											<input type="text" class="form-control" disabled
												ng-model="$root.city">
										</div>
									</div>
								</form>
							</div>
						</div>
						<div id="orderDetailsPannel"
							class="panel panel-primary order-info">
							<div class="panel-heading">
								<h3 class="panel-title text-center">Order Details</h3>
							</div>
							<div class="panel-body">
								<div class="input-group">
									<div class="col-sm-5">
										<span class="input-group-btn">
											<div class="col-sm-8">
												<button id="btnCoupon" ng-click="showOtherCoupOptn = true"
													class="btn btn-info dropdown-toggle" type="button"
													data-toggle="dropdown" aria-haspopup="true"
													aria-expanded="true">Add Coupon</button>
											</div>
											<div class="col-sm-4" id="addDiscount" class="dropdown">
												<button id="btnDiscount"
													class="btn btn-info dropdown-toggle" type="button"
													data-toggle="dropdown" aria-haspopup="true"
													aria-expanded="true">
													Add Discount
													<%--<img class="dashboard-icon" src="images/add-icon.png" alt="Add" title="Add Discount" />--%>
												</button>
												<ul id="ulDiscount" class="dropdown-menu"
													aria-labelledby="dropdownMenu1">
													<li ng-repeat="discount in availableDiscounts"><a
														href="#" ng-click="addDiscount(discount)">{{discount.name}}</a></li>
													<li role="separator" class="divider"></li>
													<li><a href="#" ng-click="showOtherDiscOptn = true">Other</a></li>
												</ul>

											</div>

										</span>
									</div>
									<div class="col-sm-7">
										<div class="col-sm-7">
											<div class="error-msg" ng-show="showPlaceOrderErr">{{placeOrderErrMsg}}</div>
										</div>
										<div class="col-sm-5">
											<span class="input-group-btn">
												<button id="btnPlaceOrder" class="btn btn-success"
													type="button" ng-click="placeOrder()"
													style="margin-top: 0; float: right;">Place Order</button>
											</span>
										</div>
									</div>
									<div class="col-sm-12 discount-options" id="otherDisc"
										style="padding-top: 5px" ng-show="showOtherDiscOptn">
										<div class="col-sm-3">
											<input id="txtDiscountName" class="form-control"
												ng-model="otherDiscName" placeholder="Discount name"
												required>
										</div>
										<div class="col-sm-3">
											<select id="discountType" class="form-control"
												ng-model="otherDiscType">
												<option value="PERCENTAGE">Percentage</option>
												<option value="ABSOLUTE">Absolute</option>
											</select>
										</div>
										<div class="col-sm-3">
											<input id="txtdiscountValue" class="form-control"
												type="number" ng-model="otherDiscValue" placeholder="Value"
												required style="width: 68%;">
										</div>
										<div class="col-sm-3">
											<button id="btnShowDisc" class="btn btn-success"
												type="button"
												ng-click="addOtherDisc(otherDiscName,otherDiscValue,otherDiscType)"
												style="margin: 0;">Add</button>
											<button id="btnHideDisc" class="btn btn-danger" type="button"
												ng-click="showOtherDiscOptn = false" style="margin: 0;">Hide</button>
										</div>
									</div>

									<div class="col-sm-12 discount-options" id="otherCoupon"
										style="padding-top: 5px" ng-show="showOtherCoupOptn">
										<div class="col-sm-4">
											<input id="couponCode"  class="form-control"
												ng-model="otherCoupName" uppercased placeholder="Enter coupon code"
												required>
										</div>
										<div class="col-sm-2"></div>
										<div class="col-sm-3"></div>
										<div class="col-sm-3">
											<button id="btnShowCoup" class="btn btn-success"
												type="button" ng-click="addCoupon(otherCoupName)"
												style="margin: 0;">Add</button>
											<button id="btnHideCoup" class="btn btn-danger" type="button"
												ng-click="showOtherCoupOptn = false" style="margin: 0;">Hide</button>
										</div>
									</div>

								</div>
								<table id="tblOrderItem" class="table table-striped">
									<thead>
										<tr>
											<td class="col-sm-4">Item Name</td>
											<td class="col-sm-2"></td>
											<td class="col-sm-1">Price</td>
											<td class="col-sm-3">Quantity</td>
											<td class="col-sm-1">Amount</td>
											<td class="col-sm-1">Remove</td>
										</tr>
									</thead>
									<tbody>
										<tr
ng-repeat="item in $root.currentOrder.items track by $index">
											<td class="col-sm-4">{{item.name}}
												<b ng-if="item.dishSizeName!=''">({{item.dishSizeName}}) </b> {{(item.instructions!=undefined &&
												item.instructions!='')?' ('+item.instructions+')':''}}</td>
											<td class="col-sm-2"><a href="#"
												ng-click="addItemInstructions(item)">Instructions</a></td>
											<td class="col-sm-1">{{item.price+'/-'}}</td>
											<td class="col-sm-3"><span class="badge"
												ng-click="decreaseQuantity(item)">-</span> <input
												type="number" min="1" max="100000"
												data-ng-change="updateOrderAmountAfterStockChange(item)" style="width: 35px;"
												ng-model="item.quantity" /> <span class="badge"
												ng-click="increaseQuantity(item)">+</span></td>
											<td class="col-sm-1">{{item.price*item.quantity+'/-'}}</td>
											<td class="col-sm-1"><a
												ng-click="deleteItemFromOrderList(item,item.dishSizeId)">
													<img class="dashboard-icon" src="images/delete-icon.png"
													alt="Delete" data-toggle="tooltip" title="Delete" />
											</a></td>
										</tr>
									</tbody>
								</table>
								<hr />
								<div>
									<div id="waveOff" class="col-sm-10">
										Delivery Charges <span
											ng-hide="$root.currentOrder.deliveryChargesWaivedOff">(
											<a href="#" ng-click="waiveOffDeliveryCharges(true)"
											onclick="return false" id="waiveOffDelChages">Waive-off</a> )
										</span>
									</div>
									<div class="col-sm-2 align-right">{{$root.currentOrder.deliveryCharges.toFixed(2)}}</div>
									<div class="com-sm-12"
										ng-show="$root.currentOrder.deliveryChargesWaivedOff">
										<div class="col-sm-10">
											Waived off Delivery Charges ( <a href="#"
												ng-click="waiveOffDeliveryCharges(false)"
												onclick="return false">Remove</a> )
										</div>
										<div class="col-sm-2 align-right">{{'-'+$root.currentOrder.deliveryCharges.toFixed(2)}}</div>
									</div>
									<div id="divSubTotal" class="col-sm-10">Sub Total</div>
									<div class="col-sm-2 align-right">{{getTotalOrderAmount().toFixed(2)}}</div>
									<div
										ng-repeat="discount in $root.orderDiscountList track by $index">
										<div class="col-sm-10">
											{{discount.name+' ('+((discount.type=='ABSOLUTE')? 'Rs.':'')
											+ discount.rawVal + ((discount.type=='PERCENTAGE')?'%':'')
											+')'}} ( <a ng-click="removeDiscount(discount)">Remove</a>)
										</div>
										<div class="col-sm-2 align-right">{{(discount.value).toFixed(2)
											}}</div>
									</div>

									<div
										ng-repeat="coupon in $root.couponAppliedListArray track by $index">
										<div class="col-sm-10">
											{{coupon.couponName}} (<a ng-click="removeCouponCode(coupon)">Remove</a>)
										</div>
										<div class="col-sm-2 align-right">-
											{{(coupon.amount).toFixed(2) }}</div>
									</div>
									<div class="col-sm-12 line-seperator"></div>
									<div id="divTotal" class="col-sm-10">Total</div>
									<div class="col-sm-2 align-right">{{$root.currentOrder.totalOrderAmountAfterDiscount.toFixed(2)}}</div>
									<div id="taxDetails" ng-repeat="tax in $root.orderTaxList">
										<div class="col-sm-10">{{tax.name}}</div>
										<div class="col-sm-2 align-right taxAmount">
											{{tax.value.toFixed(2)}}</div>
									</div>
									
									<div class="col-sm-12 line-seperator"></div>
									<div id="grandTotal" class="col-sm-10">Grand Total</div>
									<div class="col-sm-2 align-right">{{$root.currentOrder.grandTotal.toFixed(2)}}</div>
									<hr />
									<div class="col-sm-12 line-seperator"
										ng-show="$root.billingType=='ONE_OFF'"></div>
									<div class="col-sm-10" ng-show="$root.billingType=='ONE_OFF'">Rounded
										Off</div>
									<div class="col-sm-2 align-right"
										ng-show="$root.billingType=='ONE_OFF'">{{(((($root.currentOrder.grandTotal)*100)/100).toFixed(0))+".00"}}</div>
									<div class="col-sm-10" ng-show="$root.billingType=='ONE_OFF'">Previous
										Balance</div>
									<div class="col-sm-2 align-right"
										ng-show="$root.billingType=='ONE_OFF'">{{($root.cCreditBalance).toFixed(2)}}</div>
									<div class="col-sm-12 line-seperator"
										ng-show="$root.billingType=='ONE_OFF'"></div>
									<div class="col-sm-10">
										<b>Amount to pay</b>
									</div>
									<div class="col-sm-2 align-right"
										ng-show="$root.billingType!=='ONE_OFF'">
										<b>{{($root.billingType=='ONE_OFF'?(((($root.currentOrder.grandTotal+$root.cCreditBalance)*100)/100).toFixed(0)):($root.currentOrder.grandTotal).toFixed())+".00"}}</b>
									</div>
									<div class="col-sm-2 align-right"
										ng-show="$root.billingType==='ONE_OFF'">
										<b>{{($root.currentOrder.grandTotal+$root.cCreditBalance)>0?(((($root.currentOrder.grandTotal+$root.cCreditBalance)*100)/100).toFixed(0)):"0.00"}}</b>
									</div>
									<div class="col-sm-10">Amount you saved</div>
									<div class="col-sm-2 align-right">{{((($root.currentOrder.deliveryChargesWaivedOff?$root.currentOrder.deliveryCharges:0)+$root.currentOrder.finalDiscountFixValue
										+$root.couponSum).toFixed())+".00"}}</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<script type="text/ng-template" id="selectRestaurantModal">
                        <div class="modal-header">
                            Welcome to {{name}}! Select a Restaurant.
                        </div>
                        <div class="modal-body">
                            <div ng-repeat="restaurant in restaurantList" class="cursor-table" ng-click="selectRestaurant(restaurant.restaurantId)">
                                {{restaurant.restaurantName}}
                            </div>
                        </div>
                    </script>
				<script type="text/ng-template" id="customerMultipleAddressModal">
                        <div class="modal-header">
                            Select an address
                        </div>
                        <div class="modal-body">
                            <div ng-repeat="address in addressList" class="cursor-table" ng-click="selectAddress(address)">
                                {{address.customerAddress + ', ' + address.deliveryArea + ', ' + address.city}}
                            </div>
                            <div class="cursor-table" ng-click="selectAddress(undefined)">Add new Address</div>
                        </div>
                    </script>
				<script type="text/ng-template" id="instructionModal">
                        <div class="modal-header">
                            Add {{type=='dish'?"dish":"delivery"}} instructions -
                        </div>
                        <div class="modal-body">
                            <textarea ng-model="instruction" maxlength="1000" style="width: 100%; margin-bottom:5px;" placeholder="Add instructions here..." autofocus></textarea>
                            <input type="button" class="btn btn-primary" ng-click="submitInstruction()" value="Submit">
                            <input type="button" class="btn btn-warning" ng-click="instruction=''" value="Clear">
                            <input type="button" class="btn btn-info" ng-click="cancel()" value="Cancel">
                        </div>
                    </script>
				<script type="text/ng-template" id="placeOrderModal">
                        <div class="modal-header">
                            Place Order
                        </div>
                        <div class="modal-body">
                            <form name="myForm">
                                <div class="input-group">
                                    <div class="col-sm-12">
                                        <div class="col-sm-4">Delivery Time</div>
                                        <div class="col-sm-3" ng-show="!currentOrder.keepOriginalDeliveryTime">
                                            <select ng-model="deliveryDay" name="day" ng-change="deliveryTime=(deliveryDay=='Today'?todayTimes.dateList[0]:tomorrowTimes.dateList[0])">
                                                <option ng-repeat="day in deliveryDayList">{{day}}</option>
                                            </select>
                                        </div>
                                        <span class="error error-msg" ng-show="myForm.day.$error.required && showErr">Delivery Day is required!</span>
                                        <div class="col-sm-3" ng-show="!currentOrder.keepOriginalDeliveryTime">
                                            <select ng-model="deliveryTime" name="time">
                                                <option ng-repeat="time in (deliveryDay=='Today'?todayTimes.dateList:tomorrowTimes.dateList)">{{time}}</option>
                                            </select>
                                        </div>
                                        <span class="error error-msg" ng-show="myForm.time.$error.required && showErr">Delivery Time is required!</span>
                                        <div class="col-sm-6" ng-if="currentOrder.keepOriginalDeliveryTime">
                                            <input type="text" value="{{currentOrder.deliveryTime| date:'d-MMM HH:mm'}}" disabled>
                                        </div>
                                        <div class="col-sm-2" ng-if="currentOrder.isExistingOrder">
                                            <input type="checkbox" ng-model="currentOrder.keepOriginalDeliveryTime">Keep Original
                                        </div>
                                    </div>
                                    <!--  <div class="col-sm-4" ng-show="deliveryDay!='Today'" style="margin-left:15px;padding-top:10px">Place for today</div>
                                    <div class="col-sm-3" ng-show="deliveryDay!='Today'" style="padding-top:10px">
                                        <input type="checkbox" id="placeToday">
                                    </div> -->
                                    <div class="col-sm-12" style="margin-top: 10px;">
                                        <div class="col-sm-4">Order Source</div>
                                        <div class="col-sm-6" ng-show="!currentOrder.keepOriginalOrderSource">
                                            <select ng-model="orderSource" name="source">
                                                <option ng-repeat="source in orderSourceList">{{source.name}}</option>
                                            </select>
                                        </div>
                                        <span class="error error-msg" ng-show="myForm.source.$error.required && showErr">Delivery Source is required!</span>
                                        <div class="col-sm-6" ng-if="currentOrder.keepOriginalOrderSource">
                                            <input type="text" value="{{currentOrder.orderSource}}" disabled>
                                        </div>
                                        <div class="col-sm-2" ng-if="currentOrder.isExistingOrder">
                                            <input type="checkbox" ng-model="currentOrder.keepOriginalOrderSource">Keep Original
                                        </div>
                                    </div>
                                    <div class="col-sm-12" style="margin-top: 10px;">
                                        <div class="col-sm-4">Payment Method</div>
                                        <div class="col-sm-6" ng-show="!currentOrder.keepOriginalPaymentMethod">
                                            <select ng-model="paymentMethod" name="method">
                                                <option ng-repeat="method in paymentMethodList">{{method.name}}</option>
                                            </select>
                                        </div>
                                        <span class="error error-msg" ng-show="myForm.method.$error.required && showErr">Payment Method is required!</span>
                                        <div class="col-sm-6" ng-if="currentOrder.keepOriginalPaymentMethod">
                                            <input type="text" value="{{currentOrder.paymentMethod}}" disabled>
                                            <div ng-if="(currentOrder.paymentMethod==='PG_PENDING'||currentOrder.paymentMethod==='PAYTM_PENDING')">
                                                <span><input type="checkbox" ng-model="paidStatus"  class="paidStatus" value="Paid">Mark as Paid</span>
                                            </div>
                                        </div>
                                        <div class="col-sm-2" ng-if="currentOrder.isExistingOrder">
                                            <input type="checkbox" ng-model="currentOrder.keepOriginalPaymentMethod">Keep Original
                                        </div>
                                    </div>
                                    <div class="col-sm-12" style="margin-top: 10px;">
                                        <textarea ng-model="instructions" maxlength="300" style="width: 100%; margin-bottom:5px;" placeholder="Add delivery instructions here..." autofocus></textarea>
                                    </div>
                                </div>
                                <input type="button" class="btn btn-primary" ng-click="placeOrder(true)" value="Confirm Order">
                                <input type="button" class="btn btn-info" ng-click="changeOrder()" value="Change Order Items">
                            </form>
                        </div>
                    </script>
				<div id="runningOrderPopUp"
					style="display: none; position: fixed; width: 20%; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); z-index: 10001; border-top-left-radius: 10px; border-top-right-radius: 10px; border-bottom-right-radius: 10px; border-bottom-left-radius: 10px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 40%; top: 35%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;">
					<div style="text-align: center; font-size: 24px">Running
						Order</div>
					<div ng-repeat="runningOrder in runningOrderArray| limitTo:1">
						<p>
							Inovice : <a
								ng-href="{{invoiceLinkPrefix}}{{runningOrder.invoiceId}} target="_blank"">{{runningOrder.invoiceId}}</a>
						</p>
						<p>Status : {{runningOrder.status}}</p>
						<p ng-show="runningOrder.status=='OUTDELIVERY'">
							Delivery Person : {{runningOrderArray.deliveryAgent}} <a
								ng-show="runningOrder.length>1"
								style="float: right; padding-top: 10px"
								ng-click="showAllCurrentOrder()">More</a>
						</p>
						<p ng-show="runningOrder.status!=='OUTDELIVERY'">
							Delivery Person : Not Assigned <a ng-show="runningOrder.length>1"
								style="float: right; padding-top: 10px"
								ng-click="showAllCurrentOrder()">More</a>
						</p>
						<div style="text-align: center">
							<button type="button" ng-click="hideRunningOrder()"
								class="btn btn-primary">OK</button>
						</div>
					</div>
				</div>
				<div id="runningOverlay"
					style="display: none; position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>

			</div>
			<jsp:include page="common/order/allOrders.jsp">
				<jsp:param name="allowEditOrder" value="true" />
				<jsp:param name="allowCancelOrder" value="true" />
				<jsp:param name="showOrderAmountCalculation" value="true" />
			</jsp:include>
			<jsp:include page="common/order/customerProfile.jsp"></jsp:include>
		</div>
	</div>
</body>

</html>
