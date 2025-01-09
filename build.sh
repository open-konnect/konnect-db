echo "Compiling and building java code"
./gradlew clean build

echo "Building docker image"
eval $(minikube docker-env)
docker build -t konnect-db:latest .


#echo "applying persistent volume in kubernetes"
#kubectl apply -f kubernetes/pv.yaml

#echo "deploying konnect-db application"
#kubectl apply -f kubernetes/deployment.yaml

#echo "applying ingress"
#kubectl apply -f kubernetes/ingress.yaml

echo "applying service"
kubectl apply -f kubernetes/service.yaml

# kubectl rollout restart deployment konnect-db
# kubectl rollout restart statefulSet konnect-db