<div class="container" >
<div class="row" ng-if="$root.cashCollectionScreen" ng-controller="cashCollectionController">
  <div class="col-md-12">
  <div id="collectCashOverlay"
         style="position: fixed;display:none; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>
    <div id="collectCashSpinner"
         style="position: fixed;display:none; width: 60px; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); background-image: url(http://i.imgur.com/uM2gq.gif); height: 60px; z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;"></div>
 
  <div class="panel panel-Primary">
    <div class="panel-heading">
      <h3 class="panel-title" style="text-align:center">Cash Collection</h3>
    </div>
  <div class="panel-body" style="text-align:center">
    <form class="form-horizontal" role="form">
  <div class="form-group">
    <label class="control-label col-sm-4" for="deliveryBoy">Select Delivery Person :</label>
    <div class="col-sm-8" style="padding-right:50px;">
      <select class="form-control" id="deliveryBoy" style="width:50%" ng-model="deliveryBoySelectFilter" ng-change="updateTotalAmountSum()">
               <option value="ALL"><b>All delivery people</b></option>
               <option ng-repeat="boy in deliveryBoyData | orderBy:'name'" value="{{boy.id}}">{{boy.name}}</option>
             </select>
    </div>
  </div>
  <div class="form-group" ng-show="isTotalCashCollected" >
    <label class="control-label col-sm-4"  for="totalAmt">Total Cash to be collected :</label>
    <label class="control-label col-sm-8" style="text-align:left"  id="totalAmt">{{totalAmountSum}}</label>
  </div>
  
  <div class="form-group" ng-show="isShowButton"> 
    <div class="col-sm-offset-2 col-sm-8">
               <button type="button" class="btn  btn-info" ng-click="showDetailOrder()" >Show Details</button>
              <button type="button" class="btn  btn-primary" ng-click="markOrdersDeliveredfilterByDeliveryBoy()" >Delivered</button>
             
    </div>
  </div>
</form>
    <div ng-show="isShowDetail" >
     <div class="col-sm-12" style="border-top: 2px solid #ddd;border-bottom: 2px solid #ddd;padding: 10px;">
       <input type="text" ng-model="deliveredOrderFilter" ng-change="updateTotalAmountSum()"  style="width:30%" class="form-control pull-left " placeholder="Search or filter"/>
       <a class="btn btn-default" href="#" role="button" ng-click="checkAll()" >{{selectAllValue}}</a>
       <input type="button" class="btn btn-default btn-primary" ng-disabled="!showDeliveredOptions" ng-click="markOrdersDelivered()" value="Delivered"/>
       <button type="button" class="btn  btn-danger" ng-click="hideDetailOrder()" >Hide Details</button>
     </div>
    <table class="table">
    <thead>
      <tr>
        <th>Customer Name(Mob #)</th>
        <th>Address</th>
        <th>Payment Method</th>
        <th>Order Amount</th>
        <th>Credit Amount</th>
        <th>Change</th>
        <th>Total Amount</th>
        <th>Delivery Person Details</th>
      </tr>
    </thead>
    <tbody>
      <tr ng-repeat="order in orderList | filter:deliveredOrderFilter | filter:filterByDeliveryBoy | orderBy:deliveryAgent">
        <td style="vertical-align:middle"><input type="checkbox" ng-model="order.orderCheckbox"
                               ng-change="showOptions()"
                               class="orderChkbox" aria-label="..."> {{order.customerName+' ('+order.customerMobNo+')'}}</td>
        <td style="vertical-align:middle">{{order.deliveryAddress+', '+order.deliveryArea}}</td>
        <td style="vertical-align:middle"><div ng-show="order.showChangePaymentMethodOption">
                            <label style="padding-top:7px">{{order.paymentMethod}}</label> 
                            <!--<a ng-click="order.showChangePaymentMethodOption = false" class="small-text">Change</a>-->
                            <button type="button" ng-click="showEditAuthentication(order,ordAmount,'paymentType')" ng-disabled=" order.status=='CANCELLED'" style="float:right" class=" btn btn-primary btn-sm">Change</button>
                        </div>
                        <div ng-show="!order.showChangePaymentMethodOption">
                            <select ng-model="order.updatedPaymentMethod" class="form-control" ng-change="updateDeliveryAmt(order)">
                                <option ng-repeat="method in paymentMethodList">{{method.name}}</option>
                            </select>
                            <button type="button" ng-click="order.showChangePaymentMethodOption = true" class=" btn btn-primary btn-sm">Original</button>
                            <button type="button" ng-click="savePaymentMethod(order)" class="btn  btn-sm btn-success">Save</button>
                        </div></td>
        <td style="vertical-align:middle"><div  ng-show="order.status!=='CANCELLED'">{{order.orderAmount}}</div>
                         <div  ng-show="order.status==='CANCELLED'"><del>{{order.orderAmount}}</del></div>
        <td style="vertical-align:middle">{{order.creditBalance}}</td>
        <td style="vertical-align:middle">{{order.changeAmount}}</td>
        <td style="vertical-align:middle" ng-show="!$root.isPartial"><input class="form-control" type="text" readonly disabled   style="width:75px" ng-model="order.totalAmount"> <button type="button" ng-show="order.paymentMethod=='COD'||order.paymentMethod=='Card Machine'"  class="btn  btn-sm btn-primary" ng-click="showPartialPayment(order)">Partial Payment</button></td>
        <td style="vertical-align:middle" ng-show="$root.isPartial"><input class="form-control" type="text"  readonly disabled  style="width:75px" ng-model="order.totalAmount"> <!--  <button type="button"  class="btn  btn-sm btn-success" >Save</button>--></td>
        <td style="vertical-align:middle">{{order.deliveryAgent}}</td>
      </tr>
     
    </tbody>
  </table>
    </div>
    
  </div>
</div> 
  </div>
 </div>
</div>