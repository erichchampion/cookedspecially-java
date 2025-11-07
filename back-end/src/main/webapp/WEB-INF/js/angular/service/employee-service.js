/**
 * Author Name :- Abhishek Kumar
 * EmailId :- anshilabhi1991@gmail.com
 * 
 */

angular.module('myApp').service('EmployeeService',['RequestFactory', 'Constants', function(RequestFactory, Constants) {
	/**
	 *  
	 *  This method will shoot  http get request 
	 *  to get data of logged-in employee,
	 *  @argument 
	 *  @return promise
	 *  
	 */
	this.getEmployeeDtails = function() {  
		return RequestFactory.shootRequest(Constants.METHODS.GET, Constants.EMP_URLS.GET_EMP_PROFILE, null);
	}
	
	this.getOpenSaleRegister = function(){
		return RequestFactory.shootRequest(Constants.METHODS.GET, Constants.EMP_URLS.GET_OPEN_SALE_REGISTER, null);
	}
	
}]);