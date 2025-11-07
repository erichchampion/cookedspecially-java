var accessCode = '';
var tableId = '';

function keyDown(src, k){
    //alert ( 'keyDown' );
    
    if (src == "accessCode") {
        if ( $(k).attr('value') != 'OK'){
            accessCode = accessCode + $(k).attr('value');
            $('#accessCodeField').html($('#accessCodeField').html() + '*');
        }else{
            checkAccessCode();
        }
        
    }else if (src == "setTableId") {
        if ( $(k).attr('value') != 'OK'){
            tableId = tableId + $(k).attr('value');
            $('#setTableIdField').html($('#setTableIdField').html() + $(k).attr('value') );
        }else{
            setTableId();
        }
    }

}

function checkAccessCode(k){
    if ( accessCode == "1984" ){
        //alert( 'Passed' );
		$('#accessCodeField').html('');
        $('#adminLogin').hide();
        $('#setTableId').show();
        //$('#home.admin .shade').hide();
    }else{
        alert( 'Fail' );
    }
    accessCode = '';
	$('#accessCodeField').html(accessCode);

}


function setTableId(){

	if (tableId.length > 0 ){
		setActiveTable(tableId);
		$('#setTableIdField').html('');
        $('#setTableId').hide();
		$('#adminLogin').show();
		location.href = "#home";
	}else{
		alert ('ERROR: Invalid Table ID');
	}
}