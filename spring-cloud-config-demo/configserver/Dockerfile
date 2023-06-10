# build
FROM gradle:latest as builder

WORKDIR /app
COPY . .
RUN ./gradlew bootJar

# run
FROM gradle:jdk11-focal

WORKDIR /
COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 9000 12342

VOLUME /app/security

#ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:12342","-jar","app.jar","-Dspring.profiles.active=prod","--server.port=9000"]
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:12342","-jar","/app/app.jar","-Dspring.profiles.active=prod","--server.port=9000"]