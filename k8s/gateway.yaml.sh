#!/bin/bash
cat <<YAML
apiVersion: apps/v1beta1
kind: Deployment
metadata:
  name: gateway
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: gateway
    spec:
      containers:
        - name: gateway
          image: gcr.io/$GCP_PROJECT/gateway:latest
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
          env:
            - name: rest_aggregator_url
              value: "http://rest-aggregator:8080"
            - name: grpc_aggregator_host
              value: "grpc-aggregator"
            - name: grpc_aggregator_port
              value: "8080"
            - name: grpc_voting_host
              value: "grpc-voting"
            - name: grpc_voting_port
              value: "8080"
            - name: temp
              value: "$(date +%s)"
            - name: ZIPKIN_SERVICE_HOST
              value: "zipkin"
            - name: ZIPKIN_SERVICE_PORT
              value: "9411"
---
apiVersion: v1
kind: Service
metadata:
  name: gateway
spec:
  type: LoadBalancer
  selector:
    app: gateway
  ports:
   - port: 8080
     targetPort: 8080
     protocol: TCP
---
YAML
