# Demo Shop (frontend + 3 Java microservices + Postgres)

## Run locally
    docker compose up --build
    open http://localhost:8080

Quick API checks:
    curl -X POST localhost:8081/api/users    -H 'Content-Type: application/json' -d '{"name":"Ravi","email":"ravi@x.com"}'
    curl -X POST localhost:8082/api/products -H 'Content-Type: application/json' -d '{"name":"Laptop","price":55000}'
    curl -X POST localhost:8083/api/orders   -H 'Content-Type: application/json' -d '{"userId":1,"productId":1,"quantity":2}'
    curl localhost:8081/actuator/health

Stop: `docker compose down` (add `-v` to wipe the DB).

## Next: EKS
1. Push the 4 images to ECR (user-service, product-service, order-service, frontend).
2. Replace the postgres container with RDS (or a StatefulSet); pass DB_URL / DB_USER / DB_PASSWORD via ConfigMap + Secret.
3. One Deployment + ClusterIP Service per microservice; frontend behind an Ingress (AWS Load Balancer Controller).
4. Use /actuator/health/liveness and /actuator/health/readiness for probes.
5. In-cluster service names (user-service, product-service, order-service) match the compose names, so nginx.conf and the env vars work unchanged.
