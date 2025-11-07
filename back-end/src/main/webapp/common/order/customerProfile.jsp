<div class="container" ng-controller="CustomerProfileCtrl" ng-if="$root.showCustomerProfile">
<div class="row" id="searchPanel">
<div class="col-md-12 col-sm-12 col-xs-12" style="margin-top:20px;margin-bottom:20px">
<form ng-submit="getCustomerDetails()">
<label>Search Customer</label>
<div class="input-group">
<span class="input-group-btn" style="width:115px">
 <select class="form-control " style="border-top-left-radius: 4px;border-bottom-left-radius: 4px;padding-right:3px;padding-left:3px"  ng-init="searchItem = searchByoptions[0]" ng-options="option.name for option in searchByoptions"  ng-change="searchBy()" ng-model="searchItem">
  </select>
</span>
<span class="input-group-btn" style="width:55px" id="countryCode">
  <select class="form-control" style="padding-right:3px;padding-left:3px" name="mySelect" id="mySelect"
      ng-options="option.name for option in data.availableOptions track by option.value"
      ng-model="data.selectedOption"></select>
</span>

      <input type="text" ng-model="searchText" class="form-control" placeholder="Search customer by phone ">
      <span class="input-group-btn">
        <button style="margin-top:1px" class="btn btn-default" type="button" ng-click="getCustomerDetails()">Search</button>
      </span>
      
</div>
</form>
</div>
</div>
<div class="row" id="customerPanel" style="display:none">
<div class="col-md-12  col-sm-12 col-xs-12" >
<div class="panel panel-primary"  style="margin-top:30px">
  <div class="panel-heading">
    <h3 class="panel-title" style="text-align:center">Customer</h3>
  </div>
  <div class="body">
  <div class="table-responsive">
 <table class="table">
 <tr>
  <th>Name</th>
  <th>Mobile Number</th>
  <th>Email</th>
  <th> Action</th>
 </tr>
 <tr>
  <td> {{ customerList.firstName }} {{ customerList.lastName}}</td>
  <td>{{customerList.phone}}</td>
  <td>{{customerList.email}}</td>
  <td><button type="button" class="btn btn-primary btn-sm" value="{{customer.customerId}}" ng-click="getOrderHistory(customer.customerId)">Select</button></td>
 </tr>
  </table>
  </div>
  </div>
</div>
</div>
</div> 
<div class="row" id="customerDetailpanel" style="display:none">
<div class="col-md-4 col-sm-6 col-xs-12" >
<div class="panel panel-primary">
  <div class="panel-heading">
    <h3 class="panel-title" style="text-align:center">CURRENT ORDER</h3>
  </div>
  <div class="panel-body" id="showCurrentOrder" style="display:none;height:210px;" >
   <div ng-repeat="currentOrder in currentOrderArray|orderBy:currentOrder.deliveryTime:true| limitTo:1">
    <p>Invoice :  <a  ng-href="{{invoiceLinkPrefix}}{{currentOrder.invoiceId}} target="_blank"">{{currentOrder.invoiceId}}</a></p>
    <p>Status :  {{currentOrder.status}}</p>
    <p ng-show="currentOrder.status=='OUTDELIVERY'">Delivery Person :  {{currentOrder.deliveryAgent}} <a ng-show="currentOrderArray.length>1" style="float:right;padding-top:10px" ng-click="showAllCurrentOrder()">More</a></p>
    <p ng-show="currentOrder.status!=='OUTDELIVERY'">Delivery Person :  Not Assigned <a ng-show="currentOrderArray.length>1" style="float:right;padding-top:10px" ng-click="showAllCurrentOrder()">More</a></p>
    </div>
  </div>
  <div class="panel-body" style="height:210px;" id="hideCurrentOrder">
  There is no current order.
  </div>
</div>
</div>
<div class="col-md-4 col-sm-6 col-xs-12">
<div class="panel panel-primary">
  <div class="panel-heading">
    <h3 class="panel-title" >ACCOUNT BALANCE</h3>
  </div>
  <div class="panel-body" ng-show="customerList.credit!=null">
   <!--  <p>{{customerList.credit.creditBalance>0?"Credit Due":"Credit Balance"}} :{{customerList.credit.creditBalance==null?0:customerList.credit.creditBalance}}</p> -->
   <p>Credit Balance :  {{customerList.credit.creditBalance==null?0:customerList.credit.creditBalance}}</p>
   <p>Credit Account : {{customerList.credit.creditType.name}} </p>
   <p>Statement Cycle : {{customerList.credit.creditType.billingCycle}}</p>
   <p>Status : {{customerList.credit.status}}</p>
   <p>Last Payment : {{custDetails.recentPaymentAmount}} on {{custDetails.recentPaymentDate|date:'dd MMM yyyy' }} </p>
    <p>Available Credit : {{custDetails.availableCredit }}</p>
   
    
  </div> 
  <div class="panel-body" style="height:210px;" ng-show="customerList.credit==null">
  {{customerList.firstName |uppercase}} {{customerList.lastName |uppercase}} is not activated for Customer Credit.
  <p> <a ng-show="customerList.credit===null && $root.userRole===('admin' ||'restaurantManager'||'fulfillmentCenterManager')" ng-click="showAssignCreditPanel('open')">Open Credit Account</a></p>
  </div>
  
</div>
</div>
<div class="col-md-4 col-sm-6 col-xs-12" >
<div class="panel panel-primary">
  <div class="panel-heading">
    <h3 class="panel-title" style="text-align:center">{{ (customerList.firstName+" "+customerList.lastName) |uppercase}}'S INFO</h3>
  </div>
  <div class="panel-body" style="height:210px" >
   <p>Customer since {{customerList.createdTime | date:'dd MMM yyyy'}}</p>
   <p>Points :  {{customerList.rewardPoints}}</p>
   <p> <a >Redeem Points</a></p>
   <p ng-if="customerList.credit!=null"><a ng-click="getCreditHistory()">Credit Statement</a></p>
    <p ng-if="customerList.credit!=null"><a ng-click="generateOneOffStatement()" >Settle credit account </a></p>
    <p ng-if="customerList.credit!=null"><a ng-click="closeCreditAccount()" >Close Credit Account </a></p>
  </div>
</div>
</div>
</div>
<div class="row">
<div class="col-md-12 col-sm-12 col-xs-12" id="orderHistoryCol" style="display:none">
<div class="panel panel-primary">
  <div class="panel-heading" style="text-align:center">Order History</div>
  <div class="table-responsive">
  <table class="table" >
  <tr style="border-bottom: 3px solid #ddd" >
  <th>Date</th>
  <th>Invoice</th>
  <th>Delivery Address</th>
  <th>Amount</th>
  <th>Payment Method</th>
  </tr>
  <tr ng-repeat="orders in orderDetails">
  <td>{{orders.deliveryTime | date:'MMM dd yyyy'}}</td>
  <td><a  ng-href="{{invoiceLinkPrefix}}{{orders.invoiceId}}" target="_blank" >{{orders.invoiceId}}</a></td>
  <td style="word-wrap: break-word">{{orders.deliveryAddress}},{{orders.deliveryArea}} </td>
  <td >{{orders.roundOffTotal}}</td>
  <td ng-repeat="order in orders.orders">{{order.paymentStatus}}</td>
  </tr>
  </table>
  </div>
</div>
<button type="button" class="btn btn-link" ng-click="showOrderHistory()" style="float:right" ng-show="orderDetails.length==10">More Orders</button>
</div>
</div>

<div class="row" id="customerInfoSetting" style="display:none">
<div class="col-md-12 col-sm-12 col-xs-12">
<h1 style="float:left">Settings</h1>
</div>
<div class="col-md-6 col-sm-8 col-xs-12" >
<p>{{ customerList.firstName }} {{ customerList.lastName}}</p>
<p> {{customerList.email}}</p>
<p> {{customerList.phone}}</p>
<button type="button" class="btn btn-primary"  ng-click="showModifyCustomer()">Edit</button>
</div>
<div class="col-md-6 col-sm-8 col-xs-12">
<button type="button" class="btn btn-link" style="display:block" ng-click="showDeliveryAddress()">Manage Delivery Addresses</button>
<button type="button" class="btn btn-link" style="display:block" ng-show="customerList.credit!==null" ng-click="showAssignCreditPanel('manage')">Manage Credit Account</button>

<button type="button" class="btn btn-link" style="display:block">Manage Notification Settings</button>
<button type="button" class="btn btn-link" style="display:block">Reset OTP ,logout from all devices</button>

</div>
</div>
<div class="row" id="deliveryAddressPanel" style="display:none;">
<div class="col-md-12 col-sm-12 col-xs-12" style="margin-top:20px">
<button type="button" class="btn btn-link" ng-click="hideDeliveryAddress()" >Back</button>
</div>
<div class="col-md-12 col-sm-12 col-xs-12" ng-repeat="addr in addressByRestaurant">
<div class="panel panel-primary">
  <div class="panel-heading">
    <h3 class="panel-title" style="text-align:center">{{addr.restaurantName}}</h3>
  </div>
  <div class="panel-body">

<div class="col-md-4 col-sm-6 col-xs-12"  ng-repeat="address in addr.customerAddress" ng-include="getTemplate(address)">
 <script type="text/ng-template" id="display">
<div class="panel panel-primary">
<div class="table-responsive">
  <table class="table">
  <tr>
  <td style="float:right;vertical-align:middle;border:none"><label class="control-label">Customer Address :</label></td>
  <td style="vertical-align:middle;border:none">{{address.customerAddress}}</td>
  </tr>
  <tr>
  <td style="float:right;vertical-align:middle;border:none"><label class="control-label">Delivery Area :</label></td>
 <td style="vertical-align:middle;border:none">{{address.deliveryArea}}</td>
  </tr>
  <tr>
  <td style="float:right;vertical-align:middle;border:none"><label class="control-label">City :</label></td>
  <td style="vertical-align:middle;border:none">{{address.city}}</td>
  </tr>
  <tr>
  <td style="float:right;vertical-align:middle;border:none"> <label class="control-label">State :</label></td>
  <td style="vertical-align:middle;border:none">{{address.state}}</td>
  </tr>
  <tr>
  <td style="float:right;vertical-align:middle;border:none"><label class="control-label">Action :</label></td>
  <td style="vertical-align:middle;border:none"><button type="button" class="btn btn-primary btn-sm" ng-click="updateCustomerAddress(address,addr.restaurantId)" >Edit</button> <button type="button" class="btn btn-danger btn-sm" ng-click="deleteCustomerAddress(address,addr.restaurantId)" >Delete</button>
  </td>
  </tr>
  </table>
</div>
</div>
  </script>
   <script type="text/ng-template" id="edit">
<div class="panel panel-primary">
<form name="editDelivryArea" style="margin:0px" >
<div class="table-responsive">
  <table class="table" style="margin-bottom:0px">
  <tr>
  <td style="float:right;vertical-align:middle;border:none"> <label class="control-label">Customer Address :</label></td>
  <td style="vertical-align:middle;border:none"><input  type="text" class="form-control input-sm" style="height:23px;width:170px;padding:0px 10px"  ng-model="address.customerAddress" required /></td>
  </tr>
  <tr>
  <td style="float:right;vertical-align:middle;border:none"> <label class="control-label">Delivery Area :</label></td>
 <td style="vertical-align:middle;border:none"> <select ng-model="address.deliveryArea" class="form-control input-sm" style="height:23px;width:170px;padding:0px 10px"  ng-options="area.name as area.name for area in getAllDeliveryArea"></select></td>
  </tr>
  <tr>
  <td style="float:right;vertical-align:middle;border:none"> <label class="control-label">City :</label></td>
  <td style="vertical-align:middle;border:none"> <input  type="text"  class="form-control input-sm" style="height:23px;width:170px;padding:0px 10px" ng-model="address.city" disabled readonly /></td>
  </tr>
  <tr>
  <td style="float:right;vertical-align:middle;border:none"> <label class="control-label">State :</label></td>
  <td style="vertical-align:middle;border:none"> <input  type="text" class="form-control input-sm" style="height:23px;width:170px;padding:0px 10px"  ng-model="address.state" disabled readonly /></td>
  </tr>
  <td style="float:right;vertical-align:middle;border:none"> <label class="control-label">Action :</label></td>
  <td style="vertical-align:middle;border:none"> 
  <button type="submit" ng-disabled="editDelivryArea.$invalid" class="btn btn-primary btn-sm" ng-click="saveCustomerAddress(address)" >Save</button> <button type="button" class="btn btn-danger btn-sm" ng-click="cancelCustomerAddress()" >Cancel</button>
  </td>
  </tr>
  </table>
</div>
</form>
</div>
</script>
</div>
<div class="col-md-4 col-sm-6 col-xs-12" >
<div class="panel panel-primary">
<form name="addDelivryArea" style="margin:0px" >
<div class="table-responsive">
  <table class="table" style="display:none ;margin-bottom:0px" id="addDeliveryAddrPanel{{addr.restaurantId}}">
  <tr>
  <td style="float:right;vertical-align:middle;border:none"> <label class="control-label">Customer Address :</label></td>
  <td class="tdManageAddress"><input type="text" class="form-control input-sm"  style="height:23px;width:170px;padding:0px 10px" ng-model="addr.custAddress" required /></td>
  </tr>
  <tr>
  <td style="float:right;vertical-align:middle;border:none"> <label class="control-label">Delivery Area :</label></td>
 <td style="vertical-align:middle;border:none"><select ng-model="addr.custDeliveryArea" class="form-control input-sm"  style="height:23px;width:170px;padding:0px 10px" ng-options=" area.name as area.name for area in getAllDeliveryArea" required>
 <option value="">Select Delivery Area</option>
 </select>
 </td>
  </tr>
  <tr>
  <td  style="float:right;vertical-align:middle;border:none"> <label class="control-label">City :</label></td>
  <td  style="vertical-align:middle;border:none"> <input type="text" class="form-control input-sm" style="height:23px;width:170px;padding:0px 10px" ng-model="addr.city" readonly disabled required /></td>
  </tr>
  <tr>
  <td  style="float:right;vertical-align:middle;border:none"> <label class="control-label">State :</label></td>
  <td  style="vertical-align:middle;border:none"><input  type="text"  class="form-control input-sm"  style="height:23px;width:170px;padding:0px 10px" ng-model="addr.state" readonly  disabled required/></td>
  </tr>
  
  <tr>
  <td  style="float:right;vertical-align:middle;border:none"><label class="control-label">Action :</label></td>
  <td  style="vertical-align:middle;border:none"><button type="submit" ng-disabled="addDelivryArea.$invalid" class="btn btn-primary btn-sm" ng-click="addDeliveryAddress(addr)" >Add</button> <button type="button" class="btn btn-danger btn-sm" ng-click="cancelDeliveryAddress(addr.restaurantId)" >Cancel</button>
  </td>
  </tr>
  </table>
  </div>
  </form>
  <div style="text-align:center;height:205px" id="addDeliveryAddrIcon{{addr.restaurantId}}"  ng-click="showAddDeliveryAddress(addr.restaurantId)">
  <span class="glyphicon glyphicon-plus" style="font-size:175px;color:#428BCA"></span>
  </div>
</div>
</div>
</div>
</div>
</div>

</div>

<div class="row" id="modifyCustomerPanel" style="display:none">
<div class="col-md-12 col-sm-12 col-xs-12" style="margin-top:20px">
<button type="button" class="btn btn-link" ng-click="hideModifyCustomer()" >Back</button>
</div>
<div class="col-md-12 col-sm-12 col-xs-12">
<div class="panel panel-primary">
  <div class="panel-heading">
    <h3 class="panel-title" style="text-align:center">Modify Customer Details</h3>
  </div>
  <div class="panel-body">
  <div class="row">
  <div class="col-md-8 col-md-offset-1">
  
    <form class="form-horizontal" name="editCustomerForm" ng-submit="updateCustomerDetails()">
  <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="firstName">First Name:</label>
    <div class="col-sm-9 col-xs-9">
      <input type="text" ng-model="customerList.firstName"  class="form-control" id="firstName" placeholder="Enter first name">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="lastName">Last Name:</label>
    <div class="col-sm-9 col-xs-9">
      <input type="text"  ng-model="customerList.lastName" class="form-control" id="lastName" placeholder="Enter last name">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="email">Email: </label>
    <div class="col-sm-9 col-xs-9">
      <input ng-model="customerList.email"  type="email" class="form-control" id="email" placeholder="Enter email ">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="email">Phone: </label>
    <div class="col-sm-9 col-xs-9">
      <input ng-model="customerList.phone"  type="phone" class="form-control" disabled readonly id="email" placeholder="Enter phone number ">
    </div>
  </div>
  <div class="form-group"> 
    <div class="col-sm-offset-3 col-sm-9 col-xs-9 col-xs-offset-3">
      <button type="submit" ng-disabled="editCustomerForm.$invalid" class="btn btn-success">Save</button>
      <button type="button" ng-click="hideModifyCustomer()" class="btn btn-danger">Cancel</button>
    </div>
  </div>
</form>
</div>
  </div>
  </div>
</div>
</div>
</div>

<div class="row" id="moreOrdersPanel" style="display:none;">
<div class="col-md-12 col-sm-12 col-xs-12" style="margin-top:20px">
<button type="button" class="btn btn-link" ng-click="hideOrderHistory()">Back</button>
</div>
<div class="col-md-12 col-sm-12 col-xs-12">
<div class="panel panel-primary">
  <div class="panel-heading" style="text-align:center">OrderHistory</div>
  <form class="form-inline" style="margin-top:20px;margin-left:20px;margin-right:20px">
        <div class="form-group">
            <label >Search :</label>
            <input type="text" ng-model="searchOrder" class="form-control" placeholder="Search">
        </div>
    </form>
    <div class="table-responsive">
  <table class="table">
                  <tr style="border-bottom: 3px solid #ddd">
                   <th>Date</th>
                   <th>Invoice</th>
                   <th>Delivery Address</th>
                   <th>Amount</th>
                   <th>Payment Method</th>
                 </tr>
                 <tr ng-repeat="orders in filteredOrder|filter:searchOrder">
                   <td>{{orders.deliveryTime | date:'MMM dd yyyy'}}</td>
                   <td> <a  ng-href="{{invoiceLinkPrefix}}{{orders.invoiceId}}" target="_blank" >{{orders.invoiceId}}</a></td>
                   <td style="word-wrap: break-word">{{orders.deliveryAddress}},{{orders.deliveryArea}} </td>
                   <td>{{orders.roundOffTotal}}</td>
                   <td ng-show="orders.checkType!=='TakeAway'" ng-repeat="order in orders.orders">{{order.paymentStatus}}</td>
                   <td ng-show="orders.checkType=='TakeAway'" ng-repeat="order in orders.orders|limitTo:1">{{order.paymentStatus}}</td>
                 </tr>
                </table>
                </div>
                <pagination style="float:right"  ng-model="currentPage" total-items="orderDetailsList.length" items-per-page="numPerPage"  max-size="maxSize"   boundary-links="true">
</pagination>
</div>
</div>
</div>

<div class="row" id="creditTransactionPanel" style="display:none">
    <div class="col-md-12 col-sm-12 col-xs-12" style="margin-top:20px">
        <button type="button" class="btn btn-link" ng-click="hideCreditTransaction()">Back</button>
    </div>
    <div class="col-md-12 col-sm-12 col-xs-12">
        <div class="panel panel-primary">
            <div class="panel-heading" style="text-align:center">Credit History</div>
            <div class="panel-body" >
            <div class="col-md-10 col-md-offset-1">
                <div class="row">
                <div class="col-md-12" style="margin-top:15px">
                <div class="row">
                <div class="col-md-6">
                <label for="sel1">Select Statement Date</label>
                <select class="form-control" id="sel1" style="width:60%"  ng-model="selectedTransactions" ng-change="getTransactionByStatementId(selectedTransactions)">
                   <option value="Unbilled">Unbilled Transactions</option>
                   <option ng-repeat="o in statementList" value="{{o.billId}}">{{o.date| date:'MMM dd yyyy'}}</option>
                </select>
                  </div>
                  <div class="col-md-6" style="text-align:right;">
                <button type="button" ng-disabled="selectedTransactions=='Unbilled'" ng-click="saveCustomerCreditBill(selectedTransactions)"  style="margin-top:20px" class="btn btn-link"> <img src="images/download-2-24.png" /><span style="padding-left:10px;vertical-align: -webkit-baseline-middle;">Downloads</span></button>
                </div>
                </div>
                </div>
                    <div class="col-md-6" style="margin-top:15px">
                     <label>Customer Info</label>
                        <div class="panel panel-default">
                            <!-- <div class="panel-heading">
                             <h3 class="panel-title" style="text-align:center">Customer Info</h3>
                            </div> -->
                           
                            <div class="panel-body" style="text-align:center">
                                <table class="table" style="margin-bottom:0px">
                                    <tr>
                                        <td style="border:none;text-align:right"><b>Name :</b></td>
                                        <td style="border:none;text-align:left">{{latestStatement.name}}</td>
                                    </tr>
                                    <tr>
                                        <td style="border:none;text-align:right"><b>Phone No. :</b></td>
                                        <td style="border:none;text-align:left">{{latestStatement.mobileNo}}</td>
                                    </tr>
                                    <tr>
                                        <td style="border:none;text-align:right"><b>Email :</b></td>
                                        <td style="border:none;text-align:left">{{latestStatement.email}}</td>
                                    </tr>
                                    <tr>
                                        <td style="border:none;text-align:right"><b>Address :</b></td>
                                        <td style="border:none;text-align:left">{{latestStatement.billingAddress}}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6" style="margin-top:15px">
                     <label>Card Info</label>
                        <div class="panel panel-default">
                            <!-- <div class="panel-heading">
                               <h3 class="panel-title" style="text-align:center">Card Info</h3>
                               </div> -->
                              
                            <div class="panel-body" style="text-align:center">
                                <table class="table" style="margin-bottom:0px">
                                    <tr>
                                        <td style="border:none;text-align:right"><b>Statement Date :</b></td>
                                        <td style="border:none;text-align:left">{{latestStatement.statementDate==null?"Unbilled":latestStatement.statementDate | date:'MMM dd yyyy'}}</td>
                                    </tr>
                                    <tr>
                                        <td style="border:none;text-align:right"><b>Credit Account :</b></td>
                                        <td style="border:none;text-align:left">{{latestStatement.creditName}}</td>
                                    </tr>
                                    <tr>
                                        <td style="border:none;text-align:right"><b>Maximum Limit :</b></td>
                                        <td style="border:none;text-align:left">{{latestStatement.maxLimit}}</td>
                                    </tr>
                                    <tr>
                                        <td style="border:none;text-align:right"><b>Balance Limit :</b></td>
                                        <td style="border:none;text-align:left">{{latestStatement.availableCredit}}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                       </div>
                        <div class="col-md-12" style="margin-top:15px">
                            <label>Account Summary</label>
                            <div class="panel panel-default">
                                <!-- <div class="panel-heading">
                                  <h3 class="panel-title" style="text-align:center">Account Summary</h3>
                                </div> -->
                                <div class="panel-body" style="text-align:center">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Previous Amount Due</th>
                                                <th>Payment</th>
                                                <th>Purchase</th>
                                                <th>Total Amount Due</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>{{latestStatement.openingBanalce}}</td>
                                                <td>{{latestStatement.paymentReceived}}</td>
                                                <td>{{latestStatement.totalPurchases}}</td>
                                                <td>{{latestStatement.outStandingBalance}}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-12" style="margin-top:15px">
                            <!-- <form class="form-inline" style="margin-top:20px;margin-left:20px;margin-right:20px">
                            <div class="form-group">
                            <label >Search :</label>
                            <input type="text" ng-model="searchCreditTransaction" class="form-control" placeholder="Search">
                            </div>
                            </form> -->
                            <label>Transaction(s)</label>
                            <div class="panel panel-default">
                                <!-- <div class="panel-heading">
                              <h3 class="panel-title" style="text-align:center">Transaction(s)</h3>
                            </div>
                            <div class="panel-body" style="text-align:center"> -->
                                <div class="table-responsive">
                                    <table class="table">
                                        <tr>
                                            <th>Date</th>
                                            <th>Invoice</th>
                                            <th>Description</th>
                                            <th>Address</th>
                                            <th>Amount</th>
                                        </tr>
                                        <tr ng-repeat="credits in filteredCreditTransactions|filter:searchCreditTransaction|filter:filteredCredit">
                                            <td>{{credits.transactionDate | date:'MMM dd yyyy'}}</td>
                                            <td>{{credits.invoiceId}}</td>
                                            <td> {{credits.description}}</td>
                                            <td>{{credits.address}}</td>
                                            <td ng-model="creditamount">{{credits.amount}} </td>
                                        </tr>
                                    </table>
                                </div>
                                <pagination style="float:right" ng-model="currentCreditPage" total-items="creditTransactions.length" items-per-page="numPerCreditPage" max-size="maxCreditPageSize" boundary-links="true">
                                </pagination>
                                <!-- </div> -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="row" style="display:none" id="allcurrentOrderPanel">
<div class="col-md-12 col-sm-12 col-xs-12" style="margin-top:20px">
<button type="button" class="btn btn-link" ng-click="hideAllCurrentOrder()" >Back</button>
</div>
<div class="col-md-12 col-sm-12 col-xs-12">
<div class="panel panel-primary">
  <div class="panel-heading" style="text-align:center">CURRENT ORDER</div>
    <form class="form-inline" style="margin-top:20px;margin-left:20px;margin-right:20px">
        <div class="form-group">
            <label >Search :</label>
            <input type="text" ng-model="searchCurrentOrder" class="form-control" placeholder="Search">
        </div>
    </form>
  
  <div class="panel-body" >
  <div class="table-responsive">
  <table class="table">
                  <tr >
                   <th>Date</th>
                   <th>Invoice</th>
                   <th>Status</th>
                   <th>Delivery Person</th>
                 </tr>
                 <tr ng-repeat="currentOrder in currentOrderArray|orderBy:currentOrder.deliveryTime:true|filter:searchCurrentOrder">
                   <td>{{currentOrder.deliveryTime | date:"MMM dd yyyy 'at' h:mm a"}}</td>
                   <td><a ng-href="{{invoiceLinkPrefix}}{{currentOrder.invoiceId}}" target="_blank">{{currentOrder.invoiceId}}</a></td>
                   <td>{{currentOrder.status}} </td>
                   <td>{{currentOrder.deliveryAgent==null?'Not Assigned':currentOrder.deliveryAgent}}
                    </td>
                 </tr>
                </table>
                </div>
  </div>
</div>
</div>
</div>

<div class="row" id="assignCreditPanel" style="display:none">
<div class="col-md-12 col-sm-12 col-xs-12 " style="margin-top:20px">
<button type="button" class="btn btn-link" ng-click="hideAssignCreditPanel()" >Back</button>
</div>
<div class="col-md-12 col-sm-12 col-xs-12">
<div class="panel panel-primary">
  <div class="panel-heading">
    <h3 ng-show="manageAccount=='open'" class="panel-title" style="text-align:center">Open Credit Account</h3>
    <h3 ng-show="manageAccount=='manage'"  class="panel-title" style="text-align:center">Manage Credit Account</h3>
  </div>
  <div class="panel-body">
  <div class="row">
  <div class="col-md-8 col-md-offset-1">
  <form name="assignCreditForm" class="form-horizontal">
   <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="custName">Name</label>
    <div class="col-sm-9 col-xs-9">
      <input type="text"  ng-model="customerName" required  disabled  class="form-control" id="custName" >
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="custPhoneNo">Phone No</label>
    <div class="col-sm-9 col-xs-9">
      <input type="text"  ng-model="customerList.phone" required  disabled  class="form-control" id="custPhoneNo" >
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="custEmail">Email</label>
    <div class="col-sm-9 col-xs-9">
      <input type="text"  ng-model="customerList.email" required  disabled  class="form-control" id="custEmail" >
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="restName">Restaurant Name</label>
    <div class="col-sm-9 col-xs-9">
      <select ng-model="rest" ng-change="changeBillingRestaurant(rest.restaurantId)"  style="text-transform:uppercase" class="form-control"  ng-options="rest as rest.restaurantName for rest in restaurantArray" required>
      <option value=" ">Select Restaurant Name</option>
      </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="billingAddr">Billing Address</label>
    <div class="col-sm-9 col-xs-9">
      <input type="text"  ng-model="billingAddr" required   class="form-control" id="billingAddr" >
    </div>
  </div>
 <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="billingArea">Billing Area</label>
    <div class="col-sm-9 col-xs-9">
      <select ng-model="billingArea "  style="text-transform:uppercase" class="form-control"  ng-options="billingArea as billingArea.name for billingArea in getAllDeliveryArea" required>
      <option>Select Billing Area</option>
      </select>
    </div>
  </div>
   <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="creditType">Credit Type</label>
    <div class="col-sm-9 col-xs-9">
      <select ng-model="crtype "  style="text-transform:uppercase" class="form-control"  ng-options="ctype as ctype.name for ctype in creditTypes" required>
      </select>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="billingCycle">Billing Cycle</label>
    <div class="col-sm-9 col-xs-9">
      <input type="text"  ng-model="crtype.billingCycle" required  disabled  class="form-control" id="billingCycle" placeholder="Enter billing cycle">
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-3 col-xs-3" for="maxLimit">Max Limit</label>
    <div class="col-sm-9 col-xs-9">
      <input type="text"  ng-model="crtype.maxLimit" required disabled   class="form-control" id="maxLimit" placeholder="Enter max limit">
    </div>
  </div>
  <div class="form-group" ng-show="manageAccount=='manage'">
    <label class="control-label col-sm-3 col-xs-3" for="creditType">Status</label>
    <div class="col-sm-9 col-xs-9">
      <select class="form-control" name="statusSelect" 
      ng-options="option.name for option in statusData.availableOptions track by option.value"
      ng-model="statusData.selectedOption"></select>
    </div>
  </div>
  <div class="form-group"> 
    <div class="col-sm-offset-3 col-sm-9 col-xs-offset-3 col-xs-9">
       <button type="submit" ng-show="manageAccount=='open'" ng-disabled="assignCreditForm.$invalid" class="btn btn-success" ng-click="openCreditAccount()">Save</button>
       <button type="submit" ng-show="manageAccount=='manage'" ng-disabled="assignCreditForm.$invalid" class="btn btn-success" ng-click="updateCreditAccount()">Update</button>
      <button type="submit" ng-disabled="assignCreditForm.$invalid" class="btn btn-danger">Cancel</button>
    </div>
  </div>
</form>
</div>
</div>
  </div>
</div>
</div>
</div>
</div>