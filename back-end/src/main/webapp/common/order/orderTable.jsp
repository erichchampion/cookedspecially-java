<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title text-center"><%=request.getParameter("tableHeader")%></h3>
    </div>

    <div class="panel-info panel-options">
        <div class="col-lg-4">
            <input type="text" style="width: 200px;" ng-model="<%=request.getParameter("filterName")%>"
                   class="form-control pull-left "
                   placeholder="Search"/>
        </div>

        <div class="col-lg-8">
            <div class="input-group pull-right">

                <a class="btn btn-default" href="#" role="button" ng-click="checkAll('<%=request.getParameter("checkAllValue")%>')">{{<%=request.getParameter("checkAllValue")%>}}</a>
                <input ng-show="{{<%=Boolean.parseBoolean(request.getParameter("showTopCancelBtn"))%>}}" type="button" ng-click="<%=request.getParameter("cancelBtnClickMethod")%>"
                       ng-disabled="<%=request.getParameter("cancelBtnDisabledMethod")%>" class="btn btn-default btn-danger pull-right" value="<%=request.getParameter("cancelBtnValue")%>"/>
                <div type="button" ng-click="<%=request.getParameter("submitBtnClickMethod")%>" class="btn btn-primary pull-right"
                       ng-disabled="<%=request.getParameter("submitBtnDisabledMethod")%>">
                    <img class="dashboard-icon" src="<%=request.getParameter("submitBtnImage")%>"><%=request.getParameter("submitBtnValue")%></div>
            </div>
        </div>
    </div>

    <div class="panel-body">
        <table class="table">
            <thead>
                <th class="col-lg-5">Customer Name (Mob #)</th>
                <th class="col-lg-2">Delivery Time</th>
                <th class="col-lg-5"></th>
            </thead>
        </table>
         <span ng-repeat="order in <%=request.getParameter("list")%> | filter:<%=request.getParameter("filterName")%> | orderBy:'<%=request.getParameter("orderBy")%>'">
            <table class="table cursor-table">
                <tr ng-style="order.cancelled ?{'background-color':'rgba(255, 0, 0, 0.66)'}:((order.paymentMethod === 'PG_PENDING' || order.paymentMethod === 'WALLET_PENDING' ||order.paymentMethod ==='PAYTM_PENDING') ?{'background-color':'hsla(46, 64%, 58%, 0.64)'}:'')">
                    <td class="col-lg-5">
                        <div class="col-lg-1 padding-zero" ng-if="!(order.cancelled)">
                            <input type="checkbox" ng-model="order.orderCheckbox"
                                   ng-change="<%=request.getParameter("checkBoxChangeMethod")%>"
                                   class="orderChkbox" aria-label="...">
                        </div>
                        <div class="col-lg-11" ng-click="toggleDetails(order)">
                            {{order.customerName+' ('+order.customerMobNo+')'}}<span ng-if="order.isfirstOrder===true" style="color:red;font-weight:bold;padding-left:2px">New<span>
                        </div>
                    </td>
                    <td class="col-lg-1" ng-click="toggleDetails(order)"><%--<b>Delivery Time : </b>--%>{{order.deliveryTime| date:"h:mma"}}
                    </td>
                    <td class="col-lg-4" ng-click="toggleDetails(order)"
                        ng-show="order.orderType == 'Table'"><b>{{order.orderType}} : </b>{{order.table}}
                    </td>
                    <td class="col-lg-4" ng-click="toggleDetails(order)"
                        ng-show="order.orderType == 'TakeAway'"><b>{{order.orderType}}</b>
                    </td>
                    <td class="col-lg-4" ng-click="toggleDetails(order)"
                        ng-show="order.orderType == 'Delivery'"><b ng-hide="<%=request.getParameter("hideOrderTypeInfo")%>" >
                        {{order.orderType}} : </b><span ng-show="<%=request.getParameter("showOrderFullAddress")%>">{{order.deliveryAddress+', '}}</span>{{ angular.isUndefined(order.deliveryArea) ? "N/A" : order.deliveryArea}}
                    </td>
                    <td class="col-lg-2" style="width: 13%;">
                        <%--<div style="float:left;" ng-show="{{<%=Boolean.parseBoolean(request.getParameter("showPaymentStatus"))%>}}" ng-click="toggleDetails(order)">{{order.paymentStatus}}</div>--%>
                        <a  ng-if="!(order.cancelled)" ng-click="<%=request.getParameter("orderSubmitBtnClick")%>" class="pull-right">
                            <img class="dashboard-icon" src="<%=request.getParameter("submitBtnImage")%>" alt="<%=request.getParameter("orderSubmitBtnValue")%>" data-toggle="tooltip"
                                    title="<%=request.getParameter("orderSubmitBtnValue")%>"/>
                        </a>
                        <a ng-click="printOrder(order)" class="pull-right" ng-show="{{<%=Boolean.parseBoolean(request.getParameter("showOrderPrintBtn"))%>}}" ><img class="dashboard-icon" src="images/print-icon.png" alt="Print" data-toggle="tooltip" title="Print"/></a>
                    </td>
                </tr>
            </table>

            <span ng-show="order.showDetails">
                <div ng-if="order.cancelled">
                    <i>This order has been cancelled/removed.</i>
                    <a class="btn btn-info" ng-click="<%=request.getParameter("hideCancelledOrderMethod")%>">OK</a>
                </div>
                <div ng-if="order.paymentMethod === 'PG_PENDING' || order.paymentMethod === 'WALLET_PENDING'">
                    <i>This order's payment is due with payment gateway.</i>
                </div>

                <table class="table">
                    <thead>
                    <th class="col-lg-1">Quantity</th>
                    <th class="col-lg-1">Item</th>
                    <th class="col-lg-10">
                        <input type="button" ng-show="{{<%=Boolean.parseBoolean(request.getParameter("showOrderCancelBtn"))%>}}" ng-click="<%=request.getParameter("orderCancelBtnClick")%>"
                               class="btn btn-default btn-danger pull-right" value="<%=request.getParameter("orderCancelBtnValue")%>"/>
                        <%--<a ng-click="printOrder(order)" class="btn btn-default pull-right" role="button" ng-show="{{<%=Boolean.parseBoolean(request.getParameter("showOrderPrintBtn"))%>}}" href="#">Print</a>--%>
                    </th>
                    </thead>
                    <tr ng-repeat="item in order.items">
                        <td class="col-lg-1">{{item.quantity}}
                            <span ng-show="item.instruction"><br><br></span>
                            <span ng-repeat="addonn in item.addOns"><br>{{addonn.quantity}}</span>
                        </td>
                        <td class="col-lg-11" colspan="2">{{item.name}} <b> {{item.dishSizeName!=''?'('+item.dishSizeName+')':''}} </b>
                            <span ng-repeat="addonn in item.addOns"><br>{{addonn.name}}</span>
                             <span ng-show="item.instructions"><br>{{'('+item.instructions+')'}}<br></span>
                        </td>
                    </tr>
                </table>
            </span>
        </span>
    </div>
</div>