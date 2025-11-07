Steps:
1) Make sure java is installed and paths are set
2) install maven3
3) install apache tomcat 6
4) install git
5) create the ssh key and upload it on bitbucket
6) make sure code is checked out and has ssh git pull setup
7) install mysql
8) make sure 'cookedspecially' db is created with execution of sql script for schema creation.

git pull
mvn clean
mvn package -DskipTests

See scripts/deployment.sh for an example of how to build and deploy. Run one of the release scripts in the root to deploy for real. 
