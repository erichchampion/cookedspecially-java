import urllib2
import fileinput

restaurantID = "21"
menuTemplate = "menu-template"
menuPageLocation = "Home/menu.html"
insertRestaurantInfoFlag = "//INSERT RESTAURANT INFO HERE!"
url = 'http://www.cookedspecially.com/CookedSpecially/menu/getallmenusjson/' + restaurantID
data = urllib2.urlopen(url).read()
with open('menu.json', "w") as destination:
    destination.write(data)


