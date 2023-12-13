# Registrar-prototype
## Pre-requisites
- Docker
- Docker Compose
- (optional) JDK 17+
- (optional) Maven 3.6.3+


## Build
### 1. (optional) build .jar artifact
- you can skip this step as .jar artifact is already provided in `docker/registrar`
```
mvn clean install
cp target/registrar-2.0-SNAPSHOT.jar docker/registrar
```

### 2. build Docker image
```
cd docker/registrar
docker build -t registrar:2.0-SNAPSHOT .
```

## Run
Docker-compose will run a docker network consisting of Registrar and PostgreSQL database.
```
cd .. # docker folder
vim .env # update .env file - these env variables are necessary!
docker-compose up

docker-compose rm # to remove stopped service containers
```

### Spring profiles
`SPRING_PROFILES_ACTIVE` property in `.env` file contains list of comma separated profiles. Following profiles are supported:
- `local` - doesn't check authorization rights, any user can run any endpoint
- `initial-data` - insert initial data to DB for testing purposes - 2 forms with basic configuration

### REST API requests
- you can use the Swagger UI for making Registrar API requests:
  http://localhost:8080/swagger-ui/index.html
- you can make both authenticated and unauthenticated HTTP requests
  - if you set the profile "local" in the .env file, you can access all API endpoints
  - otherwise, user authorization is enforced and you can access only the endpoints for which you have sufficient roles in the target IAM system
- for authenticated requests:
  - you need to get access token from IdP, use this URL for default IdP: https://id.muni.cz/token-portal/
  - set the token in the Authorize button in the Swagger UI