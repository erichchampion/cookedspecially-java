<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">

  <title>Customer Management</title>

  <link rel="stylesheet" href="css/bootstrap.css?compile=false">

  <link rel="stylesheet" href="css/sb-admin.css?compile=false">

  <link rel="stylesheet" href="css/font-awesome.min.css?compile=false">

  <script src="js/angular/jquery-1.10.2.js" type="text/javascript"></script>
  <script src="js/angular/jquery-ui.js" type="text/javascript"></script>
  <script src="js/angular/jquery-ui.min.js" type="text/javascript"></script>

  <script src="js/angular/bootstrap.js" type="text/javascript"></script>

    <script src="js/angular/angular-1.4.5.js"></script>
    <script src="js/angular/angular-animate-1.4.5.js"></script>
    <script src="js/angular/ui-bootstrap-tpls-0.13.4.js"></script>
  <style>
    .strikethrough {
      text-decoration: line-through
    }
  </style>
  <script src="js/angular/customerManagement.js" type="text/javascript"></script>
  <script src="js/angular/customerProfile.js" type="text/javascript"></script>

  <link rel="stylesheet" href="css/customerManagement.css?compile=false">
  <script src="js/angular/customerProfile.js" type="text/javascript"></script>
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

</head>
<body ng-app="app">
<div id="wrapper">
<div class="spinOverlay"
         style=" display:none;position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>

<div class="spinSpinner"
         style=" display:none;position: fixed; width: 60px; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); background-image: url(http://i.imgur.com/uM2gq.gif); height: 60px; z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;"></div>

    <div class="js-spin-overlay"
         style="position: fixed; opacity: 0.5; z-index: 10000; background-color: rgb(0, 0, 0); left: 0px; top: 0px; width: 100%; height: 100%;"></div>

    <div class="js-spin-spinner"
         style="position: fixed; width: 60px; padding: 10px; background-size: auto; background-color: rgb(255, 255, 255); background-image: url(http://i.imgur.com/uM2gq.gif); height: 60px; z-index: 10001; border-top-left-radius: 6px; border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-bottom-left-radius: 6px; box-shadow: rgb(68, 68, 68) 0px 2px 10px; left: 50%; top: 50%; background-position: 50% 50%; background-repeat: no-repeat no-repeat;"></div>

    <div id="page-wrapper">
      <div ng-controller="CustomerManagementCtrl">
         <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
               <div class="navbar-header col-sm-4">
                </div>
                <div class="navbar-header col-sm-4" style="left: 9%;">
                    <a class="navbar-brand" href="#">{{orgName}}</a>
                </div>
                <div class="navbar-header col-sm-4">
                   <div class="dropdown pull-right">
                        <button class="navbar-brand dropdown-toggle ful-selector" type="button" id="dropdownMenu2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                            Welcome {{name}}
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu fulfillment-dropdown" aria-labelledby="dropdownMenu2"  style="margin-top: 10px;">
                            <li><a href="j_spring_security_logout">Logout</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
           <jsp:include page="common/order/customerProfile.jsp"></jsp:include> 
       </div>
   </div>
</div>
</body>
</html>