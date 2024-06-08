# ServiceAnalyzer
## Introduction
**ServiceAnalyzer** is an implementation based on the Springboot Java backend that aims to provide service analysis functionality by analysing the annotations in the code and extracting interface information to help with subsequent service registration.

## Installation and Running
1. Clone the repository:
```
git clone https://github.com/HIT-ICES/ServiceAnalyzer.git
```
2. Navigate to the project directory:
```
cd instanceservice
```
3. Build the project using Maven:
```
mvn clean install
```
4. Build the Docker image:
```
docker build -t <you_image_url> .
```
5. Deploying to Kubernetes:
   Make sure you have a Kubernetes cluster set up and kubectl configured to communicate with the cluster.
   Modify the image, namespace and other information in the deployment.yaml configuration file.
```
kubectl apply -f deployment.yaml
```

## Function

### /analyzer/newService
Based on the given repo address, get a specific version of the code in the code repository to analyse and collect service interface information.