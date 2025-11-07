<?php
    // http header
    header('Content-Type: application/json');
    
    // temporary solution using a proxy
    $restaurantId = $_GET["restaurantId"] || "1";
    $url = "http://www.bakedspecially.com:8080/CookedSpecially/menu/getallmenusjson/$restaurantId/";
 
    if ( $restaurantId == "1" ){
        $menus = file_get_contents($url);
        echo $menus;

    }else{
        echo '{"ERROR": "Unknown restaurant ID"}';
    }

?>
