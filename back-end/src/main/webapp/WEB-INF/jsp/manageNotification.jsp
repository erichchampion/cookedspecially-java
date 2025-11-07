<!DOCTYPE html>
<%@page import="com.cookedspecially.enums.notification.Device"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Manage Notification Service</title>
<base href="${pageContext.request.contextPath}/" />
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script>
function submitDetailsForm() {
	 var patientData = {}, inputs = $('#sendNotificationForm').serializeArray();
	  $.each(inputs, function(i, o){
	    patientData[o.name] = o.value;
	  });
	  patientData.mobileNo=null;
	  $.ajax({
	        type: $('#sendNotificationForm').attr('method'),
	        contentType: "application/json",
	        async: false,
	        cache: false,
	        url: $('#sendNotificationForm').attr('action'),
	        data: JSON.stringify(patientData),
	        success: function (data) {
	        	if(data.resultCode=="Error"){
	        		alert("Failed to send Notification,Trye again! "+data.message);
	        	}else{
	        	alert("Notification Successfully sent");
	        	}
	        	setTimeout(function(){window.location="${pageContext.request.contextPath}/notifier";},600);
	        },
	        error: function(jqXHR, exception) {
	        	alert("Failed to send Notification,Trye again! "+jqXHR.status);
            	setTimeout(function(){window.location="${pageContext.request.contextPath}/notifier";},600)}
	    });
 }
function deleteNotification(nitificationId) {
	if (confirm('Do you really want to delete this User')) {
		$.ajax
         ({
           type: "DELETE",
            url: "${pageContext.request.contextPath}/notifier/deRegisterNotifierBYID/"+nitificationId,
            error: function(jqXHR, exception) {
                document.getElementById("errorblock").innerHTML="Failed to delete Notification,Trye again! "+jqXHR.status;
            	setTimeout(function(){window.location="${pageContext.request.contextPath}/notifier";},600);            },
            success: function (data)
                 {
        	document.getElementById("successblock").innerHTML=data;
        	setTimeout(function(){window.location="${pageContext.request.contextPath}/notifier";},600);
                 }
       });
	} 	
}

$('#sendNotificationForm').submit(function (ev) {
	alert("submit ajax")
   
});
	$(document).ready(
			function() {
				if (!$('#keyError').is(':empty')){
					   $("#panel1").slideDown("slow");
					}
				
						$("#addToken").click(function() {
							if ($('#panel1').is(":hidden")) {
								$("#panel2").hide();
								$("#panel3").hide();
								$("#panel4").hide();
								$("#panel1").slideDown("slow");
								$("#addToken").css("background-color","#D5B6EF");
								$("#viewP12").css("background-color","");
								$("#addP12").css("background-color","");
								$("#viewToken").css("background-color","");
							} else {
								$("#panel1").hide();
								$("#addToken").css("background-color","");
							}
						});
						$("#addP12").click(function() {
							if ($('#panel2').is(":hidden")) {
								$("#panel1").hide();
								$("#panel3").hide();
								$("#panel4").hide();
								$("#panel2").slideDown("slow");
								$("#addP12").css("background-color","#D5B6EF");
								$("#addToken").css("background-color","");
								$("#viewP12").css("background-color","");
								$("#viewToken").css("background-color","");
							} else {
								$("#panel2").hide();
								$("#addP12").css("background-color","");
							}
						});
						$("#viewToken").click(function() {
							if ($('#panel3').is(":hidden")) {
								$("#panel2").hide();
								$("#panel1").hide();
								$("#panel4").hide();
								$("#panel3").slideDown("slow");
								$("#viewToken").css("background-color","#D5B6EF");
								$("#addToken").css("background-color","");
								$("#viewP12").css("background-color","");
								$("#addP12").css("background-color","");
							} else {
								$("#panel3").hide();
								$("#viewToken").css("background-color","");
							}
						});
						$("#viewP12").click(function() {
							if ($('#panel4').is(":hidden")) {
								$("#panel2").hide();
								$("#panel3").hide();
								$("#panel1").hide();
								$("#panel4").slideDown("slow");
								$("#viewP12").css("background-color","#D5B6EF");
								$("#addToken").css("background-color","");
								$("#addP12").css("background-color","");
								$("#viewToken").css("background-color","");
							} else {
								$("#panel4").hide();
								$("#viewP12").css("background-color","");
							}
						});
						$("#sendNotification").click(function() {
							if ($('#panel5').is(":hidden")) {
								$("#panel2").hide();
								$("#panel3").hide();
								$("#panel1").hide();
								$("#panel5").slideDown("slow");
								$("#sendNotification").css("background-color","#D5B6EF");
								$("#addToken").css("background-color","");
								$("#addP12").css("background-color","");
								$("#viewToken").css("background-color","");
							} else {
								$("#panel5").hide();
								$("#sendNotification").css("background-color","");
							}
						});

					});
</script>

<style type="text/css">
body {
	font-family: sans-serif;
	background-color: #424242;
}

th.contentDesplay {
	background-color: #34495E
}

h3 {
	color: #FFBB43
}

h2 {
	color: #FFBB43
}

#container {
	display: block
}

#panel1, #panel2, #panel3, #panel4, #panel5 {
	padding: 5px;
	text-align: center;
	background-color: #e5eecc;
	border: solid 1px #c3c3c3;
	display: none;
}

#panel {
	padding: 50px;
	display: none;
}

table#one {
	border: 1px solid green;
	background-color: #ffe6ff;
}
table#two {
    border: 1px solid black;
}
#two td {
    border-bottom: 1px solid black;
}
td.one {
	padding: 8px;
	text-align: left;
	border: 1px solid #BD84C1;
}

th {
	padding: 8px;
	background-color: #4CAF50;
	color: white;
	text-align: center;
}

td:hover {
	background-color: #4D614C
}

.error {
	color: #ff0000;
}

.mytext {
	width: 300px;
}

.successblock {
	color: #000;
	background-color: #AEF4B7;
	border: 3px solid #0F7B0B;
	padding: 8px;
	margin: 16px;
}
#successMSG {
  display: none;
}
#errorMSG {
  display: none;
}
.errorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
</style>
</head>
<body>
	<div id="container">
		<div align="center">
			<h2 style="float: center;">Manage Notification Service</h2>
		</div>
		<a href="index.jsp" style="float: right; color: #F7C93D">Return to Home</a>
		<hr />

		<div>
			<table align="center" id="one">
				<tr>
					<th colspan="2">Manage Box</th>
				</tr>
				<tr>
					<td id="addToken" class="one">Add New Token</td>
					<td id="addP12" class="one">Add New P12 File</td>
				</tr>
				<tr>
					<td id="viewToken" class="one">View Token Added</td>
					<td id="viewP12" class="one">View P12 File Added</td>
				</tr>
				<tr><td colspan="2" id="sendNotification" class="one" style="text-align: center; font-weight: bold">Send Notification</td></tr>
			</table>

			<c:set var="devices" value="<%=Device.values()%>" />
			<c:if test="${not empty Success}">
				<div class="successblock">
					<strong>${Success}</strong>
				</div>
			</c:if>
			<div class="successblock" id=successMSG></div>
			<div class="errorblock" id=errorMSG></div>
			<c:if test="${not empty Error}">
				<div class="errorblock">
					<strong>${Error}</strong>
				</div>
			</c:if>
		</div>
		<br />
		<div id="formPannel">
		<div id="panel1">
			<h3 style="color: blue; text-decoration: underline" >Add New Token</h3>
			<form:form method="post"
				action="${pageContext.request.contextPath}/notifier/registerNotifier"
				commandName="notifier">
				<table align="center">
					<form:hidden path="restaurantId"
						value='<%=request.getSession().getAttribute("organisationId")%>' />
					<form:hidden path="notifierId" />
					<tr>
						<td><form:label path="key">Token</form:label></td>
						<td>:</td>
						<td><form:input path="key" class="mytext" maxlength="100" /></td>
						<td id="keyError"><form:errors path="key" cssClass="error" /></td>
					</tr>
					<tr>
						<td><form:label path="device">Device</form:label></td>
						<td>:</td>
						<td><select name="device">
								<c:forEach items="${devices}" var="device">
									<option value="${device}">${device.toString()}</option>
								</c:forEach>
						</select></td>
						<td><form:errors path="device" cssClass="error" /></td>
					</tr>
					<tr>
						<td/>
						<td/>
						<td><input type="submit" value="Add" /></td>
						<td/>
					</tr>
				</table>
			</form:form>
		</div>
		<div id="panel2">
		<h3 style="color: blue; text-decoration: underline" >P12 File Upload</h3>
		<form method="POST" enctype="multipart/form-data" action="${pageContext.request.contextPath}/notifier/uploadP12File">
			<table align="center" id="two">
				<tr><td><input type="file" name="file" /></td></tr>
				<tr><td><input type="submit" value="Upload" /></td></tr>
			</table>
		</form>
		</div>
		<div id="panel3">
		<h3 style="color: blue; text-decoration: underline" >Notification Device Token(s)</h3>
			<c:if test="${!empty notifierList}">
				<div>
					<table align="center" id="two">
						<tr>
							<th class="contentDesplay">Device Type</th>
							<th class="contentDesplay">Token</th>
							<th class="contentDesplay">Operation</th>
						</tr>
						<c:forEach items="${notifierList}" var="notification">
							<input type="hidden" name="id" value="${notification.notifierId}"
								id="id" />
							<tr>
								<td>${notification.device}</td>
								<td>${notification.key}</td>
								<td><button type="button" onclick="deleteNotification(${notification.notifierId})">Delete</button></td>
							</tr>
						</c:forEach>
					</table>

				</div>
			</c:if>
			<c:if test="${empty notifierList}">
				<div align="center">No Any Token is Added yet!</div>
			</c:if>
		</div>
		<div id="panel4">
		<h3 style="color: blue; text-decoration: underline" >P12 File(s)</h3>
		<ul>
		    <c:if test="${!empty p12files}">
			 <c:forEach items="${p12files}" var="file">
			    <li>${file}</li>
			 </c:forEach>
			</c:if>
			<c:if test="${empty p12files}">
			 No Any P12 file is added.
			</c:if>
		</ul>
		</div>
		<div id="panel5">
		<h3 style="color: blue; text-decoration: underline" >Send Notification</h3>
		<form method="POST" id="sendNotificationForm" method="post"
				action="${pageContext.request.contextPath}/notifier/sendNotification" modelAttribute="notifier">
				<input type="hidden" name="restaurantId" value='<%=request.getSession().getAttribute("organisationId")%>' />
				<input type="hidden" name="mobileNo" value="[]" />
				<table align="center">
					<tr>
						<td><label>Heading</label></td>
						<td>:</td>
						<td><input type="text" name="heading" class="mytext" maxlength="100" /></td>
					</tr>
					<tr>
						<td><label>Message</label></td>
						<td>:</td>
						<td><input type="text" name="message" class="mytext" maxlength="200" /></td>
					</tr>
					<tr> 
					<td></td><td></td>
					<td><input type='button' value='Send Notification' onClick='submitDetailsForm()'/></td>
		</table>
		</form>
		</div>
	</div>
	</div>
</body>
</html>