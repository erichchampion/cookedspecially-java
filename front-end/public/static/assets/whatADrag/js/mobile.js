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
var globalDataString = "";
var globalData = {};
var globalDataRemoteString = "";
var globalDataRemote = {};
var globalDataLocalString = "";
var globalDataLocal = {};



// Added for web support

var restaurantId = "1"; //Axis
var appVersion = 1.4;
var appLatestVersion = 1.4;

var isWeb = ( location.href.indexOf('http://') > -1 ) ? true : false;
var isCooked = ( location.href.indexOf('specially.com') > -1 ) ? true : false;
if ( isWeb && ! isCooked ){
	comCookedSpeciallyRestaurantApi = "ajax/menus.json";    
}else{
	comCookedSpeciallyRestaurantApi = comCookedSpeciallyApiPrefix + "/CookedSpecially/menu/getallmenusjson/" + restaurantId;    
}


/**********
 * FUNCTIONS
 */

// HTML5 Drag and drop
// http://www.w3schools.com/html/html5_draganddrop.asp
function allowDrop(ev)
{
ev.preventDefault();
}

function drag(ev)
{
ev.dataTransfer.setData("Text",ev.target.id);
}

function drop(ev)
{
ev.preventDefault();
var data=ev.dataTransfer.getData("Text");
ev.target.appendChild(document.getElementById(data));
}

/***********
 * BEGIN addItemToMenuPage
 */
function addItemToMenuPage(menuId, sectionId, data) {
console.log("DEBUG > BEGIN addItemToMenuPage");	
	if (typeof(data.itemId) != "undefined") {
		var itemId = "item" + data.itemId;
		var name = data.name;
		var description = data.description;
		var shortDescription = data.shortDescription;
		var imageUrl = "";
		if (typeof(data.imageUrl) != "undefined") {
			imageUrl = data.imageUrl;
			ImgCache.cacheFile(comCookedSpeciallyImagePrefix + imageUrl);
		}
		var price = data.price;
		var itemType = data.itemType;
		var vegetarian = data.vegetarian;
		var alcoholic = data.alcoholic;
		
		var theItem = '<div draggable="true" ondragstart="drag(event)" class="item isotope-item ' + sectionId + '" id="' + itemId + '"' + 
//                      '     onclick="showItemDetail(\'' + menuId + '\',\'' + name + '\',\'' + imageUrl + '\',\'' + shortDescription + '\',\'' + price + '\')">' +	
                      '>' +	
		              '  <div class="itemContent">' +
					  '    <div class="title">' + name + ' </div>' +
					  '    <div class="description"> <!--' + shortDescription + ' --> </div>' +
					  '    <div class="price">' + price + ' </div>' +
					  '  </div>' +
					  '</div> ';
		$(theItem).appendTo("#" + menuId + " #container");
		
		if (imageUrl.length != 0) {
			var itemSelector = "div#" + itemId;
			var fullImageUrl = "url(" + comCookedSpeciallyImagePrefix + imageUrl + ")";
			$(itemSelector).css({
							"background-color": "white", 
							"background-image": fullImageUrl, 
							"background-repeat": "no-repeat", 
							"background-position": "center bottom", 
							"background-size": "contain"
							});
			ImgCache.cacheBackground($(itemSelector));
			ImgCache.useCachedBackground($(itemSelector));
		}
	}
}
console.log("DEBUG > END addItemToMenuPage");	
/* 
 * END addItemToMenuPage
 **********/

/***********
 * BEGIN addSectionToMenuPage
 */
function addSectionToMenuPage(menuId, data) {
console.log("DEBUG > BEGIN addSectionToMenuPage");	
	if (typeof(data.sectionId) != "undefined") {
		var sectionId = "section" + data.sectionId;
		var name = data.name;
		
		// add filter
		var theLink = '<a href="#" data-filter=".' + sectionId + '" >' + name + '</a>';
		$(theLink).appendTo("#" + menuId + " .submenu-items");
		
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
 * BEGIN addMenuPage
 */
function addMenuPage(menuId, name, description) {
	console.log("DEBUG > BEGIN addMenuPage");	
	var thePage = '<div data-role="page" id="' + menuId + '" data-url="' + menuId + '">' +
				  '  <div data-theme="a" data-role="header" data-position="fixed">' +
				  '    <img onclick="location.href=\'#home\'" id="homeBtn" style="height: 50px" src="images/axis-logo.png">' +
                  '    <div id="nav">' +
				  '      <a id="menu-button" onclick="toggleMenuItems()">Menus</a>' +
				  '      &gt; <a id="submenu-button" onclick="toggleSubMenuItems()" >' + name + '</a>' +
				  '      &gt; <a id="order-button" href="#order-panel" >My Order</a>' +
				  '    </div>' +
		          '  <div class="menu-items"></div>' +
				  '  <div class="submenu-items"></div>' +
				  '    <div id="menu-description">' + description + '</div></div>' +
				  '    <div data-role="content">' +
				  '    <div id="section-headers"><\/div>' +
				  '    <div id="container" style="position: relative; overflow: hidden; height: 4860px;" class="isotope"><\/div>' +
				  '    <div id="section-footers"></div>' +
				  '  </div>' +
                  '  <div class="shade" onclick="hideItemDetail()"></div>' +
                  '  <div class="itemDetail">' +        
                  '    <div class="itemDetailClose" onclick="hideItemDetail()">X</div>' +
                  '    <div class="itemDetailImg"></div>' +
                  '    <div class="itemDetailName"></div>' +
                  '    <div class="itemDetailDesc"></div>' +
                  '    <div class="itemDetailPrice"></div>' +
                  '  </div>' +			
                  '  <div ondrop="drop(event)" ondragover="allowDrop(event)" data-role="panel" id="order-panel" data-position="left" data-display="push" data-position-fixed="true"><a href="#order-button" data-rel="close">Hide my order<\/a><\/div>' +  
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
 * BEGIN addMenuToHomePage
 */
function addMenuToHomePage(menuId, name, description, imageUrl) {
console.log("DEBUG > BEGIN addMenuToHomePage: " + name);
    var theMenu = '<a data-role="button" data-theme="c" class="topMenuBtn ui-btn ui-shadow ui-btn-corner-all" id="' + menuId + 
	                '" href="#' + menuId + '" data-corners="true" data-shadow="true" data-iconshadow="true" data-wrapperels="span" style="display:block;float:left">' +
				    '<span class="ui-btn-inner ui-btn-corner-all">' +
				      '<span class="ui-btn-text">' + name + '</span>' +
				    '</span>' +
				    '<div class="buttonFooter"></div>' +
				  '</a>';
 
	if ($("#topMenu " + menuId).length == 0) {
		 $(theMenu).appendTo("#topMenu");
		if (imageUrl.length != 0) {
			var menuSelector = "a#" + menuId;
			var fullImageUrl = "url(" + comCookedSpeciallyImagePrefix + imageUrl + ")";
			$(menuSelector).css({"background-image": fullImageUrl, "background-size": "cover"});
			ImgCache.cacheBackground($(menuSelector));
			ImgCache.useCachedBackground($(menuSelector));

			console.log("DEBUG > menuSelector: " + menuSelector );	
			console.log("DEBUG > imageUrl: " + fullImageUrl );
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
 * BEGIN addMenuToNav
 */
function addMenuToNav(data) {
console.log("DEBUG > BEGIN addMenuToNav");	
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
		addMenuPage(menuId, name, description);
		$.each(data.sections, function() {
			if (typeof(sectionId) != "undefined" && sectionId.length == 0) {
				sectionId = "section" + this.sectionId;
			}
			addSectionToMenuPage(menuId, this);
		});
		$("#" + menuId).page();
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
 * BEGIN updateAllMenus
 */
function updateAllMenus() {
console.log("DEBUG > BEGIN updateAllMenus");	
	console.log("About to iterate through globalData");
	if (globalData != null && typeof(globalData.menus != "undefined") && globalData.menus != null) {
		$.each(globalData.menus, function(i, item) {
			updateOneMenu(item);
			menuCount++;
		});
		console.log("menuCount: " + menuCount);
	}
	else {
		console.log("DEBUG > globalData invalid");
	}
	
	// The swipe events seem to be applied, but do not work on the device
	for (var i = 0; i < menuCount; i++) {
		try {
			console.log("Iterating through globalData.menus to update swipe events: " + i + ":" + globalData.menus[i].name);
			$("div#menu" + globalData.menus[i].menuId).swiperight(function() {
				$.mobile.changePage("#home"); 
			});			

			$("div#menu" + globalData.menus[i]).swipeleft(function() {
				$.mobile.changePage("#home"); 
			});	
		} catch (error) {
			console.error("Error applying swipe events: " + error);
		}
	}
	$.each(globalData.menus, function() {
		addMenuToNav(this);
	});
	
	
	if (!isWeb){ // Added for web support
		$.mobile.page.prototype.options.domCache = true;
	}

console.log("DEBUG > END updateAllMenus");	
}
/* 
 * END updateAllMenus
 **********/

/***********
 * BEGIN compareAndCacheAjaxData
 */
function compareAndCacheAjaxData() {
console.log("DEBUG > BEGIN compareAndCacheAjaxData");	

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
		localDataValid = false;
		window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, getFSForWriting, onFileError);
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
	console.log("DEBUG > END compareAndCacheAjaxData");	
}
/* 
 * END compareAndCacheAjaxData
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
			updateAllMenus();			
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
			compareAndCacheAjaxData();
			updateAllMenus();
		});
	
	}else {
		console.log("ajax error: offline"); 
		compareAndCacheAjaxData();
		updateAllMenus();
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

/**********
 * BEGIN initIsotope
 */
function initIsotope(menuId, sectionId) {
console.log("DEBUG > BEGIN initIsotope");	
	var contentContainerSelector = "#" + menuId + " #container";
	var $contentContainer = $(contentContainerSelector);
	// update columnWidth on window resize
	/*
	$(window).smartresize(function(){
		$contentContainer.isotope({
			// update columnWidth to a percentage of container width
			// masonry: { columnWidth: $contentContainer.width() / 3 }
		});
	});
	*/
	
	$("#" + menuId).live('pageshow', function(event, ui) {
		$contentContainer.css("display", "block");
		/*
		$("#" + menuId + " #section-headers div").css("display", "none");
		$("#" + menuId + " #section-headers div." + sectionId).css("display", "block");
		$("#" + menuId + " #section-footers div").css("display", "none");
		$("#" + menuId + " #section-footers div." + sectionId).css("display", "block");
		$("#" + menuId + " #filters a").first().addClass("activeFilter");
		*/
		
		$contentContainer.isotope({
			// options...
			//transformsEnabled: false, 
			resizable: true, 
			//filter: "." + sectionId
		});
	
		// filter items when filter link is clicked
		$("#" + menuId + " .submenu-items a").click(function(){
			//$("#" + menuId + " #filters a").removeClass("activeFilter");
			//$(this).addClass("activeFilter");
			var selector = $(this).attr('data-filter');
			//alert($(this).attr('data-filter'));
			$("#" + menuId + " .submenu-items").css('z-index','1').slideUp();
			$("#" + menuId + " .submenu-items").slideUp();
			$contentContainer.isotope({ 
				// options...
				//transformsEnabled: false, 
				resizable: true, 
				filter: selector
			});
			/*
			$("#" + menuId + " #section-headers div").css("display", "none");
			$("#" + menuId + " #section-headers div" + selector).css("display", "block");
			$("#" + menuId + " #section-footers div").css("display", "none");
			$("#" + menuId + " #section-footers div" + selector).css("display", "block");
			*/
			return false;
		});

	});

	$contentContainer.imagesLoaded(function( $images, $proper, $broken ) {
	
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
	
	});

console.log("DEBUG > END initIsotope");	
}
/*
 * END initIsotope
 ***********/

try {

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
        ImgCache.init();
        ImgCache.options.debug = true;
		ImgCache.options.usePersistentCache = true;
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
        //writer.truncate(0);
        console.log("Writing: " + globalDataRemoteString);
        writer.truncate(0);
        writer.write(globalDataRemoteString);
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








function showItemDetail(p, name, image, desc, price) {
    $('.menu-items').hide();
	$('.submenu-items').hide();
	
	//alert ( 'Name: ' + name + '\nImage: ' + image + '\nDescription: ' + desc+ '\nPrice: ' + price);
    $('.shade').hide();
    $('.itemDetail').hide();
    
    $('#' + p + ' .shade').show();
    $('#' + p + ' .itemDetail').show();
    

    $('#' + p + ' .itemDetailName').html(name);
    $('#' + p + ' .itemDetailImg').css('background-image', 'url(' + comCookedSpeciallyImagePrefix + image + ') ');
    $('#' + p + ' .itemDetailDesc').html(desc);
    $('#' + p + ' .itemDetailPrice').html(price);
    
}



function hideItemDetail() {
    $('.itemDetail').fadeOut();    
    $('.shade').fadeOut();
}


function toggleMenuItems(){
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
} 


function toggleSubMenuItems(){
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
} 


	/**********
	* BEGIN showAppVersion
	*/
	function showAppVersion() {
    	var appInfo = 'About Axis Tablet App\n\n' +
                  'Restaurant: Axis Cafe' + '\n' +
                  'Application Version: Beta ' + appVersion + '\n' +
                  'Latest Version: Beta ' + appLatestVersion;
    	alert(appInfo);
	}
	/*
 	* END showAppVersion
 	***********/




} catch (error) {
	console.error("Your javascript has an error: " + error);
}
