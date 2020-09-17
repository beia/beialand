<h1>CitiSIM Docker Image</h1>

In order to build and run a docker container with the CitiSIM project you need:
  - Docker
  - Docker Compose
  

Steps:
  1. Clone repository on docker host.
  2. Move to CitiSIM directory.
  3. Run <strong>docker-compose up -d</strong>.
      - it will automatically download all necessary resources, 
        will create the images for MySQL database and Flask Project 
        and will run the containers.
      - "-d" option will run the containers in detached mode.
  4. From a web browser access <strong>http://localhost:32100</strong> and it will redirect you to the login page.
  5. If you want to stop the containers, from the same directory just run <strong>docker-compose stop</strong>.
     <strong>docker-compose down</strong> will remove the containers along with their associated volumes and all new data will be lost.
  6. To start the containers again, run <strong>docker-compose start</strong> or <strong>docker-compose up -d</strong>, they will have the same efect.
  7. In case of errors, containers' logs can be accessed with <strong>docker logs [container name]</strong>.
