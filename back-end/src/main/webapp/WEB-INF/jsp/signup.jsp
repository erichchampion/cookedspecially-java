<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>User Signup</title>
	<base href="${pageContext.request.contextPath}/"/>
	<link rel="stylesheet" href="css/style.css" />
	
	<link rel="stylesheet" href="themes/base/jquery.ui.all.css" />	
	<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
	<script type="text/javascript" src="js/ui/jquery-ui.js"></script>
	<script src="js/jquery.validationEngine-en.js" type="text/javascript" charset="utf-8"></script>
	<script src="js/jquery.validationEngine.js" type="text/javascript" charset="utf-8"></script>
	<link rel="stylesheet" href="css/validationEngine.jquery.css" type="text/css"/>
	<style type="text/css">
		
		.data, .data td {
			border-collapse: collapse;
			width: 100%;
			border: 1px solid #aaa;
			margin: 2pxx;
			padding:2px;
			
		}
		.data th {
			font-weight: bold;
			background-color: #5C82FF;
			color: white;
		}
		#user{
		padding-left:30px
		}
		#user textarea{
		background-color: #FAFFBD;
		
		}
		#user input{
		background-color: #FAFFBD;
		height:25px;}
		
		#user td{
		padding-bottom:10px;
		}
	</style>
	
</head>
<body>
<hr/>

<h3 style="color: #FFAF42;padding-left:100px">User Signup</h3>

<form:form method="post" action="user/signup.html" commandName="user">

	<table id="tblReg">
	<tr>
		<td><form:label path="userName">Email *</form:label></td>
		<td><form:input path="userName" type="email" required="true" class="validate[required]" maxlength="80" /></td>
		<td><form:errors path="userName" style="color: red; padding-left: 5px;"/></td>
	</tr>
	
	<tr>
		<td><form:label path="firstName">First Name *</form:label></td>
		<td><form:input path="firstName" required="true" class="validate[required]" maxlength="30"/>
		</td> 
	</tr>
	<tr>
		<td><form:label path="lastName">Last Name *</form:label></td>
		<td><form:input path="lastName" required="true" class="validate[required]" maxlength="40"/></td> 
	</tr>
	
	<tr>
		<td><form:label path="contact.">Contact No *</form:label></td>
		<td><form:input path="contact" required="true" class="validate[required]" maxlength="10" />		
		</td> 
	</tr>
	
	<tr>
		<td><form:label path="country">Country *</form:label></td>
		<td><form:select path="country">
		<c:forEach items="${countryList}" var="country">
			<c:choose>
					<c:when test="${country eq user.country}">
					<option value="${country}" selected="selected">${country}</option>
				</c:when>
				<c:otherwise>
					<option value="${country}">${country}</option>
				</c:otherwise>
			</c:choose>
	</c:forEach>
	</form:select></td>
		<%-- <td><form:input path="country" required="true" style="text-transform:uppercase" class="validate[required]" maxlength="20" />		 --%>
	
	</tr>
	
	<tr>
		<td><form:label path="state">State *</form:label></td>
		<td><form:input path="state" required="true" class="validate[required]" maxlength="20"/>		
		</td> 
	</tr>
		<tr>
		<td><form:label path="city">City *</form:label></td>
		<td><form:input path="city" required="true" class="validate[required]" maxlength="20"/>		
		</td> 
	</tr>
	<tr>
		<td><form:label path="address1" style="vertical-align:top">Permanent Address *</form:label></td>
		<td><form:textarea row="4" path="address1" required="true" class="validate[required]" style="resize:none; width:100%" maxlength="250"  />		
		</td> 
	</tr>
	<tr>
		<td><form:label path="address2" style="vertical-align:top">Present Address *</form:label></td>
		<td><form:textarea path="address2" row="4" required="true" class="validate[required]" style="resize:none; width:100%" maxlength="250" />		
		</td> 
	</tr>
	
	<tr>
		<td>
		<form:label path="dateOfBirth">Date Of Birth *</form:label></td>
        <td><input id="dateOfBirth" name="dateOfBirth" style="width:173px"  required="true" value="" readonly="readonly">
		</td> 
	</tr>
	
	<tr>
		<td>Password *</td>
		<td><input id="password" type="password" name="password" class="validate[required]"/ maxlength="20"> </td> 
	</tr>
	<tr>
		<td><label> Password check * </label></td>
		<td><input type="password" name="check" class="validate[required,equals[password]]" maxlength="20"/></td> 
	</tr>
	<tr><td></td>
		<td style="padding-top: 10px;">
			<input style="width:100%;background-color: #8DC360;" type="submit" value="Sign up!"/>
		</td>
	</tr>
</table><label id="errorMsg" style="display:none"></label>	
<c:set var="error.userName.exists"><form:errors path="userName"/></c:set>
</form:form>

</body>
<script>
	$(function() {
		$("#user").validationEngine();
		//$("#user").validator();
		/*
		jQuery.tools.validator.fn("[data-equals]", "Value not equal with the $1 field", function(input) {
			    var name = input.attr("data-equals"), field = this.getInputs().rfilter("[name=" + name + "]");
			    alert(name);
			    alert(input.val());
			    alert(field.val());
			    return input.val() == field.val() ? true : [name];
		});
		*/
	});
$(function(){
	$("#dateOfBirth").datepicker({
		yearRange:"-100: -14",
		maxDate:"-14Y",
		minDate:"-100Y",
		changeYear:true,
		changeMonth:true
	})
})
	
</script>
</html>