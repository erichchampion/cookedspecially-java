/**********
 * CONSTANTS
 */
var pathRoot = "/storage/sdcard0/";
var prefsDir = pathRoot + "com.bakedSpecially";
var prefsFile = "local.json";
var prefsPath = prefsDir + "/" + prefsFile;
//http://www.bakedspecially.com:8080/CookedSpecially/menu/getallmenusjson/1
//var comCookedSpeciallyImagePrefix = comCookedSpeciallyApiPrefix;
var comCookedSpeciallyImagePattern = /\/static\/\d+\//;
var comCookedSpeciallyImagePrefix = "http://www.bakedspecially.com:8080";
var comCookedSpeciallyApiPrefix = "http://www.bakedspecially.com:8080";
var comCookedSpeciallyRestaurantApi = comCookedSpeciallyApiPrefix + "/CookedSpecially/menu/getallmenusjson/1";
var localRestaurantApi = pathRoot + prefsPath; 
var localImagePrefix = pathRoot + "imgcache/";
var db;

var changeCount = 0;
var menuCount = 0;

/**********
 * GLOBALS
 */
var localDataValid = false;
var remoteDataValid = false;
var globalDataString = "";
var globalData = {};
var globalDataRemoteString = "";
var globalDataRemote = {};
var globalDataLocalString = "";
var globalDataLocal = {};
var startTime;
var endTime;
var loadingTime;


// Added for web support
var restaurantId = "1"; //Axis
var appVersion = '1.8.2';
var appLatestVersion = '1.8.2';

var isWeb = ( location.href.indexOf('http://') > -1 ) ? true : false;
var isCooked = ( location.href.indexOf('especially.com') > -1 ) ? true : false;
if ( isWeb && ! isCooked ){
	comCookedSpeciallyRestaurantApi = "ajax/menus.json";    
}else{
	comCookedSpeciallyRestaurantApi = comCookedSpeciallyApiPrefix + "/CookedSpecially/menu/getallmenusjson/" + restaurantId; 
}


/**********
 * FUNCTIONS
 */




/***********
 * BEGIN addMenuToHomePage
 * Creates the home page with al the menus
 */
function addMenuToHomePage(menuId, name, description, imageUrl) {
console.log("DEBUG > BEGIN addMenuToHomePage: " + name);
    var theMenu = '<a data-role="button" data-theme="c" data-transition="slide" class="topMenuBtn ui-btn ui-shadow ui-btn-corner-all-DISABLED" id="' + menuId + 
	                '" href="#' + menuId + '" data-corners="true" data-shadow="true" data-iconshadow="true" data-wrapperels="span" style="display:block;float:left">' +
				    '<span class="ui-btn-inner ui-btn-corner-all-DISABLED">' +
				      '<span class="ui-btn-text">' + name + '</span>' +
				    '</span>' +
				    '<div class="buttonFooter"></div>' +
				  '</a>';
 
	if ($("#topMenu " + menuId).length == 0) {
		 $(theMenu).appendTo("#topMenu");
		if (imageUrl.length != 0) {
			var menuSelector = "a#" + menuId;
			var imageUrl = "url(" + comCookedSpeciallyImagePrefix + imageUrl + ")";
			$(menuSelector).css({"background-image": imageUrl, "background-size": "cover"});
			ImgCache.cacheBackground($(menuSelector));
			ImgCache.useCachedBackground($(menuSelector));

			console.log("DEBUG > menuSelector: " + menuSelector );	
			console.log("DEBUG >>>> imageUrl: " + imageUrl );
		}
	}
	else {
		console.log("DEBUG > Skipping menu " + name + "because it has already been added");
	}

	console.log("DEBUG > END addMenuToHomePage");	
}
/* 
 * END addMenuToHomePage
 **********/
 
 




/***********
 * BEGIN addMenuPage
 * Creates a container page for each menu pages
 */
function addMenuPage(menuId, name, description) {
	console.log("DEBUG > BEGIN addMenuPage");	
	var thePage = '<div data-role="page" id="' + menuId + '" data-url="' + menuId + '">' +
				  '  <div data-theme="a" data-role="header" data-position="fixed">' +
                  '    <div id="nav">' +
				  '      <a id="home-button" data-direction="reverse"  data-transition="slide" href="#menus">Home</a> ' +
				  '      <a id="menu-button" onclick="toggleMenuItems()" >' + name + '</a> ' +
				  '      <a id="submenu-button" onclick="toggleSubMenuItems()">Sections</a>' +
				  '    </div>' +
				  '    <div id="menu-description">' + description + '</div></div>' +
		          '  <div class="menu-items"></div>' + 
				  '  <div class="submenu-items"></div>' + 
				  '    <div data-role="content">' +
				  '    <div id="section-headers"><\/div>' + 
				  '    <div id="container" style="position: relative; overflow: hidden; height: 4860px;" class="isotope"><\/div>' +
				  '    <div id="section-footers"></div>' + 
				  '  </div>' +
                  '  <div class="shade" onclick="hideItemDetail()"></div>' +
                  '  <div class="itemDetail">' +        
                  '    <div class="itemDetailClose" onclick="hideItemDetail()">close</div>' +
                  '    <div class="itemDetailImg"></div>' + 
                  '    <div class="itemDetailName"></div>' + 
                  '    <div class="itemDetailDesc"></div>' + 
                  '    <div class="addToOrder"></div>' + 
                  '    <div class="itemDetailPrice"></div>' + 
                  '    <div class="itemRating">Popular:</div>' + 
                  '  </div>' + 
                  '  <div class="order">' +        
                  '    <div class="orderTitle">Your Order</div>' + 
                  '    <div class="orderItems">Number of Items:</div>' + 
                  '    <div class="orderPrice">Price:</div>' + 
                  '  </div>' + 
				  '</div>';
	

	if ($("div#" + menuId).length == 0) {
		$(thePage).appendTo($.mobile.pageContainer);

		// removed background image
		//$("div#" + menuId).css("background-image", "url(images/background.jpg)");
	
	}
	else {
		console.log("DEBUG > Skipping menu page " + name + "because it has already been added");
	}

	console.log("DEBUG > END addMenuPage");	
}
/* 
 * END addMenuPage
 **********/
 
 


/***********
 * BEGIN addItemToMenuPage
 * Populates the menu pages container
 */
function addItemToMenuPage(menuId, sectionId, item) {
console.log("DEBUG > BEGIN addItemToMenuPage");	
	if (typeof(item.itemId) != "undefined") {
		var itemId = "item" + item.itemId;
		var name = item.name;
		var description = item.description;
		var shortDescription = item.shortDescription;
		var smallImageUrl = "";
		if (typeof(item.smallImageUrl) != "undefined") {
			smallImageUrl = item.smallImageUrl;
			var fullsmallImageUrl = comCookedSpeciallyImagePrefix + smallImageUrl;
			if(ImgCache.isCached(fullsmallImageUrl)) {
                console.log("Previously cached image: " + fullsmallImageUrl);
			} else {
			    ImgCache.cacheFile(fullsmallImageUrl);
			}
		}
		var price = item.price;
		var itemType = item.itemType;
		var vegetarian = item.vegetarian;
		var alcoholic = item.alcoholic;
		
		var theItem = '<div class="item isotope-item ' + sectionId + '" id="' + itemId + '"' + 
                      '     onclick="showItemDetail(\'' + menuId + '\',\'' + itemId + '\',\'' + name + '\',\'' + smallImageUrl + '\',\'' + shortDescription + '\',\'' + price + '\')">' +	
		              '  <div class="itemContent">' +
					  '    <div class="title">' + name + ' </div>' +
					  '    <div class="description"> <!--' + shortDescription + ' --> </div>' +
					  '    <div class="price">' + price + ' </div>' +
					  '  </div>' +
					  '</div> ';
		$(theItem).appendTo("#" + menuId + " #container");
		
		if (smallImageUrl.length != 0) {
			var itemSelector = "div#" + itemId;
			var fullsmallImageUrl = "url(" + comCookedSpeciallyImagePrefix + smallImageUrl + ")";
			$(itemSelector).css({
							"background-color": "white", 
							"background-image": fullsmallImageUrl, 
							"background-repeat": "no-repeat", 
							"background-position": "center bottom", 
							"background-size": "contain"
							});
			ImgCache.useCachedBackground($(itemSelector));
		}

	}

console.log("DEBUG > END addItemToMenuPage");	
}
/* 
 * END addItemToMenuPage
 **********/






/***********
 * BEGIN addSectionToMenuPage
 * Creates the sections drop-down menu for each menu page
 */
function addSectionToMenuPage(menuId, data) {
	console.log("DEBUG > BEGIN addSectionToMenuPage");	
	if (typeof(data.sectionId) != "undefined") {
		var sectionId = "section" + data.sectionId;
		var name = data.name;
		
		// add filter
		var theLink = '<a href="#" data-filter=".' + sectionId + '" >' + name + '</a>';
		$(theLink).appendTo("#" + menuId + " .submenu-items");
		
		//$('a#submenu-button').html(" > " + name);
		
		$.each(data.items, function() {
			addItemToMenuPage(menuId, sectionId, this);
		});
		
	}	
	console.log("DEBUG > END addSectionToMenuPage");	
}
/* 
 * END addSectionToMenuPage
 **********/




















/***********
 * BEGIN addMenuToNav ***** DISABLED ********
 */
function addMenuToNavDISABLED(data) {
console.log("*** DEBUG > BEGIN addMenuToNav");	
console.log("DEBUG > data: " + data.toString() );	
	if (typeof(data.menuId) != "undefined") {
		var menuId = "menu" + data.menuId;
		var name = data.name;
		var theLink = '<a onclick="toggleMenuItems()" href="#' + menuId + '" class="ui-link">' + name + '</a>';
		$.each($(".menu-items"), function() {
			$(theLink).appendTo(this);
		});
	}
	else {
		console.log("addMenuToNav: menuId undefined");
	}
}
console.log("DEBUG > END addMenuToNav");	
/* 
 * END addMenuToNav
 **********/





/***********
 * BEGIN updateOneMenu
 */
function updateOneMenu(data) {
console.log("DEBUG > BEGIN updateOneMenu");	
	if (typeof(data.menuId) != "undefined") {
		var menuId = "menu" + data.menuId;
		var name = data.name;
		var description = data.description;
		var smallImageUrl = "";
		if (typeof(data.smallImageUrl) != "undefined") {
			smallImageUrl = data.smallImageUrl;
		}		
		var modifiedTime = new Date(data.modifiedTime);
		/*
		alert("modifiedTime:" + modifiedTime.toJSON());
		*/
		var sectionId = "";
		addMenuPage(menuId, name, description);
		$.each(data.sections, function() {
			if (typeof(sectionId) != "undefined" && sectionId.length == 0) {
				sectionId = "section" + this.sectionId;
			}
			addSectionToMenuPage(menuId, this);
		});
		//$("div#" + menuId + "[data-role=page]").page();
		initIsotope(menuId, sectionId);
	}
	else {
		console.log("updateOneMenu: menuId undefined");
	}
console.log("DEBUG > END updateOneMenu");	
}
/* 
 * END updateOneMenu
 **********/





/***********
 * BEGIN updateOneMenuOnHomePage
 */
function updateOneMenuOnHomePage(data) {
console.log("DEBUG > BEGIN updateOneMenuOnHomePage");	
	if (typeof(data.menuId) != "undefined") {
		var menuId = "menu" + data.menuId;
		var name = data.name;
		var description = data.description;
		var imageUrl = "";
		if (typeof(data.imageUrl) != "undefined") {
			imageUrl = data.imageUrl;
		}		
		var modifiedTime = new Date(data.modifiedTime);
		/*
		alert("modifiedTime:" + modifiedTime.toJSON());
		*/
		var sectionId = "";
		addMenuToHomePage(menuId, name, description, imageUrl);
	}
	else {
		console.log("updateOneMenuOnHomePage: menuId undefined");
	}
console.log("DEBUG > END updateOneMenuOnHomePage");	
}
/* 
 * END updateOneMenu
 **********/







/***********
 * BEGIN updateAllMenus
 */
function updateAllMenus() {
console.log("DEBUG > BEGIN updateAllMenus");	
	console.log("About to iterate through globalData");
	if (globalData != null && typeof(globalData.menus != "undefined") && globalData.menus != null) {
		$.each(globalData.menus, function(i, item) {
			updateOneMenu(item);
		});
		// changed for version 1.8 
		//$.each(globalData.menus, function() {
		//	addMenuToNav(this);
		//});
	}
	else {
		console.log("DEBUG > globalData invalid");
	}
	
	// The swipe events seem to be applied, but do not work on the device
	for (var i = 0; i < menuCount; i++) {
		try {
			console.log("Iterating through globalData.menus to update swipe events: " + i + ":" + globalData.menus[i].name);
			$("div#menu" + globalData.menus[i].menuId).live('swipeleft', function (event, ui) {
				console.log("swipeleft");
				$.mobile.changePage("#home", "slide");
				event.stopImmediatePropagation();
			});
			$("div#menu" + globalData.menus[i].menuId).live('swiperight', function (event, ui) {
				console.log("swiperight");
				$.mobile.changePage("#home", "slide");
				event.stopImmediatePropagation();
			});
			/*
			$("div#menu" + globalData.menus[i].menuId).swiperight(function() {
				$.mobile.changePage("#home"); 
			});			

			$("div#menu" + globalData.menus[i]).swipeleft(function() {
				$.mobile.changePage("#home"); 
			});	
			*/
		} catch (error) {
			console.error("Error applying swipe events: " + error);
		}
	}
	
	if (!isWeb){ // Added for web support
		//Now that we're finally finished with the ajax data, cache the remote data if necessary
		window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, getFSForWriting, onFileError);
	}

	endTime = new Date().getTime();
	loadingTime = endTime - startTime
	console.info("DEBUG >>>> Loading time: " + loadingTime + " miliseconds"  );
	
console.log("DEBUG > END updateAllMenus");	
}
/* 
 * END updateAllMenus
 **********/

/***********
 * BEGIN updateAllMenusOnHomePage
 */
function updateAllMenusOnHomePage() {
console.log("DEBUG > BEGIN updateAllMenusOnHomePage");	
	console.log("About to iterate through globalData");
	if (globalData != null && typeof(globalData.menus != "undefined") && globalData.menus != null) {
		$.each(globalData.menus, function(i, item) {
			updateOneMenuOnHomePage(item);
			menuCount++;
		});
		$("#app-loader").css({ "display": "none" });
		$("#allPages").css({ "visibility": "visible" });
		$("#home").on( "pagebeforeshow", function(event, ui) {
			// do something before navigating to a new page
		});
		$("#home").on( "pageshow", function(event, ui) {
			// do something when the home screen is shown
		});

		console.log("menuCount: " + menuCount);
		
		setTimeout(updateAllMenus, 100);
	}
	else {
		console.log("DEBUG > globalData invalid");
	}
	
console.log("DEBUG > END updateAllMenusOnHomePage");	
}
/* 
 * END updateAllMenusOnHomePage
 **********/

/***********
 * BEGIN compareAjaxData
 */
function compareAjaxData() {
console.log("DEBUG > BEGIN compareAjaxData");	

	var modifiedTimeLocal = 0;
	var modifiedTimeRemote = 0;

	if (globalDataLocal != null && typeof(globalDataLocal.menus) != "undefined") {
		$.each(globalDataLocal.menus, function() {
			if (typeof(this.modifiedTime) != "undefined") {
				if (this.modifiedTime > modifiedTimeLocal) {
					modifiedTimeLocal = this.modifiedTime;
				}
			}
		});
	}
	if (globalDataRemote != null && typeof(globalDataRemote.menus) != "undefined") {
		$.each(globalDataRemote.menus, function() {
			if (typeof(this.modifiedTime) != "undefined") {
				if (this.modifiedTime > modifiedTimeRemote) {
					modifiedTimeRemote = this.modifiedTime;
				}
			}
		});
	}

	if (modifiedTimeLocal < modifiedTimeRemote) {
		globalData = JSON.parse(globalDataRemoteString);
		remoteDataValid = true;
		ImgCache.init();
		ImgCache.options.debug = true;
        ImgCache.options.usePersistentCache = true;
		ImgCache.clearCache(); 
	}
	else {
		if (globalDataLocalString != null && typeof(globalDataLocalString) != "undefined" && globalDataLocalString.length > 0) {
			globalData = JSON.parse(globalDataLocalString);
			if (globalData != null && typeof(globalData.menus) != "undefined") {
				localDataValid = true;
			}
		}
	}
	console.log("localDataValid: " + localDataValid);
	
console.log("DEBUG > END compareAjaxData");	
}
/* 
 * END compareAjaxData
 **********/





/***********
 * BEGIN updateAjaxDataRemote
 */
function updateAjaxDataRemote() {
console.log("DEBUG > BEGIN updateAjaxDataRemote");	

	
	if ( isWeb ){ // Added for web support
		$.ajax(comCookedSpeciallyRestaurantApi, {
				isLocal: false
		})
		.done (function (data) {
			if( console && console.log ) {
				console.log("(web) ajax success: " + comCookedSpeciallyRestaurantApi);
				console.log(data);
			}
			globalData = data;
			updateAllMenusOnHomePage();			
		})
		.fail(function() { 
			console.log("(web) ajax error: " + comCookedSpeciallyRestaurantApi); 
		})
		.always(function() { 
			console.log("(web) ajax complete: " + comCookedSpeciallyRestaurantApi); 

		});		
	
	
	}else if (checkConnection()) {
		$.ajax(comCookedSpeciallyRestaurantApi, {
			isLocal: false
		})
		.done (function (data) {
			if( console && console.log ) {
				console.log("ajax success: " + comCookedSpeciallyRestaurantApi);
				globalDataRemoteString = JSON.stringify(data);
				console.log(globalDataRemoteString);
			}

			globalDataRemote = JSON.parse(globalDataRemoteString);
		})
		.fail(function() { 
			console.log("ajax error: " + comCookedSpeciallyRestaurantApi); 
		})
		.always(function() { 
			console.log("ajax complete: " + comCookedSpeciallyRestaurantApi); 
			compareAjaxData();
			updateAllMenusOnHomePage();
		});
	}
	else {
		console.log("ajax error: offline"); 
		compareAjaxData();
		updateAllMenusOnHomePage();
	}
				
	
console.log("DEBUG > END updateAjaxDataRemote");	
}
/* 
 * END updateAjaxDataRemote
 **********/

/***********
 * BEGIN updateAjaxDataLocal
 */
function updateAjaxDataLocal() {
console.log("DEBUG > BEGIN updateAjaxDataLocal");	

	window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, getFS, onFileError);

console.log("DEBUG > END updateAjaxDataLocal");	
}
/* 
 * END updateAjaxDataLocal
 **********/



try {
	var startTime = new Date().getTime();
	
	//http://stackoverflow.com/questions/13986182/how-can-i-improve-the-page-transitions-for-my-jquery-mobile-app/13986390#13986390
	$(document).one("mobileinit", function () {
		$.mobile.ajaxEnabled = true;
		$.mobile.allowCrossDomainPages = true;
		$.mobile.autoInitializePage = true;
		$.mobile.defaultPageTransition = "none";
		$.mobile.loader.prototype.options.text = "loading";
		$.mobile.loader.prototype.options.textVisible = true;
		$.mobile.mobile.touchOverflowEnabled = true;
		$.mobile.pageContainer = $("#allPages");
		$.mobile.phonegapNavigationEnabled = true;
	});

	$(document).ready(function(){
		// When the document is ready, Cordova isn't
		//updateAllMenus();
		//cacheImages();
	});

	/* 
	 * http://css-tricks.com/forums/discussion/16123/reload-jquery-functions-on-ipad-orientation-change/p1
	 */
	function myOrientResizeFunction(){
		 changeCount++;
		 console.log("Orientation change: " + changeCount);
	}

	//bind to resize
	$(window).resize( function() {
		  myOrientResizeFunction()
	});


	//if you need to call it at page load to resize elements etc.
	$(window).load( function() {
		  myOrientResizeFunction()
	});

	//check for the orientation event and bind accordingly
	if (window.DeviceOrientationEvent) {
	  window.addEventListener('orientationchange', myOrientResizeFunction, false);
	} 

	// http://docs.phonegap.com/en/2.0.0/cordova_file_file.md.html#File
	
    // Wait for Cordova to load
    //
    if ( isWeb ){ // Added for web support
    	updateAjaxDataRemote();
	}else{
		document.addEventListener("deviceready", onDeviceReady, false);
	}
	
    // Cordova is ready
    //
    function onDeviceReady() {
        //ImgCache.init();
        //ImgCache.options.debug = true;
		//ImgCache.options.usePersistentCache = true;
		updateAjaxDataLocal();
		updateAjaxDataRemote();
	}


	function checkConnection() {
		var networkState = navigator.connection.type;

		var states = {};
		states[Connection.UNKNOWN]  = 'Unknown connection';
		states[Connection.ETHERNET] = 'Ethernet connection';
		states[Connection.WIFI]     = 'WiFi connection';
		states[Connection.CELL_2G]  = 'Cell 2G connection';
		states[Connection.CELL_3G]  = 'Cell 3G connection';
		states[Connection.CELL_4G]  = 'Cell 4G connection';
		states[Connection.NONE]     = 'No network connection';

		console.log('Connection type: ' + states[networkState]);
		
		if (states[networkState] == 'No network connection') {
			return(false);
		}
		return(true);
	}

/* File writing */
    function getFSForWriting(fileSystem) {
    	// Retrieve an existing directory, or create it if it does not already exist
        fileSystem.root.getDirectory(prefsDir, {create: true, exclusive: false}, checkDirSuccess, onFileError); 
        fileSystem.root.getFile(prefsPath, {create: true, exclusive: false}, getFileForWriting, onFileError);
    }

    function getFileForWriting(fileEntry) {
    	console.log("Writing to: " + fileEntry.toURL());
        fileEntry.createWriter(getFileWriter, onFileError);
    }

    function getFileWriter(writer) {
        if (remoteDataValid) {
			console.log("Writing: " + globalDataRemoteString);
			writer.truncate(0);
			writer.write(globalDataRemoteString);
        }
    }
    
/* File reading */
    function getFS(fileSystem) {
        fileSystem.root.getFile(prefsPath, {create: true, exclusive: false}, readAsText, onFileError);
    }

    function readAsText(file) {
	console.log("DEBUG > BEGIN readAsText");	
        var reader = new FileReader();
        reader.onloadend = function(evt) {
            console.log("Read from file: " + file.fullPath);
            globalDataLocal = JSON.parse(evt.target.result);
            globalDataLocalString = JSON.stringify(globalDataLocal);
            console.log("Read data: " + globalDataLocalString);
        };
        reader.readAsText(file);
	console.log("DEBUG > END readAsText");	
    }
    
	function checkDirSuccess(parent) {
	    console.log("Parent Name: " + parent.name);
	}

	function onFileSuccess(fileSystem) {
    	console.log("File success: " + fileSystem.name);
    	console.log(fileSystem.name);
	}

	function onFileError(error) {
    	console.log("File error: " + error.code);
        console.log(error.code);
    }








function showItemDetail(p, id, name, image, desc, price) {
    $('.menu-items').hide();
	//$('.submenu-items').hide();
	
	//alert ( 'Name: ' + name + '\nImage: ' + image + '\nDescription: ' + desc+ '\nPrice: ' + price);
    $('.shade').hide();
    $('.itemDetail').hide();
    
    $('#' + p + ' .shade').show();
    $('#' + p + ' .itemDetail').show();
    
	$('#' + p + ' .order').show();

    $('#' + p + ' .itemDetailName').html(name);
    $('#' + p + ' .itemDetailImg').css('background-image', 'url(' + comCookedSpeciallyImagePrefix + image + ') ');
    $('#' + p + ' .itemDetailDesc').html(desc);
    $('#' + p + ' .itemDetailPrice').html(price);
    
    $('#' + p + ' .addToOrder').html('<a href="javascript:addToOrder(\''+ p + '\',\'' + id + '\' )">Add To Order</a>');
    
    
    
}



function hideItemDetail() {
    $('.itemDetail').fadeOut();
    $('.order').fadeOut();    
    $('.shade').fadeOut();
}


function toggleMenuItemsDISABLED(){
	hideItemDetail();
	if ($('.submenu-items').is(":visible") ) {
		$('.submenu-items').css('z-index','1').slideUp();
	}
	
	if ($('.menu-items').is(":visible") ) {
		$('.menu-items').css('z-index','1').slideUp();
		$('.menu-items').slideUp();
	}else{
		$('.menu-items').css('z-index','2').slideUp();
		$('.menu-items').slideDown();
	}
	
	return false;
} 


function toggleSubMenuItemsDISABLED(){
	hideItemDetail();
	if ($('.menu-items').is(":visible") ) {
		$('.menu-items').css('z-index','1').slideUp();	
		$('.menu-items').slideUp();
	}
	
	if ($('.submenu-items').is(":visible") ) {
		$('.submenu-items').css('z-index','1').slideUp();
		$('.submenu-items').slideUp();
	}else{
		$('.submenu-items').css('z-index','2').slideUp();
		$('.submenu-items').slideDown();
	}
	
	return false;
} 


$("a#menu-button").bind("touchstart", function(e) {
    //toggleMenuItems();
});

$("a#submenu-button").bind("touchstart", function(e) {
    //toggleSubMenuItems();
});







} catch (error) {
	console.error("Your javascript has an error: " + error);
}







/**********
 * BEGIN initIsotope
 */
function initIsotope(menuId, sectionId) {
console.log("DEBUG > BEGIN initIsotope");	
	var contentContainerSelector = "div#" + menuId + " div#container";
	var $contentContainer = $(contentContainerSelector);
	
//	$("div#" + menuId).on( "pagebeforeshow", function(event, ui) {
	$("div#" + menuId).on( "pageshow", function(event, ui) {
		$contentContainer.isotope({
			filter: "." + sectionId 
			//Showing all items is slower than showing one catetory
			//filter: "*" 
		});
		//toggleSubMenuItems();
		
		// changed for version 1.8
		// highlite first section
		$('.submenu-items a').removeClass("activeFilter");
		$('.submenu-items a:first-child').addClass('activeFilter');
		
		
	});

				


	$contentContainer.isotope({
		// options...
		//transformsEnabled: false, 
		resizable: true, 
		filter: "." + sectionId, 
		//Showing all items is slower than showing one category
		//filter: "*" 
		animationEngine: "css" 
	});
	
	
	
	
	// filter items when filter link is clicked
	$("div#" + menuId + " .submenu-items a").click(function(){
		// change for version 1.8
		$(".submenu-items a").removeClass("activeFilter");
		$(this).addClass("activeFilter");
		
		
		var selector = $(this).attr('data-filter');
		
		$contentContainer.isotope({ 
			filter: selector
		});
		
		//toggleSubMenuItems();
		//$("#" + menuId + " .submenu-items").css('z-index','1').slideUp();
		//$("#" + menuId + " .submenu-items").slideUp();
		return false;
	});

	$contentContainer.imagesLoaded(function( $images, $proper, $broken ) {
	//We're not using img elements. We're using CSS backgrounds instead.
	/*
		$proper.each( function() {
			console.log("proper: " + this.src);
			if ($(this).attr('src').indexOf("http://") == 0) {
				ImgCache.useCachedFile($(this));
			}
		});

		$broken.each( function() {
			console.log("broken: " + this.src);
			//var $this = $(this).css({ display:"none" });
			if ($(this).attr('src').indexOf("http://") == 0) {
				ImgCache.useCachedFile($(this));
			}
		});
	*/
	});

console.log("DEBUG > END initIsotope");	
}
/*
 * END initIsotope
 ***********/
 
 
 






/**********
 * BEGIN showAppVersion
 */
function showAppVersion() {
    var appInfo = 'About Axis Tablet App\n\n' +
                  'Restaurant: Axis Cafe' + '\n' +
                  'AppVersion: Beta ' + appVersion + '\n\n' +
                  'Loading Time: ' + loadingTime + ' miliseconds\n' +
                  ''; //'Latest Version: Beta ' + appLatestVersion;
    alert(appInfo);


}
/*
 * END showAppVersion
 ***********/
 	