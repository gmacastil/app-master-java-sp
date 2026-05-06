# Pipeline de CI

# empaquetar java
gradle build

# generar la imagen
docker build . -t ms-cliente:5

# tagear con el nombre del registry
// ejemplo con dockerhub
docker tag ms-cliente:5 mauron/ms-cliente:5

// ejemplo con aws
docker tag ms-cliente:2 451241043517.dkr.ecr.us-east-2.amazonaws.com/ms-cliente:2

# autenticarse en el registry
docker login

# subir la imagen al registry
// ejemplo con dockerhub
docker push mauron/ms-cliente:5

// ejemplo con aws
docker push 451241043517.dkr.ecr.us-east-2.amazonaws.com/ms-cliente:2

---------------------------------------

# Pipeline de CD


# Ejecutar el contenedor

docker run --name mscliente -d \
    -p 7080:8080 \
    -e JAVA_OPTS="-Xmx256m" \
    -e MONGO_HOST=localhost \
    -e MONGO_PORT=27017 \
    -e MONGO_DATABASE=cliente_db \
    -e MONGO_USERNAME=admin \
    -e MONGO_PASSWORD=password123 \
    -e FACTURAS_SERVICE_URL=http://localhost:8080 \
    -e LOGSTASH_HOST=localhost:9201 \
    ms-cliente:2

# Ejecutar el docker-compose

docker compose up -d

# Ejecutar en k8s
## conectarse al k8s
# crear el ns
kubectl create ns app-micros

# desplegar
kubeclt apply -f k8s/*.yaml
