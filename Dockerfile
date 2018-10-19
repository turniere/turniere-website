FROM turniere/docker_base-openjdk8:8.171.11-r0-dockerv00

COPY target/turniere.jar /home/turniere/turniere.jar

WORKDIR /home/turniere

VOLUME /home/turniere/config/application.yml

EXPOSE 8080

CMD ["java", "-jar", "turniere.jar"]
