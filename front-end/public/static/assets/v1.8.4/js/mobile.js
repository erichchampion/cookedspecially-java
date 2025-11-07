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

/* *********
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
var appVersion = '1.8.4';
var appLatestVersion = '1.8.4';

var isWeb = ( location.href.indexOf('http://') > -1 ) ? true : false;
var isCooked = ( location.href.indexOf('especially.com') > -1 ) ? true : false;
if ( isWeb && ! isCooked ){
	comCookedSpeciallyRestaurantApi = "ajax/menus.json";    
}else{
	comCookedSpeciallyRestaurantApi = comCookedSpeciallyApiPrefix + "/CookedSpecially/menu/getallmenusjson/" + restaurantId; 
}




/* **********
 * 1. BEGIN initHomePage
 * Iterates through all menus in data file and add menus to the home page
   Function Calls 
    - addMenuToHomePage() for each menu
    - updateAllMenus()
 */
function initHomePage() {
	console.log("DEBUG > BEGIN initHomePage");	
	console.log("About to iterate through globalData");
	var menuIndex = 0;

	if (globalData != null && typeof(globalData.menus != "undefined") && globalData.menus != null) {
		
		// iterate through data file and create menus in homepage
		$.each(globalData.menus, function() {
			if (typeof(globalData.menus[menuIndex].menuId) != "undefined") {
				addMenuToHomePage(menuIndex);
				menuIndex++;
			}
		});
		
		$("#app-loader").css({ "display": "none" });
		$("#allPages").css({ "visibility": "visible" });
		$("#home").on( "pagebeforeshow", function(event, ui) {
			// do something before navigating to a new page
		});
		$("#home").on( "pageshow", function(event, ui) {
			// do something when the home screen is shown
		});

		console.log("menuCount: " + menuIndex);
		
		// create all menu pages
		setTimeout(updateAllMenus, 100);
	}
	else {
		console.log("DEBUG > globalData invalid");
	}
	//console.log("DEBUG > END initHomePage");	
}
/* 
 * END initHomePage
 **********/

 
 

/* 
 *******************************************************
                     UI FUNCTIONS
 ******************************************************* 
 */
 
/* **********
 * 2. BEGIN addMenuToHomePage
 * Creates the home page with all the menus
    Parameters: 
    m - menu index 
 */
function addMenuToHomePage(m) {
	console.info("DEBUG > BEGIN addMenuToHomePage: " + globalData.menus[m].name); 

    var menuId = globalData.menus[m].menuId;
    var name = globalData.menus[m].name;
    var imageUrl = globalData.menus[m].imageUrl;
	
	var theMenu = '<a data-role="button" data-theme="c" data-transition="slide" class="topMenuBtn ui-btn ui-shadow ui-btn-corner-all-DISABLED" id="menu' + menuId + 
	                '" href="#menu' + menuId + '" data-corners="true" data-shadow="true" data-iconshadow="true" data-wrapperels="span" style="display:block;float:left">' +
				    '<span class="ui-btn-inner ui-btn-corner-all-DISABLED">' +
				      '<span class="ui-btn-text">' + name + '</span>' +
				    '</span>' +
				    '<div class="buttonFooter"></div>' +
				  '</a>';
 
	if ($("#topMenu " + menuId).length == 0) {
		 $(theMenu).appendTo("#topMenu");
		if (imageUrl.length != 0) {
			var menuSelector = "a#menu" + menuId;
			var imageUrl = "url(" + comCookedSpeciallyImagePrefix + imageUrl + ")";
			$(menuSelector).css({"background-image": imageUrl, "background-size": "cover"});
			ImgCache.cacheBackground($(menuSelector));
			ImgCache.useCachedBackground($(menuSelector));

			//console.log("DEBUG > menuSelector: " + menuSelector );	
			//console.log("DEBUG >>>> imageUrl: " + imageUrl );
		}
	}
	else {
		console.log("DEBUG > Skipping menu " + name + "because it has already been added");
	}
	//console.log("DEBUG > END addMenuToHomePage");	
}
/* 
 * END addMenuToHomePage
 **********/
 
 
 
 
/* **********
 * 3. BEGIN updateAllMenus
 * Creates all menu pages
   Function Calls 
    - updateMenuPage() for each menu
 */
function updateAllMenus() {
	console.log("DEBUG > BEGIN updateAllMenus");
	console.log("About to iterate through globalData");
	
	// iterate through each menu
	var menuIndex = 0;
	if (globalData != null && typeof(globalData.menus != "undefined") && globalData.menus != null) {
		$.each(globalData.menus, function() {
			// create each menu page
			updateMenuPage(menuIndex);
			menuIndex++;
		});
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
	
	//console.log("DEBUG > END updateAllMenus");	
}
/* 
 * END updateAllMenus
 **********/
 
 
 
 
/* **********
 * 4. BEGIN updateMenuPage
 * Create each menu page
    Parameters: 
    m - menu index 
 */
 function updateMenuPage(m) {
	console.info("DEBUG > BEGIN updateMenuPage: " + m);	

	if (typeof(globalData.menus[m].menuId) != "undefined") {
		var menuId = "menu" + globalData.menus[m].menuId;
		var smallImageUrl = "";
		if (typeof(globalData.menus[m].smallImageUrl) != "undefined") {
			smallImageUrl = globalData.menus[m].smallImageUrl;
		}		
		var modifiedTime = new Date(globalData.menus[m].modifiedTime);
		var sectionId = "";
		
		// Create menu page container
		addMenuPage(m);

		// iterate through each section
		var s = 0;
		$.each(globalData.menus[m].sections, function() {
			if (typeof(sectionId) != "undefined" && sectionId.length == 0) {
				sectionId = "section" + this.sectionId;
			}
			// Create section container
			addSectionToMenuPage(m, s);
			s++;
		});
		
		//$("div#" + menuId + "[data-role=page]").page();
		initIsotope(menuId, sectionId);
	
	}else {
		console.log("updateMenuPage: menuId undefined");
	}
	//console.log("DEBUG > END updateMenuPage");	
}

/* 
 * END updateMenuPage
 **********/
 
 


/* **********
 * BEGIN addMenuPage
 * Creates page container for each menu
   Parameters: 
    i - menu index 
 */
//function addMenuPage(menuId, name, description) {
function addMenuPage(m) {
	console.info("DEBUG > BEGIN addMenuPage: " + m );
	var menuId = globalData.menus[m].menuId;

	var thePage = '<div data-role="page" id="menu' + menuId + '" data-url="' + menuId + '">' +
				  '  <div data-theme="a" data-role="header" data-position="fixed">' +
                  '    <div id="nav">' +
				  '      <a id="home-button" data-direction="reverse"  data-transition="slide" href="#menus" onclick="goHome()">Home</a> ' +
				  '      <a class="order-button" href="javascript:showOrder()">My Order</a> ' +
				  '      <a id="menu-button" onclick="toggleMenuItems()" >' + globalData.menus[m].name + '</a> ' +
				  '      <a id="submenu-button" onclick="toggleSubMenuItems()">Sections</a>' +
				  '    </div>' +
				  '    <div id="menu-description">' + globalData.menus[m].description + '</div></div>' +
		          '  <div class="menu-items"></div>' + 
				  '  <div class="submenu-items"></div>' + 
				  '    <div data-role="content">' +
				  '    <div id="section-headers"><\/div>' + 
				  '    <div id="container" style="position: relative; overflow: hidden; height: 4860px;" class="isotope"><\/div>' +
				  '    <div id="section-footers"></div>' + 
				  '  </div>' +
                  '  <div class="shade" onclick="hideItemDetail()"></div>' +
                  '  <div class="itemDetail">' +        
                  '    <div class="itemDetailClose" onclick="hideItemDetail()">Close</div>' +
                  '    <div class="itemDetailImg"></div>' + 
                  '    <div class="itemDetailName"></div>' + 
                  '    <div class="itemDetailDesc"></div>' + 
                  '    <div class="itemDetailPrice"></div>' + 
                  '    <div class="addToOrder"></div>' + 
                  '    <div class="itemRating">Popular:</div>' + 
                  '  </div>' + 
                  '  <div class="order">' +        
                  '    <div class="orderSummary">' +        
                  '      <div class="orderTitle">Your Order</div>' + 
                  '      <div class="orderNumOfItems"><span class="red label">Empty</span><span class="value"></span></div>' + 
                  '      <div class="orderPrice"><span class="label"></span><span class="value"></span></div>' + 
                  '      <a href="javascript:placeOrder()" class="placeOrder">Place Order</a>' + 
                  '      </div>' + 
                  '    <div class="orderItems"></div>' +
                  '  </div>' + 
				  '</div>';
	

	if ($("div#" + menuId).length == 0) {
		$(thePage).appendTo($.mobile.pageContainer);
	}else {
		console.log("DEBUG > Skipping menu page " + globalData.menus[m].name + "because it has already been added");
	}

	//console.log("DEBUG > END addMenuPage");	
}
/* 
 * END addMenuPage
 **********/
 
 
 
  
 /* **********
 * BEGIN addSectionToMenuPage
 * Creates sections and filter links for each menu page
   Parameters: 
    m - menu index 
    s - section index
 */ 
function addSectionToMenuPage(m, s) {
	console.info("DEBUG > addSectionToMenuPage() >  Menu: " + m + " Section: " + s);	
	
	if (typeof(globalData.menus[m].sections[s].sectionId) != "undefined") {
		var name = globalData.menus[m].sections[s].name;
		var menuId = globalData.menus[m].menuId;
		var sectionId = globalData.menus[m].sections[s].sectionId;
		
		// add filter link
		var theLink = '<a href="#" data-filter=".section' + sectionId + '" >' + name + '</a>';
		$(theLink).appendTo("#menu" +  menuId + " .submenu-items");
		
		// iterate through each item
		var itemIndex = 0;
		$.each(globalData.menus[m].sections[s].items, function() {
			addItemToMenuPage(m,s,itemIndex);
			itemIndex++;
		});
		
	}	
	//console.log("DEBUG > END addSectionToMenuPage");	
}
/* 
 * END addSectionToMenuPage
 **********/
 
 

 
 


/* **********
 * BEGIN addItemToMenuPage
 * Populates menu page containers
    Parameters: 
    m - menu index 
    s - section index
    i - item index
 */
function addItemToMenuPage(m,s,i) {
	console.info ("DEBUG > BEGIN addItemToMenuPage" );	

	var menuId = "menu" + globalData.menus[m].menuId;
	var sectionId = "section" + globalData.menus[m].sections[s].sectionId;
	var item = globalData.menus[m].sections[s].items[i];
	
	
	if (typeof(item.itemId) != "undefined") {
		var itemId = "item" + item.itemId;
		var name = item.name;
		var description = item.description;
		var shortDescription = item.shortDescription;
		var price = item.price;
		var itemType = item.itemType;
		var vegetarian = item.vegetarian ? '<div class="vegetarian"></div>' : '';
		var alcoholic = item.alcoholic;
		var imageUrl = "";
		var smallImageUrl = "";
		
		
		// cache images
		if (typeof(item.imageUrl) != "undefined") {
			imageUrl = item.imageUrl;
			var fullImageUrl = comCookedSpeciallyImagePrefix + imageUrl;
			if(ImgCache.isCached(fullImageUrl)) {
                console.log("Previously cached image: " + fullImageUrl);
			} else {
			    ImgCache.cacheFile(fullImageUrl);
			}
		}		
		if (typeof(item.smallImageUrl) != "undefined") {
			smallImageUrl = item.smallImageUrl;
			var fullsmallImageUrl = comCookedSpeciallyImagePrefix + smallImageUrl;
			if(ImgCache.isCached(fullsmallImageUrl)) {
                console.log("Previously cached image: " + fullsmallImageUrl);
			} else {
			    ImgCache.cacheFile(fullsmallImageUrl);
			}
		}

		var theItem = '<div class="item isotope-item ' + sectionId + '" id="' + itemId + '"' + 
                      '     onclick="showItemDetail(\'' + m + '\',\'' + s + '\',\'' + i + '\')">' +	
		              '  <div class="itemContent">' +
					  '    <div class="title">' + vegetarian + name + ' </div>' +
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
							//"background-size": "contain"
							"background-size": "cover"
							});
			ImgCache.useCachedBackground($(itemSelector));
		}

	}
	//console.log("DEBUG > END addItemToMenuPage");	
}
/* 
 * END addItemToMenuPage
 **********/


 
/***********
 * BEGIN showItemDetail
 * Show popup div with item details
    Parameters: 
    m - menu index 
    s - section index
    i - item index
 */
function showItemDetail(m,s,i) {

	var p = "menu" +  globalData.menus[m].menuId;
	var name = globalData.menus[m].sections[s].items[i].name;
	var desc = globalData.menus[m].sections[s].items[i].description;
	var price = globalData.menus[m].sections[s].items[i].price;
	var image = globalData.menus[m].sections[s].items[i].imageUrl;
	
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
    
    $('#' + p + ' .addToOrder').html('<a href="javascript:addToOrder(\'' + m + '\',\'' + s + '\',\'' + i + '\')">Add To Order</a>');

}

/***********
 * BEGIN hideItemDetail
 * Hide details popup div
 */
function hideItemDetail() {
    $('.itemDetail').fadeOut();
    $('.order').fadeOut();    
    $('.shade').fadeOut();
}

/***********
 * BEGIN goHome
 * Navigation
 */
function goHome(){
	$('.itemDetail').hide();
	$('.order').hide();
	$('.shade').hide();
}

























 
 /* 
 *******************************************************
                     DATA FUNCTIONS
 ******************************************************* 
 */


 
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
			initHomePage();			
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
			initHomePage();
		});
	}
	else {
		console.log("ajax error: offline"); 
		compareAjaxData();
		initHomePage();
	}
				
	
console.log("DEBUG > END updateAjaxDataRemote");	
}
/* 
 * END updateAjaxDataRemote
 **********/

/* **********
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









 
 
 
 
 
 
 
 /* 
 *******************************************************
                     MAIN PROGRAM
 ******************************************************* 
 */

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
 	