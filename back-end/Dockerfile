FROM openjdk:13-alpine
COPY ./target/noticeboard-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "noticeboard-0.0.1-SNAPSHOT.jar"]