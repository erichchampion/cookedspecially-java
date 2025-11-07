
<div class="row" ng-controller="AllCreditBills">
    <div class="col-sm-12"  ng-if="$root.allCreditBills">
        <div class="panel panel-primary customer-info">

            <div class="panel-heading">
                <h3 class="panel-title text-center">All Credit Bills</h3>
            </div>
            <div class="panel-info panel-options">
                <div class="col-sm-4">
                    <div class="input-group">
                        <input type="text" class="form-control" name="searchCreditBillsFilter" style="width: 255px;"
                               ng-model="searchCreditBillsFilter" placeholder="Name/Phone/delivery area/address">
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
                        <a href="#" ng-click="changeListOrder('deliveryAgent')">Delivered by</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'deliveryAgent'" ng-class="{reverse:listOrderReverse}"></span>
                    </th>
                    <th>
                        <a href="#" ng-click="changeListOrder('status')">Credit Status</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'status'" ng-class="{reverse:listOrderReverse}"></span>
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
                        <a href="#" ng-click="changeListOrder('paymentStatus')">Payment Status</a>
                        <span class="sortorder" ng-show="listOrderByPredicate === 'paymentStatus'" ng-class="{reverse:listOrderReverse}"></span>
					</th>
                    </thead>
                    <tr>
                    <td>SDD012</td>
                    <td>Sushil kumar(+918123843828)</td>
                    <td>Not Assigned</td>
                    <td> Ready</td>
                    <td> 1 NOV 2016</td>
                    <td> 4350</td>
                    <td> COD</td>
                    <td> Unpaid</td>
                    </tr>
                    <tr>
                    <td>SDD013</td>
                    <td>Mihir kumar(+918586051263)</td>
                    <td>Vinay(+919205536064)</td>
                    <td>Dispatch</td>
                    <td> 1 NOV 2016</td>
                    <td> 2850</td>
                    <td> Card Macine</td>
                    <td> Unpaid</td>
                    </tr>
                    <tr>
                    <td>SDD014</td>
                    <td>Abhishek kumar(+919999069200)</td>
                    <td>Vinay(+919205536064)</td>
                    <td> Delivered</td>
                    <td> 1 NOV 2016</td>
                    <td> 5250</td>
                    <td> PG</td>
                    <td> Paid</td>
                    </tr>
                    <tr>
                    <td>SDD015</td>
                    <td>Rahul Sharma(+919540095277)</td>
                    <td>Not Assigned</td>
                    <td> Ready</td>
                    <td> 1 NOV 2016</td>
                    <td> 3284</td>
                    <td> Card Machine</td>
                    <td> Unpaid</td>
                    </tr>
                </table>
                
            </div>
        </div>
    </div>
</div>
