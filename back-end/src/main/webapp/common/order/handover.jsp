<div id="page-wrapper">
	<div class="col-md-12" ng-controller="HandoverCtrl"
		style="margin-top: 20px" ng-if="$root.handover">
		<div class="row">
			<div class="col-md-6 col-md-offset-3">
			<div><h3 style="text-align:center;padding-bottom:15px">Handover Sale Register </h3></div>
				<div class="row">
					<div class="col-md-3">
						<img class="img-responsive" src="images/handover.png" alt="handover">
					</div>
					<div class="col-md-9">
					   <form name="formHandover" class="form-horizontal" role="form" ng-submit="$root.handoverSaleRegister(formHandover.$valid)">
												<div class="form-group" style="margin-left:45px">
													<label class=" control-label">Please authenticate to accept handover.</label>
												</div>
												<div class="form-group">
													<label for="handoverUsername" class=" control-label col-sm-4"
														style="font-weight: normal">Username :</label>
													<div class="col-sm-7">
														<input type="text" 
															class="form-control" name="handoverUsername" id="handoverUsername"
															ng-model="handoverUsername" required  >
													</div>
												</div>
												<div class="form-group">
													<label for="handoverPassword" class=" control-label col-sm-4"
														style="font-weight: normal">Password :</label>
													<div class="col-sm-7">
														<input type="password" 
															class="form-control" name="handoverPassword" id="handeoverPassword"
															ng-model="handoverPassword" required  >
													</div>
												</div>
												<div class="form-group">
													<label for="handoverRemark" class=" control-label col-sm-4"
														style="font-weight: normal">Remarks :</label>
													<div class="col-sm-7">
														<textarea style="resize: none" type="text"
															class="form-control" id="handoverRemark" ng-model="handoverRemark"
															required></textarea>
													</div>
												</div>
												<div class="form-group">
													<div class="col-sm-offset-4 col-sm-7">
														<button type="submit" class="btn btn-primary "
															ng-disabled="formHandover.$invalid">Submit</button>
													</div>
												</div>
											</form>
					</div>
				</div>

			</div>

		</div>

	</div>