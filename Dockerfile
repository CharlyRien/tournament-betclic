FROM node:16-alpine as build-frontend
ENV APP_HOME=/usr/app/frontend
WORKDIR $APP_HOME
COPY frontend/package.json .
COPY frontend/package-lock.json .
COPY frontend/ .
RUN npm install
RUN npm install -g @angular/cli
RUN ng build --configuration=production

FROM gradle:6.8-jdk11 AS build-backend
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle .
COPY src/ ./src/
COPY settings.gradle .
COPY --from=build-frontend $APP_HOME/frontend/dist ./src/main/resources/web
RUN gradle clean shadowJar --no-daemon

FROM adoptopenjdk/openjdk11:alpine-jre
ENV ARTIFACT_NAME=tournament-betclic-dropwizard.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=build-backend $APP_HOME/build/libs/$ARTIFACT_NAME .

EXPOSE 8080
ENTRYPOINT exec java -jar ${ARTIFACT_NAME} server config-docker.yaml