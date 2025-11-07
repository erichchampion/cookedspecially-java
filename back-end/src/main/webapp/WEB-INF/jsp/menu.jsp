<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<title>Menu Manager</title>
	<base href="${pageContext.request.contextPath}/"/>
	<link rel="stylesheet" href="css/style.css" />
	<link rel="stylesheet" href="themes/base/jquery.ui.all.css" />
	<script type="text/javascript" src="js/jquery-1.9.0.js"></script>
	<script type="text/javascript" src="js/ui/jquery-ui.js"></script>
	<style type="text/css">
		  #list .ui-selecting { background: #FECA40; }
		  #list .ui-selecting .handle { background: #ddd; }
		  #list .ui-selected { background: #F39814; color: white; }
		  #list .ui-selected .handle { background: #cde; }
		  #list { margin: 1px; padding: 0.1em; border: 1px solid #AAAAAA; }
		  #list div { border: 1px solid #AAAAAA; }
		.data, .data td {
			border-collapse: collapse;
			width: 100%;
			border: 1px solid #aaa;
			margin: 2px;
			padding: 2px;
		}
		.data th {
			font-weight: bold;
			background-color: #808080;
			color: white;
		}
	</style>
	  <script>
	  $(function() {
		  

          $( "#list" )
          .sortable({ handle: ".handle" })
          .selectable({
        	  
        	  create: function( event, ui ) {
        		  $( "div", this).each(function() {
                	  //$(this).addClass("uiselected").trigger('selectableselected');
                	  if($(this).attr('data-state') == 'pre-selected') {
                		  //alert(this.innerHTML);
                		  $(this).addClass("ui-selected").trigger('selectableselected');
                	  }
                	  //alert(this.innerHTML);
                	  
                  });
        			    
				  
        	  },
              
          	  stop: function() {
                  var result = $( "#dishIdList" ).empty();
                  $( ".ui-selected", this ).each(function() {
                	  //alert(this.lastChild.data) ;
                    var index = $(this).attr("data-dishId");
                    console.log(index);
                    var dishName = this.lastChild.data;
                    result.append("<option value=\"" + index + "\" selected=\"selected\">" + dishName + "</option>");
                    //result.append( " #" + index );
                  });
                }
              })
            .find( "div" )
              .addClass( "ui-corner-all" )
              .prepend( "<div class='handle'><span class='ui-icon ui-icon-carat-2-n-s' style=\"float:left\">" ).append("</span></div>");
          
         
      });
	  </script>
</head>
<body>
<c:set var="sessionUserId" value='<%=request.getSession().getAttribute("userId")%>'/>
<c:if test='${!empty sessionUserId}'>
Logged in as <%=request.getSession().getAttribute("username")%> <a href="user/logout">Logout</a>
</c:if>

<hr/>
<h3>Add Menu</h3>                
<form:form method="post" action="menu/add.html" commandName="menu">

	<form:hidden path="userId" value='<%=request.getSession().getAttribute("userId")%>'/>
	<form:hidden path="menuId" />
	<table>
	<tr>
		<td><form:label path="name"><spring:message code="label.name"/></form:label></td>
		<td><form:input path="name" /></td> 
	</tr>
	<tr>
		<td><form:label path="description"><spring:message code="label.description"/></form:label></td>
		<td><form:input path="description" /></td>
	</tr>
	
	<tr>
		<td><form:label path="dishes"><spring:message code="label.dishes"/></form:label></td>
		<td>
			<div id="list" >
				<c:if test="${!empty dishList}">
					<c:forEach items="${dishList}" var="dish">
						
						<c:choose>
							<c:when test="${!empty existingDishIds[dish.dishId] }">
								<div  data-state="pre-selected" data-dishId="${dish.dishId}">${dish.name}</div>
							</c:when>
							<c:otherwise>
								<div  data-state="not-selected" data-dishId="${dish.dishId}">${dish.name}</div>
							</c:otherwise>
						</c:choose>
						
					</c:forEach>
				</c:if>
			</div>
			
			<select id="dishIdList" name="dishIds" hidden="true" multiple="multiple">
				<c:if test="${!empty dishList}">
					<c:forEach items="${dishList}" var="dish">
						
						<c:choose>
							<c:when test="${!empty existingDishIds[dish.dishId] }">
								<option value="${dish.dishId}" selected="selected">${dish.name}</option>
							</c:when>
							<c:otherwise>
								
							</c:otherwise>
						</c:choose>
						
					</c:forEach>
				</c:if>
			</select>
			<!--             
            <select name="dishIds" multiple="multiple">
	            <c:if test="${!empty dishList}">
					<c:forEach items="${dishList}" var="dish">
						
						<c:choose>
							<c:when test="${!empty existingDishIds[dish.dishId] }">
								<option value="${dish.dishId}" selected="selected">${dish.name}</option>
							</c:when>
							<c:otherwise>
								<option value="${dish.dishId}" >${dish.name}</option>
							</c:otherwise>
						</c:choose>
						
					</c:forEach>
				</c:if>
			</select>
			-->
		</td>
	</tr>
	
	<tr>
		<td colspan="2">
			<input type="submit" value="<spring:message code="label.addmenu"/>"/>
		</td>
	</tr>
</table>	
</form:form>

<hr/>
<h3>Menus</h3>
<c:if  test="${!empty menuList}">
<table class="data">
<tr>
	<th>Name</th>
	<th>Description</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
	<th>&nbsp;</th>
</tr>
<c:forEach items="${menuList}" var="menu">
	<tr>
		<td>${menu.name}</td>
		<td>${menu.description}</td>
		<td><a href="menu/delete/${menu.menuId}">Delete</a></td>
		<td><a href="menu/edit/${menu.menuId}">Edit</a></td>
		<td><a href="menu/show/${menu.menuId}">Show</a></td>
	</tr>
</c:forEach>
</table>
</c:if>


</body>
</html>
