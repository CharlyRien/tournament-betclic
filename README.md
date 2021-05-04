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
Then the API is accessible from localhost:9090/api/**

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


## _API_DOCS_
------------
The current endpoints are configured :
| Method | Path | Usage | Query Example | Response Example
| ------ | ------ | ------ | ------ |  ------ |
| DELETE | /api/players | delete every player from the tournament | - | - |
| GET | /api/players | return all players sorted by points (the one with the most points is the number one) | - | ```json [{ "id": "b75173cb-4bb1-4f20-9681-4bfb267d59fd", "username": "username1", "ranking": 1, "points": 50 },{"id": "6575e34d-2632-45f9-b6df-d5b1533b8293","username": "username2", "ranking": 2,"points": 30}]``` |
| POST | /api/players | create a player | ```json { "username" : "A Username :)" }``` | ```json {"id":"6575e34d-2632-45f9-b6df-d5b1533b8293", "username": "A Username :)", "ranking": 1, "points": 0}``` |
| GET | /api/players/{id} | retrieve information about a specific player | None | ```json {"id":"6575e34d-2632-45f9-b6df-d5b1533b8293", "username": "A Username :)", "ranking": 1, "points": 50}``` |
| PATCH | /api/players/{id}/points | apply delta to the current score for a specific player | ```json { "delta" : "-10" }``` | ```json {"id":"6575e34d-2632-45f9-b6df-d5b1533b8293", "username": "A Username :)", "ranking": 1, "points": 40}``` |




