FROM openjdk:8-jre-alpine
WORKDIR app
COPY mavenzip.war mavenzip.war
COPY jetty-runner-9.4.6.v20170531.jar jetty-runner-9.4.6.v20170531.jar
EXPOSE 8080
CMD java -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts -Djava.security.egd=file:/dev/./urandom -jar jetty-runner-9.4.6.v20170531.jar --port 8080 --path / mavenzip.war
