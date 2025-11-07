# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

On the client side, we have four HTML files of interest. 

1. We first developed a U/I to be used by waiters or customers in a restaurant. It's the file linked from the the demo section of our website. 
Git: static/table/index.html
Axis: https://www.cookedspecially.com/static/table/index.html?{%22restaurants%22:[1]}
Monk: https://www.cookedspecially.com/static/table/index.html?{%22restaurants%22:[8]}

2. Then we added a U/I designed for the web, at those folks ordering take-out or for delivery:
Git: static/mobile/index.html
Axis: https://www.cookedspecially.com/static/mobile/index.html?{%22restaurants%22:[1]}
Monk: https://www.cookedspecially.com/static/mobile/index.html?{%22restaurants%22:[8]}
Multiple restaurants: https://www.cookedspecially.com/static/mobile/index.html?{%22restaurants%22:[1,8]}

3. We then customized the mobile U/I for Salad Days. The code is quite similar to the previous "mobile" version, but the workflow doesn't account for take-out, only delivery, along with a few other workflow changes as well. 
Git: static/clients/com/saladdays/index.html
SaladDays: https://www.cookedspecially.com/static/clients/com/saladdays/index.html

Normally, we make and post changes in this "beta" file first, and allow the folks at Salad Days to do user-acceptance testing before we update the main index.html file that's linked from their website and Facebook page. Right now, there's no difference between index.html and index-beta.html.
Git: static/clients/com/saladdays/index-beta.html
SaladDays: https://www.cookedspecially.com/static/clients/com/saladdays/index-beta.html

4. We also have a Salad Days U/I that they use in-house for taking orders over the phone. They use it from a desktop PC, and it's designed so the person taking orders can see more items onscreen at once, without scrolling. Otherwise, the workflow is the same as the main index.html page. 
Git: static/clients/com/saladdays/index-pos.html
SaladDays: https://www.cookedspecially.com/static/clients/com/saladdays/index-pos.html

As you've figured out, the only JQuery Mobile view in these pages to begin with is an empty landing page. Everything else is built from the JSON data pulled down by the restaurant and menu APIs.
https://www.cookedspecially.com/CookedSpecially/restaurant/getrestaurantinfo?restaurantId=21
https://www.cookedspecially.com/CookedSpecially/menu/getallmenusjson/21

If you look at the $(document).ready() method, you'll see that the whole process starts with a call to the updateRestaurantInfo() method.

$(document).ready(function(){
	// When the document is ready, Cordova isn't
	$.mobile.loading( "show", {
		text: "Loading…",
		textVisible: true,
		theme: $.mobile.loader.prototype.options.theme,
		textonly: false,
		html: ""
	});
	
	$(window).on('hashchange', function() {myHistory.push(document.location.hash)});

	$.each(restaurants, function() {
		updateRestaurantInfo(this);
	});
	
});

In the updateRestaurantInfo() method, we call the getrestaurantinfo API and set things like the name of the restaurant in the U/I. Then, we call the updateAjaxDataRemote(), which calls the getallmenusjson API to pull down of the data about menus, sections and individual items. In the original table/index.html file, we also have a parallel updateAjaxDataLocal() method. We would call the "remote" method when online, and cache a copy of the JSON data, along with all images, for use offline. When offline, we would call the updateAjaxDataLocal() method to read that cached data.

Then, the initHomePage() method builds a landing page that's displayed when there are multiple restaurants or multiple menus for a single restaurant. Since Salad Days is only a single restaurant with a single menu with just a few items, this landing page doesn't even come into play. 

The addMenuToHomePage() and updateMenuPage() methods then build views like the menu88 view you see for Salad Days, just by iterating through the JSON data. Placing a breakpoint in updateMenuPage() would be a good way to see how the U/I that you eventually see is built. 

When you're working with Salad Days, you just have to be careful on the live site, since you're working with an active restaurant. 

To play around in these files and see what happens when working on your local machine, look for this line in the HTML file:

var comCookedSpeciallyApiPrefix = "/CookedSpecially";

Change it to point to your local installation of tomcat. 

var comCookedSpeciallyApiPrefix = "http://localhost:8080/CookedSpecially";

With the SQL dump I posted to Slack, hopefully you'll be able to poke around to see how the web app and these client-side HTML files interact with one another. 

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact