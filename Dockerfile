# Build Angular
FROM node:14.3.0-alpine3.11 AS NPM

WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH

RUN mkdir /resources
COPY src/main/angular /app

RUN npm install
RUN npm install -g @angular/cli@9.1.6
RUN ng build --prod

# Build Gradle
FROM adoptopenjdk/openjdk11:alpine AS JDK

WORKDIR /project

COPY . /project
COPY --from=NPM /resources/public /project/src/main/resources/public

RUN ./gradlew --no-daemon clean build

# App Image
FROM adoptopenjdk/openjdk11:alpine-jre
EXPOSE 8080

WORKDIR /app

COPY --from=JDK /project/build/libs/stream-0.0.1-SNAPSHOT.jar /app

CMD ["java", "-Dspring.profiles.active=prod,secrets", "-Dspring.config.location=classpath:/,classpath:/config/,file:/secrets/", "-jar", "stream-0.0.1-SNAPSHOT.jar"]
