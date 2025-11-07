
<div class="row" id="SummarySales" ng-controller="salesSummaryCtrl">
	<div class="col-md-12" ng-if="$root.salesSummary">
		<div class="row">
			<div class="col-md-4 col-md-offset-8" ng-show="IsVisible">
				<div class="panel panel-default" style="border:none;webkit-box-shadow: none;box-shadow: none; ">
					<div class="panel-body">
						
							<button ng-show="getPendingSalesTotal !== 0" type="submit" class="btn btn-primary btn-md"
							ng-click="showHandover()" style="float:right">Handover</button>
			
			<button ng-show="getPendingSalesTotal === 0" type="submit" class="btn btn-primary btn-md"
							ng-click="closeSaleRegister()" style="float:right">Close Sale Register</button>
					</div>
				</div>
			</div>
			<div class="col-md-4 col-md-offset-8" ng-show="IsHandoverVisible">
				<div class="panel panel-default" style="border:none;webkit-box-shadow: none;box-shadow: none; ">
					<div class="panel-body">
						
							<button type="submit" class="btn btn-primary btn-md"
							ng-click="showHandover()" style="float:right">Handover</button>
					</div>
				</div>
			</div>
		</div>

		<fieldset ng-disabled="IsDisableRegister">
			<div class="panel panel-primary customer-info"
				ng-controller="salesSummaryCtrl">
				<div class="form-group" style="margin-top:20px;text-align:center" ng-show="$root.user.name!==$root.saleRegOwner">
				 <label style="font-size:25px;margin-bottom:0px">Sales register is being operated by {{$root.saleRegisterOwner}}. </label></div>
				<div class="panel-heading">

					<h3 class="panel-title text-center">Transactions Summary</h3>
				</div>
				<div class="panel-info panel-options">

					<div class="panel-body">
						<div class="col-md-10 col-md-offset-1">
						<div class=row>
								<div class="col-md-12">
								 <label>Balance Summary</label>
							<div class="panel panel-primary">
								<!-- <div class="panel-heading">
									<h3 class="panel-title text-center">Balance Summary</h3>
								</div> -->
								<div class="panel-body">
									<div class="col-md-4 col-md-offset-1">
										<div class="form-group">
											<label> Fulfillment Center Name: </label> <label
												style="font-weight: normal"> {{fulfillmentName}} </label>
										</div>
										<div class="form-group">
											<label> Sale Register Name: </label> <label
												style="font-weight: normal"> {{saleRegisterName}} </label>
										</div>
										<div class="form-group">
											<label>Opening Time: </label> <label
												style="font-weight: normal">{{openingTime |
												date:'yyyy-MM-dd HH:mm:ss'}} </label>
										</div>
										<div class="form-group">
											<label> Sale Register Owner: </label> <label
												style="font-weight: normal; text-transform: capitalize">
												{{saleRegisterOwner}} </label>
										</div>
										<!-- <div class="form-group">
											<label> User Name: </label> <label style="font-weight: normal;text-transform:capitalize">
												{{$root.user.name}} </label>
										</div> -->
									</div>
									<div class="col-md-4 col-md-offset-2">
										<!-- <label> Current Balance: </label> <label
											style="font-weight: normal">{{currentCashBalance}}</label> -->
										<table class="table">
											<thead>
												<tr>
													<th>Payment Methods</th>
													<th>Amount</th>

												</tr>
											</thead>
											<tbody>
												<tr>
													<td>Opening Balance</td>
													<td>{{initialCashBalance}}</td>

												</tr>
												<tr>
													<td>Added Cash</td>
													<td>{{transactionAddCash}}</td>

												</tr>
												<tr>
													<td>Current COD</td>
													<td>{{completedCod}}</td>

												</tr>
												<tr>
													<td>Withdraw Cash (-)</td>
													<td>{{transactionWithdrawCash}}</td>

												</tr>

												<tr>
													<td>Transaction Cash (-)</td>
													<td>{{transactionCash}}</td>

												</tr>
												<tr>
													<td style="font-weight:bold">Current Balance</td>
													<td style="font-weight:bold">{{currentCashBalance}}</td>

												</tr>
											</tbody>
										</table>
									</div>

								</div>
							</div>
							</div>
							</div>
							<div class=row>
								<div class="col-md-6">
								 <label>Add Cash</label>
									<div class="panel panel-primary">
										<!-- <div class="panel-heading">
											<h3 class="panel-title text-center">Add Cash</h3>
										</div> -->
										<div class="panel-body">

											<form name="formAddCash" class="form-horizontal" role="form"
												ng-submit="addCash(formAddCash.$valid)">
												<div class="form-group">
													<label for="addCash" class=" control-label col-sm-4"
														style="font-weight: normal">Add Cash :</label>
													<div class="col-sm-7">
														<input type="text" min="0" maxlength="8"
															class="form-control" name="addAmount" id="addCash"
															ng-model="addAmount" required  valid-number  ng-show="$root.roundOff=== true">
															<input type="text" min="0" maxlength="8"
															class="form-control" name="addAmount" id="addCash"
															ng-model="addAmount" required ng-show="$root.roundOff!== true"  decimal-number>
													</div>
												</div>
												<div class="form-group">
													<label for="addRemark" class=" control-label col-sm-4"
														style="font-weight: normal">Remarks :</label>
													<div class="col-sm-7">
														<textarea style="resize: none" type="text"
															class="form-control" id="addRemark" ng-model="addRemarks"
															required></textarea>
													</div>
												</div>
												<div class="form-group">
													<div class="col-sm-offset-4 col-sm-7">
														<button type="submit" class="btn btn-primary "
															ng-disabled="formAddCash.$invalid">Submit</button>
				
				</div>
												</div>
												<!-- <div ng-show="showAddSuccess"
													class="alert alert-success col-md-10 col-md-offset-1">
													<a href="#" class="close" data-dismiss="alert"
														aria-label="close">&times;</a>  {{successAddText}} .
												</div> -->
											</form>

										</div>
									</div>
								</div>
								<div class="col-md-6">
								 <label>Withdraw Cash</label>
									<div class="panel panel-primary">
										<!-- <div class="panel-heading">
											<h3 class="panel-title text-center">Withdraw Cash</h3>
										</div> -->
										<div class="panel-body">

											<form name="formWithdrawCash" class="form-horizontal"
												ng-submit="withdrawCash(formWithdrawCash.$valid)">
												<div class="form-group">
													<label for="withdrawCash" class="control-label col-sm-4"
														style="font-weight: normal">Withdraw Cash :</label>
													<div class="col-sm-7">
														<input type="text" class="form-control "
															id="withdrawCash" ng-model="withdrawAmount" min="0" maxlength="8" valid-number ng-show="$root.roundOff=== true" required>
															<input type="text" class="form-control "
															id="withdrawCash" ng-model="withdrawAmount" min="0" maxlength="8" ng-show="$root.roundOff!== true" required decimal-number>
													</div>
												</div>
												<div class="form-group">
													<label for="withdrawRemark" class=" control-label col-sm-4"
														style="font-weight: normal">Remarks :</label>
													<div class="col-sm-7">
														<textarea style="resize: none" type="text"
															class="form-control" id="withdrawRemark"
															ng-model="withdrawRemarks" required></textarea>
													</div>
												</div>
												<div class="form-group">
													<div class="col-sm-offset-4 col-sm-7">
														<button type="submit" class="btn btn-primary"
															ng-disabled="formWithdrawCash.$invalid">Submit</button>
													</div>
												</div>
												<!-- <div ng-show="showWithdrawSuccess"
													class="alert alert-success col-md-10 col-md-offset-1">
													<a href="#" class="close" data-dismiss="alert"
														aria-label="close">&times;</a> {{successWithdrawText}} .
												</div> -->
											</form>

										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-6">
								 <label>Sales Summary</label>
									<div class="panel panel-primary ">
<!-- 
										<div class="panel-heading">
											<h3 class="panel-title text-center">Completed Sales</h3>
										</div>

 -->
										<div class="panel-body">
											<table class="table">
												<thead>
													<tr>
														<th>Payment Method</th>
														<th>Pending Amount</th>
                                                        <th>Completed Amount</th>
													</tr>
												</thead>
												<tbody>
													<tr ng-repeat="s in transctionSummary.saleSummary">
														<td>{{s.paymentTypeName}}</td>
														<td>{{s.pendingAmount}}</td>
                                                        <td>{{s.completedAmount}}</td>
													</tr>

													<tr>
														<td>Total</td>
														<td>{{getPendingSalesTotal}}</td>
                                                        <td>{{getCompletedSalesTotal}}</td> 
													</tr>
												</tbody>
											</table>
										</div>
									</div>
								</div>
								<div class="col-md-6" >
                            <label>Credit Summary</label>
                            <div class="panel panel-primary">
                                <!-- <div class="panel-heading">
                                  <h3 class="panel-title" style="text-align:center">Account Summary</h3>
                                </div> -->
                                <div class="panel-body" style="text-align:center">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                               <th>Payment Method</th>
												<th>Pending Amount</th>
                                                 <th>Completed Amount</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr ng-repeat="c in transctionSummary.creditSummary">
                                               <td>{{c.paymentTypeName}}</td>
											   <td>{{c.pendingAmount}}</td>
                                               <td>{{c.completedAmount}}</td>
                                            </tr>
                                            <tr>
														<td>Total</td>
														<td>{{pendingCreditTotal}}</td>
                                                        <td>{{completedCreditTotal}}</td> 
													</tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
								<!-- <div class="col-md-6">
								 <label>Pending Sales</label>
									<div class="panel panel-primary ">

										<div class="panel-heading">
											<h3 class="panel-title text-center">Pending Sales</h3>
										</div>


										<div class="panel-body">
											<table class="table">
												<thead>
													<tr>
														<th>Payment Methods</th>
														<th>Amount</th>

													</tr>
												</thead>
												<tbody>
												<tbody>
													<tr ng-repeat="p in saleSummaryData.pendingSale|filter:'!ADD_CASH' |filter:'!WITHDRAW_CASH' |filter:'!TRANSACTION_CASH'">
														<td>{{p.paymentTypeName}}</td>
														<td>{{p.amount}}</td>
													</tr>
													<tr>
														<td>Total</td>
														<td>{{getPendingSalesTotal}}</td>

													</tr>
												</tbody>
											</table>
										</div>
									</div>
								</div> -->
								<div class="col-md-12">
								<center><label style="font-size: 22px;">The total sales for today is {{getPendingSalesTotal+getCompletedSalesTotal}}.</label></center>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
	</div>
</div>