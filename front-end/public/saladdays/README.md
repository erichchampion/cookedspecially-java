# saladdays-web
##Directory Structure :

1. Home/ : Directory with unprocessed HTML/CSS/JS/PHP/Res etc. This is what you edit in your IDE
	1. Home/resources : Contains the Policy PDF
	2. Home/swiftmailer : Swiftmailer PHP source
	3. Home/css
	4. Home/js
	5. Home/images : This folder contains a folder for each page in which that page's images are placed.
2. deploy/ : The directory where gulp places the HTML/CSS/JS/PHP/Res etc. to be scp'ed to the server. Follows the same directory structure as the 
3. Legacy/ : Snapshot of the old saladdays.co menu page. Both index.html and POS
4. Old files/: Contains certain pages which are no longer used
5. Template : Contains the Template upon which some of the new pages are based

##Languages being used :
1. HTML
2. CSS
3. JS
4. PHP
5. Python (for menu updater which is deprecated now)

##IDE & frameworks :
The build process or pre-processign is IDE independent, you can use any environment you like.
We used Sublime Text to begin with IntelliJ Webstorm later.

##Source Control and repositories details :
This is a private git repo hosted on GitHub, share your usernames for access or get hold of the compressed directory and you can host the git repo anywhere. The project has a dirty commit histroy.  
* URL : https://github.com/heresamit/saladdays-web  
* Git path : git@github.com:heresamit/saladdays-web.git  

##Other libraries, frameworks :
1. Swiftmailer (PHP) used to send emails.
2. jQuery 1.11
3. Bootstrap 3.0.3
4. Animate.css

##Build process :
1. We use gulp for preprocessing / minification etc.
2. You'll need to install, npm, node, gulp and all the gulp extensions in the gulpfile.
3. Final gulp extension scp's the HTML/CSS/JS/Resources to the server (you may want to change the paths in the SCP command)

##Deployment details :
  ***************************
  Hosted on a DigitalOcean droplet : `128.199.216.41 `
  After gulp succeeds, you ssh to the droplet
  `ssh saladdays@salad-days.co.in`
  Then execute
  `sudo sh update.sh`, this essentially does a hot switch, you don't need to restart the server.
  *********************************
More Details
This is bit tricky, because it requires 3 step process 1. Gulp for minification and pre processing. 2. move it to a folder on server,
3. move from this folder to actual server folder, which will impact "saladdays.co"

1. Using Gulp on windows:
	a. for gulp: need to install node.js for windows
	b. either from cmd or other shell
		npm install gulp
		npm install gulp-plugins
	c. Open gulpfile.js in IntelliJ. there are many tasks mentioned for preprocessing of files.
		Right click on task "build" : it will minify/preprocess all the required files in local folder "deploy"
2. Now run task "default" or "upload" : it will upload the "deploy" folder to "website" folder on server. In my case (windows), it dint
work and rsync was not recognised command somehow. so I choose as alternate path.
Alternate Path:
	a. Choose Mobaxterm (Rahul knows abot it).
	b. ssh saladdays@saladdays.co
	c. Fill up the password : it will give explorer to server.
	d. First delete the content of "website" folder on server. take the back up if required
	e. Simply Drag and drop the files from your local "deploy" folder to "website" folder on server.
	FYI: still live saladdays.co has not been affected, since last step is still pending.

for backup into website-backup folder run this command.
cp -r usr/share/nginx/html/* home/saladdays/website-backup/website_Date

3. Now Run command on mobaxterm: sudo sh update.sh
	it may ask for password.
	Done. update.sh contains two command (will run on server itself)
			1.rm -rf /usr/share/nginx/html/*
			del the content of nginx server
			2. mv website /usr/share/nginx/html/
			Move the content of website folder to nginx folder (on server)

we can ignore the step 1 for minification and directly place the code files on website folder, if required

on server: website-backup is the folder having initial versions
******************************
##HTTP Server :
**nginx**  
* Restart Command : `sudo systemctl restart nginx`
* Conf file location : `sudo vi /etc/nginx/nginx.conf` [Keep a backup]


