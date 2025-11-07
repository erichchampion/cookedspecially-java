<div id="page-wrapper">
	<div class="col-md-12" ng-controller="LockRegisterCtrl"
		style="margin-top: 50px" ng-if="$root.lockSaleRegister">
		<div class="row">
			<div class="col-md-6 col-md-offset-3">
				<div class="row">
					<div class="col-md-3">
						<img class="img-responsive" src="images/user.png" alt="user">
					</div>
					<div class="col-md-9">
						<form name="formUnLockRegister" class="form-horizontal"
							role="form"
							ng-submit="unLockSaleRegister(formUnLockRegister.$valid)">
							<h4>{{$root.user.name}}</h4>
							<h5>Locked</h5>
							<input type="password" style="width: 200px" class="form-control"
								ng-model="unlockPassword" required>
							<button type="submit" class="btn btn-primary">Submit</button>
						</form>
					</div>
				</div>

			</div>

		</div>

	</div>