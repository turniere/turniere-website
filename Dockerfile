FROM alpine:3.8

RUN apk add openjdk8-jre

COPY target/turniere-0.0.1-SNAPSHOT.jar /home/turniere/turniere.jar

WORKDIR /home/turniere

VOLUME /home/turniere/config/application.yml

EXPOSE 80

CMD ["java", "-jar", "turniere.jar"]
