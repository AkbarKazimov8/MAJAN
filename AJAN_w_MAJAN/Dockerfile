FROM openjdk:8-jdk-alpine as build

ARG MAVEN_VERSION=3.3.9
ARG USER_HOME_DIR="/root"
RUN apk add --no-cache curl tar

RUN apk add --no-cache curl tar && \
mkdir -p /usr/share/maven && \
curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar -xzC /usr/share/maven --strip-components=1 && \
ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

#WORKDIR /app
#COPY pom.xml .
#RUN mvn dependency:go-offline

COPY . /app
WORKDIR /app
RUN mvn install && \
find . \( ! -name "*.ttl" -a ! -name "*.jar" -a ! -name "plugin.properties" \) -not -path "./.extract*" -not -path "*/target*" -delete && \
rm -rf /usr/share/maven

FROM openjdk:8-jre-alpine
COPY --from=build /app /app
WORKDIR /app
