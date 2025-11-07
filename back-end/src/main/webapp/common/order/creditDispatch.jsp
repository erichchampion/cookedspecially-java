
<div class="row" ng-controller="CreditDispatch">
    <div class="col-sm-12"  ng-if="$root.creditDispatch">
       <div class="panel panel-primary customer-info">

            <div class="panel-heading">
                <h3 class="panel-title text-center">Credit Bills</h3>
            </div>
            <div class="panel-info panel-options">
                <div class="col-lg-4">
           <div class="input-group">
                        <input type="text" class="form-control" name="searchAllCreditBillsFilter" style="width: 255px;"
                               ng-model="searchAllCreditBillsFilter" placeholder="Name/Phone/delivery area/address">
                    </div>
        </div>

        <div class="col-lg-8">
            <div class="input-group pull-right">

                <a class="btn btn-default" href="#" role="button" >Select All</a>
                <div type="button"  class="btn btn-primary pull-right">
                  <img class="dashboard-icon" src="images/delivery.png" alt="Dispatch"  title="Dispatch"/>Dispatch</div>
            </div>
        </div>
            </div>

            <div class="panel-body">
                <table class="table">
                    <thead>
                    <th>
                        <a href="#" ng-click="changeListCreditBills('invoiceId')">Invoice No.</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'invoiceId'" ng-class="{reverse:listOrderReverse}"></span>
                    </th>
                    <th>
                        <a href="#" ng-click="changeListOrder('customerName')">Customer Details</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'customerName'" ng-class="{reverse:listOrderReverse}"></span></div>
                    </th>
                    <th>
                        <a href="#" ng-click="changeListOrder('orderTime')">Creation Time</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'orderTime'" ng-class="{reverse:listOrderReverse}"></span>
                    </th>
 					<th>
                        <a href="#" ng-click="changeListOrder('orderAmount')">Credit Amount</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'orderAmount'" ng-class="{reverse:listOrderReverse}"></span>
                    </th>
                    <th>
					    <a href="#" ng-click="changeListOrder('paymentMethod')">Payment Mode</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'paymentMethod'" ng-class="{reverse:listOrderReverse}"></span>
					</th>
                    <th>
                        <a href="#" ng-click="changeListOrder('paymentStatus')">Action</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'paymentStatus'" ng-class="{reverse:listOrderReverse}"></span>
					</th>
                    </thead>
                    <tr>
                    <td>SDD012</td>
                    <td>Sushil kumar(+918123843828)</td>
                    <td> 1 NOV 2016</td>
                    <td> 4350</td>
                    <td> 
                        <select ng-model="selectedPaymentMethod">
                                <option ng-repeat="method in paymentMethodsList">{{method.name}}</option>
                            </select>
                    </td>
                    <td>
                        <a ng-click="printOrder(order)"><img class="dashboard-icon" src="images/delivery.png" alt="Dispatch"  title="Dispatch"/></a>
                        <a ng-click="printOrder(order)"><img class="dashboard-icon" src="images/print-icon.png" alt="Print"  title="Print"/></a>
                     </td>
                    </tr>
                    <tr>
                    <td>SDD013</td>
                    <td>Mihir kumar(+918586051263)</td>
                    <td> 1 NOV 2016</td>
                    <td> 2850</td>
                    <td> Card Macine</td>
                    <td> 
                        <a ng-click="printOrder(order)"><img class="dashboard-icon" src="images/delivery.png" alt="Dispatch"  title="Dispatch"/></a>
                        <a ng-click="printOrder(order)"><img class="dashboard-icon" src="images/print-icon.png" alt="Print"  title="Print"/></a>
                    </td>
                    </tr>
                    <tr>
                    <td>SDD014</td>
                    <td>Abhishek kumar(+919999069200)</td>
                    <td> 1 NOV 2016</td>
                    <td> 5250</td>
                    <td> PG</td>
                    <td>
                        <a ng-click="printOrder(order)"><img class="dashboard-icon" src="images/delivery.png" alt="Dispatch"  title="Dispatch"/></a>
                        <a ng-click="printOrder(order)"><img class="dashboard-icon" src="images/print-icon.png" alt="Print"  title="Print"/></a> 
                    </td>
                    </tr>
                    <tr>
                    <td>SDD015</td>
                    <td>Rahul Sharma(+919540095277)</td>
                    <td> 1 NOV 2016</td>
                    <td> 3284</td>
                    <td> Card Machine</td>
                    <td>
                        <a ng-click="printOrder(order)"><img class="dashboard-icon" src="images/delivery.png" alt="Print"  title="Dispatch"/></a>
                        <a ng-click="printOrder(order)"><img class="dashboard-icon" src="images/print-icon.png" alt="Print"  title="Print"/></a>
                    </td>
                    </tr>
                </table>
                
            </div>
        </div>
    </div>
</div>
