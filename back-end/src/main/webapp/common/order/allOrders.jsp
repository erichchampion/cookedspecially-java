
<div class="row" ng-controller="allOrdersController">
    <div class="col-sm-12"  ng-if="$root.showAllOrders">
        <div class="panel panel-primary customer-info">

            <div class="panel-heading">
                <h3 class="panel-title text-center">{{"All orders"+($root.ordersOfDay==="future"?" - Future":"")}}</h3>
            </div>
            <div class="panel-info panel-options">
                <div class="col-sm-4">
                    <div class="input-group">
                        <input type="text" class="form-control" name="searchOrderFilter" style="width: 255px;"
                               ng-model="searchOrderFilter" placeholder="Name/Phone/delivery area/address">
                    </div>
                </div>
            </div>

            <div class="panel-body">
                <table class="table">
                    <thead>
                    <th class="col-sm-3">
                    <div class="col-sm-5"> <a href="#" ng-click="changeListOrder('invoiceId')">Invoice No.</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'invoiceId'" ng-class="{reverse:listOrderReverse}"></span>
		          </div>
                    <div class="col-sm-7"> <a href="#" ng-click="changeListOrder('customerName')">Customer Details</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'customerName'" ng-class="{reverse:listOrderReverse}"></span></div>
                    </th>
					<th class="col-sm-1">
                        <a href="#" ng-click="changeListOrder('deliveryAgent')">Delivered by</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'deliveryAgent'" ng-class="{reverse:listOrderReverse}"></span>
                    </th>
					<th class="col-sm-1">
                        <a href="#" ng-click="changeListOrder('orderSource')">Order Source </a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'orderSource'" ng-class="{reverse:listOrderReverse}"></span>
                    </th>
                    <th class="col-sm-1">
                        <a href="#" ng-click="changeListOrder('status')">Order Status</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'status'" ng-class="{reverse:listOrderReverse}"></span>
                    </th>

                    <th class="col-sm-1">
                        <a href="#" ng-click="changeListOrder('orderTime')">Order Time</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'orderTime'" ng-class="{reverse:listOrderReverse}"></span>
                    </th>

                    <th class="col-sm-1">
                        <a href="#" ng-click="changeListOrder('deliveryTime')">Delivery Time</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'deliveryTime'" ng-class="{reverse:listOrderReverse}"></span>
                    </th>
 					<th class="col-sm-5">
                    <div class="col-sm-2">
                        <a href="#" ng-click="changeListOrder('orderAmount')">Order Amount</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'orderAmount'" ng-class="{reverse:listOrderReverse}"></span>
                    </div>

					<div class="col-sm-4">
					<a href="#" ng-click="changeListOrder('paymentMethod')">Payment Mode</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'paymentMethod'" ng-class="{reverse:listOrderReverse}"></span>
                 
					</div>
                    
                    <div class="col-sm-2">
                        <a href="#" ng-click="changeListOrder('paymentStatus')">Payment Status</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'paymentStatus'" ng-class="{reverse:listOrderReverse}"></span>
                    </div>
                    <div class="col-sm-4">
                    <div class="col-sm-6"><a href="#" style="padding-right:13px"> Action</a></div>
                    <div class="col-sm-6"> <a href="#">Order Access</a></div>
                    </div>
						</th>
                    </thead>
                </table>
                <span ng-repeat="order in allOrdersList | filter: searchOrderFilter| orderBy:listOrderByPredicate:listOrderReverse">
                    <table class="table cursor-table">
                        <tr ng-style="((order.paymentMethod ==='PG_PENDING'||order.paymentMethod === 'WALLET_PENDING'||order.paymentMethod ==='PAYTM_PENDING')&&order.status!=='CANCELLED')?{'background-color':'hsla(46, 64%, 58%, 0.64)'}:''">
                            <td class="col-sm-3" colspan="2" ng-click="toggleDetails(order)">
                                <div class="col-sm-5" style="padding-left: 0px;">
                                    <a href="#" ng-click="printOrder(order)">{{order.invoiceId}}</a>
                                </div>
                                <div class="col-sm-7">
                                    {{order.customerName+' ('+order.customerMobNo+'), '}}
                                    <span>{{order.deliveryAddress+', '}}</span>
                                    {{order.deliveryArea+', '+ $root.city }}
                                    <span ng-if="order.isfirstOrder===true" style="color:red;font-weight:bold;padding-left:2px">New<span>
                                </div>
                            </td>
                            <td class="col-sm-1" ng-click="toggleDetails(order)">{{order.deliveryAgent}}</td>
                            <td class="col-sm-1" ng-click="toggleDetails(order)">{{order.orderSource}}</td>
                            <td class="col-sm-1" ng-click="toggleDetails(order)">{{getFriendlyName(order.status)}}</td>
                            <td class="col-sm-1" ng-click="toggleDetails(order)">{{order.orderTime|date:"d-MMM HH:mm"}}<span ng-if="order.isEdited===true" style="color:red;font-weight:bold;padding-left:2px">(Edited)</span></td>
                            <td class="col-sm-1" ng-click="toggleDetails(order)">{{order.deliveryTime|date:"d-MMM HH:mm"}}
                            </td >
                            <td class="col-sm-5" >
                            <div class="col-sm-2" ng-click="toggleDetails(order)">{{order.orderAmount}}</div>
                            <div class="col-sm-4" ng-click="toggleDetails(order)" ng-if="paymentMethodList==undefined" >
                            <div ng-show="order.paymentMethod=='PAYTM_PENDING' || order.paymentMethod=='PG_PENDING'">
                            <span ng-if="order.status!='CANCELLED'" ng-click="getPaytmStatus(order.checkId)"><a><label style="padding-top:7px">{{order.paymentMethod}}</label></a></span>
                            <span ng-if="order.status=='CANCELLED'"> <label style="padding-top:7px">{{order.paymentMethod}}</label></span>
                           </div>
                           <span ng-show="order.paymentMethod!='PAYTM_PENDING' && order.paymentMethod!='PG_PENDING'"> <label style="padding-top:7px">{{order.paymentMethod}}</label></span>
                           </div>
                           <div class="col-sm-4"  ng-if="paymentMethodList!=undefined"> 
                           <div ng-if=" order.status!=='DELIVERED'"> <label style="padding-top:7px">{{order.paymentMethod}}</label></div>
                           <div ng-if=" order.status=='DELIVERED'">
                           <div ng-show=" order.showChangePaymentMethodOption===true ">
                            <label style="padding-top:7px">{{order.paymentMethod}}</label> 
                            <!--<a ng-click="order.showChangePaymentMethodOption = false" class="small-text">Change</a>-->
                            <button type="button" ng-click="showEditAuthentication(order)" ng-disabled=" order.status=='CANCELLED'" style="float:right" class=" btn btn-primary btn-sm">Change</button>
                        </div>
                        <div ng-show="!order.showChangePaymentMethodOption">
                            <select ng-model="order.updatedPaymentMethod" class="form-control" ng-change="updateDeliveryAmt(order)">
                                <option ng-repeat="method in paymentMethodList">{{method.name}}</option>
                            </select>
                            <button type="button" ng-click="order.showChangePaymentMethodOption = true" class=" btn btn-primary btn-sm">Original</button>
                            <button type="button" ng-click="savePaymentMethod(order)" class="btn  btn-sm btn-success">Save</button>
                        </div>
                        </div>
                            </div>
                            <div class="col-sm-2" ng-click="toggleDetails(order)">{{order.paymentStatus}}</div>
                            <div class="col-sm-4" style="padding-left:0px;padding-right:0px">
                             
                                <div class="col-sm-3" style="padding-left:5px">
                                    <div ng-if="!(allowEditOrder(order,<%=Boolean.parseBoolean(request.getParameter("allowEditOrder"))%>))">
                                        <a><img class="dashboard-icon" src="images/editOrder-icon-grey.png"
                                                style="cursor: not-allowed;" alt="Edit Order" data-toggle="tooltip" title="Edit Order"/></a>
                                    </div>
                                    <div ng-if="allowEditOrder(order,<%=Boolean.parseBoolean(request.getParameter("allowEditOrder"))%>)">
                                        <a  ng-show="$root.user.role!=='deliveryManager'" ng-click="editOrder(order)"><img class="dashboard-icon" src="images/editOrder-icon.png"
                                                                            alt="Edit Order" data-toggle="tooltip" title="Edit Order"/></a>
                                          <a ng-show="$root.user.role==='deliveryManager'"   ng-click="showEditAccess(order, 'Edit')"><img class="dashboard-icon" src="images/editOrder-icon.png"
                                                                            alt="Edit Order" data-toggle="tooltip" title="Edit Order"/></a>
                                    </div>
                                    
                                </div>
                                <div class="col-sm-3" style="padding-left:5px">

                                    <div ng-if="!(allowCancelOrder(order,<%=Boolean.parseBoolean(request.getParameter("allowCancelOrder"))%>))">
                                        <a><img class="dashboard-icon" src="images/cancelOrder-icon-grey.png"
                                                style="cursor: not-allowed;" alt="Cancel Order" data-toggle="tooltip" title="Cancel Order"/></a>
                                    </div>
                                    <div ng-if="allowCancelOrder(order,<%=Boolean.parseBoolean(request.getParameter("allowCancelOrder"))%>)">
                                        <a ng-show="$root.user.role!=='deliveryManager'" ng-click="cancelOrder(order)"><img class="dashboard-icon" src="images/cancelOrder-icon.png"
                                                 alt="Cancel Order" data-toggle="tooltip" title="Cancel Order"/></a>
                                        <a ng-show="$root.user.role==='deliveryManager'"  ng-click="showEditAccess(order,'Cancel')"><img class="dashboard-icon" src="images/cancelOrder-icon.png"
                                                 alt="Cancel Order" data-toggle="tooltip" title="Cancel Order"/></a>
                                    </div>
                                </div>
                               <div class="col-sm-6" style="padding: 0px 3px;">
                                <div ng-if="allowEditOrder(order,<%=Boolean.parseBoolean(request.getParameter("allowCancelOrder"))%>)">
                                  <!--  <input type="checkbox" checked data-toggle="toggle"> -->
                                  <div ng-show="$root.user.role==='deliveryManager'">{{order.allowEdit===true?"Allowed":"NotAllowed"}}</div>
                                   <div ng-show="$root.user.role!=='deliveryManager'" class="btn-group-vertical btn-group-sm" id="allowEditBtn">
                      <!--  <button type="button" class="btn btn-primary btn-sm " style="font-size:10px;padding:3px 0px"  ng-click="getEditAccess(order,true)">Allow</button> -->
                       <button type="button" class="btn btn-primary btn-sm " style="font-size:10px;padding:3px 0px" ng-class="{active : order.allowEdit===true}"  ng-click="getEditAccess(order,true)">Allow</button>
                        <button type="button" class="btn btn-primary btn-sm " style="margin-left:10px;font-size:10px;padding:3px 0px" ng-class="{active : order.allowEdit===false}"  ng-click="getEditAccess(order,false)">NotAllow</button>
                          </div>
    
                            </div>
                                </div>
                            </div>
                            </td>
                        </tr>
                    </table>

                    <span ng-show="order.showDetails">
                        <div ng-if="(order.paymentMethod === 'PG_PENDING' || order.paymentMethod === 'WALLET_PENDING')">
                            <i>This order's payment is due with payment gateway.</i>
                        </div>
                        <table class="table">
                            <thead>
                            <tr>
                                <td class="col-sm-6">Item</td>
                                <td class="col-sm-2">Price</td>
                                <td class="col-sm-2">Quantity</td>
                                <td class="col-sm-2">Amount</td>
                            </tr>
                            </thead>
                            <tr ng-repeat="item in order.items">
                                <td class="col-sm-6">{{item.name}} <b ng-if="item.dishSizeName!=''"> ({{item.dishSizeName !=''?''+item.dishSizeName :''}})</b> {{(item.instructions!=undefined &&
                                    item.instructions!='')?'
                                    ('+item.instructions+')':''}}
                                </td>
                                <td class="col-sm-2">{{item.price}}</td>
                                <td class="col-sm-2">{{item.quantity}}</td>
                                <td class="col-sm-2">{{item.quantity * item.price}}</td>
                            </tr>
                        </table>
                        <div class="row">
                            <div class="col-sm-7"><b>Delivery Instruction : </b>{{order.instructions}}</div>
                            <div class="col-sm-5" ng-show="<%=Boolean.parseBoolean(request.getParameter("showOrderAmountCalculation"))%>">
                             <!--    <div class="row">
                                    <div class="col-sm-6">Sub Total</div>
                                    <div class="col-sm-2 align-right">{{order.subTotal}}</div>
                                    <div ng-repeat="discount in order.discountList">
                                        <div class="col-sm-6">{{discount.name}}</div>
                                        <div class="col-sm-2 align-right">-{{(discount.amountForCurrentOrder).toFixed(2)}}
                                        </div>
                                    </div>
                                    <div class="col-sm-6">Total</div>
                                    <div class="col-sm-2 align-right">{{order.total.toFixed(2)}}</div>
                                    <div ng-repeat="tax in $root.taxList">
                                        <div class="col-sm-6">{{tax.name}}</div>
                                        <div class="col-sm-2 align-right">
                                            {{(tax.chargeType=="PERCENTAGE"?(order.total*tax.taxValue/100):tax.taxValue).toFixed(2)}}
                                        </div>
                                    </div>
                                    <div class="col-sm-6">Delivery Charges</div>
                                    <div class="col-sm-2 align-right"> {{order.deliveryCharges||order.waivedOffDeliveryCharges}}
                                    </div>
                                    <div ng-if="order.deliveryCharges == 0">
                                        <div class="col-sm-6">Waived off Delivery Charges</div>
                                        <div class="col-sm-2 align-right">-{{order.waivedOffDeliveryCharges}}</div>
                                    </div>
                                    <div class="col-sm-6"><b>Net Payable</b></div>
                                    <div class="col-sm-2 align-right"><b>{{order.payableAmount.toFixed(0)}}</b></div>
                                    <div class="col-sm-6">Amount Saved</div>
                                    <div class="col-sm-2 align-right">{{order.amountSaved.toFixed(0)}}</div>
                                </div> -->
                            </div>
                        </div>
                    </span>
                </span>
            </div>
        </div>
    </div>
</div>
