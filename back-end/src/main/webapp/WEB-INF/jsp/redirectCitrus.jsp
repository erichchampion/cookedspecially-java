<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
 <html>
     <head>
         <meta HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
     </head>
     <body>
         <form  id="notice" method="post" action="${formPostUrl}">
             <input type="hidden" id="merchantTxnId" name="merchantTxnId" value="${merchantTxnId}" />
             <input type="hidden" id="orderAmount" name="orderAmount" value="${orderAmount}" />
             <input type="hidden" id="currency" name="currency" value="${currency}" />
             <input type="hidden" name="returnUrl" value="${responseBack}" />
              <input type="hidden" id="notifyUrl" name="notifyUrl" value="${notifyUrl}" />
               <input type="hidden" id="email" name="email" value="${email}" />
             <input type="hidden" id="secSignature" name="secSignature" value="${securitySignature}" />
             
            <!--  <input type="Submit" value="Pay Now"/> -->
         </form>
         
         <script type="text/javascript">
function onLoad () {
	   var frm = document.getElementById("notice");
		frm.submit();
}
window.onload = onLoad();
</script>
         
     </body>
 </html>												
        