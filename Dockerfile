FROM alpine:3.8

RUN apk add openjdk8-jre

COPY target/turniere.jar /home/turniere/turniere.jar

WORKDIR /home/turniere

VOLUME /home/turniere/config/application.yml

EXPOSE 8080

CMD ["java", "-jar", "turniere.jar"]
