
<div class="row" >
    <div class="col-sm-10 col-md-offset-1"  ng-if="$root.agedCreditBills">
        <div class="panel panel-primary customer-info">

            <div class="panel-heading">
                <h3 class="panel-title text-center">Aged Credit Bills</h3>
            </div>
            <div class="panel-info panel-options">
                <div class="col-sm-4">
                    <div class="input-group">
                        <input type="text" class="form-control" name="searchAgedCreditBillsFilter" style="width: 255px;"
                               ng-model="searchAgedCreditBillsFilter" placeholder="Name/Phone/email/address">
                    </div>
                </div>
            </div>

            <div class="panel-body">
                <table class="table">
                    <thead>
                    <th>
                        <a href="#" ng-click="changeListCreditBills('customerInfo')">Customer Info</a>
                        <span class="sortorder" ng-show="listCreditBillByPredicate === 'customerInfo'" ng-class="{reverse:listCreditBillReverse}"></span>
                    </th>
                    <th>
                        <a href="#" ng-click="changeListCreditBills('customerAddress')">Customer Address</a>
                        <span class="sortorder" ng-show="listCreditBillByPredicate === 'customerAddress'" ng-class="{reverse:listCreditBillReverse}"></span>
                    </th>
					<th>
                        <a href="#" ng-click="changeListCreditBills('creditBalance')">Credit Balance</a>
                        <span class="sortorder" ng-show="listCreditBillByPredicate === 'creditBalance'" ng-class="{reverse:listCreditBillReverse}"></span>
                    </th>
                    <th>
                        <a href="#" ng-click="changeListCreditBills('lastTransaction')">Last transaction</a>
                        <span class="sortorder" ng-show="listCreditBillByPredicate === 'lastTransaction'" ng-class="{reverse:listCreditBillReverse}"></span>
                    </th>
                    <th>
                        <a href="#" ng-click="changeListCreditBills('action')">Action</a>
                        <span class="sortorder" ng-show="listCreditBillByPredicate === 'action'" ng-class="{reverse:listCreditBillReverse}"></span>
                    </th>
 					
                    </thead>
                    <tr ng-repeat="agedCredit in agedCreditData |orderBy:listCreditBillByPredicate:listCreditBillReverse|filter:searchAgedCreditBillsFilter | filter: equalThan('creditBalance', 0)">
                    <td>{{agedCredit.name+" ("+agedCredit.mobileNo+") "}}<br>{{agedCredit.email}}</td>
                    <td>{{agedCredit.billingAddress}}</td>
                    <td>{{agedCredit.creditBalance}}</td>
                    <td>{{agedCredit.lastTransactionDate|date:'dd MMM yyyy'}}</td>
                    <td><button type="button" ng-click="markStructureCredit(agedCredit.customerId)"  class="btn btn-success btn-small">Generate Statement</button> </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
