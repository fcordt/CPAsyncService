# How to Build:

Run gradle task `dockerBuildImage` for each project - the image will be build

> cpauth\gradlew dockerBuildImage

> cpbackend\gradlew dockerBuildImage

> cpconsumer\gradlew dockerBuildImage


Afterwards run docker compose (or docker-compose) from inside the docker folder:

> cd docker
> docker compose up -d

now you can call the frontend request API with http://localhost:8082/api/v1/charging/auth as defined in documentation\charging_api.json