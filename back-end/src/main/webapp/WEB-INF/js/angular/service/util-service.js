/**
 * Author Name :- Abhishek Kumar 
 * EmailId :- anshilabhi1991@gmail.com
 * 
 */
angular.module('myApp').service('UtilService',['Excel','$injector', '$timeout', '$modal', function(Excel,$injector, $timeout, $modal) {

	/**
	 * 
	 * This method is used when url needs to be modified This support one arg to
	 * be appended args : {key:value} formated. key will be added as attribute
	 * name and value will be the value for the same key as url?key=value
	 * 
	 * @argument url, args
	 * @return url
	 * 
	 */
	this.addsToURL = function(url, args) {
		url += "?";
		angular.forEach(args, function(value, key) {
			url += key + "=" + value+"&";
		});
		return url.replace(/&$/, '');
	}

	/**
	 * 
	 * This method is used when url needs to be modified This support one arg to
	 * be appended args : string/int formated. value passed will be appended on
	 * the base url like url/value
	 * 
	 * @argument url, args
	 * @return url
	 * 
	 */
	this.addPathToURL = function(url, args) {
		for (var i=0; i<args.length; i++){
			if(args[i] != undefined || args[i] != null)
				url += "/" + args[i];
        }
		return url;
	}
	
	this.append = function(url, arg) {
		return url+arg;
	}
	
	/**
	 * This method will take you to the home page i.e. index page.
	 * 
	 */
	this.openLoginPage=function(){
		console.log('Open Home Page');
		window.location= '/CookedSpecially/';
	}
	
	this.showSpinner=function(flag){
		 if(flag){
	        	$(".js-spin-overlay").show();
	        	$(".js-spin-spinner").show();
	        } 
	        else{
	        	$(".js-spin-overlay").hide();
	        	$(".js-spin-spinner").hide();
	        }       
    }
	
	this.openModal = function(templateUrl, controllerName,windowClass, size, scope, title, filename, data){
		return $modal.open({
            templateUrl: templateUrl,
            controller: controllerName,
            windowClass: windowClass,
            size: size || 'lg',
            scope: scope,
            resolve: {
            	Excel: function() {
                    return Excel;
                },
                ReportService: function() {
                    return $injector.get('ReportService');
                },
                title: function() {
                	return title;
                	},
                filename: function(){
                	return filename;
                },
                data: function(){
                    	return data;
                    }
                }
        });
	}
	
	this.createHyperLinkAndClick = function(url){
		var a = document.createElement('a');
		a.href = url;
		$timeout(function(){a.click();},100);
	}
	
	this.isValidArray = function(data){
		var result = false;
		try{
			result=(data instanceof Array) && (data.length>1);
		}catch (e) {
		}
		return result;
	}


	this.clearEle = function(elementID){
		angular.forEach(elementID, function(ele){
			try{
			document.getElementById(ele).innerHTML = "";
			}catch (e) {
			}
		});
	}
	this.jsonify = function(data){
		var jsonArray=[];
		for(i=1; i<data.length; i++){
			var jsonObj={}
			for(j=0; j<data[i].length; j++){
				var key=data[0][j];
				if(key.includes(":"))
					key=key.split(":")[0];
				jsonObj[key]=data[i][j];
			}
			jsonArray.push(jsonObj);
		}
		return jsonArray;
	}

}]);