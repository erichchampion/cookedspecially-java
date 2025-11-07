// Bind to all events when the document is ready
$(document).ready(function(){

	// Get the DOM references we'll need to play with.
	var appEvents = $( "ul#applicationEvents" );
	var appStatus = $( "span#applicationStatus" );
	var cacheProgress = $( "span#cacheProgress" );
 
	// Get a short-hand for our application cache object.
	var appCache = window.applicationCache;

	// Check for an updated manifest file every 60 minutes. If it's updated, download a new cache as defined by the new manifest file.
	setInterval(function () { appCache.update(); }, 3600000); 

	// Create a cache properties object to help us keep track of
	// the progress of the caching.
	var cacheProperties = {
	filesDownloaded: 0,
	totalFiles: 0
	};
 
 
	// I log an event to the event list.
	function logEvent( event ){
		var logOutput = (event + " ... " + (new Date()).toTimeString());
		console.log(logOutput);
		appEvents.prepend("<li>" + logOutput + "</li>");
	}
 
 
	// I display the download progress.
	function displayProgress(){
		// Increment the running total.
		cacheProperties.filesDownloaded++;
 
		// Check to see if we have a total number of files.
		if (cacheProperties.totalFiles){
 
			// We have the total number of files, so output the
			// running total as a function of the known total.
			console.log(
				cacheProperties.filesDownloaded +
				" of " +
				cacheProperties.totalFiles +
				" files downloaded."
			);
			cacheProgress.text(
				cacheProperties.filesDownloaded +
				" of " +
				cacheProperties.totalFiles +
				" files downloaded."
			);
 
		} else {
 
			// We don't yet know the total number of files, so
			// just output the running total.
			console.log(
				cacheProperties.filesDownloaded +
				" files downloaded."
			);
			cacheProgress.text(
				cacheProperties.filesDownloaded +
				" files downloaded."
			);
 
		}
	}

	// Bind to online/offline events.
	$( window ).bind("online offline", function( event ){
		// Update the online status.
		appStatus.text( navigator.onLine ? "Online" : "Offline" );
	});
 
	// Set the initial status of the application.
	console.log(navigator.onLine ? "Online" : "Offline");
	appStatus.text( navigator.onLine ? "Online" : "Offline" );

 
	// List for checking events. This gets fired when the browser
	// is checking for an udpated manifest file or is attempting
	// to download it for the first time.
	$( appCache ).bind("checking", function( event ){
		logEvent( "Checking for manifest" );
	});
 
	// This gets fired if there is no update to the manifest file
	// that has just been checked.
	$( appCache ).bind("noupdate", function( event ){
		logEvent( "No cache updates" );
	});
 
	// This gets fired when the browser is downloading the files
	// defined in the cache manifest.
	$( appCache ).bind("downloading", function( event ){
		logEvent( "Downloading cache" );
	});
 
	// This gets fired for every file that is downloaded by the
	// cache update.
	$( appCache ).bind("progress", function( event ){
		logEvent( "File downloaded" );
 
		// Show the download progress.
		displayProgress();
	});
 
	// This gets fired when all cached files have been
	// downloaded and are available to the application cache.
	$( appCache ).bind("cached", function( event ){
		logEvent( "All files downloaded" );
	});
 
	// This gets fired when new cache files have been downloaded
	// and are ready to replace the *existing* cache. The old
	// cache will need to be swapped out.
	$( appCache ).bind("updateready", function( event ){
		logEvent( "New cache available" );
 
		// Swap out the old cache.
		appCache.swapCache();
	});
 
	// This gets fired when the cache manifest cannot be found.
	$( appCache ).bind("obsolete", function( event ){
		logEvent( "Manifest cannot be found" );
	});
 
	// This gets fired when an error occurs
	$( appCache ).bind("error", function( event ){
		logEvent( "An error occurred" );
	});

	var totalFiles;
	appCache.addEventListener('progress', function(e){
		if( typeof totalFiles === 'undefined' ){
			totalFiles = e.total; // tested on opera, chrome, ff, safari
			console.log("Files in manifest: " + totalFiles);
		}
	}, false);

}); //END: document.ready

