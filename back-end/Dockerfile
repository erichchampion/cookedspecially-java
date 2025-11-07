# https://docs.docker.com/engine/reference/builder/

FROM tomcat
LABEL maintainer="erich@cookedspecially.com"

# Delete existing ROOT folder
RUN rm -rf /usr/local/tomcat/webapps/ROOT


ADD target/CookedSpecially-1.0.3.33-SNAPSHOT.war /usr/local/tomcat/webapps/
# RUN curl -O -L https://github.com/AKSarav/SampleWebApp/raw/master/dist/SampleWebApp.war

EXPOSE 8080
CMD ["catalina.sh", "run"]