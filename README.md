# Tournament leaderboard application with Dropwizard (Gradle and Kotlin)
------------
A Leaderboard project using Kotlin with the framework Dropwizard. 

There are two parts in this project :
 * the frontend inside the directory /frontend
 * the backend directly into the folder /src

## _Prerequisites_
------------
The simple way to start the service with everything already configured is to use Docker-compose
So, you will need to have : 
 * Docker
 * Docker-compose

Optionally you will need NodeJS (v.16.0.0), and the angular CLI node library if you want to serve frontend separately, 
for development for example : 

```sh
npm install -g @angular/cli
cd frontend
ng serve --open
```

## _Start_
------------
Just use bash to start the project : 
```sh
./start.sh
```

With this method we use docker-compose to do different things : 
- build the frontend and package it into Dropwizard assets
- build the backend (create fat jar with : gradle shadowJar)
- create and start a redis server to be use by the backend
- start the back-end app just built

after that you can normally see the frontend here : 

http://localhost:8080/index.html

to stop the server you can directly use the other bash file : 
```sh
./stop.sh
```

## _Build It Yourself_
------------
To build, test and run the backend:

```sh
gradlew test
gradlew clean run
```

The backend app can also be run with:
```sh
gradlew clean shadowJar
java -jar build/libs/tournament-betclic-dropwizard.jar server config.yaml
```

To build and test the frontend:
```sh
cd frontend
ng test
ng build
```

The frontend app can also be served standalone with:
```sh
cd frontend
ng serve --open
```
when served the server which is watching your frontend files rebuild your files after files modifications.



