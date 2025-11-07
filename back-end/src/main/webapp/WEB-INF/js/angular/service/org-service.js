/**
 * Author Name :- Abhishek Kumar
 * EmailId :- anshilabhi1991@gmail.com
 * 
 */

angular.module('myApp').service('OrgService', ['$q', '$http', 'RequestFactory', 'Constants', 'UtilService', function($q, $http, RequestFactory, Constants, UtilService) {
	/**
	 *  
	 *  This method will shoot  http get request 
	 *  to get data of given orgId,
	 *  @argument orgId: integer 
	 *  @return promise
	 *  
	 */
	this.getOrgInfo = function(orgId) {  
		return RequestFactory.shootRequest(Constants.METHODS.GET, UtilService.addsToURL(Constants.ORG_URLS.GET_ORG_INFO, {'orgId':orgId}), null);
	}
	
}]);