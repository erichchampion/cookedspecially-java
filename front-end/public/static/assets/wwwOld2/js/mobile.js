/**********
 * CONSTANTS
 */
//http://www.bakedspecially.com:8080/CookedSpecially/menu/getallmenusjson/1
//var comCookedSpeciallyImagePrefix = comCookedSpeciallyApiPrefix;
var comCookedSpeciallyImagePrefix = "http://www.bakedspecially.com:8080";
var comCookedSpeciallyApiPrefix = "http://www.bakedspecially.com:8080";
var comCookedSpeciallyRestaurantApi = comCookedSpeciallyApiPrefix + "/CookedSpecially/menu/getallmenusjson/1";
var localRestaurantApi = "ajax/menus.json";

var changeCount = 0;

/**********
 * GLOBALS
 */
var globalData = {};

/**********
 * FUNCTIONS
 */

/***********
 * BEGIN addItemToMenuPage
 */
function addItemToMenuPage(menuId, sectionId, data) {
	if (typeof(data.itemId) != "undefined") {
		var itemId = "item" + data.itemId;
		var name = data.name;
		var description = data.description;
		var shortDescription = data.shortDescription;
		var imageUrl = "";
		if (typeof(data.imageUrl) != "undefined") {
			imageUrl = data.imageUrl;
		}
		var price = data.price;
		var itemType = data.itemType;
		var vegetarian = data.vegetarian;
		var alcoholic = data.alcoholic;
		var theItem = '<div class="item isotope-item ' + sectionId + '" id="' + itemId + '" ><img class="item-background-image" src="' + comCookedSpeciallyImagePrefix + imageUrl + '"/><div class="item-content"><div class="title">' + name + ' <\/div><div class="description">' + shortDescription + ' <\/div><div class="price">' + price + ' <\/div><\/div><\/div> ';
		$(theItem).appendTo("#" + menuId + " #container");
		if (imageUrl.length != 0) {
			var itemSelector = "div#" + itemId;
			var fullImageUrl = "url(" + comCookedSpeciallyImagePrefix + imageUrl + ")";
			//$(itemSelector).css({"background-image": fullImageUrl, "background-size": "cover"});
			//$(itemSelector).css({"background-color": "white", "background-image": fullImageUrl, "background-repeat": "no-repeat", "background-position": "center bottom", "background-size": "contain"});
		}
	}
}
/* 
 * END addItemToMenuPage
 **********/

/***********
 * BEGIN addSectionToMenuPage
 */
function addSectionToMenuPage(menuId, data) {
	if (typeof(data.sectionId) != "undefined") {
		var sectionId = "section" + data.sectionId;
		var name = data.name;
		var theLink = '<li><a href="#" data-filter=".' + sectionId + '" class="ui-link">' + name + '<\/a><\/li>';
		$(theLink).appendTo("#" + menuId + " #site-nav #filters");
		$.each(data.items, function() {
			addItemToMenuPage(menuId, sectionId, this);
		});
		if (typeof(data.description) != "undefined") {
			var description = data.description;
			var header = data.header;
			var footer = data.footer;
			if (description.length != 0 && header.length != 0 ) {
			 	description = description + " | ";
			}
			description = description + header;
			var theHeader = '<div style="display:none;" class="' + sectionId + '">' + description + '<\/div>';
			$(theHeader).appendTo("#" + menuId + " #section-headers");
			var theFooter = '<div style="display:none;" class="' + sectionId + '">' + footer + '<\/div>';
			$(theFooter).appendTo("#" + menuId + " #section-footers");
		}
	}	
}
/* 
 * END addSectionToMenuPage
 **********/

/***********
 * BEGIN addMenuPage
 */
function addMenuPage(menuId, name, description) {
	var thePage = '<div data-role="page" id="' + menuId + '" data-url="' + menuId + '"><div data-theme="a" data-role="header" data-position="fixed"><img id="homeBtn" style="height: 50px" src="images/axis-logo.png"><div id="nav"><a href="#home">Menus</a><\/div><\/div><div data-role="content"><div id="menu-description">' + name + " | " + description + '<\/div><div id="site-nav"><ul id="filters"><\/ul><\/div><div id="section-headers"><\/div><div id="container" style="position: relative; overflow: hidden; height: 4860px;" class="isotope"><\/div><div id="section-footers"><\/div><\/div><\/div>';
	$(thePage).appendTo($.mobile.pageContainer);
	$("div#" + menuId).css("background-image", "url(images/background.jpg)");

	$("#" + menuId).swipeleft(function() {
		$.mobile.changePage("#home");
	});
}
/* 
 * END addMenuPage
 **********/

/***********
 * BEGIN addMenuToHomePage
 */
function addMenuToHomePage(menuId, name, description, imageUrl) {
	var theMenu = '<a data-role="button" data-theme="c" class="topMenuBtn ui-btn ui-shadow ui-btn-corner-all ui-btn-up-c" id="' + menuId + '" href="#' + menuId + '" data-corners="true" data-shadow="true" data-iconshadow="true" data-wrapperels="span"><span class="ui-btn-inner ui-btn-corner-all"><span class="ui-btn-text">' + name + '<\/span><\/span><\/a>';
	$(theMenu).appendTo("#topMenu");
	if (imageUrl.length != 0) {
		var menuSelector = "a#" + menuId;
		var fullImageUrl = "url(" + comCookedSpeciallyImagePrefix + imageUrl + ")";
		$(menuSelector).css({"background-image": fullImageUrl, "background-size": "cover"});
	}
}
/* 
 * END addMenuToHomePage
 **********/

/***********
 * BEGIN addMenuToNav
 */
function addMenuToNav(data) {
/*
	if (typeof(data.menuId) != "undefined") {
		var menuId = "menu" + data.menuId;
		var name = data.name;
		var theLink = ' | <a href="#' + menuId + '" class="ui-link">' + name + '</a>';
		$.each($("#site-nav"), function() {
			$(theLink).appendTo(this);
		});
	}
	else {
		console.log("addMenuToNav: menuId undefined");
	}
*/
}
/* 
 * END addMenuToNav
 **********/

/***********
 * BEGIN updateOneMenu
 */
function updateOneMenu(data) {
	if (typeof(data.menuId) != "undefined") {
		var menuId = "menu" + data.menuId;
		var name = data.name;
		var description = data.description;
		var imageUrl = "";
		if (typeof(data.imageUrl) != "undefined") {
			imageUrl = data.imageUrl;
		}		//var modificationDate = new Date(this.modificationDate);;
		var modificationDate = new Date(data.modificationDate);
		/*
		alert("modificationDate:" + modificationDate.toJSON());
		*/
		var sectionId = "";
		addMenuToHomePage(menuId, name, description, imageUrl);
		addMenuPage(menuId, name, description);
		$.each(data.sections, function() {
			if (sectionId.length == 0) {
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
}
/* 
 * END updateOneMenu
 **********/

/***********
 * BEGIN updateAllMenus
 */
function updateAllMenus() {
	$.ajax(comCookedSpeciallyRestaurantApi, {
				isLocal: false
	})
	.done (function (data) {
		if( console && console.log ) {
			console.log("ajax success: " + comCookedSpeciallyRestaurantApi);
			console.log(data);
		}

		globalData = data;
		$.each(globalData.menus, function() {
			updateOneMenu(this);
		});
		$.each(globalData.menus, function() {
			addMenuToNav(this);
		});
	})
	.fail(function() { 
		console.log("ajax error: " + comCookedSpeciallyRestaurantApi); 
		$.ajax(localRestaurantApi, {
			isLocal: true
		})
		.done(function (data) {
			if( console && console.log ) {
				console.log("ajax success: " + localRestaurantApi);
				console.log(data);
			}
			globalData = jQuery.parseJSON(data);
			$.each(globalData.menus, function() {
				updateOneMenu(this);
			});
			$.each(globalData.menus, function() {
				addMenuToNav(this);
			});
		})
		.fail(function() { console.log("ajax error: " + localRestaurantApi); })
		.always(function() { console.log("ajax complete: " + localRestaurantApi); });
	})
	.always(function() { console.log("ajax complete: " + comCookedSpeciallyRestaurantApi); });
	
	$.mobile.page.prototype.options.domCache = true;
}
/* 
 * END updateAllMenus
 **********/

/**********
 * BEGIN initIsotope
 */
function initIsotope(menuId, sectionId) {
	var contentContainerSelector = "#" + menuId + " #container";
	var $contentContainer = $(contentContainerSelector);
	// update columnWidth on window resize
	$(window).smartresize(function(){
		$contentContainer.isotope({
			// update columnWidth to a percentage of container width
			// masonry: { columnWidth: $contentContainer.width() / 3 }
		});
	});

	$("#" + menuId).live('pageshow', function(event, ui) {
		$contentContainer.css("display", "block");
		$("#" + menuId + " #section-headers div").css("display", "none");
		$("#" + menuId + " #section-headers div." + sectionId).css("display", "block");
		$("#" + menuId + " #section-footers div").css("display", "none");
		$("#" + menuId + " #section-footers div." + sectionId).css("display", "block");
		$("#" + menuId + " #filters a").first().addClass("activeFilter");

		$contentContainer.isotope({
			// options...
			resizable: true, 
			filter: "." + sectionId,
			animationOptions: {
				duration: 750,
				easing: 'linear',
				queue: false,
			}
		});
	
		// filter items when filter link is clicked
		$("#" + menuId + " #filters a").click(function(){
			$("#" + menuId + " #filters a").removeClass("activeFilter");
			$(this).addClass("activeFilter");
			var selector = $(this).attr('data-filter');
			$contentContainer.isotope({ 
				resizable: true, 
				filter: selector,
				animationOptions: {
					duration: 750,
					easing: 'linear',
					queue: false,
				}
			});
			$("#" + menuId + " #section-headers div").css("display", "none");
			$("#" + menuId + " #section-headers div" + selector).css("display", "block");
			$("#" + menuId + " #section-footers div").css("display", "none");
			$("#" + menuId + " #section-footers div" + selector).css("display", "block");
			return false;
		});

	});

	$contentContainer.imagesLoaded(function( $images, $proper, $broken ) {
	
		$proper.each( function() {
			console.log("proper: " + this.src);
		});

		$broken.each( function() {
			console.log("broken: " + this.src);
			var $this = $(this).css({ display:"none" });
		});
	
	});
}
/*
 * END initIsotope
 ***********/

try {

	$(document).ready(function(){
		updateAllMenus();
	});

	// http://docs.phonegap.com/en/2.0.0/cordova_file_file.md.html#File
	// Wait for Cordova to load
	//
	document.addEventListener("deviceready", onDeviceReady, false);

	// Cordova is ready
	//
	function onDeviceReady() {
		checkConnection();
	}

	function checkConnection() {
		var networkState = navigator.network.connection.type;

		var states = {};
		states[Connection.UNKNOWN]  = 'Unknown connection';
		states[Connection.ETHERNET] = 'Ethernet connection';
		states[Connection.WIFI]     = 'WiFi connection';
		states[Connection.CELL_2G]  = 'Cell 2G connection';
		states[Connection.CELL_3G]  = 'Cell 3G connection';
		states[Connection.CELL_4G]  = 'Cell 4G connection';
		states[Connection.NONE]     = 'No network connection';

		console.log('Connection type: ' + states[networkState]);
	}

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


} catch (error) {
	console.error("Your javascript has an error: " + error);
}
