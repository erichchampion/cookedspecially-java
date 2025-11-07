<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
	<title> Add On Dish Manager</title>
	
	<style type="text/css">
		body {
			font-family: sans-serif;
		}
		.data,.data td {
			border-collapse: collapse;
			width: 100%;
			border: 1px solid #aaa;
			margin: 2px;
			padding: 2px;
			text-align:center;
			vertical-align:middle;
		}
		.data th {
			font-weight: bold;
			background-color: #5C82FF;
			color: white;
		}
		#addOn td{
		border:none;
		}
	</style>
	<base href="${pageContext.request.contextPath}/"/>
	<link rel="stylesheet" href="css/style.css" />
	<script type="text/javascript" src="js/nicEdit.js"></script>
	<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
	<script src="js/jquery.validationEngine-en.js" type="text/javascript" charset="utf-8"></script>
	<script src="js/jquery.validationEngine.js" type="text/javascript" charset="utf-8"></script>
	<link rel="stylesheet" href="css/validationEngine.jquery.css" type="text/css"/>
	<script type="text/javascript">
		bkLib.onDomLoaded(function() { 
			//nicEditors.allTextAreas()
			//var nicEditorInstance = new nicEditor({fullPanel : false, iconsPath: 'images/nicEditorIcons.gif', buttonList : ['bold','italic','underline','left','center','right', 'justify', 'ol', 'ul', 'subscript', 'superscript', 'strikethrough', 'removeformat', 'indent', 'outdent', 'hr', 'forecolor', 'bgcolor', 'fontSize', 'fontFamily', 'fontFormat']});
			var nicEditorInstance = new nicEditor({fullPanel : false, iconsPath: 'images/nicEditorIcons.gif', buttonList : ['bold','italic','underline','left','center','right', 'justify']});
			
			nicEditorInstance.panelInstance('shortDescription');
			nicEditorInstance.panelInstance('description');
			 $(".nicEdit-main").attr('id', 'smallDescription');
	         $(".nicEdit-main").attr('tabindex', '1');
	         $(".nicEdit-main").attr('oninput', 'smallDescriptionLimitText(this,900);'); 
		});
		function deleteDish(addOnId) {
			if (confirm('Do you really want to delete this dish')) {
				window.location.href = 'addOnDish/delete/' + addOnId;
			} 
		}

		function selectNutrientes() {
			var divId= $("#dishType option:selected").text();
			divId = divId.replace(/ /g,'');
			if(divId!=null){
			$("#colAddNutrientCenterLabel").html("Fill Nutrientes :");
			$("#colAddNutrientCenterButton").html($("#nutrientes"+divId).html());
		}
			showAddOnDishSizeDiv();
		}

		function showAddOnDishSizeDiv(){
			var divId= $("#dishType option:selected").text();
			divId = divId.replace(/ /g,'');
			if($('#manageDishSize').is(':checked')){
			if(divId!=null){
				//$("#colAddDishSizeLabel").html("Dish Size :");
				$("#colAddDishSizeButton").html($("#dishSize"+divId).html());
			}
			}
		else {
			 $('#dish .displayPrice').show();
			 $('#dish .price').show();
			}
		}
	</script>
	
</head>
<body>
<a href="manageRestaurant.jsp" style="float: right;">Return to Home</a>
<hr/>
<div style="color:red;">${errorMsg}</div>
<h3>Add</h3>

<form:form method="post" action="addOnDish/add.html" commandName="dish" enctype="multipart/form-data">
<c:set var="dishTypeFlag" value=""></c:set>
<c:set var="nutrientInfoFlag" value="hello"></c:set>
<c:set var="dishSizeFlag" value="hello"></c:set>
<c:forEach items="${dishTypes}" var="dishType">
<c:if test="${dish.dishType == dishType.name}">
<c:forEach items="${dish.nutritionalInfo}" var="nutrientInfo">
<c:forEach items="${nutrients}" var="nutrients">
<c:if test="${nutrients.dishType==dishType.name}">
<c:if test="${nutrientInfo.nutrientId == nutrients.id}">
<c:set var="repeatFlag" value=""></c:set>
</c:if> 
</c:if>
</c:forEach>
</c:forEach>
</c:if>
</c:forEach>

<c:forEach items="${dishTypes}" var="dishType">
<c:if test="${nutrientInfoFlag=='hello' && dish.nutritionalInfo.size()>0 && dish.nutritionalInfo !=null && dish.nutritionalInfo!='' && dish.dishType == dishType.name}">
<div id="nutrientes${dishType.name.replaceAll('\\s','')}" hidden="true" title="Nutrientes">
<c:forEach items="${dish.nutritionalInfo}" var="nutrientInfo">
<c:forEach items="${nutrients}" var="nutrients">
	 	<c:if test="${nutrients.dishType==dishType.name}">
		<c:if test="${nutrientInfo.nutrientId == nutrients.id}">
		<tr>	
			<td width="50%"><input type="text" readonly name="nutrientName" value="${nutrients.name}" id="name" ></td>
			<td width="50%"><input type="number" step="any" name="instructions" id="instructions" value="${nutrientInfo.value}"></td>
		</tr>
		<br/>
		</c:if>
		</c:if>
</c:forEach>
</c:forEach>

<c:forEach items="${nutrients}" var="nutrients">
<c:set var="nutrientInfoFlag" value=""></c:set>
<c:forEach items="${dish.nutritionalInfo}" var="nutrientInfo">
	 	<c:if test="${nutrients.dishType==dish.dishType}">
		<c:if test="${nutrientInfo.nutrientId == nutrients.id}">
		<c:set var="nutrientInfoFlag" value="false"></c:set>
		<c:set var="repeatFlag" value="${nutrients.name}"></c:set>
		</c:if>
		<c:set var="nutrientInfoFlag" value="true"></c:set>
		</c:if>
	</c:forEach>
		<c:if test="${nutrientInfoFlag == true && nutrients.name !=repeatFlag}">
		<tr>	
			<td width="50%"><input type="text" readonly name="nutrientName" value="${nutrients.name}" id="name" ></td>
			<td width="50%"><input type="number" step="any" name="instructions" id="instructions" ></td>
		</tr><br/>
		</c:if>
</c:forEach>
<c:set var="dishTypeFlag" value="${dishType.name}"></c:set>
</div>
</c:if>

<div id="nutrientes${dishType.name.replaceAll('\\s','')}" hidden="true" title="Nutrientes">
	 <c:forEach items="${nutrients}" var="nutrients"> 
	<c:if test="${nutrients.dishType==dishType.name}">
		 <tr>
		 		<td width="50%"><input type="text" readonly name="nutrientName" value="${nutrients.name}" id="name" ></td>
				<td width="50%"><input type="number" step="any"  name="instructions" id="instructions" ></td>
		</tr><br/>
		</c:if>
		</c:forEach>
</div>


<!-- AddOn Dish Size  -->

<c:if test="${dishSizeFlag=='hello' && dish.addOnDishSize.size()>0 && dish.addOnDishSize !=null && dish.addOnDishSize!='' && dish.dishType == dishType.name}">
<div id="dishSize${dishType.name.replaceAll('\\s','')}" hidden="true" title="Dish Size">
<tr> 		    <td width="50%"><input type="text" readonly="true" style="background-color:#424242;border: none;color:white;padding-right:95px;" id="name"></input></td>
				<td width="50%"><input type="text" step="any" readonly="true" style="padding-right: 4;background-color:#5C82FF;border: none;text-align: center;color:white;"  value="capacity"></input></td>
				<td width="50%"><input type="text" step="any" readonly="true" style="padding-right:4;background-color:#5C82FF;border: none;text-align: center;color:white;"  value="price"></input></td>
				<td width="50%"><input type="text" step="any" readonly="true" style="padding-right:4;background-color:#5C82FF;border: none;text-align: center;color:white;"  value="display Price"></input></td>
				<td width="50%"><input type="text" step="any" readonly="true" style="padding-right:4;background-color:#5C82FF;border: none;text-align: center;color:white;"  value="factor"></input></td>
</tr><br>
<c:forEach items="${dish.addOnDishSize}" var="dishSize" varStatus="i">
<c:forEach items="${dishSizeList}" var="dishList">
	 	<c:if test="${dishList.dishType==dishType.name}">
		<c:if test="${dishSize.dishSizeId == dishList.id}">
		 <tr>
		 <td width="50%"><form:hidden  path="DishSizeList[${i.index}].id"   id="id" value="${dishSize.id}"></form:hidden></td>
		        <td width="50%"><form:hidden  path="DishSizeList[${i.index}].dishSizeId" id="dishSizeId" value="${dishSize.dishSizeId}"></form:hidden></td>
		        <td width="50%"><form:hidden  path="DishSizeList[${i.index}].addOnDishId" id="addOnDishId" value="${dishSize.addOnDishId}"></form:hidden></td>
		 		<td width="50%"><form:input type="text" readonly="true"  style="background-color:#424242;color:white;border: none;padding-right:95px;" path="DishSizeList[${i.index}].name" id="name" value="${dishSize.name}"></form:input></td>
				<td width="50%"><form:input type="number" step="any"  min="0"  path="DishSizeList[${i.index}].capacity" id="value"  value="${dishSize.capacity}"></form:input></td>
				<td width="50%"><form:input type="number" step="any"  min="0"  path="DishSizeList[${i.index}].price" id="price"  value="${dishSize.price}"></form:input></td>
				<td width="50%"><form:input type="number" step="any"  min="0"  path="DishSizeList[${i.index}].displayPrice" id="displayPrice"  value="${dishSize.displayPrice}"></form:input></td>
				<td width="50%"><form:input type="number" step="any"  min="0"  path="DishSizeList[${i.index}].factor" id="factor"  value="${dishSize.factor}"></form:input></td>
		</tr>
		<br/>
		</c:if>
		</c:if>
</c:forEach>
</c:forEach>

<c:forEach items="${dishSizeList}" var="dishList" varStatus="i">
<c:set var="dishSizeFlag" value=""></c:set>
<c:forEach items="${dish.addOnDishSize}" var="dishSize" >
	 	<c:if test="${dishList.name==dish.dishType}">
		<c:if test="${dishSize.dishSizeId == dishList.id}">
		<c:set var="dishSizeFlag" value="false"></c:set>
		<c:set var="repeatFlag" value="${dishList.name}"></c:set>
		</c:if>
		<c:set var="dishSizeFlag" value="true"></c:set>
		</c:if>
	</c:forEach>
		<c:if test="${dishSizeFlag == true && dishList.name !=repeatFlag}">
		 <tr>
		        <td width="50%"><form:hidden  path="DishSizeList[${i.index}].id" id="dishSizeId" value="${dishSize.id}"></form:hidden></td>
		 	    <td width="50%"><form:hidden  path="DishSizeList[${i.index}].addOnDishId" id="addOnDishId" value="${dish.addOnId}"></form:hidden></td>
		 		<td width="50%"><form:input type="text" readonly="true" style="background-color:#424242;color:white;border: none;padding-right:95px;" path="DishSizeList[${i.index}].name" id="name" value="${dishSize.name}"></form:input></td>
				<td width="50%"><form:input type="number" step="any"  min="0"  path="DishSizeList[${i.index}].capacity" id="value" ></form:input></td>
				<td width="50%"><form:input type="number" step="any"  min="0"  path="DishSizeList[${i.index}].price" id="price" ></form:input></td>
				<td width="50%"><form:input type="number" step="any"  min="0"  path="DishSizeList[${i.index}].displayPrice" id="displayPrice" ></form:input></td>
				<td width="50%"><form:input type="number" step="any"  min="0"  path="DishSizeList[${i.index}].factor" id="factor" ></form:input></td>
		</tr><br/>
		</c:if>
</c:forEach>
<c:set var="dishTypeFlag" value="${dishType.name}"></c:set>
</div>
</c:if>

<div id="dishSize${dishType.name.replaceAll('\\s','')}" hidden="true" title="dishSize">
<c:set var="count" value="0"></c:set>
<tr> 		    <td width="50%"><input type="text" readonly="true" style="background-color:#424242;border: none;color:white;padding-right:95px;" id="name"></input></td>
				<td width="50%"><input type="text" step="any" readonly="true" style="padding-right: 4;background-color:#5C82FF;border: none;text-align: center;color:white;"  value="capacity"></input></td>
				<td width="50%"><input type="text" step="any" readonly="true" style="padding-right:4;background-color:#5C82FF;border: none;text-align: center;color:white;"  value="price"></input></td>
				<td width="50%"><input type="text" step="any" readonly="true" style="padding-right:4;background-color:#5C82FF;border: none;text-align: center;color:white;"  value="display Price"></input></td>
				<td width="50%"><input type="text" step="any" readonly="true" style="padding-right:4;background-color:#5C82FF;border: none;text-align: center;color:white;"  value="factor"></input></td>
</tr><br>
	
	 <c:forEach items="${dishSizeList}" var="dishSize" varStatus="i"> 
	<c:if test="${dishSize.dishType==dishType.name}">
		 <tr>
		        <td width="50%"><form:hidden  path="DishSizeList[${i.index}].dishSizeId" id="dishSizeId" value="${dishSize.id}"></form:hidden></td>
		 	    <td width="50%"><form:hidden  path="DishSizeList[${i.index}].addOnDishId" id="addOnDishId" value="${dish.addOnId}"></form:hidden></td>
		 		<td width="50%"><form:input type="text" readonly="true" style="background-color:#424242;color:white;border: none;padding-right:95px;" path="DishSizeList[${i.index}].name" id="name" value="${dishSize.name}"></form:input></td>
				<td width="50%"><form:input type="number" step="any" min="0"   path="DishSizeList[${i.index}].capacity" id="value" ></form:input></td>
				<td width="50%"><form:input type="number" step="any" min="0"   path="DishSizeList[${i.index}].price" id="price" ></form:input></td>
				<td width="50%"><form:input type="number" step="any" min="0"   path="DishSizeList[${i.index}].displayPrice" id="displayPrice" ></form:input></td>
				<td width="50%"><form:input type="number" step="any" min="0"   path="DishSizeList[${i.index}].factor" id="factor" ></form:input></td>
		<c:set var="count" value="1"></c:set>
		</tr><br/>
		</c:if>
		</c:forEach>
		<c:if test="${count==0}">
		<center><tr><td colspan="5" align="center"> Add-On Dish Sizes are not yet created please create <a href="dishTypes/listDishSize">here</a></td></tr></center>
		</c:if>
</div>

</c:forEach>
</form:form>

<form:form method="post" action="addOnDish/add.html" commandName="dish" enctype="multipart/form-data">

	<form:hidden path="addOnId"/>
	<form:hidden path="imageUrl"/>
	<form:hidden path="rectangularImageUrl"/>
	<form:hidden path="restaurantId" value='<%=request.getSession().getAttribute("restaurantId")%>'/>
	<table id="addOn" style="border-collapse:collapse!important;">
	
	<tr>
		<td><form:label path="name"><spring:message code="label.name"/>* </form:label></td>
		<td><form:input path="name" maxlength="100" required="true" class="validate[required]" /></td> 
	</tr>
	<tr>
		<td><form:label path="description"><spring:message code="label.description"/></form:label></td>
		<td><textarea id="description" name="description"  placeholder="Description" style="width:680px;" >${dish.description}</textarea></td>
	</tr>
	<tr>
		<td><form:label path="shortDescription">Short Description</form:label></td>
		<td><textarea id="shortDescription" name="shortDescription"  placeholder="Short Description" style="width:680px;">${dish.shortDescription}</textarea></td>
	</tr>
	
	<tr class="price">
		<td><form:label path="price">Price* </form:label></td>
		<td><form:input path="price" required="true" class="validate[required]" /></td>
	</tr>
	<tr class="displayPrice">
		<td><form:label path="displayPrice">Display Price </form:label></td>
		<td><form:input path="displayPrice"  /></td>
	</tr>
	
	<tr>
		<td><form:label path="limitQuantity">Quantity limit* </form:label></td>
		<td><form:input  type="number" path="limitQuantity" required="true" class="validate[required]" /></td>
	</tr>
	
	<tr>
		<td><form:label path="dishType">Add-On Type</form:label></td>
		
		<td>
		<select onchange="selectNutrientes();" name="dishType" id="dishType">
			<c:choose>
					<c:when test="${'OTHERS' == dish.dishType }">
						<option value="OTHERS" selected="selected">OTHERS</option>
					</c:when>
					<c:otherwise>
						<option value="OTHERS">OTHERS</option>
					</c:otherwise>
				</c:choose>
			
			<c:forEach items="${dishTypes}" var="dishType">
				<c:choose>
					<c:when test="${dishType.name == dish.dishType }">
						<option value="${dishType.name}" selected="selected">${dishType.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${dishType.name}">${dishType.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			
		</select>
		</td>
	</tr>
	
	<tr><td id="colAddNutrientCenterLabel"></td><td id="colAddNutrientCenterButton" ></td></tr>
	
	<tr>
	<td>Manage Size</td>
		<td>
		<input type="checkbox" onclick="showAddOnDishSizeDiv()" id="manageDishSize" name="manageDishSize" />
		</td>
	</tr>
	
	<tr><td id="colAddDishSizeButton" colspan="2"></td></tr>
	
	
	<tr>
		<td><form:label path="vegetarian">Vegetarian</form:label></td>
		<td>
		<c:choose>
			<c:when test="${dish.vegetarian}"><input type="checkbox" id="vegetarian" name="vegetarian" checked/></c:when>
			<c:otherwise><input type="checkbox" id="vegetarian" name="vegetarian" /></c:otherwise>
		</c:choose>
		
		</td>
	</tr>
	
	<tr>
		<td><form:label path="alcoholic">Alcoholic</form:label></td>
		<td>
		<c:choose>
			<c:when test="${dish.alcoholic}"><input type="checkbox" id="alcoholic" name="alcoholic" checked /></c:when>
			<c:otherwise><input type="checkbox" id="alcoholic" name="alcoholic" /></c:otherwise>
		</c:choose>
		
		
		</td>
	</tr>
	<tr>
		<td><form:label path="disabled">Disabled</form:label></td>
		<td>
		<c:choose>
			<c:when test="${dish.disabled}"><input type="checkbox" id="disabled" name="disabled" checked /></c:when>
			<c:otherwise><input type="checkbox" id="disabled" name="disabled" /></c:otherwise>
		</c:choose>
		
		
		</td>
	</tr>
	<!-- Dish active days -->
<%-- 	<tr>
		<td><form:label path="dishActiveDays">Dish Active days</form:label></td>
		<td>
		<c:forEach items="${weekdayFlags}" var="weekdayFlag">
			${weekdayFlag.weekdayCode}
				<c:choose>
					<c:when test="${dish.dishActiveDays[weekdayFlag.index]}">
						<input type="checkbox" id="dishActiveDays[${weekdayFlag.index}]" name="dishActiveDays[${weekdayFlag.index}]" checked />
					</c:when>
					<c:otherwise>
						<input type="checkbox" id="dishActiveDays[${weekdayFlag.index}]" name="dishActiveDays[${weekdayFlag.index}]" />
					</c:otherwise>
				</c:choose>
			
		</c:forEach>
		</td>
	</tr> --%>
	
	<tr>
		<td width="30"><form:label path="imageUrl">
		<spring:message code="label.imageUrl"/>
		<c:choose>
			<c:when test="${fn:startsWith(dish.imageUrl, 'http://')}">(${dish.imageUrl})</c:when>
			<c:when test="${fn:startsWith(dish.imageUrl, '/')}">(${dish.imageUrl})</c:when>
		</c:choose> 
		</form:label></td>
		<td><input type="file" name="file"/>
		<form:errors path="imageUrl" style="color:red;"/> </td>
	</tr>
	
	<tr>
		<td><form:label path="rectangularImageUrl">
		Rectagular  Image
		<c:choose>
			<c:when test="${fn:startsWith(dish.rectangularImageUrl, 'http://')}">(${dish.rectangularImageUrl})</c:when>
			<c:when test="${fn:startsWith(dish.rectangularImageUrl, '/')}">(${dish.rectangularImageUrl})</c:when>
		</c:choose> 
		</form:label></td>
		<td><input type="file" name="file[1]"/>
		<form:errors path="rectangularImageUrl" style="color:red;"/> </td>
	</tr>
	
	<tr>
	<td>Current Time : ${currTime}</td>
	</tr>
	
	<!-- <tr>
	<td>Happy hour</td>
	</tr> -->
	<%-- <tr>
	
		<td><form:label path="happyHourEnabled">Enabled</form:label></td>
		<td>
		<c:choose>
			<c:when test="${dish.happyHourEnabled}"><input type="checkbox" id="happyHourEnabled" name="happyHourEnabled" checked /></c:when>
			<c:otherwise><input type="checkbox" id="happyHourEnabled" name="happyHourEnabled" /></c:otherwise>
		</c:choose>
		</td>
	</tr>
	<!-- Happy Hour active days -->
	<tr>
		<td><form:label path="happyHourActiveDays">Happy hour Active days</form:label></td>
		<td>
		<c:forEach items="${weekdayFlags}" var="weekdayFlag">
			${weekdayFlag.weekdayCode}
			
				<c:choose>
					<c:when test="${dish.happyHourActiveDays[weekdayFlag.index]}">
						<input type="checkbox" id="happyHourActiveDays[${weekdayFlag.index}]" name="happyHourActiveDays[${weekdayFlag.index}]" checked />
					</c:when>
					<c:otherwise>
						<input type="checkbox" id="happyHourActiveDays[${weekdayFlag.index}]" name="happyHourActiveDays[${weekdayFlag.index}]" />
					</c:otherwise>
				</c:choose>
			
		</c:forEach>
		</td>
	</tr>
	 --%>
	<%-- <tr>
		<td><form:label path="happyHourStartHour">Happy Hour Start</form:label></td>
		<td>
		Hour
		<select name="happyHourStartHour">
			 
			<c:forEach items="${hours}" var="hour">
				<c:choose>
					<c:when test="${dish.happyHourStartHour == hour}">
						<option value="${hour}" selected>${hour}</option>
					</c:when>
					<c:otherwise>
						<option value="${hour}">${hour}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		Min 
		<select name="happyHourStartMin">
			<c:forEach items="${mins}" var="min">
				<c:choose>
					<c:when test="${dish.happyHourStartMin == min}">
						<option value="${min}" selected>${min}</option>
					</c:when>
					<c:otherwise>
						<option value="${min}">${min}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		</td>
	</tr> --%>
	<%-- <tr>
		<td><form:label path="happyHourEndHour">Happy Hour End</form:label></td>
		<td>
		Hour 
		<select name="happyHourEndHour">
			<c:forEach items="${hours}" var="hour">
				<c:choose>
					<c:when test="${dish.happyHourEndHour == hour}">
						<option value="${hour}" selected>${hour}</option>
					</c:when>
					<c:otherwise>
						<option value="${hour}">${hour}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		Min 
		<select name="happyHourEndMin">
			<c:forEach items="${mins}" var="min">
				<c:choose>
					<c:when test="${dish.happyHourEndMin == min}">
						<option value="${min}" selected>${min}</option>
					</c:when>
					<c:otherwise>
						<option value="${min}">${min}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		</td>
	</tr>
	<tr>
		<td><form:label path="happyHourPrice">Happy hour Price </form:label></td>
		<td><form:input path="happyHourPrice" /></td>
	</tr> --%>
	<tr>
		<td colspan="2">
			
				<c:choose>
					<c:when test="${!empty dish.addOnId}"><input type="submit" value="Save Dish"/><button type="button" onclick="document.location.href='addOnDish/'">Cancel</button></c:when>
					<c:otherwise><input type="submit" value="Add Add-On"/><input type="reset" value="Cancel"></c:otherwise>
				</c:choose>
			
		</td>
	</tr>
</table>	
</form:form>
<hr/>
	
<h3>Dishes</h3>
<c:if  test="${!empty dishList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Description</th>
	<th>Add-On Type</th>
	<th>Image</th>
	<th>Display Price</th>
	<th>Price</th>
	<th>Quantity Limit</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${dishList}" var="dish">
	<tr>
		<td style="width:30%;">${dish.name}</td>
		<td style="width:50%;">${dish.description}</td>
		<td style="width:10%;">${dish.dishType}</td>
		<td><img height="200" width="200" src="${dish.imageUrl}" /></td>
		<td>${dish.displayPrice}</td>
		<td>${dish.price}</td>
		<td>${dish.limitQuantity}</td>
		<td><button type="button" onclick="deleteDish(${dish.addOnId});">delete</button></td>
		<td><button type="button" onclick="window.location.href='addOnDish/edit/${dish.addOnId}';">edit</button></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
<script>
$(function() {
	$('#manageDishSize').prop('checked', true);
	selectNutrientes();
	//showAddOnDishSizeDiv();
	
	$("#dish").validationEngine();
});

$('#manageDishSize').change(function() {
	   if ($('#manageDishSize').is(':checked')) {
		   $('#addOn .displayPrice').hide();
		   $('#addOn .price').hide()
	   }else {
		   $("#colAddDishSizeButton").html("");
		   $('#addOn .displayPrice').show();
		   $('#addOn .price').show();
		   }

	});

	function smallDescriptionLimitText(textarea,charCount){
		var data = textarea.innerHTML;
		var textLength = data.toString();
       if(textLength.length>charCount){
			alert("Characters  limit exceeded. You can only enter "+charCount+" characters ");
			textarea.innerHTML=textLength.substr(0,charCount);
            }
		}
	
</script>
</html>
