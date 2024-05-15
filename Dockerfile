FROM gradle:7.5-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/app
WORKDIR /home/app
RUN gradle clean build --no-daemon -i -x test -x javadoc

FROM openjdk:17-alpine
COPY --from=build /home/app/build/libs/submodel-server-0.0.1-SNAPSHOT.jar /usr/local/lib/ss/app.jar
RUN apk update
RUN addgroup -S sw && adduser -S sw -G sw
USER sw
WORKDIR /usr/local/lib/ss
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]