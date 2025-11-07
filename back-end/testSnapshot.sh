#!/bin/zsh
#============================================================================
# - COPYRIGHT NOTICE -
# Copyright (c) CookedSpecially, All Rights Reserved
#
# Author: Abhishek Kumar  abhishek@cookedspecially.com
#============================================================================

echo "Build"
mvn package
#echo "Shutdown tomcat"
#sudo /opt/apache-tomcat/bin/shutdown.sh
#echo "Delete old webapp directory"
#sudo rm -fr /opt/apache-tomcat/webapps/CookedSpecially*
#echo "Copy"
#cp target/CookedSpecially-*-SNAPSHOT.war /opt/apache-tomcat/webapps/CookedSpecially.war
#ls -lt /opt/apache-tomcat/webapps/
#echo "Start tomcat"
#cd /opt/apache-tomcat
#sudo bin/startup.sh
cd ~/Documents/git/webapps-cookedspecially

scp -vr -i ~/my-key-pair.pem target/CookedSpecially-*-SNAPSHOT.war ec2-user@ec2-54-234-171-187.compute-1.amazonaws.com:/opt/apache-tomcat/webapps/CookedSpecially.war
