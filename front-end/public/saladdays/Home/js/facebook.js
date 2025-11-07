
var customerData={
		"facebookEmail": null,
		"facebookId": null,
		"firstName": null,
		"lastName": null,
		"phone": null,
		"restaurantId":21
};

var fetchOTPParam ={
		"phoneNumber":null,
		"orgId":32,
		"device":""
};
// Load the SDK asynchronously
(function(thisdocument, scriptelement, id) {
 var js, fjs = thisdocument.getElementsByTagName(scriptelement)[0];
 if (thisdocument.getElementById(id)) return;
 
 js = thisdocument.createElement(scriptelement); js.id = id;
 js.src = "//connect.facebook.net/en_US/sdk.js"; //you can use 
 fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));
 
window.fbAsyncInit = function() {
	FB.init({
		 appId      : '226697114202871', //production  APP ID
		// appId :'443556615851308', //stage app ID
		 //appId		: '464497630419882',//local
		 //cookie     : true,  // enable cookies to allow the server to access 
		                     // the session
		 xfbml      : true,  // parse social plugins on this page
		 version    : 'v2.2' // use version 2.1
		});
} 
function loginWithFacebook() {
	$loading.show();
	if(typeof FB == 'undefined') {
		do {
			console.log("Initializing FB");
		}
		while (typeof FB == 'undefined');
	}
	FB.login(function(response) {
		if(response.status==='connected') {
			fetchDataFromFacebook();
		} else {
			alert("Could not login with Facebook, Plaese Try again!");
			$loading.hide();
		}
  }, {scope: 'public_profile,email'});
}

function fetchDataFromFacebook(){
  FB.api('/me', {fields: 'first_name,last_name,email'}, function(response) {
            customerData.facebookId=response.id;
            customerData.facebookEmail=response.email;
            customerData.firstName=response.first_name;
            customerData.lastName=response.last_name;
            isCustomerFacebookIdExist(customerData.facebookId);
 });
}



function isCustomerFacebookIdExist(fbId) {
	$.ajax({
		type: "POST",
		contentType: "application/json; charset=utf-8",
		url: apiPrefix+'/customer/isCustomerFacebookIdExist?facebookId='+fbId,
		success: function (data) {
			$loading.hide();
			if(!data) { 
				alert("Something went wrong please Try again!");
			} else {
				if(!data.phone){
					$("#lblMobileNoError").text("");
					$('#mobileNoModal').modal('show');
					$("#txtMobileNo").val("");
				} else{ 
			    	customerData.phone = data.phone;
			    	if (data.isAuthentic == 1) {
			    		var	custIdParam = "&phone=" + data.phone;
			    		fetchCustomerInfoAndPopulateFields(custIdParam);
			    	} else {
			    		fetchNewOTP();
			    	}
			    }
		    }
		},
		error: function () {
			
		}
	});
}

function submitMobileNumber(){
	$("#lblMobileNoError").text("");
	var cleanPhoneNumber = $("input#txtMobileNo").val().trim().toLowerCase();
	cleanPhoneNumber = countryCod+""+cleanPhoneNumber.replace(phoneClean, "");
	if (phoneMatch.test(cleanPhoneNumber)) {
		 $('#mobileNoModal').modal('hide');
		 customerData.phone=cleanPhoneNumber;
		 //fetchNewOTP();
		 $('#sign-in-div').hide();
			$('#auto-fill-details').show();
			$(".logout").show();
			updateFacebookInfo(customerInfo);
			signUp();
	} else {
		$("#lblMobileNoError").text("Please enter a valid Mobile Number");
		$("#lblMobileNoError").focus();
	}
}

function submitOTP(){
	var otp=$("#txtOTP").val();
	if (/^\d{6}$/.test(otp)) {
		$('#otpModal').modal('hide');
		verifyOTP(otp);
	} else {
		$("#lblOTPError").text("Please enter 6 digit valid OTP");
		$("#lblOTPError").focus();
	}	
}

function signUp(){
	$.ajax({
		type: "POST",
		headers: { 
            'Accept': 'application/json',
            'Content-Type': 'application/json' 
        },
		url: apiPrefix+'/customer/signUp?restaurantId=21',
		data: JSON.stringify(customerData),
		dataType: "text",
		success: function (data) {
			if(data=='"SUCCESS"') {
				var	custIdParam = "&phone=" + encodeURIComponent(customerData.phone);
				fetchCustomerInfoAndPopulateFields(custIdParam);
				$('#auto-fill-details').show();
				$(".logout").show();
			} else{
				alert("Error while sign in");
			}
		},
		error: function (data) {
			console.log("hi"+JSON.stringify(data));
		}
	});
}

function verifyOTP(otp){
	$.ajax({
		type: "POST",
		contentType: "charset=utf-8",
		url: apiPrefix+'/customer/verifyOauthOTP?phone='+encodeURIComponent(customerData.phone)+"&OTP="+otp+"&restaurantId=21",
		success: function (data) {
			if (data) {
				$('#sign-in-div').hide();
				$('#auto-fill-details').show();
				$(".logout").show();
				updateFacebookInfo(customerInfo);
				signUp();
			} else {
				$("#txtOTP").val("")
				$('#otpModal').modal('show');
				$("#lblOTPError").text("Please enter 6 digit valid OTP");
				$("#lblOTPError").focus();
			}
		},
		error: function () {
			console.log("Error while verifying OTP");
		}
	});
}

function updateFacebookInfo(customerInfo) {
	if (typeof customerData != 'undefined' && customerData != null && typeof customerInfo != 'undefined' && customerInfo != null) {
		if (customerInfo.facebookId == null && customerData.facebookId != null) {
			customerInfo.facebookId = customerData.facebookId;
			if (customerInfo.firstName == null && customerInfo.lastName != null && customerData.firstName != null && customerData.lastName != null) {
				customerInfo.firstName = customerData.firstName;
				customerInfo.lastName = customerData.lastName;
			} else {
				if (customerInfo.firstName == null && customerData.firstName != null) {
					customerInfo.firstName = customerData.firstName;	
				}
				if (customerInfo.lastName == null && customerData.lastName != null) {
					customerInfo.lastName = customerData.lastName;	
				}
			}
			if (customerInfo.email == null && customerData.facebookEmail != null) {
				customerInfo.email = customerData.facebookEmail;	
			}
		}
	}
}

function fetchNewOTP(){
	$('#otpModal').modal('hide');
	fetchOTPParam.phoneNumber=customerData.phone;
	var fetchOTP = apiPrefix+'/customer/fetchNewOTP';
	var jsonString = JSON.stringify(fetchOTPParam);
		$.ajax({
			type: "POST",
			contentType: "application/json; charset=utf-8",
			url:  fetchOTP,
			data: jsonString,
		success: function (data) {
			if(data){
				$("#lblOTPError").text("");
				$("#txtOTP").val("")
				$('#otpModal').modal('show');
			} else {
				$('#mobileNoModal').modal('show');
				$("#lblMobileNoError").text("Please enter a valid Mobile Number");
			}
		},
		error: function () {
			console.log("Error while sending OTP.");
		}
	});
}
