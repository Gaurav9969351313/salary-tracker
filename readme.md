docker build -t gt-salery-tracker:v1 .

docker container run --name salary-tracker -p 8080:8080 -d gt-salery-tracker:v1

docker logs salary-tracker