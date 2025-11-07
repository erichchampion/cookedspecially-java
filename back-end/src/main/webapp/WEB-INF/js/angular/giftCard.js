/**
 * Author  Abhishek Kumar
 */

angular.module('app', [ 'ngAnimate', 'ui.bootstrap' ]);
angular.module('app').factory('Excel',function($window){
    var uri='data:application/vnd.ms-excel;base64,',
        template='<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>GiftCards</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>',
        base64=function(s){return $window.btoa(unescape(encodeURIComponent(s)));},
        format=function(s,c){return s.replace(/{(\w+)}/g,function(m,p){return c[p];})};
    return {
        tableToExcel:function(tableId,worksheetName){
            var table=$(tableId),
                ctx={worksheet:worksheetName,table:table.html()},
                href=uri+base64(format(template,ctx));
            return href;
        }
    };
});

angular.module('app').directive('ngConfirmClick', [
                                 function() {
                                   return {
                                     priority: 1,
                                     link: function(scope, element, attr) {
                                       var msg = attr.ngConfirmClick || "Are you sure?";
                                       var clickAction = attr.ngClick;
                                       attr.ngClick = "";
                                       element.bind('click', function(event) {
                                         if (window.confirm(msg)) {
                                           scope.$eval(clickAction)
                                         }
                                       });
                                     }
                                   };
                                 }
                               ]);
angular.module('app').controller('GiftCardCtrl',['Excel','$timeout','$scope','$rootScope','$http','$modal' ,function(Excel,$timeout,$scope,$rootScope, $http,$modal) {
 
 $scope.showSpinner=function(flag){
        if(flag){
        	$(".js-spin-overlay").show();
        	$(".js-spin-spinner").show();
        } 
        else{
        	$(".js-spin-overlay").hide();
        	$(".js-spin-spinner").hide();
        }      
    }
 
 String.prototype.format = function () {
     var args = [].slice.call(arguments);
     return this.replace(/(\{\d+\})/g, function (a){
         return args[+(a.substr(1,a.length-2))||0];
     });
};
	
	function getEmployeeDtails(){
		 $scope.showSpinner(true)
		 $http.get("/CookedSpecially/user/getEmployeeDetails")
				.then(
						function successCallback(response) {
						$scope.empData=response.data;
						$scope.orgName=$scope.empData.orgName;
						$scope.name=$scope.empData.name;
						$rootScope.userRole=$scope.empData.role;
						$rootScope.orgId=$scope.empData.orgId;
						fetchOrganisationInfo($rootScope.orgId);
						$scope.showSpinner(false)
						}, 
						function errorCallback(response) {
							$scope.errorMsg = response.data;
						});
	 }
	
	 function getGiftCardList(fromDate, toDate, status, timeZone) {
		 $("#giftCardListTable").show();
		 $scope.showSpinner(true)
		 $scope.giftCardList=[];
		 var url = "/CookedSpecially/giftCard/listGiftCard?";
		 if (fromDate != null){
			 url = url+"fromDate="+fromDate
			}
		 if (toDate != null){
			 url = url+"&toDate="+toDate
			}
		 if (status != null && status != "ALL"){
			 url = url+"&status="+status
			}
		 if (timeZone == null && (toDate != null || fromDate != null)){
			 var timeZonel = new Date().getTimezoneOffset();
			 console.log(timeZonel);
			 url = url+"&inputDateTimeZone="+timeZonel
			}
		 else if (timeZone != null && (toDate != null || fromDate != null)){
			 url = url+"&inputDateTimeZone="+timeZone
			}
		 $http.get(url)
				.then(
						function successCallback(response) {
							console.log(response.data)
						if(response.data.length>0){
							for(i=0;i<response.data.length;i++){
    							$scope.giftCardList.push(response.data[i]);
	    					  }
							}
						else
							$scope.giftCardList=[];
							$scope.showSpinner(false)
						}, 
						function errorCallback(response) {
							if(response.data.message.indexOf("Please Relogin and try again") != -1)
								$scope.openLoginPage()
							else{
							$scope.showSpinner(false)
							alert(response.data.message);
								}
						});
	 }
	 
	function fetchOrganisationInfo(orgId) {
		console.log("Get Organisation Info");
		console.log(orgId)
		if(orgId!=null){
		  $http.get("/CookedSpecially/organization/getOrganizationInfo?orgId="+orgId).then(
				  function successCallback(response) {
					  $rootScope.organisation=response.data;
					  console.log(response.data)
				  },
				  function errorCallback(response) {
						if(response.data.message.indexOf("Please Relogin and try again") != -1)
							$scope.openLoginPage()
					});
		 }
		else
			$scope.openLoginPage()
	}
	 
	 var redeemGiftCardStep= '<p>* Your Gift Card Code can be entered when prompted during checkout</p>'+
	 '<p>* Gift Card fund will be applied automatically to eligible orders during the check out process.</p>'+
	 '<p>* You must pay for remaining balance on your order with another payment method as listed.</p>'+
	 '<p>* If your Order amount is lesser than the Gift Crad fund, then remaining balance will be added to your credit account and will be applied on subsequent Orders.</p>';

	 var printGiftCardTmplt ='<!DOCTYPE html><html><head>' +
     '<style>.row { display: flex;}'+
     '.col { flex: 1; padding: 1em; }'+
     'p { margin:0 }</style>'+
     '</head><body onload="window.print()"><div class="reward-body">'+
     '<div class="row"><div class="col">'+
     '<img src="{0}" alt="icon" style="width:128px;height:128px;">'+
     '</div><div class="col" align="center"><h2 style="font-family: cursive">Gift Card Amount</h2>'+
     '<h2>{1} {2} </h2>'+
     '</div></div>'+
     '<div class="row">'+
      '<h3>Redeem Code: {3}</h3>'+
     '</div><div style="padding: 1em;">'+
     '<p style="font-weight: bold;">To redeem Gift Card follow below steps:-</p>'+
     '<p>* Visit {4}</p>'+
     redeemGiftCardStep+
     '</div></div></html>';
	 
	 $scope.listAllGiftCard=function(){
	    	$("#giftCardListPannel").show();
	    	$("#createCardListPannel").hide();
	    	$("#giftCardActivatePannel").hide();
	    	$("#createAndActivatePannel").hide();
	    	var fromDate = $scope.fromDateSelected;
	    	var toDate = $scope.toDateSelected;
	    	var status = $scope.status;
	    	console.log(status)
	    	console.log(toDate)
	    	console.log(fromDate)
	    	getGiftCardList(fromDate, toDate, status, null);
	    }
	 
	 $scope.printGiftCard = function (giftCardId, amount) {
		 var frame1 = $('<iframe />');
         frame1[0].name = "frame1";
         frame1.css({ "position": "absolute", "top": "-1000000px" });
         $("body").append(frame1);
         var frameDoc = frame1[0].contentWindow ? frame1[0].contentWindow : frame1[0].contentDocument.document ? frame1[0].contentDocument.document : frame1[0].contentDocument;
         frameDoc.document.open();
         frameDoc.document.write(printGiftCardTmplt.format($rootScope.organisation.marketingImage,$rootScope.organisation.currency, amount, giftCardId, $rootScope.organisation.websiteURL));
         frameDoc.document.close();
         setTimeout(function () {
             window.frames["frame1"].focus();
             window.frames["frame1"].print();
             frame1.remove();
         }, 2);
	    }
	 
	 $scope.openCreateGiftCardPage=function(){
	    	$("#giftCardListPannel").hide();
	    	$("#giftCardActivatePannel").hide();
	    	$("#createCardListPannel").show();
	    	$("#giftCardCreatedListPannel").hide();
	    	$("#createAndActivatePannel").hide();
	    }
	 
	 $scope.openLoginPage=function(){
		 window.location= '/CookedSpecially/';
	    }
	 
	    $scope.createGiftCardForPrint=function() {
		    $scope.showSpinner(true);
			 $scope.giftCardLatestList=[];
	    	var req = {
					method : 'PUT',
					url : "/CookedSpecially/giftCard/createGiftCardForPrint",
					headers: {'Content-Type' : 'application/json'},
					data:{amount:$scope.amount,
						category:$scope.category,
						expireAfterDays:$scope.expireDayCount,
						noOfCard:$scope.noOfCard}
			       }
			 $http(req).then(function successCallback(response) {	
					 if(response.data.length>0){
							for(i=0;i<response.data.length;i++){
	    							$scope.giftCardLatestList.push(response.data[i]);
	    					  }
							$("#giftCardCreatedListPannel").show();
							$scope.noOfCard=null
						    $scope.showSpinner(false);
							}	
					},
					function errorCallback(response) {
					if(response){
						console.log(response)
						$scope.showSpinner(false);
						if(response.data.message.indexOf("Please Relogin and try again") != -1){
							$scope.openLoginPage()}
						alert(response.data.message);
					}					
			 });
	    }
	    
	    $scope.exportToExcel=function(tableId){
            var exportHref=Excel.tableToExcel(tableId,'GiftCards');
            $timeout(function(){location.href=exportHref;},100);
        }
	    
	    $scope.searchGiftCardForActivation=function() {
	    	$("#giftCardActivatePannel").show();
	    	$("#giftCardActivateAwaitingList").show();
	    	$("#giftCardActivationForm").hide();
	    	$("#giftCardListPannel").hide();
	    	$("#createCardListPannel").hide();
	    	$("#createAndActivatePannel").hide();
	    	getGiftCardList($scope.fromDateActivateList, $scope.toDateActivateList, "PRINT", null);
	    }
	    
	    $scope.getExpireOn = function(giftCardCreationDate, dayCount){
	    	var d = new Date(giftCardCreationDate);
	    	d.setDate(d.getDate() + dayCount);
	    	return d;
	    }
	    $scope.openGiftCardForm = function(giftCard){
	    	$scope.showSpinner(true);
	    	var d = new Date(giftCard.createdOn);
	    	d.setDate(d.getDate() + giftCard.expiryDayCount);
	    	$scope.giftCard=giftCard
	    	$("#giftCardActivationForm").show();
	    	$("#giftCardActivateAwaitingList").hide();
	    	$("#createAndActivatePannel").hide();
	    	$('#giftCardId').val(giftCard.formattedGiftCardId);
	    	$('#giftCardActivationAmount').val(giftCard.amount);
	    	$('#giftCardCategory').val(giftCard.category);
	    	$('#giftCardExpireOn').val(d.toISOString().slice(0,10));
	    	$('#giftCreationDate').val(new Date(giftCard.createdOn).toISOString().slice(0,10));
	    	$scope.showSpinner(false);
	    };
	    
	    
	    $scope.activateGiftCard = function(){
	    	$scope.showSpinner(true);
			var amount=$scope.giftCard.amount
			if($scope.giftCardActivationAmount)
				amount=$scope.giftCardActivationAmount
			var giftCardId=$scope.giftCard.giftCardId;	
			console.log("Actiavtion .....giftCardId="+$scope.giftCard.giftCardId+": amount="+amount)	
			var req = {
					method : 'PUT',
					url : "/CookedSpecially/giftCard/loadMoneyAndActivate",
					headers: {'Content-Type' : 'application/json'},
					data:{amount:amount,
						invoiceId:$scope.invoiceId,
						giftCardId:$scope.giftCard.giftCardId,
						message:$scope.activationMsg
						}
			       }
			 $http(req).then(function successCallback(response) {
					 console.log(response.data);
					 if(response.data){
						 alert(response.data.message)
						 $scope.showSpinner(false);
						 if(response.data.result=="SUCCESS"){
							 $scope.printGiftCard(giftCardId, amount);
							 $scope.searchGiftCardForActivation();
						 }
					 }
					},
					function errorCallback(response) {
						if(response){
							$scope.showSpinner(false);
							if(response.data.message.indexOf("Please Relogin and try again") != -1){
								$scope.openLoginPage()}
							alert(response.data.message);
						}
			 });
	    }
	   
	    $scope.deActivate = function(giftCardId, bool){
	    	if (confirm("Are you sure want to deactivate GiftCard?"))
	           {
	    		$scope.showSpinner(false);
				console.log("DeActiavtion .....")
				console.log(giftCardId)
				var req = {
						method : 'POST',
						url : "/CookedSpecially/giftCard/deactivateGiftCard",
						headers: {'Content-Type' : 'application/json'},
						data:{
							giftCardId:giftCardId
							}
				       }
				 $http(req).then(function successCallback(response) {
						 console.log(response.data);
						 if(response.data){
							 alert(response.data.message)
						 }
						 $scope.showSpinner(false);
							if(bool){
								$scope.listAllGiftCard();
							}else{
								$scope.searchGiftCardForActivation();	
							}
						 
						},
						function errorCallback(response) {
							if(response){
								$scope.showSpinner(false);
								if(response.data.message.indexOf("Please Relogin and try again") != -1){
									$scope.openLoginPage()}
								alert(response.data.message);		
							}
				 });
	           }
	    	else
	    	console.log("NO Inside confirmed..")	
	    }
	    
	    $scope.rollBack = function(giftCardId){
	    	$scope.showSpinner(true);
			console.log("Rolling Back .....")
			console.log(giftCardId)
			var req = {
					method : 'POST',
					url : "/CookedSpecially/giftCard/restoreGiftCard",
					headers: {'Content-Type' : 'application/json'},
					data:{
						giftCardId:giftCardId
						}
			       }
			 $http(req).then(function successCallback(response) {
				     console.log("Gift Card DeActivated Successfully")
					 console.log(response.data);
					 if(response.data){
						 alert(response.data.message)
					 }
					 $scope.showSpinner(false);
					 $scope.listAllGiftCard();
					},
					function errorCallback(response) {
						if(response){
							$scope.showSpinner(false);
							if(response.data.message.indexOf("Please Relogin and try again") != -1){
								$scope.openLoginPage()}
							alert(response.data.message);		
						}
			 });
	    }
	    
	    $scope.createGiftCardPannel = function(){
	    	$("#createCardListPannel").hide();
		    $("#giftCardActivatePannel").hide();
		    $("#giftCardListPannel").hide();
		    $("#createAndActivatePannel").show();
	    }
	    
	    $scope.createGiftCardAndActivate = function(){
	    	$scope.showSpinner(true);
			var req = {
					method : 'PUT',
					url : "/CookedSpecially/giftCard/createAndActivateGiftCard",
					headers: {'Content-Type' : 'application/json'},
					data:{category:$scope.category,
						  amount:$scope.amount,
						  msg:$scope.msg,
						  invoiceId:$scope.invoiceId,
						  expireAfterDayCount:$scope.expireDayCount
						}
			       }
			 $http(req).then(function successCallback(response) {
					 console.log(response.data);
					 if(response.data){
						 alert(response.data.message)
						 $scope.showSpinner(false);
						 if(response.data.result=="SUCCESS")
							 $scope.searchGiftCardForActivation();
					 }
					},
					function errorCallback(response) {
						if(response){
							$scope.showSpinner(false);
							if(response.data.message.indexOf("Please Relogin and try again") != -1){
								$scope.openLoginPage()}
							alert(response.data.message);
						}
			 });
	    }
	    
	   
	    
	    $scope.categoryOptions = ["Happy Birthday", "Happy Aniversary", "Happy Farewell", "Other"];
	    $scope.statusOptions = ["ALL","ACTIVE", "INACTIVE", "REDEEMED", "PAYMENT_AWAITING"];
	    $scope.giftCard = null
	    
	    $("#createCardListPannel").hide();
	    $("#giftCardActivatePannel").hide();
	    $("#giftCardListPannel").show();
	    $("#createAndActivatePannel").hide();
	    
	    getEmployeeDtails();
	    getGiftCardList(null, null, null, null);
}
]);