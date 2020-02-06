FROM openjdk:8-jre-alpine

RUN apk add --update ca-certificates && rm -rf /var/cache/apk/* && \
  find /usr/share/ca-certificates/mozilla/ -name "*.crt" -exec keytool -import -trustcacerts \
  -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts -storepass changeit -noprompt \
  -file {} -alias {} \; && \
  keytool -list -keystore /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts --storepass changeit

ENV MAVEN_VERSION 3.5.4
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH $MAVEN_HOME/bin:$PATH

RUN wget http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  tar -zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  rm apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  mv apache-maven-$MAVEN_VERSION /usr/lib/mvn

WORKDIR app
COPY mavenzip.war mavenzip.war
COPY jetty-runner-9.4.6.v20170531.jar jetty-runner-9.4.6.v20170531.jar
EXPOSE 8080
CMD java -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts -Djava.security.egd=file:/dev/./urandom -jar jetty-runner-9.4.6.v20170531.jar --port 8080 --path / mavenzip.war
